package net.codjo.mad.gui.request.factory;
import net.codjo.mad.client.request.FieldsList;
import net.codjo.mad.client.request.Request;
import net.codjo.test.common.XmlUtil;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;
/**
 */
public class SelectFactoryTest extends TestCase {
    protected Map<String, String> attributes;
    protected RequestFactory behavior;


    /**
     * Test que le comportement construit la bonne request lorsque des champs sont définit dans l'IHM.
     *
     * @throws Exception
     */
    public void test_buildRequest() throws Exception {
        behavior.init(getSelector());

        Request request = behavior.buildRequest(attributes);

        String xml = getRequestAsXml(request.getRequestId());

        XmlUtil.assertEquals(xml, request.toXml());
    }


    /**
     * Test que le helper rappatrie les bons attributs.
     *
     * @throws Exception
     */
    public void test_buildRequest_attributes() throws Exception {
        behavior.init(getSelector());

        attributes = new HashMap<String, String>();
        attributes.put("sicovamCode", null);
        Request request = behavior.buildRequest(attributes);

        String xml = getRequestWithAttributesAsXml(request.getRequestId());

        assertEquals(xml, request.toXml());
    }


    /**
     * Test que le comportement construit la bonne request (plusieurs PK).
     *
     * @throws Exception
     */
    public void test_buildRequest_multiPk() throws Exception {
        behavior.init(getMultipleSelector());

        Request request = behavior.buildRequest(attributes);

        String xml = getRequestMultiplePkAsXml(request.getRequestId());

        XmlUtil.assertEquals(xml, request.toXml());
    }


    @Override
    protected void setUp() {
        behavior = new SelectFactory("selectCodificationPtfById");
    }


    protected FieldsList getMultipleSelector() {
        FieldsList fields = new FieldsList();
        fields.addField("pimsCode", "999");
        fields.addField("sicovamCode", "12");
        return fields;
    }


    protected String getRequestAsXml(String requestId) {
        return "<select request_id=\"" + requestId + "\">"
               + "<id>selectCodificationPtfById</id>" + "<selector>"
               + "<field name='pimsCode'>999</field>" + "</selector>" + "</select>";
    }


    protected String getRequestMultiplePkAsXml(String requestId) {
        return "<select request_id=\"" + requestId + "\">"
               + "<id>selectCodificationPtfById</id>" + "<selector>"
               + "<field name='pimsCode'>999</field>"
               + "<field name='sicovamCode'>12</field>" + "</selector>" + "</select>";
    }


    protected String getRequestWithAttributesAsXml(String requestId) {
        return "<select request_id=\"" + requestId + "\">"
               + "<id>selectCodificationPtfById</id>" + "<selector>"
               + "<field name=\"pimsCode\">999</field>" + "</selector>" + "<attributes>"
               + "<name>sicovamCode</name>" + "</attributes>" + "</select>";
    }


    protected FieldsList getSelector() {
        FieldsList fields = new FieldsList();
        fields.addField("pimsCode", "999");
        return fields;
    }
}
