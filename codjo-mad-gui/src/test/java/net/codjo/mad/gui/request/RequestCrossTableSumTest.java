package net.codjo.mad.gui.request;
import net.codjo.mad.client.request.Row;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JTable;
import org.uispec4j.Panel;
import org.uispec4j.Table;
import org.uispec4j.UISpecTestCase;
/**
 * 
 */
public class RequestCrossTableSumTest extends UISpecTestCase {
    RequestCrossTablePanel crossTablePanel;
    JTable crossTable;
    RequestCrossTableSum crossTableSum;
    private ListDataSource listDataSource;

    public void test_sum() throws Exception {
        assertEquals(1, crossTableSum.getRowCount());
        assertEquals(3, crossTableSum.getColumnCount());
        assertEquals(crossTable.getColumnCount(), crossTableSum.getColumnCount());

        assertEquals("Scenario", crossTable.getColumnModel().getColumn(0).getHeaderValue());
        assertEquals("John", crossTable.getColumnModel().getColumn(1).getHeaderValue());
        assertEquals("Robert", crossTable.getColumnModel().getColumn(2).getHeaderValue());

        assertEquals(new BigDecimal(2.75), crossTableSum.getValueAt(0, 1));
        assertEquals(new BigDecimal(1.25), crossTableSum.getValueAt(0, 2));
    }


    public void test_notEditable() throws Exception {
        assertFalse(crossTableSum.isCellEditable(0, 1));
        assertFalse(crossTableSum.isCellEditable(0, 2));
    }


    public void test_crossTableSumListener() throws Exception {
        assertEquals(new BigDecimal(2.75), crossTableSum.getValueAt(0, 1));
        assertEquals(new BigDecimal(1.25), crossTableSum.getValueAt(0, 2));

        crossTablePanel.setNotEditableColumns(new String[] {"Scenario"});
        Panel tablePanel = new Panel(crossTablePanel);
        Table table = tablePanel.getTable("crossTable");
        table.editCell(0, 1, "0.25", true);

        assertTrue(table.contentEquals(
                new Object[][] {
                    {"Scénario 1", "0.25", "0.50"},
                    {"Scénario 2", "0.75", "0.75"},
                    {"Scénario 3", "1.50", ""}
                }));

        assertEquals("2.50", crossTableSum.getValueAt(0, 1).toString());
        assertEquals("1.25", crossTableSum.getValueAt(0, 2).toString());
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
        crossTablePanel.bindDataSource(listDataSource, "resource", "nbJours", new String[] {"hidden"});

        crossTableSum = new RequestCrossTableSum(crossTablePanel, new String[] {"Scenario"});
        crossTableSum.refreshModel();
    }


    private void fillDataSource() throws Exception {
        listDataSource = new ListDataSource();
        listDataSource.addRow(new Row(createRowMap("Scénario 1", "0.50", "John", "hidden 1")));
        listDataSource.addRow(new Row(createRowMap("Scénario 1", "0.50", "Robert", "hidden 1")));
        listDataSource.addRow(new Row(createRowMap("Scénario 2", "0.75", "John", "hidden 2")));
        listDataSource.addRow(new Row(createRowMap("Scénario 2", "0.75", "Robert", "hidden 2")));
        listDataSource.addRow(new Row(createRowMap("Scénario 3", "1.50", "John", "hidden 3")));
    }


    private Map createRowMap(String scenario, String nbJours, String resource, String hidden) {
        Map<String, String> fields = new HashMap<String, String>();
        fields.put("scenarioName", scenario);
        fields.put("nbJours", nbJours);
        fields.put("resource", resource);
        fields.put("hidden", hidden);
        return fields;
    }


    @Override
    protected void tearDown() throws Exception {}
}
