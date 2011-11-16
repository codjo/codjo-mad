package net.codjo.mad.gui.request;
import java.awt.Component;
import java.awt.event.FocusAdapter;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.ComboBoxEditor;
import javax.swing.JComboBox;
import javax.swing.text.JTextComponent;
import org.jdesktop.swingx.autocomplete.AbstractAutoCompleteAdaptor;
import org.jdesktop.swingx.autocomplete.AutoCompleteComboBoxEditor;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import org.jdesktop.swingx.autocomplete.AutoCompleteDocument;
import org.jdesktop.swingx.autocomplete.AutoCompletePropertyChangeListener;
import org.jdesktop.swingx.autocomplete.ComboBoxAdaptor;
import org.jdesktop.swingx.autocomplete.ObjectToStringConverter;
import org.jdesktop.swingx.autocomplete.workarounds.MacOSXPopupLocationFix;
/**
 * This decorator fix the following bug:
 * A user type the begin of an item stored in the middle of a huge combobox.
 * The popup scroll to the end.
 * The bug is on the method setSelectedItem of the class ComboBoxAdaptor  
 */
public class CustomAutoCompleteComboDecorator extends AutoCompleteDecorator {

    private static void removePropertyChangeListener(Component c) {
        PropertyChangeListener[] listeners = c.getPropertyChangeListeners("editor");

        for (PropertyChangeListener l : listeners) {
            if (l instanceof AutoCompletePropertyChangeListener) {
                c.removePropertyChangeListener("editor", l);
            }
        }
    }


    private static void removeKeyListener(Component c) {
        KeyListener[] listeners = c.getKeyListeners();

        for (KeyListener l : listeners) {
            if (l instanceof CustomAutoCompleteKeyAdapter) {
                c.removeKeyListener(l);
            }
        }
    }


    public static void decorate(final JComboBox comboBox, final ObjectToStringConverter stringConverter) {
        boolean strictMatching = !comboBox.isEditable();
        // has to be editable
        comboBox.setEditable(true);
        // fix the popup location
        MacOSXPopupLocationFix.install(comboBox);

        // configure the text component=editor component
        JTextComponent editorComponent = (JTextComponent)comboBox.getEditor().getEditorComponent();
        final AbstractAutoCompleteAdaptor adaptor = new CustomComboBoxAdaptor(comboBox);
        final AutoCompleteDocument document = new AutoCompleteDocument(adaptor,
                                                                       strictMatching,
                                                                       stringConverter);
        decorate(editorComponent, document, adaptor);

        //remove old key listener
        removeKeyListener(editorComponent);

        // show the popup list when the user presses a key
        final KeyListener keyListener = new CustomAutoCompleteKeyAdapter() {
            public void keyPressed(KeyEvent keyEvent) {
                // don't popup on action keys (cursor movements, etc...)
                if (keyEvent.isActionKey()) {
                    return;
                }
                // don't popup if the combobox isn't visible anyway
                if (comboBox.isDisplayable() && !comboBox.isPopupVisible()) {
                    int keyCode = keyEvent.getKeyCode();
                    // don't popup when the user hits shift,ctrl or alt
                    if (keyCode == KeyEvent.VK_SHIFT || keyCode == KeyEvent.VK_CONTROL
                        || keyCode == KeyEvent.VK_ALT) {
                        return;
                    }
                    // don't popup when the user hits escape (see issue #311)
                    if (keyCode == KeyEvent.VK_ESCAPE) {
                        return;
                    }
                    comboBox.setPopupVisible(true);
                }
            }
        };
        editorComponent.addKeyListener(keyListener);

        if (stringConverter != ObjectToStringConverter.DEFAULT_IMPLEMENTATION) {
            comboBox.setEditor(new AutoCompleteComboBoxEditor(comboBox.getEditor(), stringConverter));
        }

        //remove old property change listener
        removePropertyChangeListener(comboBox);

        // Changing the l&f can change the combobox' editor which in turn
        // would not be autocompletion-enabled. The new editor needs to be set-up.
        comboBox.addPropertyChangeListener("editor", new AutoCompletePropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent e) {
                ComboBoxEditor editor = (ComboBoxEditor)e.getOldValue();
                if (editor != null && editor.getEditorComponent() != null) {
                    removeKeyListener(editor.getEditorComponent());
                }

                editor = (ComboBoxEditor)e.getNewValue();
                if (editor != null && editor.getEditorComponent() != null) {
                    if (!(editor instanceof AutoCompleteComboBoxEditor)
                        && stringConverter != ObjectToStringConverter.DEFAULT_IMPLEMENTATION) {
                        comboBox.setEditor(new AutoCompleteComboBoxEditor(editor, stringConverter));
                        // Don't do the decorate step here because calling setEditor will trigger
                        // the propertychange listener a second time, which will do the decorate
                        // and addKeyListener step.
                    }
                    else {
                        decorate((JTextComponent)editor.getEditorComponent(), document, adaptor);
                        editor.getEditorComponent().addKeyListener(keyListener);
                    }
                }
            }
        });
    }


    static class CustomAutoCompleteFocusAdapter extends FocusAdapter {
    }
    static class CustomAutoCompleteKeyAdapter extends KeyAdapter {
    }
    static class CustomComboBoxAdaptor extends ComboBoxAdaptor {
        private JComboBox comboBox;
        CustomComboBoxAdaptor(JComboBox comboBox) {
            super(comboBox);
            this.comboBox = comboBox;
        }


        @Override
        public void setSelectedItem(Object item) {
            comboBox.setSelectedItem(item);
        }
    }
}
