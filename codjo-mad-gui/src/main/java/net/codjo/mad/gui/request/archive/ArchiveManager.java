package net.codjo.mad.gui.request.archive;
import net.codjo.mad.client.request.RequestException;
import java.awt.Component;
import java.util.Date;
import javax.swing.JInternalFrame;
/**
 * Interface decrivant l'historisation.
 *
 * @author $Author: gaudefr $
 * @version $Revision: 1.5 $
 */
public interface ArchiveManager {
    public void displayWhatsNewWindow(JInternalFrame frame);


    public Date askArchiveDate(Component aFrame);


    public void updateDSWithArchiveDate(Date archiveDate);


    public void doArchive(String archiveId, Date archiveDate)
            throws RequestException;


    public void startArchive(Date archiveDate);
}
