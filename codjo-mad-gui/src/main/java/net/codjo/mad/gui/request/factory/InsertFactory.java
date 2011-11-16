package net.codjo.mad.gui.request.factory;
import net.codjo.mad.client.request.FieldsList;
import net.codjo.mad.client.request.InsertRequest;
import net.codjo.mad.client.request.Request;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
/**
 * Comportement de update d'une DetailDataSource.
 *
 * @author $Author: gaudefr $
 * @version $Revision: 1.5 $
 */
public class InsertFactory implements RequestFactory {
    private List excludedFieldList = new ArrayList();
    private String id;

    /**
     * Constructeur d'un selectBehavior.
     *
     * @param id Identifiant du type de la requete (e.g. selectCodificationPtfById)
     */
    public InsertFactory(String id) {
        this.id = id;
    }


    /**
     * Constructeur
     *
     * @param id Identifiant du type de la requete
     * @param excludedFieldList liste des champs non inséré.
     */
    public InsertFactory(String id, String[] excludedFieldList) {
        this.id = id;
        setExcludedFieldList(excludedFieldList);
    }


    public InsertFactory() {}

    public void setExcludedFieldList(String[] excludedFieldList) {
        this.excludedFieldList = Arrays.asList(excludedFieldList);
    }


    public void setId(String id) {
        this.id = id;
    }


    public String[] getExcludedFieldList() {
        return (String[])excludedFieldList.toArray(new String[] {});
    }


    public String getId() {
        return id;
    }


    public Request buildRequest(Map fields) {
        if (fields == null || fields.size() == 0) {
            throw new IllegalArgumentException();
        }
        InsertRequest request = new InsertRequest();
        request.setId(id);

        for (Iterator i = fields.entrySet().iterator(); i.hasNext();) {
            Map.Entry item = (Map.Entry)i.next();
            if (!excludedFieldList.contains(item.getKey())) {
                request.addField((String)item.getKey(), (String)item.getValue());
            }
        }
        return request;
    }


    /**
     * Initialisation du comportement.
     *
     * @param pk liste des clé primaires.
     */
    public void init(FieldsList pk) {}


    public boolean needsSelector() {
        return false;
    }
}
