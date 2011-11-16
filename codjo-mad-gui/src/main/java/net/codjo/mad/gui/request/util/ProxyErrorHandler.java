package net.codjo.mad.gui.request.util;
import net.codjo.mad.gui.request.ErrorHandler;
/**
 * DOCUMENT ME!
 *
 * @version $Revision: 1.4 $
 */
public class ProxyErrorHandler implements ErrorHandler, ErrorChecker {
    private ErrorHandler target;
    private Exception exception;

    public ProxyErrorHandler(ErrorHandler target) {
        this.target = target;
    }

    public void handleError(String errorId, Exception ex) {
        exception = ex;

        target.handleError(errorId, ex);
    }


    public void clearError() {
        exception = null;
    }


    public boolean hasError() {
        return exception != null;
    }
}
