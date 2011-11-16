package net.codjo.mad.client.request.util;
import net.codjo.mad.client.request.InsertRequest;
import net.codjo.mad.client.request.Request;
import net.codjo.mad.client.request.RequestSender;
import net.codjo.mad.client.request.Result;
import net.codjo.mad.client.request.ResultManager;
import junit.framework.TestCase;
/**
 *
 */
public class RequestTestHelperTest extends TestCase {
    RequestTestHelper helper;


    public RequestTestHelperTest(String str) {
        super(str);
    }


    public void test_scenario_error() throws Exception {
        helper.setRequest(getRequestListForTest());
        helper.setResult(getRequestResultError());
        helper.activate();

        RequestSender sender = new RequestSender();
        ResultManager rm = sender.send(helper.getRequest());

        helper.verify();
        assertTrue(rm.hasError());
    }


    public void test_scenario_ok() throws Exception {
        helper.setRequest(getRequestListForTest());
        helper.setResult(getRequestResult());
        helper.activate();

        RequestSender sender = new RequestSender();
        ResultManager rm = sender.send(getRequestListForTest());

        helper.verify();
        Result result = rm.getResult(helper.getRequestId(0));
        assertNotNull(result);
        assertEquals("pimsCode", result.getPrimaryKey(0));
    }


    @Override
    protected void setUp() {
        helper = new RequestTestHelper();
    }


    @Override
    protected void tearDown() {
        helper.tearDown();
    }


    private Request[] getRequestListForTest() throws Exception {
        InsertRequest insertRequest = new InsertRequest();
        insertRequest.setId("newCodificationPtf");
        insertRequest.addField("SICOVAM", "6969");
        insertRequest.addField("ISIN", "0451");
        insertRequest.addField("LABEL", "COUCOU D");

        return new Request[]{insertRequest};
    }


    private String getRequestResult() {
        return "<?xml version=\"1.0\"?>" + "<results>" + "     <result request_id=\""
               + helper.getRequestId(0) + "\">" + "        <primarykey>"
               + "           <field name=\"pimsCode\"/>" + "        </primarykey>"
               + "        <row>" + "           <field name=\"pimsCode\">666</field>"
               + "        </row>" + "     </result>" + "</results>";
    }


    private String getRequestResultError() {
        return "<?xml version=\"1.0\"?>" + "<results>" + "  <error request_id = \""
               + helper.getRequestId(0) + "\">" + "    <label>une erreur</label>"
               + "    <type>class java.lang.RuntimeException</type>" + "  </error>"
               + "</results>";
    }
}
