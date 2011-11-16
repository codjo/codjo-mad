package net.codjo.mad.client.request;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import static net.codjo.mad.common.TestHelper.*;
/**
 */
public class DeleteRequestTest extends TestCase {
    public void test_constructor() throws Exception {
        FieldsList primaryKey = new FieldsList();
        String handlerId = "delete-ptf";
        DeleteRequest request = new DeleteRequest(handlerId, primaryKey);

        assertEquals(handlerId, request.getHandlerId());
        assertEquals(primaryKey, request.getPrimaryKey());
    }


    public void test_delete() throws Exception {
        DeleteRequest request = new DeleteRequest();
        request.setId("deleteCodificationPtf");

        List<Field> fields = new ArrayList<Field>();
        fields.add(new Field("SICOVAM", "6969"));
        fields.add(new Field("ISIN", "0451"));
        fields.add(new Field("LABEL", "COUCOU D"));
        FieldsList primaryKey = new FieldsList();
        primaryKey.setFields(fields);
        request.setPrimaryKey(primaryKey);

        String requestId = RequestIdManager.getInstance().getNewRequestId();

        String xml =
              "<delete request_id=\"" + String.valueOf(Integer.parseInt(requestId) - 1)
              + "\">" + "<id>deleteCodificationPtf</id>" + "<primarykey>"
              + getFieldXmlTag("SICOVAM", "6969")
              + getFieldXmlTag("ISIN", "0451")
              + getFieldXmlTag("LABEL", "COUCOU D") + "</primarykey>"
              + "</delete>";
        assertEquals(xml, request.toXml());
    }
}
