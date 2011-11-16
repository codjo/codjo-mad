package net.codjo.mad.gui.util;
import net.codjo.mad.gui.request.RequestTable;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.table.TableCellRenderer;
/**
 *
 */
public class DefaultRenderValueFunctor implements RenderValueFunctor {
    public Object getRenderedValue(RequestTable jTable, int row, int col) {
        int columnIndexToView = jTable.convertColumnIndexToView(col);
        TableCellRenderer renderer = jTable.getCellRenderer(row, columnIndexToView);
        Object value = jTable.getValueAt(row, col);

        if (renderer != null) {
            Component component = renderer
                  .getTableCellRendererComponent(jTable, value, false, false, row, col);
            if (component instanceof JLabel) {
                value = ((JLabel)component).getText();
            }
        }

        return value;
    }
}
