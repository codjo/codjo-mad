package net.codjo.mad.client.request;
import net.codjo.mad.client.request.util.CastorHelper;
import org.apache.log4j.Logger;
/**
 * Factory des <code>ResultManager</code>.
 */
public final class ResultFactory {
    private static final Logger APP = Logger.getLogger(ResultFactory.class);


    private ResultFactory() {
    }


    public static ResultManager buildResultManager(String xmlResult)
          throws BuildException {
        try {
            long startTime = System.currentTimeMillis();
            ResultManager resultManager = (ResultManager)CastorHelper.unmarshaller(xmlResult,
                                                                                   ResultManager.class,
                                                                                   "resultMapping.xml");
            long endTime = System.currentTimeMillis();
            if (APP.isDebugEnabled()) {
                APP.debug("Désérialisation du xml réalisée en " + (endTime - startTime) + " ms");
            }
            return resultManager;
        }
        catch (Exception ex) {
            APP.error("Erreur lors du traitement de :" + xmlResult, ex);
            throw new BuildException(ex.toString());
        }
    }


    /**
     * Erreur lors de la construction.
     */
    public static final class BuildException extends Exception {
        public BuildException(String msg) {
            super(msg);
        }
    }
}
