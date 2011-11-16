package net.codjo.mad.gui.request.wrapper;
import net.codjo.gui.toolkit.date.DateField;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Date;
import javax.swing.event.UndoableEditListener;
/**
 * Wrapper pour les panels : DateField
 *
 * @author $Author: gaudefr $
 * @version $Revision: 1.3 $
 */
public class DateFieldWrapper extends AbstractWrapper {
    private BasicUndoableSupport support = new BasicUndoableSupport(this);
    private DateField comp;


    DateFieldWrapper(String fieldName, DateField comp) {
        super(fieldName, comp);
        this.comp = comp;
        comp.addPropertyChangeListener(DateField.DATE_PROPERTY_NAME, new DateFieldChangeListener());
        initPreviousValue();
    }


    public void setXmlValue(String value) {
        if (AbstractWrapper.NULL.equals(value)) {
            comp.setDate(null);
        }
        else {
            comp.setDate(java.sql.Date.valueOf(value));
        }
    }


    public String getXmlValue() {
        Date date = comp.getDate();
        if (date != null) {
            return new java.sql.Date(date.getTime()).toString();
        }
        else {
            return AbstractWrapper.NULL;
        }
    }


    public String getDisplayValue() {
        Date date = comp.getDate();
        if (date != null) {
            return new java.sql.Date(date.getTime()).toString();
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


    private class DateFieldChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            fireChangeEvent();
        }
    }
}
