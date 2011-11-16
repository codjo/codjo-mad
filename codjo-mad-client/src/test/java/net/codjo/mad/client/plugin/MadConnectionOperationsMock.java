package net.codjo.mad.client.plugin;
import net.codjo.mad.client.request.Request;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.client.request.Result;
import net.codjo.mad.client.request.ResultManager;
import net.codjo.test.common.LogString;
/**
 * Class Mock de {@link MadConnectionOperations}.
 */
public class MadConnectionOperationsMock implements MadConnectionOperations {
    private LogString log = new LogString();
    private Result resultMock = new Result();
    private ResultManager resultManagerMock = new ResultManager();


    public MadConnectionOperationsMock() {
    }


    public MadConnectionOperationsMock(LogString log) {
        this.log = log;
    }


    public Result sendRequest(Request request) throws RequestException {
        log.call("sendRequest", request.getHandlerId());
        return resultMock;
    }


    public Result sendRequest(Request request, long timeout) throws RequestException {
        log.call("sendRequest", request.getHandlerId(), timeout);
        return resultMock;
    }


    public ResultManager sendRequests(Request[] request) throws RequestException {
        log.call("sendRequest", toHandlerIds(request));
        return resultManagerMock;
    }


    public ResultManager sendRequests(Request[] request, long timeout) throws RequestException {
        log.call("sendRequest", toHandlerIds(request), timeout);
        return resultManagerMock;
    }


    public void mockSendRequest(Result result) {
        this.resultMock = result;
    }


    public void mockSendRequests(ResultManager resultManager) {
        this.resultManagerMock = resultManager;
    }


    private static String toHandlerIds(Request[] request) {
        StringBuffer buffer = new StringBuffer();
        if (request != null) {
            for (Request aRequest : request) {
                if (buffer.length() > 0) {
                    buffer.append(", ");
                }
                buffer.append(aRequest.getHandlerId());
            }
        }
        return buffer.toString();
    }
}
