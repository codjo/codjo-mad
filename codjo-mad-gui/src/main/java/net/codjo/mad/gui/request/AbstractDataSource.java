package net.codjo.mad.gui.request;
import net.codjo.mad.client.request.FieldsList;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.client.request.RequestSender;
import net.codjo.mad.client.request.Result;
import net.codjo.mad.client.request.Row;
import net.codjo.mad.gui.framework.GuiContext;
import net.codjo.mad.gui.request.event.DataSourceListener;
import net.codjo.mad.gui.request.event.DataSourceSupport;
import net.codjo.mad.gui.request.factory.RequestFactory;
import net.codjo.mad.gui.request.factory.SelectFactory;
import net.codjo.mad.gui.request.util.MultiRequestsHelper;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoableEdit;
import javax.swing.undo.UndoableEditSupport;
/**
 * Classe abstraite facilitant l'implantation d'un DataSource.
 *
 * @author $Author: gaudefr $
 * @version $Revision: 1.11 $
 */
abstract class AbstractDataSource implements DataSource {
    public static final String NULL = "null";

    private Map<String, String> defaultValues = new HashMap<String, String>();
    private DataSourceSupport dataSourceSupport = new DataSourceSupport(this);
    private PropertyChangeSupport propertySupport = new PropertyChangeSupport(this);
    private RequestSender requestSender = new RequestSender();
    private UndoableEditSupport undoSupport = new UndoableEditSupport();
    private GuiContext guiContext;
    private RequestFactory loadFactory;
    private FieldsList selector;
    private LoadManager loadManager;
    private List<Controller> controllerList = new ArrayList<Controller>();
    private String entityName;
    protected SelectionDataSource selection;


    public String getEntityName() {
        return entityName;
    }


    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }


    public void setDefaultValue(String fieldName, String value) {
        if (value == null) {
            throw new IllegalArgumentException();
        }
        defaultValues.put(fieldName, value);
    }


    public void setGuiContext(GuiContext guiContext) {
        this.guiContext = guiContext;
    }


    public void setLoadFactory(RequestFactory loadFactory) {
        RequestFactory old = getLoadFactory();
        this.loadFactory = loadFactory;
        propertySupport.firePropertyChange(LOAD_FACTORY_PROPERTY, old, getLoadFactory());
    }


    public void setLoadFactoryId(String selectid) {
        setLoadFactory(new SelectFactory(selectid));
    }


    public void setRequestSender(RequestSender requestSender) {
        this.requestSender = requestSender;
    }


    public void setSelectedRow(Row selectedRow) {
        if (selectedRow != null) {
            setSelection(new SelectionDataSource(selectedRow));
        }
        else {
            setSelection(null);
        }
    }


    public void setSelection(SelectionDataSource selectionDataSource) {
        SelectionDataSource oldSelection = selection;
        selection = selectionDataSource;
        propertySupport.firePropertyChange(SELECTED_ROW_PROPERTY, oldSelection, selection);
    }


    public void setSelector(FieldsList selector) {
        this.selector = selector;
    }


    public String getDefaultValue(String fieldName) {
        return defaultValues.get(fieldName);
    }


    public GuiContext getGuiContext() {
        return guiContext;
    }


    public RequestFactory getLoadFactory() {
        return loadFactory;
    }


    @Deprecated
    public RequestSender getRequestSender() {
        return requestSender;
    }


    public Row getSelectedRow() {
        if (selection != null) {
            return selection.getRow();
        }
        return null;
    }


    public FieldsList getSelector() {
        return selector;
    }


    public void setLoadManager(LoadManager loadManager) {
        this.loadManager = loadManager;
    }


    public LoadManager getLoadManager() {
        return loadManager;
    }


    public void addDataSourceListener(DataSourceListener listener) {
        getDataSourceSupport().addDataSourceListener(listener);
    }


    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(listener);
    }


    public void addPropertyChangeListener(String propertyName,
                                          PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(propertyName, listener);
    }


    public void addUndoableEditListener(UndoableEditListener listener) {
        undoSupport.addUndoableEditListener(listener);
    }


    public void removeDataSourceListener(DataSourceListener listener) {
        getDataSourceSupport().removeDataSourceListener(listener);
    }


    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.removePropertyChangeListener(listener);
    }


    public void removePropertyChangeListener(String propertyName,
                                             PropertyChangeListener listener) {
        propertySupport.removePropertyChangeListener(propertyName, listener);
    }


    public void removeUndoableEditListener(UndoableEditListener listener) {
        undoSupport.removeUndoableEditListener(listener);
    }


    public void load() throws RequestException {
        getLoadManager().doLoad(getDataSourceSupport());
    }


    public void save() throws RequestException {
        for (Controller controller : controllerList) {
            boolean canSave = controller.control();
            if (!canSave) {
                return;
            }
        }
        MultiRequestsHelper helper = new MultiRequestsHelper(getRequestSender());
        addSaveRequestTo(helper);
        helper.sendRequest();
    }


    protected DataSourceSupport getDataSourceSupport() {
        return dataSourceSupport;
    }


    protected void fireBeforeLoadEvent(MultiRequestsHelper helper) {
        getDataSourceSupport().fireBeforeLoadEvent(helper);
    }


    protected void fireBeforeSaveEvent(MultiRequestsHelper helper) {
        getDataSourceSupport().fireBeforeSaveEvent(helper);
    }


    protected void fireLoadEvent(Result result) {
        getDataSourceSupport().fireLoadEvent(result);
    }


    protected void firePropertyChange(String propertyName, Object oldValue,
                                      Object newValue) {
        propertySupport.firePropertyChange(propertyName, oldValue, newValue);
    }


    protected void firePropertyChange(PropertyChangeEvent evt) {
        propertySupport.firePropertyChange(evt);
    }


    protected void fireSaveEvent(Result result) {
        getDataSourceSupport().fireSaveEvent(result);
    }


    protected Row newRowWhithDefaultValues() {
        Row newRow = new Row();
        newRow.addAllField(defaultValues);
        return newRow;
    }


    protected void postEdit(UndoableEdit event) {
        undoSupport.postEdit(event);
    }


    public void addLoadRequestTo(MultiRequestsHelper helper) {
        getLoadManager().addLoadRequestTo(helper);
    }


    public void addController(Controller controler) {
        controllerList.add(controler);
    }
}
