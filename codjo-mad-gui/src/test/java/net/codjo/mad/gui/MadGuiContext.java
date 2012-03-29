package net.codjo.mad.gui;
import net.codjo.gui.toolkit.util.ErrorDialog;
import net.codjo.i18n.common.Language;
import net.codjo.i18n.common.TranslationManager;
import net.codjo.mad.gui.util.InternationalizableGuiContext;

import static net.codjo.mad.gui.i18n.InternationalizationUtil.retrieveTranslationManager;
import static net.codjo.mad.gui.i18n.InternationalizationUtil.retrieveTranslationNotifier;

public class MadGuiContext extends InternationalizableGuiContext {
    public MadGuiContext() {
        TranslationManager translationManager = retrieveTranslationManager(this);
        translationManager.addBundle("net.codjo.mad.gui.i18n", Language.FR);
        translationManager.addBundle("net.codjo.mad.gui.i18n", Language.EN);
        ErrorDialog.setTranslationBackpack(translationManager, retrieveTranslationNotifier(this));
    }
}
