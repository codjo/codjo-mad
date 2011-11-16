package net.codjo.mad.gui.request.action;
import net.codjo.gui.toolkit.util.ErrorDialog;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.gui.framework.AbstractGuiAction;
import net.codjo.mad.gui.framework.GuiContext;
import net.codjo.mad.gui.request.RequestTable;
import net.codjo.mad.gui.request.event.DataSourceAdapter;
import net.codjo.mad.gui.request.event.DataSourceEvent;
import java.awt.event.ActionEvent;
import org.apache.log4j.Logger;

public class NextPageAction extends AbstractGuiAction {
    private static final Logger APP = Logger.getLogger(NextPageAction.class);
    private RequestTable table;


    public NextPageAction(GuiContext ctxt, RequestTable table) {
        super(ctxt, "Suivant", "Afficher la page suivante", "mad.next");
        this.table = table;
        table.getDataSource().addDataSourceListener(new DataSourceAdapter() {
            @Override
            public void loadEvent(DataSourceEvent event) {
                dataSourceLoaded();
            }
        });
        dataSourceLoaded();
    }


    public void actionPerformed(ActionEvent event) {
        try {
            displayWaitCursor();
            table.loadNextPage();
        }
        catch (RequestException ex) {
            APP.error("Erreur lors du chargement", ex);
            ErrorDialog.show(table, "erreur lors du chargement", ex);
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


    private void dataSourceLoaded() {
        setEnabled(table.hasNextPage());
    }
}
