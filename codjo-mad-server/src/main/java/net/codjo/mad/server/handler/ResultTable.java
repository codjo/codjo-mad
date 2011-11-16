package net.codjo.mad.server.handler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 *
 */
public class ResultTable {
    private List<Map<Object, Object>> rows;
    private List<String> primaryKeys;


    public ResultTable() {
        rows = new ArrayList<Map<Object, Object>>();
    }


    public void setPrimaryKey(List<String> primaryKeys) {
        this.primaryKeys = primaryKeys;
    }


    public void setPrimayKey(String... keys) {
        primaryKeys = new ArrayList<String>();
        for (String key : keys) {
            primaryKeys.add(key);
        }
    }


    public ResultTable addRow() {

        rows.add(new HashMap<Object, Object>());

        return this;
    }


    public ResultTable addField(String key, Object value) {

        rows.get(rows.size() - 1).put(key, value);

        return this;
    }


    private String primaryKeyToXML() {
        StringBuffer buffer = new StringBuffer("");
        if (primaryKeys != null) {
            buffer.append("<primarykey>");

            for (String primaryKey : primaryKeys) {
                buffer.append("<field name=\"").append(primaryKey).append("\"/>");
            }

            buffer.append("</primarykey>");
        }
        return buffer.toString();
    }


    private String fieldToXMLString(Object key, Object value) {

        if (value instanceof String) {

            value = "<![CDATA[" + value + "]]>";
        }
        return "<field name=\"" + key + "\">" + value + "</field>";
    }


    private String rowToXMLString(Map row) {
        StringBuffer buffer = new StringBuffer("<row>");

        for (Object key : row.keySet()) {
            Object value = row.get(key);

            buffer.append(fieldToXMLString(key, value));
        }
        buffer.append("</row>");
        return buffer.toString();
    }


    public String toXML() {
        StringBuffer buffer = new StringBuffer();

        buffer.append(primaryKeyToXML());
        for (Map row : rows) {
            buffer.append(rowToXMLString(row));
        }

        return buffer.toString();
    }
}
