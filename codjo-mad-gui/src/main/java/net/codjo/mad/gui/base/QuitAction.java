package net.codjo.mad.gui.base;
import net.codjo.mad.gui.framework.AbstractGuiAction;
import net.codjo.mad.gui.framework.GuiContext;
import net.codjo.mad.gui.framework.GuiEvent;
/**
 * Action permettant de quitter une application.
 */
public class QuitAction extends AbstractGuiAction {
    public QuitAction(GuiContext ctxt) {
        super(ctxt, "Quitter", "Quitter l'application", "exit");
    }


    public void actionPerformed(java.awt.event.ActionEvent arg0) {
        sendEvent(GuiEvent.QUIT);
    }
}
