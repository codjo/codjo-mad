package net.codjo.mad.gui.framework;
import net.codjo.gui.toolkit.progressbar.ProgressBarLabel;
/**
 * Classe Utilitire pour la classe SwingWorker.
 */
class SwingWorkerUtil {
    private ProgressBarLabel infoLabel;


    public void setInfoLabel(ProgressBarLabel infoLabel) {
        this.infoLabel = infoLabel;
    }


    public ProgressBarLabel getInfoLabel() {
        return infoLabel;
    }


    public void executeTask(SwingRunnable task) {
        if (getInfoLabel() != null) {
            getInfoLabel().start(task.getStartLabel());
        }
        new SwingWorker(new SwingRunnableProxy(task)).start();
    }


    private class SwingRunnableProxy extends SwingRunnable {
        private SwingRunnable task;


        SwingRunnableProxy(SwingRunnable task) {
            this.task = task;
        }


        @Override
        public void updateGui() {
            if (infoLabel != null) {
                getInfoLabel().stop(task.getStopLabel());
            }
            task.updateGui();
        }


        public void run() {
            task.run();
        }
    }
}
