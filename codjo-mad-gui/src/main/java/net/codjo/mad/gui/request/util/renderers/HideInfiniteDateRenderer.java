package net.codjo.mad.gui.request.util.renderers;
import net.codjo.mad.gui.request.RequestTable;
import java.awt.Component;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
/**
 * Classe de renderer qui n'affiche pas les dates dont la valeur est 31/12/9999 (correspond à l'infinie,
 * utiles pour éviter la gestion des dates nulles).
 */
public class HideInfiniteDateRenderer implements TableCellRenderer {
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected,
                                                   boolean hasFocus,
                                                   int row,
                                                   int column) {
        return getRendererComponent(value, table, row, column, isSelected, hasFocus);
    }


    private Component getRendererComponent(Object value, JTable table, int row,
                                           int column, boolean isSelected, boolean hasFocus) {
        RequestTable requestTable = ((RequestTable)table);
        int prefenceColumn = requestTable.convertColumnIndexToModel(column);
        String pattern = extractPattern(requestTable, prefenceColumn);
        Object displayValue = value;

        DateFormat dateFormat = new SimpleDateFormat(pattern);
        try {
            Date date = dateFormat.parse(value.toString());
            if (date != null) {
                GregorianCalendar calendar =
                      new GregorianCalendar(9999, Calendar.DECEMBER, 31);
                if (calendar.getTime().equals(date)) {
                    displayValue = "";
                }
            }
        }
        catch (ParseException e) {
            ;
        }

        return new DefaultTableCellRenderer().getTableCellRendererComponent(table,
                                                                            displayValue,
                                                                            isSelected,
                                                                            hasFocus,
                                                                            row,
                                                                            column);
    }


    protected String extractPattern(RequestTable requestTable, int prefenceColumn) {
        String pattern = requestTable.getPreference().getColumns().get(prefenceColumn).getFormat();
        String formatType = "date(";
        return pattern.substring(formatType.length(), pattern.lastIndexOf(")"));
    }
}
