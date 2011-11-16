package net.codjo.mad.client.request;
import net.codjo.mad.client.request.util.CastorHelper;
import org.apache.log4j.Logger;
/**
 * Classe facilitant l'implementation d'un Request.
 *
 * @author $Author: gaudefr $
 * @version $Revision: 1.4 $
 */
public abstract class AbstractRequest implements Request {
    private static final Logger LOG = Logger.getLogger(AbstractRequest.class);
    private String id;
    private String requestId;


    protected AbstractRequest() {
        setRequestId(RequestIdManager.getInstance().getNewRequestId());
    }


    public void setId(String id) {
        this.id = id;
    }


    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }


    public String getHandlerId() {
        return getId();
    }


    public String getId() {
        return id;
    }


    public String getRequestId() {
        return requestId;
    }


    @Override
    public String toString() {
        return getId();
    }


/*
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        AbstractRequest request = (AbstractRequest)object;
        return (id == null ? request.id == null : id.equals(request.id));
    }


    @Override
    public int hashCode() {
        return (id != null ? id.hashCode() : 0);
    }
*/


    public String toXml(String mappingFileName) {
        try {
            String xml = CastorHelper.marshaller(this, mappingFileName);
            // trouver le début de la balise racine en sautant la déclaration d'en-tête
            return xml.substring(xml.indexOf("<", 1));
        }
        catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new RuntimeException(ex.toString());
        }
    }
}
