package net.codjo.mad.gui.request.factory;
import net.codjo.mad.client.request.FieldsList;
import net.codjo.mad.client.request.Request;
import java.util.Map;
/**
 * Definie un comportement (load, addSaveRequestTo, update ...) pour une DataSource.
 *
 * @author $Author: gaudefr $
 * @version $Revision: 1.4 $
 */
public interface RequestFactory {
    public Request buildRequest(Map fields);


    public boolean needsSelector();


    /**
     * Positionne la liste des champs a exclure des requêtes.
     *
     * @param excludedFieldList liste de nom de champs
     */
    public void setExcludedFieldList(String[] excludedFieldList);


    /**
     * Initialise la partie selector de la requête.
     *
     * @param selectors les selectors
     */
    public void init(FieldsList selectors);


    /**
     * Retourne l'id.
     *
     * @return id
     */
    public String getId();
}
