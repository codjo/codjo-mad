package net.codjo.mad.gui.util;
import net.codjo.i18n.common.Language;
import net.codjo.i18n.common.TranslationManager;
import net.codjo.i18n.gui.TranslationNotifier;
import net.codjo.mad.gui.framework.DefaultGuiContext;
/**
 *
 */
public class InternationalizableGuiContext extends DefaultGuiContext {
    public InternationalizableGuiContext() {
        TranslationManager translationManager = new TranslationManager();
        putProperty(TranslationManager.TRANSLATION_MANAGER_PROPERTY, translationManager);
        TranslationNotifier translationNotifier = new TranslationNotifier(Language.FR, translationManager);
        putProperty(TranslationNotifier.TRANSLATION_NOTIFIER_PROPERTY, translationNotifier);
    }
}
