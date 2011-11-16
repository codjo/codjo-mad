package net.codjo.mad.gui.request;
import net.codjo.gui.toolkit.table.TableFilter;
import net.codjo.mad.client.request.FieldsList;
import net.codjo.mad.client.request.Request;
import net.codjo.mad.client.request.Result;
import net.codjo.mad.client.request.Row;
import net.codjo.mad.client.request.SelectRequest;
import net.codjo.mad.client.request.UpdateRequest;
import net.codjo.mad.client.request.util.RequestTestHelper;
import net.codjo.mad.gui.request.Column.Sorter;
import net.codjo.mad.gui.request.RequestTable.ScrollBarPolicy;
import net.codjo.mad.gui.request.factory.UpdateFactory;
import net.codjo.mad.gui.request.util.PreferenceRenderer;
import net.codjo.mad.gui.request.util.RequestTableRendererSorter;
import net.codjo.mad.gui.request.util.comparators.RowComparator;
import net.codjo.test.common.LogString;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import org.uispec4j.Table;
import org.uispec4j.UISpecTestCase;
/**
 * Classe de test de la classe {@link RequestTable}.
 */
public class RequestTableTest extends UISpecTestCase {
    private static final String SELECT_ALL_ID = "selectAll";
    private static final String[] COLUMNS = {"pimsCode", "isin", "sicovam", "flag"};
    public static final int ISIN_MAX_SIZE = 100;
    public static final int ISIN_PREFERRED_SIZE = 50;
    public static final int SICOVAM_PREFERRED_SIZE = 75;
    public static final int FLAG_PREFERRED_SIZE = 30;
    private RequestTestHelper helper;
    private Preference preference;
    private RequestTable table;
    private Preference otherPreference;
    private Preference preferenceWithSorter;


    public RequestTableTest(String testCaseName) {
        super(testCaseName);
    }


    @Override
    protected void setUp() {
        helper = new RequestTestHelper();
        table = new RequestTable();
        preference = new Preference();
        otherPreference = new Preference();

        List<Column> list = new ArrayList<Column>();
        list.add(createColumn("pimsCode", 0, 0, 0, null));
        list.add(createColumn("isin", 0, ISIN_MAX_SIZE, ISIN_PREFERRED_SIZE, null));
        list.add(createColumn("sicovam", 0, 0, SICOVAM_PREFERRED_SIZE, null));
        Column flag = createColumn("flag", 0, 0, FLAG_PREFERRED_SIZE, null);
        flag.setRenderer("net.codjo.gui.toolkit.swing.CheckBoxRenderer");
        list.add(flag);
        preference.setColumns(list);

        List<Column> otherList = new ArrayList<Column>();
        otherList.add(createColumn("pimsCode", 0, 0, 0, null));
        otherList.add(createColumn("isin", 0, ISIN_MAX_SIZE, ISIN_PREFERRED_SIZE, null));
        otherPreference.setColumns(otherList);

        preferenceWithSorter = new Preference();
        List<Column> columnList = new ArrayList<Column>();
        columnList.add(createColumn("pimsCode", 0, 0, 0, null));
        columnList.add(createColumn("isin", 0, ISIN_MAX_SIZE, ISIN_PREFERRED_SIZE, Sorter.STRING.name()));
        columnList.add(createColumn("sicovam", 0, 0, SICOVAM_PREFERRED_SIZE, Sorter.STRING.name()));
        Column flag2 = createColumn("flag", 0, 0, FLAG_PREFERRED_SIZE, null);
        flag2.setRenderer("net.codjo.gui.toolkit.swing.CheckBoxRenderer");
        columnList.add(flag2);
        preferenceWithSorter.setColumns(columnList);

        preference.setSelectAllId(SELECT_ALL_ID);
        otherPreference.setSelectAllId(SELECT_ALL_ID);
        preferenceWithSorter.setSelectAllId(SELECT_ALL_ID);
    }


    @Override
    protected void tearDown() {
        helper.tearDown();
    }


    private void setTablePage(int index) {
        table.setCurrentPage(index);
    }


    private Request[] getRequestListForTest(int pageNumber, int pageSize)
          throws Exception {
        SelectRequest select = new SelectRequest();
        select.setId(SELECT_ALL_ID);
        // On fait ce truc bizarre pour que les attributs soient dans le même
        //  ordre que la requête envoyée par la table.
        select.setAttributes(buildFields(COLUMNS).keySet().toArray(new String[COLUMNS.length]));
        select.setPage(pageNumber, pageSize);

        return new Request[]{select};
    }


    private String createSingleRowResult() {
        return "<?xml version=\"1.0\"?><results><result request_id=\""
               + (helper.getRequestId(0)) + "\">"
               + "        <primarykey>"
               + "           <field name=\"pimsCode\"/>"
               + "        </primarykey>"
               + "        <row>"
               + "           <field name=\"pimsCode\">666</field>"
               + "           <field name=\"isin\">isinVal</field>"
               + "           <field name=\"sicovam\">sicovamVal</field>"
               + "           <field name=\"flag\">true</field>"
               + "        </row>"
               + "</result></results>";
    }


    /**
     * Test que le chargement s'effectue correctement.
     */
    public void test_load() throws Exception {
        helper.setRequest(getRequestListForTest(1, ListDataSource.PAGE_SIZE));
        helper.setResult(createSingleRowResult());
        helper.activate();
        table.setPreference(preference);
        table.load();

        helper.verify();
        assertEquals(1, table.getRowCount());
        assertEquals(4, table.getColumnCount());
        assertEquals("666", table.getValueAt(0, 0));
        checkColumnSizes("isin", RequestTable.MIN_WIDTH, ISIN_MAX_SIZE,
                         ISIN_PREFERRED_SIZE);
        checkColumnSizes("sicovam", RequestTable.MIN_WIDTH, RequestTable.MAX_WIDTH,
                         SICOVAM_PREFERRED_SIZE);
        checkColumnSizes("pimsCode", RequestTable.MIN_WIDTH, Integer.MAX_VALUE, 75);
    }


    public void test_load_withHiddenColumns() throws Exception {
        helper.setRequest(getRequestListForTest(1, ListDataSource.PAGE_SIZE));
        helper.setResult(createSingleRowResult());
        helper.activate();

        List<Column> list = new ArrayList<Column>();
        list.add(createColumn("pimsCode", 0, 0, 0, null));
        list.add(createColumn("isin", 0, ISIN_MAX_SIZE, ISIN_PREFERRED_SIZE, null));
        list.add(createColumn("flag", 0, ISIN_MAX_SIZE, FLAG_PREFERRED_SIZE, null));
        preference.setColumns(list);
        preference.getHiddenColumns().add(new Column("sicovam", "Code sicovam"));

        table.setPreference(preference);
        table.load();

        helper.verify();
        assertEquals(1, table.getRowCount());
        assertEquals(3, table.getColumnCount());
        assertEquals("pimsCode", table.getColumnName(0));
        assertEquals("isin", table.getColumnName(1));
        assertEquals("flag", table.getColumnName(2));
        assertEquals("sicovamVal", table.getModel().getValueAt(0, 3));
    }


    public void test_convertFieldNameToModelIndex() throws Exception {
        helper.setRequest(getRequestListForTest(1, ListDataSource.PAGE_SIZE));
        helper.setResult(createSingleRowResult());
        helper.activate();

        List<Column> list = new ArrayList<Column>();
        list.add(createColumn("pimsCode", 0, 0, 0, null));
        list.add(createColumn("isin", 0, ISIN_MAX_SIZE, ISIN_PREFERRED_SIZE, null));
        list.add(createColumn("flag", 0, ISIN_MAX_SIZE, FLAG_PREFERRED_SIZE, null));
        preference.setColumns(list);
        preference.getHiddenColumns().add(new Column("sicovam", "Code sicovam"));

        table.setPreference(preference);
        table.load();

        try {
            table.convertFieldNameToModelIndex("bobo");
            fail();
        }
        catch (IllegalArgumentException ex) {
            assertEquals("La colonne 'bobo' n'existe pas", ex.getMessage());
        }

        assertEquals(3, table.convertFieldNameToModelIndex("sicovam"));
    }


    public void test_loadAfterSetModel() throws Exception {
        helper.setRequest(getRequestListForTest(1, ListDataSource.PAGE_SIZE));
        helper.setResult(createSingleRowResult());
        helper.activate();

        List<Column> list = new ArrayList<Column>();
        list.add(createColumn("pimsCode", 0, 0, 0, null));
        list.add(createColumn("isin", 0, ISIN_MAX_SIZE, ISIN_PREFERRED_SIZE, null));
        list.add(createColumn("flag", 0, ISIN_MAX_SIZE, FLAG_PREFERRED_SIZE, null));
        preference.setColumns(list);
        preference.getHiddenColumns().add(new Column("sicovam", "Code sicovam"));

        table.setPreference(preference);

        table.setModel(new TableFilter(table.getModel()));

        table.load();

        assertEquals(1, table.getRowCount());
        assertEquals(3, table.getColumnCount());
        assertSame(preference, table.getPreference());
        checkColumnSizes("isin", RequestTable.MIN_WIDTH, ISIN_MAX_SIZE,
                         ISIN_PREFERRED_SIZE);
    }


    /**
     * Test que le chargement de la page précédente s'effectue correctement.
     */
    public void test_loadPreviousPage() throws Exception {
        helper.setRequest(getRequestListForTest(1, ListDataSource.PAGE_SIZE));
        helper.setResult(createSingleRowResult());
        helper.activate();
        table.setPreference(preference);

        setTablePage(2);
        table.loadPreviousPage();

        helper.verify();
        assertEquals(1, table.getRowCount());
        assertEquals(4, table.getColumnCount());
        assertEquals("666", table.getValueAt(0, 0));
    }


    /**
     * Test que si aucune Preference n'est positionnée (pour les colonnes), alors aucune requête n'est envoyée
     * au serveur et qu'une erreur est lancée.
     */
    public void test_load_noPreference() throws Exception {
        try {
            table.load();
            fail("Le load doit echouer car aucune preference n'est positionné");
        }
        catch (IllegalStateException ex) {
            ; // Echec
        }
    }


    /**
     * Test que le positionnement direct du Result fonctionne correctement.
     */
    public void test_setResult() throws Exception {
        Result result = new Result();
        result.addPrimaryKey("pimsCode");
        Row row = new Row();
        row.addField("pimsCode", "666");
        row.addField("isin", "null");
        row.addField("sicovam", "sicovamVal");
        result.addRow(row);

        table.setPreference(preference);
        table.setResult(result);

        assertEquals(1, table.getRowCount());
        assertEquals(4, table.getColumnCount());
        assertEquals("666", table.getValueAt(0, 0));
        assertEquals(PreferenceRenderer.class, table.getCellRenderer(0, 1).getClass());
    }


    /**
     * a) Vérifie que la table est en lecture seule par défaut. Dans ce cas il est impossible de modifier le
     * modèle.<br/>
     *
     * b) La deuxième partie du test positionne la table en écriture et vérifie la modification du modèle
     */
    public void test_update_model() throws Exception {
        Result result = new Result();
        result.addPrimaryKey("pimsCode");
        Row row = new Row();
        row.addField("pimsCode", "666");
        row.addField("isin", "isinVal");
        row.addField("sicovam", "sicovamVal");
        result.addRow(row);
        table.setPreference(preference);
        table.setResult(result);

        // a)
        assertTrue(!table.isEditable());
        String value = this.table.getModel().getValueAt(0, 1).toString();
        try {
            this.table.getModel().setValueAt("valIsin", 0, 1);
            fail("Table en lecture seule !");
        }
        catch (Exception ex) {
            ; // Echec
        }
        assertEquals("isinVal", value);

        // b)
        table.setEditable(true);
        this.table.getModel().setValueAt("valIsin", 0, 1);
        assertEquals("valIsin", this.table.getModel().getValueAt(0, 1).toString());
    }


    public void test_hiddenColunms() throws Exception {
        Result result = new Result();
        result.addPrimaryKey("pimsCode");
        Row row = new Row();
        row.addField("pimsCode", "666");
        row.addField("isin", "isinVal");
        row.addField("sicovam", "sicovamVal");
        result.addRow(row);
        table.setPreference(preference);
        table.setResult(result);

        // a)
        assertTrue(!table.isEditable());
        String value = this.table.getModel().getValueAt(0, 1).toString();
        try {
            this.table.getModel().setValueAt("valIsin", 0, 1);
            fail("Table en lecture seule !");
        }
        catch (Exception ex) {
            ; // Echec
        }
        assertEquals("isinVal", value);

        // b)
        table.setEditable(true);
        this.table.getModel().setValueAt("valIsin", 0, 1);
        assertEquals("valIsin", this.table.getModel().getValueAt(0, 1).toString());
    }


    public void test_sortByColumn() throws Exception {
        table.setPreference(preference);
        table.setResult(createMultipleRowsResult());

        Table testTable = new Table(table);
        assertTrue(testTable.contentEquals(new Object[][]{
              {"1", "isinVal1", "sicovamVal1", Boolean.TRUE},
              {"2", "isinVal2", "sicovamVal2", Boolean.FALSE},
              {"3", "isinVal3", "sicovamVal3", Boolean.FALSE},
        }));

        table.getDataSource().setValue(0, "isin", "zorro");
        sortByColumn(1, false);

        assertTrue(testTable.contentEquals(new Object[][]{
              {"2", "isinVal2", "sicovamVal2", Boolean.FALSE},
              {"3", "isinVal3", "sicovamVal3", Boolean.FALSE},
              {"1", "zorro", "sicovamVal1", Boolean.TRUE},
        }));

        table.setPreference(otherPreference);
        assertTrue(testTable.contentEquals(new Object[][]{
              {"1", "zorro"},
              {"2", "isinVal2"},
              {"3", "isinVal3"},
        }));

        sortByColumn(1, false);
        assertTrue(testTable.contentEquals(new Object[][]{
              {"2", "isinVal2"},
              {"3", "isinVal3"},
              {"1", "zorro"},
        }));
    }


    public void test_sortByColumnWithACheckBoxRenderer() throws Exception {
        table.setPreference(preference);
        table.setResult(createMultipleRowsResult());

        Table testTable = new Table(table);
        assertTrue(testTable.contentEquals(new Object[][]{
              {"1", "isinVal1", "sicovamVal1", Boolean.TRUE},
              {"2", "isinVal2", "sicovamVal2", Boolean.FALSE},
              {"3", "isinVal3", "sicovamVal3", Boolean.FALSE},
        }));

        sortByColumn(3, false);
        assertTrue(testTable.contentEquals(new Object[][]{
              {"2", "isinVal2", "sicovamVal2", Boolean.FALSE},
              {"3", "isinVal3", "sicovamVal3", Boolean.FALSE},
              {"1", "isinVal1", "sicovamVal1", Boolean.TRUE},
        }));
    }


    public void test_sortWithMultipleColumn() throws Exception {
        table.setPreference(preference);
        table.setResult(createResultForSortWithMultipleColumn());

        Table testTable = new Table(table);
        assertTrue(testTable.contentEquals(new Object[][]{
              {"2", "isinVal2", "sicovamVal3", Boolean.TRUE},
              {"1", "isinVal1", "sicovamVal1", Boolean.FALSE},
              {"3", "isinVal3", "sicovamVal3", Boolean.TRUE},
              {"1", "isinVal2", "sicovamVal2", Boolean.FALSE},
        }));

        sortByColumn(1, false);
        assertTrue(testTable.contentEquals(new Object[][]{
              {"1", "isinVal1", "sicovamVal1", Boolean.FALSE},
              {"2", "isinVal2", "sicovamVal3", Boolean.TRUE},
              {"1", "isinVal2", "sicovamVal2", Boolean.FALSE},
              {"3", "isinVal3", "sicovamVal3", Boolean.TRUE},
        }));

        sortByColumn(2, false);
        assertTrue(testTable.contentEquals(new Object[][]{
              {"1", "isinVal1", "sicovamVal1", Boolean.FALSE},
              {"1", "isinVal2", "sicovamVal2", Boolean.FALSE},
              {"2", "isinVal2", "sicovamVal3", Boolean.TRUE},
              {"3", "isinVal3", "sicovamVal3", Boolean.TRUE},
        }));
    }


    public void test_sortByColumnWithNull() throws Exception {
        table.setPreference(preference);
        table.setResult(createMultipleRowsResult());

        Table testTable = new Table(table);
        assertTrue(testTable.contentEquals(new Object[][]{
              {"1", "isinVal1", "sicovamVal1", Boolean.TRUE},
              {"2", "isinVal2", "sicovamVal2", Boolean.FALSE},
              {"3", "isinVal3", "sicovamVal3", Boolean.FALSE},
        }));

        table.getDataSource().setValue(0, "isin", "null");
        table.getDataSource().setValue(1, "isin", "zorro");

        sortByColumn(1, false);

        assertTrue(testTable.contentEquals(new Object[][]{
              {"1", "", "sicovamVal1", Boolean.TRUE},
              {"3", "isinVal3", "sicovamVal3", Boolean.FALSE},
              {"2", "zorro", "sicovamVal2", Boolean.FALSE},
        }));

        table.setPreference(preferenceWithSorter);
        table.setResult(createMultipleRowsResult());

        table.getDataSource().setValue(0, "isin", "null");
        table.getDataSource().setValue(1, "isin", "zorro");

        sortByColumn(1, true);

        assertTrue(testTable.contentEquals(new Object[][]{
              {"2", "zorro", "sicovamVal2", Boolean.FALSE},
              {"3", "isinVal3", "sicovamVal3", Boolean.FALSE},
              {"1", "", "sicovamVal1", Boolean.TRUE},
        }));
    }


    public void test_sortViaRowComparator() throws Exception {
        Table testTable = createSortedTable();

        assertTrue(testTable.contentEquals(new Object[][]{
              {"1", "isinVal1", "sicovamVal1", Boolean.TRUE},
              {"2", "isinVal2", "sicovamVal2", Boolean.FALSE},
              {"3", "isinVal3", "sicovamVal3", Boolean.FALSE},
        }));

        table.getDataSource().setValue(0, "pimsCode", "4");
        table.sort();

        assertTrue(testTable.contentEquals(new Object[][]{
              {"2", "isinVal2", "sicovamVal2", Boolean.FALSE},
              {"3", "isinVal3", "sicovamVal3", Boolean.FALSE},
              {"4", "isinVal1", "sicovamVal1", Boolean.TRUE},
        }));

        assertEquals("2", table.getDataSource().getValueAt(1, "pimsCode"));
        table.getDataSource().setValue(1, "isin", "valIsin2");
        table.sort();

        assertTrue(testTable.contentEquals(new Object[][]{
              {"2", "valIsin2", "sicovamVal2", Boolean.FALSE},
              {"3", "isinVal3", "sicovamVal3", Boolean.FALSE},
              {"4", "isinVal1", "sicovamVal1", Boolean.TRUE},
        }));

        table.setPreference(otherPreference);
        table.sort();
        assertTrue(testTable.contentEquals(new Object[][]{
              {"2", "valIsin2"},
              {"3", "isinVal3"},
              {"4", "isinVal1"},
        }));

        table.getDataSource().setValue(1, "pimsCode", "5");
        table.sort();

        assertTrue(testTable.contentEquals(new Object[][]{
              {"3", "isinVal3"},
              {"4", "isinVal1"},
              {"5", "valIsin2"},
        }));
    }


    public void test_rowSelection() throws Exception {
        Table testTable = createSortedTable();
        ListDataSource dataSource = table.getDataSource();

        assertTrue(testTable.contentEquals(new Object[][]{
              {"1", "isinVal1", "sicovamVal1", Boolean.TRUE},
              {"2", "isinVal2", "sicovamVal2", Boolean.FALSE},
              {"3", "isinVal3", "sicovamVal3", Boolean.FALSE},
        }));

        Row rowIsinVal1 = dataSource.getRow(0);
        dataSource.setSelectedRow(rowIsinVal1);

        assertTrue(testTable.selectionEquals(new boolean[][]{
              {true, true, true},
              {false, false, false},
              {false, false, false},
        }));

        table.clearSelection();

        assertTrue(testTable.selectionEquals(new boolean[][]{
              {false, false, false},
              {false, false, false},
              {false, false, false},
        }));

        dataSource.setValue(0, "pimsCode", "4");
        table.sort();

        assertTrue(testTable.contentEquals(new Object[][]{
              {"2", "isinVal2", "sicovamVal2", Boolean.FALSE},
              {"3", "isinVal3", "sicovamVal3", Boolean.FALSE},
              {"4", "isinVal1", "sicovamVal1", Boolean.TRUE},
        }));

        dataSource.setSelectedRow(rowIsinVal1);

        assertTrue(testTable.selectionEquals(new boolean[][]{
              {false, false, false},
              {false, false, false},
              {true, true, true},
        }));
    }


    public void test_multipleRowSelection() throws Exception {
        Table testTable = createSortedTable();
        ListDataSource dataSource = table.getDataSource();
        final StringBuilder logString = new StringBuilder();
        dataSource.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                if (DataSource.SELECTED_ROW_PROPERTY.equals(evt.getPropertyName())) {
                    final int[] selectedRows = table.getSelectedRows();
                    logString.append("selectedRow.propertyChange = ");
                    for (int selectedRow : selectedRows) {
                        logString.append(selectedRow).append(";");
                    }
                }
            }
        });

        assertTrue(testTable.contentEquals(new Object[][]{
              {"1", "isinVal1", "sicovamVal1", Boolean.TRUE},
              {"2", "isinVal2", "sicovamVal2", Boolean.FALSE},
              {"3", "isinVal3", "sicovamVal3", Boolean.FALSE},
        }));

        testTable.getJTable().addRowSelectionInterval(0, 0);
        assertEquals("selectedRow.propertyChange = 0;", logString.toString());
        logString.delete(0, logString.length());

        testTable.getJTable().addRowSelectionInterval(2, 2);
        assertEquals("selectedRow.propertyChange = 0;2;", logString.toString());
        logString.delete(0, logString.length());

        testTable.getJTable().addRowSelectionInterval(1, 1);
        assertEquals("selectedRow.propertyChange = 0;1;2;", logString.toString());

        assertTrue(testTable.selectionEquals(new boolean[][]{
              {true, true, true},
              {true, true, true},
              {true, true, true},
        }));
    }


    public void test_horizontalScrollBarPolicy_default() throws Exception {
        table.setPreference(preference);
        table.setSize(210, 10);

        assertEquals(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS, table.getAutoResizeMode());
    }


    public void test_horizontalScrollBarPolicy_noScrollBar() throws Exception {
        table.setPreference(preference);
        table.setSize(300, 10);
        table.setHorizontalScrollBarPolicy(ScrollBarPolicy.HORIZONTAL_SCROLLBAR_SHOW_IF_NEEDED);

        assertEquals(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS, table.getAutoResizeMode());
    }


    public void test_horizontalScrollBarPolicy_scrollBar() throws Exception {
        table.setPreference(preference);
        table.setSize(210, 10);
        table.setHorizontalScrollBarPolicy(ScrollBarPolicy.HORIZONTAL_SCROLLBAR_SHOW_IF_NEEDED);

        assertEquals(JTable.AUTO_RESIZE_OFF, table.getAutoResizeMode());
    }


    public void test_setEntityNameFromPreference() throws Exception {
        preference.setEntity("anEntityName");

        table.setPreference(preference);

        assertEquals("anEntityName", table.getDataSource().getEntityName());
    }


    public void test_setEntityNameFromPreference_alreadySet() throws Exception {
        table.getDataSource().setEntityName("dataSourceEntityName");
        preference.setEntity("anEntityName");

        table.setPreference(preference);

        assertEquals("dataSourceEntityName", table.getDataSource().getEntityName());
    }


    public void test_convertRowIndexToModel() {
        table.setPreference(preference);
        table.setResult(createMultipleRowsResult());

        Table testTable = new Table(table);
        assertTrue(testTable.contentEquals(new Object[][]{
              {"1", "isinVal1", "sicovamVal1", Boolean.TRUE},
              {"2", "isinVal2", "sicovamVal2", Boolean.FALSE},
              {"3", "isinVal3", "sicovamVal3", Boolean.FALSE},
        }));

        table.getDataSource().setValue(0, "isin", "zorro");
        sortByColumn(1, false);

        assertTrue(testTable.contentEquals(new Object[][]{
              {"2", "isinVal2", "sicovamVal2", Boolean.FALSE},
              {"3", "isinVal3", "sicovamVal3", Boolean.FALSE},
              {"1", "zorro", "sicovamVal1", Boolean.TRUE},
        }));

        assertEquals(1, table.convertRowIndexToModelIndex(0));
        assertEquals(2, table.convertRowIndexToModelIndex(1));
        assertEquals(0, table.convertRowIndexToModelIndex(2));
    }


    public void test_cleanUpSorter() throws Exception {
        final LogString log = new LogString();

        RequestTableRendererSorter oldSorterModel = (RequestTableRendererSorter)table.getModel();
        TableModel subModel = oldSorterModel.getModel();

        table.setPreference(preference);

        RequestTableRendererSorter newSorterModel = (RequestTableRendererSorter)table.getModel();
        assertNotSame(oldSorterModel, newSorterModel);
        assertSame(subModel, newSorterModel.getModel());

        oldSorterModel.addTableModelListener(new TableModelListnerLogger(log, "old.tableChanged"));
        newSorterModel.addTableModelListener(new TableModelListnerLogger(log, "new.tableChanged"));
        table.getDataSource().addRow(new Row());

        log.assertContent("new.tableChanged()");
    }


    public void test_setModel() throws Exception {
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.addColumn("COL 1");
        tableModel.addColumn("COL 2");
        tableModel.addRow(new Object[]{"A String", 1});
        tableModel.addRow(new Object[]{"Another one", 2});
        tableModel.addRow(new Object[]{"Last string", 3});

        table.setModel(tableModel);

        assertEquals(2, table.getColumnCount());
        assertEquals(3, table.getRowCount());
        assertEquals("COL 1", table.getColumnName(0));
        assertEquals("COL 2", table.getColumnName(1));
        assertEquals("A String", table.getValueAt(0, 0));
        assertEquals(1, table.getValueAt(0, 1));
        assertEquals("Another one", table.getValueAt(1, 0));
        assertEquals(2, table.getValueAt(1, 1));
        assertEquals("Last string", table.getValueAt(2, 0));
        assertEquals(3, table.getValueAt(2, 1));
    }


    public void test_setAutoCommit() throws Exception {
        preference.setUpdate(new UpdateFactory("updatePims"));
        table.setPreference(preference);
        Result result = createMultipleRowsResult();
        result.setPrimaryKey("pimsCode");
        table.setResult(result);
        table.setEditable(true);

        //exception si on met autocommit à false alors qu'il l'est deja
        try {
            table.setAutoCommit(false);
        }
        catch (IllegalStateException e) {
            assertEquals("Autocommit listener is already cleared!", e.getMessage());
        }

        //exception si on passe des colonnes obligatoires inconnues
        try {
            table.setAutoCommit(true, "foo", "bar");
        }
        catch (IllegalArgumentException e) {
            assertEquals("Unknown field required by autocommit: foo", e.getMessage());
        }

        table.setAutoCommit(true, "pimsCode");

        //test de mise a jour avec autocommit
        FieldsList rowValues = new FieldsList();
        rowValues.addField("sicovam", "sicovamVal3bis");
        rowValues.addField("flag", "false");
        rowValues.addField("isin", "isinVal3");
        Request[] request = {new UpdateRequest("updatePims", new FieldsList("pimsCode", "3"),
                                               rowValues)};
        helper.setRequest(request);
        helper.setResult("<?xml version=\"1.0\"?><results>"
                         + "<result request_id=\"" + helper.getRequestId(0) + "\">"
                         + "<primarykey><field name=\"pimsCode\"/></primarykey>"
                         + "<row><field name=\"pimsCode\">3</field></row></result></results>");
        helper.activate();

        table.editCellAt(2, 2);
        table.setValueAt("sicovamVal3bis", 2, 2);

        helper.verify();
        assertEquals("sicovamVal3bis", table.getValueAt(2, 2));

        //exception si on met autocommit a true alors qu'il l'est deja
        try {
            table.setAutoCommit(true, "pimsCode");
        }
        catch (IllegalStateException e) {
            assertEquals("Autocommit listener is already set!", e.getMessage());
        }

        table.setAutoCommit(false);

        //test de mise a jour sans autocommit: pas de requetes envoyées
        table.setValueAt("sicovamVal3ter", 2, 2);
        assertEquals("sicovamVal3ter", table.getValueAt(2, 2));
    }


    private void sortByColumn(int columnIndex, boolean descending) {
        ((RequestTableRendererSorter)table.getModel()).sortByColumn(columnIndex, descending);
    }


    private Map<String, String> buildFields(String[] attributes) {
        Map<String, String> fields = new HashMap<String, String>();
        for (String attribute : attributes) {
            fields.put(attribute, null);
        }
        return fields;
    }


    private Table createSortedTable() {
        Result result = createMultipleRowsResult();
        table.setEditable(true);
        table.setRowComparator(new RowComparator() {
            public int compare(Row row1, Row row2) {
                Integer row1Priority = new Integer(row1.getFieldValue("pimsCode"));
                Integer row2Priority = new Integer(row2.getFieldValue("pimsCode"));
                return row1Priority.compareTo(row2Priority);
            }
        });
        table.setPreference(preference);
        table.setResult(result);

        return new Table(table);
    }


    private Result createMultipleRowsResult() {
        Result result = new Result();
        Row row = new Row();
        row.addField("pimsCode", "1");
        row.addField("isin", "isinVal1");
        row.addField("sicovam", "sicovamVal1");
        row.addField("flag", "true");
        result.addRow(row);
        row = new Row();
        row.addField("pimsCode", "2");
        row.addField("isin", "isinVal2");
        row.addField("sicovam", "sicovamVal2");
        row.addField("flag", "false");
        result.addRow(row);
        row = new Row();
        row.addField("pimsCode", "3");
        row.addField("isin", "isinVal3");
        row.addField("sicovam", "sicovamVal3");
        row.addField("flag", "false");
        result.addRow(row);
        return result;
    }


    private Result createResultForSortWithMultipleColumn() {
        Result result = new Result();
        Row row = new Row();
        row.addField("pimsCode", "2");
        row.addField("isin", "isinVal2");
        row.addField("sicovam", "sicovamVal3");
        row.addField("flag", "true");
        result.addRow(row);
        row = new Row();
        row.addField("pimsCode", "1");
        row.addField("isin", "isinVal1");
        row.addField("sicovam", "sicovamVal1");
        row.addField("flag", "false");
        result.addRow(row);
        row = new Row();
        row.addField("pimsCode", "3");
        row.addField("isin", "isinVal3");
        row.addField("sicovam", "sicovamVal3");
        row.addField("flag", "true");
        result.addRow(row);
        row = new Row();
        row.addField("pimsCode", "1");
        row.addField("isin", "isinVal2");
        row.addField("sicovam", "sicovamVal2");
        row.addField("flag", "false");
        result.addRow(row);
        return result;
    }


    private Column createColumn(String fieldName, int minSize, int maxSize,
                                int preferredSize, String sorter) {
        Column column = new Column();
        column.setFieldName(fieldName);
        column.setLabel(fieldName);
        column.setMinSize(minSize);
        column.setMaxSize(maxSize);
        column.setPreferredSize(preferredSize);
        column.setSorter(sorter);
        return column;
    }


    private void checkColumnSizes(String columnName, int minSize, int maxSize,
                                  int preferredSize) {
        int index = table.convertFieldNameToViewIndex(columnName);
        TableColumn column = table.getColumnModel().getColumn(index);
        assertEquals(minSize, column.getMinWidth());
        assertEquals(maxSize, column.getMaxWidth());
        assertEquals(preferredSize, column.getPreferredWidth());
    }


    private static class TableModelListnerLogger implements TableModelListener {
        private final LogString log;
        private String logMessage;


        TableModelListnerLogger(LogString log, String message) {
            this.log = log;
            logMessage = message;
        }


        public void tableChanged(TableModelEvent event) {
            log.call(logMessage);
        }
    }
}
