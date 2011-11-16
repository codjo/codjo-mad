package net.codjo.mad.common.structure;
import java.util.Map;
import java.util.Collection;

public interface StructureReader {
    public Collection<DefaultTableStructure> getAllTableStructure();


    public Map<String, TableStructure> getQuarantineTables();


    public TableStructure getTableByJavaName(String javaName);


    public TableStructure getTableBySqlName(String sqlTableName);


    public boolean containsTableByJavaName(String javaName);


    public boolean containsTableBySqlName(String sqlTableName);
}
