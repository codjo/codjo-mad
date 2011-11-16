package net.codjo.mad.gui.request.factory;
import net.codjo.mad.client.request.FieldsList;
import net.codjo.mad.client.request.Request;
import net.codjo.mad.client.request.UpdateRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
/**
 * Comportement de update d'une DetailDataSource.
 */
public class UpdateFactory implements RequestFactory {
    private List<String> excludedFieldList = new ArrayList<String>();
    private String id;
    private FieldsList pk;


    /**
     * Constructeur d'un updateBehavior.
     *
     * @param id Identifiant du type de la requete (e.g. selectCodificationPtfById)
     */
    public UpdateFactory(String id) {
        this.id = id;
    }


    /**
     * Constructeur
     *
     * @param id                Identifiant du type de la requete
     * @param excludedFieldList liste des champs non mise-à-jours
     */
    public UpdateFactory(String id, String[] excludedFieldList) {
        this.id = id;
        setExcludedFieldList(excludedFieldList);
    }


    public UpdateFactory() {
    }


    public void setExcludedFieldList(String[] excludedFieldList) {
        this.excludedFieldList = Arrays.asList(excludedFieldList);
    }


    public void setId(String id) {
        this.id = id;
    }


    public List<String> getExcludedFieldList() {
        return excludedFieldList;
    }


    public String getId() {
        return id;
    }


    public Request buildRequest(Map fields) {
        UpdateRequest request = new UpdateRequest();
        request.setId(id);
        request.setPrimaryKey(pk);

        if (fields != null && fields.size() != 0) {
            for (Object object : fields.entrySet()) {
                Entry item = (Entry)object;
                String fieldName = (String)item.getKey();
                if (!pk.contains(fieldName) && !excludedFieldList.contains(fieldName)) {
                    request.addField(fieldName, (String)item.getValue());
                }
            }
        }
        return request;
    }


    /**
     * Initialisation du comportement.
     *
     * @param fieldsList liste des clé primaires.
     */
    public void init(FieldsList fieldsList) {
        this.pk = fieldsList;
    }


    public boolean needsSelector() {
        return true;
    }


    @Override
    public String toString() {
        return "UpdateFactory(" + id + ")";
    }
}
