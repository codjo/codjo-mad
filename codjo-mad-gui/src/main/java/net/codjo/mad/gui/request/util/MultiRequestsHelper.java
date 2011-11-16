package net.codjo.mad.gui.request.util;
import net.codjo.mad.client.plugin.MadConnectionOperations;
import net.codjo.mad.client.request.Request;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.client.request.RequestSender;
import net.codjo.mad.client.request.ResultManager;
import net.codjo.mad.gui.request.RequestSubmiter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/**
 * Assistant permettant d'envoyer plusieurs requetes aux serveurs d'un seul bloc.
 */
public class MultiRequestsHelper {
    private List<RequestSubmiter> submiters = new ArrayList<RequestSubmiter>();
    private RequestSender requestSender;
    private MadConnectionOperations operations;


    @Deprecated
    public MultiRequestsHelper(RequestSender requestSender) {
        this.requestSender = requestSender;
    }


    public MultiRequestsHelper(MadConnectionOperations operations) {
        this.operations = operations;
    }


    public List<RequestSubmiter> getSubmiters() {
        return Collections.unmodifiableList(submiters);
    }


    public void addSubmiter(RequestSubmiter submiter) {
        submiters.add(submiter);
    }


    public void sendRequest() throws RequestException {
        try {
            sendRequestImpl();
        }
        finally {
            submiters.clear();
        }
    }


    private void setRequestResult(final Request[] allRequest, final ResultManager manager) {
        for (int i = 0; i < allRequest.length; i++) {
            RequestSubmiter submiter = submiters.get(i);
            if (allRequest[i] != null) {
                submiter.setResult(manager.getResult(allRequest[i].getRequestId()));
            }
        }
    }


    private Request[] buildAllRequests() {
        Request[] allRequest = new Request[submiters.size()];
        for (int i = 0; i < allRequest.length; i++) {
            RequestSubmiter submiter = submiters.get(i);
            allRequest[i] = submiter.buildRequest();
        }

        return allRequest;
    }


    private Request[] filterNullRequest(Request[] allRequest) {
        List<Request> list = new ArrayList<Request>();
        for (Request request : allRequest) {
            if (request != null) {
                list.add(request);
            }
        }
        return list.toArray(new Request[list.size()]);
    }


    private void sendRequestImpl() throws RequestException {
        if (submiters.size() == 0) {
            return;
        }

        Request[] requests = filterNullRequest(buildAllRequests());
        if (requests.length == 0) {
            return;
        }

        ResultManager manager = requestSender != null ?
                                requestSender.send(requests) :
                                operations.sendRequests(requests);

        if (manager.hasError()) {
            throw new RequestException(manager.getErrorResult());
        }

        setRequestResult(requests, manager);
    }
}
