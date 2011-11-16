package net.codjo.mad.client.plugin;
import net.codjo.agent.AclMessage;
import net.codjo.agent.AclMessage.Performative;
import net.codjo.agent.Aid;
import net.codjo.agent.Behaviour;
import net.codjo.agent.MessageTemplate;
import net.codjo.agent.UserId;
import net.codjo.agent.behaviour.CyclicBehaviour;
import net.codjo.mad.common.message.InstituteAmbassadorProtocol;
import net.codjo.util.system.EventSynchronizer;
import org.apache.log4j.Logger;
/**
 * Comportement du {@link net.codjo.mad.client.plugin.PresidentAgent} qui envoie un message au
 * SecretaryGeneralAgent dès sa création.
 *
 * @see net.codjo.mad.common.message.InstituteAmbassadorProtocol
 */
class InstituteAmbassadorInitiator extends Behaviour {
    private static final Logger APP = Logger.getLogger(InstituteAmbassadorInitiator.class.getName());
    private final UserId userId;
    private final Aid secretaryGeneralAID;
    private EventSynchronizer<InstitueEvent> synchronizer;
    private final MessageTemplate responseTemplate;
    private int step = 1;
    private boolean receivedResponse;


    InstituteAmbassadorInitiator(UserId userId,
                                 Aid secretaryGeneralAID,
                                 EventSynchronizer<InstitueEvent> synchronizer) {
        this.userId = userId;
        this.secretaryGeneralAID = secretaryGeneralAID;
        this.synchronizer = synchronizer;

        responseTemplate = MessageTemplate.and(MessageTemplate.matchLanguage(AclMessage.OBJECT_LANGUAGE),
                                               MessageTemplate.matchProtocol(InstituteAmbassadorProtocol.ID));
    }


    @Override
    protected void action() {
        if (step == 1) {
            sendRequest();
        }
        else {
            waitResponse();
        }
    }


    private void sendRequest() {
        AclMessage aclMessage = new AclMessage(AclMessage.Performative.REQUEST);
        aclMessage.setLanguage(AclMessage.OBJECT_LANGUAGE);
        aclMessage.setProtocol(InstituteAmbassadorProtocol.ID);
        aclMessage.encodeUserId(userId);
        aclMessage.addReceiver(secretaryGeneralAID);

        getAgent().send(aclMessage);
        step = 2;
    }


    private void waitResponse() {
        AclMessage aclMessage = getAgent().receive(responseTemplate);
        if (aclMessage == null) {
            block();
            return;
        }

        if (aclMessage.getPerformative().equals(Performative.FAILURE)) {
            APP.info("Ambassador cannot be created");
            synchronizer.receivedEvent(InstitueEvent.failureEvent(aclMessage));
            getAgent().die();
        }
        else {
            PresidentAgent presidentAgent = (PresidentAgent)getAgent();
            String ambassadorName = aclMessage.getContent();
            receivedResponse = true;

            presidentAgent.setAmbassadorName(ambassadorName);
            APP.info("Ambassador '" + ambassadorName + "' instituated");
            presidentAgent.addBehaviour(new ObjectListenerBehavior());

            synchronizer.receivedEvent(InstitueEvent.successEvent());
        }
    }


    @Override
    public boolean done() {
        return receivedResponse;
    }


    public static class ObjectListenerBehavior extends CyclicBehaviour {
        @Override
        public void action() {
            Object obj = getAgent().getO2AObject();
            if (obj == null) {
                block();
                return;
            }
            RequestSynchronizer synchronizer = (RequestSynchronizer)obj;
            getAgent().addBehaviour(new RequestInitiator(synchronizer));
        }
    }
}
