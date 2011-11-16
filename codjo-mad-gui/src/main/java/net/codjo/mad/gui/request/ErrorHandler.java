package net.codjo.mad.gui.request;
/**
 * Gestionnaire d'erreur.
 */
public interface ErrorHandler {
    void handleError(String errorId, Exception ex);
}
