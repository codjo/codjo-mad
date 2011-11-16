package net.codjo.mad.gui.request.factory;
import net.codjo.mad.client.request.Request;
import net.codjo.test.common.XmlUtil;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;
/**
 *
 */
public class InsertFactoryTest extends TestCase {
    protected Map<String, String> fields;
    protected RequestFactory insertFactory;


    public InsertFactoryTest(String str) {
        super(str);
    }


    /**
     * Test que le comportement construit la bonne request lorsque des champs sont définit dans l'IHM.
     *
     * @throws Exception
     */
    public void test_buildRequest() throws Exception {
        fields = new HashMap<String, String>();
        fields.put("sicovamCode", "12");
        fields.put("label", "LABELL");

        Request request = insertFactory.buildRequest(fields);

        String xml = getRequestAsXml(request.getRequestId());

        XmlUtil.assertEquals(xml, request.toXml());
    }


    /**
     * Test la requete construite ne place pas les champs exclues dans les champs à inserer.
     *
     * @throws Exception
     */
    public void test_buildRequest_removeExcludedFields()
          throws Exception {
        fields = new HashMap<String, String>();
        fields.put("sicovamCode", "12");
        fields.put("label", "LABELL");
        fields.put("excludedField", "--");

        insertFactory.setExcludedFieldList(new String[]{"excludedField"});
        Request request = insertFactory.buildRequest(fields);

        String xml = getRequestAsXml(request.getRequestId());

        XmlUtil.assertEquals(xml, request.toXml());
    }


    public void test_buildRequest_emptyFields() throws Exception {
        fields = new HashMap<String, String>();

        try {
            insertFactory.buildRequest(fields);
            fail("le test ne doit pas passer");
        }
        catch (IllegalArgumentException ex) {
        }
    }


    @Override
    protected void setUp() {
        insertFactory = new InsertFactory("newCodificationPtf");
    }


    protected String getRequestAsXml(String requestId) {
        return "<insert request_id=\"" + requestId + "\">"
               + "<id>newCodificationPtf</id>"
               + "  <row>"
               + "    <field name='label'>LABELL</field>"
               + "    <field name='sicovamCode'>12</field>"
               + "  </row>"
               + "</insert>";
    }
}
