package net.codjo.mad.gui.request.util.selection;
import net.codjo.mad.gui.request.RequestTable;
import net.codjo.gui.toolkit.util.ErrorDialog;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.apache.log4j.Logger;
/**
 * Action permettant de transferer une ligne de 'from' vers 'to'.
 *
 * @version $Revision: 1.6 $
 */
public class SelectRowAction extends AbstractAction {
    private static final Logger APP = Logger.getLogger(SelectRowAction.class);

    private RequestTable to;
    private RequestTable from;

    public SelectRowAction(RequestTable fromTable, RequestTable toTable) {
        super(">>");
        from = fromTable;
        to = toTable;
        setEnabled(false);
    }

    public void actionPerformed(ActionEvent event) {
        try {
            for (int i = 0; i < from.getAllSelectedDataRows().length; i++) {
                to.getDataSource().addRow(from.getAllSelectedDataRows()[i]);
            }
        }
        catch (Exception ex) {
            APP.error(ex.getMessage(), ex);
            ErrorDialog.show(null, ex.getMessage(), ex);
        }
        setEnabled(false);
        from.repaint();
    }
}
