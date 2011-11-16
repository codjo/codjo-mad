package net.codjo.mad.client.request;
import net.codjo.mad.client.plugin.MadConnectionOperations;
import net.codjo.mad.client.request.util.CastorHelper;
import net.codjo.mad.client.request.util.ServerWrapper;
import net.codjo.mad.client.request.util.ServerWrapperFactory;
import net.codjo.test.common.AssertUtil;
import net.codjo.test.common.XmlUtil;
import net.codjo.test.common.fixture.Fixture;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import junit.framework.Assert;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
/**
 * Mock d'un {@link net.codjo.mad.client.request.util.ServerWrapper}.
 */
public class MadServerFixture implements Fixture {
    private WrapperMock wrapperMock = new WrapperMock();
    private List<String> xmlLastSentRequests = new LinkedList<String>();
    private ErrorResult errorResult;
    private Result lastServerResult;
    private QueueResultServerMock queueMock = new QueueResultServerMock();
    private ServerMock serverMock = queueMock;

    // ------------------------------------------------------------------------ Mock


    public void mockServerResult(String[] names, String[][] rows) {
        mockServerResult(createResult(names, rows));
    }


    public void mockServerResult(Result result) {
        mockServerResult(new Result[]{result});
    }


    public void mockServerResult(Result[] results) {
        queueServerResult(results);
    }


    public void queueServerResult(Result... results) {
        queueMock.results.add(results);
        lastServerResult = results != null ? results[0] : null;
    }


    public void mockServerError(String label) {
        errorResult = new ErrorResult();
        errorResult.setLabel(label);
    }


    public void setServerMock(ServerMock serverMock) {
        this.serverMock = serverMock;
    }

    // ------------------------------------------------------------------------ Misc


    public Result getServerResult() {
        return lastServerResult;
    }


    private String getXmlLastSentRequest() {
        if (xmlLastSentRequests.size() > 0) {
            return getXmlSentRequest(xmlLastSentRequests.size() - 1);
        }
        return null;
    }


    private String getXmlSentRequest(int index) {
        return xmlLastSentRequests.get(index);
    }


    public String getLastSentRequests() {
        if (xmlLastSentRequests.size() == 0) {
            return null;
        }
        return getSentRequests(xmlLastSentRequests.size() - 1);
    }


    public String getSentRequests(int index) {
        String requests = getXmlSentRequest(index).replaceAll("<audit>.*</audit>", "")
              .replaceAll("&#13;&#10;", "");
        return "<requests>" + extractTagValues(requests, 0, "<requests>", "</requests>")[0] + "</requests>";
    }


    public static Result createResult(String[] names, String[][] rows) {
        Result result = new Result();
        result.addPrimaryKey(names[0]);
        addRows(result, names, rows);
        result.setTotalRowCount(rows.length);
        return result;
    }

    // ------------------------------------------------------------------------ Assert


    public void assertRequestedHandlers(String[] handlerIds) {
        AssertUtil.assertEquals(handlerIds, extractRequestHandlerIds(getXmlLastSentRequest()));
    }


    public void assertSentRequests(String xml) {
        String lastSentRequests = getLastSentRequests();
        Assert.assertNotNull("Aucune requête n'a été envoyé au serveur", lastSentRequests);
        XmlUtil.assertEquivalent(xml, lastSentRequests);
    }


    public void assertSentRequests(String xml, int index) {
        XmlUtil.assertEquivalent(xml, getSentRequests(index));
    }


    public void assertResult(String[] names, String[][] rows, Result result) {
        assertResult(createResult(names, rows), result);
    }


    public void assertResult(Result expected, Result actual) {
        Assert.assertEquals(expected.getRowCount(), actual.getRowCount());

        int rowIndex = 0;
        for (Iterator iter = actual.getRows().iterator(); iter.hasNext(); rowIndex++) {
            Row row = (Row)iter.next();
            Row expectedRow = expected.getRow(rowIndex);

            Assert.assertEquals("Result field count", expectedRow.getFieldCount(), row.getFieldCount());

            for (int i = 0; i < expectedRow.getFieldCount(); i++) {
                Assert.assertEquals(expectedRow.getField(i).getValue(),
                                    row.getField(i).getValue());
            }
        }
    }
    // ------------------------------------------------------------------------ Factory


    public MadConnectionOperations getOperations() {
        return new SimpleMadConnectionOperations();
    }

    // ------------------------------------------------------------------------ Impl


    public void doSetUp() throws Exception {
        RequestIdManager.getInstance().reset();
        ServerWrapperFactory.setPrototype(wrapperMock);
    }


    public void doTearDown() throws Exception {
        xmlLastSentRequests.clear();
        queueMock.results.clear();
        lastServerResult = null;
        ServerWrapperFactory.setPrototype(null);
    }


    public ServerWrapper getServerWrapper() {
        return wrapperMock;
    }


    private Result[] buildDefaultResults(int count) {
        Result[] result = new Result[count];
        for (int i = 0; i < count; i++) {
            result[i] = createResult(new String[]{"myId", "name", "comment"},
                                     new String[][]{
                                           {"1", "bobo", "c1"},
                                           {"2", "boris", "c2"}});
        }
        return result;
    }


    private ResultManager toResultManager(Result[] results) {
        ResultManager manager = new ResultManager();
        if (errorResult != null) {
            errorResult.setRequestId(results[0].getRequestId());
            manager.setErrorResult(errorResult);
        }
        else {
            manager.setResults(Arrays.asList(results));
        }
        return manager;
    }


    private static void addRows(Result result, String[] names, String[][] rows) {
        for (String[] row : rows) {
            result.addRow(buildRow(row, names));
        }
    }


    private static Row buildRow(String[] row, String[] names) {
        Row rowResult = new Row();
        for (int j = 0; j < row.length; j++) {
            rowResult.addField(names[j], row[j]);
        }
        return rowResult;
    }


    private String[] extractRequestIds(String xml) {
        return extractTagValues(xml, 0, "request_id=\"", "\"");
    }


    private String[] extractRequestHandlerIds(String xml) {
        return extractTagValues(xml, 0, "<id>", "</id>");
    }


    private String[] extractTagValues(String xml, int count, String startTag, String endTag) {
        if (xml == null) {
            return new String[0];
        }

        int start = xml.indexOf(startTag);

        if (start == -1) {
            return new String[count];
        }

        start += startTag.length();

        int end = xml.indexOf(endTag, start);

        String[] results = extractTagValues(xml.substring(end), count + 1, startTag, endTag);

        results[count] = xml.substring(start, end);

        return results;
    }


    public class WrapperMock implements ServerWrapper {
        private boolean buildDefaultEnabled = true;

        public ServerWrapper copy() {
            return this;
        }


        public void close() {
        }


        public String sendWaitResponse(String xmlRequest, long timeout) {
            xmlLastSentRequests.add(xmlRequest);
            try {
                String[] requestIds = extractRequestIds(xmlRequest);
                Result[] results = serverMock.createResults(requestIds, xmlRequest, timeout);
                if (results == null && buildDefaultEnabled) {
                    results = buildDefaultResults(requestIds.length);
                }

                Assert.assertEquals("Incohérence entre le nombre de requête envoyé "
                                    + "et le nombre de résultat simulé",
                                    requestIds.length, results.length);

                for (int i = 0; i < requestIds.length; i++) {
                    results[i].setRequestId(requestIds[i]);
                }
                return resultManagerToString(toResultManager(results));
            }
            catch (Throwable e) {
                throw new Error("Erreur dans le mécanisme de mock", e);
            }
        }


        public String resultManagerToString(ResultManager manager)
              throws MarshalException, ValidationException, IOException, MappingException {
            return CastorHelper.marshaller(manager, "resultMapping.xml");
        }


        public void setBuildDefaultEnabled(boolean b) {
            buildDefaultEnabled = b;
        }
    }

    public static interface ServerMock {
        public Result[] createResults(String[] requestIds, String xmlRequest, long timeout);
    }

    public static class QueueResultServerMock implements ServerMock {
        Queue<Result[]> results = new LinkedList<Result[]>();


        public Result[] createResults(String[] requestIds, String xmlRequest, long timeout) {
            return results.poll();
        }
    }
    private class SimpleMadConnectionOperations implements MadConnectionOperations {

        public Result sendRequest(Request request) throws RequestException {
            ResultManager manager = sendRequests(new Request[]{request}, 1000);
            return manager.getResult(request.getRequestId());
        }


        public Result sendRequest(Request request, long timeout) throws RequestException {
            ResultManager manager = sendRequests(new Request[]{request}, timeout);
            return manager.getResult(request.getRequestId());
        }


        public ResultManager sendRequests(Request[] requests) throws RequestException {
            return sendRequests(requests, 1000);
        }


        public ResultManager sendRequests(Request[] request, long timeout) throws RequestException {
            ResultManager manager = new RequestSender(timeout).send(request, getServerWrapper());
            if (manager.hasError()) {
                throw new RequestException(manager.getErrorResult());
            }
            return manager;
        }
    }
}
