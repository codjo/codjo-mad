package net.codjo.mad.client.request.util;
import net.codjo.mad.client.plugin.MadConnectionConfiguration;
import net.codjo.mad.client.request.AbstractRequest;
import net.codjo.mad.client.request.Request;
import net.codjo.mad.client.request.RequestIdManager;
import net.codjo.mad.client.request.RequestSender;
import junit.framework.AssertionFailedError;
import org.easymock.MockControl;
/**
 * Classe pour faciliter l'écriture des tests utilisant un RequesSender
 *
 * @author Boris
 * @version $Revision: 1.2 $
 */
public class RequestTestHelper {
    private MockControl control;
    private ServerWrapper mockHelper;
    private Request[] requests;
    private String result;


    public RequestTestHelper() {
        control = MockControl.createControl(ServerWrapper.class);
        mockHelper = (ServerWrapper)control.getMock();
    }


    public void setRequest(Request[] requests) {
        this.requests = requests;

        int lastRequestId =
              Integer.parseInt(RequestIdManager.getInstance().getNewRequestId());
        for (Request request : requests) {
            ((AbstractRequest)request).setRequestId(Integer.toString(++lastRequestId));
        }
    }


    public void setResult(String result) {
        this.result = result;
    }


    public Request[] getRequest() {
        return requests;
    }


    public String getRequestId(int requestIndex) {
        return requests[requestIndex].getRequestId();
    }


    public void activate() throws Exception {
        // Copy effectue par la factory JMSHelperFactory
        mockHelper.copy();
        control.setReturnValue(mockHelper);

        // Initialisation faite par le RequesSender

        // Envoie + reception
        mockHelper.sendWaitResponse(getRequestAsXml(), MadConnectionConfiguration.DEFAULT_TIME_OUT);
        control.setReturnValue(result, 1);

        // Fermeture
        mockHelper.close();
        control.setVoidCallable(1);

        // Active EasyMock
        control.replay();

        // Change le prototype
        ServerWrapperFactory.setPrototype(mockHelper);
    }


    public void tearDown() {
        ServerWrapperFactory.initWithDefaultPrototype();
    }


    public void verify() throws AssertionFailedError {
        control.verify();
    }


    String getRequestAsXml() {
        return new RequestSender().buildRequests(requests);
    }
}
