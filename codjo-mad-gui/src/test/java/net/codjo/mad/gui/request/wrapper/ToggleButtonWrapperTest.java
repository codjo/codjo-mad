package net.codjo.mad.gui.request.wrapper;
import javax.swing.JToggleButton;

public class ToggleButtonWrapperTest extends AbstractWrapperTest {
    private JToggleButton comp;
    private GuiWrapper wrapper;


    public ToggleButtonWrapperTest(String str) {
        super(str);
    }


    @Override
    public void test_wrapper_manage_null_value() throws Exception {
        GuiWrapper wrapper1 = getWrapper();

        wrapper1.setXmlValue("null");

        assertEquals(false, comp.isSelected());
    }


    public void test_wrapp() throws Exception {
        JToggleButton comp1 = new JToggleButton();
        comp1.setSelected(false);

        GuiWrapper wrapper1 = new ToggleButtonWrapper("dbfield", comp1);
        assertEquals("false", wrapper1.getXmlValue());

        wrapper1.setXmlValue("true");
        assertEquals(true, comp1.isSelected());
    }


    public void test_getDisplayValue() throws Exception {
        JToggleButton comp1 = new JToggleButton();
        comp1.setSelected(false);

        GuiWrapper wrapper1 = new ToggleButtonWrapper("dbfield", comp1);
        assertEquals("false", wrapper1.getDisplayValue());

        comp1.setSelected(true);
        assertEquals("true", wrapper1.getDisplayValue());
    }


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        comp = new JToggleButton();
        comp.setSelected(false);
        wrapper = new ToggleButtonWrapper("dbfield", comp);
    }


    @Override
    protected GuiWrapper getWrapper() {
        return wrapper;
    }


    @Override
    protected void changeValueThroughGUI() {
        comp.setSelected(true);
    }


    @Override
    protected void changeValueThroughWrapper() {
        wrapper.setXmlValue("true");
    }
}
