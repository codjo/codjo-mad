package net.codjo.mad.gui.request;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.plaf.metal.MetalLookAndFeel;
import net.codjo.mad.client.request.FieldsList;
import net.codjo.mad.client.request.MadServerFixture;
import net.codjo.mad.client.request.RequestException;
import org.uispec4j.Key;
import org.uispec4j.ListBox;
import org.uispec4j.TextBox;
import org.uispec4j.Trigger;
import org.uispec4j.UISpecTestCase;
import org.uispec4j.Window;
import org.uispec4j.interception.WindowHandler;
import org.uispec4j.interception.WindowInterceptor;
/**
 *
 */
public class RequestTextFieldAutoCompleterTest extends UISpecTestCase {
    private static final String[] COLUMNS_NAMES = new String[]{"aCode", "aLabel"};

    private JTextField textField = new JTextField();
    private TextBox textBox = new TextBox(textField);
    private MadServerFixture fixture = new MadServerFixture();


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        fixture.doSetUp();

        fixture.mockServerResult(COLUMNS_NAMES,
                                 new String[][]{
                                       {"ptf1", "portefeuille1"},
                                       {"ptf2", "portefeuille2"},
                                       {"ptf3", "portefeuille3"},
                                       {"ptf4", "portefeuille4"},
                                       {"ptf5", "portefeuille5"},
                                 });

        UIManager.setLookAndFeel(new MetalLookAndFeel());
    }


    @Override
    protected void tearDown() throws Exception {
        fixture.doTearDown();
        super.tearDown();
    }


    public void test_nominal() throws Exception {
        final RequestTextFieldAutoCompleter autoCompleter = createAutoCompleter();

        WindowInterceptor.init(new Trigger() {
            public void run() throws Exception {
                textBox.pressKey(Key.E);
            }
        }).process(new WindowHandler() {
            @Override
            public Trigger process(Window window) throws Exception {
                assertEquals("E", textBox.getText());
                assertAutoCompleteList(window, 5,
                                       new String[]{"portefeuille1",
                                                    "portefeuille2",
                                                    "portefeuille3",
                                                    "portefeuille4",
                                                    "portefeuille5"});

                WindowInterceptor.init(new Trigger() {
                    public void run() throws Exception {
                        textBox.pressKey(Key.d3);
                    }
                }).process(new WindowHandler() {
                    @Override
                    public Trigger process(Window window) throws Exception {
                        assertAutoCompleteList(window, 1, new String[]{"portefeuille3"});

                        window.getListBox().selectIndex(0);
                        textBox.pressKey(Key.ENTER);
                        assertEquals("portefeuille3", textBox.getText());
                        assertEquals("ptf3", autoCompleter.getSelectedCode());

                        return Trigger.DO_NOTHING;
                    }
                }).run();

                return Trigger.DO_NOTHING;
            }
        }).run();
    }


    public void test_selector() throws Exception {
        final RequestTextFieldAutoCompleter autoCompleter =
              new RequestTextFieldAutoCompleter(textField, "aHandler", "aCode",
                                                "aLabel", new FieldsList("source", "pipo"));
        autoCompleter.load();

        fixture.assertSentRequests("<requests>"
                                   + "  <select request_id='1'>"
                                   + "    <id>aHandler</id>"
                                   + "    <selector>"
                                   + "      <field name='source'>"
                                   + "         pipo"
                                   + "      </field>"
                                   + "    </selector>"
                                   + "    <attributes>"
                                   + "      <name>aCode</name>"
                                   + "      <name>aLabel</name>"
                                   + "    </attributes>"
                                   + "    <page num='1' rows='100000'/>"
                                   + "  </select>"
                                   + "</requests>");
    }


    private RequestTextFieldAutoCompleter createAutoCompleter() throws RequestException {
        final RequestTextFieldAutoCompleter autoCompleter =
              new RequestTextFieldAutoCompleter(textField, "aHandler", "aCode", "aLabel");
        autoCompleter.load();

        final JFrame frame = new JFrame();
        frame.add(textField);

        WindowInterceptor.run(new Trigger() {
            public void run() throws Exception {

                frame.setVisible(true);
            }
        });

        return autoCompleter;
    }


    private void assertAutoCompleteList(Window selectedWindow, int listSize, String[] listElements) {
        ListBox listBox = selectedWindow.getListBox();
        assertEquals(listSize, listBox.getSize());
        assertTrue(listBox.contentEquals(listElements));
    }
}
