package net.codjo.mad.gui.request.requetor;
import net.codjo.gui.toolkit.util.ErrorDialog;
import net.codjo.mad.gui.framework.AbstractGuiAction;
import net.codjo.mad.gui.framework.GuiContext;
import net.codjo.mad.gui.request.RequestTable;
import net.codjo.mad.gui.request.factory.RequestFactory;
import java.awt.event.ActionEvent;
import org.apache.log4j.Logger;

public class ClearAction extends AbstractGuiAction {
    private static final Logger APP = Logger.getLogger(ClearAction.class);
    private RequestTable table;


    public ClearAction(GuiContext ctxt, RequestTable table) {
        super(ctxt, "Effacer", "Annuler les critères de recherche", "mad.clear");
        this.table = table;
        setEnabled(table.getPreference().getRequetor() != null);
    }


    public void actionPerformed(ActionEvent event) {
        RequestFactory factory = table.getPreference().getSelectAll();
        table.getDataSource().setLoadFactory(factory);
        try {
            displayWaitCursor();
            table.load();
        }
        catch (Exception ex) {
            APP.error("Erreur interne", ex);
            ErrorDialog.show(table, "Erreur interne", ex);
        }
        finally {
            displayDefaultCursor();
        }
    }
}
