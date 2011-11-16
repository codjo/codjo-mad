package net.codjo.mad.gui.request.action;
import net.codjo.gui.toolkit.util.ErrorDialog;
import net.codjo.mad.client.plugin.MadConnectionOperations;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.gui.framework.GuiContext;
import net.codjo.mad.gui.request.RequestTable;
import net.codjo.mad.gui.request.util.MultiRequestsHelper;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.UIManager;
import org.apache.log4j.Logger;

public class SubmitAction extends AbstractAction {
    private static final Logger LOG = Logger.getLogger(SubmitAction.class);
    private final GuiContext context;
    private final RequestTable requestTable;


    public SubmitAction(GuiContext ctxt, RequestTable requestTable) {
        super("Sauvegarder", UIManager.getIcon("mad.save"));
        this.context = ctxt;
        this.requestTable = requestTable;
    }


    public void actionPerformed(ActionEvent e) {
        MadConnectionOperations operations = context.getSender().getConnectionOperations();
        MultiRequestsHelper mrh = new MultiRequestsHelper(operations);
        requestTable.getDataSource().addSaveRequestTo(mrh);
        try {
            mrh.sendRequest();
        }
        catch (RequestException ex) {
            LOG.error(ex.getMessage(), ex);
            ErrorDialog.show(context.getMainFrame(), ex.getMessage(), ex);
        }
    }
}
