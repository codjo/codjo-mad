package net.codjo.mad.gui.request;
import net.codjo.mad.client.request.Row;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import org.uispec4j.Panel;
import org.uispec4j.Table;
import org.uispec4j.UISpecTestCase;

public class RequestCrossTableRowSumTest extends UISpecTestCase {
    RequestCrossTablePanel crossTablePanel;
    JTable crossTable;
    RequestCrossTableRowSum crossTableRowSum;
    private ListDataSource listDataSource;


    public void test_sum() throws Exception {
        assertEquals(3, crossTableRowSum.getRowCount());
        assertEquals(1, crossTableRowSum.getColumnCount());
        assertEquals(crossTable.getRowCount(), crossTableRowSum.getRowCount());

        assertEquals(new BigDecimal(1).setScale(2, BigDecimal.ROUND_DOWN), crossTableRowSum.getValueAt(0, 0));
        assertEquals(new BigDecimal(1.50).setScale(2, BigDecimal.ROUND_DOWN),
                     crossTableRowSum.getValueAt(1, 0));
        assertEquals(new BigDecimal(1.50).setScale(2, BigDecimal.ROUND_DOWN),
                     crossTableRowSum.getValueAt(2, 0));
    }


    public void test_notEditable() throws Exception {
        assertFalse(crossTableRowSum.isCellEditable(1, 0));
        assertFalse(crossTableRowSum.isCellEditable(2, 0));
    }


    public void test_crossTableSumListener() throws Exception {
        assertEquals(new BigDecimal(1.00).setScale(2, BigDecimal.ROUND_DOWN),
                     crossTableRowSum.getValueAt(0, 0));
        assertEquals(new BigDecimal(1.50).setScale(2, BigDecimal.ROUND_DOWN),
                     crossTableRowSum.getValueAt(1, 0));
        assertEquals(new BigDecimal(1.50).setScale(2, BigDecimal.ROUND_DOWN),
                     crossTableRowSum.getValueAt(2, 0));

        crossTablePanel.setNotEditableColumns(new String[]{"Scenario"});
        Panel tablePanel = new Panel(crossTablePanel);
        Table table = tablePanel.getTable("crossTable");
        table.editCell(0, 1, "0.25", true);

        assertTrue(table.contentEquals(
              new Object[][]{
                    {"Scénario 1", "0.25", "0.50"},
                    {"Scénario 2", "0.75", "0.75"},
                    {"Scénario 3", "1.50", ""}
              }));

        assertEquals(new BigDecimal(0.75).setScale(2, BigDecimal.ROUND_DOWN),
                     crossTableRowSum.getValueAt(0, 0));
        assertEquals(new BigDecimal(1.50).setScale(2, BigDecimal.ROUND_DOWN),
                     crossTableRowSum.getValueAt(1, 0));
        assertEquals(new BigDecimal(1.50).setScale(2, BigDecimal.ROUND_DOWN),
                     crossTableRowSum.getValueAt(2, 0));
    }


    @Override
    protected void setUp() throws Exception {
        crossTablePanel = new RequestCrossTablePanel();
        crossTable = crossTablePanel.getTable();
        fillDataSource();
        Map<String, String> staticColumnFieldsMap = new HashMap<String, String>(2);
        staticColumnFieldsMap.put("scenarioName", "Scenario");
        staticColumnFieldsMap.put("hidden", "Hidden");
        crossTablePanel.setStaticColumnFieldsMap(staticColumnFieldsMap);
//        crossTableRowSum = new RequestCrossTableRowSum(crossTablePanel, new String[] {"scenarioName","hidden"});
        crossTablePanel.bindDataSource(listDataSource, "resource", "nbJours", new String[]{"hidden"});

        crossTableRowSum = new RequestCrossTableRowSum(crossTablePanel, new String[]{"Scenario"});
        crossTableRowSum.refreshModel();
    }


    private void fillDataSource() throws Exception {
        listDataSource = new ListDataSource();
        listDataSource.addRow(new Row(createRowMap("Scénario 1", "0.50", "John", "hidden 1")));
        listDataSource.addRow(new Row(createRowMap("Scénario 1", "0.50", "Robert", "hidden 1")));
        listDataSource.addRow(new Row(createRowMap("Scénario 2", "0.75", "John", "hidden 2")));
        listDataSource.addRow(new Row(createRowMap("Scénario 2", "0.75", "Robert", "hidden 2")));
        listDataSource.addRow(new Row(createRowMap("Scénario 3", "1.50", "John", "hidden 3")));
    }


    protected static Map createRowMap(String scenario, String nbJours, String resource, String hidden) {
        Map<String, String> fields = new HashMap<String, String>();
        fields.put("scenarioName", scenario);
        fields.put("nbJours", nbJours);
        fields.put("resource", resource);
        fields.put("hidden", hidden);
        return fields;
    }


    @Override
    protected void tearDown() throws Exception {
    }


    public static void main(String[] args) throws Exception {
        JFrame frame = new JFrame("Test RequestCrossTableRowSum");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JPanel mainPanel = new JPanel(new BorderLayout());
        Container contentPane = frame.getContentPane();

        RequestCrossTablePanel crossTablePanel = new RequestCrossTablePanel();
        JTable crossTable = crossTablePanel.getTable();
        crossTable.setAutoResizeMode(RequestTable.AUTO_RESIZE_OFF);
        ListDataSource listDataSource = new ListDataSource();
        listDataSource.addRow(new Row(createRowMap("Scénario 1", "0.50", "John", "hidden 1")));
        listDataSource.addRow(new Row(createRowMap("Scénario 1", "0.50", "Robert", "hidden 1")));
        listDataSource.addRow(new Row(createRowMap("Scénario 2", "0.75", "John", "hidden 2")));
        listDataSource.addRow(new Row(createRowMap("Scénario 2", "0.75", "Robert", "hidden 2")));
        listDataSource.addRow(new Row(createRowMap("Scénario 3", "1.50", "John", "hidden 3")));
        Map<String, String> staticColumnFieldsMap = new HashMap<String, String>(2);
        staticColumnFieldsMap.put("scenarioName", "Scenario");
        staticColumnFieldsMap.put("hidden", "Hidden");
        crossTablePanel.setStaticColumnFieldsMap(staticColumnFieldsMap);

        RequestCrossTableRowSum crossTableRowSum = new RequestCrossTableRowSum(crossTablePanel,
                                                                               new String[]{"Hidden",
                                                                                            "Scenario"});
        crossTablePanel.setNotEditableColumns(new String[]{"Scenario"});

        crossTablePanel.bindDataSource(listDataSource, "resource", "nbJours", new String[]{"hidden"});
        crossTableRowSum.refreshModel();

        JPanel tablePanel = new JPanel();
        tablePanel.setLayout(new BoxLayout(tablePanel, BoxLayout.Y_AXIS));

        JScrollPane crossTableRowSumScrollPane = new JScrollPane(crossTableRowSum);
        JScrollPane crossTableScrollPane = new JScrollPane(crossTable);
        crossTableScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        crossTableRowSum.init();
        crossTableRowSumScrollPane.setPreferredSize(new Dimension(50, -1));
        mainPanel.add(crossTableScrollPane, BorderLayout.CENTER);
        mainPanel.add(crossTableRowSumScrollPane, BorderLayout.WEST);
        contentPane.add(mainPanel);
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
