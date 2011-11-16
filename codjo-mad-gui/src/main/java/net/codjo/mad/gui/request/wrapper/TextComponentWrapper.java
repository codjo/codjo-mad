package net.codjo.mad.gui.request.wrapper;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.JTextComponent;
/**
 * Wrapper pour un JTextField
 *
 * @author $Author: gaudefr $
 * @version $Revision: 1.5 $
 */
public class TextComponentWrapper extends AbstractWrapper {
    private JTextComponent comp;
    private ComponentChangeListener guiListener = new ComponentChangeListener();

    TextComponentWrapper(String fieldName, JTextComponent comp) {
        super(fieldName, comp);
        this.comp = comp;
        comp.getDocument().addDocumentListener(guiListener);
        initPreviousValue();
    }

    public void setXmlValue(String value) {
        comp.getDocument().removeDocumentListener(guiListener);
        try {
            if (NULL.equals(value)) {
                comp.setText("");
            }
            else {
                comp.setText(value);
            }
            fireChangeEvent();
        }
        finally {
            comp.getDocument().addDocumentListener(guiListener);
        }
    }


    public String getXmlValue() {
        if ("".equals(comp.getText())) {
            return NULL;
        }
        return comp.getText();
    }


    public String getDisplayValue() {
        return comp.getText();
    }


    public void addUndoableEditListener(UndoableEditListener listener) {
        comp.getDocument().addUndoableEditListener(listener);
    }


    public void removeUndoableEditListener(UndoableEditListener listener) {
        comp.getDocument().removeUndoableEditListener(listener);
    }

    private class ComponentChangeListener implements DocumentListener {
        public void changedUpdate(DocumentEvent event) {
            fireChangeEvent();
        }


        public void insertUpdate(DocumentEvent event) {
            fireChangeEvent();
        }


        public void removeUpdate(DocumentEvent event) {
            fireChangeEvent();
        }
    }
}
