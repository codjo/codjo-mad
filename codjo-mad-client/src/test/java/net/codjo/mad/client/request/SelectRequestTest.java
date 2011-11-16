package net.codjo.mad.client.request;
import static net.codjo.mad.common.TestHelper.getFieldXmlTag;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
/**
 */
public class SelectRequestTest extends TestCase {
    public void test_constructor() throws Exception {
        FieldsList selector = new FieldsList();
        String handlerId = "select-ptf-byId";
        SelectRequest request = new SelectRequest(handlerId, selector);

        assertEquals(handlerId, request.getHandlerId());
        assertEquals(selector, request.getSelector());
    }


    public void test_select_all_alias() throws Exception {
        SelectRequest request = new SelectRequest();

        request.setId("selectAllCodificationPtf");
        request.setAttributes(new String[]{"PIMS", "SICOVAM", "ISIN"});
        request.setPage(1, 100);

        String xml =
              "<select request_id=\"" + request.getRequestId() + "\">"
              + "<id>selectAllCodificationPtf</id>" + "<attributes>" + "<name>PIMS</name>"
              + "<name>SICOVAM</name>" + "<name>ISIN</name>" + "</attributes>"
              + "<page num=\"1\" rows=\"100\"/>" + "</select>";

        assertEquals(xml, request.toXml());
    }


    public void test_select_all() throws Exception {
        SelectRequest request = new SelectRequest();
        request.setId("selectAllCodificationPtf");
        request.setAttributes(new SimpleListElement("PIMS", "SICOVAM", "ISIN"));

        Page page = new Page();
        page.setNum("1");
        page.setRows("100");
        request.setPage(page);

        String requestId = RequestIdManager.getInstance().getNewRequestId();

        String xml =
              "<select request_id=\"" + String.valueOf(Integer.parseInt(requestId) - 1)
              + "\">" + "<id>selectAllCodificationPtf</id>" + "<attributes>"
              + "<name>PIMS</name>" + "<name>SICOVAM</name>" + "<name>ISIN</name>"
              + "</attributes>" + "<page num=\"1\" rows=\"100\"/>" + "</select>";

        assertEquals(xml, request.toXml());
    }


    public void test_select_selector() throws Exception {
        SelectRequest request = new SelectRequest();
        request.setId("selectCodificationPtfById");

        List<Field> fields = new ArrayList<Field>();
        fields.add(new Field("PIMS", "609"));
        FieldsList selector = new FieldsList();
        selector.setFields(fields);
        request.setSelector(selector);

        String requestId = RequestIdManager.getInstance().getNewRequestId();

        String xml =
              "<select request_id=\"" + String.valueOf(Integer.parseInt(requestId) - 1)
              + "\">" + "<id>selectCodificationPtfById</id>" + "<selector>"
              + getFieldXmlTag("PIMS", "609") + "</selector>" + "</select>";

        assertEquals(xml, request.toXml());
    }

/*
    public void test_equals() throws Exception {
        SelectRequest request = new SelectRequest("fred", new FieldsList());
        SelectRequest testRequest = new SelectRequest("fred", new FieldsList());
        
        //noinspection ObjectEqualsNull
        assertFalse(request.equals(null));
        assertTrue(request.equals(testRequest));

        request.setPage(4, 40);
        assertFalse(request.equals(testRequest));
        testRequest.setPage(4, 40);
        assertTrue(request.equals(testRequest));

        request.addSelector("coach", "boris");
        assertFalse(request.equals(testRequest));
        testRequest.addSelector("coach", "boris");
        assertTrue(request.equals(testRequest));

        FieldsList selector = new FieldsList("coach", "olivier");
        request.setSelector(selector);
        assertFalse(request.equals(testRequest));
        testRequest.setSelector(selector);
        assertTrue(request.equals(testRequest));
    }


    public void test_hashCode() throws Exception {
        assertTrue(new SelectRequest().hashCode() == 0);

        SelectRequest request = new SelectRequest("fred", new FieldsList());
        request.setPage(4, 40);
        request.addSelector("coach", "boris");
        SelectRequest testRequest = new SelectRequest("fred", new FieldsList());
        testRequest.setPage(4, 40);
        testRequest.addSelector("coach", "boris");
        assertTrue(request.hashCode() == testRequest.hashCode());
    }
*/
}
