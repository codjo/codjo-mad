package net.codjo.mad.gui.request;
import net.codjo.mad.client.request.Row;
import net.codjo.test.common.LogString;
import java.util.HashMap;
import java.util.Map;
import org.uispec4j.Panel;
import org.uispec4j.Table;
import org.uispec4j.UISpecTestCase;
/**
 *
 */
public class RequestCrossTablePanelTest extends UISpecTestCase {
    private Table crossTable;
    private RequestCrossTablePanel crossTablePanel;
    private ListDataSource listDataSource;
    private LogString log;


    public void test_columns() throws Exception {
        assertEquals("Scenario",
                     crossTable.getJTable().getColumnModel().getColumn(0).getHeaderValue());
        assertEquals("John",
                     crossTable.getJTable().getColumnModel().getColumn(1).getHeaderValue());
        assertEquals("Robert",
                     crossTable.getJTable().getColumnModel().getColumn(2).getHeaderValue());
    }


    public void test_rows() throws Exception {
        assertTrue(crossTable.contentEquals(
              new Object[][]{
                    {"Scénario 1", "0.50", "0.50"},
                    {"Scénario 2", "0.75", "0.75"},
                    {"Scénario 3", "1.50", ""}
              }));
    }


    public void test_notEditable() throws Exception {
        assertTrue(crossTable.columnIsEditable("scenarioName", false));
        assertTrue(crossTable.columnIsEditable("John", false));
        assertTrue(crossTable.columnIsEditable("Robert", false));
    }


    public void test_editable() throws Exception {
        crossTablePanel.setNotEditableColumns(new String[]{"Scenario"});

        assertTrue(crossTable.columnIsEditable("scenarioName", false));
        assertTrue(crossTable.columnIsEditable("John", true));
        assertTrue(crossTable.columnIsEditable("Robert", true));
    }


    public void test_setEmptyCellsCanBeEdited() throws Exception {
        crossTablePanel.setNotEditableColumns(new String[]{"Scenario"});
        crossTablePanel.setEmptyCellsCanBeEdited(false);

        assertTrue(crossTable.columnIsEditable(0, false));
        assertTrue(crossTable.columnIsEditable(1, true));

        assertFalse(crossTable.cellIsEditable(2, 0));
        assertTrue(crossTable.cellIsEditable(2, 1));
        assertFalse(crossTable.cellIsEditable(2, 2));
    }


    public void test_tableModelContainsRow() throws Exception {
        Map<String, String> rowToFind = new HashMap<String, String>();
        rowToFind.put("scenarioName", "Scénario 2");

        assertEquals(1, crossTablePanel.getRowIndexbyFieldMap(rowToFind));

        rowToFind = new HashMap<String, String>();
        rowToFind.put("scenarioName", "Scénario 4");
        assertEquals(-1, crossTablePanel.getRowIndexbyFieldMap(rowToFind));
    }


    public void test_crossTableListener() throws Exception {
        crossTablePanel.setNotEditableColumns(new String[]{"Scenario"});

        crossTablePanel.addCrossTableListener(new CrossTableListenerMock(log));

        crossTable.editCell(0, 1, "0.25", true);

        assertTrue(crossTable.contentEquals(
              new Object[][]{
                    {"Scénario 1", "0.25", "0.50"},
                    {"Scénario 2", "0.75", "0.75"},
                    {"Scénario 3", "1.50", ""}
              }));

        log.assertContent("cellContentsChanged(0, John)");
    }


    public void test_getValueAt() throws Exception {
        assertEquals("Scénario 2", crossTablePanel.getValueAt("scenarioName", 1));
        assertEquals("hidden 2", crossTablePanel.getValueAt("hidden", 1));
    }


    public void test_getGeneratedColumnNames() throws Exception {
        String[] actual = crossTablePanel.getGeneratedColumnNames();
        assertEquals(2, actual.length);
        assertEquals("John", actual[0]);
        assertEquals("Robert", actual[1]);
    }


    @Override
    protected void setUp() throws Exception {
        log = new LogString();
        crossTablePanel = new RequestCrossTablePanel();
        Panel tablePanel = new Panel(crossTablePanel);
        crossTable = tablePanel.getTable("crossTable");
        fillDataSource();
        Map<String, String> staticColumnFieldsMap = new HashMap<String, String>(1);
        staticColumnFieldsMap.put("scenarioName", "Scenario");
        staticColumnFieldsMap.put("hidden", "Hidden");
        crossTablePanel.setStaticColumnFieldsMap(staticColumnFieldsMap);
        crossTablePanel.bindDataSource(listDataSource, "resource", "nbJours",
                                       new String[]{"hidden"});
    }


    private void fillDataSource() throws Exception {
        listDataSource = new ListDataSource();
        listDataSource.addRow(new Row(createRowMap("Scénario 1", "0.50", "John",
                                                   "hidden 1")));
        listDataSource.addRow(new Row(createRowMap("Scénario 1", "0.50", "Robert",
                                                   "hidden 1")));
        listDataSource.addRow(new Row(createRowMap("Scénario 2", "0.75", "John",
                                                   "hidden 2")));
        listDataSource.addRow(new Row(createRowMap("Scénario 2", "0.75", "Robert",
                                                   "hidden 2")));
        listDataSource.addRow(new Row(createRowMap("Scénario 3", "1.50", "John",
                                                   "hidden 3")));
    }


    private Map createRowMap(String scenario, String nbJours, String resource,
                             String hidden) {
        Map<String, String> fields = new HashMap<String, String>();
        fields.put("scenarioName", scenario);
        fields.put("nbJours", nbJours);
        fields.put("resource", resource);
        fields.put("hidden", hidden);
        return fields;
    }


    private class CrossTableListenerMock implements RequestCrossTablePanel.RequestCrossTableListener {
        private LogString log;


        CrossTableListenerMock(LogString log) {
            this.log = log;
        }


        public void cellContentsChanged(int rowIndex, String column) {
            log.call("cellContentsChanged", "" + rowIndex, column);
        }
    }
}
