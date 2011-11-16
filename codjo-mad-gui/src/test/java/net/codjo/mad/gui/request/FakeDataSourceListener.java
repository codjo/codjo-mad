package net.codjo.mad.gui.request;
import net.codjo.mad.gui.request.event.DataSourceEvent;
import net.codjo.mad.gui.request.event.DataSourceListener;
/**
 * DOCUMENT ME!
 *
 * @version $Revision: 1.6 $
 */
public class FakeDataSourceListener implements DataSourceListener {
    protected int nbOfCall = 0;
    protected DataSourceEvent lastEvt;
    protected DataSourceEvent lastBeforeLoadEvent;
    protected DataSourceEvent lastLoadEvent;
    boolean beforeLoadEvent = false;
    boolean beforeSaveEvent = false;
    boolean loadEvent = false;
    boolean saveEvent = false;
    protected long saveTime = 0;

    public void beforeLoadEvent(DataSourceEvent event) {
        beforeLoadEvent = true;
        this.lastBeforeLoadEvent = event;
    }


    public void beforeSaveEvent(DataSourceEvent event) {
        beforeSaveEvent = true;
    }


    public boolean hasBeenCalled() {
        return nbOfCall > 0;
    }


    public void loadEvent(DataSourceEvent event) {
        loadEvent = true;
        nbOfCall++;
        this.lastEvt = event;
        this.lastLoadEvent = event;
    }


    public void saveEvent(DataSourceEvent event) {
        saveEvent = true;
        nbOfCall++;
        this.lastEvt = event;
        try {
            Thread.sleep(100);
        }
        catch (InterruptedException ex) {
            ; // En cas d'echec
        }
        saveTime = System.currentTimeMillis();
    }
}
