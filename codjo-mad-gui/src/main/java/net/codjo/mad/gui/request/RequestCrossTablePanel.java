package net.codjo.mad.gui.request;
import net.codjo.mad.client.request.Row;
import net.codjo.mad.gui.request.util.RequestCrossTableRendererSorter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
/**
 *
 */
public class RequestCrossTablePanel extends JPanel {
    private JTable crossTable;
    private Map columnMap;
    private String[] notEditableColumns;
    private List<RequestCrossTableListener> crossTableListenerList =
        new ArrayList<RequestCrossTableListener>();
    private String[] staticColumnFields;
    private boolean isEmptyCellsEditable = true;
    private Map<String, String> staticColumnFieldsMap;

    public RequestCrossTablePanel() {
        crossTable = new CrossTable();
        setCrossTableName("CrossTable");
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        buildGui();
    }

    protected void buildGui() {
        add(new JScrollPane(crossTable));
    }


    public String getColumnName(int index) {
        return crossTable.getColumnName(index);
    }


    public TableModel getModel() {
        return crossTable.getModel();
    }


    public JTable getTable() {
        return crossTable;
    }


    public TableColumnModel getColumnModel() {
        return crossTable.getColumnModel();
    }


    public int convertFieldNameToModelIndex(String fieldName) {
        JTable table = new JTable(getModel());
        return table.getColumnModel().getColumnIndex(fieldName);
    }


    public int convertColumnIndexToView(int index) {
        return crossTable.convertColumnIndexToView(index);
    }


    public Map getColumnMap() {
        return columnMap;
    }


    public void setCrossTableName(String name) {
        crossTable.setName(name);
    }


    public void bindDataSource(ListDataSource listDataSource, String dynamicColumnField,
        String fieldToMapToDynamicColumnField, String[] hiddenColumns) {
        staticColumnFields = staticColumnFieldsMap.keySet().toArray(new String[staticColumnFieldsMap.size()]);

        initTableModel(listDataSource, dynamicColumnField);
        fillTable(listDataSource, fieldToMapToDynamicColumnField, dynamicColumnField);

        initColumnHeaders();

        initHiddenColumns(hiddenColumns);

        crossTable.getModel().addTableModelListener(new TableModelListener() {
                public void tableChanged(TableModelEvent event) {
                    if (event.getColumn() != TableModelEvent.ALL_COLUMNS) {
                        for (RequestCrossTableListener listener : crossTableListenerList) {
                            listener.cellContentsChanged(event.getFirstRow(),
                                crossTable.getModel().getColumnName(event.getColumn()));
                        }
                    }
                }
            });
    }


    public int getRowIndexbyFieldMap(Map staticFieldValues) {
        int result = -1;
        TableModel model = crossTable.getModel();
        int rowIndex = 0;
        while (result == -1 && rowIndex < model.getRowCount()) {
            int colResult = 0;
            for (int columnIndex = 0; columnIndex < model.getColumnCount(); columnIndex++) {
                String colName = model.getColumnName(columnIndex);
                if (staticFieldValues.containsKey(colName)) {
                    if (staticFieldValues.get(colName).equals(model.getValueAt(rowIndex, columnIndex))) {
                        colResult++;
                    }
                }
            }
            if (colResult == staticFieldValues.size()) {
                result = rowIndex;
            }
            rowIndex++;
        }
        return result;
    }


    public void addCrossTableListener(RequestCrossTableListener requestCrossTableListener) {
        crossTableListenerList.add(requestCrossTableListener);
    }


    public void setCellEditorForGeneratedColumns(TableCellEditor editor) {
        for (Object object : columnMap.entrySet()) {
            Map.Entry<String, Integer> entry = (Map.Entry<String, Integer>)object;

            if (!Arrays.asList(staticColumnFields).contains(entry.getKey())) {
                Integer value = entry.getValue();
                findTableColumn(value).setCellEditor(editor);
            }
        }
    }


    public String[] getGeneratedColumnNames() {
        List<String> generatedColumns = new ArrayList<String>();
        for (Object object : columnMap.keySet()) {
            String key = (String)object;

            if (!Arrays.asList(staticColumnFields).contains(key)) {
                generatedColumns.add(key);
            }
        }
        String[] strings = generatedColumns.toArray(new String[generatedColumns.size()]);
        Arrays.sort(strings);
        return strings;
    }


    public TableColumn findTableColumn(int columnModelIndex) {
        Enumeration enumeration = crossTable.getColumnModel().getColumns();
        for (; enumeration.hasMoreElements();) {
            TableColumn col = (TableColumn)enumeration.nextElement();
            if (col.getModelIndex() == columnModelIndex) {
                return col;
            }
        }
        return null;
    }


    public Object getValueAt(String columnName, int rowIndex) {
        for (int i = 0; i < crossTable.getModel().getColumnCount(); i++) {
            String currentColumnName = crossTable.getModel().getColumnName(i);
            if (columnName.equals(currentColumnName)) {
                return crossTable.getModel().getValueAt(rowIndex, i);
            }
        }
        return null;
    }


    public void setNotEditableColumns(String[] columnFields) {
        Arrays.sort(columnFields);
        notEditableColumns = columnFields;
    }


    public void setCellRenderer(String columnHeader, TableCellRenderer renderer) {
        Enumeration enumeration = crossTable.getColumnModel().getColumns();
        for (; enumeration.hasMoreElements();) {
            TableColumn column = (TableColumn)enumeration.nextElement();
            if (column.getHeaderValue().equals(columnHeader)) {
                column.setCellRenderer(renderer);
                return;
            }
        }
    }


    private void initColumnHeaders() {
        for (Map.Entry<String, String> entry : staticColumnFieldsMap.entrySet()) {
            Integer colIndex = (Integer)columnMap.get(entry.getKey());
            crossTable.getColumnModel().getColumn(colIndex).setHeaderValue(entry.getValue());
        }
    }


    private void initHiddenColumns(String[] hiddenColumns) {
        for (String hiddenColumn : hiddenColumns) {
            int index = (Integer)columnMap.get(hiddenColumn);
            TableColumn column = getColumnByModelIndex(index);
            if (column != null) {
                crossTable.getColumnModel().removeColumn(column);
            }
        }
    }

    public TableColumn getColumnByModelIndex(int modelIndex) {
        for(int i=0; i < crossTable.getColumnCount(); ++i) {
            if(crossTable.getColumnModel().getColumn(i).getModelIndex() == modelIndex) {
                return crossTable.getColumnModel().getColumn(i);
            }
        }
        return null;
    }

    private void fillTable(ListDataSource listDataSource, String fieldToMapToDynamicColumnField,
        String dynamicColumnField) {
        DefaultTableModel model = (DefaultTableModel)crossTable.getModel();

        for (int i = 0; i < listDataSource.getTotalRowCount(); i++) {
            Row row = listDataSource.getRow(i);

            Map staticFieldValues = getStaticFieldValues(row);

            int rowIndex = getRowIndexbyFieldMap(staticFieldValues);

            if (rowIndex == -1) {
                model.addRow(createDefaultRow(row));
                rowIndex = model.getRowCount() - 1;
            }
            String fieldValue = row.getFieldValue(fieldToMapToDynamicColumnField);
            String dynamicFieldValue = row.getFieldValue(dynamicColumnField);
            Integer columnIndex = (Integer)columnMap.get(dynamicFieldValue);

            model.setValueAt(fieldValue, rowIndex, columnIndex.intValue());
        }
    }


    private String[] createDefaultRow(Row row) {
        String[] result = new String[staticColumnFields.length];
        for (int i = 0; i < staticColumnFields.length; i++) {
            String fieldName = staticColumnFields[i];
            result[i] = row.getFieldValue(fieldName);
        }
        return result;
    }


    private Map getStaticFieldValues(Row row) {
        Map<String, String> result = new HashMap<String, String>(staticColumnFields.length);

        for (String staticColumnField : staticColumnFields) {
            result.put(staticColumnField, row.getFieldValue(staticColumnField));
        }
        return result;
    }


    private void initTableModel(ListDataSource listDataSource, String dynamicColumnField) {
        DefaultTableModel tableModel = new DefaultTableModel();

        addColumnsToModel(staticColumnFields, tableModel);

        String[] columns = getColumns(listDataSource, dynamicColumnField);
        addColumnsToModel(columns, tableModel);

        crossTable.setModel(tableModel);

        initColumnMap();
    }


    private void initColumnMap() {
        columnMap = new HashMap(crossTable.getColumnModel().getColumnCount());
        Enumeration enumColumns = crossTable.getColumnModel().getColumns();
        int columnIndex = 0;
        while (enumColumns.hasMoreElements()) {
            TableColumn column = (TableColumn)enumColumns.nextElement();
            columnMap.put(column.getHeaderValue(), columnIndex);
            columnIndex++;
        }
    }


    private String[] getColumns(ListDataSource listDataSource, String columnField) {
        SortedSet<String> columnSet = new TreeSet<String>();
        for (int i = 0; i < listDataSource.getTotalRowCount(); i++) {
            Row row = listDataSource.getRow(i);
            columnSet.add(row.getFieldValue(columnField));
        }
        return columnSet.toArray(new String[columnSet.size()]);
    }


    private void addColumnsToModel(String[] columnFields, DefaultTableModel tableModel) {
        for (String column : columnFields) {
            tableModel.addColumn(column);
        }
    }


    public void setEmptyCellsCanBeEdited(boolean editable) {
        isEmptyCellsEditable = editable;
    }


    public boolean isEmptyCellsEditable() {
        return isEmptyCellsEditable;
    }


    public void setStaticColumnFieldsMap(Map<String, String> staticColumnFieldsMap) {
        this.staticColumnFieldsMap = staticColumnFieldsMap;
    }

    public interface RequestCrossTableListener {
        void cellContentsChanged(int rowIndex, String column);
    }

    private class CrossTable extends JTable {
        private RequestCrossTableRendererSorter headerRendererSorter =
            new RequestCrossTableRendererSorter(this);

        @Override
        public boolean isCellEditable(int row, int column) {
            if (notEditableColumns == null || notEditableColumns.length == 0) {
                return false;
            }
            String fieldName = getColumnModel().getColumn(column).getHeaderValue().toString();

            int result = Arrays.binarySearch(notEditableColumns, fieldName);

            return result < 0 && cellCanBeEdited(row, column);
        }


        private boolean cellCanBeEdited(int row, int column) {
            return isEmptyCellsEditable() || !"".equals(getValueAt(row, column));
        }


        @Override
        public Object getValueAt(int row, int column) {
            if (super.getValueAt(row, column) == null) {
                return "";
            }
            return super.getValueAt(row, column);
        }
    }
}
