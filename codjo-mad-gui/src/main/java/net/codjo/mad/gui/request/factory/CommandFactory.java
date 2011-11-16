package net.codjo.mad.gui.request.factory;
import net.codjo.mad.client.request.CommandRequest;
import net.codjo.mad.client.request.Field;
import net.codjo.mad.client.request.FieldsList;
import net.codjo.mad.client.request.Request;
import java.util.Iterator;
import java.util.Map;
/**
 * factory d'une requete de type command.
 *
 * @author $Author: gaudefr $
 * @version $Revision: 1.6 $
 */
public class CommandFactory implements RequestFactory {
    private String id;
    private FieldsList selectors;

    public CommandFactory(String id) {
        this.id = id;
    }


    public CommandFactory() {}

    public void setExcludedFieldList(String[] excludedFieldList) {}


    public void setId(String id) {
        this.id = id;
    }


    public String getId() {
        return id;
    }


    /**
     * à utiliser en mettant null en paramètres. Signatureobligée par l'interface
     * RequestFactory.
     *
     * @param attributes
     *
     * @return retourne la Request créée
     */
    public Request buildRequest(Map attributes) {
        CommandRequest request = new CommandRequest();
        request.setId(id);

        if (selectors != null) {
            for (Iterator i = selectors.getFields().iterator(); i.hasNext();) {
                Field field = (Field)i.next();
                request.addSelector(field.getName(), field.getValue());
            }
        }
        if (attributes != null && attributes.size() != 0) {
            for (Iterator i = attributes.entrySet().iterator(); i.hasNext();) {
                Map.Entry item = (Map.Entry)i.next();
                String fieldName = (String)item.getKey();
                if (!selectors.contains(fieldName)) {
                    request.addSelector(fieldName, (String)item.getValue());
                }
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
        return true;
    }
}
