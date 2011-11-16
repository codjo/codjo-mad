package net.codjo.mad.gui.request;
import net.codjo.mad.gui.request.event.DataSourceAdapter;
import net.codjo.mad.gui.request.event.DataSourceEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.table.AbstractTableModel;
/**
 * Modele de table representant une table.
 *
 * @author $Author: gaudefr $
 * @version $Revision: 1.9 $
 */
public class RequestTableModel extends AbstractTableModel {
    private AutomaticLoadListener automaticLoad = new AutomaticLoadListener();
    private boolean editable = false;
    private List<String> notEditableColsName = new ArrayList<String>();
    private ListDataSource listDataSource;
    private ColumnDescriptor[] columnFields = new ColumnDescriptor[0];


    public RequestTableModel() {
    }


    public void setEditable(boolean editable) {
        setEditable(editable, new String[]{});
    }


    public void setEditable(boolean editable, String[] notEditableColsName) {
        this.editable = editable;
        this.notEditableColsName = Arrays.asList(notEditableColsName);
    }


    public void setListDataSource(ListDataSource listDataSource) {
        if (this.listDataSource != null) {
            this.listDataSource.removeDataSourceListener(automaticLoad);
            this.listDataSource.removePropertyChangeListener(ListDataSource.CONTENT_PROPERTY, automaticLoad);
        }

        this.listDataSource = listDataSource;
        this.fireTableDataChanged();

        if (this.listDataSource != null) {
            this.listDataSource.addDataSourceListener(automaticLoad);
            this.listDataSource.addPropertyChangeListener(ListDataSource.CONTENT_PROPERTY, automaticLoad);
        }
    }


    /**
     * Do not use.
     *
     * @see #initializeModel(net.codjo.mad.gui.request.RequestTableModel.ColumnDescriptor[])
     * @deprecated old fashioned
     */
    @Deprecated
    public void setPreference(Preference preference) {
        initializeModel(createColumnFields(preference));
    }


    public void initializeModel(ColumnDescriptor[] fields) {
        this.columnFields = fields;
        this.fireTableStructureChanged();
    }


    public static ColumnDescriptor[] createColumnFields(Preference preference) {
        List<Column> columns = preference.getColumns();
        List<Column> hiddenColumns = preference.getHiddenColumns();
        ColumnDescriptor[] columnFields;
        columnFields = new ColumnDescriptor[columns.size() + hiddenColumns.size()];

        for (int i = 0; i < columns.size(); i++) {
            Column column = columns.get(i);
            columnFields[i] =
                  new ColumnDescriptor(column.getFieldName(), column.getLabel());
        }
        for (int i = 0; i < hiddenColumns.size(); i++) {
            Column column = hiddenColumns.get(i);
            if (column.getLabel() == null) {
                columnFields[i + columns.size()] =
                      new ColumnDescriptor(column.getFieldName());
            }
            else {
                columnFields[i + columns.size()] =
                      new ColumnDescriptor(column.getFieldName(), column.getLabel());
            }
        }
        return columnFields;
    }


    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (!isCellEditable(rowIndex, columnIndex)) {
            throw new IllegalStateException("Impossible de modifier: "
                                            + getColumnName(columnIndex)
                                            + " la colonne est en lecture seule.");
        }
        listDataSource.setValue(rowIndex, listDataSource.getColumns()[columnIndex],
                                aValue.toString());
        fireTableCellUpdated(rowIndex, columnIndex);
    }


    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if (!isEditable()) {
            return false;
        }
        String fieldName = columnFields[columnIndex].getFieldName();

        return !notEditableColsName.contains(fieldName)
               && (listDataSource.getLoadResult().getPrimaryKeys() == null
                   || listDataSource.isAddedRow(rowIndex)
                   || !listDataSource.getLoadResult().getPrimaryKeys().contains(fieldName));
    }


    @Override
    public Class getColumnClass(int columnIndex) {
        return String.class;
    }


    public int getColumnCount() {
        return columnFields.length;
    }


    @Override
    public String getColumnName(int column) {
        return columnFields[column].getLabel();
    }


    public String getColumnField(int column) {
        return columnFields[column].getFieldName();
    }


    public boolean isEditable() {
        return editable;
    }


    public int getRowCount() {
        if (this.listDataSource != null) {
            return listDataSource.getRowCount();
        }
        else {
            return 0;
        }
    }


    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == RequestTable.ROW_COLUMN) {
            return listDataSource.getRow(rowIndex);
        }
        else {
            return listDataSource.getValueAt(rowIndex, columnFields[columnIndex].getFieldName());
        }
    }


    String[] getNotEditableColsName() {
        return notEditableColsName.toArray(new String[notEditableColsName.size()]);
    }


    int getColunmIndex(String fieldName) {
        for (int i = 0; i < columnFields.length; i++) {
            ColumnDescriptor columnField = columnFields[i];

            if (fieldName.equals(columnField.getFieldName())) {
                return i;
            }
        }
        throw new IllegalArgumentException("La colonne '" + fieldName + "' n'existe pas");
    }


    public static class ColumnDescriptor {
        private final String fieldName;
        private final String label;


        ColumnDescriptor(String fieldName) {
            this(fieldName, fieldName);
        }


        ColumnDescriptor(String fieldName, String label) {
            this.fieldName = fieldName;
            this.label = label;
        }


        public String getFieldName() {
            return fieldName;
        }


        public String getLabel() {
            return label;
        }
    }

    private class AutomaticLoadListener extends DataSourceAdapter
          implements PropertyChangeListener {
        @Override
        public void loadEvent(DataSourceEvent event) {
            fireTableDataChanged();
        }


        public void propertyChange(PropertyChangeEvent evt) {
            fireTableDataChanged();
        }
    }
}
