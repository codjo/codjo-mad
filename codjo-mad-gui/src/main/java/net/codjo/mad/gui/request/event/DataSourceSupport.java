package net.codjo.mad.gui.request.event;
import net.codjo.mad.client.request.Result;
import net.codjo.mad.gui.request.DataSource;
import net.codjo.mad.gui.request.util.MultiRequestsHelper;
import java.util.Vector;
/**
 * DOCUMENT ME!
 *
 * @author $Author: gaudefr $
 * @version $Revision: 1.9 $
 */
public class DataSourceSupport {
    private DataSource source;
    private Vector listeners;

    public DataSourceSupport() {}


    public DataSourceSupport(DataSource source) {
        this.source = source;
    }

    public void addDataSourceListener(DataSourceListener listener) {
        if (listeners == null) {
            listeners = new Vector();
        }
        listeners.add(listener);
    }


    public void fireBeforeLoadEvent(MultiRequestsHelper helper) {
        fireBeforeLoadEvent(new DataSourceEvent(source, helper));
    }


    public void fireBeforeLoadEvent(DataSourceEvent evt) {
        Vector targets = null;

        synchronized (this) {
            if (listeners != null) {
                targets = (Vector)listeners.clone();
            }
        }

        if (targets != null) {
            for (int i = 0; i < targets.size(); i++) {
                DataSourceListener target = (DataSourceListener)targets.elementAt(i);
                target.beforeLoadEvent(evt);
            }
        }
    }


    public void fireBeforeSaveEvent(MultiRequestsHelper helper) {
        fireBeforeSaveEvent(new DataSourceEvent(source, helper));
    }


    public void fireBeforeSaveEvent(DataSourceEvent evt) {
        Vector targets = null;

        synchronized (this) {
            if (listeners != null) {
                targets = (Vector)listeners.clone();
            }
        }

        if (targets != null) {
            for (int i = 0; i < targets.size(); i++) {
                DataSourceListener target = (DataSourceListener)targets.elementAt(i);
                target.beforeSaveEvent(evt);
            }
        }
    }


    public void fireLoadEvent(Result result) {
        fireLoadEvent(new DataSourceEvent(source, result));
    }


    public void fireLoadEvent(DataSourceEvent evt) {
        Vector targets = null;

        synchronized (this) {
            if (listeners != null) {
                targets = (Vector)listeners.clone();
            }
        }

        if (targets != null) {
            for (int i = 0; i < targets.size(); i++) {
                DataSourceListener target = (DataSourceListener)targets.elementAt(i);
                target.loadEvent(evt);
            }
        }
    }


    public void fireSaveEvent(Result result) {
        fireSaveEvent(new DataSourceEvent(source, result));
    }


    public void fireSaveEvent(DataSourceEvent evt) {
        Vector targets = null;

        synchronized (this) {
            if (listeners != null) {
                targets = (Vector)listeners.clone();
            }
        }

        if (targets != null) {
            for (int i = 0; i < targets.size(); i++) {
                DataSourceListener target = (DataSourceListener)targets.elementAt(i);
                target.saveEvent(evt);
            }
        }
    }


    public void removeDataSourceListener(DataSourceListener listener) {
        if (listeners == null) {
            return;
        }
        listeners.removeElement(listener);
    }
}
