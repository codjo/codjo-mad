package net.codjo.mad.gui.request.factory;
import net.codjo.mad.client.request.DeleteRequest;
import net.codjo.mad.client.request.FieldsList;
import net.codjo.mad.client.request.Request;
import java.util.Map;
/**
 * Comportement de update d'une DetailDataSource.
 *
 * @author $Author: gaudefr $
 * @version $Revision: 1.5 $
 */
public class DeleteFactory implements RequestFactory {
    private String id;
    private FieldsList pk;

    /**
     * Constructeur d'un selectBehavior.
     *
     * @param id Identifiant du type de la requete (e.g. selectCodificationPtfById)
     */
    public DeleteFactory(String id) {
        this.id = id;
    }


    public DeleteFactory() {}

    public void setId(String id) {
        this.id = id;
    }


    public void setExcludedFieldList(String[] excludedFieldList) {}


    public String getId() {
        return id;
    }


    public Request buildRequest(Map fields) {
        DeleteRequest request = new DeleteRequest();
        request.setId(id);
        request.setPrimaryKey(pk);
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
}
