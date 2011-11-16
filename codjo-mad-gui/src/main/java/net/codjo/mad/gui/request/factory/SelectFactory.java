package net.codjo.mad.gui.request.factory;
import net.codjo.mad.client.request.Field;
import net.codjo.mad.client.request.FieldsList;
import net.codjo.mad.client.request.Request;
import net.codjo.mad.client.request.SelectRequest;
import java.util.Iterator;
import java.util.Map;
/**
 * Comportement de select d'une DetailDataSource.
 *
 * @author $Author: gaudefr $
 * @version $Revision: 1.6 $
 */
public class SelectFactory implements RequestFactory {
    private String id;
    private FieldsList selectors;
    private boolean needsSelectors = true;

    /**
     * Constructeur d'un selectBehavior.
     *
     * @param id Identifiant du type de la requete (e.g. selectCodificationPtfById)
     */
    public SelectFactory(String id) {
        this.id = id;
    }


    public SelectFactory(String id, boolean needsSelectors) {
        this.id = id;
        this.needsSelectors = needsSelectors;
    }


    public SelectFactory() {}

    public void setExcludedFieldList(String[] excludedFieldList) {}


    public void setId(String id) {
        this.id = id;
    }


    public String getId() {
        return id;
    }


    public Request buildRequest(Map attributes) {
        SelectRequest request = new SelectRequest();
        request.setId(id);

        if (attributes != null && attributes.size() != 0) {
            request.setAttributes((String[])attributes.keySet().toArray(new String[] {}));
        }

        if (selectors != null) {
            for (Iterator i = selectors.getFields().iterator(); i.hasNext();) {
                Field field = (Field)i.next();
                request.addSelector(field.getName(), field.getValue());
            }
        }

        return request;
    }


    /**
     * Initialisation du comportement.
     *
     * @param fieldsList liste de selecteur.
     */
    public void init(FieldsList fieldsList) {
        this.selectors = fieldsList;
    }


    public boolean needsSelector() {
//        return true;
        return needsSelectors;
    }


    public String toString() {
        return "SelectFactory(" + id + ")";
    }
}
