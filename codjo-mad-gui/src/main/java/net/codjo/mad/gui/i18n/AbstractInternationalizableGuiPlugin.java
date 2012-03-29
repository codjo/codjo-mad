package net.codjo.mad.gui.i18n;
import net.codjo.gui.toolkit.util.ErrorDialog;
import net.codjo.i18n.common.Language;
import net.codjo.i18n.common.TranslationManager;
import net.codjo.i18n.gui.TranslationNotifier;
import net.codjo.mad.gui.base.AbstractGuiPlugin;
import net.codjo.mad.gui.base.GuiConfiguration;
import net.codjo.mad.gui.framework.MutableGuiContext;

import static net.codjo.mad.gui.i18n.InternationalizationUtil.retrieveTranslationManager;
import static net.codjo.mad.gui.i18n.InternationalizationUtil.retrieveTranslationNotifier;
/**
 *
 */
public abstract class AbstractInternationalizableGuiPlugin extends AbstractGuiPlugin {

    protected abstract void registerLanguageBundles(TranslationManager translationManager);


    public void initGui(GuiConfiguration configuration) throws Exception {
        initInternationalizationPlugin(configuration.getGuiContext());
        registerLanguageBundles(retrieveTranslationManager(configuration.getGuiContext()));
        ErrorDialog.setTranslationBackpack(retrieveTranslationManager(configuration.getGuiContext()),
                                           retrieveTranslationNotifier(configuration.getGuiContext()));
    }


    private void initInternationalizationPlugin(MutableGuiContext guiContext) {
        // Todo enlever ce hack dès que possible
        // temporaire en attendant que toutes les applis ajoutent le plugin
        Object manager = null;
        if (guiContext.hasProperty(TranslationManager.TRANSLATION_MANAGER_PROPERTY)) {
            manager = guiContext.getProperty(TranslationManager.TRANSLATION_MANAGER_PROPERTY);
        }
        if (manager == null) {
            TranslationManager translationManager = new TranslationManager();
            guiContext.putProperty(TranslationManager.TRANSLATION_MANAGER_PROPERTY, translationManager);
            TranslationNotifier translationNotifier = new TranslationNotifier(Language.FR, translationManager);
            guiContext.putProperty(TranslationNotifier.TRANSLATION_NOTIFIER_PROPERTY, translationNotifier);
        }
    }
}
