package net.codjo.mad.common.structure;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
/**
 * Description d'une structure de table.
 *
 */
public class DefaultTableStructure implements TableStructure {
    private Map fields = new HashMap();
    private String label;
    private String sqlName;
    private String type;
    private String javaName;

    public DefaultTableStructure(String label, String javaName, String sqlName,
        String type) {
        this.label = label;
        this.javaName = javaName;
        this.sqlName = sqlName;
        this.type = type;
    }

    public FieldStructure getFieldByJava(String nameJava) {
        for (Object o : fields.values()) {
            FieldStructure field = (FieldStructure)o;
            if (nameJava.equals(field.getJavaName())) {
                return field;
            }
        }
        return null;
    }


    public FieldStructure getFieldBySql(String nameSQL) {
        return (FieldStructure)fields.get(nameSQL);
    }


    public int getFieldCount() {
        return fields.size();
    }


    public Map getFieldsByJavaKey() {
        Map map = new HashMap();
        for (Object o : fields.values()) {
            FieldStructure field = (FieldStructure)o;
            map.put(field.getJavaName(), field);
        }
        return map;
    }


    public Map getFieldsBySqlKey() {
        return Collections.unmodifiableMap(fields);
    }


    public String getJavaName() {
        return javaName;
    }


    public void setJavaName(String javaName) {
        this.javaName = javaName;
    }


    public String getLabel() {
        return label;
    }


    public void setLabel(String label) {
        this.label = label;
    }


    public String getSqlName() {
        return sqlName;
    }


    public String getType() {
        return type;
    }


    public List<String> getFunctionalKeyFields() {
        List<String> fieldNames = new LinkedList<String>();
        for (Object o : fields.values()) {
            FieldStructure field = (FieldStructure)o;
            if(field.isFunctionalKey()) {
                fieldNames.add(field.getJavaName());
            }
        }
        return fieldNames;
    }


    public List<String> getSqlPrimaryKeyFields() {
        List<String> fieldNames = new LinkedList<String>();
        for (Object o : fields.values()) {
            FieldStructure field = (FieldStructure)o;
            if(field.isSqlPrimaryKey()) {
                fieldNames.add(field.getJavaName());
            }
        }
        return fieldNames;
    }


    public void setType(String type) {
        this.type = type;
    }


    @Override
    public String toString() {
        if (getLabel() != null) {
            return getLabel();
        }
        else {
            return getSqlName();
        }
    }


    public int compareTo(Object obj) {
        return getLabel().compareTo(((Structure)obj).getLabel());
    }


    public void addField(FieldStructure field) {
        fields.put(field.getSqlName(), field);
    }
}
