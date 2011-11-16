package net.codjo.mad.gui.request;
import net.codjo.mad.client.request.Request;
import net.codjo.mad.client.request.Result;
/**
 *
 * @author $Author: gaudefr $
 * @version $Revision: 1.3 $
 */
public interface RequestSubmiter {
    public Request buildRequest();


    public void setResult(Result result);
}
