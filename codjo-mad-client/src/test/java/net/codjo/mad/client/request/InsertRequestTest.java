package net.codjo.mad.client.request;
import junit.framework.TestCase;
import static net.codjo.mad.common.TestHelper.*;
/**
 */
public class InsertRequestTest extends TestCase {

    public void test_constructor() throws Exception {
        FieldsList row = new FieldsList();
        String handlerId = "new-ptf";
        InsertRequest request = new InsertRequest(handlerId, row);

        assertEquals(handlerId, request.getHandlerId());
        assertEquals(row, request.getRow());
    }


    public void test_insert_castor() throws Exception {
        InsertRequest request = new InsertRequest();
        request.setId("newCodificationPtf");
        request.addField("SICOVAM", "6969");
        request.addField("ISIN", "0451");
        request.addField("LABEL", "COUCOU D");

        String requestId = RequestIdManager.getInstance().getNewRequestId();

        String xml =
              "<insert request_id=\"" + String.valueOf(Integer.parseInt(requestId) - 1)
              + "\">" + "<id>newCodificationPtf</id>" + "<row>"
              + getFieldXmlTag("SICOVAM", "6969")
              + getFieldXmlTag("ISIN", "0451")
              + getFieldXmlTag("LABEL", "COUCOU D") + "</row>" + "</insert>";
        assertEquals(xml, request.toXml());
    }
}
