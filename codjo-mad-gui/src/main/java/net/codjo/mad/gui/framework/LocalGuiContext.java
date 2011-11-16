package net.codjo.mad.gui.framework;
import net.codjo.gui.toolkit.progressbar.ProgressBarLabel;
import net.codjo.security.common.api.User;
import java.util.HashMap;
import java.util.Map;
import java.util.Observer;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
/**
 * GuiContext utilisable pour faire des modification local d'un autre environnement GuiContext.
 *
 * <p> Particulierement utile lorsque l'on veux faire transiter des informations à un écran détail. </p>
 *
 * @author $Author: gaudefr $
 * @version $Revision: 1.6 $
 */
public class LocalGuiContext implements MutableGuiContext {
    private Map<Object, Object> properties = new HashMap<Object, Object>();
    private Sender localSender;
    private SwingWorkerUtil swUtil = new SwingWorkerUtil();
    private GuiContext subContext;


    public LocalGuiContext(GuiContext subContext) {
        this.subContext = subContext;
    }


    public LocalGuiContext(GuiContext subContext, ProgressBarLabel pbl) {
        this.subContext = subContext;
        swUtil.setInfoLabel(pbl);
    }


    public JFrame getMainFrame() {
        return subContext.getMainFrame();
    }


    public JDesktopPane getDesktopPane() {
        return subContext.getDesktopPane();
    }


    public Object getProperty(Object propertyName) {
        if (properties.containsKey(propertyName)) {
            return properties.get(propertyName);
        }
        else {
            return subContext.getProperty(propertyName);
        }
    }


    public boolean hasProperty(Object propertyName) {
        return properties.containsKey(propertyName)
               || subContext.hasProperty(propertyName);
    }


    public void addObserver(Observer obs) {
        subContext.addObserver(obs);
    }


    public void displayInfo(String msg) {
        if (swUtil.getInfoLabel() != null) {
            swUtil.getInfoLabel().setText(msg);
        }
        else {
            subContext.displayInfo(msg);
        }
    }


    public void executeTask(SwingRunnable task) {
        if (swUtil.getInfoLabel() != null) {
            swUtil.executeTask(task);
        }
        else {
            subContext.executeTask(task);
        }
    }


    public void putProperty(Object propertyName, Object value) {
        properties.put(propertyName, value);
    }


    public void removeObserver(Observer obs) {
        subContext.removeObserver(obs);
    }


    public User getUser() {
        return subContext.getUser();
    }


    public void setSender(Sender sender) {
        localSender = sender;
    }


    public Sender getSender() {
        if (localSender != null) {
            return localSender;
        }
        return subContext.getSender();
    }


    public void sendEvent(GuiEvent evt) {
        subContext.sendEvent(evt);
    }


    @Override
    public String toString() {
        return "LocalGuiContext(" + properties + ") \n\tinherit from "
               + subContext.toString();
    }
}
