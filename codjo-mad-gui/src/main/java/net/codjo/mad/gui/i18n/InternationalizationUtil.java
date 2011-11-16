package net.codjo.mad.gui.i18n;
import net.codjo.i18n.common.TranslationManager;
import net.codjo.i18n.gui.TranslationNotifier;
import net.codjo.mad.gui.framework.GuiContext;

public class InternationalizationUtil {
    private InternationalizationUtil() {
    }


    public static TranslationNotifier retrieveTranslationNotifier(GuiContext guiContext) {
        if (!guiContext.hasProperty(TranslationNotifier.TRANSLATION_NOTIFIER_PROPERTY)) {
            throw new RuntimeException("Impossible d'internationaliser sans TranslationNotifier.");
        }
        return (TranslationNotifier)guiContext.getProperty(TranslationNotifier.TRANSLATION_NOTIFIER_PROPERTY);
    }


    public static TranslationManager retrieveTranslationManager(GuiContext guiContext) {
        if (!guiContext.hasProperty(TranslationManager.TRANSLATION_MANAGER_PROPERTY)) {
            throw new RuntimeException("Impossible d'internationaliser sans TranslationManager.");
        }
        return (TranslationManager)guiContext.getProperty(TranslationManager.TRANSLATION_MANAGER_PROPERTY);
    }
}
