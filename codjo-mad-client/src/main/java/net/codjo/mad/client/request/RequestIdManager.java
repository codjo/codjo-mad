package net.codjo.mad.client.request;
/**
 * Classe responsable de la generation des id des demandes effectuees par un Request.
 *
 * @author $Author: levequt $
 * @version $Revision: 1.6 $
 */
public final class RequestIdManager {
    private static RequestIdManager instance = null;
    private long id = 0;

    private RequestIdManager() {}

    public static synchronized RequestIdManager getInstance() {
        if (instance == null) {
            instance = new RequestIdManager();
        }
        return instance;
    }


    public synchronized String getNewRequestId() {
        return String.valueOf(++id);
    }

    public synchronized void reset() {
        id = 0;
    }
}
