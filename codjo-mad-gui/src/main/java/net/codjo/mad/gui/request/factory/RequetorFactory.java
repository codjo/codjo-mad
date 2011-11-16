package net.codjo.mad.gui.request.factory;
import net.codjo.mad.client.request.FieldsList;
import net.codjo.mad.client.request.Request;
import net.codjo.mad.client.request.SelectRequest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
/**
 * Comportement de select pour le requêteur.
 */
public class RequetorFactory extends SelectFactory {
    private String sqlQuery;
    private String[] displayedColNames;
    private Map linkedFieldsTypeMap = new HashMap();

    public RequetorFactory() {}


    public RequetorFactory(String id) {
        super(id);
    }

    public void init(FieldsList selectors) {
        // Bloque selectors
    }


    public Request buildRequest(Map attributes) {
        SelectRequest request = new SelectRequest();

        request.setId(getId());
        request.setAttributes(displayedColNames);
        request.addSelector("sqlQuery", sqlQuery);
        addSelectorsForLinkedFields(request);

        return request;
    }


    /**
     * Pour chaque champ affiché appartenant à une table liée, on rajoute un selector. Le
     * nom du selector est le nom Java du champ, et la valeur est son type SQL Java
     * (entier).
     *
     * @param request la requête de sélection
     */
    private void addSelectorsForLinkedFields(SelectRequest request) {
        for (Iterator iter = linkedFieldsTypeMap.keySet().iterator(); iter.hasNext();) {
            String fieldName = (String)iter.next();
            String fieldType = (String)linkedFieldsTypeMap.get(fieldName);

            request.addSelector(fieldName, fieldType);
        }
    }


    public void setSqlQuery(String sqlQuery) {
        this.sqlQuery = sqlQuery;
    }


    public void setDisplayedColNames(String[] displayedColNames) {
        this.displayedColNames = displayedColNames;
    }


    public void addLinkedFieldType(String fieldName, int fieldType) {
        linkedFieldsTypeMap.put(fieldName, String.valueOf(fieldType));
    }
}
