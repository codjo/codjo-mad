package net.codjo.mad.gui.framework;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.gui.request.GuiLogic;
import net.codjo.mad.gui.request.PreferenceFactory;
/**
 * Logique de base d'une fenêtre List.
 */
public abstract class AbstractListLogic implements GuiLogic<SimpleListGui> {
    private final SimpleListGui gui;


    protected AbstractListLogic(GuiContext guiContext, SimpleListGui newGui,
                                String preferenceId) throws RequestException {
        this.gui = newGui;
        gui.init(guiContext, PreferenceFactory.getPreference(preferenceId));
        gui.load();
    }


    public SimpleListGui getGui() {
        return gui;
    }
}
