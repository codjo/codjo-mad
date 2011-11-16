package net.codjo.mad.client.plugin;
import net.codjo.agent.AgentController;
import net.codjo.agent.BadControllerException;
/**
 * Permet de synchroniser les threads swing et agent pour l'envoie de requete.
 */
class RequestSynchronizer {
    private final Object lock = new Object();
    private String request;
    private String response = null;
    private AgentController guiAgent;


    RequestSynchronizer(AgentController guiAgent) {
        this.guiAgent = guiAgent;
    }


    /**
     * pour les tests.
     */
    RequestSynchronizer() {}


    private AgentController getGuiAgent() {
        return guiAgent;
    }


    public String sendRequest(String newRequest, long timeout)
          throws BadControllerException, InterruptedException {
        request = newRequest;

        getGuiAgent().putO2AObject(this);

        synchronized (lock) {
            if (response == null) {
                lock.wait(timeout);
            }
        }

        return response;
    }


    public void receiveResponse(String receivedResponse) {
        synchronized (lock) {
            response = receivedResponse;
            lock.notifyAll();
        }
    }


    public String getRequest() {
        return request;
    }
}
