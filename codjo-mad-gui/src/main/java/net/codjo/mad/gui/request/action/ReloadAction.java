package net.codjo.mad.gui.request.action;
import net.codjo.gui.toolkit.util.ErrorDialog;
import net.codjo.mad.gui.framework.AbstractGuiAction;
import net.codjo.mad.gui.framework.GuiContext;
import net.codjo.mad.gui.request.RequestTable;
import java.awt.event.ActionEvent;
import org.apache.log4j.Logger;

public class ReloadAction extends AbstractGuiAction {
    private static final Logger APP = Logger.getLogger(ReloadAction.class);
    private RequestTable table;


    public ReloadAction(GuiContext ctxt, RequestTable table) {
        super(ctxt, "Rafraîchir", "Rafraîchir la liste", "mad.reload");
        this.table = table;
        setEnabled(true);
    }


    public void actionPerformed(ActionEvent event) {
        try {
            displayWaitCursor();
            table.load();
        }
        catch (Exception ex) {
            APP.error("Erreur interne", ex);
            ErrorDialog.show(table, "erreur interne", ex);
        }
        finally {
            displayDefaultCursor();
        }
    }


    @Override
    protected String getSecurityFunction() {
        if ((table == null)
            || (table.getPreference() == null)
            || (table.getPreference().getSelectAll() == null)) {
            return super.getSecurityFunction();
        }
        return table.getPreference().getSelectAll().getId();
    }
}
