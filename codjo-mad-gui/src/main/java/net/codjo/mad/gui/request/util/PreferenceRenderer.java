package net.codjo.mad.gui.request.util;
import net.codjo.mad.gui.request.Column;
import net.codjo.mad.gui.request.RequestTable;
import java.awt.Component;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.math.BigDecimal;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
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
 * @version $Revision: 1.8 $
 */
public class PreferenceRenderer implements ListCellRenderer, TableCellRenderer {
    private DefaultListCellRenderer listCellRenderer = new DefaultListCellRenderer();
    private DefaultTableCellRenderer tableCellRenderer = new DefaultTableCellRenderer();


    public Component getListCellRendererComponent(JList list, Object value, int index,
                                                  boolean isSelected, boolean cellHasFocus) {
        Object translated = translateComboValue(value);
        listCellRenderer.getListCellRendererComponent(list, translated, index,
                                                      isSelected, cellHasFocus);
        return listCellRenderer;
    }


    public Component getTableCellRendererComponent(JTable table,
                                                   Object value,
                                                   boolean isSelected,
                                                   boolean hasFocus,
                                                   int row,
                                                   int column) {
        Class classType = String.class;

        RequestTable requestTable = ((RequestTable)table);

        int modelIndex = requestTable.convertColumnIndexToModel(column);

        List columns = requestTable.getPreference().getColumns();
        if (modelIndex >= columns.size()) {
            return new JLabel("-");
        }

        String format = ((Column)columns.get(modelIndex)).getFormat();

        if (format != null) {
            format = format.trim();
            if (isType(format, "numeric(")) {
                classType = Number.class;
                format = determineFormat(format, "numeric(");
            }
            if (isType(format, "date(")) {
                classType = Date.class;
                format = determineFormat(format, "date(");
            }
            if (isType(format, "timestamp(")) {
                classType = Timestamp.class;
                format = determineFormat(format, "timestamp(");
            }
            if (isType(format, "boolean(")) {
                classType = Boolean.class;
                format = determineFormat(format, "boolean(");
            }
            if ("string".equalsIgnoreCase(format)) {
                classType = String.class;
                format = null;
            }
        }

        Object translated = renderValue(value, classType, format);

        String renderer = ((Column)columns.get(modelIndex)).getRenderer();
        if (renderer != null) {
            try {
                return getRenderedComponent(renderer, table, translated, isSelected,
                                            hasFocus, row, column);
            }
            catch (InstantiationException e) {
                ;
            }

            tableCellRenderer.getTableCellRendererComponent(table, translated,
                                                            isSelected, hasFocus, row, column);
            return tableCellRenderer;
        }
        tableCellRenderer.getTableCellRendererComponent(table, translated, isSelected,
                                                        hasFocus, row, column);
        return tableCellRenderer;
    }


    private Component getRenderedComponent(String renderer,
                                           JTable table,
                                           Object translated,
                                           boolean isSelected,
                                           boolean hasFocus,
                                           int row,
                                           int column) throws InstantiationException {
        try {
            TableCellRenderer usedRenderer;
            Class clazz = Class.forName(renderer);
            usedRenderer = (TableCellRenderer)clazz.newInstance();
            return usedRenderer.getTableCellRendererComponent(table, translated,
                                                              isSelected, hasFocus, row, column);
        }
        catch (IllegalAccessException e) {
            throw new InstantiationException(e.getMessage());
        }
        catch (ClassNotFoundException e) {
            throw new InstantiationException(e.getMessage());
        }
    }


    private String determineFormat(String format, String type) {
        return format.substring(type.length(), format.lastIndexOf(")"));
    }


    private boolean isType(String format, String type) {
        return format.toLowerCase().startsWith(type) && format.endsWith(")");
    }


    private Object translateComboValue(Object value) {
        if ("null".equals(value)) {
            return " ";
        }
        else {
            return value;
        }
    }


    private Object renderValue(Object value, Class classType, String format) {
        if (value == null || "null".equals(value) || value.toString().trim().length() == 0) {
            return "";
        }

        String stringValue = value.toString();
        tableCellRenderer.setHorizontalAlignment(SwingConstants.LEFT);
        if (classType.equals(Number.class)) {
            tableCellRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
            if (format != null
                && format.trim().length() > 0
                && stringValue.trim().length() > 0) {
                return stringToNumeric(stringValue, format);
            }
        }
        if (classType.equals(Date.class)) {
            return stringToDate(stringValue, format);
        }
        if (classType.equals(Timestamp.class)) {
            return stringToTimeStamp(stringValue, format);
        }
        if (classType.equals(Boolean.class)) {
            return stringToBoolean(stringValue, format);
        }

        return value;
    }


    private String stringToBoolean(String stringValue, String format) {
        StringTokenizer stringTokenizer = new StringTokenizer(format, ";,");
        String forTrue = null;
        String forFalse = null;
        if (stringTokenizer.hasMoreElements()) {
            forTrue = (String)stringTokenizer.nextElement();
        }
        if (stringTokenizer.hasMoreElements()) {
            forFalse = (String)stringTokenizer.nextElement();
        }
        if (forTrue != null && forFalse != null) {
            if ("{yes}{1}{true}{vrai}".contains("{" + stringValue.toLowerCase() + "}")) {
                return forTrue;
            }
            else {
                return forFalse;
            }
        }
        else {
            return stringValue;
        }
    }


    private String stringToDate(String stringValue, String format) {
        SimpleDateFormat dateFormatInput = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat dateFormatOutput = new SimpleDateFormat(format);
        try {
            return dateFormatOutput.format(dateFormatInput.parse(stringValue));
        }
        catch (ParseException e) {
            return stringValue;
        }
    }


    private String stringToTimeStamp(String stringValue, String format) {
        SimpleDateFormat dateFormatInput = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dateParsed;
        try {
            dateParsed = dateFormatInput.parse(stringValue);
        }
        catch (ParseException e) {
            dateFormatInput = new SimpleDateFormat("yyyy-MM-dd");
            try {
                dateParsed = dateFormatInput.parse(stringValue);
            }
            catch (ParseException e1) {
                return stringValue;
            }
        }
        SimpleDateFormat dateFormatOutput = new SimpleDateFormat(format);
        return dateFormatOutput.format(dateParsed);
    }


    /**
     * exemple de formatage monétaire : "#,##0.00##################"
     */
    public static String stringToNumeric(String stringValue, String format) {
        DecimalFormatSymbols decimalFormatSymbols =
              new DecimalFormatSymbols(Locale.FRENCH);
        decimalFormatSymbols.setDecimalSeparator('.');
        decimalFormatSymbols.setGroupingSeparator(' ');

        DecimalFormat decimalFormat = new DecimalFormat(format, decimalFormatSymbols);
        decimalFormat.setParseBigDecimal(true);

        try {
            return decimalFormat.format(new BigDecimal(stringValue));
        }
        catch (Exception e) {
            return stringValue;
        }
    }
}
