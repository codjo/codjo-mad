package net.codjo.mad.client.request;
/**
 * Interface des demandes clientes.
 *
 * @author $Author: gaudefr $
 * @version $Revision: 1.3 $
 */
public interface Request {
    public String toXml();


    public String getHandlerId();


    public String getRequestId();
}
