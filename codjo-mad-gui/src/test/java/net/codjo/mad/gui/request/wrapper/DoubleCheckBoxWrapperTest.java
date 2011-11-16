package net.codjo.mad.gui.request.wrapper;
import net.codjo.gui.toolkit.DoubleCheckBoxField;
/**
 *
 */
public class DoubleCheckBoxWrapperTest extends AbstractWrapperTest {
    private DoubleCheckBoxField comp;
    private GuiWrapper wrapper;


    public DoubleCheckBoxWrapperTest(String str) {
        super(str);
    }


    public void test_wrapp() throws Exception {
        comp.setSelected(true);
        assertEquals("true", wrapper.getXmlValue());

        comp.setSelected(false);
        assertEquals("false", wrapper.getXmlValue());
    }


    @Override
    public void test_wrapper_manage_null_value() throws Exception {
        GuiWrapper wrapper1 = getWrapper();
        wrapper1.setXmlValue("null");
        assertEquals(false, comp.isSelected());
    }


    public void test_getDisplayValue() throws Exception {
        comp.setSelected(true);
        assertEquals("true", wrapper.getDisplayValue());

        comp.setSelected(false);
        assertEquals("false", wrapper.getDisplayValue());
    }


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        comp = new DoubleCheckBoxField();
        wrapper = new DoubleCheckBoxWrapper("dbField", comp);
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
