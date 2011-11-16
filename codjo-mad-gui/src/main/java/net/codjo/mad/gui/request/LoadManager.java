package net.codjo.mad.gui.request;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.gui.request.event.DataSourceSupport;
import net.codjo.mad.gui.request.util.MultiRequestsHelper;
/**
 * Interface décrivant une classe responsable du chargement d'un {@link DataSource}.
 *
 * @see ListDataSource#setLoadManager(LoadManager)
 */
public interface LoadManager {
    /**
     * Lance la phase de chargement. Le load manager est responsable de l'emission des
     * evenements <tt>beforeLoadEvent</tt> et <tt>loadEvent</tt>.
     *
     * @param support Objet permettant de lancer les events.
     *
     * @throws RequestException Erreur durant le chargement.
     *
     * @see net.codjo.mad.gui.request.event.DataSourceListener#beforeLoadEvent(net.codjo.mad.gui.request.event.DataSourceEvent)
     * @see net.codjo.mad.gui.request.event.DataSourceListener#loadEvent(net.codjo.mad.gui.request.event.DataSourceEvent)}
     */
    void doLoad(DataSourceSupport support) throws RequestException;


    /**
     * Ajout la (ou les) requetes de chargement dans le <tt>helper</tt>.
     *
     * @param helper Receptacle de requête
     */
    void addLoadRequestTo(MultiRequestsHelper helper);
}
