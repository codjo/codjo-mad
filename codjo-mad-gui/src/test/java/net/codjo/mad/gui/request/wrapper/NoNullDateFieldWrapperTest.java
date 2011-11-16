package net.codjo.mad.gui.request.wrapper;
import net.codjo.gui.toolkit.date.NoNullDateField;
import java.util.Date;
/**
 *
 */
public class NoNullDateFieldWrapperTest extends AbstractWrapperTest {
    private NoNullDateField comp;
    private GuiWrapper wrapper;


    @Override
    public void test_wrapper_manage_null_value() throws Exception {
        wrapper.setXmlValue("null");
        assertEquals("9999-12-31", wrapper.getXmlValue());
    }


    public void test_getDisplayValue() throws Exception {
        comp.setDate(NoNullDateField.DEFAULT_NO_NULL_DATE);
        assertEquals("", wrapper.getDisplayValue());
    }


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        comp = new NoNullDateField();
        wrapper = new NoNullDateFieldWrapper("dbfield", comp);
    }


    @Override
    protected GuiWrapper getWrapper() {
        return wrapper;
    }


    @Override
    protected void changeValueThroughGUI() {
        comp.setDate(new Date(50000000));
    }


    @Override
    protected void changeValueThroughWrapper() {
        wrapper.setXmlValue("2002-05-31");
    }
}
