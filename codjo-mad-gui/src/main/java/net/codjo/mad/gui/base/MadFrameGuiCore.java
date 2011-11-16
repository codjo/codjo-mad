package net.codjo.mad.gui.base;
import net.codjo.gui.toolkit.util.Modal;
import net.codjo.mad.gui.request.action.ModalityService;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
/**
 *
 */
public class MadFrameGuiCore extends MadGuiCore {
    private final JPanel mainFrame;


    public MadFrameGuiCore(JPanel mainFrame) {
        this.mainFrame = mainFrame;
    }


    @Override
    protected MainWindow createMainWindow() {
        GuiFrameMainWindow frameMainWindow = new GuiFrameMainWindow(applicationData, mainFrame);
        frameMainWindow.getGuiContext().putProperty(ModalityService.class, new ModalityServiceAdapter());
        return frameMainWindow;
    }


    private static class ModalityServiceAdapter extends ModalityService {

        @Override
        public void apply(JComponent componentInFather, final JInternalFrame modalFrame) {
            JFrame root = findFirstAncestor(JFrame.class, componentInFather);
            JFrame child = findFirstAncestor(JFrame.class, modalFrame.getContentPane());
            if (root != null) {
                Modal.applyModality(root, child);
            }
        }
    }
}
