package net.codjo.mad.gui.base;
import java.util.ListResourceBundle;
import javax.swing.JButton;
import net.codjo.i18n.common.Language;
import net.codjo.i18n.common.TranslationManager;
import net.codjo.i18n.gui.TranslationNotifier;
import net.codjo.mad.gui.i18n.InternationalizationUtil;
import net.codjo.mad.gui.util.InternationalizableGuiContext;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
/**
 *
 */
public class ButtonBuilderTest {

    @Test
    public void test_build() throws Exception {
        JButton button = new ButtonBuilder("name of the button", new ActionMock()).build();

        assertEquals("name of the button", button.getName());
    }


    @Test
    public void test_buildInternationalizableButton() throws Exception {
        InternationalizableGuiContext guiContext = new InternationalizableGuiContext();
        TranslationManager translationManager = InternationalizationUtil.retrieveTranslationManager(guiContext);
        translationManager.addBundle(new MyFrenchResources(), Language.FR);
        translationManager.addBundle(new MyEnglishResources(), Language.EN);
        TranslationNotifier translationNotifier = InternationalizationUtil.retrieveTranslationNotifier(guiContext);

        JButton button =
              new ButtonBuilder(guiContext, "name of the button", new ActionMock(), "test.label", "test.tooltip")
                    .build();

        assertEquals("name of the button", button.getName());
        assertEquals("mon label", button.getText());
        assertEquals("mon infobulle", button.getToolTipText());

        translationNotifier.setLanguage(Language.EN);

        assertEquals("my label", button.getText());
        assertEquals("my tooltip", button.getToolTipText());
    }


    private static class MyFrenchResources extends ListResourceBundle {
        private static final Object[][] CONTENTS = new Object[][]{
              {"test.label", "mon label"},
              {"test.tooltip", "mon infobulle"},
        };


        @Override
        public Object[][] getContents() {
            return CONTENTS;
        }
    }

    private static class MyEnglishResources extends ListResourceBundle {
        private static final Object[][] CONTENTS = new Object[][]{
              {"test.label", "my label"},
              {"test.tooltip", "my tooltip"},
        };


        @Override
        public Object[][] getContents() {
            return CONTENTS;
        }
    }
}
