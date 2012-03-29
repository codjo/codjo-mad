package net.codjo.mad.gui.base;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import net.codjo.gui.toolkit.AboutWindow;
import net.codjo.mad.gui.framework.AbstractGuiAction;
import net.codjo.mad.gui.framework.GuiContext;

import static net.codjo.mad.gui.i18n.InternationalizationUtil.retrieveTranslationManager;
import static net.codjo.mad.gui.i18n.InternationalizationUtil.retrieveTranslationNotifier;
/**
 * Fenêtre à propos d'une application.
 */
public class AboutWindowAction extends AbstractGuiAction {
    GuiContext guiContext;


    public AboutWindowAction(GuiContext guiContext) {
        super(guiContext, "A propos...", "Historique des versions");
        this.guiContext = guiContext;
    }


    public void actionPerformed(ActionEvent event) {
        displayNewWindow();
    }


    private void displayNewWindow() {
        AboutWindow aboutWindow =
              new AboutWindow(guiContext.getMainFrame(),
                              (String)getGuiContext().getProperty("application.name"),
                              (String)getGuiContext().getProperty("application.version"),
                              AboutWindowAction.class.getResource("/versions/historique.html"),
                              retrieveTranslationManager(guiContext),
                              retrieveTranslationNotifier(guiContext));

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = aboutWindow.getSize();
        if (frameSize.height > screenSize.height) {
            frameSize.height = screenSize.height;
        }
        if (frameSize.width > screenSize.width) {
            frameSize.width = screenSize.width;
        }
        aboutWindow.setLocation((screenSize.width - frameSize.width) / 2,
                                (screenSize.height - frameSize.height) / 2);

        aboutWindow.setModal(true);
        aboutWindow.setVisible(true);
    }
}
