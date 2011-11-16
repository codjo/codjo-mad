package net.codjo.mad.client.plugin;
import net.codjo.agent.AclMessage;
import net.codjo.agent.Aid;
import net.codjo.agent.Behaviour;
import net.codjo.agent.MessageTemplate;
import net.codjo.mad.common.ZipUtil;
import net.codjo.mad.common.message.RequestProtocol;
import java.io.IOException;
import org.apache.log4j.Logger;
/**
 * comportement d'envoie d'un message du client {@link PresidentAgent} vers le délégué du serveur pour ce
 * client Ambassadeur.
 */
class RequestInitiator extends Behaviour {
    private static final Logger APP = Logger.getLogger(RequestInitiator.class.getName());
    private static final String UNZIP_ERROR_MESSAGE = "Impossible de dézipper le résultat de la requête.";

    private final RequestSynchronizer requestSynchronizer;
    private boolean receivedResponse;
    private int step = 1;


    RequestInitiator(RequestSynchronizer requestSynchronizer) {
        this.requestSynchronizer = requestSynchronizer;
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
        aclMessage.setContent(requestSynchronizer.getRequest());
        aclMessage.setConversationId(requestSynchronizer.toString());
        aclMessage.setLanguage(AclMessage.XML_LANGUAGE);
        aclMessage.setProtocol(RequestProtocol.ID);
        PresidentAgent presidentAgent = (PresidentAgent)getAgent();
        aclMessage.addReceiver(new Aid(presidentAgent.getAmbassadorName()));
        getAgent().send(aclMessage);
        step = 2;
    }


    private void waitResponse() {
        AclMessage aclMessage =
              getAgent().receive(MessageTemplate.matchConversationId(
                    requestSynchronizer.toString()));
        if (aclMessage == null) {
            block();
            return;
        }
        requestSynchronizer.receiveResponse(unzip(aclMessage));
        receivedResponse = true;
    }


    private String unzip(AclMessage aclMessage) {
        try {
            long startTime = System.currentTimeMillis();
            String unziped = ZipUtil.unzip(aclMessage.getByteSequenceContent());
            long endTime = System.currentTimeMillis();
            if (APP.isDebugEnabled()) {
                APP.debug("Décompression du message reçu en " + (endTime - startTime) + " ms");
            }
            return unziped;
        }
        catch (IOException e) {
            APP.error(UNZIP_ERROR_MESSAGE, e);
        }
        return UNZIP_ERROR_MESSAGE;
    }


    @Override
    public boolean done() {
        return receivedResponse;
    }
}
