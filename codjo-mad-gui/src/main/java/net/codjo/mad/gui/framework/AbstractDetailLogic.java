package net.codjo.mad.gui.framework;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.gui.request.DetailDataSource;
import net.codjo.mad.gui.request.GuiLogic;
import net.codjo.mad.gui.request.util.ButtonPanelLogic;
/**
 * Logique de base d'une fenêtre Detail.
 */
public abstract class AbstractDetailLogic implements GuiLogic<AbstractDetailGui> {
    private AbstractDetailGui gui;
    private ButtonPanelLogic buttonPanelLogic;


    protected AbstractDetailLogic(DetailDataSource dataSource, AbstractDetailGui newGui)
          throws RequestException {
        this.gui = newGui;

        gui.setGuiContext(dataSource.getGuiContext());
        gui.setDesktopPane(dataSource.getGuiContext().getDesktopPane());
        gui.declareFields(dataSource);

        if (dataSource.getLoadFactory() != null) {
            gui.switchToUpdateMode();
        }

        buttonPanelLogic = new ButtonPanelLogic(gui.getButtonPanelGui());
        buttonPanelLogic.setMainDataSource(dataSource);

        loadFatherDataSource();
        dataSource.load();
    }


    public AbstractDetailGui getGui() {
        return gui;
    }


    public ButtonPanelLogic getButtonPanelLogic() {
        return buttonPanelLogic;
    }


    public void loadFatherDataSource() throws RequestException {
    }
}
