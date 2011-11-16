package net.codjo.mad.gui.request.util;
import net.codjo.mad.gui.request.Column;
import net.codjo.mad.gui.request.Preference;
import net.codjo.mad.gui.request.RequestTable;
import static net.codjo.mad.gui.request.util.PreferenceRenderer.stringToNumeric;
import java.awt.Component;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import junit.framework.TestCase;
/**
 * Classe de test de {@link PreferenceRenderer}.
 */
public class PreferenceRendererTest extends TestCase {
    private RequestTable requestTable;
    private PreferenceRenderer preferenceRenderer;
    private int column;


    public void test_getTableCellRendererComponent_numeric() {
        column = 0;
        assertEquals("", getTableCellRendererComponent(""));
        assertEquals("xxx", getTableCellRendererComponent("xxx"));
        assertEquals("2 541.23", getTableCellRendererComponent("2541.23"));
    }


    public void test_getTableCellRendererComponent_date() {
        column = 1;
        assertEquals("xxx", getTableCellRendererComponent("xxx"));
        assertEquals("31/12/2005", getTableCellRendererComponent("2005-12-31"));
        assertEquals("31/12/2005", getTableCellRendererComponent("2005-12-31 23:57:38"));
    }


    public void test_getTableCellRendererComponent_boolean() {
        column = 2;
        assertEquals("pasdaccord", getTableCellRendererComponent("xxx"));

        assertEquals("daccord", getTableCellRendererComponent("yes"));
        assertEquals("daccord", getTableCellRendererComponent("1"));
        assertEquals("daccord", getTableCellRendererComponent("true"));
        assertEquals("daccord", getTableCellRendererComponent("vrai"));
    }


    public void test_getTableCellRendererComponent_timestamp() {
        column = 3;
        assertEquals("xxx", getTableCellRendererComponent("xxx"));
        assertEquals("31/12/2005 00-00-00", getTableCellRendererComponent("2005-12-31"));
        assertEquals("31/12/2005 23-57-38",
                     getTableCellRendererComponent("2005-12-31 23:57:38"));
        assertEquals("31/12/2005 23-57-38",
                     getTableCellRendererComponent("2005-12-31 23:57:38.7"));
        assertEquals("31/12/2005 12-01-00",
                     getTableCellRendererComponent("2005-12-31 12:01:00.5"));
        assertEquals("31/12/2005 00-01-00",
                     getTableCellRendererComponent("2005-12-31 00:01:00.5"));
        assertEquals("31/12/2005 03-01-00",
                     getTableCellRendererComponent("2005-12-31 03:01:00.5"));
        assertEquals("31/12/2005 11-59-00",
                     getTableCellRendererComponent("2005-12-31 11:59:00.5"));
        assertEquals("31/12/2005 12-00-00",
                     getTableCellRendererComponent("2005-12-31 12:00:00"));
    }


    public void test_getTableCellRendererComponent_OtherTimestamp() {
        column = 5;
        assertEquals("xxx", getTableCellRendererComponent("xxx"));
        assertEquals("31/12/2005 12-00-00 AM", getTableCellRendererComponent("2005-12-31"));
        assertEquals("31/12/2005 11-57-38 PM",
                     getTableCellRendererComponent("2005-12-31 23:57:38"));
        assertEquals("31/12/2005 11-57-38 PM",
                     getTableCellRendererComponent("2005-12-31 23:57:38.7"));
        assertEquals("31/12/2005 12-01-00 PM",
                     getTableCellRendererComponent("2005-12-31 12:01:00.5"));
        assertEquals("31/12/2005 12-01-00 AM",
                     getTableCellRendererComponent("2005-12-31 00:01:00.5"));
        assertEquals("31/12/2005 03-01-00 AM",
                     getTableCellRendererComponent("2005-12-31 03:01:00.5"));
        assertEquals("31/12/2005 11-59-00 AM",
                     getTableCellRendererComponent("2005-12-31 11:59:00.5"));
        assertEquals("31/12/2005 12-00-00 PM",
                     getTableCellRendererComponent("2005-12-31 12:00:00"));
    }


    public void test_getTableCellRendererComponent_specificDate() {
        column = 4;
        assertEquals("xxx", getTableCellRendererComponent("xxx"));

        assertEquals("31/12/2005", getTableCellRendererComponent("2005-12-31"));
        assertEquals("", getTableCellRendererComponent("9999-12-31 00:00:00"));
    }


    public void test_stringToNumeric() throws Exception {

        assertEquals("-1 540.10462413668716127",
                     stringToNumeric("-1540.10462413668716127000", "#,##0.####################"));

        assertEquals("-1 540.10462413668716127000",
                     stringToNumeric("-1540.10462413668716127000", "#,##0.00000000000000000000"));

        assertEquals("-1 540.10462413668716127895",
                     stringToNumeric("-1540.10462413668716127895", "#,##0.####################"));
    }


    private String getTableCellRendererComponent(String value) {
        Component component =
              preferenceRenderer.getTableCellRendererComponent(requestTable, value, true,
                                                               true, 0, column);
        return ((JLabel)component).getText();
    }


    @Override
    protected void setUp() throws Exception {
        requestTable = new RequestTable();
        Preference preference = new Preference();
        List<Column> columns = new ArrayList<Column>();
        createColumn(columns, "testNumber", "Numeric(#,##0.00)");
        createColumn(columns, "testDate", "date(dd/MM/yyyy)");
        createColumn(columns, "testBoolean", "boolean(daccord,pasdaccord)");
        createColumn(columns, "testTimestamp", "timestamp(dd/MM/yyyy HH-mm-ss)");
        createColumn(columns, "testSpecificDate", "date(dd/MM/yyyy)",
                     "net.codjo.mad.gui.request.util.PreferenceRendererTest$SpecificDateRenderer");
        createColumn(columns, "testTimestamp", "timestamp(dd/MM/yyyy hh-mm-ss a)");
        preference.setColumns(columns);
        requestTable.setPreference(preference);
        preferenceRenderer = new PreferenceRenderer();
    }


    private void createColumn(List<Column> columns, final String fieldName, final String format) {
        Column columnNumber = new Column(fieldName, fieldName);
        columnNumber.setFormat(format);
        columns.add(columnNumber);
    }


    private void createColumn(List<Column> columns, final String fieldName, final String format,
                              final String renderer) {
        Column columnNumber = new Column(fieldName, fieldName);
        columnNumber.setFormat(format);
        columnNumber.setRenderer(renderer);
        columns.add(columnNumber);
    }


    public static class SpecificDateRenderer implements TableCellRenderer {
        public Component getTableCellRendererComponent(JTable table,
                                                       Object value,
                                                       boolean isSelected,
                                                       boolean hasFocus,
                                                       int row,
                                                       int column) {
            String pattern = "dd/MM/yyyy";
            DateFormat dateFormat = new SimpleDateFormat(pattern);
            try {
                Date date = dateFormat.parse(value.toString());
                if (date != null) {
                    GregorianCalendar calendar =
                          new GregorianCalendar(9999, Calendar.DECEMBER, 31);
                    if (calendar.getTime().equals(date)) {
                        return new JLabel();
                    }
                    table.getCellRenderer(row, column);
                    return new DefaultTableCellRenderer().getTableCellRendererComponent(table,
                                                                                        value, isSelected,
                                                                                        hasFocus, row,
                                                                                        column);
                }
            }
            catch (ParseException e) {
                ;
            }
            return new JLabel((String)value);
        }
    }
}
