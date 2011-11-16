package net.codjo.mad.client.request;
import net.codjo.mad.client.plugin.MadConnectionConfiguration;
import net.codjo.mad.client.request.util.ServerWrapper;
import net.codjo.mad.client.request.util.ServerWrapperFactory;
import java.rmi.RemoteException;
import org.apache.log4j.Logger;
/**
 * Envoie des requetes aux serveurs et traite le resultat.
 *
 * @deprecated utiliser {@link net.codjo.mad.client.plugin.MadConnectionPlugin#getOperations()} ou
 *             GuiContext.getSender().
 */
@Deprecated
public class RequestSender {
    private static final String XML_HEADER = "<?xml version=\"1.0\"?>";
    private static final Logger APP = Logger.getLogger(RequestSender.class);
    private long timeout = MadConnectionConfiguration.DEFAULT_TIME_OUT;
    static final String TIMEOUT_ERROR_MESSAGE
          = "Erreur de communication avec le serveur: Il n'a pas répondu dans le temps imparti";


    public RequestSender() {
    }


    public RequestSender(long timeout) {
        this.timeout = timeout;
    }


    @Deprecated
    public String buildRequests(Request[] requests) {
        StringBuffer returnedReq = new StringBuffer(XML_HEADER + "\n");
        returnedReq.append("<requests>").append(getAudit());
        for (Request request : requests) {
            String xml = request.toXml();
            returnedReq.append(xml);
        }
        returnedReq.append("</requests>");
        return returnedReq.toString();
    }


    @Deprecated
    public ResultManager send(Request request) throws RequestException {
        return send(new Request[]{request});
    }


    @Deprecated
    public ResultManager send(Request[] requests) throws RequestException {
        ServerWrapper wrapper = ServerWrapperFactory.createWrapper();
        return send(requests, wrapper);
    }


    @Deprecated
    public ResultManager send(Request[] requests, ServerWrapper serverWrapper) throws RequestException {
        try {
            try {
                String result = sendRequests(requests, serverWrapper);

                if (result == null) {
                    throw new RequestException(TIMEOUT_ERROR_MESSAGE + " (> " + (timeout / 1000) + "s).");
                }

                if (APP.isDebugEnabled()) {
                    debug(requests, result);
                }

                return ResultFactory.buildResultManager(result);
            }
            finally {
                serverWrapper.close();
            }
        }
        catch (RequestException error) {
            logError(error, requests);
            throw error;
        }
        catch (Exception error) {
            logError(error, requests);
            throw new RequestException(error.getMessage());
        }
    }


    private void debug(Request[] requests, String result) {
        APP.debug("*****************************************");
        APP.debug(buildRequests(requests));
        APP.debug("-----------------------------------------");
        APP.debug(result);
        APP.debug("*****************************************");
    }


    private void logError(Exception error, Request[] requests) {
        APP.error("Erreur lors de l'envoie de la requête : ", error);
        APP.error(buildRequests(requests));
        APP.error("Erreur renvoyée : ");
    }


    private String getAudit() {
        return "<audit><user>" + System.getProperty("user.name") + "</user></audit>";
    }


    private String sendRequests(final Request[] requests, final ServerWrapper wrapper)
          throws RemoteException {
        long startTime = 0;
        if (APP.isInfoEnabled()) {
            startTime = System.currentTimeMillis();
            APP.info("> Envoie de " + requests.length + " requete(s) au serveur "
                     + requests[0].toString() + " avec un timeout de " + timeout + " ms");
        }

        String result = wrapper.sendWaitResponse(buildRequests(requests), timeout);

        if (APP.isInfoEnabled()) {
            long endTime = System.currentTimeMillis();
            APP.info("< Envoie de la requete au serveur (" + (endTime - startTime) + " ms) ");
        }

        return result;
    }
}
