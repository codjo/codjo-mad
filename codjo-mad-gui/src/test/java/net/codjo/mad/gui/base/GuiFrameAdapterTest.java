package net.codjo.mad.gui.base;
import java.awt.Component;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import org.uispec4j.Trigger;
import org.uispec4j.UISpecTestCase;
import org.uispec4j.Window;
import org.uispec4j.interception.WindowInterceptor;
import org.uispec4j.utils.Utils;

public class GuiFrameAdapterTest extends UISpecTestCase {
    private static final String TITRE = "titre";
    private JInternalFrame internalFrame;
    private boolean called;


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        called = false;
    }


    public void test_openJFrame() throws Exception {
        Component component = load();
        assertTrue(component instanceof JFrame);
        assertEquals(TITRE, ((JFrame)component).getTitle());
    }


    public void test_windowClosed() throws Exception {
        JFrame jFrame = (JFrame)load();
        internalFrame.addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosed(InternalFrameEvent event) {
                calledByListener();
            }
        });
        jFrame.dispose();

        Utils.waitForPendingAwtEventsToBeProcessed();

        assertTrue(called);
    }


    private Component load() {
        GuiFrameAdapter adapter = new GuiFrameAdapter();
        internalFrame = new JInternalFrame(TITRE);
        adapter.add(internalFrame);

        Window window = WindowInterceptor.run(new Trigger() {
            public void run() throws Exception {
                internalFrame.setVisible(true);
            }
        });

        return window.getAwtComponent();
    }


    private void calledByListener() {
        called = true;
    }
}
