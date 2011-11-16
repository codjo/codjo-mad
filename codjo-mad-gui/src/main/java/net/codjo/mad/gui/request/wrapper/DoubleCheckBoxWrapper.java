package net.codjo.mad.gui.request.wrapper;
import net.codjo.gui.toolkit.DoubleCheckBoxField;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.event.UndoableEditListener;
/**
 * Classe responsable de ..
 *
 * @author $Author: gaudefr $
 * @version $Revision: 1.3 $
 */
public class DoubleCheckBoxWrapper extends AbstractWrapper {
    private BasicUndoableSupport support = new BasicUndoableSupport(this);
    private DoubleCheckBoxField comp;


    DoubleCheckBoxWrapper(String fieldName, DoubleCheckBoxField comp) {
        super(fieldName, comp);
        this.comp = comp;
        comp.addPropertyChangeListener(DoubleCheckBoxField.VALUE_PROPERTY_NAME,
                                       new DoubleCheckBoxFieldChangeListener());
        initPreviousValue();
    }


    public void setXmlValue(String value) {
        comp.setSelected("true".equalsIgnoreCase(value));
    }


    public String getXmlValue() {
        return comp.isSelected() ? "true" : "false";
    }


    public String getDisplayValue() {
        return getXmlValue();
    }


    public void addUndoableEditListener(UndoableEditListener listener) {
        support.addUndoableEditListener(listener);
    }


    public void removeUndoableEditListener(UndoableEditListener listener) {
        support.removeUndoableEditListener(listener);
    }


    private class DoubleCheckBoxFieldChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            fireChangeEvent();
        }
    }
}
