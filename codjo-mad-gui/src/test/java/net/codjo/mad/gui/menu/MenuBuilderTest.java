package net.codjo.mad.gui.menu;
import net.codjo.i18n.common.Language;
import net.codjo.i18n.common.TranslationManager;
import net.codjo.mad.gui.MadGuiContext;
import net.codjo.mad.gui.framework.GuiContext;
import net.codjo.mad.gui.framework.MutableGuiContext;
import net.codjo.mad.gui.i18n.InternationalizationUtil;
import net.codjo.mad.gui.menu.MenuFactory.BuildException;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.ListResourceBundle;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.MenuElement;
import junit.framework.TestCase;

public class MenuBuilderTest extends TestCase {
    static final String MON_ACTION_NAME = "menuAction";
    private MenuBuilder forMenu;
    private MenuBuilder notForMenu;
    MutableGuiContext ctxt;


    public MenuBuilderTest(String str) {
        super(str);
    }


    public void test_build_action() throws Exception {
        forMenu.setAction("net.codjo.mad.gui.menu.MenuBuilderTest$MonAction");
        JComponent jmenu = forMenu.build(ctxt);
        assertTrue(jmenu instanceof JMenuItem);
        assertEquals(MON_ACTION_NAME, ((JMenuItem)jmenu).getText());
        assertTrue(((JMenuItem)jmenu).getAction() instanceof MonAction);

        notForMenu.setAction("net.codjo.mad.gui.menu.MenuBuilderTest$MonAction");
        JComponent jbutton = notForMenu.build(ctxt);
        assertTrue(jbutton instanceof JButton);
        assertEquals(MON_ACTION_NAME, ((JButton)jbutton).getText());

        assertSame("Action identique", ((JMenuItem)jmenu).getAction(),
                   ((JButton)jbutton).getAction());
    }


    public void test_build_fromGuiContext() throws Exception {
        forMenu.setActionId("a menu");
        notForMenu.setActionId("a menu");

        JMenu aMenu = new JMenu("TEST");
        ctxt.putProperty("a menu", aMenu);

        JComponent jmenu = forMenu.build(ctxt);
        assertTrue(jmenu instanceof JMenu);
        assertEquals("TEST", ((JMenuItem)jmenu).getText());

        jmenu = notForMenu.build(ctxt);
        assertTrue(jmenu instanceof JMenu);
        assertEquals("TEST", ((JMenuItem)jmenu).getText());
    }


    public void test_getGuiContextProperty() throws Exception {
        MenuBuilder builder = new MenuBuilder();
        builder.setPlugin("plugin");
        builder.setActionId("actionId");
        assertEquals("plugin#actionId", builder.getGuiContextProperty());

        builder = new MenuBuilder();
        builder.setActionId("actionId");
        builder.setPlugin("plugin");
        assertEquals("plugin#actionId", builder.getGuiContextProperty());
    }


    public void test_build() throws BuildException {
        JMenu aMenu = new JMenu("TEST");
        forMenu.setActionId("ForMenu");
        forMenu.setPlugin("net.codjo.MyPlugin");
        ctxt.putProperty(forMenu.getGuiContextProperty(), aMenu);

        JComponent menu = forMenu.build(ctxt);
        assertTrue(JMenu.class.isInstance(menu));
        assertEquals("Mon menu", ((JMenu)menu).getText());
        assertEquals("Mon tooltip menu", menu.getToolTipText());

        JButton aButton = new JButton("TEST");
        notForMenu.setActionId("NotForMenu");
        notForMenu.setPlugin("net.codjo.MyPlugin");
        ctxt.putProperty(notForMenu.getGuiContextProperty(), aButton);

        JComponent button = notForMenu.build(ctxt);
        assertTrue(JButton.class.isInstance(button));
        assertEquals("TEST", ((JButton)button).getText());
        assertEquals("Mon tooltip bouton", button.getToolTipText());
    }


    public void test_build_separator() throws Exception {
        forMenu.setSeparator(true);
        notForMenu.setSeparator(true);

        JComponent jmenu = forMenu.build(ctxt);
        assertTrue(jmenu instanceof JSeparator);
        assertEquals(JSeparator.HORIZONTAL, ((JSeparator)jmenu).getOrientation());

        jmenu = notForMenu.build(ctxt);
        assertTrue(jmenu instanceof JSeparator);
        assertEquals(JSeparator.VERTICAL, ((JSeparator)jmenu).getOrientation());
    }


    public void test_build_submenu() throws Exception {
        forMenu.setSubMenus(new ArrayList<MenuBuilder>());
        forMenu.setName("subMenuKey");
        JComponent jmenu = forMenu.build(ctxt);
        assertTrue(jmenu instanceof JMenu);
        assertEquals("Sous-menu", ((JMenu)jmenu).getText());
    }


    public void test_activation_propagation() throws Exception {
        MenuBuilder menu = new MenuBuilder();
        menu.setName("menuKey");
        List<MenuBuilder> subMenus = new ArrayList<MenuBuilder>();
        menu.setSubMenus(subMenus);

        MenuBuilder menu1 = new MenuBuilder();
        subMenus.add(menu1);
        menu1.setName("menu1Key");
        List<MenuBuilder> subMenus1 = new ArrayList<MenuBuilder>();
        menu1.setSubMenus(subMenus1);

        MenuBuilder menu11 = new MenuBuilder();
        menu11.setAction("net.codjo.mad.gui.menu.MenuBuilderTest$MonAction");
        menu11.setName("menu1-1Key");
        subMenus1.add(menu11);

        MenuBuilder menu12 = new MenuBuilder();
        menu12.setAction("net.codjo.mad.gui.menu.MenuBuilderTest$MonAction");
        menu12.setName("menu1-2Key");
        subMenus1.add(menu12);

        MenuBuilder menu2 = new MenuBuilder();
        subMenus.add(menu2);
        menu2.setName("menu2Key");
        List<MenuBuilder> subMenus2 = new ArrayList<MenuBuilder>();
        menu2.setSubMenus(subMenus2);

        MenuBuilder menu21 = new MenuBuilder();
        menu21.setAction("net.codjo.mad.gui.menu.MenuBuilderTest$MonAction");
        menu21.setName("menu2-1Key");
        subMenus2.add(menu21);

        MenuBuilder menu22 = new MenuBuilder();
        menu22.setAction("net.codjo.mad.gui.menu.MenuBuilderTest$MonActionInactive");
        menu22.setName("menu2-2Key");
        subMenus2.add(menu22);

        MenuBuilder menu3 = new MenuBuilder();
        subMenus.add(menu3);
        menu3.setName("menu3Key");
        List<MenuBuilder> subMenus3 = new ArrayList<MenuBuilder>();
        menu3.setSubMenus(subMenus3);

        MenuBuilder menu31 = new MenuBuilder();
        menu31.setAction("net.codjo.mad.gui.menu.MenuBuilderTest$MonActionInactive");
        menu31.setName("menu3-1Key");
        subMenus3.add(menu31);

        MenuBuilder menu32 = new MenuBuilder();
        menu32.setAction("net.codjo.mad.gui.menu.MenuBuilderTest$MonActionInactive");
        menu32.setName("menu3-2Key");
        subMenus3.add(menu32);

        JMenu jmenu = (JMenu)menu.build(ctxt);
        MenuElement[] subElements = jmenu.getSubElements();
        assertEquals(1, subElements.length);

        subElements = subElements[0].getSubElements();
        assertEquals(3, subElements.length);

        assertTrue(subElements[0].getComponent().isEnabled());
        assertTrue(subElements[1].getComponent().isEnabled());
        assertFalse(subElements[2].getComponent().isEnabled());
    }


    @Override
    protected void setUp() {
        forMenu = new MenuBuilder();
        notForMenu = new MenuBuilder();
        notForMenu.setForMenu(false);
        ctxt = new MadGuiContext();

        TranslationManager translationManager = InternationalizationUtil.retrieveTranslationManager(ctxt);
        translationManager.addBundle(new MyFrenchResources(), Language.FR);
    }


    @Override
    protected void tearDown() {
    }


    public static class MonAction extends AbstractAction {
        /**
         * pff.
         *
         * @param ctxt pff noinspection UNUSED_SYMBOL
         */
        public MonAction(GuiContext ctxt) {
            super(MON_ACTION_NAME);
        }


        public void actionPerformed(ActionEvent evt) {
        }
    }

    public static class MonActionInactive extends AbstractAction {
        public MonActionInactive(GuiContext ctxt) {
            super(MON_ACTION_NAME + " inactive");
            setEnabled(false);
        }


        public void actionPerformed(ActionEvent evt) {
        }
    }

    private static class MyFrenchResources extends ListResourceBundle {
        private static final Object[][] CONTENTS = new Object[][]{
              {"net.codjo.mad.gui.menu.MenuBuilderTest$MonAction", MON_ACTION_NAME},
              {"net.codjo.mad.gui.menu.MenuBuilderTest$MonAction.tooltip", "tooltip"},
              {"net.codjo.mad.gui.menu.MenuBuilderTest$MonActionInactive", MON_ACTION_NAME + " inactive"},
              {"net.codjo.mad.gui.menu.MenuBuilderTest$MonActionInactive.tooltip", " inactive tooltip"},
              {"subMenuKey", "Sous-menu"},
              {"menuKey", "Menu"},
              {"menu1Key", "Menu"},
              {"menu1-1Key", "Menu"},
              {"menu1-2Key", "Menu"},
              {"menu2Key", "Menu"},
              {"menu2-1Key", "Menu"},
              {"menu2-2Key", "Menu"},
              {"menu3Key", "Menu"},
              {"menu3-1Key", "Menu"},
              {"menu3-2Key", "Menu"},
              {"a_menu", "Un menu"},
              {"not_a_menu", "Pas un menu"},
              {"not_a_menu.tooltip", "Tooltip de pas un menu"},
              {"net.codjo.MyPlugin#ForMenu", "Mon menu"},
              {"net.codjo.MyPlugin#ForMenu.tooltip", "Mon tooltip menu"},
              {"net.codjo.MyPlugin#NotForMenu", "Mon bouton"},
              {"net.codjo.MyPlugin#NotForMenu.tooltip", "Mon tooltip bouton"},
        };


        @Override
        public Object[][] getContents() {
            return CONTENTS;
        }
    }
}
