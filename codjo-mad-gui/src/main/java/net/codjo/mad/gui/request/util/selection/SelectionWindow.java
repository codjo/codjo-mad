package net.codjo.mad.gui.request.util.selection;
import net.codjo.gui.toolkit.util.ErrorDialog;
import net.codjo.gui.toolkit.util.GuiUtil;
import net.codjo.gui.toolkit.util.Modal;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.gui.framework.GuiContext;
import net.codjo.mad.gui.request.JoinKeys;
import net.codjo.mad.gui.request.ListDataSource;
import net.codjo.mad.gui.request.RequestTable;
import net.codjo.mad.gui.request.RowFiller;
import net.codjo.mad.gui.request.StandardRowFiller;
import net.codjo.mad.gui.request.util.ButtonPanelLogic;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
/**
 *
 */
public class SelectionWindow extends JInternalFrame {
    private SelectionGui selectionGui;
    private ButtonPanelLogic buttonPanelLogic = new ButtonPanelLogic();
    private GuiContext guiContext;
    private JInternalFrame parentFrame;
    protected static final String BUTTON_NAME_PREFIX = "SelectionWindow.";


    public SelectionWindow(String title,
                           RequestTable fromTable, RequestTable toTable,
                           String labelFrom, String labelTo,
                           String borderTitle,
                           GuiContext guiContext,
                           JoinKeys joinKeys,
                           JInternalFrame parentFrame) {
        this(title, fromTable, toTable, labelFrom, labelTo, borderTitle, guiContext,
             new StandardRowFiller(toTable.getPreference()), joinKeys, parentFrame);
    }


    public SelectionWindow(String title,
                           RequestTable fromTable, RequestTable toTable,
                           String labelFrom, String labelTo,
                           String borderTitle,
                           GuiContext guiContext,
                           RowFiller rowFiller,
                           JoinKeys joinKeys,
                           JInternalFrame parentFrame) {
        super(title, true, true, true, true);
        this.guiContext = guiContext;
        this.parentFrame = parentFrame;
        fromTable.getDataSource().setGuiContext(guiContext);
        toTable.getDataSource().setGuiContext(guiContext);
        selectionGui = new SelectionGui(fromTable, toTable);
        selectionGui.setFromLabel(labelFrom);
        selectionGui.setToLabel(labelTo);

        jbInit(toTable.getDataSource(), rowFiller, borderTitle, joinKeys);
    }


    private void jbInit(ListDataSource dataSource,
                        RowFiller rowFiller,
                        String borderTitle,
                        JoinKeys joinKeys) {
        JPanel northPanel = new JPanel(new GridBagLayout());

        if (borderTitle != null) {
            selectionGui.setBorder(
                  new TitledBorder(BorderFactory.createEtchedBorder(Color.white,
                                                                    new Color(134, 134, 134)),
                                   borderTitle));
        }

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(northPanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanelLogic.getGui(), BorderLayout.SOUTH);
        mainPanel.add(selectionGui, BorderLayout.CENTER);
        setContentPane(mainPanel);

        setMinimumSize(new Dimension(200, 100));
        setPreferredSize(new Dimension(800, 600));

        buttonPanelLogic.setMainDataSource(dataSource);

        try {
            selectionGui.getFromTable().load();
            selectionGui.getToTable().load();
        }
        catch (RequestException e) {
            ErrorDialog.show(this, "Erreur au chargement des données", e);
        }

        SelectionLogic logic = new SelectionLogic(selectionGui, joinKeys, rowFiller);
        logic.start();

        buttonPanelLogic.showUndoRedo(true);
        buttonPanelLogic.getGui().removeArchiveButton();
        buttonPanelLogic.getGui().removeWhatsNewButton();

        JButton okButton = buttonPanelLogic.getGui().getOkButton();
        okButton.setName(BUTTON_NAME_PREFIX + okButton.getName());

        JButton cancelButton = buttonPanelLogic.getGui().getCancelButton();
        cancelButton.setName(BUTTON_NAME_PREFIX + cancelButton.getName());
    }


    public void displayWindow() {
        try {
            guiContext.getDesktopPane().add(this);
            setFrameIcon(UIManager.getIcon("icon"));
            pack();
            GuiUtil.centerWindow(this);
            addModalStuff();
            setVisible(true);
            setSelected(true);
        }
        catch (Exception ex) {
            ErrorDialog.show(getDesktopPane(), "Impossible d'afficher la liste", ex);
        }
    }


    private void addModalStuff() {
        if (parentFrame != null) {
            Modal.applyModality(parentFrame, this);
        }
    }


    public void setFromLabel(String newLabel) {
        selectionGui.setFromLabel(newLabel);
    }


    public void setToLabel(String newLabel) {
        selectionGui.setToLabel(newLabel);
    }


    public SelectionGui getSelectionGui() {
        return selectionGui;
    }


    public ButtonPanelLogic getButtonPanelLogic() {
        return buttonPanelLogic;
    }
}
