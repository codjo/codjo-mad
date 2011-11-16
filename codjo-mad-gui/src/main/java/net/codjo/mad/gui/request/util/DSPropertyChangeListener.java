package net.codjo.mad.gui.request.util;
import net.codjo.mad.gui.request.DetailDataSource;
import net.codjo.mad.gui.request.event.DataSourceAdapter;
import net.codjo.mad.gui.request.event.DataSourceEvent;
import java.beans.PropertyChangeListener;
/**
 * PropertyChangeListener optimisé. Ce Listener ne sera appele qu'une seule fois apres le
 * chargement et enseuite a chaque changement de valeur.
 * 
 * <p>
 * <b>NB</b> : La methode propertyChange sera appele apres chaque chargement avec un
 * event null.
 * </p>
 *
 * @author $Author: gaudefr $
 * @version $Revision: 1.4 $
 */
public abstract class DSPropertyChangeListener extends DataSourceAdapter
    implements PropertyChangeListener {
    private DetailDataSource dataSource;
    private String[] properties;

    /**
     * Constructeur
     *
     * @param ds Le datasource contenant les properties a écouter.
     * @param properties Les property a ecouter.
     */
    protected DSPropertyChangeListener(DetailDataSource ds, String[] properties) {
        this.dataSource = ds;
        this.properties = properties;
        ds.addDataSourceListener(this);
        addPropertyListener();
    }

    public void beforeLoadEvent(DataSourceEvent event) {
        removePropertyListener();
    }


    public void loadEvent(DataSourceEvent event) {
        propertyChange(null);
        addPropertyListener();
    }


    protected DetailDataSource getDataSource() {
        return dataSource;
    }


    private void removePropertyListener() {
        for (int i = 0; i < properties.length; i++) {
            dataSource.removePropertyChangeListener(properties[i], this);
        }
    }


    private void addPropertyListener() {
        for (int i = 0; i < properties.length; i++) {
            dataSource.addPropertyChangeListener(properties[i], this);
        }
    }
}
