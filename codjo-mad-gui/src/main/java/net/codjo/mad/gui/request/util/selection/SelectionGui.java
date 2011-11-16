package net.codjo.mad.gui.request.util.selection;
import net.codjo.mad.gui.framework.GuiContext;
import net.codjo.mad.gui.framework.LocalGuiContext;
import net.codjo.mad.gui.request.RequestTable;
import net.codjo.mad.gui.request.RequestToolBar;
import net.codjo.mad.gui.request.action.NextPageAction;
import net.codjo.mad.gui.request.action.PreviousPageAction;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
/**
 * IHM de selection. Elle est composée de deux tables ("from" et "to") avec passage de lignes de l'une vers
 * l'autre.
 *
 * @version $Revision: 1.4 $
 * @see SelectionLogic
 */
public class SelectionGui extends JPanel {
    private JButton selectButton = new JButton();
    private JButton unSelectButton = new JButton();
    private RequestTable fromTable = new RequestTable();
    private RequestTable toTable = new RequestTable();
    private JLabel fromTableLabel = new JLabel();
    private JLabel toTableLabel = new JLabel();
    private RequestToolBar fromTableButtons = new RequestToolBar();
    private RequestToolBar toTableButtons = new RequestToolBar();


    public SelectionGui() {
    }


    public SelectionGui(RequestTable fromTable, RequestTable toTable) {
        this.fromTable = fromTable;
        this.toTable = toTable;
        jbInit();
    }


    public SelectionGui(GuiContext guiCtxt) {
        fromTable.getDataSource().setGuiContext(guiCtxt);
        jbInit();
    }


    public void init(RequestTable fTable, RequestTable tTable) {
        this.fromTable = fTable;
        this.toTable = tTable;
        jbInit();
    }


    public void init(GuiContext guiCtxt) {
        fromTable.getDataSource().setGuiContext(guiCtxt);
        jbInit();
    }


    public void setSelectAction(Action addAction) {
        selectButton.setAction(addAction);
    }


    public void setFromLabel(String fromLabel) {
        fromTableLabel.setText(fromLabel);
    }


    public void setUnSelectAction(Action removeAction) {
        unSelectButton.setAction(removeAction);
    }


    public void setToLabel(String fromLabel) {
        toTableLabel.setText(fromLabel);
    }


    public RequestTable getFromTable() {
        return fromTable;
    }


    public RequestTable getToTable() {
        return toTable;
    }


    public RequestToolBar getToTableButtons() {
        return toTableButtons;
    }


    public JButton getSelectButton() {
        return selectButton;
    }


    public JButton getUnSelectButton() {
        return unSelectButton;
    }


    protected void initFromToolBar(final GuiContext guiContext) {
        if (guiContext == null) {
            return;
        }
        fromTableButtons.add(new PreviousPageAction(guiContext, fromTable));
        fromTableButtons.add(new NextPageAction(guiContext, fromTable));
        fromTableButtons.init(new LocalGuiContext(guiContext), fromTable);
        fromTableButtons.addSeparator();

        toTableButtons.setHasExcelButton(true);
        toTableButtons.setHasNavigatorButton(false);
        toTableButtons.init(new LocalGuiContext(guiContext), toTable);
        toTableButtons.removeAction(RequestToolBar.ACTION_ADD);
        toTableButtons.removeAction(RequestToolBar.ACTION_EDIT);
        toTableButtons.removeAction(RequestToolBar.ACTION_RELOAD);
        toTableButtons.removeAction(RequestToolBar.ACTION_DELETE);
        toTableButtons.removeAction(RequestToolBar.ACTION_LOAD);
        toTableButtons.removeAction(RequestToolBar.ACTION_CLEAR);
        toTableButtons.removeAction(RequestToolBar.ACTION_NEXT_PAGE);
        toTableButtons.removeAction(RequestToolBar.ACTION_PREVIOUS_PAGE);
    }


    private void jbInit() {
        unSelectButton.setName("unSelectButton");
        selectButton.setName("selectButton");
        fromTableLabel.setText("...");
        toTableLabel.setText("...");
        setLayout(new GridBagLayout());

        JPanel fromTablePanel = new JPanel();
        fromTablePanel.setLayout(new BorderLayout());
        fromTablePanel.add(new JScrollPane(fromTable), BorderLayout.CENTER);
        fromTablePanel.add(fromTableButtons, BorderLayout.SOUTH);

        JPanel toTablePanel = new JPanel();
        toTablePanel.setLayout(new BorderLayout());
        toTablePanel.add(new JScrollPane(toTable), BorderLayout.CENTER);
        toTablePanel.add(toTableButtons, BorderLayout.SOUTH);

        fromTableButtons.setFloatable(false);
        toTableButtons.setFloatable(false);

        final GuiContext guiContext = fromTable.getDataSource().getGuiContext();
        initFromToolBar(guiContext);

        add(fromTableLabel,
            new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                                   GridBagConstraints.NONE, new Insets(5, 10, 0, 0), 0, 0));
        add(toTableLabel,
            new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                                   GridBagConstraints.NONE, new Insets(5, 10, 0, 0), 0, 0));
        add(selectButton,
            new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                   GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
        add(fromTablePanel,
            new GridBagConstraints(0, 3, 1, 2, 1.0, 1.0, GridBagConstraints.WEST,
                                   GridBagConstraints.BOTH, new Insets(0, 10, 10, 10), 0, 0));
        add(toTablePanel,
            new GridBagConstraints(2, 3, 1, 2, 1.0, 1.0, GridBagConstraints.WEST,
                                   GridBagConstraints.BOTH, new Insets(0, 10, 10, 10), 0, 0));
        add(unSelectButton,
            new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0, GridBagConstraints.NORTH,
                                   GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
    }
}
