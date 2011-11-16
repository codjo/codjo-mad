/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.mad.server.plugin;
import net.codjo.agent.AclMessage;
import static net.codjo.agent.AclMessage.OBJECT_LANGUAGE;
import net.codjo.agent.AclMessage.Performative;
import net.codjo.agent.MessageTemplate;
import static net.codjo.agent.MessageTemplate.matchLanguage;
import static net.codjo.agent.MessageTemplate.matchProtocol;
import net.codjo.agent.ServiceException;
import net.codjo.agent.UserId;
import net.codjo.agent.behaviour.CyclicBehaviour;
import net.codjo.mad.common.message.InstituteAmbassadorProtocol;
import net.codjo.mad.server.handler.HandlerMap;
import net.codjo.mad.server.handler.Processor;
import net.codjo.security.common.api.User;
import net.codjo.security.server.api.SecurityServiceHelper;
import net.codjo.sql.server.ConnectionPool;
import net.codjo.sql.server.JdbcServiceHelper;
import org.apache.log4j.Logger;
/**
 * Comportement du {@link SecretaryGeneralAgent} qui crée un ambassadeur suite à la réception du message
 * envoyé par un président.
 */
class InstituteAmbassadorParticipant extends CyclicBehaviour {
    private static final Logger APP = Logger.getLogger(InstituteAmbassadorParticipant.class.getName());
    private static final String AMBASSADOR_NAME = "Ambassador";
    private final BackPack backPack;
    private final AmbassadorRemovalBehaviour removalBehaviour;
    private MessageTemplate requestTemplate;


    InstituteAmbassadorParticipant(BackPack backPack, AmbassadorRemovalBehaviour behaviour) {
        this.backPack = backPack;
        this.removalBehaviour = behaviour;
        requestTemplate = MessageTemplate.and(matchLanguage(OBJECT_LANGUAGE),
                                              matchProtocol(InstituteAmbassadorProtocol.ID));
    }


    @Override
    protected void action() {
        AclMessage myACLMessage = getAgent().receive(requestTemplate);
        if (myACLMessage == null) {
            block();
            return;
        }

        UserId userId = myACLMessage.decodeUserId();
        try {
            User user = getSecurityService().getUser(userId);
            ConnectionPool pool = getJdbcService().getPool(userId);
            HandlerMap handlerMap
                  = backPack.getHandlerMapBuilder().createHandlerMap(userId, new Object[]{userId, pool});
            Processor processor = new Processor(handlerMap, backPack);
            HandlerExecutor handlerExecutor = backPack.getHandlerExecutorFactory().create(userId);
            AmbassadorAgent ambassadorAgent =
                  new AmbassadorAgent(userId,
                                      handlerExecutor,
                                      backPack.getHandlerExecutionMode(),
                                      processor,
                                      pool,
                                      backPack.getJdo(),
                                      user);

            String localAmbasadorName = AMBASSADOR_NAME + "For" + myACLMessage.getSender().getLocalName();
            getAgent().getAgentContainer().acceptNewAgent(localAmbasadorName, ambassadorAgent).start();

            removalBehaviour.declare(myACLMessage.getSender(), ambassadorAgent.getAID(), userId);

            APP.info("'" + localAmbasadorName + "' instituated");

            // Envoi de la réponse
            sendReplyMessage(myACLMessage, localAmbasadorName);
        }
        catch (Throwable loginFailure) {
            APP.error("Creation of ambassador for user '" + userId.getLogin() + "' refused", loginFailure);
            AclMessage reply = myACLMessage.createReply(Performative.FAILURE);
            reply.setProtocol(InstituteAmbassadorProtocol.ID);
            reply.setContentObject(loginFailure);
            getAgent().send(reply);
        }
    }


    private void sendReplyMessage(AclMessage myACLMessage, String ambassadorName) {
        AclMessage aclMessage = myACLMessage.createReply();
        aclMessage.setContent(ambassadorName);
        aclMessage.setLanguage(OBJECT_LANGUAGE);
        aclMessage.setProtocol(InstituteAmbassadorProtocol.ID);
        getAgent().send(aclMessage);
    }


    private JdbcServiceHelper getJdbcService() throws ServiceException {
        return ((JdbcServiceHelper)getAgent().getHelper(JdbcServiceHelper.NAME));
    }


    private SecurityServiceHelper getSecurityService() throws ServiceException {
        return ((SecurityServiceHelper)getAgent().getHelper(SecurityServiceHelper.NAME));
    }


    @Override
    public boolean done() {
        return false;
    }
}
