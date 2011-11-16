package net.codjo.mad.gui.framework;
import net.codjo.gui.toolkit.progressbar.ProgressBarLabel;
import net.codjo.security.common.api.User;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import org.apache.log4j.Logger;
/**
 * Classe par défaut d'un contexte.
 */
public class DefaultGuiContext extends Observable implements MutableGuiContext {
    private static final Logger APP = Logger.getLogger(DefaultGuiContext.class);
    static final String UNDEFINED_USER =
          "Utilisateur non défini. Le client n'a probablement pas fini le cycle de démarrage";
    private Map<Object, Object> properties = new HashMap<Object, Object>();
    private SwingWorkerUtil swUtil = new SwingWorkerUtil();
    private JDesktopPane desktopPane;
    private JFrame mainFrame;
    private User user;
    private Sender sender;


    public DefaultGuiContext(JDesktopPane dk) {
        setDesktopPane(dk);
    }


    public DefaultGuiContext() {
    }


    public void setMainFrame(JFrame mainFrame) {
        this.mainFrame = mainFrame;
    }


    public JFrame getMainFrame() {
        return mainFrame;
    }


    public void setDesktopPane(JDesktopPane desktopPane) {
        this.desktopPane = desktopPane;
    }


    public void setInfoLabel(ProgressBarLabel infoLabel) {
        swUtil.setInfoLabel(infoLabel);
    }


    public JDesktopPane getDesktopPane() {
        return desktopPane;
    }


    public Object getProperty(Object propertyName) {
        if (!properties.containsKey(propertyName)) {
            APP.error(properties);
            throw new IllegalArgumentException("Propertie inconnue : " + propertyName);
        }

        return properties.get(propertyName);
    }


    public boolean hasProperty(Object propertyName) {
        return properties.containsKey(propertyName);
    }


    public void displayInfo(String msg) {
        if (swUtil.getInfoLabel() != null) {
            swUtil.getInfoLabel().setText(msg);
        }
        else {
            APP.info("info> " + msg);
        }
    }


    public void executeTask(SwingRunnable task) {
        swUtil.executeTask(task);
    }


    public void putAllProperties(Map<Object, Object> map) {
        properties.putAll(map);
    }


    public void putProperty(Object propertyName, Object value) {
        properties.put(propertyName, value);
    }


    public void removeObserver(Observer obs) {
        deleteObserver(obs);
    }


    public User getUser() {
        if (user == null) {
            throw new IllegalStateException(UNDEFINED_USER);
        }
        return user;
    }


    public void setUser(User user) {
        this.user = user;
    }


    public void sendEvent(GuiEvent evt) {
        setChanged();
        notifyObservers(evt);
    }


    public void setSender(Sender sender) {
        this.sender = sender;
    }


    public Sender getSender() {
        return sender;
    }


    @Override
    public String toString() {
        return "DefaultGuiContext(" + properties + ")";
    }
}
