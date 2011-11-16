package net.codjo.mad.gui.request.action;
import net.codjo.gui.toolkit.util.ErrorDialog;
import net.codjo.mad.client.request.FieldsList;
import net.codjo.mad.client.request.Request;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.client.request.Result;
import net.codjo.mad.client.request.Row;
import net.codjo.mad.gui.framework.AbstractGuiAction;
import net.codjo.mad.gui.framework.GuiContext;
import net.codjo.mad.gui.request.Preference;
import net.codjo.mad.gui.request.RequestTable;
import net.codjo.mad.gui.request.factory.RequestFactory;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.apache.log4j.Logger;

public class DeleteAction extends AbstractGuiAction {
    private static final Logger APP = Logger.getLogger(DeleteAction.class);
    private String confirmMessage = "Etes-vous sûr(e) de vouloir supprimer ?";
    private RequestTable table;


    public DeleteAction(GuiContext ctxt, RequestTable table) {
        super(ctxt, "Supprimer", "Supprimer les enregistrements sélectionnés", "mad.delete");
        this.table = table;
        setEnabled(false);
        table.getSelectionModel().addListSelectionListener(new TableSelectionListener());
    }


    public void setConfirmMessage(String newConfirmMsg) {
        this.confirmMessage = newConfirmMsg;
    }


    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled & isActivable());
    }


    public void actionPerformed(ActionEvent event) {
        try {
            if (confirmMessage != null) {
                int selectedOption =
                      JOptionPane.showConfirmDialog(getDesktopPane(), confirmMessage,
                                                    "Confirmation de suppression", JOptionPane.YES_NO_OPTION);
                if (selectedOption != JOptionPane.YES_OPTION) {
                    return;
                }
            }

            displayWaitCursor();
            table.cancelAllEditors();

            if (table.isEditable()) {
                modifiableTableAction();
            }
            else {
                sendDeleteRequest();
            }
        }
        catch (Exception ex) {
            APP.error("Erreur interne", ex);
            ErrorDialog.show(table, "erreur interne", ex);
        }
        finally {
            displayDefaultCursor();
        }
    }


    protected void modifiableTableAction() throws RequestException {
        Row[] rows = table.getAllSelectedDataRows();
        for (Row row : rows) {
            table.getDataSource().removeRow(row);
        }
    }


    @Override
    protected String getSecurityFunction() {
        if ((table == null)
            || (table.getPreference() == null)
            || (table.getPreference().getDelete() == null)) {
            return super.getSecurityFunction();
        }
        return getPreference().getDelete().getId();
    }


    @Override
    protected boolean isActivable() {
        if (table == null) {
            return false;
        }
        ListSelectionModel lsm = table.getSelectionModel();
        return !lsm.isSelectionEmpty() && getPreference().getDelete() != null;
    }


    private Preference getPreference() {
        return table.getPreference();
    }


    protected FieldsList getSelectedRowPkValues(Row row) {
        FieldsList fields = new FieldsList();
        Result result = table.getLoadResult();
        for (int i = 0; i < result.getPrimaryKeyCount(); i++) {
            String pk = result.getPrimaryKey(i);
            fields.addField(pk, row.getFieldValue(pk));
        }

        return fields;
    }


    protected Request buildDeleteRowRequest(Row row) {
        RequestFactory factory = table.getPreference().getDelete();
        factory.init(getSelectedRowPkValues(row));
        return factory.buildRequest(null);
    }


    protected void sendDeleteRequest() throws RequestException {
        Row[] rows = table.getAllSelectedDataRows();

        List<Request> list = new ArrayList<Request>();
        for (Row row : rows) {
            Request[] requests = getRequestsForRow(row);
            list.addAll(Arrays.asList(requests));
        }
        Request[] array = list.toArray(new Request[list.size()]);

        try {
            getGuiContext().getSender().getConnectionOperations().sendRequests(array);
        } catch (RequestException e) {
            ErrorDialog.show(table, "Impossible d'effacer la ligne", e.getMessage());
            return;
        }
        table.load();
    }


    protected RequestTable getTable() {
        return table;
    }


    protected Request[] getRequestsForRow(Row row) {
        return new Request[]{buildDeleteRowRequest(row)};
    }


    private class TableSelectionListener implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent event) {
            if (event.getValueIsAdjusting()) {
                return;
            }
            setEnabled(true);
        }
    }
}
