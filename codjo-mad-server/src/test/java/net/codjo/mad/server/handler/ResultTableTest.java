package net.codjo.mad.server.handler;
import static net.codjo.test.common.matcher.JUnitMatchers.*;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Test;
/**
 *
 */
public class ResultTableTest extends TestCase {

    @Test
    public void test_addRow() throws Exception {

        ResultTable table = new ResultTable();

        List<String> primaryKeys = new ArrayList<String>();
        primaryKeys.add("fofTypeCode");
        table.setPrimaryKey(primaryKeys);

        table.addRow().addField("fofTypeCode", "FCI").addField("currencyCode", "EUR");

        table.addRow().addField("fofTypeCode", "BEP");
        table.addField("currencyCode", "USD");

        String expected
              = "<primarykey><field name=\"fofTypeCode\"/></primarykey><row><field name=\"fofTypeCode\"><![CDATA[FCI]]></field><field name=\"currencyCode\"><![CDATA[EUR]]></field></row><row><field name=\"fofTypeCode\"><![CDATA[BEP]]></field><field name=\"currencyCode\"><![CDATA[USD]]></field></row>";

        assertThat(expected, equalTo(table.toXML()));
    }


    @Test
    public void test_a() throws Exception {
        ResultTable table = new ResultTable();

        table.setPrimayKey("fofTypeCode", "currencyCode");

        table.addRow().addField("fofTypeCode", "FCI").addField("currencyCode", "EUR");

        table.addRow().addField("fofTypeCode", "BEP");
        table.addField("currencyCode", "USD");

        String expected
              = "<primarykey><field name=\"fofTypeCode\"/><field name=\"currencyCode\"/></primarykey><row><field name=\"fofTypeCode\"><![CDATA[FCI]]></field><field name=\"currencyCode\"><![CDATA[EUR]]></field></row><row><field name=\"fofTypeCode\"><![CDATA[BEP]]></field><field name=\"currencyCode\"><![CDATA[USD]]></field></row>";

        assertThat(expected, equalTo(table.toXML()));
    }
}
