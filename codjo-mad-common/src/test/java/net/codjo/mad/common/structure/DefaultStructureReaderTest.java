package net.codjo.mad.common.structure;
import java.sql.Types;
import static java.util.Arrays.asList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import junit.framework.TestCase;

public class DefaultStructureReaderTest extends TestCase {
    StructureReader reader;


    public DefaultStructureReaderTest(String name) {
        super(name);
    }


    public void test_getTableStructure() throws Exception {
        TableStructure table = reader.getTableBySqlName("BOBO");

        assertEquals("data", table.getType());
        assertEquals("la table a BOBO", table.getLabel());
        assertEquals("Bobo", table.getJavaName());

        assertEquals(2, table.getFieldCount());
        assertEquals("Code portefeuille", table.getFieldBySql("PORTFOLIO").getLabel());
        assertEquals("NET_DIVIDEND", table.getFieldBySql("NET_DIVIDEND").getLabel());
        assertEquals("netDividend", table.getFieldBySql("NET_DIVIDEND").getJavaName());
        assertEquals("NET_DIVIDEND est de Type NUMERIC ", Types.NUMERIC,
                     table.getFieldBySql("NET_DIVIDEND").getSqlType());
        assertEquals("NET_DIVIDEND est de Type INDEFINI ", Types.JAVA_OBJECT,
                     table.getFieldBySql("PORTFOLIO").getSqlType());
    }


    public void test_getSqlAttributes() throws Exception {
        TableStructure table = reader.getTableByJavaName("Bobo2");

        FieldStructure portfolioField = table.getFieldBySql("PORTFOLIO");
        assertEquals(null, portfolioField.getSqlPrecision());
        assertEquals(Types.JAVA_OBJECT, portfolioField.getSqlType());
        assertFalse(portfolioField.isSqlRequired());
        assertTrue(portfolioField.isSqlPrimaryKey());

        FieldStructure netDividendfield = table.getFieldBySql("NET_DIVIDEND");
        assertEquals("17,2", netDividendfield.getSqlPrecision());
        assertEquals(Types.TIMESTAMP, netDividendfield.getSqlType());
        assertTrue(netDividendfield.isSqlRequired());
        assertFalse(netDividendfield.isSqlPrimaryKey());
    }


    public void test_getSqlPrimaryKey() throws Exception {
        TableStructure table = reader.getTableByJavaName("Bobo2");
        List<String> actualFields = table.getSqlPrimaryKeyFields();
        Collections.sort(actualFields);
        assertEquals(asList("portfolio"), actualFields);
    }


    public void test_isFunctionalKey() throws Exception {
        TableStructure table = reader.getTableByJavaName("Dividend");

        assertIsFunctionalKey(table, true, "portfolioCode");
        assertIsFunctionalKey(table, false, "netDividend");
        assertIsFunctionalKey(table, true, "dividendDate");
        assertIsFunctionalKey(table, false, "automaticUpdate");
        assertIsFunctionalKey(table, false, "comment");
        assertIsFunctionalKey(table, false, "createdBy");
    }


    public void test_getFunctionalKey() throws Exception {
        TableStructure table = reader.getTableByJavaName("Dividend");
        List<String> actualFields = table.getFunctionalKeyFields();
        Collections.sort(actualFields);
        assertEquals(asList("dividendDate", "portfolioCode"), actualFields);
    }


    private void assertIsFunctionalKey(TableStructure table, boolean expected, String javaFieldName) {
        FieldStructure portfolioField = table.getFieldByJava(javaFieldName);
        assertEquals(expected, portfolioField.isFunctionalKey());
    }


    public void test_getSqlTypes() throws Exception {
        TableStructure table = reader.getTableBySqlName("SQL_TYPES");

        assertEquals(Types.TIMESTAMP, table.getFieldBySql("TYPE_1").getSqlType());
        assertEquals(Types.NUMERIC, table.getFieldBySql("TYPE_2").getSqlType());
        assertEquals(Types.VARCHAR, table.getFieldBySql("TYPE_3").getSqlType());
        assertEquals(Types.LONGVARCHAR, table.getFieldBySql("TYPE_4").getSqlType());
        assertEquals(Types.INTEGER, table.getFieldBySql("TYPE_5").getSqlType());
        assertEquals(Types.BIT, table.getFieldBySql("TYPE_6").getSqlType());
    }


    public void test_getTableStructureByJavaName()
          throws Exception {
        TableStructure table = reader.getTableByJavaName("Bobo");

        assertEquals("data", table.getType());
        assertEquals("la table a BOBO", table.getLabel());
        assertEquals("BOBO", table.getSqlName());

        assertEquals(2, table.getFieldCount());
        assertEquals("Code portefeuille", table.getFieldBySql("PORTFOLIO").getLabel());
        assertEquals("NET_DIVIDEND", table.getFieldBySql("NET_DIVIDEND").getLabel());
        assertEquals("netDividend", table.getFieldBySql("NET_DIVIDEND").getJavaName());

        assertNotNull(reader.getTableByJavaName("Bobo2"));
        assertNotNull(reader.getTableByJavaName("Bobo3"));
    }


    public void test_getQuarantineTables() throws Exception {
        Map quarantineTableMap = reader.getQuarantineTables();

        assertEquals(2, quarantineTableMap.size());
        assertEquals("Mon dividend a moi",
                     ((TableStructure)quarantineTableMap.get("Q_AP_DIVIDEND")).getLabel());
        assertEquals("Q_AP_TOTO",
                     ((TableStructure)quarantineTableMap.get("Q_AP_TOTO")).getLabel());
    }


    public void test_containsTableByJavaName() throws Exception {
        assertTrue(reader.containsTableByJavaName("Bobo"));
        assertFalse(reader.containsTableByJavaName("Test"));
        assertTrue(reader.containsTableByJavaName("Bobo2"));
        assertTrue(reader.containsTableByJavaName("Bobo3"));
    }


    public void test_containsTableBySqlName() throws Exception {
        assertTrue(reader.containsTableBySqlName("BOBO"));
        assertFalse(reader.containsTableBySqlName("TEST"));
    }


    @Override
    protected void setUp() throws Exception {
        reader =
              new DefaultStructureReader(DefaultStructureReaderTest.class.getResourceAsStream(
                    "structure.xml"));
    }
}
