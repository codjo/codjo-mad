package net.codjo.mad.gui.request.wrapper;
import net.codjo.gui.toolkit.date.DateField;
import net.codjo.gui.toolkit.date.NoNullDateField;
import java.awt.Color;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.UIManager;
import javax.swing.text.JTextComponent;
import junit.framework.TestCase;

public class GuiWrapperFactoryTest extends TestCase {
    public GuiWrapperFactoryTest(String str) {
        super(str);
    }


    public void test_component_color_JCheckBox() throws Exception {
        JCheckBox comp = new JCheckBox();

        comp.setEnabled(false);

        GuiWrapperFactory.wrapp("dbfield", comp);

        assertEquals(UIManager.getColor("Panel.background"), comp.getBackground());
        assertEquals(Color.darkGray, comp.getForeground());

        comp.setEnabled(true);

        assertEquals(UIManager.getColor("CheckBox.background"), comp.getBackground());
        assertEquals(UIManager.getColor("CheckBox.foreground"), comp.getForeground());
    }


    public void test_component_color_JCheckBox_fg()
          throws Exception {
        JCheckBox comp = new JCheckBox();
        comp.setEnabled(false);
        comp.setForeground(Color.pink);

        GuiWrapperFactory.wrapp("dbfield", comp);

        assertEquals(UIManager.getColor("Panel.background"), comp.getBackground());
        assertEquals(Color.pink, comp.getForeground());

        comp.setEnabled(true);

        assertEquals(UIManager.getColor("CheckBox.background"), comp.getBackground());
        assertEquals(Color.pink, comp.getForeground());
    }


    public void test_component_color_JToggleButton() throws Exception {
        JToggleButton comp = new JToggleButton();

        comp.setEnabled(false);

        GuiWrapperFactory.wrapp("dbfield", comp);

        assertEquals(UIManager.getColor("ToggleButton.background"), comp.getBackground());
        assertEquals(Color.darkGray, comp.getForeground());

        comp.setEnabled(true);

        assertEquals(UIManager.getColor("ToggleButton.background"), comp.getBackground());
        assertEquals(UIManager.getColor("ToggleButton.foreground"), comp.getForeground());
    }


    public void test_component_color_JToggleButton_fg() throws Exception {
        JToggleButton comp = new JToggleButton();
        comp.setForeground(Color.cyan);

        comp.setEnabled(false);

        GuiWrapperFactory.wrapp("dbfield", comp);

        assertEquals(UIManager.getColor("ToggleButton.background"), comp.getBackground());
        assertEquals(Color.cyan, comp.getForeground());

        comp.setEnabled(true);

        assertEquals(UIManager.getColor("ToggleButton.background"), comp.getBackground());
        assertEquals(Color.cyan, comp.getForeground());
    }


    public void test_component_color_JComboBox() throws Exception {
        JComboBox comp = new JComboBox();

        comp.setEnabled(false);

        GuiWrapperFactory.wrapp("dbfield", comp);

        //Couleur du combo non change par le wrapperFactory
        assertFalse(Color.darkGray.equals(comp.getForeground()));

        comp.setEnabled(true);
    }


    public void test_component_color_JTextComponent_Editable()
          throws Exception {
        JTextComponent comp = new JTextArea();

        comp.setEditable(false);

        GuiWrapperFactory.wrapp("dbfield", comp);

        assertEquals(UIManager.getColor("Panel.background"), comp.getBackground());
        assertEquals(Color.darkGray, comp.getForeground());

        comp.setEditable(true);

        assertEquals(UIManager.getColor("TextField.background"), comp.getBackground());
        assertEquals(UIManager.getColor("TextField.foreground"), comp.getForeground());
    }


    public void test_component_color_JTextComponent()
          throws Exception {
        JTextComponent comp = new JTextField();

        comp.setEnabled(false);

        GuiWrapperFactory.wrapp("dbfield", comp);

        assertEquals(UIManager.getColor("Panel.background"), comp.getBackground());
        assertEquals(Color.darkGray, comp.getForeground());

        comp.setEnabled(true);

        assertEquals(UIManager.getColor("TextField.background"), comp.getBackground());
        assertEquals(UIManager.getColor("TextField.foreground"), comp.getForeground());
    }


    public void test_wrapp_JCheckBox() throws Exception {
        JCheckBox comp = new JCheckBox();
        comp.setSelected(false);

        GuiWrapper wrapper = GuiWrapperFactory.wrapp("dbfield", comp);

        assertEquals("false", wrapper.getXmlValue());

        wrapper.setXmlValue("true");
        assertEquals(true, comp.isSelected());
    }

    public void test_wrapp_JCheckBox_subClass() throws Exception {
        JCheckBox comp = new JCheckBox() {
        };
        GuiWrapper wrapper = GuiWrapperFactory.wrapp("dbfield", comp);
        assertNotNull(wrapper);
    }


    public void test_wrapp_JTextField() throws Exception {
        JTextComponent comp = new JTextField();
        comp.setText("un test");

        GuiWrapper wrapper = GuiWrapperFactory.wrapp("dbfield", comp);

        assertEquals("un test", wrapper.getXmlValue());

        wrapper.setXmlValue("toto");
        assertEquals("toto", comp.getText());
    }


    public void test_wrapp_DateField() throws Exception {
        DateField component = new DateField();
        Date date = new GregorianCalendar(2002, GregorianCalendar.DECEMBER, 6).getTime();
        component.setDate(date);

        GuiWrapper wrapper = GuiWrapperFactory.wrapp("dbfield", component);

        assertEquals("2002-12-06", wrapper.getXmlValue());

        wrapper.setXmlValue("2006-12-12");
        assertEquals(new GregorianCalendar(2006, GregorianCalendar.DECEMBER, 12).getTime(),
                     component.getDate());
    }


    public void test_wrapp_NoNullDateField() throws Exception {
        NoNullDateField component = new NoNullDateField();
        Date date = new GregorianCalendar(9999, GregorianCalendar.DECEMBER, 31).getTime();
        component.setDate(date);

        GuiWrapper wrapper = GuiWrapperFactory.wrapp("dbfield", component);

        assertEquals("9999-12-31", wrapper.getXmlValue());
        assertEquals("", wrapper.getDisplayValue());
    }


    public void test_wrapp_unknownComponentType()
          throws Exception {
        JSplitPane comp = new JSplitPane();

        try {
            GuiWrapperFactory.wrapp("dbfield", comp);
            fail("Un JSplitPane est un composant non supporté (voir insupportable)");
        }
        catch (UnsupportedComponentException ex) {
            ; // error
        }
    }
}
