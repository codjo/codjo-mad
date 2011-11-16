/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.mad.server.handler;
import net.codjo.mad.common.Log;
/**
 * Classe utilitaire pour la gestion des erreurs.
 *
 * @author $Author: gaudefr $
 * @version $Revision: 1.6 $
 */
final class ErrorUtil {
    private ErrorUtil() {}

    public static String determineErrorResult(Exception ex) {
        Log.error("Erreur = " + ex);

        Exception root = ErrorUtil.findRootException(ex);
        if (ex != root) {
            Log.error("Erreur causé par = " + root.getLocalizedMessage(), root);
        }
        else {
            Log.error("Erreur causé par = " + ex.getLocalizedMessage(), ex);
        }

        if (ex instanceof RequestFailureException) {
            Log.error("Retour Erreur : " + ((RequestFailureException)ex).getErrorXml());
            return ((RequestFailureException)ex).getErrorXml();
        }
        else {
            return RequestFailureException.buildErrorNode(root, "Interne");
        }
    }


    /**
     * Trouve l'exception racine ayant declench" l'erreur.
     *
     * @param error erreur
     *
     * @return erreur racine
     */
    public static Exception findRootException(Exception error) {
        return findRootException(error, error);
    }


    private static Exception findRootException(Exception father, Exception son) {
        if (son == null) {
            return father;
        }
        else if (son instanceof RequestFailureException) {
            return findRootException(son, ((RequestFailureException)son).getCausedBy());
        }
        else if (son instanceof HandlerException) {
            return findRootException(son, ((HandlerException)son).getCausedBy());
        }
        else {
            return son;
        }
    }
}
