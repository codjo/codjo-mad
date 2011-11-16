package net.codjo.mad.gui.request.wrapper;
import net.codjo.gui.toolkit.number.NumberField;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import javax.swing.event.UndoableEditListener;
/**
 * Description of the Class
 *
 * @author $Author: gaudefr $
 * @version $Revision: 1.3 $
 */
public class NumberFieldWrapper extends AbstractWrapper {
    private BasicUndoableSupport support = new BasicUndoableSupport(this);
    private NumberField comp;


    NumberFieldWrapper(String fieldName, NumberField comp) {
        super(fieldName, comp);
        this.comp = comp;
        comp.addPropertyChangeListener(NumberField.NUMBER_PROPERTY,
                                       new ComponentChangeListener());
        initPreviousValue();
    }


    public void setXmlValue(String value) {
        if (AbstractWrapper.NULL.equals(value)) {
            comp.setNumber(null);
        }
        else {
            comp.setNumber(new BigDecimal(value));
        }
    }


    public String getXmlValue() {
        Number nb = comp.getNumber();
        if (nb != null) {
            return nb.toString();
        }
        else {
            return AbstractWrapper.NULL;
        }
    }


    public String getDisplayValue() {
        Number nb = comp.getNumber();
        if (nb != null) {
            return nb.toString();
        }
        else {
            return "";
        }
    }


    public void addUndoableEditListener(UndoableEditListener listener) {
        support.addUndoableEditListener(listener);
    }


    public void removeUndoableEditListener(UndoableEditListener listener) {
        support.removeUndoableEditListener(listener);
    }


    private class ComponentChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            fireChangeEvent();
        }
    }
}
