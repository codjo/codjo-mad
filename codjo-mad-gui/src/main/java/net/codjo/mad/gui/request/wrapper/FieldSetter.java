package net.codjo.mad.gui.request.wrapper;
import net.codjo.mad.gui.request.GuiWrapperUndoListener;
/**
 *
 */
public class FieldSetter {
    private GuiWrapper guiWrapper;
    private String guiValue;
    private GuiWrapperUndoListener guiUndoListener;


    public FieldSetter(GuiWrapper guiWrapper, String guiValue, GuiWrapperUndoListener guiUndoListener) {
        this.guiWrapper = guiWrapper;
        this.guiValue = guiValue;
        this.guiUndoListener = guiUndoListener;
    }


    public void run() {
        guiUndoListener.stopPosting();
        try {
            guiWrapper.setXmlValue(guiValue);
        }
        finally {
            guiUndoListener.startPosting();
        }
    }
}
