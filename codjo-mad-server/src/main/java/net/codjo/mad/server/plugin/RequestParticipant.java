/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.mad.server.plugin;
import net.codjo.agent.AclMessage;
import net.codjo.agent.ContainerFailureException;
import net.codjo.agent.MessageTemplate;
import net.codjo.agent.behaviour.CyclicBehaviour;
import net.codjo.mad.common.message.RequestProtocol;
import net.codjo.mad.server.handler.Processor;
import net.codjo.mad.server.plugin.HandlerExecutor.HandlerExecutionMode;
import net.codjo.security.common.api.User;
import net.codjo.sql.server.ConnectionPool;
import org.apache.log4j.Logger;
import org.exolab.castor.jdo.JDO;
/**
 * comportement de reception d'un message mad du client PresidentAgent par le délégué du serveur pour ce
 * client {@link AmbassadorAgent}. Les requêtes sont au format mad.
 *
 * @see RequestProtocol
 */
class RequestParticipant extends CyclicBehaviour {
    private static final Logger LOG = Logger.getLogger(AgentMadRequestContext.class.getName());
    private final HandlerExecutor executor;
    private final HandlerExecutionMode mode;
    private final MessageTemplate madRequestMessageTemplate;
    private final Processor processor;
    private final ConnectionPool pool;
    private final JDO jdo;
    private final User user;


    RequestParticipant(HandlerExecutor executor,
                       HandlerExecutionMode mode,
                       Processor processor,
                       ConnectionPool pool,
                       JDO jdo,
                       User user) {
        this.executor = executor;
        this.mode = mode;
        this.processor = processor;
        this.pool = pool;
        this.jdo = jdo;
        this.user = user;
        madRequestMessageTemplate =
              MessageTemplate.and(MessageTemplate.matchLanguage(AclMessage.XML_LANGUAGE),
                                  MessageTemplate.matchProtocol(RequestProtocol.ID));
    }


    @Override
    public void action() {
        AclMessage aclMessage = getAgent().receive(madRequestMessageTemplate);
        if (aclMessage == null) {
            block();
            return;
        }

        try {
            doAction(aclMessage);
        }
        catch (ContainerFailureException e) {
            LOG.error(e.getMessage(), e);
        }
    }


    private void doAction(AclMessage aclMessage) throws ContainerFailureException {
        AmbassadorDelegateAgent delegateAgent = new AmbassadorDelegateAgent(aclMessage,
                                                                            executor,
                                                                            processor,
                                                                            pool,
                                                                            jdo,
                                                                            user
        );
        switch (mode) {
            case SYNCHRONOUS:
                delegateAgent.executeAndReply(getAgent(), aclMessage);
                break;
            case ASYNCHRONOUS:
                getAgent().getAgentContainer()
                      .acceptNewAgent(delegateAgent.createNickName("ambassador-delegate-"), delegateAgent)
                      .start();
                break;
        }
    }
}
