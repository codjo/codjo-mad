package net.codjo.mad.gui.request.wrapper;
import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.UIManager;
import javax.swing.text.JTextComponent;
import net.codjo.gui.toolkit.DoubleCheckBoxField;
import net.codjo.gui.toolkit.date.DateField;
import net.codjo.gui.toolkit.date.NoNullDateField;
import net.codjo.gui.toolkit.date.TimestampDateField;
import net.codjo.gui.toolkit.number.NumberField;
import net.codjo.gui.toolkit.path.FilePathField;
import net.codjo.mad.gui.request.RequestTextFieldAutoCompleter;
/**
 * Factory de <code>net.codjo.mad.gui.request.wrapper.GuiWrapper</code>.
 *
 * @author $Author: gaudefr $
 * @version $Revision: 1.7 $
 * @see net.codjo.mad.gui.request.wrapper.GuiWrapper
 */
public class GuiWrapperFactory {
    protected static final String COLOR_FAMILY = "GuiWrapperFactory.COLOR";
    private static final Color UNMODIFY_FOREGROUND_COLOR = Color.darkGray;
    private static final PropertyChangeListener COLOR_UPDATER =
          new PropertyChangeListener() {
              public void propertyChange(PropertyChangeEvent evt) {
                  updateComponentColor((JComponent)evt.getSource());
              }
          };
    private static GuiWrapperFactory singleton = new GuiWrapperFactory();
    private static final String TEXT_FIELD = "TextField";


    protected GuiWrapperFactory() {
    }


    public static void setGuiWrapperFactoryImpl(GuiWrapperFactory impl) {
        singleton = impl;
    }


    /**
     * Encapsule un composant non graphique.
     *
     * @param fieldName Le nom du champ.
     *
     * @return un wrapper
     */
    public static GuiWrapper wrapp(String fieldName) {
        return new TextComponentWrapper(fieldName, new JTextField());
    }


    /**
     * Encapsule un composant graphique.
     *
     * @param fieldName Le nom du champ.
     * @param component le composant a encapsuler
     *
     * @return un wrapper
     *
     * @throws UnsupportedComponentException Composant graphique non gere.
     */
    public static GuiWrapper wrapp(String fieldName, final JComponent component)
          throws UnsupportedComponentException {
        GuiWrapper wrapper = getGuiWrapperFactoryImpl().wrappImpl(fieldName, component);

        if (wrapper == null) {
            throw new UnsupportedComponentException(component.getClass().toString());
        }

        if (component.getClientProperty(COLOR_FAMILY) != null) {
            updateComponentColor(component);

            component.addPropertyChangeListener("enabled", COLOR_UPDATER);
            component.addPropertyChangeListener("editable", COLOR_UPDATER);
        }
        return wrapper;
    }


    /**
     * Encapsule un composant graphique.
     *
     * @param fieldName Le nom du champ.
     * @param component le composant a encapsuler
     *
     * @return un wrapper (ou null si non gere)
     */
    protected GuiWrapper wrappImpl(final String fieldName, final JComponent component) {
        if (component instanceof NumberField) {
            component.putClientProperty(COLOR_FAMILY, TEXT_FIELD);
            return new NumberFieldWrapper(fieldName, (NumberField)component);
        }
        if (component instanceof JCheckBox) {
            component.putClientProperty(COLOR_FAMILY, "CheckBox");
            return new ToggleButtonWrapper(fieldName, (JCheckBox)component);
        }
        if (component instanceof JToggleButton) {
            component.putClientProperty(COLOR_FAMILY, "ToggleButton");
            return new ToggleButtonWrapper(fieldName, (JToggleButton)component);
        }
        if (component instanceof DoubleCheckBoxField) {
            component.putClientProperty(COLOR_FAMILY, "Panel");
            return new DoubleCheckBoxWrapper(fieldName, (DoubleCheckBoxField)component);
        }
        if (component instanceof JComboBox) {
//            component.putClientProperty(COLOR_FAMILY, "ComboBox");
            return new ComboBoxWrapper(fieldName, (JComboBox)component);
        }
        if (component instanceof NoNullDateField) {
            component.putClientProperty(COLOR_FAMILY, TEXT_FIELD);
            return new NoNullDateFieldWrapper(fieldName, (NoNullDateField)component);
        }
        if (component instanceof DateField) {
            component.putClientProperty(COLOR_FAMILY, TEXT_FIELD);
            return new DateFieldWrapper(fieldName, (DateField)component);
        }
        if (component instanceof TimestampDateField) {
            component.putClientProperty(COLOR_FAMILY, TEXT_FIELD);
            return new TimestampDateFieldWrapper(fieldName, (TimestampDateField)component);
        }
        if (component instanceof JTextComponent) {
            component.putClientProperty(COLOR_FAMILY, TEXT_FIELD);
            return new TextComponentWrapper(fieldName, (JTextComponent)component);
        }
        if (component instanceof FilePathField) {
            component.putClientProperty(COLOR_FAMILY, TEXT_FIELD);
            return new TextComponentWrapper(fieldName, ((FilePathField)component).getFileNameField());
        }
        if (component instanceof RequestTextFieldAutoCompleter) {
            return new RequestTextFieldAutoCompleterWrapper(fieldName, (RequestTextFieldAutoCompleter)component);
        }
        return null;
    }


    private static GuiWrapperFactory getGuiWrapperFactoryImpl() {
        return singleton;
    }


    private static void updateComponentColor(JComponent component) {
        Object colorFamily = component.getClientProperty(COLOR_FAMILY);

        boolean isModify = component.isEnabled();
        if (component instanceof JTextComponent) {
            JTextComponent textComponent = ((JTextComponent)component);
            isModify = isModify && textComponent.isEditable();
        }

        // Gestion Couleur
        if (isModify) {
            component.setBackground(UIManager.getColor(colorFamily + ".background"));
            if (component.getForeground() == UNMODIFY_FOREGROUND_COLOR) {
                component.setForeground(UIManager.getColor(colorFamily + ".foreground"));
            }
        }
        else {
            component.setBackground(UIManager.getColor("Panel.background"));
            if (component.getForeground().equals(UIManager.getColor(colorFamily + ".foreground"))) {
                component.setForeground(UNMODIFY_FOREGROUND_COLOR);
            }
        }
    }
}
