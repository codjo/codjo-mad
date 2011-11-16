package net.codjo.mad.gui.request.util;
import net.codjo.gui.toolkit.table.TableRendererSorter;
import net.codjo.mad.gui.request.Column;
import net.codjo.mad.gui.request.Preference;
import net.codjo.mad.gui.request.RequestTable;
import net.codjo.mad.gui.request.util.comparators.MadComparator;
import java.awt.Component;
import java.util.List;
import javax.swing.table.TableCellRenderer;
/**
 *
 */
public class RequestTableRendererSorter extends TableRendererSorter {

    public RequestTableRendererSorter(final RequestTable requestTable) {
        super(requestTable);
        customizeComparators();
    }


    @Override
    protected boolean isSortableValue(TableCellRenderer cellRenderer, Object value, int row, int column) {
        boolean sortableValue = super.isSortableValue(cellRenderer, value, row, column);
        if (!PreferenceRenderer.class.isInstance(cellRenderer)) {
            return sortableValue;
        }
        Component rendererComponent =
              cellRenderer.getTableCellRendererComponent(table, value, false, false, row, column);
        if (!TableCellRenderer.class.isInstance(rendererComponent)) {
            return sortableValue;
        }
        return sortableValue
               || super.isSortableValue((TableCellRenderer)rendererComponent, value, row, column);
    }


    private void customizeComparators() {
        Preference preference = ((RequestTable)table).getPreference();
        if (preference != null) {
            List<Column> columns = preference.getColumns();
            for (int columnIndex = 0; columnIndex < columns.size(); columnIndex++) {
                Column column = columns.get(columnIndex);
                columnsToComparator.put(columnIndex, MadComparator.newInstance(column.getSorter()));
            }
        }
    }
}
