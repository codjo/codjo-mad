package net.codjo.mad.gui.request.util;

import net.codjo.mad.client.plugin.MadConnectionOperations;
import net.codjo.mad.client.request.Request;
import net.codjo.mad.client.request.RequestSender;
import net.codjo.mad.client.request.Result;
import net.codjo.mad.client.request.ResultManager;
import net.codjo.mad.client.request.SelectRequest;
import net.codjo.mad.gui.request.RequestSubmiter;
import org.junit.Test;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.stub;
import static org.mockito.Mockito.verify;

public class MultiRequestsHelperTest {

    @Test
    public void test_requestSender() throws Exception {
        RequestSender requestSender = mock(RequestSender.class);
        ResultManager resultManager = mock(ResultManager.class);
        stub(requestSender.send((Request[])anyObject())).toReturn(resultManager);

        MultiRequestsHelper multiRequestsHelper = new MultiRequestsHelper(requestSender);

        RequestSubmiter requestSubmiter = mock(RequestSubmiter.class);
        SelectRequest request = new SelectRequest();
        stub(requestSubmiter.buildRequest()).toReturn(request);

        multiRequestsHelper.addSubmiter(requestSubmiter);

        multiRequestsHelper.sendRequest();

        verify(requestSender).send((Request[])anyObject());
        verify(requestSubmiter).buildRequest();
        verify(requestSubmiter).setResult((Result)anyObject());
    }


    @Test
    public void test_madConnectionOperations() throws Exception {
        MadConnectionOperations operations = mock(MadConnectionOperations.class);
        ResultManager resultManager = mock(ResultManager.class);
        stub(operations.sendRequests((Request[])anyObject())).toReturn(resultManager);

        MultiRequestsHelper multiRequestsHelper = new MultiRequestsHelper(operations);

        RequestSubmiter requestSubmiter = mock(RequestSubmiter.class);
        SelectRequest request = new SelectRequest();
        stub(requestSubmiter.buildRequest()).toReturn(request);

        multiRequestsHelper.addSubmiter(requestSubmiter);

        multiRequestsHelper.sendRequest();

        verify(operations).sendRequests((Request[])anyObject());
        verify(requestSubmiter).buildRequest();
        verify(requestSubmiter).setResult((Result)anyObject());
    }
}
