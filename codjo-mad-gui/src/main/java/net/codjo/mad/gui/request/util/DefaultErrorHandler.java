package net.codjo.mad.gui.request.util;
import net.codjo.gui.toolkit.util.ErrorDialog;
import net.codjo.mad.gui.request.ErrorHandler;
import java.awt.Component;
/**
 * Implantation par défaut du ErrorHandler..
 */
public class DefaultErrorHandler implements ErrorHandler {
    private final Component parent;
    private final String message;

    public DefaultErrorHandler(Component parent, String message) {
        this.parent = parent;
        this.message = message;
    }


    public DefaultErrorHandler(String message) {
        this(null, message);
    }

    public void handleError(String errorId, Exception ex) {
        ErrorDialog.show(parent, message, ex);
    }
}
