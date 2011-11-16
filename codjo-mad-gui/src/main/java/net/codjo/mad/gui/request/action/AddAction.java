package net.codjo.mad.gui.request.action;
import net.codjo.mad.client.request.Row;
import net.codjo.mad.gui.framework.GuiContext;
import net.codjo.mad.gui.request.DetailDataSource;
import net.codjo.mad.gui.request.RequestTable;

public class AddAction extends AbstractWindowAction {
    public AddAction(GuiContext ctxt, RequestTable table, DetailWindowBuilder builder) {
        super(ctxt, table, builder, "Ajout", "Ajouter un enregistrement", "mad.add");
        setEnabled(true);
    }


    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled & isActivable());
    }


    @Override
    protected String getSecurityFunction() {
        if ((getTable() == null)
            || (getPreference() == null)
            || (getPreference().getInsert() == null)) {
            return super.getSecurityFunction();
        }
        return getPreference().getInsert().getId();
    }


    @Override
    protected void modifiableTableAction() throws Exception {
        getDataSource().addRow(buildRow());
        getTable().scrollToLastRow();
    }


    @Override
    protected DetailDataSource newDetailDataSource() {
        DetailDataSource source = new DetailDataSource(getGuiContext());
        source.setSaveFactory(getPreference().getInsert());
        return source;
    }


    @Override
    protected boolean isActivable() {
        if (!getTable().isEditable()
            && getPreference().getDetailWindowClass() != null
            && getPreference().getInsert() != null) {
            return true;
        }
        else if (getTable().isEditable()) {
            return true;
        }
        return false;
    }


    private Row buildRow() {
        Row row = getDataSource().newRow();
        if (getDataSource().getColumns() != null) {
            for (int i = 0; i < getDataSource().getColumns().length; i++) {
                if (!row.contains(getDataSource().getColumns()[i])) {
                    row.addField(getDataSource().getColumns()[i], "null");
                }
            }
        }
        return row;
    }
}
