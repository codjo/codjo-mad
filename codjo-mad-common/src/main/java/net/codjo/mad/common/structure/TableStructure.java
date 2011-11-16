package net.codjo.mad.common.structure;
import java.util.List;
import java.util.Map;
/**
 * Description d'une structure de table.
 */
public interface TableStructure extends Structure {
    FieldStructure getFieldByJava(String javaName);


    FieldStructure getFieldBySql(String sqlName);


    int getFieldCount();


    Map getFieldsByJavaKey();


    Map getFieldsBySqlKey();


    String getJavaName();


    String getType();


    List<String> getFunctionalKeyFields();


    List<String> getSqlPrimaryKeyFields();
}
