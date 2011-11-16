package net.codjo.mad.gui.request.wrapper;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.JTextComponent;
import net.codjo.mad.gui.request.RequestTextFieldAutoCompleter;

public class RequestTextFieldAutoCompleterWrapper extends AbstractWrapper {
    private ComponentChangeListener guiListener = new ComponentChangeListener();
    private RequestTextFieldAutoCompleter autoCompleter;
    private JTextComponent textComponent;


    RequestTextFieldAutoCompleterWrapper(String fieldName, RequestTextFieldAutoCompleter comp) {
        super(fieldName, comp.getTextComponent());
        this.autoCompleter = comp;
        this.textComponent = autoCompleter.getTextComponent();
        textComponent.getDocument().addDocumentListener(guiListener);
        initPreviousValue();
    }

    public void setXmlValue(String value) {
        textComponent.getDocument().removeDocumentListener(guiListener);
        try {
            autoCompleter.setCode(value);
            fireChangeEvent();
        }
        finally {
            textComponent.getDocument().addDocumentListener(guiListener);
        }
    }


    public String getXmlValue() {
        if ("".equals(textComponent.getText())) {
            return NULL;
        }
        return autoCompleter.getSelectedCode();
    }


    public String getDisplayValue() {
        return textComponent.getText();
    }


    public void addUndoableEditListener(UndoableEditListener listener) {
        textComponent.getDocument().addUndoableEditListener(listener);
    }


    public void removeUndoableEditListener(UndoableEditListener listener) {
        textComponent.getDocument().removeUndoableEditListener(listener);
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
