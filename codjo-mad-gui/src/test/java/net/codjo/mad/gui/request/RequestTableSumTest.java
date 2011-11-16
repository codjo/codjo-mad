package net.codjo.mad.gui.request;
import net.codjo.gui.toolkit.swing.NumberFieldEditor;
import net.codjo.mad.client.request.Request;
import net.codjo.mad.client.request.Row;
import net.codjo.mad.client.request.SelectRequest;
import net.codjo.mad.client.request.util.RequestTestHelper;
import net.codjo.mad.gui.request.RequestTableSum.AbstractCellContent;
import net.codjo.test.common.LogString;
import java.awt.BorderLayout;
import java.awt.Container;
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
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import junit.framework.TestCase;

public class RequestTableSumTest extends TestCase {
    private static final String SELECT_ALL_ID = "selectAll";
    private static final String[] COLUMNS = {"pimsCode", "isin", "amount1", "amount2"};
    private RequestTestHelper helper;
    private RequestTable requestTable;
    private RequestTableSum requestTableSum;


    public void test_load() throws Exception {
        assertEquals(1, requestTableSum.getRowCount());
        assertEquals(4, requestTableSum.getColumnCount());
        assertEquals(requestTable.getColumnCount(), requestTableSum.getColumnCount());
        assertEquals("TOTAL", ((AbstractCellContent)requestTableSum.getValueAt(0, 0)).getValue());
        assertEquals("null", ((AbstractCellContent)requestTableSum.getValueAt(0, 1)).getValue());
        assertEquals(new BigDecimal(10),
                     ((RequestTableSum.Sum)requestTableSum.getValueAt(0, 2)).getValue());
        assertEquals(new BigDecimal(10.4).setScale(1, BigDecimal.ROUND_DOWN),
                     ((RequestTableSum.Sum)requestTableSum.getValueAt(0, 3)).getValue());
    }


    public void test_addRow() throws Exception {
        Row row = new Row();
        row.addField("pimsCode", "123");
        row.addField("isin", "isinVal123");
        row.addField("amount1", "10");
        row.addField("amount2", "11");
        requestTable.getDataSource().addRow(row);

        assertEquals(new BigDecimal(20),
                     ((RequestTableSum.Sum)requestTableSum.getValueAt(0, 2)).getValue());
        assertEquals(new BigDecimal(21.4).setScale(1, BigDecimal.ROUND_CEILING),
                     ((RequestTableSum.Sum)requestTableSum.getValueAt(0, 3)).getValue());
    }


    public void test_removeRow() throws Exception {
        requestTable.getDataSource().removeRow(1);

        assertEquals(new BigDecimal(9),
                     ((RequestTableSum.Sum)requestTableSum.getValueAt(0, 2)).getValue());
        assertEquals(new BigDecimal(9.3).setScale(1, BigDecimal.ROUND_DOWN),
                     ((RequestTableSum.Sum)requestTableSum.getValueAt(0, 3)).getValue());
    }


    public void test_setName() throws Exception {
        assertEquals("RequestTableName", requestTable.getName());
        assertEquals(requestTable.getName() + ".Sum", requestTableSum.getName());

        requestTableSum.setName("myName");
        requestTableSum.initRequestTable(requestTable);
        assertEquals("myName", requestTableSum.getName());
    }


    public void test_updateRow() throws Exception {
        final LogString listener = new LogString();
        requestTableSum.getModel().addTableModelListener(new TableModelListener() {
            public void tableChanged(TableModelEvent event) {
                listener.info("passage dans le listener");
            }
        });
        requestTable.getDataSource().setValue(1, "amount1", "12");
        requestTable.getDataSource().setValue(1, "amount2", "12");

        Thread.sleep(5);

        BigDecimal value =
              ((RequestTableSum.Sum)requestTableSum.getValueAt(0, 2)).getValue();
        assertEquals(new BigDecimal(21.0), value);
        assertEquals(new BigDecimal(21.4).setScale(1, BigDecimal.ROUND_DOWN),
                     ((RequestTableSum.Sum)requestTableSum.getValueAt(0, 3)).getValue());
        assertEquals("passage dans le listener, passage dans le listener", listener.getContent());
    }


    private static Column createColumn(String fieldName, int minSize, int maxSize,
                                       int preferredSize) {
        return createColumn(fieldName, minSize, maxSize, preferredSize, false);
    }


    private static Column createColumn(String fieldName,
                                       int minSize,
                                       int maxSize,
                                       int preferredSize,
                                       String summableLabel) {
        Column column = createColumn(fieldName, minSize, maxSize, preferredSize, false);
        column.setSummableLabel(summableLabel);
        return column;
    }


    private static Column createColumn(String fieldName, int minSize, int maxSize,
                                       int preferredSize, boolean summable) {
        Column column = new Column();
        column.setFieldName(fieldName);
        column.setLabel(fieldName);
        column.setMinSize(minSize);
        column.setMaxSize(maxSize);
        column.setPreferredSize(preferredSize);
        column.setSummable(summable);
        return column;
    }


    public static Request[] getRequestListForTest(int pageNumber, int pageSize)
          throws Exception {
        SelectRequest select = new SelectRequest();
        select.setId(SELECT_ALL_ID);
        select.setAttributes(buildFieldsArray(COLUMNS));
        select.setPage(pageNumber, pageSize);

        return new Request[]{select};
    }


    public static String[] buildFieldsArray(String[] columns) {
        return buildFields(columns).keySet().toArray(new String[]{});
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
        list.add(createColumn("pimsCode", 0, 0, 100, "TOTAL"));
        list.add(createColumn("isin", 0, 0, 100));
        list.add(createColumn("amount1", 0, 0, 100, true));
        list.add(createColumn("amount2", 0, 0, 100, true));
        preference.setColumns(list);
        preference.setSelectAllId(SELECT_ALL_ID);
        requestTable.setPreference(preference);
        requestTable.setCellEditor("amount1", new NumberFieldEditor());
        requestTable.setCellEditor("amount2", new NumberFieldEditor());
        requestTable.setName("RequestTableName");

        requestTableSum = new RequestTableSum(requestTable);
        requestTable.load();
    }


    @Override
    protected void tearDown() throws Exception {
        helper.tearDown();
    }


    private static String getRequestResult(RequestTestHelper helper) {
        return "<?xml version=\"1.0\"?>" + "<results>" + "     <result request_id=\""
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
               + "           <field name=\"amount1\">1</field>                   "
               + "           <field name=\"amount2\">1.1</field>                   "
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
        JFrame frame = new JFrame("Test RequestTableSum");
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
        requestTable.setEditable(true);
        Preference preference = new Preference();
        List<Column> list = new ArrayList<Column>();
        list.add(createColumn("pimsCode", 0, 0, 100, "TOTAL"));
        list.add(createColumn("isin", 0, 0, 100));
        list.add(createColumn("amount1", 0, 0, 100, true));
        list.add(createColumn("amount2", 0, 0, 100, true));
        preference.setColumns(list);
        preference.setSelectAllId(SELECT_ALL_ID);
        requestTable.setPreference(preference);
        requestTable.setCellEditor("amount1", new NumberFieldEditor());
        requestTable.setCellEditor("amount2", new NumberFieldEditor());

        RequestTableSum requestTableSum = new RequestTableSum();
        requestTable.load();

        JScrollPane tableScrollPane = new JScrollPane(requestTable);

        JScrollPane sumTableScrollPane = new JScrollPane(requestTableSum);
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);
        mainPanel.add(sumTableScrollPane, BorderLayout.SOUTH);

        requestTableSum.initRequestTable(requestTable);

        requestTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        requestTableSum.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

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
