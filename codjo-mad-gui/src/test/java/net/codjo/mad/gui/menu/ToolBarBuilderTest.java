package net.codjo.mad.gui.menu;
import net.codjo.mad.gui.framework.DefaultGuiContext;
import net.codjo.mad.gui.framework.MutableGuiContext;
import net.codjo.i18n.common.TranslationManager;
import net.codjo.i18n.gui.TranslationNotifier;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import junit.framework.TestCase;

public class ToolBarBuilderTest extends TestCase {
    private DefaultGuiContext ctxt;


    @Override
    protected void setUp() throws Exception {
        ctxt = new DefaultGuiContext();
        ctxt.putProperty(TranslationManager.TRANSLATION_MANAGER_PROPERTY, null);
        ctxt.putProperty(TranslationNotifier.TRANSLATION_NOTIFIER_PROPERTY, null);
    }


    public void testBuildToolBar() throws Exception {
        List<MenuBuilder> list = new ArrayList<MenuBuilder>();
        MenuBuilder file = new MenuBuilder();
        file.setAction("MyAction");
        list.add(file);

        ToolBarBuilder toolbarBuilder = new ToolBarBuilder();
        toolbarBuilder.setComponents(list);

        ctxt.putProperty(file.getAction(), new MonAction());

        // Premier cas avec valeur par défaut
        JToolBar jtoolbar = toolbarBuilder.buildToolBar(ctxt);
        assertTrue("BorderPainted est desactivé", jtoolbar.isBorderPainted());
        assertTrue("Floatable est desactivé", jtoolbar.isFloatable());

        assertEquals(1, jtoolbar.getComponentCount());
        assertEquals(JButton.class, jtoolbar.getComponentAtIndex(0).getClass());
        assertEquals("MyAction", ((JButton)jtoolbar.getComponentAtIndex(0)).getText());

        // Premier cas avec valeur changé
        toolbarBuilder.setFloatable(Boolean.FALSE);
        toolbarBuilder.setBorderPainted(Boolean.FALSE);
        toolbarBuilder.setIconOnly(Boolean.TRUE);
        JToolBar toolbarBis = toolbarBuilder.buildToolBar(ctxt);
        assertFalse("BorderPainted est desactivé", toolbarBis.isBorderPainted());
        assertFalse("Floatable est desactivé", toolbarBis.isFloatable());

        assertEquals(1, toolbarBis.getComponentCount());
        assertEquals("", ((JButton)toolbarBis.getComponentAtIndex(0)).getText());
    }


    /*
    * Test that action is not available when action is disabled
    * and builder setting isHiddenWhenNotAllowed is set to true
    */
    public void test_unavailableAction() throws Exception {
        buildToolbarAndTest(true, 0);
    }


    /*
    * Test that action is available when action is disabled
    * and builder setting isHiddenWhenNotAllowed is set to false
    */
    public void test_availableAction() throws Exception {
        buildToolbarAndTest(false, 1);
    }


    /*
    *
    *
    */
    public void test_hideSeparatorAtEnd() throws Exception {
        MenuBuilder builder1 = new MenuBuilder();
        builder1.setAction("MyAction");
        MenuBuilder builder2 = new MenuBuilder();
        builder2.setSeparator(true);

       buildToolbarAndTest(builder1, builder2);
    }


    public void test_hideSeparatorAtBegin() throws Exception {
        MenuBuilder builder1 = new MenuBuilder();
        builder1.setSeparator(true);
        MenuBuilder builder2 = new MenuBuilder();
        builder2.setAction("MyAction");

        buildToolbarAndTest(builder1, builder2);
    }


    public void test_hideMultipleSeparators() throws Exception {
        MenuBuilder builder1 = new MenuBuilder();
        builder1.setSeparator(true);
        MenuBuilder builder2 = new MenuBuilder();
        builder2.setSeparator(true);
        MenuBuilder builder3 = new MenuBuilder();
        builder3.setAction("MyAction");

        buildToolbarAndTest(builder1, builder2, builder3);
    }

    public void buildToolbarAndTest(MenuBuilder ... builders) throws Exception {
        List<MenuBuilder> list = new ArrayList<MenuBuilder>();

        for(MenuBuilder builder : builders) {
            list.add(builder);

            if(builder.getAction() != null) {
                ctxt.putProperty(builder.getAction(), new MonAction());
            }
        }

        ToolBarBuilder toolbarBuilder = new ToolBarBuilder();
        toolbarBuilder.setComponents(list);
        JToolBar jtoolbar = toolbarBuilder.buildToolBar(ctxt);

        assertEquals(1, jtoolbar.getComponents().length);
        assertEquals(JButton.class, jtoolbar.getComponent(0).getClass());
    }


    private void buildToolbarAndTest(boolean hidden, int expectedNumberOfComponents)
          throws MenuFactory.BuildException {
        MenuBuilder builder = new MenuBuilder();
        builder.setAction("MyAction");
        builder.setHideWhenNotAllowed(hidden);
        MonAction myAction = new MonAction();
        myAction.setEnabled(false);
        ToolBarBuilder toolbarBuilder = new ToolBarBuilder();
        toolbarBuilder.addMenuBar(builder);

        ctxt.putProperty(builder.getAction(), myAction);

        JToolBar jtoolbar = toolbarBuilder.buildToolBar(ctxt);

        assertEquals(expectedNumberOfComponents, jtoolbar.getComponents().length);
    }


    public void testToolBarButtonName() throws Exception {
        List<MenuBuilder> list = new ArrayList<MenuBuilder>();
        MenuBuilder menuBuilder = new MenuBuilder() {
            @Override
            public JComponent build(MutableGuiContext ctxt) throws MenuFactory.BuildException {
                ImageIcon imageIcon = new ImageIcon();
                JMenuItem item = new JMenuItem("Item", imageIcon);
                MonAction action = new MonAction();
                action.putValue(Action.NAME, "MonNomAMoi");
                item.setAction(action);
                item.setIcon(imageIcon);
                return item;
            }
        };
        list.add(menuBuilder);

        ToolBarBuilder toolbarBuilder = new ToolBarBuilder();
        toolbarBuilder.setComponents(list);

        JToolBar jtoolbar = toolbarBuilder.buildToolBar(ctxt);
        JButton jButton = ((JButton)jtoolbar.getComponentAtIndex(0));
        assertEquals("MonNomAMoi", jButton.getName());
    }


    public static class MonAction extends AbstractAction {
        public MonAction() {
            super("MyAction", UIManager.getIcon("FileChooser.newFolderIcon"));
        }


        public void actionPerformed(ActionEvent evt) {
        }
    }
}
