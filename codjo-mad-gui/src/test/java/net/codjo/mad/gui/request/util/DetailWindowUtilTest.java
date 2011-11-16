package net.codjo.mad.gui.request.util;
import net.codjo.mad.client.request.RequestSender;
import net.codjo.mad.gui.framework.DefaultGuiContext;
import net.codjo.mad.gui.request.DetailDataSource;
import net.codjo.mad.gui.request.FieldType;
import net.codjo.mad.gui.request.wrapper.GuiWrapper;
import net.codjo.security.common.api.UserMock;
import java.util.Map;
import javax.swing.JCheckBox;
import javax.swing.JInternalFrame;
import javax.swing.JTextField;
import junit.framework.TestCase;
/**
 */
public class DetailWindowUtilTest extends TestCase {
    private DefaultGuiContext guiContext;


    /**
     * Verifie que le collecteur declare correctement les champs publique.
     *
     * @throws Exception
     */
    public void test_declarePublicFields() throws Exception {
        MyPanel panel = new MyPanel();
        panel.sicovamCode = new JTextField();

        DetailDataSource datasource =
              new DetailDataSource(guiContext, new RequestSender(), null, null, null);

        DetailWindowUtil.declarePublicFields(datasource, panel);

        Map<String, GuiWrapper> publicFields = datasource.getDeclaredFields();

        assertTrue(publicFields.containsKey("pimsCode"));
        assertNotNull(publicFields.get("pimsCode"));

        assertTrue(publicFields.containsKey("sicovamCode"));
        assertNotNull(publicFields.get("sicovamCode"));

        assertTrue(publicFields.containsKey("boolField"));
        assertNotNull(publicFields.get("boolField"));

        assertEquals(3, publicFields.size());
    }


    public void test_getPublicFieldsValue_error()
          throws Exception {
        try {
            DetailWindowUtil.getPublicFields(null);
            fail("Pointeur null");
        }
        catch (NullPointerException ex) {
            ; //
        }
    }


    /**
     * Verifie que le collecteur recupere seulement les champs publique.
     *
     * @throws Exception
     */
    public void test_publicFields() throws Exception {
        MyPanel panel = new MyPanel();
        Map publicFields = DetailWindowUtil.getPublicFields(panel);

        assertTrue(publicFields.containsKey("pimsCode"));
        assertEquals(panel.pimsCode, publicFields.get("pimsCode"));

        assertTrue(publicFields.containsKey("sicovamCode"));
        assertEquals(panel.sicovamCode, publicFields.get("sicovamCode"));

        assertTrue(publicFields.containsKey("boolField"));
        assertEquals(panel.boolField, publicFields.get("boolField"));

        assertEquals(3, publicFields.size());
    }


    public void test_manageEditModeFields() throws Exception {
        MyPanel panel = new MyPanel();
        panel.getPrivateData().putClientProperty(FieldType.EDIT_MODE,
                                                 FieldType.NOT_EDITABLE);

        DetailDataSource datasource =
              new DetailDataSource(guiContext, new RequestSender(), null, null, null);
        datasource.declare("privateData", panel.getPrivateData());

        DetailWindowUtil.manageEditModeFields(datasource);

        assertFalse(panel.getPrivateData().isEnabled());
    }


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        guiContext = new DefaultGuiContext();
        guiContext.setUser(new UserMock().mockIsAllowedTo(true));
    }


    @SuppressWarnings({"PublicField"})
    public static class MyPanel extends JInternalFrame {
        public static final int STATIC_DATA = 5;
        public JCheckBox boolField = new JCheckBox();
        public String data = null;
        public JTextField pimsCode = new JTextField();
        public JTextField sicovamCode = null;
        JTextField packageProtectedData = new JTextField();
        private JTextField privateData = new JTextField();


        public JTextField getPrivateData() {
            return privateData;
        }
    }
}
