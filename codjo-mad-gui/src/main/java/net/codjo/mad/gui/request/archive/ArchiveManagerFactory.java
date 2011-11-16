package net.codjo.mad.gui.request.archive;
import net.codjo.mad.gui.request.DetailDataSource;
/**
 * Interface decrivant la factory permettant de construire un manager d'historisation.
 *
 * @author $Author: gaudefr $
 * @version $Revision: 1.3 $
 */
public interface ArchiveManagerFactory {
    ArchiveManager newArchiveManager(DetailDataSource ds);
}
