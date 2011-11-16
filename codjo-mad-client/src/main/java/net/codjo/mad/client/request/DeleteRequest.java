package net.codjo.mad.client.request;
/**
 * Description of the Class
 *
 * @author $Author: gaudefr $
 * @version $Revision: 1.4 $
 */
public class DeleteRequest extends AbstractRequest {
    private FieldsList primaryKey;


    public DeleteRequest() {
    }


    public DeleteRequest(String handlerId, FieldsList primaryKey) {
        setId(handlerId);
        setPrimaryKey(primaryKey);
    }


    public void setPrimaryKey(FieldsList primaryKey) {
        this.primaryKey = primaryKey;
    }


    public FieldsList getPrimaryKey() {
        return primaryKey;
    }


    public String toXml() {
        return toXml("deleteMapping.xml");
    }
}
