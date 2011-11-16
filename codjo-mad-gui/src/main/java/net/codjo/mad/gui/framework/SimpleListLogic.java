package net.codjo.mad.gui.framework;
import net.codjo.mad.client.request.RequestException;
import java.awt.Dimension;
/**
 * Classe logique d'une écran contenant une liste.
 */
public class SimpleListLogic extends AbstractListLogic {
    public SimpleListLogic(GuiContext guiContext, String title, String preferenceId)
            throws RequestException {
        super(guiContext, new SimpleListGui(title), preferenceId);
    }


    public SimpleListLogic(GuiContext guiContext, String title, String preferenceId,
        Dimension dimension) throws RequestException {
        super(guiContext, new SimpleListGui(title, dimension), preferenceId);
    }
}
