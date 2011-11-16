package net.codjo.mad.gui.request;
import net.codjo.gui.toolkit.table.TableMap;
import net.codjo.gui.toolkit.table.TableRendererSorter;
import net.codjo.gui.toolkit.util.ErrorDialog;
import net.codjo.mad.client.request.FieldsList;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.client.request.RequestSender;
import net.codjo.mad.client.request.Result;
import net.codjo.mad.client.request.Row;
import net.codjo.mad.gui.framework.GuiContext;
import net.codjo.mad.gui.request.event.DataSourceAdapter;
import net.codjo.mad.gui.request.event.DataSourceEvent;
import net.codjo.mad.gui.request.factory.RequestFactory;
import net.codjo.mad.gui.request.factory.SelectFactory;
import net.codjo.mad.gui.request.util.PreferenceRenderer;
import net.codjo.mad.gui.request.util.RequestHelper;
import net.codjo.mad.gui.request.util.RequestTableRendererSorter;
import net.codjo.mad.gui.request.util.comparators.RowComparator;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
/**
 * Table permettant d'afficher une requete.
 */
public class RequestTable extends JTable {
    public static final String EDITABLE_PROPERTY = "editable";
    public static final int ROW_COLUMN = -1;
    static final int MIN_WIDTH = 15;
    static final int MAX_WIDTH = 1000;
    private final TableModelChangedListener tableModelChangedListener = new TableModelChangedListener();
    private final ListDataSource dataSource;
    private final RequestTableModel requestTableModel;
    private Preference preference = new Preference();
    private TableRendererSorter modelSorter = null;
    private RowComparator rowComparator = null;
    private ScrollBarPolicy horizontalScrollBarPolicy = ScrollBarPolicy.HORIZONTAL_SCROLLBAR_DO_NOT_SHOW;
    private AutocommitListener autocommitListener;

    public enum ScrollBarPolicy {
        HORIZONTAL_SCROLLBAR_DO_NOT_SHOW,
        HORIZONTAL_SCROLLBAR_SHOW_IF_NEEDED
    }


    public RequestTable() {
        this(new ListDataSource());
    }


    public RequestTable(RequestTableModel requestTableModel) {
        this(new ListDataSource(), requestTableModel);
    }


    public RequestTable(ListDataSource listDataSource) {
        this(listDataSource, new RequestTableModel());
    }


    public RequestTable(ListDataSource listDataSource, RequestTableModel requestTableModel) {
        this.dataSource = listDataSource;
        this.requestTableModel = requestTableModel;

        SelectedRowUpdater selectedRowUpdater = new SelectedRowUpdater();
        dataSource.addPropertyChangeListener(DataSource.SELECTED_ROW_PROPERTY, selectedRowUpdater);
        getSelectionModel().addListSelectionListener(selectedRowUpdater);

        EditorAutomaticCloser editorAutomaticCloser = new EditorAutomaticCloser();
        dataSource.addDataSourceListener(editorAutomaticCloser);
        requestTableModel.addTableModelListener(editorAutomaticCloser);

        requestTableModel.setListDataSource(dataSource);

        setDefaultRenderer(String.class, new PreferenceRenderer());

        setModel(requestTableModel);
        installModelSorter();
    }


    public void setRowComparator(final RowComparator rowComparator) {
        this.rowComparator = rowComparator;
        installModelSorter();
    }


    public void disableSorter() {
        //noinspection ComparatorMethodParameterNotUsed
        setRowComparator(new RowComparator() {
            public int compare(Row row1, Row row2) {
                return 0;
            }
        });
    }


    public void sort() {
        modelSorter.sort();
        requestTableModel.fireTableDataChanged();
    }


    public void setCellEditor(final String fieldName, final TableCellEditor editor) {
        getTableColum(fieldName).setCellEditor(editor);
    }


    public void setCellRenderer(final String fieldName, final TableCellRenderer renderer) {
        getTableColum(fieldName).setCellRenderer(renderer);
    }


    public TableCellRenderer getCellRenderer(final String fieldName) {
        return getTableColum(fieldName).getCellRenderer();
    }


    public void setCurrentPage(int index) {
        dataSource.setCurrentPage(index);
    }


    public void setDefaultValue(String fieldName, String value) {
        dataSource.setDefaultValue(fieldName, value);
    }


    public void setEditable(boolean editable) {
        boolean old = requestTableModel.isEditable();
        requestTableModel.setEditable(editable);
        firePropertyChange(EDITABLE_PROPERTY, old, editable);
    }


    public void setEditable(boolean editable, String[] notEditableColsName) {
        boolean old = requestTableModel.isEditable();
        requestTableModel.setEditable(editable, notEditableColsName);
        updatePreferenceFactory(notEditableColsName);
        firePropertyChange(EDITABLE_PROPERTY, old, editable);
    }


    public void setEditable(String... columnName) {
        String[] notEditableColumns = new String[requestTableModel.getColumnCount()];
        List<String> editableColumns = Arrays.asList(columnName);
        for (int i = 0; i < requestTableModel.getColumnCount(); i++) {
            String currentColumn = requestTableModel.getColumnField(i);
            if (!editableColumns.contains(currentColumn)) {
                notEditableColumns[i] = currentColumn;
            }
        }
        setEditable(true, notEditableColumns);
    }


    public void setLoadResult(Result loadResult) {
        dataSource.setLoadResult(loadResult);
    }


    public void setPageSize(int pageSize) {
        getDataSource().setPageSize(pageSize);
    }


    public void setPreference(String preferenceId) {
        setPreference(PreferenceFactory.getPreference(preferenceId));
    }


    /**
     * Attention : eviter de maintenir une instance de RequestTable et d'invoquer "trop" setPreference tout au
     * long de son cycle de vie car la réinitialisation des model appliqués dessus est mal faite (Les
     * instances des TableModelEvent sont maintenues et donc la liste grossit au point de générer des fuites
     * mémoire.
     */
    public void setPreference(Preference preference) {
        this.preference = preference;
        setName(preference.getId());

        setModel(buildModel());
        requestTableModel.initializeModel(RequestTableModel.createColumnFields(preference));
        setModel(requestTableModel);

        installModelSorter();
    }


    @Deprecated
    public void setRequestSender(RequestSender requestSender) {
        dataSource.setRequestSender(requestSender);
    }


    public void setResult(Result result) {
        dataSource.setLoadResult(result);
    }


    public void setSelectFactory(RequestFactory selectAll) {
        preference.setSelectAll(selectAll);
        dataSource.setLoadFactory(selectAll);
    }


    public void setSelectFactoryId(String id) {
        dataSource.setLoadFactory(new SelectFactory(id));
    }


    public void setSelector(FieldsList selectors) {
        this.dataSource.setSelector(selectors);
    }


    /**
     * Retourne les lignes sélectionnées de la table.
     *
     * @return les row selectionnées
     */
    public Row[] getAllSelectedDataRows() {
        int[] idx = getSelectedRows();
        List<Row> rows = new ArrayList<Row>();
        for (int anIdx : idx) {
            rows.add((Row)getValueAt(anIdx, ROW_COLUMN));
        }
        return rows.toArray(new Row[rows.size()]);
    }


    public String getColumnValue(int row, String fieldName) {
        return ((Row)getValueAt(row, ROW_COLUMN)).getFieldValue(fieldName);
    }


    public int getCurrentPage() {
        return dataSource.getCurrentPage();
    }


    public ListDataSource getDataSource() {
        return dataSource;
    }


    public boolean isEditable() {
        return requestTableModel.isEditable();
    }


    /**
     * Retourne la première ligne sélectionnée de la table.
     *
     * @return La premiere ligne selectionnée
     */
    public Row getFirstSelectedDataRow() {
        int idx = getSelectedRow();
        if (idx == -1) {
            return null;
        }
        else {
            return (Row)getValueAt(idx, ROW_COLUMN);
        }
    }


    public Result getLoadResult() {
        return dataSource.getLoadResult();
    }


    public int getPage() {
        return getDataSource().getPageSize();
    }


    public Preference getPreference() {
        return preference;
    }


    @Deprecated
    public RequestSender getRequestSender() {
        return dataSource.getRequestSender();
    }


    public RequestFactory getSelectFactory() {
        return dataSource.getLoadFactory();
    }


    public String getSelectedFieldValue(String fieldName) {
        return getFirstSelectedDataRow().getFieldValue(fieldName);
    }


    public DetailDataSource getSelectedRowDataSource(GuiContext guiContext) {
        return RequestHelper.newDataSource(guiContext,
                                           getFirstSelectedDataRow(),
                                           getLoadResult(),
                                           getPreference());
    }


    public FieldsList getSelectors() {
        return this.dataSource.getSelector();
    }


    public void cancelAllEditors() {
        for (int i = 0; i < getColumnCount(); i++) {
            TableCellEditor tce = getColumn(getColumnName(i)).getCellEditor();
            if (tce != null) {
                tce.cancelCellEditing();
            }
        }
    }


    public boolean hasNextPage() {
        return dataSource.hasNextPage();
    }


    public boolean hasPreviousPage() {
        return dataSource.hasPreviousPage();
    }


    public void load() throws RequestException {
        dataSource.load();
    }


    public void loadNextPage() throws RequestException {
        dataSource.loadNextPage();
    }


    public void loadPreviousPage() throws RequestException {
        dataSource.loadPreviousPage();
    }


    public int convertFieldNameToViewIndex(String fieldName) {
        return convertColumnIndexToView(preference.getColumnIndex(fieldName));
    }


    public int convertFieldNameToModelIndex(String fieldName) {
        return requestTableModel.getColunmIndex(fieldName);
    }


    public int convertRowIndexToModelIndex(int viewRowIndex) {
        return modelSorter.getConvertedIndex(viewRowIndex);
    }


    public void showCell(int row, int column) {
        Rectangle rect = getCellRect(row, column, true);
        scrollRectToVisible(rect);
    }


    public void scrollToLastRow() {
        showCell(getRowCount() - 1, 0);
    }


    public ScrollBarPolicy getHorizontalScrollBarPolicy() {
        return horizontalScrollBarPolicy;
    }


    public void setHorizontalScrollBarPolicy(ScrollBarPolicy horizontalScrollBarPolicy) {
        this.horizontalScrollBarPolicy = horizontalScrollBarPolicy;
        updateSrollBarPolicy();
    }


    public void setAutoCommit(boolean autocommit, String... requiredFields) {
        if (autocommit) {
            if (null != autocommitListener) {
                throw new IllegalStateException("Autocommit listener is already set!");
            }
            for (String field : requiredFields) {
                try {
                    getTableColum(field);
                } catch (IndexOutOfBoundsException e) {
                    throw new IllegalArgumentException("Unknown field required by autocommit: "+field);
                }
            }
            autocommitListener = new AutocommitListener(this, requiredFields);
            dataSource.addPropertyChangeListener(ListDataSource.UPDATED_ROW_PROPERTY, autocommitListener);
            dataSource.addPropertyChangeListener(ListDataSource.REMOVED_ROW_PROPERTY, autocommitListener);
        } else {
            if (null == autocommitListener) {
                throw new IllegalStateException("Autocommit listener is already cleared!");
            }
            dataSource.removePropertyChangeListener(ListDataSource.UPDATED_ROW_PROPERTY, autocommitListener);
            dataSource.removePropertyChangeListener(ListDataSource.REMOVED_ROW_PROPERTY, autocommitListener);
            autocommitListener = null;
        }
    }


    protected RequestTableModel buildModel() {
        return new RequestTableModel();
    }


    private void updateSrollBarPolicy() {
        if (horizontalScrollBarPolicy == ScrollBarPolicy.HORIZONTAL_SCROLLBAR_SHOW_IF_NEEDED
            && getColumnModel().getTotalColumnWidth() > getSize().getWidth()) {
            setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        }
        else {
            setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        }
    }


    private void installModelSorter() {
        if (modelSorter != null) {
            removePropertyChangeListener(tableModelChangedListener);
            TableModel oldTableModel = modelSorter.getModel();
            modelSorter.removeMouseListenerToHeaderInTable(this);
            modelSorter.setModel(null);
            modelSorter = null;

            if (getModel() instanceof TableRendererSorter) {
                setModel(oldTableModel);
            }
        }

        if (rowComparator != null) {
            modelSorter = new TableRendererSorter(this) {
                @Override
                public int compare(int row1, int row2) {
                    return rowComparator.compare(dataSource.getRow(row1), dataSource.getRow(row2));
                }
            };
        }
        else {
            modelSorter = new RequestTableRendererSorter(this);
            modelSorter.addMouseListenerToHeaderInTable(this);
        }
        addPropertyChangeListener(tableModelChangedListener);

        setModel(modelSorter);
    }


    private void setColumnsSize() {
        for (int i = 0; i < getPreference().getColumns().size(); i++) {
            Column columnPreference = (getPreference().getColumns().get(i));
            int minSize = columnPreference.getMinSize();
            int maxSize = columnPreference.getMaxSize();
            int preferredSize = columnPreference.getPreferredSize();

            if (i < getColumnModel().getColumnCount()) {
                TableColumn column = getColumnModel().getColumn(i);
                setColumnPreferredWidth(column, preferredSize);
                setColumnMinWidth(column, minSize);
                setColumnMaxWidth(column, maxSize);
            }
        }
        modelSorter.changeHeaderRenderer(this);
        updateSrollBarPolicy();
    }


    private void setColumnPreferredWidth(TableColumn column, int preferredWidth) {
        if (preferredWidth != 0) {
            column.setMinWidth(MIN_WIDTH);
            column.setMaxWidth(MAX_WIDTH);
            column.setPreferredWidth(preferredWidth);
            column.setWidth(column.getPreferredWidth());
        }
    }


    private void setColumnMaxWidth(TableColumn column, int maxSize) {
        if (maxSize != 0) {
            column.setMaxWidth(maxSize);
        }
    }


    private void setColumnMinWidth(TableColumn column, int minSize) {
        if (minSize != 0) {
            column.setMinWidth(minSize);
        }
    }


    private void updatePreferenceFactory(String[] notEditableColsName) {
        if (preference.getUpdate() != null) {
            preference.getUpdate().setExcludedFieldList(notEditableColsName);
        }
        if (preference.getInsert() != null) {
            preference.getInsert().setExcludedFieldList(notEditableColsName);
        }
        if (preference.getSelectAll() != null) {
            preference.getSelectAll().setExcludedFieldList(notEditableColsName);
        }
        if (preference.getSelectByPk() != null) {
            preference.getSelectByPk().setExcludedFieldList(notEditableColsName);
        }
        if (preference.getDelete() != null) {
            preference.getDelete().setExcludedFieldList(notEditableColsName);
        }
    }


    private TableColumn getTableColum(String fieldName) {
        return getColumnModel().getColumn(convertFieldNameToViewIndex(fieldName));
    }


    private class EditorAutomaticCloser extends DataSourceAdapter implements TableModelListener {
        @Override
        public void beforeLoadEvent(DataSourceEvent event) {
            if (!isEditable()) {
                return;
            }
            cancelAllEditors();
        }


        @Override
        public void beforeSaveEvent(DataSourceEvent event) {
            if (!isEditable()) {
                return;
            }
            closeAllEditors();
        }


        public void tableChanged(TableModelEvent event) {
            if (!isEditable()) {
                return;
            }
            requestTableModel.removeTableModelListener(this);
            cancelAllEditors();
            requestTableModel.addTableModelListener(this);
        }


        private void closeAllEditors() {
            for (int i = 0; i < getColumnCount(); i++) {
                TableCellEditor tce = getColumn(getColumnName(i)).getCellEditor();
                if (tce != null) {
                    tce.stopCellEditing();
                }
            }
        }
    }

    private class SelectedRowUpdater implements ListSelectionListener, PropertyChangeListener {
        private boolean isUpdating = false;


        public void propertyChange(PropertyChangeEvent evt) {
            if (isUpdating) {
                return;
            }
            isUpdating = true;
            try {
                Row selectedRow = dataSource.getSelectedRow();
                if (selectedRow == null) {
                    clearSelection();
                }
                else {
                    for (int i = 0; i < getRowCount(); i++) {
                        Row row = (Row)getValueAt(i, ROW_COLUMN);
                        if (selectedRow.equals(row)) {
                            setRowSelectionInterval(i, i);
                        }
                    }
                }
            }
            finally {
                isUpdating = false;
            }
        }


        public void valueChanged(ListSelectionEvent event) {
            if (isUpdating) {
                return;
            }
            isUpdating = true;
            try {
                if (event.getValueIsAdjusting()) {
                    return;
                }

                dataSource.setSelection(new SelectionDataSource(getAllSelectedDataRows()));
            }
            finally {
                isUpdating = false;
            }
        }
    }

    private class TableModelChangedListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent event) {
            if ("model".equals(event.getPropertyName())) {
                if (isModelSorterInstalled(event)) {
                    reInitPreference();
                }
                else {
                    installModelSorter();
                }
            }
        }


        private boolean isModelSorterInstalled(PropertyChangeEvent event) {
            return event.getNewValue() instanceof TableMap;
        }


        private void reInitPreference() {
            if (dataSource.getEntityName() == null) {
                dataSource.setEntityName(preference.getEntity());
            }

            updatePreferenceFactory(requestTableModel.getNotEditableColsName());

            int visibleColunmsSize = preference.getColumns().size();
            int hiddenColunmsSize = preference.getHiddenColumns().size();
            String[] columns = new String[visibleColunmsSize + hiddenColunmsSize];
            for (int i = 0; i < visibleColunmsSize; i++) {
                columns[i] = preference.getColumns().get(i).getFieldName();
            }
            for (int i = 0; i < hiddenColunmsSize; i++) {
                columns[i + visibleColunmsSize] = preference.getHiddenColumns().get(i).getFieldName();
            }
            dataSource.setColumns(columns);
            dataSource.setLoadFactory(preference.getSelectAll());
            dataSource.setDeleteFactory(preference.getDelete());
            dataSource.setUpdateFactory(preference.getUpdate());
            dataSource.setInsertFactory(preference.getInsert());
            setColumnsSize();

            for (Column hiddenColum : preference.getHiddenColumns()) {
                try {
                    removeColumn(getColumn(hiddenColum.getLabel()));
                }
                catch (IllegalArgumentException e) {
                    // lorsque la colonne a déjà été supprimée de la table
                    ;
                }
            }
        }
    }

    private class AutocommitListener implements PropertyChangeListener {
        private RequestTable table;
        private String[] fields;


        private AutocommitListener(RequestTable table, String... fields) {
            this.table = table;
            this.fields = fields;
        }


        public void propertyChange(PropertyChangeEvent evt) {
            if (ListDataSource.UPDATED_ROW_PROPERTY.equals(evt.getPropertyName())
                && table.getDataSource().hasBeenUpdated() && 0 <= table.getEditingRow()) {
                for (String field : fields) {
                    Object required = table.getValueAt(table.getEditingRow(),
                                                       table.convertFieldNameToViewIndex(field));
                    if (ListDataSource.NULL.equals(String.valueOf(required))) {
                        return;
                    }
                }
                autocommit();
            } else if (ListDataSource.REMOVED_ROW_PROPERTY.equals(evt.getPropertyName())) {
                autocommit();
            }
        }


        private void autocommit() {
            try {
                table.getDataSource().save();
            } catch (RequestException e) {
                ErrorDialog.show(SwingUtilities.getWindowAncestor(table), "Could not autocommit!", e);
            }
        }
    }
}
