package net.codjo.mad.gui.request;
import net.codjo.mad.client.request.Result;
import net.codjo.mad.client.request.Row;
import net.codjo.mad.gui.request.event.DataSourceAdapter;
import net.codjo.mad.gui.request.event.DataSourceEvent;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import junit.framework.TestCase;
/**
 * Classe de test de {@link RequestComboBoxTest}.
 */
public class RequestComboBoxTest extends TestCase {
    private RequestComboBox requestComboBox;


    @Override
    protected void setUp() {
        requestComboBox = new RequestComboBox();
    }


    public void test_setDataSource() {
        Result loadResult = buildSimpleResult("LABEL", new String[]{"b", "a"});

        requestComboBox.setModelFieldName("LABEL");
        ListDataSource dataSource = new ListDataSource();
        dataSource.setLoadResult(loadResult);
        requestComboBox.setDataSource(dataSource);

        assertModelContent(new String[]{"a", "b"}, requestComboBox);
    }


    public void test_comboBoxItemsOrder() {
        Result loadResult = buildSimpleResult("LABEL", new String[]{"b", "a"});

        requestComboBox.setModelFieldName("LABEL");
        requestComboBox.getDataSource().setLoadResult(loadResult);

        assertModelContent(new String[]{"a", "b"}, requestComboBox);
    }


    public void test_comboBoxItemsOrder_notCaseSensitive() {
        Result loadResult = buildSimpleResult("LABEL", new String[]{"B", "a"});

        requestComboBox.setModelFieldName("LABEL");
        requestComboBox.getDataSource().setLoadResult(loadResult);

        assertModelContent(new String[]{"a", "B"}, requestComboBox);
    }


    public void test_comboBoxItemsOrder_alreadySorted() {
        Result loadResult = buildSimpleResult("LABEL", new String[]{"a", "b"});

        requestComboBox.setModelFieldName("LABEL");
        requestComboBox.getDataSource().setLoadResult(loadResult);

        assertModelContent(new String[]{"a", "b"}, requestComboBox);
    }


    public void test_comboBoxItemsOrder_resultUnsorted() {
        String[] columns = new String[]{"LABEL", "CODE"};
        String[][] rows = new String[][]{
              {"b", "1"},
              {"a", "2"}
        };
        Result loadResult = buildResult(columns, rows);

        requestComboBox.getDataSource().setLoadResult(loadResult);
        requestComboBox.setModelFieldName("LABEL");

        assertModelContent(new String[]{"a", "b"}, requestComboBox);
    }


    public void test_comboBoxItemsOrder_renderer() {
        String[] columns = new String[]{"LABEL", "CODE"};
        String[][] rows = new String[][]{
              {"b", "2"},
              {"c", "1"}
        };
        Result loadResult = buildResult(columns, rows);

        requestComboBox.getDataSource().setLoadResult(loadResult);
        requestComboBox.setModelFieldName("CODE");
        requestComboBox.setRendererFieldName("LABEL");

        assertModelContent(new String[]{"2", "1"}, requestComboBox);
    }


    public void test_setSortEnabled() {
        Result loadResult = buildSimpleResult("LABEL", new String[]{"b", "a"});

        requestComboBox.setModelFieldName("LABEL");
        requestComboBox.getDataSource().setLoadResult(loadResult);

        assertTrue(requestComboBox.isSortEnabled());

        requestComboBox.setSortEnabled(false);
        assertFalse(requestComboBox.isSortEnabled());
        assertModelContent(new String[]{"b", "a"}, requestComboBox);
    }


    public void test_setCustomComparator() {
        Result loadResult = buildSimpleResult("LABEL", new String[]{"ba", "aa", "I'm the best !"});

        requestComboBox.setModelFieldName("LABEL");
        requestComboBox.setCustomComparator(new Comparator<String>() {
            public int compare(String o1, String o2) {
                return "I'm the best !".equals(o1) ? 1 : o1.compareTo(o2);
            }
        });
        requestComboBox.getDataSource().setLoadResult(loadResult);

        assertModelContent(new String[]{"I'm the best !", "aa", "ba"}, requestComboBox);
    }


    public void test_setContainsNullValue() throws Exception {
        requestComboBox.setContainsNullValue(true);
        requestComboBox.setModelFieldName("LABEL");
        requestComboBox.getDataSource().setLoadResult(buildSimpleResult("LABEL",
                                                                        new String[]{"b", "a"}));

        assertModelContent(new String[]{"null", "a", "b"}, requestComboBox);

        requestComboBox.getDataSource().addRow(toRow("LABEL", "c"));

        assertModelContent(new String[]{"null", "a", "b", "c"}, requestComboBox);
    }


    public void test_setNullValueLabel() {
       String[] columns = new String[]{"LABEL", "CODE"};
        String[][] rows = new String[][]{
              {"b", "2"},
              {"a", "1"}
        };
        requestComboBox.setNullValueLabel("Tout");
        Result loadResult = buildResult(columns, rows);

        requestComboBox.setRendererFieldName("LABEL");
        requestComboBox.setModelFieldName("CODE");
        requestComboBox.getDataSource().setLoadResult(loadResult);
        assertModelContent(new String[]{RequestComboBox.NULL,"1", "2"}, requestComboBox);

        assertEquals("Tout", getRendererForRow(0).getText());
        assertEquals("a", getRendererForRow(1).getText());
        assertEquals("b", getRendererForRow(2).getText());
    }


    public void test_setContainsNullValue_nullAlwaysFirst() {
        requestComboBox.setContainsNullValue(true);
        requestComboBox.setModelFieldName("LABEL");
        requestComboBox.getDataSource().setLoadResult(buildSimpleResult("LABEL",
                                                                        new String[]{"b", "a"}));

        requestComboBox.setSortEnabled(false);

        assertModelContent(new String[]{"null", "b", "a"}, requestComboBox);

        requestComboBox.setSortEnabled(true);

        assertModelContent(new String[]{"null", "a", "b"}, requestComboBox);
    }


    public void test_getSelectedValue() {
        Result loadResult = buildSimpleResult("LABEL", new String[]{"b", "a"});

        requestComboBox.setModelFieldName("LABEL");
        requestComboBox.getDataSource().setLoadResult(loadResult);

        requestComboBox.setSelectedItem("b");

        assertEquals("b", requestComboBox.getSelectedValue("LABEL"));
        assertEquals("b",
                     requestComboBox.getDataSource().getSelectedRow().getFieldValue("LABEL"));
        assertEquals("b", requestComboBox.getToolTipText());
    }


    public void test_getSelectedValueToDisplay() {
        Result loadResult = buildSimpleResult("LABEL", new String[]{"null", "a"});

        requestComboBox.setModelFieldName("LABEL");
        requestComboBox.getDataSource().setLoadResult(loadResult);

        requestComboBox.setSelectedItem("null");

        assertEquals("null", requestComboBox.getSelectedValue("LABEL"));
        assertEquals(RequestComboBox.NULL_LABEL,
                     requestComboBox.getSelectedValueToDisplay("LABEL"));
        assertEquals(" ", requestComboBox.getToolTipText());

        requestComboBox.setSelectedItem("a");

        assertEquals("a", requestComboBox.getSelectedValueToDisplay("LABEL"));
        assertEquals("a", requestComboBox.getToolTipText());
    }


    public void test_renderer() {
        assertEquals("a", requestComboBox.rendererValue("a"));
        assertEquals(RequestComboBox.NULL_LABEL, requestComboBox.rendererValue("null"));
        assertEquals("", requestComboBox.rendererValue(""));
    }


    public void test_getValueAt() {
        String[] columns = new String[]{"LABEL", "CODE"};
        String[][] rows = new String[][]{
              {"b", "1"},
              {"a", "2"}
        };
        Result loadResult = buildResult(columns, rows);

        requestComboBox.setModelFieldName("LABEL");
        requestComboBox.getDataSource().setLoadResult(loadResult);

        assertEquals("2", requestComboBox.getValueAt(0, "CODE"));
    }


    public void test_indexTranslationViewAndDataSource()
          throws Exception {
        Result loadResult = buildSimpleResult("LABEL", new String[]{"b", "a"});

        requestComboBox.setModelFieldName("LABEL");
        requestComboBox.getDataSource().setLoadResult(loadResult);

        assertEquals(1, requestComboBox.dataSourceIndexToViewIndex(0));
        assertEquals(0, requestComboBox.viewIndexToDataSourceIndex(1));
    }


    public void test_event() throws Exception {
        Result loadResult = buildSimpleResult("LABEL", new String[]{"b", "a"});

        RequestComboBoxTest.MockListDataListener listener =
              new RequestComboBoxTest.MockListDataListener();
        requestComboBox.getModel().addListDataListener(listener);

        requestComboBox.getDataSource().setLoadResult(loadResult);

        assertEquals(0, listener.getCalledTimes());

        requestComboBox.setModelFieldName("LABEL");

        assertEquals(1, listener.getCalledTimes());
    }


    public void test_getSelectedValue_updateFromDataSource() {
        Result loadResult = buildSimpleResult("LABEL", new String[]{"b", "a"});

        requestComboBox.setModelFieldName("LABEL");
        requestComboBox.getDataSource().setLoadResult(loadResult);

        requestComboBox.getDataSource().setSelectedRow(requestComboBox.getDataSource()
              .getRow(0));

        assertEquals("b", requestComboBox.getSelectedValue("LABEL"));
        assertEquals("b", requestComboBox.getToolTipText());
    }


    public void test_getSelectedValue_notSorted() {
        Result loadResult = buildSimpleResult("LABEL", new String[]{"b", "a"});

        requestComboBox.setModelFieldName("LABEL");
        requestComboBox.setSortEnabled(false);
        requestComboBox.getDataSource().setLoadResult(loadResult);

        requestComboBox.setSelectedItem("b");

        assertEquals("b", requestComboBox.getSelectedValue("LABEL"));
        assertEquals("b", requestComboBox.getToolTipText());
    }


    public void test_comboBoxRenderer() {
        String[] columns = new String[]{"LABEL", "CODE"};
        String[][] rows = new String[][]{
              {"b", "2"},
              {"a", "1"}
        };
        Result loadResult = buildResult(columns, rows);

        requestComboBox.getDataSource().setLoadResult(loadResult);
        requestComboBox.setRendererFieldName("LABEL");
        requestComboBox.setModelFieldName("CODE");
        assertModelContent(new String[]{"1", "2"}, requestComboBox);

        assertEquals("b", getRendererForRow(1).getText());
    }


    public void test_comboBoxSynchronize_model() {
        Map<String, String> fields = new HashMap<String, String>();
        fields.put("CODE", "01");
        fields.put("LABEL", "LBL01");
        final Row newRow = new Row(fields);
        Result result = new Result();
        result.addRow(newRow);

        final ListDataSource dataSource = new ListDataSource();

        class ListenerParasiteSelectionnerPremierElement extends DataSourceAdapter {
            @Override
            public void loadEvent(DataSourceEvent event) {
                dataSource.setSelectedRow(newRow);
            }
        }
        dataSource.addDataSourceListener(new ListenerParasiteSelectionnerPremierElement());

        requestComboBox.setDataSource(dataSource);
        requestComboBox.setModelFieldName("CODE");
        requestComboBox.setRendererFieldName("LABEL");

        dataSource.setLoadResult(result);

        assertEquals(0, requestComboBox.getSelectedIndex());
        assertEquals("LBL01", requestComboBox.getToolTipText());
    }


    public void test_comboBoxUnSynchronize_model() {
        Map<String, String> fields = new HashMap<String, String>();
        fields.put("CODE", "01");
        fields.put("LABEL", "LBL01");
        final Row newRow = new Row(fields);
        Result result = new Result();
        result.addRow(newRow);

        final ListDataSource dataSource = new ListDataSource();

        class ListenerParasiteSelectionnerFauxElement extends DataSourceAdapter {
            @Override
            public void loadEvent(DataSourceEvent event) {
                dataSource.setSelectedRow(new Row());
            }
        }
        dataSource.addDataSourceListener(new ListenerParasiteSelectionnerFauxElement());

        requestComboBox.setDataSource(dataSource);
        requestComboBox.setModelFieldName("CODE");
        requestComboBox.setRendererFieldName("LABEL");

        try {
            dataSource.setLoadResult(result);
        }
        catch (IllegalArgumentException iae) {
            assertEquals("Index incorrecte : -1", iae.getMessage());
        }
        assertEquals(-1, requestComboBox.getSelectedIndex());
        assertEquals(null, requestComboBox.getToolTipText());
    }


    private Result buildSimpleResult(String columnName, String[] values) {
        String[][] rows = new String[values.length][1];
        for (int i = 0; i < values.length; i++) {
            rows[i][0] = values[i];
        }
        return buildResult(new String[]{columnName}, rows);
    }


    private Result buildResult(String[] columnNames, String[][] rows) {
        Result loadResult = new Result();

        for (String[] row : rows) {
            Map<String, String> fields = new HashMap<String, String>();

            for (int columnIndex = 0; columnIndex < columnNames.length; columnIndex++) {
                String columnName = columnNames[columnIndex];
                fields.put(columnName, row[columnIndex]);
            }

            loadResult.addRow(new Row(fields));
        }

        return loadResult;
    }


    private void assertModelContent(Object[] expected, RequestComboBox comboBox) {
        assertEquals(expected.length, comboBox.getItemCount());
        for (int i = 0; i < expected.length; i++) {
            Object expectedAt = expected[i];
            Object elementAt = comboBox.getItemAt(i);
            assertEquals(expectedAt, elementAt);
        }
    }


    private Row toRow(String name, String value) {
        Row row = new Row();
        row.addField(name, value);
        return row;
    }


    private JLabel getRendererForRow(int viewIndex) {
        return (JLabel)requestComboBox.getRenderer().getListCellRendererComponent(new JList(),
                                                                                  "notUsed", viewIndex, false,
                                                                                  false);
    }


    public class MockListDataListener implements ListDataListener {
        private long calledTimes = 0;


        public void intervalAdded(ListDataEvent event) {
            calledTimes++;
        }


        public void intervalRemoved(ListDataEvent event) {
            calledTimes++;
        }


        public void contentsChanged(ListDataEvent event) {
            calledTimes++;
        }


        public long getCalledTimes() {
            return calledTimes;
        }
    }
}
