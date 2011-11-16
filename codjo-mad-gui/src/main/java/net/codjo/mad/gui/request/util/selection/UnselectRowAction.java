package net.codjo.mad.gui.request.util.selection;
import net.codjo.mad.client.request.Row;
import net.codjo.mad.gui.request.RequestTable;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
/**
 * DOCUMENT ME!
 *
 * @version $Revision: 1.5 $
 */
public class UnselectRowAction extends AbstractAction {
    private RequestTable to;
    private RequestTable from;


    public UnselectRowAction(RequestTable to, RequestTable from) {
        super("<<");
        this.to = to;
        this.from = from;
        this.setEnabled(false);
    }


    public void actionPerformed(ActionEvent event) {
        if (to.getDataSource().getSelectedRow() != null) {
            Row[] rows = to.getAllSelectedDataRows();
            for (Row row : rows) {
                to.getDataSource().removeRow(row);
            }
        }
        this.setEnabled(false);
        refreshSelectionTableRows(from);
        from.repaint();
    }


    private void refreshSelectionTableRows(RequestTable table) {
        int[] selectedRows = table.getSelectedRows();
        int rowCount = selectedRows.length;
        if (rowCount > 0) {
            table.getDataSource().setSelectedRow(null);
            table.setRowSelectionInterval(selectedRows[0], selectedRows[rowCount - 1]);
        }
    }
}
