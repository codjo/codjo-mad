package net.codjo.mad.gui.request.util;
import net.codjo.mad.gui.request.Column;
import net.codjo.mad.gui.request.RequestTable;
import java.awt.Component;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
/**
 * Renderer de String.
 *
 * @author $Author: gaudefr $
 * @version $Revision: 1.5 $
 */
public class StringRenderer implements ListCellRenderer, TableCellRenderer,
    java.util.Comparator {
    private DefaultListCellRenderer listCellRenderer = new DefaultListCellRenderer();
    private DefaultTableCellRenderer tableCellRenderer = new DefaultTableCellRenderer();

    public Component getListCellRendererComponent(JList list, Object value, int index,
        boolean isSelected, boolean cellHasFocus) {
        Object translated = translateComboValue(value);
        listCellRenderer.getListCellRendererComponent(list, translated, index,
            isSelected, cellHasFocus);
        return listCellRenderer;
    }


    public Component getTableCellRendererComponent(JTable table, Object value,
        boolean isSelected, boolean hasFocus, int row, int column) {
        boolean isNumeric = table.getModel().getColumnClass(column).equals(Number.class);
        Object translated = translateValue((RequestTable)table, column, value, isNumeric);
        if (isNumeric) {
            tableCellRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        }

        else {
            tableCellRenderer.setHorizontalAlignment(SwingConstants.LEFT);
        }

        tableCellRenderer.getTableCellRendererComponent(table, translated, isSelected,
            hasFocus, row, column);

        return tableCellRenderer;
    }


    public int compare(Object o1, Object o2) {
        Comparable t1 = (Comparable)o1;
        return t1.compareTo(o2);
    }


    private Object translateComboValue(Object value) {
        if ("null".equals(value)) {
            return " ";
        }
        else {
            return value;
        }
    }


    private Object translateValue(RequestTable table, int column, Object value,
        boolean isNumeric) {
        String stringValue = value.toString();
        if ("null".equals(value)) {
            return "";
        }
        else {
            if (isNumeric) {
                return stringToNumeric(stringValue,
                    ((Column)table.getPreference().getColumns().get(column)).getFormat());
            }

            return value;
        }
    }


    public static String stringToNumeric(String stringValue, String format) {
        DecimalFormatSymbols decimalFormatSymbols =
            new DecimalFormatSymbols(Locale.FRENCH);
        decimalFormatSymbols.setDecimalSeparator('.');
        decimalFormatSymbols.setGroupingSeparator(' ');
        // exemple de formatage monétaire : "#,##0.00##################"
        DecimalFormat decimalFormat = new DecimalFormat(format, decimalFormatSymbols);

        return decimalFormat.format(Double.parseDouble(stringValue));
    }
}
