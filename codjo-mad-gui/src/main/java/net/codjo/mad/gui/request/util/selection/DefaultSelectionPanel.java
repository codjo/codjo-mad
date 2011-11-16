package net.codjo.mad.gui.request.util.selection;
import net.codjo.mad.gui.framework.GuiContext;
import javax.swing.Action;
/**
 * Classe obsolète.
 *
 * @author $Author: gaudefr $
 * @version $Revision: 1.11 $
 *
 * @see SelectionGui
 * @deprecated Classe renommée en SelectionGui
 */
public class DefaultSelectionPanel extends SelectionGui {
    public DefaultSelectionPanel() {
        super(null);
    }

    public void setAddAction(Action addAction) {
        setSelectAction(addAction);
    }


    public void setRemoveAction(Action removeAction) {
        setUnSelectAction(removeAction);
    }


    public void setGuiContext(GuiContext guiCtxt) {
        initFromToolBar(guiCtxt);
    }
}
