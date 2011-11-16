package net.codjo.mad.gui.menu;
import net.codjo.i18n.common.Language;
import net.codjo.i18n.common.TranslationManager;
import net.codjo.mad.gui.MadGuiContext;
import net.codjo.mad.gui.framework.DefaultGuiContext;
import net.codjo.mad.gui.i18n.InternationalizationUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.ListResourceBundle;
import javax.swing.JMenuBar;
import junit.framework.TestCase;
/**
 *
 */
public class MenuBarBuilderTest extends TestCase {
    private DefaultGuiContext context;


    @Override
    protected void setUp() throws Exception {
        context = new MadGuiContext();
        TranslationManager translationManager = InternationalizationUtil.retrieveTranslationManager(context);
        translationManager.addBundle(new MyFrenchResources(), Language.FR);
    }


    public void testBuildMenuBar() throws Exception {
        List<MenuBuilder> list = new ArrayList<MenuBuilder>();
        MenuBuilder file = new MenuBuilder();
        file.setName("come.agf.mad.gui.menu.File");
        file.setSubMenus(new ArrayList<MenuBuilder>());
        list.add(file);

        MenuBarBuilder menubarbuilder = new MenuBarBuilder();
        menubarbuilder.setMenus(list);

        JMenuBar jmenubarRet = menubarbuilder.buildMenuBar(context);
        assertEquals(1, jmenubarRet.getMenuCount());
        assertEquals("Fichier", jmenubarRet.getMenu(0).getText());
    }


    private static class MyFrenchResources extends ListResourceBundle {
        private static final Object[][] CONTENTS = new Object[][]{
              {"come.agf.mad.gui.menu.File", "Fichier"},
        };


        @Override
        public Object[][] getContents() {
            return CONTENTS;
        }
    }
}
