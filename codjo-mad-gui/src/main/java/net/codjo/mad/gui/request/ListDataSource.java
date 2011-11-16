package net.codjo.mad.gui.request;
import net.codjo.mad.client.request.FieldsList;
import net.codjo.mad.client.request.Request;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.client.request.Result;
import net.codjo.mad.client.request.Row;
import net.codjo.mad.client.request.SelectRequest;
import net.codjo.mad.gui.request.event.DataSourceSupport;
import net.codjo.mad.gui.request.factory.DeleteFactory;
import net.codjo.mad.gui.request.factory.InsertFactory;
import net.codjo.mad.gui.request.factory.RequestFactory;
import net.codjo.mad.gui.request.factory.UpdateFactory;
import net.codjo.mad.gui.request.undo.AddRowUndoableEdit;
import net.codjo.mad.gui.request.undo.DataListUndoable;
import net.codjo.mad.gui.request.undo.RemoveRowUndoableEdit;
import net.codjo.mad.gui.request.undo.SnapshotEdit;
import net.codjo.mad.gui.request.undo.UpdateRowUndoableEdit;
import net.codjo.mad.gui.request.util.MultiRequestsHelper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEditSupport;
import org.apache.log4j.Logger;

public class ListDataSource extends AbstractDataSource {
    private static final Logger LOGGER = Logger.getLogger(ListDataSource.class);
    public static final String CONTENT_PROPERTY = "datasource.content";
    public static final String ADDED_ROW_PROPERTY = "datasource.added.row";
    public static final String REMOVED_ROW_PROPERTY = "datasource.removed.row";
    public static final String UPDATED_ROW_PROPERTY = "datasource.updated.row";

    public static final int PAGE_SIZE = 1000;
    private RowFiller rowFiller;
    private List<Row> addedRows = new ArrayList<Row>();
    private String[] columns = {};
    private int currentPage = 1;
    private Result loadResult = new Result();
    private int pageSize = PAGE_SIZE;
    private List<Row> removedRows = new ArrayList<Row>();
    private UndoInterface undoInterface = new UndoInterface();
    private UndoableEditSupport undoSupport = new SnapshotUndoableEditSupport();
    private List<Row> updatedRows = new ArrayList<Row>();
    private RequestFactory deleteFactory;
    private RequestFactory insertFactory;
    private RequestFactory updateFactory;


    public ListDataSource() {
        setLoadManager(new DefaultLoadManager());
        undoSupport.addUndoableEditListener(new UndoRedoWrapper());
    }


    public void setColumns(String[] columns) {
        this.columns = columns;
    }


    public void setCurrentPage(int idx) {
        currentPage = idx;
    }


    public void setDeleteFactory(RequestFactory deleteFactory) {
        this.deleteFactory = deleteFactory;
    }


    public void setDeleteFactoryId(String deleteId) {
        setDeleteFactory(new DeleteFactory(deleteId));
    }


    public void setInsertFactory(RequestFactory insertFactory) {
        this.insertFactory = insertFactory;
    }


    public void setInsertFactoryId(String insertId) {
        setInsertFactory(new InsertFactory(insertId));
    }


    public void setLoadResult(Result loadResult) {
        cancelEditMode();

        this.loadResult = loadResult;
        fireLoadEvent(loadResult);
    }


    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }


    public void setRowFiller(RowFiller filler) {
        this.rowFiller = filler;
    }


    public void setUpdateFactory(RequestFactory updateFactory) {
        this.updateFactory = updateFactory;
    }


    public void setUpdateFactoryId(String deleteId) {
        setUpdateFactory(new UpdateFactory(deleteId));
    }


    public void setValue(int row, String columnId, String value) {
        String old = getLoadResult().getValue(row, columnId);
        if (old.equals(value)) {
            return;
        }
        Row roww = setValueImpl(row, columnId, value);
        undoSupport.postEdit(new UpdateRowUndoableEdit(undoInterface, roww, columnId, old, value));
    }


    public Row[] getAddedRow() {
        return this.addedRows.toArray(new Row[this.addedRows.size()]);
    }


    public String[] getColumns() {
        return columns;
    }


    public void declare(final String fieldName) {
        String[] newColumns = new String[columns.length + 1];
        boolean doesContain = false;
        for (int i = 0; i < columns.length; i++) {
            newColumns[i] = columns[i];
            if (columns[i].equals(fieldName)) {
                doesContain = true;
            }
        }
        if (!doesContain) {
            newColumns[columns.length] = fieldName;
            setColumns(newColumns);
        }
    }


    public int getTotalRowCount() {
        if (loadResult != null) {
            if (loadResult.getTotalRowCount() == loadResult.getRowCount()) {
                return loadResult.getTotalRowCount();
            }
            return loadResult.getTotalRowCount() + addedRows.size() - removedRows.size();
        }
        else {
            return 0;
        }
    }


    public void startSnapshotMode() {
        undoSupport.beginUpdate();
    }


    public void stopSnapshotMode() {
        undoSupport.endUpdate();
    }


    public int getCurrentPage() {
        return currentPage;
    }


    public Result getLoadResult() {
        return loadResult;
    }


    public int getPageSize() {
        return pageSize;
    }


    public Row[] getRemovedRow() {
        return this.removedRows.toArray(new Row[this.removedRows.size()]);
    }


    public Row getRow(int idx) {
        return getLoadResult().getRows().get(idx);
    }


    public int getRowCount() {
        if (loadResult == null) {
            return 0;
        }
        return loadResult.getRowCount();
    }


    /* @deprecated - cette méthode ne fonctionne pas si des lignes ont été ajoutées ou retirées ! */
    public int getSelectedRowIndex() {
        if (getSelectedRow() == null) {
            return -1;
        }
        return getLoadResult().getRowIndex(getSelectedRow());
    }


    public Row[] getUpdatedRow() {
        return this.updatedRows.toArray(new Row[this.updatedRows.size()]);
    }


    public String getValueAt(int rowIdx, String fieldName) {
        if (loadResult == null) {
            throw new IllegalArgumentException("pas de résultat");
        }
        return loadResult.getValue(rowIdx, fieldName);
    }


    /**
     * Ajoute une nouvelle ligne au dataSource. Le dataSource passe en mode Edition.
     *
     * @param newRow La nouvelle ligne
     *
     * @return l'indice de la ligne dans la datasource.
     */
    public int addRow(Row newRow) {
        int idx = addRowImpl(newRow);
        undoSupport.postEdit(new AddRowUndoableEdit(undoInterface, newRow, idx));
        callRowFiller(newRow, idx);
        return idx;
    }


    public void cancelEditMode() {
        addedRows.clear();
        updatedRows.clear();
        removedRows.clear();
    }


    public void clear() {
        setSelectedRow(null);
        setLoadResult(new Result());
    }


    public boolean containsField(int row, String fieldName) {
        return getLoadResult().containsField(row, fieldName);
    }


    public boolean hasBeenUpdated() {
        return !addedRows.isEmpty() || !removedRows.isEmpty() || !updatedRows.isEmpty();
    }


    public boolean hasNextPage() {
        return getLoadResult() != null
               && (currentPage - 1) * pageSize + getLoadResult().getRowCount() < getTotalRowCount();
    }


    public boolean hasPreviousPage() {
        return currentPage > 1;
    }


    public void apply(String fieldName, String fieldValue) {
        for (int rowIdx = 0; rowIdx < getRowCount(); rowIdx++) {
            setValueImpl(rowIdx, fieldName, fieldValue);
        }
    }


    public void loadNextPage() throws RequestException {
        if (hasNextPage()) {
            ++currentPage;
            load();
        }
    }


    public void loadPreviousPage() throws RequestException {
        if (hasPreviousPage()) {
            --currentPage;
            load();
        }
    }


    public Row newRow() {
        return newRowWhithDefaultValues();
    }


    public Row removeRow(int idx) {
        Row row = removeRowImpl(getRow(idx));
        undoSupport.postEdit(new RemoveRowUndoableEdit(undoInterface, row, idx));
        return row;
    }


    public void removeRow(Row row) {
        removeRowImpl(row);
        undoSupport.postEdit(new RemoveRowUndoableEdit(undoInterface, row, getRowIndex(row)));
    }


    public void addSaveRequestTo(MultiRequestsHelper helper) {
        addSubmiterTo(helper, removedRows, deleteFactory,
                      "Pas de factory delete pour la suppression des lignes ");
        addSubmiterTo(helper, updatedRows, updateFactory,
                      "Pas de factory update pour la maj des lignes ");
        addSubmiterTo(helper, addedRows, insertFactory,
                      "Pas de factory insert pour l'ajout des lignes ");
        fireBeforeSaveEvent(helper);
    }


    @Override
    public String toString() {
        return "ListDatasource(" + getLoadResult() + ")";
    }


    protected Request buildLoadRequest() {
        if (getSelector() != null && getLoadFactory().needsSelector()) {
            getLoadFactory().init(new FieldsList(getSelector()));
        }
        Request request = getLoadFactory().buildRequest(buildFields(columns));
        if (request != null) {
            ((SelectRequest)request).setPage(currentPage, pageSize);
        }
        return request;
    }


    private Row setValueImpl(final int row, final String columnId, final String value) {
        String old = getLoadResult().getValue(row, columnId);

        if (old.equals(value)) {
            return getLoadResult().getRow(row);
        }

        getLoadResult().setValue(row, columnId, value);
        Row roww = getLoadResult().getRow(row);
        if (!updatedRows.contains(roww) && !addedRows.contains(roww) && !isExcludedOnUpdate(columnId)) {
            updatedRows.add(roww);
        }

        firePropertyChange(row + "." + columnId, old, value);
        firePropertyChange(UPDATED_ROW_PROPERTY, old, value);
        firePropertyChange(CONTENT_PROPERTY, null, loadResult);

        return roww;
    }


    private boolean isExcludedOnUpdate(String columnId) {
        if (updateFactory != null && updateFactory instanceof UpdateFactory) {
            List<String> excludedFields = ((UpdateFactory)updateFactory).getExcludedFieldList();
            return excludedFields.contains(columnId);
        }
        return false;
    }


    public boolean isAddedRow(Row row) {
        return addedRows.contains(row);
    }


    public boolean isUpdatedRow(Row row) {
        return updatedRows.contains(row);
    }


    public boolean isAddedRow(int rowIdx) {
        return addedRows.contains(getRow(rowIdx));
    }


    private int getRowIndex(Row row) {
        if (getLoadResult() == null) {
            return -1;
        }
        return getLoadResult().getRowIndex(row);
    }


    private int addRowImpl(final Row newRow) {
        if (newRow == null || addedRows.contains(newRow)) {
            throw new IllegalArgumentException();
        }

        if (removedRows.contains(newRow)) {
            removedRows.remove(newRow);
        }
        else {
            addedRows.add(newRow);
        }

        int idx = loadResult.addRow(newRow);
        firePropertyChange(ADDED_ROW_PROPERTY, null, newRow);
        firePropertyChange(CONTENT_PROPERTY, null, loadResult);

        return idx;
    }


    private void addSubmiterTo(MultiRequestsHelper mrh, List<Row> rows,
                               RequestFactory factory, String errorMsg) {
        if (factory == null && !rows.isEmpty()) {
            throw new IllegalStateException(errorMsg + rows);
        }
        else if (factory == null) {
            return;
        }

        for (Row row : rows) {
            mrh.addSubmiter(new LdsSubmiter(factory, row, rows));
        }
    }


    private Map buildFields(String[] attributes) {
        Map<String, Object> fields = new HashMap<String, Object>();
        for (String attribute : attributes) {
            fields.put(attribute, null);
        }
        return fields;
    }


    private void callRowFiller(final Row newRow, final int idx) {
        if (rowFiller != null) {
            rowFiller.fillAddedRow(newRow, idx, this);
        }
    }


    public Set<String> getDistinctNotNullValues(String fieldName) {
        Set<String> distinctValues = new TreeSet<String>();

        List<Row> rows = loadResult.getRows();
        if (rows == null) {
            return distinctValues;
        }
        for (Row row : rows) {
            String value = row.getFieldValue(fieldName);
            if (value != null) {
                distinctValues.add(value);
            }
        }
        return distinctValues;
    }


    @Override
    public void load() throws RequestException {
        cancelEditMode();

        if (columns.length == 0) {
            throw new IllegalStateException("Preference mal configurée : "
                                            + " pas de colonne à afficher");
        }
        getLoadManager().doLoad(getDataSourceSupport());
    }


    private Row removeRowImpl(final Row row) {
        loadResult.removeRow(row);
        if (isAddedRow(row)) {
            addedRows.remove(row);
        }
        else if (isUpdatedRow(row)) {
            updatedRows.remove(row);
            removedRows.add(row);
        }
        else {
            removedRows.add(row);
        }
        firePropertyChange(REMOVED_ROW_PROPERTY, row, null);
        firePropertyChange(CONTENT_PROPERTY, null, loadResult);

        return row;
    }


    public Iterator<Row> rows() {
        final List<Row> rows = getLoadResult().getRows();
        if (rows == null) {
            List<Row> emptyList = Collections.emptyList();
            return emptyList.iterator();
        }
        else {
            return rows.iterator();
        }
    }


    private class DefaultLoadManager implements LoadManager {
        public void doLoad(DataSourceSupport support)
              throws RequestException {
            if (getLoadFactory() == null) {
                setLoadResult(new Result());
                return;
            }

            MultiRequestsHelper helper = new MultiRequestsHelper(getRequestSender());
            ListDataSource.this.addLoadRequestTo(helper);
            helper.sendRequest();
        }


        public void addLoadRequestTo(MultiRequestsHelper helper) {
            LoadSubmiter loadSubmiter = new LoadSubmiter();
            helper.addSubmiter(loadSubmiter);
            fireBeforeLoadEvent(helper);

            // A appeler après fireBeforeLoadEvent pour permettre aux listeners*
            // de modifier la requête qui sera construite par buildLoadRequest()
            loadSubmiter.init(buildLoadRequest());
        }
    }

    private class LoadSubmiter implements RequestSubmiter {
        private Request request;


        public RequestSubmiter init(Request loadRequest) {
            this.request = loadRequest;
            return this;
        }


        public Request buildRequest() {
            return request;
        }


        public void setResult(Result result) {
            setSelectedRow(null);
            setLoadResult(result);
        }
    }

    private class LdsSubmiter implements RequestSubmiter {
        private RequestFactory factory;
        private List<Row> todoList;
        private Row row;


        LdsSubmiter(RequestFactory factory, Row row, List<Row> todo) {
            this.factory = factory;
            this.row = row;
            this.todoList = todo;
        }


        public void setResult(Result result) {
            todoList.remove(row);
            row.updateWith(result.getRow(0));
            if (!hasBeenUpdated()) {
                fireSaveEvent(getLoadResult());
            }
        }


        public Request buildRequest() {
            if (factory.needsSelector()) {
                factory.init(buildPrimaryKeyList());
            }

            return factory.buildRequest(row.toMap());
        }


        private FieldsList buildPrimaryKeyList() {
            FieldsList pks = new FieldsList();
            for (Iterator i = loadResult.primaryKeys(); i.hasNext();) {
                String pkName = (String)i.next();
                pks.addField(pkName, row.getFieldValue(pkName));
            }
            return pks;
        }
    }

    /**
     * Interface pour gerer les evt d'undo/redo.
     *
     * @author $Author: gaudefr $
     * @version $Revision: 1.27 $
     */
    private class UndoInterface implements DataListUndoable {
        public void setValue(Object row, Object columnId, Object value) {
            ListDataSource.this.setValueImpl(getRowIndex((Row)row), (String)columnId,
                                             (String)value);
        }


        public void addRow(int idx, Object row) {
            ListDataSource.this.addRowImpl((Row)row);
        }


        public void removeRow(Object row) {
            ListDataSource.this.removeRowImpl((Row)row);
        }
    }

    private class UndoRedoWrapper implements UndoableEditListener {
        public void undoableEditHappened(UndoableEditEvent event) {
            postEdit(event.getEdit());
        }
    }

    private class SnapshotUndoableEditSupport extends UndoableEditSupport {
        @Override
        protected CompoundEdit createCompoundEdit() {
            return new SnapshotCompoundEdit();
        }
    }

    private class SnapshotCompoundEdit extends CompoundEdit implements SnapshotEdit {
    }
}
