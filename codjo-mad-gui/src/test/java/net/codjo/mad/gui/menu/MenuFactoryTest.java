package net.codjo.mad.gui.menu;
import net.codjo.i18n.common.Language;
import net.codjo.i18n.common.TranslationManager;
import net.codjo.mad.gui.MadGuiContext;
import net.codjo.mad.gui.framework.GuiContext;
import net.codjo.mad.gui.i18n.InternationalizationUtil;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.ListResourceBundle;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import junit.framework.TestCase;

public class MenuFactoryTest extends TestCase {
    static final String MON_ACTION_NAME = "'culé";
    static final String MON_ACTION_TOOLTIP = "c'est un tooltip";
    private MadGuiContext ctxt;


    @Override
    protected void setUp() throws Exception {
        ctxt = new MadGuiContext();
        TranslationManager translationManager = InternationalizationUtil.retrieveTranslationManager(ctxt);
        translationManager.addBundle(new MyFrenchResources(), Language.FR);
    }


    public void testEasyXmlBuildMenuBar() throws Exception {
        URL url = MenuFactoryTest.class.getResource("MenuFactory_menubarTest.xml");

        ctxt.putProperty("net.codjo.mad.gui.menu.MenuFactoryTest#MiAccion", new JMenuItem(new MiAccion(ctxt)));

        JMenuBar menuBar = MenuFactory.buildMenuBar(url, ctxt);

        // Verification menus principaux
        assertEquals(3, menuBar.getMenuCount());
        JMenu file = menuBar.getMenu(0);
        assertEquals("Fichier", file.getText());
        assertEquals("Vide", menuBar.getMenu(1).getText());

        // Verification menu fichier
        assertEquals(3, file.getMenuComponentCount());
        assertEquals(MON_ACTION_NAME, ((JMenuItem)file.getMenuComponent(0)).getText());
        assertTrue(file.getMenuComponent(1) instanceof JSeparator);
        assertEquals("Paramétrage", ((JMenuItem)file.getMenuComponent(2)).getText());

        // Verification sous-menu parametrage
        JMenu params = (JMenu)file.getMenuComponent(2);
        assertEquals(1, params.getMenuComponentCount());
        assertEquals(MON_ACTION_NAME, ((JMenuItem)params.getMenuComponent(0)).getText());

        // Menu From plugin
        JMenu fromPluginsMenu = menuBar.getMenu(2);
        assertEquals("Via Plugins", fromPluginsMenu.getText());
        assertEquals(1, fromPluginsMenu.getMenuComponentCount());
        assertEquals("MyAction", ((JMenuItem)fromPluginsMenu.getMenuComponent(0)).getText());
    }


    public void testEasyXmlBuildToolBar() throws Exception {
        JButton aButton = new JButton("", UIManager.getIcon("FileChooser.newFolderIcon"));
        ctxt.putProperty("un bouton", aButton);

        JToolBar toolBar = MenuFactory.buildToolBar(getClass().getResource("MenuFactory_toolbarTest.xml"),
                                                    ctxt);

        assertFalse("BorderPainted est desactivé", toolBar.isBorderPainted());
        assertFalse("Floatable est desactivé", toolBar.isFloatable());
        assertEquals(4, toolBar.getComponentCount());
        assertEquals(MonAction.class, ((JButton)toolBar.getComponentAtIndex(0)).getAction().getClass());
        assertEquals(JSeparator.class, toolBar.getComponentAtIndex(1).getClass());
        assertEquals(aButton, toolBar.getComponentAtIndex(2));
        assertEquals("", aButton.getText());
        assertEquals(MonAutreAction.class, ((JButton)toolBar.getComponentAtIndex(3)).getAction().getClass());
    }


    public void test_shareSameAction() throws Exception {
        JButton aButton = new JButton("");
        ctxt.putProperty("un bouton", aButton);
        ctxt.putProperty("net.codjo.mad.gui.menu.MenuFactoryTest#MiAccion", new JButton(new MiAccion(ctxt)));

        JToolBar toolBar =
              MenuFactory
                    .buildToolBar(MenuFactoryTest.class.getResource("MenuFactory_toolbarTest.xml"), ctxt);
        JMenuBar menuBar =
              MenuFactory
                    .buildMenuBar(MenuFactoryTest.class.getResource("MenuFactory_menubarTest.xml"), ctxt);

        // Recuperation de l'action 'MenuFactoryTest$MonAction'
        JMenu file = menuBar.getMenu(0);
        Action menuAction = ((JMenuItem)file.getMenuComponent(0)).getAction();
        Action toolbarAction = ((JButton)toolBar.getComponentAtIndex(0)).getAction();

        assertSame("La meme instance de l'action 'MenuFactoryTest$MonAction' est partagée"
                   + " entre le menu et la toolbar ", menuAction, toolbarAction);
    }


    public static class MonAction extends AbstractAction {
        @SuppressWarnings({"UnusedDeclaration"})
        public MonAction(GuiContext ctxt) {
            super(MON_ACTION_NAME);
        }


        public void actionPerformed(ActionEvent evt) {
        }
    }

    public static class MiAccion extends AbstractAction {
        @SuppressWarnings({"UnusedDeclaration"})
        public MiAccion(GuiContext ctxt) {
            super("MiAccion");
        }


        public void actionPerformed(ActionEvent evt) {
        }
    }

    public static class MonAutreAction extends AbstractAction {
        @SuppressWarnings({"UnusedDeclaration"})
        public MonAutreAction(GuiContext ctxt) {
            super(MON_ACTION_NAME);
        }


        public void actionPerformed(ActionEvent evt) {
        }
    }

    private static class MyFrenchResources extends ListResourceBundle {
        private static final Object[][] CONTENTS = new Object[][]{
              {"net.codjo.mad.gui.menu.File", "Fichier"},
              {"net.codjo.mad.gui.menu.Settings", "Paramétrage"},
              {"net.codjo.mad.gui.menu.Empty", "Vide"},
              {"net.codjo.mad.gui.menu.FromPlugins", "Via Plugins"},
              {"net.codjo.mad.gui.menu.MenuFactoryTest$MonAction", MON_ACTION_NAME},
              {"net.codjo.mad.gui.menu.MenuFactoryTest$MonAction.tooltip", MON_ACTION_TOOLTIP},
              {"net.codjo.mad.gui.menu.MenuFactoryTest#MiAccion", "MyAction"},
              {"net.codjo.mad.gui.menu.MenuFactoryTest#MiAccion.tooltip", "MyActionTooltip"},
              {"net.codjo.mad.gui.menu.MenuFactoryTest$MonAutreAction", MON_ACTION_NAME},
              {"net.codjo.mad.gui.menu.MenuFactoryTest$MonAutreAction.tooltip", MON_ACTION_TOOLTIP},
        };


        @Override
        public Object[][] getContents() {
            return CONTENTS;
        }
    }
}
