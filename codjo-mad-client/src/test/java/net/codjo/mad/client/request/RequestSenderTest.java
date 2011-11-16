package net.codjo.mad.client.request;
import net.codjo.mad.client.plugin.MadConnectionConfiguration;
import static net.codjo.mad.common.TestHelper.getFieldXmlTag;
import net.codjo.mad.client.request.util.ServerWrapper;
import net.codjo.mad.client.request.util.ServerWrapperFactory;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import junit.framework.AssertionFailedError;
import junit.framework.TestCase;
import org.easymock.MockControl;
/**
 */
public class RequestSenderTest extends TestCase {
    private static final String END_OF_CDATA_ESCAPED = "]]&gt";
    private static final String END_OF_CDATA_NOT_ESCAPED = "]]>";


    public void test_buildRequests() throws Exception {
        RequestSender sender = new RequestSender();
        String expected = getRequestAsXml();
        String requests = sender.buildRequests(getRequestListForTest());

        compare(flatten(expected), flatten(requests));
    }


    public void test_send() throws Exception {
        String errorResult =
              "<?xml version=\"1.0\"?>" + "<results>" + "<error request_id = \"2\">"
              + "<label>une erreur</label>"
              + "<type>class java.lang.RuntimeException</type>" + "</error>" + "</results>";

        MockControl<ServerWrapper> control = installServerWrapperMock(errorResult,
                                                                      MadConnectionConfiguration.DEFAULT_TIME_OUT);

        RequestSender sender = new RequestSender();

        sender.send(getRequestListForTest());

        control.verify();
    }


    public void test_sendWithWrapper() throws Exception {
        String errorResult =
              "<?xml version=\"1.0\"?>" + "<results>" + "<error request_id = \"2\">"
              + "<label>une erreur</label>"
              + "<type>class java.lang.RuntimeException</type>" + "</error>" + "</results>";

        MockControl<ServerWrapper> control = installServerWrapperMock(errorResult,
                                                                      MadConnectionConfiguration.DEFAULT_TIME_OUT);
        ServerWrapperFactory.setPrototype(null);

        RequestSender sender = new RequestSender();

        sender.send(getRequestListForTest(), control.getMock().copy());

        control.verify();
    }


    public void test_send_timeout() throws Exception {
        int timeout = 1000;

        MockControl<ServerWrapper> control = installServerWrapperMock(null, timeout);

        RequestSender sender = new RequestSender(timeout);

        try {
            sender.send(getRequestListForTest());
            fail();
        }
        catch (RequestException e) {
            assertEquals(RequestSender.TIMEOUT_ERROR_MESSAGE + " (> 1s).", e.getMessage());
        }

        control.verify();
    }


    @Override
    protected void tearDown() {
        ServerWrapperFactory.initWithDefaultPrototype();
    }


    private MockControl<ServerWrapper> installServerWrapperMock(String serverResult, long timeOut)
          throws RemoteException {
        MockControl<ServerWrapper> control = MockControl.createControl(ServerWrapper.class);
        ServerWrapper mockHelper = control.getMock();

        mockHelper.copy();
        control.setReturnValue(mockHelper);

        mockHelper.sendWaitResponse(getRequestAsXml(), timeOut);
        control.setReturnValue(serverResult, 1);

        mockHelper.close();
        control.setVoidCallable(1);

        control.replay();

        ServerWrapperFactory.setPrototype(mockHelper);
        return control;
    }


    private String getRequestAsXml() throws NumberFormatException {
        String requestId = RequestIdManager.getInstance().getNewRequestId();
        return "<?xml version=\"1.0\"?>" + "\n" + "<requests><audit><user>"
               + System.getProperty("user.name") + "</user>" + "</audit>"
               + "<insert request_id=\"" + String.valueOf(Integer.parseInt(requestId) + 1)
               + "\"><id>newCodificationPtf</id>" + "<row>"
               + getFieldXmlTag("SICOVAM", "6969")
               + getFieldXmlTag("ISIN", "0451")
               + getFieldXmlTag("LABEL", "COUCOU D") + "</row>" + "</insert>"
               + "<update request_id=\"" + String.valueOf(Integer.parseInt(requestId) + 2)
               + "\">" + "<id>updateCodificationPtf</id>" + "<primarykey>"
               + getFieldXmlTag("PIMS", "609") + "</primarykey>" + "<row>"
               + getFieldXmlTag("SICOVAM", "6969")
               + getFieldXmlTag("ISIN", "0451")
               + getFieldXmlTag("LABEL", "COUCOU D" + END_OF_CDATA_ESCAPED + ";") + "</row>" + "</update>"
               + "</requests>";
    }


    private Request[] getRequestListForTest() throws Exception {
        List<AbstractRequest> requests = new ArrayList<AbstractRequest>();
        InsertRequest insertRequest = new InsertRequest();
        insertRequest.setId("newCodificationPtf");

        List<Field> fields = new ArrayList<Field>();
        fields.add(new Field("SICOVAM", "6969"));
        fields.add(new Field("ISIN", "0451"));
        fields.add(new Field("LABEL", "COUCOU D"));
        FieldsList row = new FieldsList();
        row.setFields(fields);
        insertRequest.setRow(row);

        UpdateRequest updateRequest = new UpdateRequest();
        updateRequest.setId("updateCodificationPtf");

        List<Field> pkFields = new ArrayList<Field>();
        pkFields.add(new Field("PIMS", "609"));
        FieldsList primaryKey = new FieldsList();
        primaryKey.setFields(pkFields);
        updateRequest.setPrimaryKey(primaryKey);

        fields = new ArrayList<Field>();
        fields.add(new Field("SICOVAM", "6969"));
        fields.add(new Field("ISIN", "0451"));
        fields.add(new Field("LABEL", "COUCOU D" + END_OF_CDATA_NOT_ESCAPED));
        row = new FieldsList();
        row.setFields(fields);
        updateRequest.setRow(row);

        requests.add(insertRequest);
        requests.add(updateRequest);
        return requests.toArray(new Request[]{});
    }


    /**
     * Mise à plat de la chaîne de charactère (sans saut de ligne).
     *
     * @param str
     *
     * @return la chaîne mise à plat
     */
    private static String flatten(String str) {
        StringBuffer buffer = new StringBuffer();
        boolean previousWhite = true;
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (ch == '\r' || ch == '\n') {
            }
            else if (Character.isWhitespace(ch) || Character.isSpaceChar(ch)) {
                if (!previousWhite) {
                    buffer.append(" ");
                }
                previousWhite = true;
            }
            else {
                buffer.append(ch);
                previousWhite = false;
            }
        }

        return buffer.toString();
    }


    public static void compare(String expected, String actual) {
        if (expected.equals(actual)) {
            return;
        }
        for (int i = 0; i < expected.length(); i++) {
            if (!actual.startsWith(expected.substring(0, i))) {
                int min = Math.max(0, i - 30);
                String first =
                      "..." + expected.substring(min, Math.min(i + 30, expected.length()))
                      + "...";
                String second =
                      "..." + actual.substring(min, Math.min(i + 30, actual.length()))
                      + "...";
                throw new AssertionFailedError("Comparaison\n\texpected = " + first
                                               + "\n" + "\tactual   = " + second);
            }
        }
    }
}
