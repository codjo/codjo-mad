package net.codjo.mad.client.plugin;
import net.codjo.mad.client.request.Request;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.client.request.Result;
import net.codjo.mad.client.request.ResultManager;
/**
 * @see MadConnectionPlugin#getOperations()
 */
public interface MadConnectionOperations {
    public Result sendRequest(Request request) throws RequestException;


    public Result sendRequest(Request request, long timeout) throws RequestException;


    public ResultManager sendRequests(Request[] request) throws RequestException;


    public ResultManager sendRequests(Request[] request, long timeout) throws RequestException;
}
