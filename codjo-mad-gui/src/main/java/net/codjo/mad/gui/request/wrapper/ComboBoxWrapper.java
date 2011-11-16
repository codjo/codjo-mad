package net.codjo.mad.gui.request.wrapper;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.accessibility.Accessible;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.UndoableEditListener;
import org.apache.log4j.Logger;
/**
 * Wrapper pour une JCheckBox.
 *
 * @author $Author: bernaju $
 * @version $Revision: 1.6 $
 */
public class ComboBoxWrapper extends AbstractWrapper {
    private static final Logger APP = Logger.getLogger(ComboBoxWrapper.class);
    private BasicUndoableSupport support = new BasicUndoableSupport(this);
    private JComboBox comp;


    ComboBoxWrapper(String fieldName, JComboBox comp) {
        super(fieldName, comp);
        this.comp = comp;
        ComponentChangeListener listener = new ComponentChangeListener();
        comp.addPropertyChangeListener("model", listener);
        comp.getModel().addListDataListener(listener);

        comp.getEditor().addActionListener(listener);
        comp.getEditor().getEditorComponent().addFocusListener(listener);
        initPreviousValue();
    }


    public void setXmlValue(String value) {
        for (int i = 0; i < comp.getItemCount(); i++) {
            if (value.equals(comp.getItemAt(i).toString())) {
                comp.setSelectedIndex(i);
                return;
            }
        }

        // Gestion des cas particulier, si non present dans le modele
        if (NULL.equals(value)) {
            comp.setSelectedIndex(-1);
            return;
        }

        if (comp.isEditable()) {
            comp.addItem(value);
            comp.setSelectedItem(value);
            fireChangeEvent();
            return;
        }
        APP.error("Erreur durant " + getFieldName() + ".setXmlValue(" + value + ")");
        for (int i = 0; i < comp.getItemCount(); i++) {
            APP.error("\t->" + comp.getItemAt(i).toString());
            if (value.equals(comp.getItemAt(i).toString())) {
                APP.error("\t\ttrouve mais pas trouve avant ?????");
            }
        }
        throw new IllegalArgumentException("Valeur '" + value + "' n'est "
                                           + "pas contenu dans le modèle du champs " + getFieldName());
    }


    public String getXmlValue() {
        if (comp.getSelectedIndex() == -1) {
            return NULL;
        }
        String selectedItem = comp.getSelectedItem().toString();
        return ("".equals(selectedItem) ? NULL : selectedItem);
    }


    public String getDisplayValue() {
        if (comp.getSelectedIndex() == -1) {
            return "";
        }
        ListCellRenderer cellRenderer = comp.getRenderer();
        if (cellRenderer == null) {
            return comp.getSelectedItem().toString();
        }
        else {
            return getRendererValue(comp, comp.getSelectedIndex());
        }
    }


    private String getRendererValue(JComboBox comboBox, int index) {
        Accessible popup = comboBox.getUI().getAccessibleChild(comboBox, 0);
        if (popup != null && popup instanceof javax.swing.plaf.basic.ComboPopup) {
            final JList list = ((javax.swing.plaf.basic.ComboPopup)popup).getList();
            Component renderedComponent =
                  list.getCellRenderer()
                        .getListCellRendererComponent(list,
                                                      list.getModel().getElementAt(index),
                                                      index,
                                                      list.isSelectedIndex(index), false);

            if (JLabel.class.isInstance(renderedComponent)) {
                return ((JLabel)renderedComponent).getText();
            }
        }
        throw new RuntimeException("Impossible de renvoyer la valeur affichée par le renderer.");
    }


    public void addUndoableEditListener(UndoableEditListener listener) {
        support.addUndoableEditListener(listener);
    }


    public void removeUndoableEditListener(UndoableEditListener listener) {
        support.removeUndoableEditListener(listener);
    }


    private class ComponentChangeListener extends FocusAdapter implements ListDataListener,
                                                                          PropertyChangeListener,
                                                                          ActionListener {
        public void actionPerformed(ActionEvent event) {
            updateComboFromEditor();
        }


        public void focusLost(FocusEvent event) {
            updateComboFromEditor();
        }


        public void contentsChanged(ListDataEvent event) {
            fireChangeEvent();
        }


        public void intervalAdded(ListDataEvent event) {
        }


        public void intervalRemoved(ListDataEvent event) {
        }


        public void propertyChange(PropertyChangeEvent event) {
            // Changement de modèle
            comp.getModel().addListDataListener(this);
        }


        private void updateComboFromEditor() {
            if (comp.getEditor().getItem().equals(getXmlValue())) {
                return;
            }
            setXmlValue((String)comp.getEditor().getItem());
        }
    }
}
