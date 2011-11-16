package net.codjo.mad.gui.request.wrapper;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
/**
 *
 */
public class ComboBoxWrapperTest extends AbstractWrapperTest {
    private Integer[] modelValue = new Integer[]{5, 6};
    private JComboBox comp;
    private GuiWrapper wrapper;


    public ComboBoxWrapperTest(String str) {
        super(str);
    }


    public void test_setXmlValue_NULL_in_model() throws Exception {
        comp = new JComboBox(new String[]{"null", "other"});
        wrapper = new ComboBoxWrapper("toto", comp);

        wrapper.setXmlValue("null");
        assertEquals("null", wrapper.getXmlValue());
        assertEquals(0, comp.getSelectedIndex());
    }


    public void test_setXmlValue_editable() throws Exception {
        comp.setEditable(true);
        wrapper.setXmlValue("unknownValue");
        assertEquals("unknownValue", wrapper.getXmlValue());
    }


    public void test_setXmlValue_unknownValue() throws Exception {
        try {
            wrapper.setXmlValue("987sdflkj");
            fail("Valeur non présente dans le modèle");
        }
        catch (IllegalArgumentException ex) {
            ; // erreur
        }
    }


    public void test_wrapp() throws Exception {
        comp.setSelectedItem(6);

        assertEquals("6", wrapper.getXmlValue());

        wrapper.setXmlValue("5");

        assertEquals(5, comp.getSelectedItem());
    }


    public void test_getDisplayValue_aJLabelRenderer() throws Exception {
        comp = new JComboBox(new String[]{"1", "2", "3"});
        comp.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list,
                                                          Object value,
                                                          int index,
                                                          boolean isSelected,
                                                          boolean cellHasFocus) {
                if ("1".equals(value)) {
                    setText("one");
                }
                else if ("2".equals(value)) {
                    setText("two");
                }
                else if ("3".equals(value)) {
                    setText("three");
                }
                return this;
            }
        });
        wrapper = new ComboBoxWrapper("toto", comp);

        wrapper.setXmlValue("2");
        assertEquals("2", wrapper.getXmlValue());
        assertEquals("two", wrapper.getDisplayValue());
    }


    public void test_getDisplayValue_noRenderer() throws Exception {
        comp = new JComboBox(new String[]{"1", "2", "3"});
        wrapper = new ComboBoxWrapper("toto", comp);

        wrapper.setXmlValue("2");
        assertEquals("2", wrapper.getXmlValue());
        assertEquals("2", wrapper.getDisplayValue());
    }


    public void test_getDisplayValue_editableComboBox() throws Exception {
        comp.setEditable(true);
        wrapper.setXmlValue("unknownValue");
        assertEquals("unknownValue", wrapper.getXmlValue());
        assertEquals("unknownValue", wrapper.getDisplayValue());
    }


    public void test_getDisplayValue_noJLabelRendererThrowsAnException() throws Exception {
        comp = new JComboBox(new String[]{"1", "2", "3"});
        comp.setRenderer(new ListCellRenderer() {
            public Component getListCellRendererComponent(JList list,
                                                          Object value,
                                                          int index,
                                                          boolean isSelected,
                                                          boolean cellHasFocus) {
                return new JPanel();
            }
        });
        wrapper = new ComboBoxWrapper("toto", comp);

        wrapper.setXmlValue("2");
        assertEquals("2", wrapper.getXmlValue());

        try {
            wrapper.getDisplayValue();
            fail();
        }
        catch (Exception e) {
            assertEquals("Impossible de renvoyer la valeur affichée par le renderer.", e.getMessage());
        }
    }


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        comp = new JComboBox(modelValue);
        wrapper = new ComboBoxWrapper("dbfield", comp);
    }


    @Override
    protected GuiWrapper getWrapper() {
        return wrapper;
    }


    @Override
    protected void changeValueThroughGUI() {
        comp.setSelectedItem(6);
    }


    @Override
    protected void changeValueThroughWrapper() {
        wrapper.setXmlValue("6");
    }
}
