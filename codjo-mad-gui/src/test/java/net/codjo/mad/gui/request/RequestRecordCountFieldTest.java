package net.codjo.mad.gui.request;
import net.codjo.i18n.common.Language;
import net.codjo.mad.client.request.Result;
import net.codjo.mad.client.request.Row;
import net.codjo.mad.gui.MadGuiContext;
import net.codjo.mad.gui.i18n.InternationalizationUtil;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import junit.framework.TestCase;
/**
 * Classe de test de {@link RequestRecordCountField}.
 */
public class RequestRecordCountFieldTest extends TestCase {
    private RequestRecordCountField countField;
    private MadGuiContext context;


    public void test_name() throws Exception {
        RequestTable requestTable = createRequestTable();
        requestTable.setName("Bobo");
        countField.initialize(requestTable, context);
        assertEquals("Bobo.RequestRecordCountField", countField.getName());
    }


    public void test_empty() throws Exception {
        countField.initialize(createRequestTable(), context);
        assertEquals("0 à 0 sur 0", countField.getText());
    }


    public void test_newTableModel() throws Exception {
        RequestTable table = createRequestTable();
        Preference pref = new Preference();

        Column col1 = new Column();
        col1.setFieldName("isin");
        col1.setLabel("isin");

        List<Column> list = new ArrayList<Column>();
        list.add(col1);
        pref.setColumns(list);

        table.setPreference(pref);

        Result result = new Result();
        Row row1 = new Row();
        row1.addField("isin", "null");
        result.addRow(row1);
        Row row2 = new Row();
        row2.addField("isin", "null");
        result.addRow(row2);
        Row row3 = new Row();
        row3.addField("isin", "null");
        result.addRow(row3);

        table.setResult(result);
        countField.initialize(table, context);
        assertEquals("1 à 3 sur 3", countField.getText());
        DefaultTableModel defaultTableModel =
              new DefaultTableModel(new String[]{"isin"}, 1);
        table.setModel(defaultTableModel);

        assertEquals("1 à 1 sur 3", countField.getText());
    }


    public void test_newTableModel_empty() throws Exception {
        RequestTable table = createRequestTable();
        table.getDataSource().addRow(new Row());

        countField.initialize(table, context);
        assertEquals("1 à 1 sur 1", countField.getText());

        table.setModel(new RequestTableModel());

        assertEquals("0 à 0 sur 1", countField.getText());
    }


    public void test_onlyAddedRows() throws Exception {
        RequestTable table = createRequestTable();

        table.getDataSource().addRow(new Row());
        table.getDataSource().addRow(new Row());
        countField.initialize(table, context);
        assertEquals("1 à 2 sur 2", countField.getText());
    }


    public void test_withRemovedRows() throws Exception {
        Result result = new Result();
        result.addRow(new Row());

        RequestTable table = createRequestTable();
        table.getDataSource().setLoadResult(result);
        table.getDataSource().addRow(new Row());

        countField.initialize(table, context);

        table.getDataSource().removeRow(0);

        assertEquals("1 à 1 sur 1", countField.getText());
    }


    public void test_notFirstPage() throws Exception {
        ListDataSource dataSource =
              new ListDataSource() {
                  @Override
                  public void load() {
                      setLoadResult(buildResult(10, 30));
                  }
              };
        dataSource.setGuiContext(context);

        RequestTable table = new RequestTable(dataSource);
        table.setPageSize(10);
        countField.initialize(table, context);

        assertEquals("0 à 0 sur 0", countField.getText());

        table.load();
        assertEquals("1 à 10 sur 30", countField.getText());

        table.loadNextPage();
        assertEquals("11 à 20 sur 30", countField.getText());
    }


    public void test_onlyAddedRowsInTwoSteps() throws Exception {
        Result result = new Result();
        result.addRow(new Row());
        result.addRow(new Row());
        result.addRow(new Row());
        RequestTable table = createRequestTable();
        table.getDataSource().setLoadResult(result);

        countField.initialize(table, context);

        assertEquals("1 à 3 sur 3", countField.getText());

        table.getDataSource().addRow(new Row());
        table.getDataSource().addRow(new Row());
        assertEquals("1 à 5 sur 5", countField.getText());
    }


    public void test_addOneMoreThanCurrentPage() throws Exception {
        int pageSize = 10;
        RequestTable table = createRequestTable();
        table.getDataSource().setLoadResult(buildResult(pageSize, pageSize * 2));
        table.setPageSize(pageSize);
        countField.initialize(table, context);

        assertEquals("1 à 10 sur 20", countField.getText());

        table.getDataSource().addRow(new Row());
        assertEquals("1 à 11 sur 21", countField.getText());
    }


    public void test_updateTranslation() throws Exception {
        Result result = new Result();
        result.addRow(new Row());
        result.addRow(new Row());
        result.addRow(new Row());
        RequestTable table = createRequestTable();
        table.getDataSource().setLoadResult(result);

        countField.initialize(table, context);

        assertEquals("1 à 3 sur 3", countField.getText());

        InternationalizationUtil.retrieveTranslationNotifier(context).setLanguage(Language.EN);
        assertEquals("1 of 3 out of 3", countField.getText());

        table.getDataSource().addRow(new Row());
        table.getDataSource().addRow(new Row());
        assertEquals("1 of 5 out of 5", countField.getText());

        InternationalizationUtil.retrieveTranslationNotifier(context).setLanguage(Language.FR);
        assertEquals("1 à 5 sur 5", countField.getText());
    }


    private RequestTable createRequestTable() {
        RequestTable table = new RequestTable();
        table.getDataSource().setGuiContext(context);
        return table;
    }


    @Override
    protected void setUp() throws Exception {
        countField = new RequestRecordCountField();
        context = new MadGuiContext();
    }


    private Result buildResult(int size, int recordCount) {
        Result result = new Result();
        for (int index = 0; index < size; index++) {
            result.addRow(new Row());
        }
        result.setTotalRowCount(recordCount);
        return result;
    }
}
