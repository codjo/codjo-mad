package net.codjo.mad.client.request;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import static net.codjo.mad.common.TestHelper.*;
/**
 */
public class SqlRequestTest extends TestCase {

    public void test_constructor() throws Exception {
        String handlerId = "getNextActionId";
        FieldsList arguments = new FieldsList("date", "2007-01-01 10:00:05");
        SimpleListElement attributes = new SimpleListElement("PTF_CODE");

        SqlRequest request = new SqlRequest(handlerId);
        assertEquals(handlerId, request.getId());

        request = new SqlRequest(handlerId, arguments);
        assertEquals(handlerId, request.getId());
        assertEquals(arguments, request.getArguments());

        request = new SqlRequest(handlerId, arguments, attributes);
        assertEquals(handlerId, request.getId());
        assertEquals(arguments, request.getArguments());
        assertEquals(attributes, request.getAttributes());
    }


    public void test_sql_attributs() throws Exception {
        SqlRequest request = new SqlRequest();

        request.setId("selectAllCodificationPtf");
        request.setAttributes(new String[]{"PIMS", "SICOVAM", "ISIN"});
        request.setPage(1, 100);

        String xml =
              "<sql request_id=\"" + request.getRequestId() + "\">"
              + "<id>selectAllCodificationPtf</id>" + "<attributes>" + "<name>PIMS</name>"
              + "<name>SICOVAM</name>" + "<name>ISIN</name>" + "</attributes>"
              + "<page num=\"1\" rows=\"100\"/>" + "</sql>";

        assertEquals(xml, request.toXml());
    }


    public void test_sql_attributsSimplListElement()
          throws Exception {
        SqlRequest request = new SqlRequest();
        request.setId("selectAllCodificationPtf");

        request.setAttributes(new SimpleListElement("PIMS", "SICOVAM", "ISIN"));

        Page page = new Page();
        page.setNum("1");
        page.setRows("100");
        request.setPage(page);

        String requestId = RequestIdManager.getInstance().getNewRequestId();

        String xml =
              "<sql request_id=\"" + String.valueOf(Integer.parseInt(requestId) - 1)
              + "\">" + "<id>selectAllCodificationPtf</id>" + "<attributes>"
              + "<name>PIMS</name>" + "<name>SICOVAM</name>" + "<name>ISIN</name>"
              + "</attributes>" + "<page num=\"1\" rows=\"100\"/>" + "</sql>";

        assertEquals(xml, request.toXml());
    }


    public void test_sql_arguments() throws Exception {
        SqlRequest request = new SqlRequest();
        request.setId("selectCodificationPtfById");

        List<Field> fields = new ArrayList<Field>();
        fields.add(new Field("PIMS", "609"));

        FieldsList arguments = new FieldsList();
        arguments.setFields(fields);
        request.setArguments(arguments);

        String requestId = RequestIdManager.getInstance().getNewRequestId();

        String xml =
              "<sql request_id=\"" + String.valueOf(Integer.parseInt(requestId) - 1)
              + "\">" + "<id>selectCodificationPtfById</id>" + "<selector>"
              + getFieldXmlTag("PIMS", "609") + "</selector>" + "</sql>";

        assertEquals(xml, request.toXml());
    }
}
