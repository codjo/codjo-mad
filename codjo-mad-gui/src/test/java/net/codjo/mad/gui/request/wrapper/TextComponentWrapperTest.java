package net.codjo.mad.gui.request.wrapper;
import javax.swing.JTextField;
/**
 *
 */
public class TextComponentWrapperTest extends AbstractWrapperTest {
    private JTextField comp;
    private GuiWrapper wrapper;


    @Override
    public void test_wrapper_manage_null_value() throws Exception {
        super.test_wrapper_manage_null_value();

        assertEquals("", comp.getText());
    }


    public void test_wrapp_event() throws Exception {
        FakeChangeListener listener = new FakeChangeListener();
        comp = new JTextField();
        comp.setText("Valeur A");

        wrapper = new TextComponentWrapper("dbfield", comp);
        wrapper.addPropertyChangeListener(listener);

        wrapper.setXmlValue("Valeur B");

        assertEquals(1, listener.nbOfCall);
        assertEquals("Valeur A", listener.evt.getOldValue());
        assertEquals("Valeur B", listener.evt.getNewValue());
    }


    public void test_wrapp() throws Exception {
        comp = new JTextField();
        comp.setText("un test");

        wrapper = new TextComponentWrapper("dbfield", comp);

        assertEquals("un test", wrapper.getXmlValue());

        wrapper.setXmlValue("toto");
        assertEquals("toto", comp.getText());
    }


    public void test_getDisplayValue() throws Exception {
        comp = new JTextField();
        comp.setText("un test");

        wrapper = new TextComponentWrapper("dbfield", comp);

        assertEquals("un test", wrapper.getDisplayValue());

        wrapper.setXmlValue(AbstractWrapper.NULL);
        assertEquals("", comp.getText());
    }


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        comp = new JTextField();
        wrapper = new TextComponentWrapper("dbfield", comp);
    }


    @Override
    protected GuiWrapper getWrapper() {
        return wrapper;
    }


    @Override
    protected void changeValueThroughGUI() {
        comp.setText("true");
    }


    @Override
    protected void changeValueThroughWrapper() {
        wrapper.setXmlValue("true");
    }
}
