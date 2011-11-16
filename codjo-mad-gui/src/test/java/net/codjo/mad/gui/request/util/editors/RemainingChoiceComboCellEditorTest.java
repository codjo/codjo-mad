package net.codjo.mad.gui.request.util.editors;
import net.codjo.mad.client.request.Field;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.client.request.Result;
import net.codjo.mad.client.request.Row;
import net.codjo.mad.gui.framework.DefaultGuiContext;
import net.codjo.mad.gui.request.ListDataSource;
import net.codjo.mad.gui.request.PreferenceFactory;
import net.codjo.mad.gui.request.RequestComboBox;
import net.codjo.mad.gui.request.RequestTable;
import net.codjo.mad.gui.request.RequestToolBar;
import net.codjo.mad.gui.MadGuiContext;
import net.codjo.security.common.api.UserMock;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import org.uispec4j.ComboBox;
import org.uispec4j.Table;
import org.uispec4j.UISpecTestCase;
import org.xml.sax.InputSource;

public class RemainingChoiceComboCellEditorTest extends UISpecTestCase {
    private static final String PREFERENCE =
          "<?xml version='1.0'?>                                                 "
          + "<preferenceList>                                                    "
          + "  <preference id='list'>                                            "
          + "    <column fieldName='aCode' label='Code'/>                        "
          + "  </preference>                                                     "
          + "</preferenceList>                                                   ";

    private Table table;
    private RequestToolBar requestToolBar;


    public void test_remainingChoice() throws Exception {
        ListDataSource tableDataSource = new ListDataSource();
        tableDataSource.addRow(TableRowBuilder.buildRow("aCode", "AB"));
        tableDataSource.addRow(TableRowBuilder.buildRow("aCode", "AE"));

        initTable(tableDataSource);

        assertTrue(table.contentEquals(new Object[][]{
              {"AB"},
              {"AE"},
        }));

        assertTrue(editCell(0, 0).contentEquals(new String[]{"AB", "AC", "AD"}));

        assertTrue(editCell(1, 0).contentEquals(new String[]{"AC", "AD", "AE"}));
    }


    public void test_comboNotEditableWhenComboIsEmpty() throws Exception {
        ListDataSource tableDataSource = new ListDataSource();
        tableDataSource.addRow(TableRowBuilder.buildRow("aCode", "AB"));
        tableDataSource.addRow(TableRowBuilder.buildRow("aCode", "AC"));
        tableDataSource.addRow(TableRowBuilder.buildRow("aCode", "AD"));
        tableDataSource.addRow(TableRowBuilder.buildRow("aCode", "AE"));

        initTable(tableDataSource);

        assertTrue(table.contentEquals(new Object[][]{
              {"AB"},
              {"AC"},
              {"AD"},
              {"AE"},
        }));

        addNewRow();

        assertTrue(table.contentEquals(new Object[][]{
              {"AB"},
              {"AC"},
              {"AD"},
              {"AE"},
              {""},
        }));

        assertFalse(table.getJTable().editCellAt(0, 0));
        assertFalse(table.getJTable().editCellAt(1, 0));
        assertFalse(table.getJTable().editCellAt(2, 0));
        assertFalse(table.getJTable().editCellAt(3, 0));
        assertFalse(table.getJTable().editCellAt(4, 0));
    }


    private void addNewRow() {
        requestToolBar.getAction(RequestToolBar.ACTION_ADD).actionPerformed(null);
    }


    private ComboBox editCell(int row, int column) {
        return table.editCell(row, column).getComboBox();
    }


    private void initTable(ListDataSource tableDataSource) throws Exception {
        MadGuiContext context = new MadGuiContext();
        tableDataSource.setGuiContext(context);
        PreferenceFactory.loadMapping(new InputSource(new StringReader(PREFERENCE)));
        RequestTable requestTable = new RequestTable(tableDataSource);
        requestTable.setEditable(true);
        requestTable.setPreference(PreferenceFactory.getPreference("list"));
        RequestComboBox comboBox = initComboBox();
        requestTable.setCellEditor("aCode", new RemainingChoiceComboCellEditor(comboBox,
                                                                               tableDataSource,
                                                                               "aCode",
                                                                               "refCode"));
        requestToolBar = new RequestToolBar();
        context.setUser(new UserMock().mockIsAllowedTo(true));
        requestToolBar.init(context, requestTable);

        table = new Table(requestTable);
    }


    private RequestComboBox initComboBox() {
        ListDataSource comboBoxDataSource = new ComboBoxDataSourceMock(new String[]{"refCode", "refLabel"});

        RequestComboBox comboBox = new RequestComboBox();

        comboBox.setContainsNullValue(false);
        comboBox.setModelFieldName("refCode");

        comboBox.setDataSource(comboBoxDataSource);
        return comboBox;
    }


    private class ComboBoxDataSourceMock extends ListDataSource {
        private Result result;
        private final String[] columns;


        private ComboBoxDataSourceMock(String[] columns) {
            this.columns = columns;
            initResult();
        }


        @Override
        public void load() throws RequestException {
            clear();
            setColumns(columns);
            initResult();
        }


        private void initResult() {
            this.result = new Result();
            result.addRow(ComboBoxRowBuilder.buildRow("refCode", "AB"));
            result.addRow(ComboBoxRowBuilder.buildRow("refCode", "AC"));
            result.addRow(ComboBoxRowBuilder.buildRow("refCode", "AD"));
            result.addRow(ComboBoxRowBuilder.buildRow("refCode", "AE"));
            setLoadResult(result);
        }
    }

    private static class ComboBoxRowBuilder {

        private ComboBoxRowBuilder() {
        }


        public static Row buildRow(String fieldName, String fieldValue) {
            List<Field> fieldList = new ArrayList<Field>();
            fieldList.add(new Field(fieldName, fieldValue));
            fieldList.add(new Field("refLabel", "Label " + fieldValue));
            Row row = new Row();
            row.setFields(fieldList);

            return row;
        }
    }

    private static class TableRowBuilder {

        private TableRowBuilder() {
        }


        public static Row buildRow(String fieldName, String fieldValue) {
            List<Field> fieldList = new ArrayList<Field>();
            fieldList.add(new Field(fieldName, fieldValue));
            Row row = new Row();
            row.setFields(fieldList);

            return row;
        }
    }
}
