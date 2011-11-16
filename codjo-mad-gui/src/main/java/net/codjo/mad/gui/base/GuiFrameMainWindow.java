package net.codjo.mad.gui.base;
import net.codjo.mad.gui.util.ApplicationData;
import java.awt.BorderLayout;
import javax.swing.JDesktopPane;
import javax.swing.JPanel;

public class GuiFrameMainWindow extends MainWindow {
    private JPanel contentPane;


    public GuiFrameMainWindow(ApplicationData applicationData, JPanel mainFrame) {
        super(applicationData);
        //noinspection InstanceVariableUsedBeforeInitialized
        contentPane.add(mainFrame, BorderLayout.CENTER);
    }


    @Override
    protected JDesktopPane createMiddleComponent(JPanel aContentPane) {
        this.contentPane = aContentPane;
        return new GuiFrameAdapter();
    }
}
