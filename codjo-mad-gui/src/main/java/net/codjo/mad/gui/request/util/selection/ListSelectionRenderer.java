package net.codjo.mad.gui.request.util.selection;
import net.codjo.mad.gui.request.util.StringRenderer;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
/**
 * Renderer mettant en gris les lignes de 'from' déjà transferé dans 'to'.
 *
 * @version $Revision: 1.6 $
 */
class ListSelectionRenderer implements TableCellRenderer {
    private TableCellRenderer tableRenderer;
    private SelectionLogic logic;


    ListSelectionRenderer(SelectionLogic logic) {
        this.logic = logic;
        tableRenderer = new StringRenderer();
    }

    ListSelectionRenderer(SelectionLogic logic, TableCellRenderer tableRenderer) {
        this.logic = logic;
        this.tableRenderer = tableRenderer;
    }


    public Component getTableCellRendererComponent(JTable table,
                                                   Object value,
                                                   boolean isSelected,
                                                   boolean hasFocus,
                                                   int row,
                                                   int column) {
        JComponent comp =
              (JComponent)tableRenderer.getTableCellRendererComponent(table, value,
                                                                      isSelected, hasFocus, row, column);

        comp.setOpaque(true);

        if (hasBeenTransfered(row)) {
            if (isSelected) {
                comp.setBackground(Color.darkGray);
                comp.setForeground(Color.lightGray);
            }
            else {
                comp.setBackground(Color.lightGray);
                comp.setForeground(Color.darkGray);
            }
        }
        else {
            if (isSelected) {
                comp.setBackground(table.getSelectionBackground());
                comp.setForeground(table.getSelectionForeground());
            }
            else {
                comp.setBackground(table.getBackground());
                comp.setForeground(table.getForeground());
            }
        }

        return comp;
    }


    private boolean hasBeenTransfered(int row) {
        return logic.hasBeenTransfered(row);
    }
}
