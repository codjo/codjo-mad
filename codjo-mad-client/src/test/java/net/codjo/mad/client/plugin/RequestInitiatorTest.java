package net.codjo.mad.client.plugin;
import net.codjo.agent.AclMessage;
import net.codjo.agent.test.BehaviourTestCase;
import net.codjo.mad.common.ZipUtil;
import net.codjo.mad.common.message.RequestProtocol;
/**
 * Classe de test de {@link net.codjo.mad.client.plugin.RequestInitiator}.
 */
public class RequestInitiatorTest extends BehaviourTestCase {
    public void test_action() throws Exception {
        PresidentAgentMock myAgent = new PresidentAgentMock();

        RequestSynchronizer requestSynchronizer = new RequestSynchronizerMock(myAgent);
        String expectedConversationId = requestSynchronizer.toString();

        RequestInitiator requestInitiator = new RequestInitiator(requestSynchronizer);
        requestInitiator.setAgent(myAgent);

        requestInitiator.action();
        assertFalse(requestInitiator.done());
        myAgent.getLog().assertContent("agent.send(" + myAgent.getAmbassadorName() + ", "
                                       + expectedConversationId + ", madRequest)");
        AclMessage lastSentMessage = myAgent.getLastSentMessage();
        assertEquals(RequestProtocol.ID, lastSentMessage.getProtocol());
        myAgent.getLog().clear();

        AclMessage aclMessage = new AclMessage(AclMessage.Performative.REQUEST);
        aclMessage.setByteSequenceContent(ZipUtil.zip("résultat"));
        myAgent.mockReceiveContent(aclMessage);
        requestInitiator.action();
        assertTrue(requestInitiator.done());

        myAgent.getLog().assertContent("agent.receive((ConversationId: "
                                       + expectedConversationId + ")), synchro.receiveResponse(résultat)");
    }


    public class RequestSynchronizerMock extends RequestSynchronizer {
        private final PresidentAgentMock myAgent;


        public RequestSynchronizerMock(PresidentAgentMock myAgent) {
            this.myAgent = myAgent;
        }


        @Override
        public synchronized void receiveResponse(String receivedResponse) {
            myAgent.getLog().call("synchro.receiveResponse", receivedResponse);
        }


        @Override
        public String getRequest() {
            return "madRequest";
        }
    }
}
