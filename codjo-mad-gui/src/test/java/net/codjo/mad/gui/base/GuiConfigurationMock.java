package net.codjo.mad.gui.base;
import net.codjo.mad.common.structure.StructureReader;
import net.codjo.mad.gui.framework.DefaultGuiContext;
import net.codjo.mad.gui.framework.MutableGuiContext;
import net.codjo.plugin.common.ApplicationPlugin;
import net.codjo.security.common.api.UserMock;
import net.codjo.test.common.LogString;
import net.codjo.i18n.common.TranslationManager;
import net.codjo.i18n.common.Language;
import net.codjo.i18n.gui.TranslationNotifier;
import javax.swing.Action;
/**
 *
 */
public class GuiConfigurationMock implements GuiConfiguration {
    private LogString log = new LogString();
    private Action lastRegisteredAction;
    private StructureReader structureReaderMock;
    private DefaultGuiContext guiContextMock = new DefaultGuiContext();


    public GuiConfigurationMock() {
        guiContextMock.setUser(new UserMock().mockIsAllowedTo(true));
        TranslationManager translationManager = new TranslationManager();
        guiContextMock.putProperty(TranslationManager.TRANSLATION_MANAGER_PROPERTY, translationManager);
        TranslationNotifier translationNotifier = new TranslationNotifier(Language.FR, translationManager);
        guiContextMock.putProperty(TranslationNotifier.TRANSLATION_NOTIFIER_PROPERTY, translationNotifier);        
    }


    public GuiConfigurationMock(LogString log) {
        this();
        this.log = log;
    }


    public StructureReader getStructureReader() {
        return structureReaderMock;
    }


    public MutableGuiContext getGuiContext() {
        return guiContextMock;
    }


    public void addToStatusBar(ComponentBuilder builder) {
        log.call("addToStatusBar", toSimpleName(builder.getClass()));
    }


    public void registerAction(ApplicationPlugin plugin, String actionId, Class<? extends Action> action) {
        log.call("registerAction", toSimpleName(plugin.getClass()), actionId, toSimpleName(action));
    }


    public void registerAction(ApplicationPlugin plugin, String actionId, Action action) {
        log.call("registerAction",
                 toSimpleName(plugin.getClass()),
                 actionId,
                 toSimpleName(action.getClass()));
        lastRegisteredAction = action;
    }


    private String toSimpleName(Class aClass) {
        String name = aClass.getName();
        return name.substring(name.lastIndexOf('.') + 1, name.length());
    }


    public Action getLastRegisteredAction() {
        return lastRegisteredAction;
    }


    public void mockGetStructureReader(StructureReader mock) {
        this.structureReaderMock = mock;
    }


    public void mockGetGuiContext(DefaultGuiContext mock) {
        this.guiContextMock = mock;
    }
}
