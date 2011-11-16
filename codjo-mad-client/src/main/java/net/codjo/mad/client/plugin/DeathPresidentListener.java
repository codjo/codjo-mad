package net.codjo.mad.client.plugin;
import net.codjo.plugin.common.ApplicationCore;
import org.apache.log4j.Logger;
/**
 * Listener ecoutant la mort du president pour déclencher un system exit.
 */
public class DeathPresidentListener implements PresidentListener {
    public static final int INTERNAL_ERROR_PRESIDENT_KILLED = 201;
    private final Logger logger = Logger.getLogger(DeathPresidentListener.class);
    private ApplicationCore applicationCore;


    public DeathPresidentListener(ApplicationCore core) {
        this.applicationCore = core;
    }


    public void stopped() {
        if (!applicationCore.isStopping()) {
            new Thread(new Runnable() {
                public void run() {
                    silentStop();
                }
            }).start();
        }
    }


    void silentStop() {
        if (!applicationCore.isStopping()) {
            //noinspection finally
            try {
                applicationCore.stop();
            }
            catch (Exception e) {
                logger.error(
                      "Impossible d'arreter proprement et en urgence le container !"
                      + e.getLocalizedMessage());
            }
            finally {
                System.exit(INTERNAL_ERROR_PRESIDENT_KILLED);
            }
        }
    }
}
