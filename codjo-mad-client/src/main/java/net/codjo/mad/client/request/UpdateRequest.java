package net.codjo.mad.client.request;
/**
 * Description of the Class
 *
 * @author $Author: gaudefr $
 * @version $Revision: 1.4 $
 */
public class UpdateRequest extends AbstractRequest {
    private FieldsList primaryKey;
    private FieldsList row;


    public UpdateRequest() {
    }


    public UpdateRequest(String handlerId, FieldsList primaryKey, FieldsList newRowValues) {
        setId(handlerId);
        setPrimaryKey(primaryKey);
        setRow(newRowValues);
    }


    public void setPrimaryKey(FieldsList primaryKey) {
        this.primaryKey = primaryKey;
    }


    public void setRow(FieldsList row) {
        this.row = row;
    }


    public FieldsList getPrimaryKey() {
        return primaryKey;
    }


    public FieldsList getRow() {
        return row;
    }


    public void addField(String fieldName, String fieldValue) {
        if (row == null) {
            row = new FieldsList();
        }
        row.addField(fieldName, fieldValue);
        setRow(row);
    }


    public void addPrimaryKey(String pkName, String value) {
        if (getPrimaryKey() == null) {
            setPrimaryKey(new FieldsList());
        }
        getPrimaryKey().addField(pkName, value);
    }


    public String toXml() {
        return toXml("updateMapping.xml");
    }
}
