package net.codjo.mad.gui.framework;
import net.codjo.gui.toolkit.LabelledItemPanel;
import net.codjo.gui.toolkit.util.ErrorDialog;
import net.codjo.mad.gui.request.DetailDataSource;
import net.codjo.mad.gui.request.ListDataSource;
import net.codjo.mad.gui.request.RequestComboBox;
import net.codjo.mad.gui.request.util.ButtonPanelGui;
import java.awt.BorderLayout;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
/**
 * Structure d'une fenêtre Detail.
 */
public abstract class AbstractDetailGui extends JInternalFrame {
    protected LabelledItemPanel itemPanel = new LabelledItemPanel();
    protected ButtonPanelGui buttonPanelGui = new ButtonPanelGui();
    private JDesktopPane desktopPane;


    protected AbstractDetailGui(String title) {
        super(title, true, true, true, true);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(itemPanel, BorderLayout.CENTER);
        getContentPane().add(buttonPanelGui, BorderLayout.SOUTH);

        buildAndAddItems();
    }


    @Override
    public JDesktopPane getDesktopPane() {
        return desktopPane;
    }


    public ButtonPanelGui getButtonPanelGui() {
        return buttonPanelGui;
    }


    protected abstract void buildAndAddItems();


    public abstract void declareFields(DetailDataSource dataSource);


    public abstract void switchToUpdateMode();


    public void setGuiContext(GuiContext guiContext) {
    }


    public void setFatherDataSource(ListDataSource dataSource) {
    }


    /**
     * DOCUMENT ME!
     *
     * @return new RequestComboBox
     *
     * @deprecated use RequestComboBox.createRequestComboBox
     */
    public static RequestComboBox createRequestComboBox(String code, String label,
                                                        boolean containsNullValue) {
        final RequestComboBox comboBox = new RequestComboBox();
        initRequestComboBox(comboBox, code, label, containsNullValue);
        return comboBox;
    }


    /**
     * DOCUMENT ME!
     *
     * @deprecated use not static RequestComboBox.initRequestComboBox
     */
    public static void initRequestComboBox(RequestComboBox comboBox, String code,
                                           String label, boolean containsNullValue) {
        comboBox.setModelFieldName(code);
        if (label != null) {
            comboBox.setRendererFieldName(label);
        }
        comboBox.setContainsNullValue(containsNullValue);
    }


    public void error(String errorTitle, String message) {
        ErrorDialog.show(getDesktopPane(), errorTitle, message);
    }


    public void setDesktopPane(JDesktopPane desktopPane) {
        this.desktopPane = desktopPane;
    }
}
