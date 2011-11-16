package net.codjo.mad.gui.framework;
/**
 * Classe responsable de ..
 *
 * @author $Author: gaudefr $
 * @version $Revision: 1.4 $
 */
public abstract class SwingRunnable implements Runnable {
    private String startLabel = "Traitement en cours...";
    private String stopLabel = null;

    protected SwingRunnable() {}


    protected SwingRunnable(String startLabel, String stopLabel) {
        this.startLabel = startLabel;
        this.stopLabel = stopLabel;
    }


    protected SwingRunnable(String startLabel) {
        this(startLabel, null);
    }

    public void setStartLabel(String startLabel) {
        this.startLabel = startLabel;
    }


    public void setStopLabel(String stopLabel) {
        this.stopLabel = stopLabel;
    }


    public String getStartLabel() {
        return startLabel;
    }


    public String getStopLabel() {
        return stopLabel;
    }


    public void updateGui() {}
}
