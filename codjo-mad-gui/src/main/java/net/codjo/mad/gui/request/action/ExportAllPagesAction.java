package net.codjo.mad.gui.request.action;
import net.codjo.mad.gui.framework.GuiContext;
import net.codjo.mad.gui.request.RequestTable;

public class ExportAllPagesAction extends SpreadsheetExportAction {

    public ExportAllPagesAction(GuiContext ctxt, RequestTable... table) {
        super(ctxt, table);
        putValue(SHORT_DESCRIPTION, "Exporter toutes les pages");
    }


    @Override
    protected String getSecurityFunction() {
        if (tables == null
            || 0 == tables.length
            || null == tables[0].getPreference()
            || null == tables[0].getPreference().getSelectAll()) {
            return super.getSecurityFunction();
        }
        return tables[0].getPreference().getSelectAll().getId();
    }


    @Override
    protected boolean exportAllPages() {
        return true;
    }
}
