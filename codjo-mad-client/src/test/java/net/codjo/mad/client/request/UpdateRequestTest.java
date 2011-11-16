package net.codjo.mad.client.request;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import static net.codjo.mad.common.TestHelper.*;
/**
 */
public class UpdateRequestTest extends TestCase {

    public void test_constructor() throws Exception {
        FieldsList primaryKey = new FieldsList();
        FieldsList newRowValues = new FieldsList();
        UpdateRequest request = new UpdateRequest("handlerId", primaryKey, newRowValues);

        assertEquals("handlerId", request.getHandlerId());
        assertEquals(primaryKey, request.getPrimaryKey());
        assertEquals(newRowValues, request.getRow());
    }


    public void test_update() throws Exception {
        UpdateRequest request = new UpdateRequest();
        request.setId("updateCodificationPtf");

        List<Field> pkFields = new ArrayList<Field>();
        pkFields.add(new Field("PIMS", "609"));
        FieldsList primaryKey = new FieldsList();
        primaryKey.setFields(pkFields);
        request.setPrimaryKey(primaryKey);

        List<Field> fields = new ArrayList<Field>();
        fields.add(new Field("SICOVAM", "6969"));
        fields.add(new Field("ISIN", "0451"));
        fields.add(new Field("LABEL", "COUCOU D"));
        FieldsList row = new FieldsList();
        row.setFields(fields);
        request.setRow(row);

        String requestId = RequestIdManager.getInstance().getNewRequestId();

        String xml =
              "<update request_id=\"" + String.valueOf(Integer.parseInt(requestId) - 1)
              + "\">" + "<id>updateCodificationPtf</id>" + "<primarykey>"
              + getFieldXmlTag("PIMS", "609") + "</primarykey>" + "<row>"
              + getFieldXmlTag("SICOVAM", "6969")
              + getFieldXmlTag("ISIN", "0451")
              + getFieldXmlTag("LABEL", "COUCOU D") + "</row>" + "</update>";

        assertEquals(xml, request.toXml());
    }
}
