package net.codjo.mad.common.structure;
import java.sql.Types;
import junit.framework.TestCase;
/**
 * Classe de Test de <code>DbUtil</code>.
 *
 * @author $Author: gaudefr $
 * @version $Revision: 1.4 $
 */
public class SqlUtilTest extends TestCase {
    private SqlUtil sqlUtil = new SqlUtil();


    public void test_sqlTypeToString() {
        assertEquals("DATE", sqlUtil.sqlTypeToString(Types.DATE));
        assertEquals("NUMERIC", sqlUtil.sqlTypeToString(Types.NUMERIC));
        assertEquals("VARCHAR", sqlUtil.sqlTypeToString(Types.VARCHAR));
        assertEquals("INTEGER", sqlUtil.sqlTypeToString(Types.INTEGER));
    }


    public void test_sqlTypeToInt() {
        assertEquals(Types.DATE, sqlUtil.stringToSqlType("DATE"));
        assertEquals(Types.NUMERIC, sqlUtil.stringToSqlType("NUMERIC"));
        assertEquals(Types.VARCHAR, sqlUtil.stringToSqlType("VARCHAR"));
        assertEquals(Types.VARCHAR, sqlUtil.stringToSqlType("varchar"));
        assertEquals(Types.INTEGER, sqlUtil.stringToSqlType("INTEGER"));
    }
}
