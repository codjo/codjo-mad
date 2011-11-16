package net.codjo.mad.gui.request.action;
import net.codjo.gui.toolkit.util.ErrorDialog;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.gui.framework.AbstractGuiAction;
import net.codjo.mad.gui.framework.GuiContext;
import net.codjo.mad.gui.request.RequestTable;
import net.codjo.mad.gui.util.ExportExcelBuilder;
import java.awt.event.ActionEvent;
import org.apache.log4j.Logger;

public class SpreadsheetExportAction extends AbstractGuiAction {
    protected static final Logger LOG = Logger.getLogger(SpreadsheetExportAction.class);

    protected RequestTable[] tables;
    protected ExportExcelBuilder builder;


    public SpreadsheetExportAction(GuiContext ctxt, RequestTable... list) {
        super(ctxt, "Export", "Exporter la page courante", "mad.export");
        this.tables = list;
        setEnabled(true);
    }


    protected void setBuilder(ExportExcelBuilder builder) {
        this.builder = builder;
    }


    protected void doExport(ActionEvent event) throws RequestException {
        builder.generate(exportAllPages());
    }


    protected boolean exportAllPages() {
        return false;
    }


    public final void actionPerformed(ActionEvent event) {
        try {
            displayWaitCursor();

            if (null == builder) {
                builder = new ExportExcelBuilder(getGuiContext());
                for (RequestTable table : tables) {
                    builder.add(table, ExportExcelBuilder.createColumnHeader(table));
                    LOG.debug("Exporting "+table.getName()+" with standard headers.");
                }
            }
            doExport(event);
        }
        catch (Exception ex) {
            LOG.error("Erreur d'export des données", ex);
            ErrorDialog.show(null, "Erreur d'export des données", ex);
        }
        finally {
            displayDefaultCursor();
        }
    }


    @Override
    public void setEnabled(boolean enabled) {
        boolean enableHandler = true;
        for (RequestTable aTable : tables) {
            if ((aTable != null)
                && (aTable.getPreference() != null)
                && (aTable.getPreference().getSelectAll() != null)
                && !getGuiContext().getUser().isAllowedTo(aTable.getPreference().getSelectAll().getId())) {
                enableHandler = false;
                break;
            }
        }

        super.setEnabled(enabled && enableHandler);
    }
}
