package net.codjo.mad.client.request;
/**
 * Description of the Class
 *
 * @author $Author: gaudefr $
 * @version $Revision: 1.4 $
 */
public class InsertRequest extends AbstractRequest {
    private FieldsList row;


    public InsertRequest() {
    }


    public InsertRequest(String handlerId, FieldsList row) {
        setId(handlerId);
        setRow(row);
    }


    public void setRow(FieldsList row) {
        this.row = row;
    }


    public FieldsList getRow() {
        return row;
    }


    public void addField(String field, String value) {
        if (row == null) {
            row = new FieldsList();
        }
        row.addField(field, value);
    }


    public String toXml() {
        return toXml("insertMapping.xml");
    }
}
