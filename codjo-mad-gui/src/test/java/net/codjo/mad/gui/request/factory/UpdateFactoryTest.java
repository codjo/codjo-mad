package net.codjo.mad.gui.request.factory;
import net.codjo.mad.client.request.FieldsList;
import net.codjo.mad.client.request.Request;
import net.codjo.test.common.XmlUtil;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;
/**
 */
public class UpdateFactoryTest extends TestCase {
    protected Map<String, String> fields;
    protected RequestFactory updateFactory;


    /**
     * Test que le comportement construit la bonne request lorsque des champs sont définit dans l'IHM.
     *
     * @throws Exception
     */
    public void test_buildRequest() throws Exception {
        updateFactory.init(getPk());

        fields = new HashMap<String, String>();
        fields.put("sicovamCode", "12");
        fields.put("label", "LABELL");

        Request request = updateFactory.buildRequest(fields);

        String xml = getRequestAsXml(request.getRequestId());

        XmlUtil.assertEquals(xml, request.toXml());
    }


    /**
     * Test la requete construite ne place pas les champs pk dans les champs à mettre à jours.
     *
     * @throws Exception
     */
    public void test_buildRequest_removePk() throws Exception {
        updateFactory.init(getPk());

        fields = new HashMap<String, String>();
        fields.put("sicovamCode", "12");
        fields.put("label", "LABELL");
        fields.put("pimsCode", "newPkvalue");

        Request request = updateFactory.buildRequest(fields);

        // xml ne contient pas le champs pimsCode
        String xml = getRequestAsXml(request.getRequestId());

        XmlUtil.assertEquals(xml, request.toXml());
    }


    /**
     * Test la requete construite ne place pas les champs exclues dans les champs à mettre à jours.
     *
     * @throws Exception
     */
    public void test_buildRequest_removeExcludedFields()
          throws Exception {
        updateFactory.init(getPk());

        fields = new HashMap<String, String>();
        fields.put("sicovamCode", "12");
        fields.put("label", "LABELL");
        fields.put("excludedField", "--");

        updateFactory.setExcludedFieldList(new String[]{"excludedField"});

        Request request = updateFactory.buildRequest(fields);

        // xml ne contient pas le champs excludedField
        String xml = getRequestAsXml(request.getRequestId());

        XmlUtil.assertEquals(xml, request.toXml());
    }


    /**
     * Test que le comportement construit la bonne request (plusieurs PK).
     *
     * @throws Exception
     */
    public void test_buildRequest_multiPk() throws Exception {
        updateFactory.init(getMultiplePk());

        fields = new HashMap<String, String>();
        fields.put("label", "LABELL");

        Request request = updateFactory.buildRequest(fields);

        String xml = getRequestMultiplePkAsXml(request.getRequestId());

        XmlUtil.assertEquals(xml, request.toXml());
    }


    @Override
    protected void setUp() {
        updateFactory = new UpdateFactory("updateCodificationPtf");
    }


    protected FieldsList getMultiplePk() {
        FieldsList fieldsList = new FieldsList();
        fieldsList.addField("pimsCode", "999");
        fieldsList.addField("sicovamCode", "12");
        return fieldsList;
    }


    protected FieldsList getPk() {
        FieldsList fieldsList = new FieldsList();
        fieldsList.addField("pimsCode", "999");
        return fieldsList;
    }


    protected String getRequestAsXml(String requestId) {
        return "<update request_id='" + requestId + "'>"
               + "<id>updateCodificationPtf</id>"
               + "  <primarykey>"
               + "    <field name='pimsCode'>999</field>"
               + "  </primarykey>"
               + "  <row>"
               + "    <field name='label'>LABELL</field>"
               + "    <field name='sicovamCode'>12</field>"
               + "  </row>"
               + "</update>";
    }


    protected String getRequestMultiplePkAsXml(String requestId) {
        return "<update request_id='" + requestId + "'>"
               + "<id>updateCodificationPtf</id>"
               + "  <primarykey>"
               + "    <field name='pimsCode'>999</field>"
               + "    <field name='sicovamCode'>12</field>"
               + "  </primarykey>"
               + "  <row>"
               + "    <field name='label'>LABELL</field>"
               + "  </row>"
               + "</update>";
    }
}
