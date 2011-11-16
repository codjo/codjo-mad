package net.codjo.mad.gui.framework;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.gui.request.Preference;
import net.codjo.mad.gui.request.RequestTable;
import net.codjo.mad.gui.request.RequestToolBar;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
/**
 *
 */
public class SimpleListPanel extends JPanel {
    private final JPanel headerPanel = new JPanel();
    private final RequestTable table;
    private final RequestToolBar toolBar;


    public SimpleListPanel() {
        this(new RequestTable(), new RequestToolBar());
    }


    public SimpleListPanel(RequestTable table, RequestToolBar toolBar) {
        this.table = table;
        this.toolBar = toolBar;

        buildGui(table, toolBar);
    }


    private void buildGui(RequestTable requestTable, RequestToolBar requestToolBar) {
        setLayout(new BorderLayout());
        add(headerPanel, BorderLayout.NORTH);
        add(new JScrollPane(requestTable), BorderLayout.CENTER);
        add(requestToolBar, BorderLayout.SOUTH);
    }


    public void init(GuiContext guiContext, Preference preference) {
        table.setPreference(preference);
        toolBar.setHasExcelButton(true);
        toolBar.init(guiContext, table);
    }


    public void load() throws RequestException {
        table.load();
    }


    public JPanel getHeaderPanel() {
        return headerPanel;
    }


    public RequestTable getTable() {
        return table;
    }


    public RequestToolBar getToolBar() {
        return toolBar;
    }
}
