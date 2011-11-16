package net.codjo.mad.gui.base;
import net.codjo.gui.toolkit.windowMenu.WindowMenu;
import net.codjo.mad.gui.framework.LocalGuiContext;
import net.codjo.mad.gui.menu.MenuFactory;
import net.codjo.mad.gui.util.ApplicationData;
import net.codjo.mad.gui.i18n.InternationalizationUtil;
import net.codjo.i18n.gui.TranslationNotifier;
import java.awt.BorderLayout;
import java.net.URL;
import java.util.List;
import javax.swing.JMenu;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.JMenuItem;
import javax.swing.AbstractAction;

class MainWindow extends ApplicationWindow {
    private URL menuConfigUrl;
    private URL toolbarConfigUrl;


    MainWindow(ApplicationData data) {
        super(data.getName(), data);
    }


    public void finishDisplay(LocalGuiContext context, List<ComponentBuilder> componentBuilders)
          throws MenuFactory.BuildException {
        TranslationNotifier notifier = InternationalizationUtil.retrieveTranslationNotifier(context);
        context.putProperty("Menu Edition", buildEditionMenu(notifier));
        context.putProperty("Menu Fenetre", buildWindowMenu(notifier));

        if (menuConfigUrl != null) {
            setJMenuBar(MenuFactory.buildMenuBar(menuConfigUrl, context));
            getJMenuBar().setVisible(true);
        }

        if (toolbarConfigUrl != null) {
            JToolBar toolBar = MenuFactory.buildToolBar(toolbarConfigUrl, context);
            getContentPane().add(toolBar, BorderLayout.NORTH);
            getGuiContext().getDesktopPane();
        }

        for (ComponentBuilder componentBuilder : componentBuilders) {
            getStatusBar().add(componentBuilder.build());
        }
    }


    private WindowMenu buildWindowMenu(TranslationNotifier notifier) {
        WindowMenu menu = new WindowMenu(getGuiContext().getDesktopPane());
        notifier.addInternationalizableComponent(menu, "net.codjo.mad.gui.menu.Window", null);
        return menu;
    }


    private JMenu buildEditionMenu(TranslationNotifier notifier) {
        JMenu editionMenu = new JMenu();
        notifier.addInternationalizableComponent(editionMenu, "net.codjo.mad.gui.menu.Edition", null);

        javax.swing.text.DefaultEditorKit.CutAction cutAction =
              new javax.swing.text.DefaultEditorKit.CutAction();
        cutAction.putValue(javax.swing.AbstractAction.NAME, "Couper");
        cutAction.putValue(javax.swing.AbstractAction.SMALL_ICON, UIManager.getIcon("cut"));
        editionMenu.add(buildMenuItem(cutAction, "net.codjo.mad.gui.CutAction", notifier));

        javax.swing.text.DefaultEditorKit.CopyAction copyAction =
              new javax.swing.text.DefaultEditorKit.CopyAction();
        copyAction.putValue(javax.swing.AbstractAction.NAME, "Copier");
        copyAction.putValue(javax.swing.AbstractAction.SMALL_ICON, UIManager.getIcon("copy"));
        editionMenu.add(buildMenuItem(copyAction, "net.codjo.mad.gui.CopyAction", notifier));

        javax.swing.text.DefaultEditorKit.PasteAction pasteAction =
              new javax.swing.text.DefaultEditorKit.PasteAction();
        pasteAction.putValue(javax.swing.AbstractAction.NAME, "Coller");
        pasteAction.putValue(javax.swing.AbstractAction.SMALL_ICON, UIManager.getIcon("paste"));
        editionMenu.add(buildMenuItem(pasteAction, "net.codjo.mad.gui.PasteAction", notifier));

        return editionMenu;
    }

    private JMenuItem buildMenuItem(AbstractAction action, String key, TranslationNotifier notifier) {
        JMenuItem item = new JMenuItem(action);
        notifier.addInternationalizableComponent(item, key, key+".tooltip");
        return item;
    }


    public void setMenuConfigUrl(URL menuConfigUrl) {
        this.menuConfigUrl = menuConfigUrl;
    }


    public void setToolbarConfigUrl(URL toolbarConfigUrl) {
        this.toolbarConfigUrl = toolbarConfigUrl;
    }
}
