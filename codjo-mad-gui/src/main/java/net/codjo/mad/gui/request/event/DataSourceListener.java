package net.codjo.mad.gui.request.event;
import java.util.EventListener;
/**
 * Ecoute les event lie aux DataSource.
 * 
 * <p>
 * <b>NB</b> : L'evenement beforeLoad n'est pas forcement suivi d'un evenement Load (cas
 * d'un echec).
 * </p>
 *
 * @author $Author: gaudefr $
 * @version $Revision: 1.8 $
 */
public interface DataSourceListener extends EventListener {
    /**
     * Appelé après un chargement réussi.
     *
     * @param event
     */
    public void loadEvent(DataSourceEvent event);


    /**
     * Appelé avant de charger. Cette méthode est appelée avant l'envoi au serveur.
     *
     * @param event
     */
    public void beforeLoadEvent(DataSourceEvent event);


    /**
     * Appelé avant d'enregistrer en base. Cette méthode est appelée avant l'envoi au
     * serveur.
     *
     * @param event
     */
    public void beforeSaveEvent(DataSourceEvent event);


    /**
     * Appelé après un enregistrement en base.
     *
     * @param event
     */
    public void saveEvent(DataSourceEvent event);
}
