package net.codjo.mad.gui.request.factory;
import net.codjo.mad.client.request.FieldsList;
import net.codjo.mad.client.request.Request;
import net.codjo.test.common.XmlUtil;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;
/**
 *
 */
public class CommandFactoryTest extends TestCase {
    protected Map attributes;
    protected RequestFactory behavior;


    public CommandFactoryTest(String str) {
        super(str);
    }


    /**
     * Test que le comportement construit la bonne request lorsque des champs sont définit dans l'IHM.
     *
     * @throws Exception
     */
    public void test_buildRequest() throws Exception {
        behavior.init(getSelector());

        Request request = behavior.buildRequest(null);

        String xml = getRequestAsXml(request.getRequestId());

        XmlUtil.assertEquals(xml, request.toXml());
    }


    public void test_buildRequestLikeUpdate() throws Exception {
        behavior.init(getSelector());

        Map<String, String> fields = new HashMap<String, String>();
        fields.put("name", "12");
        fields.put("label", "LABELL");

        Request request = behavior.buildRequest(fields);

        String xml = getRequestLikeUpdateAsXml(request.getRequestId());

        XmlUtil.assertEquals(xml, request.toXml());
    }


    @Override
    protected void setUp() {
        behavior = new CommandFactory("commandPerfCalculator");
    }


    protected String getRequestAsXml(String requestId) {
        return "<command request_id=\"" + requestId + "\">"
               + "<id>commandPerfCalculator</id>" + "<args>"
               + "<field name='pimsCode'>999</field>"
               + "<field name='sicovamCode'>12</field>" + "</args>" + "</command>";
    }


    protected String getRequestLikeUpdateAsXml(String requestId) {
        return "<command request_id=\"" + requestId + "\">"
               + "<id>commandPerfCalculator</id>" + "<args>"
               + "<field name='pimsCode'>999</field>"
               + "<field name='sicovamCode'>12</field>"
               + "<field name='label'>LABELL</field>"
               + "<field name='name'>12</field>" + "</args>" + "</command>";
    }


    protected FieldsList getSelector() {
        FieldsList fields = new FieldsList();
        fields.addField("pimsCode", "999");
        fields.addField("sicovamCode", "12");
        return fields;
    }
}
