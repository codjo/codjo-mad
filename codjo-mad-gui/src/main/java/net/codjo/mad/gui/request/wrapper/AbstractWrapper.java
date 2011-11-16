package net.codjo.mad.gui.request.wrapper;
import net.codjo.mad.gui.request.FieldType;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.JComponent;
/**
 * Description of the Class
 *
 * @author $Author: gaudefr $
 * @version $Revision: 1.6 $
 */
public abstract class AbstractWrapper implements GuiWrapper {
    protected static final String NULL = "null";
    protected PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    private JComponent component;
    private String fieldName;
    private String previousValue;

    protected AbstractWrapper(String fieldName, JComponent component) {
        setFieldName(fieldName);
        this.component = component;
    }

    public final FieldType getFieldType() {
        FieldType type = (FieldType)component.getClientProperty(FieldType.EDIT_MODE);
        if (type == null) {
            return FieldType.N_A;
        }
        else {
            return type;
        }
    }


    public final Integer getUpdateOrder() {
        Integer order =
            (Integer)component.getClientProperty(net.codjo.mad.gui.request.DetailDataSource.UPDATE_PRIORITY);
        if (order == null) {
            return net.codjo.mad.gui.request.DetailDataSource.NORMAL_PRIORITY;
        }
        else {
            return order;
        }
    }


    public String getFieldName() {
        return fieldName;
    }


    public JComponent getGuiComponent() {
        return this.component;
    }


    public synchronized void addPropertyChangeListener(PropertyChangeListener pcl) {
        propertyChangeSupport.addPropertyChangeListener(pcl);
    }


    public synchronized void removePropertyChangeListener(PropertyChangeListener pcl) {
        propertyChangeSupport.removePropertyChangeListener(pcl);
    }


    public void fireChangeEvent() {
        this.propertyChangeSupport.firePropertyChange(getFieldName(), getPreviousValue(), getXmlValue());
        initPreviousValue();
    }


    protected void initPreviousValue() {
        setPreviousValue(getXmlValue());
    }


    private void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }


    private void setPreviousValue(String previousValue) {
        this.previousValue = previousValue;
    }


    private String getPreviousValue() {
        return previousValue;
    }
}
