package net.codjo.mad.gui.framework;
/**
 * Overview.
 *
 * @author $Author: gaudefr $
 * @version $Revision: 1.4 $
 */
public class SwingWorker extends net.codjo.gui.toolkit.swing.SwingWorker {
    private SwingRunnable swingRunnable;


    public SwingWorker(SwingRunnable swingRunnable) {
        this.swingRunnable = swingRunnable;
    }


    @Override
    public Object construct() {
        if (swingRunnable != null) {
            swingRunnable.run();
        }
        return null;
    }


    /**
     * Called on the event dispatching thread (not on the worker thread) after the <code>construct</code>
     * method has returned.
     */
    @Override
    public void finished() {
        if (swingRunnable != null) {
            swingRunnable.updateGui();
        }
    }
}
