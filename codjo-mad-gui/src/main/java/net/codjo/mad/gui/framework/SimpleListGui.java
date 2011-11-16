package net.codjo.mad.gui.framework;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.gui.request.Preference;
import net.codjo.mad.gui.request.RequestTable;
import net.codjo.mad.gui.request.RequestToolBar;
import java.awt.Dimension;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
/**
 * Fenêtre contenant simplement une table et une barre d'outil standard (requeteur, boutons de navigation...).
 * {@link AbstractAction, AbstractListLogic}
 */
public class SimpleListGui extends JInternalFrame {
    private static final Dimension DEFAULT_DIMENSION = new Dimension(700, 500);
    private SimpleListPanel simpleListPanel;
    private Dimension dimension;


    public SimpleListGui(String title) {
        this(title, DEFAULT_DIMENSION);
    }


    public SimpleListGui(String title, Dimension dimension) {
        super(title, true, true, true, true);
        this.dimension = dimension;
        buildGui();
    }


    protected void buildGui() {
        simpleListPanel = new SimpleListPanel();
        add(simpleListPanel);
        setPreferredSize(dimension);
    }


    public void init(GuiContext guiContext, Preference preference) {
        simpleListPanel.init(guiContext, preference);
    }


    public void load() throws RequestException {
        simpleListPanel.load();
    }


    public JPanel getHeaderPanel() {
        return simpleListPanel.getHeaderPanel();
    }


    public RequestTable getTable() {
        return simpleListPanel.getTable();
    }


    public RequestToolBar getToolBar() {
        return simpleListPanel.getToolBar();
    }
}
