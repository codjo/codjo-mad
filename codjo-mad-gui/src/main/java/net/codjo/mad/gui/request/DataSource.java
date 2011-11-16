package net.codjo.mad.gui.request;
import net.codjo.mad.client.request.FieldsList;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.client.request.RequestSender;
import net.codjo.mad.client.request.Result;
import net.codjo.mad.client.request.Row;
import net.codjo.mad.gui.framework.GuiContext;
import net.codjo.mad.gui.request.event.DataSourceListener;
import net.codjo.mad.gui.request.factory.RequestFactory;
import net.codjo.mad.gui.request.util.MultiRequestsHelper;
import java.beans.PropertyChangeListener;
import javax.swing.event.UndoableEditListener;
/**
 * Interface decrivant une source de donnée.
 *
 * @author $Author: gaudefr $
 * @version $Revision: 1.11 $
 */
public interface DataSource {
    public static final String LOAD_FACTORY_PROPERTY = "loadFactory";
    public static final String SELECTED_ROW_PROPERTY = "selectedRow";


    public RequestSender getRequestSender();


    public void setLoadResult(Result loadResult);


    public Result getLoadResult();


    public void setGuiContext(GuiContext guiContext);


    public GuiContext getGuiContext();


    public void setUpdateFactory(RequestFactory updateFactory);


    public void setInsertFactory(RequestFactory insertFactory);


    public void addDataSourceListener(DataSourceListener listener);


    public void setDefaultValue(String fieldName, String value);


    public String getDefaultValue(String fieldName);


    /**
     * Détermine si l'objet a été modifié depuis son chargement.
     *
     * @return <code>true</code> si l'objet a été modifié, sinon <code>false</code>
     */
    public boolean hasBeenUpdated();


    public RequestFactory getLoadFactory();


    public void removeUndoableEditListener(UndoableEditListener listener);


    public void addUndoableEditListener(UndoableEditListener listener);


    public void addPropertyChangeListener(PropertyChangeListener listener);


    public void removePropertyChangeListener(PropertyChangeListener listener);


    public void removePropertyChangeListener(String propertyName,
                                             PropertyChangeListener listener);


    public void addPropertyChangeListener(String propertyName,
                                          PropertyChangeListener listener);


    public void load() throws RequestException;


    public void save() throws RequestException;


    public void clear();


    public void setLoadManager(LoadManager loadManager);


    public LoadManager getLoadManager();


    /**
     * Deprecated.
     *
     * @param helper -
     *
     * @see LoadManager#addLoadRequestTo(net.codjo.mad.gui.request.util.MultiRequestsHelper)
     * @see #getLoadManager()
     * @deprecated Il faut utiliser Le LoadManager
     */
    @Deprecated
    public void addLoadRequestTo(MultiRequestsHelper helper);


    // Externalisation en cours dans {@link LoadManager}
    // avec selected row ?
    public void addSaveRequestTo(MultiRequestsHelper helper);


    /**
     * Applique la valeur 'fieldValue' au champ 'fieldName' pour tous le contenu du datasource. Si le champ
     * n'existe pas il est crée.
     *
     * @param fieldName  le champ
     * @param fieldValue la valeur
     */
    public void apply(String fieldName, String fieldValue);


    // End Externaliser
    public void setLoadFactory(RequestFactory loadFactory);


    public FieldsList getSelector();


    public void setSelector(FieldsList selector);


    public void setSelectedRow(Row selectedRow);


    public Row getSelectedRow();


    public void removeDataSourceListener(DataSourceListener listener);


    public void declare(final String fieldName);


    public int getTotalRowCount();


    public void startSnapshotMode();


    public void stopSnapshotMode();


    public void addController(Controller controler);


    public interface Controller {
        boolean control();
    }
}
