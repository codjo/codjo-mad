package net.codjo.mad.client.request;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
/**
 * Resultat d'une requete.
 */
public class Result {
    private FieldsList primaryKeys;
    private String requestId;
    private List<Row> rows;
    private int totalRowCount;


    public Result() {
    }


    public Result(FieldsList primaryKeys, Row... rows) {
        this.primaryKeys = primaryKeys;
        this.rows = new ArrayList<Row>(rows.length);
        this.rows.addAll(Arrays.asList(rows));
    }


    public Result(Result res) {
        if (res.primaryKeys != null) {
            this.primaryKeys = new FieldsList(res.primaryKeys);
        }
        if (res.rows != null) {
            rows = new ArrayList<Row>(res.rows.size());
            for (Row item : res.rows) {
                rows.add(new Row(item));
            }
        }
    }


    public void setPrimaryKey(String pk) {
        this.primaryKeys = new FieldsList();
        this.primaryKeys.addField(pk, null);
    }


    public void setPrimaryKeys(FieldsList primaryKeys) {
        this.primaryKeys = primaryKeys;
    }


    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }


    @Deprecated
    public void setRows(java.util.Collection rows) {
        // UGLY : méthode necessaire pour castor (mapping xml)
        //noinspection unchecked
        setRows((ArrayList)rows);
    }


    public void setRows(List<Row> rows) {
        this.rows = rows;
    }


    public void setValue(int row, String columnId, String value) {
        getRow(row).setFieldValue(columnId, value);
    }


    public String getPrimaryKey(int index) {
        return getPrimaryKeys().getField(index).getName();
    }


    public int getPrimaryKeyCount() {
        return getPrimaryKeys().getFieldCount();
    }


    public FieldsList getPrimaryKeys() {
        return primaryKeys;
    }


    public String getRequestId() {
        return requestId;
    }


    public Row getFirstRow() {
        return getRow(0);
    }


    public Row getRow(int row) {
        if (rows == null) {
            throw new IndexOutOfBoundsException("Index: " + row + ", Size: 0");
        }
        return rows.get(row);
    }


    public int getRowCount() {
        if (rows == null) {
            return 0;
        }
        return rows.size();
    }


    public int getRowIndex(Row row) {
        return rows.indexOf(row);
    }


    public List<Row> getRows() {
        return rows;
    }


    public String getValue(int row, String columnId) {
        return getRow(row).getFieldValue(columnId);
    }


    public void addPrimaryKey(String pkName) {
        if (getPrimaryKeys() == null) {
            setPrimaryKeys(new FieldsList());
        }
        getPrimaryKeys().addField(pkName, "");
    }


    public int addRow(Row row) {
        if (getRows() == null) {
            setRows(new ArrayList<Row>());
        }
        getRows().add(row);
        return getRows().size() - 1;
    }


    public FieldsList buildPrimaryKeyListForRow(Row selectedRow) {
        FieldsList pks = new FieldsList();
        for (Iterator i = primaryKeys(); i.hasNext();) {
            String pkName = (String)i.next();
            pks.addField(pkName, selectedRow.getFieldValue(pkName));
        }
        return pks;
    }


    /**
     * @deprecated use {@link #containsField(int,String)}.
     */
    @Deprecated
    public boolean contains(int row, String fieldName) {
        return containsField(row, fieldName);
    }


    public boolean containsField(int row, String fieldName) {
        return getRow(row).contains(fieldName);
    }


    public Iterator primaryKeys() {
        return getPrimaryKeys().fieldNames();
    }


    public void removeElement(Row obj) {
        getRows().remove(obj);
    }


    public Row removeRow(int index) {
        return getRows().remove(index);
    }


    public Row removeRow(Row row) {
        getRows().remove(row);
        return row;
    }


    @Override
    public String toString() {
        return "Result(" + getRequestId() + ", PK=" + getPrimaryKeys() + ", CONTENT+"
               + getRows() + ")";
    }


    public int getTotalRowCount() {
        return Math.max(totalRowCount, getRowCount());
    }


    public void setTotalRowCount(int recordCount) {
        this.totalRowCount = recordCount;
    }
}
