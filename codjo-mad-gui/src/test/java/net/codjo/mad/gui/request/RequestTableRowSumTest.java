package net.codjo.mad.gui.request;
import net.codjo.gui.toolkit.swing.NumberFieldEditor;
import net.codjo.mad.client.request.Request;
import net.codjo.mad.client.request.Row;
import net.codjo.mad.client.request.SelectRequest;
import net.codjo.mad.client.request.util.RequestTestHelper;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import junit.framework.TestCase;

public class RequestTableRowSumTest extends TestCase {
    private static final String SELECT_ALL_ID = "selectAll";
    private static final String[] COLUMNS = {"pimsCode", "isin", "amount1", "amount2"};
    private RequestTestHelper helper;
    private RequestTable requestTable;
    private RequestTableRowSum requestTableRowSum;


    public void test_load() throws Exception {
        assertEquals(5, requestTableRowSum.getRowCount());
        assertEquals(1, requestTableRowSum.getColumnCount());
        assertEquals(requestTable.getRowCount(), requestTableRowSum.getRowCount());
        assertEquals(new BigDecimal(0),
                     ((RequestTableRowSum.Sum)requestTableRowSum.getValueAt(0, 0)).getValue());
        assertNull(((RequestTableRowSum.Sum)requestTableRowSum.getValueAt(1, 0)).getValue());
        assertEquals(new BigDecimal(4.1).setScale(1, BigDecimal.ROUND_UP),
                     ((RequestTableRowSum.Sum)requestTableRowSum.getValueAt(2, 0)).getValue());
        assertEquals(new BigDecimal(6.1).setScale(1, BigDecimal.ROUND_UP),
                     ((RequestTableRowSum.Sum)requestTableRowSum.getValueAt(3, 0)).getValue());
        assertEquals(new BigDecimal(8.1).setScale(1, BigDecimal.ROUND_UP),
                     ((RequestTableRowSum.Sum)requestTableRowSum.getValueAt(4, 0)).getValue());
    }


    public void test_addRow() throws Exception {
        Row row = new Row();
        row.addField("pimsCode", "123");
        row.addField("isin", "isinVal123");
        row.addField("amount1", "10");
        row.addField("amount2", "11");
        requestTable.getDataSource().addRow(row);

        assertEquals(new BigDecimal(21).setScale(1, BigDecimal.ROUND_DOWN),
                     ((RequestTableRowSum.Sum)requestTableRowSum.getValueAt(5, 0)).getValue().setScale(1,
                                                                                                       BigDecimal.ROUND_DOWN));
    }


    public void test_removeRow() throws Exception {
        requestTable.getDataSource().removeRow(1);

        assertEquals(new BigDecimal(0),
                     ((RequestTableRowSum.Sum)requestTableRowSum.getValueAt(0, 0)).getValue());
        assertEquals(new BigDecimal(4.1).setScale(1, BigDecimal.ROUND_UP),
                     ((RequestTableRowSum.Sum)requestTableRowSum.getValueAt(1, 0)).getValue());
        assertEquals(new BigDecimal(6.1).setScale(1, BigDecimal.ROUND_UP),
                     ((RequestTableRowSum.Sum)requestTableRowSum.getValueAt(2, 0)).getValue());
        assertEquals(new BigDecimal(8.1).setScale(1, BigDecimal.ROUND_UP),
                     ((RequestTableRowSum.Sum)requestTableRowSum.getValueAt(3, 0)).getValue());
        assertEquals(4, requestTableRowSum.getRowCount());
    }


    public void test_setName() throws Exception {
        assertEquals("RequestTableName", requestTable.getName());
        assertEquals(requestTable.getName() + ".RowSum", requestTableRowSum.getName());

        requestTableRowSum.setName("myName");
        requestTableRowSum.initRequestTable(requestTable);
        assertEquals("myName", requestTableRowSum.getName());
    }


    public void test_updateRow() throws Exception {
        requestTable.getDataSource().setValue(1, "amount1", "12");
        requestTable.getDataSource().setValue(1, "amount2", "12");

        Thread.sleep(5);

        BigDecimal value = ((RequestTableRowSum.Sum)requestTableRowSum.getValueAt(1, 0)).getValue();
        assertEquals(new BigDecimal(24.0), value);
    }


    private static Column createColumn(String fieldName, int minSize, int maxSize, int preferredSize) {
        return createColumn(fieldName, minSize, maxSize, preferredSize, false);
    }


    private static Column createColumn(String fieldName,
                                       int minSize,
                                       int maxSize,
                                       int preferredSize,
                                       boolean summable) {
        Column column = new Column();
        column.setFieldName(fieldName);
        column.setLabel(fieldName);
        column.setMinSize(minSize);
        column.setMaxSize(maxSize);
        column.setPreferredSize(preferredSize);
        column.setSummable(summable);
        return column;
    }


    public static Request[] getRequestListForTest(int pageNumber, int pageSize) throws Exception {
        SelectRequest select = new SelectRequest();
        select.setId(SELECT_ALL_ID);
        select.setAttributes(buildFieldsArray(COLUMNS));
        select.setPage(pageNumber, pageSize);

        return new Request[]{select};
    }


    public static String[] buildFieldsArray(String[] columns) {
        return buildFields(columns).keySet().toArray(new String[columns.length]);
    }


    private static Map<String, Object> buildFields(String[] attributes) {
        Map<String, Object> fields = new HashMap<String, Object>();
        for (String attribute : attributes) {
            fields.put(attribute, null);
        }
        return fields;
    }


    @Override
    protected void setUp() throws Exception {
        helper = new RequestTestHelper();
        helper.setRequest(getRequestListForTest(1, ListDataSource.PAGE_SIZE));
        helper.setResult(getRequestResult(helper));
        helper.activate();

        requestTable = new RequestTable();
        requestTable.setEditable(true);
        Preference preference = new Preference();
        List<Column> list = new ArrayList<Column>();
        list.add(createColumn("pimsCode", 0, 0, 100));
        list.add(createColumn("isin", 0, 0, 100));
        list.add(createColumn("amount1", 0, 0, 100, true));
        list.add(createColumn("amount2", 0, 0, 100, true));
        preference.setColumns(list);
        preference.setSelectAllId(SELECT_ALL_ID);
        requestTable.setPreference(preference);
        requestTable.setCellEditor("amount1", new NumberFieldEditor());
        requestTable.setCellEditor("amount2", new NumberFieldEditor());
        requestTable.setName("RequestTableName");

        requestTableRowSum = new RequestTableRowSum(requestTable);
        requestTable.load();
    }


    @Override
    protected void tearDown() throws Exception {
        helper.tearDown();
    }


    private static String getRequestResult(RequestTestHelper helper) {
        return "<?xml version=\"1.0\"?>"
               + "<results>"
               + "     <result request_id=\""
               + (helper.getRequestId(0)) + "\">" + "        <primarykey>"
               + "           <field name=\"pimsCode\"/>" + "        </primarykey>"
               + "        <row>                                                   "
               + "           <field name=\"pimsCode\">666</field>                 "
               + "           <field name=\"isin\">isinVal</field>                 "
               + "           <field name=\"amount1\">0</field>                   "
               + "           <field name=\"amount2\">0</field>                   "
               + "        </row>                                                  "
               + "        <row>                                                   "
               + "           <field name=\"pimsCode\">1</field>                 "
               + "           <field name=\"isin\">isinVal1</field>                 "
               + "           <field name=\"amount1\">null</field>                   "
               + "           <field name=\"amount2\">null</field>                   "
               + "        </row>                                                  "
               + "        <row>                                                   "
               + "           <field name=\"pimsCode\">2</field>                 "
               + "           <field name=\"isin\">isinVal2</field>                 "
               + "           <field name=\"amount1\">2</field>                   "
               + "           <field name=\"amount2\">2.1</field>                   "
               + "        </row>                                                  "
               + "        <row>                                                   "
               + "           <field name=\"pimsCode\">3</field>                 "
               + "           <field name=\"isin\">isinVal3</field>                 "
               + "           <field name=\"amount1\">3</field>                   "
               + "           <field name=\"amount2\">3.1</field>                   "
               + "        </row>                                                  "
               + "        <row>                                                   "
               + "           <field name=\"pimsCode\">4</field>                 "
               + "           <field name=\"isin\">isinVal4</field>                 "
               + "           <field name=\"amount1\">4</field>                   "
               + "           <field name=\"amount2\">4.1</field>                   "
               + "        </row>                                                  "
               + "     </result>" + "</results>";
    }


    public static void main(String[] args) throws Exception {
        JFrame frame = new JFrame("Test RequestTableRowSum");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JPanel mainPanel = new JPanel(new BorderLayout());
        Container contentPane = frame.getContentPane();
        contentPane.add(mainPanel, BorderLayout.CENTER);
        JPanel tablePanel = new JPanel();
        tablePanel.setLayout(new BoxLayout(tablePanel, BoxLayout.Y_AXIS));

        RequestTestHelper helper = new RequestTestHelper();
        helper.setRequest(getRequestListForTest(1, ListDataSource.PAGE_SIZE));
        helper.setResult(getRequestResult(helper));
        helper.activate();

        RequestTable requestTable = new RequestTable();
        requestTable.setName("myRequestTable");
        requestTable.setEditable(true);
        Preference preference = new Preference();
        List<Column> list = new ArrayList<Column>();
        list.add(createColumn("pimsCode", 0, 0, 100));
        list.add(createColumn("isin", 0, 0, 100));
        list.add(createColumn("amount1", 0, 0, 100, true));
        list.add(createColumn("amount2", 0, 0, 100, true));
        preference.setColumns(list);
        preference.setSelectAllId(SELECT_ALL_ID);
        requestTable.setPreference(preference);
        requestTable.setCellEditor("amount1", new NumberFieldEditor());
        requestTable.setCellEditor("amount2", new NumberFieldEditor());

        RequestTableRowSum requestTableRowSum = new RequestTableRowSum(requestTable);
        requestTable.load();
        requestTable.setAutoResizeMode(RequestTable.AUTO_RESIZE_OFF);

        JScrollPane tableScrollPane = new JScrollPane(requestTable);
        tableScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        JScrollPane sumTableScrollPane = new JScrollPane(requestTableRowSum);
        tableScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        requestTableRowSum.initParentListeners();
        sumTableScrollPane.setPreferredSize(new Dimension(50, -1));
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);
        mainPanel.add(sumTableScrollPane, BorderLayout.WEST);

        frame.pack();
        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                System.exit(0);
            }
        });
    }
}
