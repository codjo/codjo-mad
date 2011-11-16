package net.codjo.mad.gui.request;
import net.codjo.mad.gui.request.factory.DeleteFactory;
import net.codjo.mad.gui.request.factory.InsertFactory;
import net.codjo.mad.gui.request.factory.RequestFactory;
import net.codjo.mad.gui.request.factory.RequetorFactory;
import net.codjo.mad.gui.request.factory.SelectFactory;
import net.codjo.mad.gui.request.factory.UpdateFactory;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
/**
 * Preference pour l'affichage d'une table.
 *
 * @author $Author: gaudefr $
 * @version $Revision: 1.7 $
 */
public class Preference {
    private static final Logger APP = Logger.getLogger(Preference.class);
    private String id;
    private Class detailWindowClass;
    private String dwClassName;
    private String entity;
    private RequestFactory selectAll;
    private RequestFactory selectByPk;
    private RequestFactory update;
    private RequestFactory insert;
    private RequestFactory delete;
    private RequetorFactory requetor;
    private List<Column> columns = new ArrayList<Column>();
    private List<Column> hiddenColumns = new ArrayList<Column>();


    public Preference() {
    }


    public Preference(String preferenceId) {
        setId(preferenceId);
    }


    /**
     * Constructeur par copie.
     *
     * @param preference
     */
    public Preference(Preference preference) {
        setId(preference.getId());
        setDetailWindowClass(preference.getDetailWindowClass());
        setEntity(preference.getEntity());
        setSelectAll(preference.getSelectAll());
        setSelectByPk(preference.getSelectByPk());
        setUpdate(preference.getUpdate());
        setInsert(preference.getInsert());
        setDelete(preference.getDelete());
        setRequetor(preference.getRequetor());
        setColumns(new ArrayList<Column>(preference.getColumns()));
        setHiddenColumns(new ArrayList<Column>(preference.getHiddenColumns()));
    }


    public String getEntity() {
        return entity;
    }


    public void setEntity(String entity) {
        this.entity = entity;
    }


    public void setDelete(RequestFactory delete) {
        this.delete = delete;
    }


    public void setDeleteId(String id) {
        setDelete(new DeleteFactory(id));
    }


    public void setColumns(List<Column> columnsToBeDisplayed) {
        this.columns = columnsToBeDisplayed;
    }


    public void setHiddenColumns(List<Column> hiddenColumns) {
        this.hiddenColumns = hiddenColumns;
    }


    public void setHiddenColumnNames(List<String> hiddenColumnNames) {
        for (String hiddenColumnName : hiddenColumnNames) {
            hiddenColumns.add(new Column(hiddenColumnName, hiddenColumnName));
        }
    }


    public void setDetailWindowClass(Class detailWindowClass) {
        this.detailWindowClass = detailWindowClass;
    }


    public void hideColumn(Column column) {
        if (columns.contains(column)) {
            columns.remove(column);
            hiddenColumns.add(column);
        }
    }


    public void displayColumn(Column column) {
        if (hiddenColumns.contains(column)) {
            hiddenColumns.remove(column);
            columns.add(column);
        }
    }


    public void displayColumn(int position, Column column) {
        if (hiddenColumns.contains(column)) {
            hiddenColumns.remove(column);
            if (position > columns.size()) {
                columns.add(column);
            }
            else {
                columns.add(position, column);
            }
        }
    }


    public void setDwClassName(String dwClassName) {
        try {
            initDetailWindowClassName(dwClassName);
        }
        catch (Throwable ex) {
            APP.error("ClassNotFoundException", ex);
        }
        this.dwClassName = dwClassName;
    }


    public void setId(String id) {
        this.id = id;
    }


    public void setInsert(RequestFactory insert) {
        this.insert = insert;
    }


    public void setInsertId(String id) {
        setInsert(new InsertFactory(id));
    }


    public void setSelectAll(RequestFactory selectAll) {
        this.selectAll = selectAll;
    }


    public void setSelectAllId(String id) {
        setSelectAll(new SelectFactory(id));
    }


    public void setSelectByPk(RequestFactory selectByPk) {
        this.selectByPk = selectByPk;
    }


    public void setSelectByPkId(String id) {
        setSelectByPk(new SelectFactory(id));
    }


    public void setUpdate(RequestFactory update) {
        this.update = update;
    }


    public void setUpdateId(String id) {
        setUpdate(new UpdateFactory(id));
    }


    public int getColumnIndex(String fieldName) {
        for (int i = 0; i < columns.size(); i++) {
            if ((columns.get(i)).getFieldName().equals(fieldName)) {
                return i;
            }
        }
        return -1;
    }


    public List<Column> getColumns() {
        return columns;
    }


    public String[] getColumnsName() {
        String[] columnsName = new String[columns.size()];
        for (int i = 0; i < columns.size(); i++) {
            columnsName[i] = (columns.get(i)).getFieldName();
        }
        return columnsName;
    }


    public String[] getHiddenColumnsName() {
        String[] columnsName = new String[hiddenColumns.size()];
        for (int i = 0; i < hiddenColumns.size(); i++) {
            columnsName[i] = (hiddenColumns.get(i)).getFieldName();
        }
        return columnsName;
    }


    public RequestFactory getDelete() {
        return delete;
    }


    public Class getDetailWindowClass() {
        return detailWindowClass;
    }


    public String getDwClassName() {
        return dwClassName;
    }


    public String getId() {
        return id;
    }


    public RequestFactory getInsert() {
        return insert;
    }


    public RequestFactory getSelectAll() {
        return selectAll;
    }


    public RequestFactory getSelectByPk() {
        return selectByPk;
    }


    public RequestFactory getUpdate() {
        return update;
    }


    public RequetorFactory getRequetor() {
        return requetor;
    }


    public List<Column> getHiddenColumns() {
        return hiddenColumns;
    }


    public void setRequetor(RequetorFactory requetor) {
        this.requetor = requetor;
    }


    public void setRequetorId(String requetorId) {
        this.requetor = new RequetorFactory(requetorId);
    }


    @Override
    public String toString() {
        return "preference(id=" + getId() + ")";
    }


    private void initDetailWindowClassName(String detailWindowClassName)
          throws ClassNotFoundException {
        if (detailWindowClassName == null || "".equals(detailWindowClassName.trim())) {
            return;
        }
        detailWindowClass = Class.forName(detailWindowClassName.trim());
    }
}
