package net.codjo.mad.gui.framework;
import java.awt.Cursor;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.UIManager;
import org.apache.log4j.Logger;
/**
 * Classe abstraite facilitant l'implantation d'une action utilisant le framework.
 *
 * @author $Author: gaudefr $
 * @version $Revision: 1.6 $
 */
public abstract class AbstractGuiAction extends AbstractAction {
    public static final String ID_PROPERTY = "ID";
    private static final Logger APP = Logger.getLogger(AbstractGuiAction.class);
    private GuiContext guiContext;
    private String securityFunction;


    protected AbstractGuiAction(GuiContext ctxt, String name, String description) {
        this(ctxt, name, description, null);
    }


    protected AbstractGuiAction(GuiContext ctxt, String name, String description,
                                String iconId) {
        this(ctxt, name, description, iconId, null);
    }


    protected AbstractGuiAction(GuiContext ctxt, String name, String description,
                                String iconId, String actionId) {
        if (ctxt == null) {
            throw new NullPointerException("Aucun contexte IHM.");
        }
        putValue(NAME, name);
        putValue(ID_PROPERTY, actionId);
        putValue(SHORT_DESCRIPTION, description);

        if (iconId != null) {
            Icon icon = UIManager.getIcon(iconId);
            if (icon == null) {
                icon = loadIcon(iconId);
            }
            putValue(SMALL_ICON, icon);
        }
        this.guiContext = ctxt;
        setSecurityFunction(getClass().getName());
    }


    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled && isActivable()
                         && getGuiContext().getUser().isAllowedTo(getSecurityFunction()));
    }


    protected boolean isActivable() {
        return true;
    }


    protected String getSecurityFunction() {
        return securityFunction;
    }


    public void setSecurityFunction(String securityFunction) {
        this.securityFunction = securityFunction;
        super.setEnabled(guiContext.getUser().isAllowedTo(getSecurityFunction()));
    }


    private javax.swing.Icon loadIcon(String fileName) {
        java.net.URL resource = this.getClass().getResource(fileName);
        if (resource != null) {
            return new ImageIcon(resource);
        }
        else {
            String errorMsg =
                  "[ERREUR] L'icone " + fileName + " est introuvable à partir " + " de "
                  + this.getClass();
            errorMsg =
                  errorMsg + "\tNB : Il est aussi possible de mettre cet icone "
                  + " dans le UIManager";
            APP.error(errorMsg);
            return null;
        }
    }


    protected JDesktopPane getDesktopPane() {
        return guiContext.getDesktopPane();
    }


    protected JFrame getMainFrame() {
        return guiContext.getMainFrame();
    }


    protected GuiContext getGuiContext() {
        return guiContext;
    }


    protected void sendEvent(GuiEvent evt) {
        getGuiContext().sendEvent(evt);
    }


    protected void displayWaitCursor() {
        JFrame mainFrame = getGuiContext().getMainFrame();
        if (mainFrame != null) {
            getGuiContext().getMainFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        }
    }


    protected void displayDefaultCursor() {
        JFrame mainFrame = getGuiContext().getMainFrame();
        if (mainFrame != null) {
            getGuiContext().getMainFrame().setCursor(Cursor.getDefaultCursor());
        }
    }
}
