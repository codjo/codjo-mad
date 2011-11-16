package net.codjo.mad.gui.request;
import net.codjo.i18n.common.Language;
import net.codjo.i18n.common.TranslationManager;
import net.codjo.mad.client.request.Row;
import net.codjo.mad.gui.MadGuiContext;
import net.codjo.mad.gui.base.AboutWindowAction;
import net.codjo.mad.gui.base.GuiActionMock;
import net.codjo.mad.gui.framework.AbstractAction;
import net.codjo.mad.gui.framework.AbstractGuiAction;
import net.codjo.mad.gui.framework.GuiContext;
import net.codjo.mad.gui.i18n.InternationalizationUtil;
import static net.codjo.mad.gui.request.RequestToolBar.ACTION_ADD;
import net.codjo.mad.gui.request.action.DeleteAction;
import net.codjo.mad.gui.request.action.EditAction;
import net.codjo.mad.gui.request.factory.RequetorFactory;
import net.codjo.mad.gui.request.requetor.FindAction;
import net.codjo.security.common.api.UserMock;
import net.codjo.test.common.LogString;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.ListResourceBundle;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
import org.uispec4j.Panel;

public class RequestToolBarTest {
    private RequestToolBar toolBar = new RequestToolBar();
    private RequestTable table = new RequestTable();
    private MadGuiContext context;


    @Test
    public void test_getName() {
        assertEquals(".ToolBar", toolBar.getName());

        table.setName("MY_TABLE");
        toolBar.init(context, table);
        assertEquals("MY_TABLE.ToolBar", toolBar.getName());
    }


    @Test
    public void test_guiContext() {
        assertNull(table.getDataSource().getGuiContext());
        toolBar.init(context, table);
        assertSame(context, table.getDataSource().getGuiContext());
    }


    @Test
    public void test_guiContext_notReplaced() {
        table.getDataSource().setGuiContext(new MadGuiContext());
        toolBar.init(context, table);
        assertNotSame(context, table.getDataSource().getGuiContext());
    }


    @Test
    public void test_defaultButtons() throws Exception {
        table.setName("TABLE");
        toolBar.init(context, table);
        assertFalse(hasComponent("TABLE.ExportAllPagesAction"));
    }


    @Test
    public void test_excelButton() throws Exception {
        table.setName("TABLE");
        toolBar.setHasExcelButton(true);
        toolBar.init(context, table);
        assertTrue(hasComponent("TABLE.ExportAllPagesAction"));
    }


    @Test
    public void test_replace() throws Exception {
        AboutWindowAction aboutWindowAction = new AboutWindowAction(context);
        DeleteAction deleteAction = new DeleteAction(context, table);
        int editComponentIndex;
        int editMenuItemIndex;
        table.setName("TABLE");
        toolBar.init(context, table);

        toolBar.getMenuItemInPopUpMenu(toolBar.getAction(RequestToolBar.ACTION_EDIT)).setName("TABLE.TEST1");
        toolBar.getMenuItemInPopUpMenu(toolBar.getAction(RequestToolBar.ACTION_DELETE)).setName("TABLE.TEST2");
        editComponentIndex = getComponentIndex("TABLE." + RequestToolBar.ACTION_EDIT);
        editMenuItemIndex = getMenuItemIndex("TABLE.TEST1");

        assertFalse(hasComponent("TABLE.repB.net.codjo.mad.gui.base.AboutWindowAction"));
        assertTrue(hasComponent("TABLE." + RequestToolBar.ACTION_EDIT));
        assertTrue(hasComponent("TABLE." + RequestToolBar.ACTION_DELETE));
        assertFalse(hasMenuItem("TABLE.repM.net.codjo.mad.gui.base.AboutWindowAction"));
        assertTrue(hasMenuItem("TABLE.TEST1"));
        assertTrue(hasMenuItem("TABLE.TEST2"));
        assertNotNull(toolBar.getAction(RequestToolBar.ACTION_EDIT));
        assertNotNull(toolBar.getAction(RequestToolBar.ACTION_DELETE));
        assertNull(toolBar.getAction(AboutWindowAction.class.getName()));
        DeleteAction oldDeleteAction = (DeleteAction)toolBar.getAction(RequestToolBar.ACTION_DELETE);

        toolBar.replace(RequestToolBar.ACTION_EDIT, aboutWindowAction);
        toolBar.replace(RequestToolBar.ACTION_DELETE, deleteAction);

        assertTrue(hasComponent("TABLE.repB.net.codjo.mad.gui.base.AboutWindowAction"));
        assertFalse(hasComponent("TABLE." + RequestToolBar.ACTION_EDIT));
        assertTrue(hasComponent("TABLE." + RequestToolBar.ACTION_DELETE));
        assertTrue(hasMenuItem("TABLE.repM.net.codjo.mad.gui.base.AboutWindowAction"));
        assertFalse(hasMenuItem("TABLE.TEST1"));
        assertTrue(hasMenuItem("TABLE.TEST2"));
        assertNotNull(toolBar.getAction(RequestToolBar.ACTION_EDIT));
        assertTrue(toolBar.getAction(RequestToolBar.ACTION_EDIT).equals(aboutWindowAction));
        assertNotNull(toolBar.getAction(RequestToolBar.ACTION_DELETE));
        assertFalse(toolBar.getAction(RequestToolBar.ACTION_DELETE).equals(deleteAction));
        assertTrue(toolBar.getAction(RequestToolBar.ACTION_DELETE).equals(oldDeleteAction));
        assertNotNull(toolBar.getAction(AboutWindowAction.class.getName()));
        assertSame(aboutWindowAction, toolBar.getAction(RequestToolBar.ACTION_EDIT));
        assertEquals(getComponentIndex("TABLE.repB.net.codjo.mad.gui.base.AboutWindowAction"), editComponentIndex);
        assertEquals(getMenuItemIndex("TABLE.repM.net.codjo.mad.gui.base.AboutWindowAction"), editMenuItemIndex);

        assertEquals(getComponentIndex("TABLE." + RequestToolBar.ACTION_EDIT), -1);
        assertEquals(getMenuItemIndex("TABLE.TEST1"), -1);
    }


    @Test
    public void test_replace_nominal() throws Exception {
        table.setName("TABLE");
        toolBar.init(context, table);
        toolBar.getMenuItemInPopUpMenu(toolBar.getAction(RequestToolBar.ACTION_EDIT)).setName("TABLE.TEST1");

        assertTrue(hasComponent("TABLE." + RequestToolBar.ACTION_EDIT));
        assertTrue(hasMenuItem("TABLE.TEST1"));
        assertFalse(hasComponent("TABLE.repB.net.codjo.mad.gui.base.AboutWindowAction"));
        assertFalse(hasMenuItem("TABLE.repM.net.codjo.mad.gui.base.AboutWindowAction"));

        AboutWindowAction newAction = new AboutWindowAction(context);
        toolBar.replace(RequestToolBar.ACTION_EDIT, newAction);

        assertNull(toolBar.getAction(EditAction.class.getName()));
        assertSame(newAction, toolBar.getAction(RequestToolBar.ACTION_EDIT));
        assertFalse(hasComponent("TABLE." + RequestToolBar.ACTION_EDIT));
        assertFalse(hasMenuItem("TABLE.TEST1"));
        assertTrue(hasComponent("TABLE.repB.net.codjo.mad.gui.base.AboutWindowAction"));
        assertTrue(hasMenuItem("TABLE.repM.net.codjo.mad.gui.base.AboutWindowAction"));
    }


    @Test
    public void test_replace_unknownAction() throws Exception {
        table.setName("TABLE");
        toolBar.init(context, table);

        try {
            toolBar.replace("UnknownAction", new DeleteAction(context, table));
            fail();
        }
        catch (IllegalArgumentException e) {
            assertEquals("L'action UnknownAction n'existe pas.", e.getMessage());
        }
    }


    @Test
    public void test_replace_sameAction() throws Exception {
        table.setName("TABLE");
        toolBar.init(context, table);
        Action oldAction = toolBar.getAction(RequestToolBar.ACTION_DELETE);
        assertTrue(hasComponent("TABLE." + RequestToolBar.ACTION_DELETE));

        toolBar.replace(RequestToolBar.ACTION_DELETE, new DeleteAction(context, table));

        assertTrue(hasComponent("TABLE." + RequestToolBar.ACTION_DELETE));
        assertSame(oldAction, toolBar.getAction(RequestToolBar.ACTION_DELETE));
    }


    @Test
    public void test_setSqlRequetorXXXClause() throws Exception {
        table.setName("TABLE");
        toolBar.init(context, table);

        LogString logString = new LogString();
        toolBar.replace(RequestToolBar.ACTION_FIND, new MyFindAction(logString));
        toolBar.setSqlRequetorMandatoryClause("A mandatory clause");
        toolBar.setSqlRequetorOrderClause("An order clause");

        logString.assertContent("setSqlRequetorMandatoryClause(A mandatory clause), "
                                + "setSqlRequetorOrderClause(An order clause)");
    }


    @Test
    public void test_add() throws Exception {
        GuiActionMock actionMock1 = new GuiActionMock(context);
        GuiActionMockBis actionMock2 = new GuiActionMockBis();
        DeleteAction deleteAction = new DeleteAction(context, table);
        int editComponentIndex;
        int editMenuItemIndex;

        table.setName("TABLE");
        toolBar.init(context, table);
        toolBar.getMenuItemInPopUpMenu(toolBar.getAction(RequestToolBar.ACTION_EDIT)).setName("TABLE.TEST");

        editComponentIndex = getComponentIndex("TABLE." + RequestToolBar.ACTION_EDIT);
        editMenuItemIndex = getMenuItemIndex("TABLE.TEST");

        toolBar.add(actionMock1, GuiActionMock.class.getName(), Position.left(RequestToolBar.ACTION_EDIT), true);
        toolBar.add(actionMock2, GuiActionMockBis.class.getName(), Position.left(RequestToolBar.ACTION_EDIT), false);
        try {
            toolBar.add(deleteAction, DeleteAction.class.getName(), Position.left(RequestToolBar.ACTION_EDIT), false);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(("L'action "+DeleteAction.class.getName()+" existe déjà.").equals(e.getMessage()));
        }

        assertTrue(hasComponent("TABLE."+GuiActionMock.class.getName()));
        assertTrue(hasMenuItem("TABLE.popup."+GuiActionMock.class.getName()));
        assertNotNull(toolBar.getAction(GuiActionMock.class.getName()));

        assertTrue(hasComponent("TABLE."+GuiActionMockBis.class.getName()));
        assertFalse(hasMenuItem("TABLE.popup."+GuiActionMockBis.class.getName()));
        assertNotNull(toolBar.getAction(GuiActionMockBis.class.getName()));

        assertTrue(hasComponent("TABLE.DeleteAction"));
        assertFalse(hasMenuItem("TABLE.popup."+DeleteAction.class.getName()));

        assertEquals(getComponentIndex("TABLE."+GuiActionMock.class.getName()), editComponentIndex);
        assertEquals(getMenuItemIndex("TABLE.popup."+GuiActionMock.class.getName()), editMenuItemIndex);
        assertEquals(getComponentIndex("TABLE."+GuiActionMockBis.class.getName()), editComponentIndex + 1);
        assertEquals(getMenuItemIndex("TABLE.popup."+GuiActionMockBis.class.getName()), -1);
        assertEquals(getComponentIndex("TABLE." + RequestToolBar.ACTION_EDIT), editComponentIndex + 2);
        assertEquals(getMenuItemIndex("TABLE.TEST"), editMenuItemIndex + 1);
    }


    @Test
    public void test_reloadButton() throws Exception {
        table.setName("TABLE");
        toolBar.init(context, table);
        JButton button = toolBar.getButtonInToolBar(toolBar.getAction(RequestToolBar.ACTION_RELOAD));
        assertTrue(button.isEnabled());
        Row row = new Row();
        table.getDataSource().addRow(row);
        assertFalse(button.isEnabled());
        table.getDataSource().removeRow(row);
        assertTrue(button.isEnabled());
    }


    @Test
    public void test_add_last() throws Exception {
        table.setName("TABLE");
        toolBar.init(context, table);

        toolBar.add(new AboutWindowAction(context), "AboutWindowAction", Position.last());

        assertEquals(12, getComponentIndex("TABLE.AboutWindowAction"));
    }


    @Test
    public void test_add_left() throws Exception {
        table.setName("TABLE");
        toolBar.init(context, table);

        int oldIndex = getComponentIndex("TABLE." + RequestToolBar.ACTION_EDIT);
        toolBar.add(new AboutWindowAction(context),
                    "AboutWindowAction",
                    Position.left(RequestToolBar.ACTION_EDIT));

        assertEquals(oldIndex + 1, getComponentIndex("TABLE." + RequestToolBar.ACTION_EDIT));
        assertEquals(oldIndex, getComponentIndex("TABLE.AboutWindowAction"));
    }


    @Test
    public void test_add_right() throws Exception {
        table.setName("TABLE");
        toolBar.init(context, table);

        int oldIndex = getComponentIndex("TABLE." + RequestToolBar.ACTION_EDIT);
        toolBar.add(new AboutWindowAction(context),
                    "AboutWindowAction",
                    Position.right(RequestToolBar.ACTION_EDIT));

        assertEquals(oldIndex, getComponentIndex("TABLE." + RequestToolBar.ACTION_EDIT));
        assertEquals(oldIndex + 1, getComponentIndex("TABLE.AboutWindowAction"));
    }


    @Test
    public void test_add_existingAction() throws Exception {
        table.setName("TABLE");
        toolBar.init(context, table);

        try {
            toolBar.add(new FindAction(context, table),
                        RequestToolBar.ACTION_FIND,
                        Position.left(RequestToolBar.ACTION_EDIT));
            fail();
        }
        catch (IllegalArgumentException e) {
            assertEquals("L'action " + RequestToolBar.ACTION_FIND + " existe déjà.", e.getMessage());
        }
    }


    @Test
    public void test_addComponent_left() throws Exception {
        table.setName("TABLE");
        toolBar.init(context, table);
        JLabel label = new JLabel();
        label.setName("MyLabel");
        int index = getComponentIndex("TABLE." + RequestToolBar.ACTION_EDIT);

        toolBar.addComponent(label, Position.left(RequestToolBar.ACTION_EDIT));

        assertEquals(index, getComponentIndex("MyLabel"));
    }


    @Test
    public void test_addComponent_right() throws Exception {
        table.setName("TABLE");
        toolBar.init(context, table);
        JLabel label = new JLabel();
        label.setName("MyLabel");
        int index = getComponentIndex("TABLE." + RequestToolBar.ACTION_EDIT);

        toolBar.addComponent(label, Position.right(RequestToolBar.ACTION_EDIT));

        assertEquals(index + 1, getComponentIndex("MyLabel"));
    }


    @Test
    public void test_addComponent_unknownActionName() throws Exception {
        table.setName("TABLE");
        toolBar.init(context, table);

        try {
            toolBar.addComponent(new JLabel(), Position.left("UnknownAction"));
            fail();
        }
        catch (IllegalArgumentException e) {
            assertEquals("L'action UnknownAction n'existe pas.", e.getMessage());
        }
    }


    @Test
    public void test_bugPopupMenu() throws Exception {
        table.setName("TABLE");
        toolBar.init(context, table);

        AbstractAction dummyAction = new AbstractAction(context, "dummyAction", "dummyDescription") {
            @Override
            protected JInternalFrame buildFrame(GuiContext ctxt) throws Exception {
                return null;
            }
        };
        toolBar.add(dummyAction, "dummyAction", Position.left(RequestToolBar.ACTION_DELETE), true);

        JMenuItem item = toolBar.getMenuItemInPopUpMenu(dummyAction);
        assertNotNull(item);
    }


    @Test
    public void test_addComponent_relative_to_another_not_in_popup() throws Exception {
        table.setName("TABLE");
        toolBar.init(context, table);

        try {
            toolBar.add(new GuiActionMock(context), "Mocked action", Position.left(ACTION_ADD), true);
            fail();
        }
        catch (IllegalArgumentException e) {

        }
        try {
            toolBar.add(new GuiActionMock(context), "Mocked action", Position.right(ACTION_ADD), true);
            fail();
        }
        catch (IllegalArgumentException e) {

        }
    }


    @Test
    public void test_addComponent_not_in_popup() throws Exception {
        table.setName("TABLE");
        toolBar.init(context, table);

        GuiActionMock mockAction = new GuiActionMock(context);
        toolBar.add(mockAction, "Mocked action", Position.left(ACTION_ADD), false);
        assertNull(toolBar.getMenuItemInPopUpMenu(mockAction));
        assertNotNull(toolBar.getButtonInToolBar(mockAction));
    }


    @Test
    public void test_addComponent_relative_to_another_in_popup() throws Exception {
        table.setName("TABLE");
        toolBar.init(context, table);

        GuiActionMock mockAction = new GuiActionMock(context);
        toolBar.add(mockAction, "Mocked action", Position.last(), true);
        assertNotNull(toolBar.getMenuItemInPopUpMenu(mockAction));

        GuiActionMock mockAction2 = new GuiActionMock(context);
        toolBar.add(mockAction2, "Mocked action2", Position.right(RequestToolBar.ACTION_EDIT), true);
        assertNotNull(toolBar.getMenuItemInPopUpMenu(mockAction2));

        GuiActionMock mockAction3 = new GuiActionMock(context);
        toolBar.add(mockAction3, "Mocked action3", Position.left(RequestToolBar.ACTION_EDIT), true);
        assertNotNull(toolBar.getMenuItemInPopUpMenu(mockAction3));
    }


    @Test
    public void test_updateTranslation() throws Exception {
        Preference preference = new Preference();
        preference.setRequetor(new RequetorFactory());
        table.setPreference(preference);
        table.setName("TABLE");
        table.setEditable(false);
        toolBar.setHasUndoRedoButtons(true);
        toolBar.setHasValidationButton(true);
        toolBar.setHasExcelButton(true);
        toolBar.init(context, table);
        Panel uiToolBar = new Panel(toolBar);
        JPopupMenu popupMenu = toolBar.getPopupMenu();

        GuiActionMock actionMock = new GuiActionMock(context);
        toolBar.add(actionMock, "mock", Position.last(), true);

        assertButtonI18n(uiToolBar, RequestToolBar.ACTION_ADD, "Ajouter un enregistrement");
        assertButtonI18n(uiToolBar, RequestToolBar.ACTION_DELETE, "Supprimer les enregistrements sélectionnés");
        assertButtonI18n(uiToolBar, RequestToolBar.ACTION_EDIT, "Editer l'enregistrement sélectionné");
        assertButtonI18n(uiToolBar, RequestToolBar.ACTION_EXPORT_ALL_PAGES, "Exporter toutes les pages");
        assertButtonI18n(uiToolBar, RequestToolBar.ACTION_NEXT_PAGE, "Afficher la page suivante");
        assertButtonI18n(uiToolBar, RequestToolBar.ACTION_PREVIOUS_PAGE, "Afficher la page précédente");
        assertButtonI18n(uiToolBar, RequestToolBar.ACTION_RELOAD, "Rafraîchir la liste");
        assertButtonI18n(uiToolBar, RequestToolBar.ACTION_CLEAR, "Annuler les critères de recherche");
        assertButtonI18n(uiToolBar, RequestToolBar.ACTION_FIND, "Définir les critères de recherche");
        assertButtonI18n(uiToolBar, RequestToolBar.ACTION_SAVE, "Sauvegarder");
        assertButtonI18n(uiToolBar, RequestToolBar.ACTION_UNDO, "Annuler la dernière modification");
        assertButtonI18n(uiToolBar, RequestToolBar.ACTION_REDO, "Rétablir la dernière modification");
        assertButtonI18n(uiToolBar, "mock", "MyGuiActionMock tooltip");
        assertEquals("Editer", ((JMenuItem)popupMenu.getComponent(0)).getText());
        assertEquals("Supprimer", ((JMenuItem)popupMenu.getComponent(1)).getText());
        assertEquals("MyGuiActionMock", ((JMenuItem)popupMenu.getComponent(2)).getText());

        InternationalizationUtil.retrieveTranslationNotifier(context).setLanguage(Language.EN);

        assertButtonI18n(uiToolBar, RequestToolBar.ACTION_ADD, "Add an entry");
        assertButtonI18n(uiToolBar, RequestToolBar.ACTION_DELETE, "Delete selected entries");
        assertButtonI18n(uiToolBar, RequestToolBar.ACTION_EDIT, "Edit selected entry");
        assertButtonI18n(uiToolBar, RequestToolBar.ACTION_EXPORT_ALL_PAGES, "Open as Excel worksheet");
        assertButtonI18n(uiToolBar, RequestToolBar.ACTION_NEXT_PAGE, "Show next page");
        assertButtonI18n(uiToolBar, RequestToolBar.ACTION_PREVIOUS_PAGE, "Show previous page");
        assertButtonI18n(uiToolBar, RequestToolBar.ACTION_RELOAD, "Reload table");
        assertButtonI18n(uiToolBar, RequestToolBar.ACTION_CLEAR, "Clear find criteria");
        assertButtonI18n(uiToolBar, RequestToolBar.ACTION_FIND, "Set find criteria");
        assertButtonI18n(uiToolBar, RequestToolBar.ACTION_SAVE, "Save");
        assertButtonI18n(uiToolBar, RequestToolBar.ACTION_UNDO, "Undo last edit");
        assertButtonI18n(uiToolBar, RequestToolBar.ACTION_REDO, "Redo last edit");
        assertButtonI18n(uiToolBar, "mock", "MyGuiActionMock tooltip EN");
        assertEquals("Edit", ((JMenuItem)popupMenu.getComponent(0)).getText());
        assertEquals("Delete", ((JMenuItem)popupMenu.getComponent(1)).getText());
        assertEquals("MyGuiActionMock EN", ((JMenuItem)popupMenu.getComponent(2)).getText());

        InternationalizationUtil.retrieveTranslationNotifier(context).setLanguage(Language.FR);

        assertButtonI18n(uiToolBar, RequestToolBar.ACTION_ADD, "Ajouter un enregistrement");
        assertButtonI18n(uiToolBar, RequestToolBar.ACTION_DELETE, "Supprimer les enregistrements sélectionnés");
        assertButtonI18n(uiToolBar, RequestToolBar.ACTION_EDIT, "Editer l'enregistrement sélectionné");
        assertButtonI18n(uiToolBar, RequestToolBar.ACTION_EXPORT_ALL_PAGES, "Exporter toutes les pages");
        assertButtonI18n(uiToolBar, RequestToolBar.ACTION_NEXT_PAGE, "Afficher la page suivante");
        assertButtonI18n(uiToolBar, RequestToolBar.ACTION_PREVIOUS_PAGE, "Afficher la page précédente");
        assertButtonI18n(uiToolBar, RequestToolBar.ACTION_RELOAD, "Rafraîchir la liste");
        assertButtonI18n(uiToolBar, RequestToolBar.ACTION_CLEAR, "Annuler les critères de recherche");
        assertButtonI18n(uiToolBar, RequestToolBar.ACTION_FIND, "Définir les critères de recherche");
        assertButtonI18n(uiToolBar, RequestToolBar.ACTION_SAVE, "Sauvegarder");
        assertButtonI18n(uiToolBar, RequestToolBar.ACTION_UNDO, "Annuler la dernière modification");
        assertButtonI18n(uiToolBar, RequestToolBar.ACTION_REDO, "Rétablir la dernière modification");
        assertButtonI18n(uiToolBar, "mock", "MyGuiActionMock tooltip");
        assertEquals("Editer", ((JMenuItem)popupMenu.getComponent(0)).getText());
        assertEquals("Supprimer", ((JMenuItem)popupMenu.getComponent(1)).getText());
        assertEquals("MyGuiActionMock", ((JMenuItem)popupMenu.getComponent(2)).getText());
    }


    private void assertButtonI18n(Panel uiToolBar, String name, String expectedTooltip) {
        JButton button = (JButton)uiToolBar.getButton("TABLE."+name).getAwtComponent();
        assertNull(button.getText(), button.getText());
        assertEquals(expectedTooltip, button.getToolTipText());
    }


    private boolean hasComponent(String name) {
        for (int i = 0; i < toolBar.getComponentCount(); i++) {
            Component comp = toolBar.getComponent(i);
            if (name.equals(comp.getName())) {
                return true;
            }
        }
        return false;
    }


    private boolean hasMenuItem(String name) {
        for (int i = 0; i < toolBar.getPopupMenu().getComponentCount(); i++) {
            Component comp = toolBar.getPopupMenu().getComponent(i);
            if (name.equals(comp.getName())) {
                return true;
            }
        }
        return false;
    }


    private int getComponentIndex(String name) {
        for (int i = 0; i < toolBar.getComponentCount(); i++) {
            Component comp = toolBar.getComponent(i);
            if (name.equals(comp.getName())) {
                return i;
            }
        }
        return -1;
    }


    private int getMenuItemIndex(String name) {
        for (int i = 0; i < toolBar.getPopupMenu().getComponentCount(); i++) {
            Component comp = toolBar.getPopupMenu().getComponent(i);
            if (name.equals(comp.getName())) {
                return i;
            }
        }
        return -1;
    }


    @Before
    public void setUp() throws Exception {
        context = new MadGuiContext();
        context.setUser(new UserMock().mockIsAllowedTo(true));
        TranslationManager translationManager = InternationalizationUtil.retrieveTranslationManager(context);
        translationManager.addBundle(new MyFrenchResources(), Language.FR);
        translationManager.addBundle(new MyEnglishResources(), Language.EN);
        PreferenceFactory.loadMadIcons();
    }


    private class GuiActionMockBis extends AbstractGuiAction {

        GuiActionMockBis() {
            super(context, "GuiActionMockBis", "Premier mock");
        }


        public void actionPerformed(ActionEvent event) {
        }
    }

    private class MyFindAction extends FindAction {
        private final LogString logString;


        private MyFindAction(LogString logString) {
            super(RequestToolBarTest.this.context, RequestToolBarTest.this.table);
            this.logString = logString;
        }


        @Override
        public void setSqlRequetorMandatoryClause(String clause) {
            logString.call("setSqlRequetorMandatoryClause", clause);
        }


        @Override
        public void setSqlRequetorOrderClause(String clause) {
            logString.call("setSqlRequetorOrderClause", clause);
        }
    }

    private static class MyFrenchResources extends ListResourceBundle {
        private static final Object[][] CONTENTS = {
              {"net.codjo.mad.gui.base.GuiActionMock", "MyGuiActionMock"},
              {"net.codjo.mad.gui.base.GuiActionMock.tooltip", "MyGuiActionMock tooltip"},
              {"net.codjo.mad.gui.request.RequestToolBarTest$GuiActionMockBis.tooltip", "GuiActionMockBis tooltip"},
              {"net.codjo.mad.gui.request.RequestToolBarTest$1", "dummy"},
              {"net.codjo.mad.gui.request.RequestToolBarTest$1.tooltip", "dummy tooltip"},
        };


        @Override
        protected Object[][] getContents() {
            return CONTENTS;
        }
    }

    private static class MyEnglishResources extends ListResourceBundle {
        private static final Object[][] CONTENTS = {
              {"net.codjo.mad.gui.base.GuiActionMock", "MyGuiActionMock EN"},
              {"net.codjo.mad.gui.base.GuiActionMock.tooltip", "MyGuiActionMock tooltip EN"},
              {"net.codjo.mad.gui.request.RequestToolBarTest$GuiActionMockBis.tooltip", "GuiActionMockBis tooltip EN"},
              {"net.codjo.mad.gui.request.RequestToolBarTest$1", "dummy EN"},
              {"net.codjo.mad.gui.request.RequestToolBarTest$1.tooltip", "dummy tooltip EN"},
        };


        @Override
        protected Object[][] getContents() {
            return CONTENTS;
        }
    }
}
