package net.codjo.mad.gui.request.requetor;
import net.codjo.mad.common.structure.DefaultTableStructure;
import net.codjo.mad.common.structure.StructureReader;
import net.codjo.mad.common.structure.TableStructure;
import java.util.ArrayList;
import java.util.List;
/**
 * Description de la jointure entre deux tables.
 */
public class Link {
    private static final String AS = " as ";
    private List<Key> keys = new ArrayList<Key>();
    private String from = null;
    private String to = null;
    private TableStructure fromTable;
    private TableStructure toTable;


    public Link() {
        this.from = "";
        this.to = "";

        toTable = new DefaultTableStructure("", "", "", "");
        fromTable = toTable;
    }


    public Link(String from, String to, StructureReader structureReader) {
        this.from = from;
        this.to = to;

        if (from != null) {
            fromTable = structureReader.getTableBySqlName(getSqlTableName(from));
        }
        toTable = structureReader.getTableBySqlName(getSqlTableName(to));
    }


    public void addKey(Key key) {
        keys.add(key);
    }


    public String[][] keysToArray() {
        String[][] array = new String[keys.size()][3];
        for (int i = 0; i < keys.size(); i++) {
            Key key = keys.get(i);
            array[i][0] = key.getFromField();
            array[i][1] = key.getToField();
            array[i][2] = key.getOperatorField();
        }

        return array;
    }


    public String getFrom() {
        return from;
    }


    public String getTo() {
        return to;
    }


    public TableStructure getFromTable() {
        return fromTable;
    }


    public TableStructure getToTable() {
        return toTable;
    }


    public List<Key> getKeys() {
        return keys;
    }


    @Override
    public String toString() {
        String label = toTable.getLabel();
        String toAlias = getAlias(to);
        if (toAlias != null) {
            label += " (" + toAlias + ")";
        }
        return label;
    }


    private String getSqlTableName(String linkName) {
        String tableName;
        int asIndex = linkName.indexOf(AS);
        if (asIndex != -1) {
            tableName = linkName.substring(0, asIndex).trim();
        }
        else {
            tableName = linkName;
        }
        return tableName;
    }


    private String getAlias(String linkName) {
        String tableName = null;

        int fromAs = linkName.indexOf(AS);
        if (fromAs != -1) {
            tableName =
                  linkName.substring(fromAs + AS.length(), linkName.length()).trim();
        }

        return tableName;
    }


    public boolean containsField(String javaFieldName) {
        return javaFieldName.startsWith(getAlias(getTo()) + ".")
               || toTable.getFieldByJava(javaFieldName) != null;
    }


    public String getFullSqlFieldName(String javaFieldName) {
        String result;
        if (hasAlias()) {
            String fieldSqlName =
                  getToTable().getFieldByJava(removeAlias(javaFieldName)).getSqlName();
            result = getAlias(getTo()) + "." + fieldSqlName;
        }
        else {
            result =
                  getTo() + "." + getToTable().getFieldByJava(javaFieldName).getSqlName();
        }
        return result;
    }


    public String completeSqlFieldName(String sqlFieldName) {
        String result;
        if (hasAlias()) {
            result = getAlias(getTo()) + "." + sqlFieldName;
        }
        else {
            result = getTo() + "." + sqlFieldName;
        }
        return result;
    }


    private boolean hasAlias() {
        return getTo().contains(AS);
    }


    private String removeAlias(String javaFieldName) {
        if (javaFieldName.indexOf('.') == -1) {
            return javaFieldName;
        }
        return javaFieldName.substring(javaFieldName.indexOf('.') + 1,
                                       javaFieldName.length());
    }


    public Object getFieldByJava(String javaFieldName) {
        return getToTable().getFieldByJava(removeAlias(javaFieldName));
    }
}
