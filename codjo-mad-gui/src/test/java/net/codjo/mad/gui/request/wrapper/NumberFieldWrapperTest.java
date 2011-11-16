package net.codjo.mad.gui.request.wrapper;
import net.codjo.gui.toolkit.number.NumberField;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import javax.swing.SwingUtilities;
/**
 *
 */
public class NumberFieldWrapperTest extends AbstractWrapperTest {
    private NumberField comp;
    private NumberFieldWrapper wrapper;


    public NumberFieldWrapperTest(String str) {
        super(str);
    }


    public void test_wrapp() throws Exception {
        comp.setNumber(new BigDecimal("125.3"));
        assertEquals("125.3", wrapper.getXmlValue());

        synchWithEventThread();

        wrapper.setXmlValue("18.666");
        assertEquals("18.666", comp.getText());
    }


    public void test_getDisplayValue() throws Exception {
        comp.setNumber(new BigDecimal("125.3"));
        assertEquals("125.3", wrapper.getDisplayValue());

        wrapper.setXmlValue(AbstractWrapper.NULL);
        assertEquals("", comp.getText());
    }


    @Override
    public void test_wrapper_manage_null_value() throws Exception {
        super.test_wrapper_manage_null_value();
        assertEquals("", comp.getText());
    }


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        comp = new NumberField();
        wrapper = new NumberFieldWrapper("dbField", comp);
    }


    @Override
    protected GuiWrapper getWrapper() {
        return wrapper;
    }


    @Override
    protected void changeValueThroughGUI() {
        comp.setNumber(new BigDecimal("12.3"));
        try {
            synchWithEventThread();
        }
        catch (Exception ex) {
            ;
        }
    }


    @Override
    protected void changeValueThroughWrapper() {
        wrapper.setXmlValue("18.5");
    }


    private void synchWithEventThread()
          throws InterruptedException, InvocationTargetException {
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
            }
        });
    }
}
