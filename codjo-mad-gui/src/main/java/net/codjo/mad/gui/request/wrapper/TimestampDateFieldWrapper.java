package net.codjo.mad.gui.request.wrapper;
import net.codjo.gui.toolkit.date.TimestampDateField;
import java.sql.Timestamp;
import javax.swing.event.UndoableEditListener;
/**
 * Wrapper pour les panels : TimestampDateField
 *
 * @author $Author: gaudefr $
 * @version $Revision: 1.3 $
 */
public class TimestampDateFieldWrapper extends AbstractWrapper {
    private TimestampDateField comp;


    TimestampDateFieldWrapper(String fieldName, TimestampDateField comp) {
        super(fieldName, comp);
        this.comp = comp;
        initPreviousValue();
    }


    public void setXmlValue(String value) {
        if (AbstractWrapper.NULL.equals(value)) {
            comp.setDate(null);
        }
        else {
            comp.setDate(java.sql.Timestamp.valueOf(value));
        }
    }


    public String getXmlValue() {
        Timestamp date = comp.getDate();
        if (date != null) {
            return date.toString();
        }
        else {
            return AbstractWrapper.NULL;
        }
    }


    public String getDisplayValue() {
        Timestamp date = comp.getDate();
        if (date != null) {
            return date.toString();
        }
        else {
            return "";
        }
    }


    public void addUndoableEditListener(UndoableEditListener listener) {
    }


    public void removeUndoableEditListener(UndoableEditListener listener) {
    }
}
