package net.codjo.mad.gui.request.wrapper;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.UndoableEditListener;

public class ToggleButtonWrapper extends AbstractWrapper {
    private BasicUndoableSupport support = new BasicUndoableSupport(this);
    private JToggleButton comp;


    public ToggleButtonWrapper(String fieldName, JToggleButton comp) {
        super(fieldName, comp);
        this.comp = comp;
        comp.addChangeListener(new ComponentChangeListener());
        initPreviousValue();
    }


    public void setXmlValue(String value) {
        comp.setSelected("true".equalsIgnoreCase(value));
    }


    public String getXmlValue() {
        return comp.isSelected() ? "true" : "false";
    }


    public String getDisplayValue() {
        return comp.isSelected() ? "true" : "false";
    }


    public void addUndoableEditListener(UndoableEditListener listener) {
        support.addUndoableEditListener(listener);
    }


    public void removeUndoableEditListener(UndoableEditListener listener) {
        support.removeUndoableEditListener(listener);
    }


    private class ComponentChangeListener implements ChangeListener {
        public void stateChanged(ChangeEvent event) {
            fireChangeEvent();
        }
    }
}
