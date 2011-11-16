package net.codjo.mad.gui.base;
import net.codjo.agent.AgentContainer;
import net.codjo.agent.UserId;
import net.codjo.i18n.common.TranslationManager;
import net.codjo.i18n.gui.TranslationNotifier;
import net.codjo.i18n.gui.plugin.InternationalizationGuiPlugin;
import net.codjo.mad.gui.framework.DefaultGuiContext;
import net.codjo.mad.gui.framework.GuiEvent;
import net.codjo.mad.gui.framework.LocalGuiContext;
import net.codjo.mad.gui.request.PreferenceFactory;
import net.codjo.mad.gui.request.action.ModalityService;
import net.codjo.mad.gui.util.ApplicationData;
import net.codjo.plugin.common.PluginsLifecycle.LifecycleListener;
import net.codjo.plugin.gui.GuiCore;
import net.codjo.plugin.gui.GuiPluginsLifecycle;
import net.codjo.security.common.api.User;
import java.awt.Dimension;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import javax.swing.JDesktopPane;
import javax.swing.JMenu;
import javax.swing.SwingUtilities;
/**
 * IHM générique d'une application, basée sur la notion de {@link net.codjo.plugin.common.ApplicationPlugin}.
 */
public class MadGuiCore extends GuiCore<MainWindow> {
    private static final String USER_ENVIRONMENT = "user.environment";
    private GuiClientConfigurationImpl configuration = new GuiClientConfigurationImpl();
    ApplicationData applicationData;


    public MadGuiCore() {
        init(toResource("/conf/menu.xml"), "Le fichier /conf/menu.xml est introuvable.",
             toResource("/conf/toolbar.xml"), "Le fichier /conf/toolbar.xml est introuvable.");
        configuration.setMainWindowSize(new Dimension(1200, 900));
    }


    public MadGuiCore(URL menuConfigUrl, URL toolbarConfigUrl) {
        init(menuConfigUrl, "L'URL vers le fichier XML du menu est nulle.",
             toolbarConfigUrl, "L'URL vers le fichier XML de la toolbar est nulle.");
    }


    private void init(URL menuConfigUrl,
                      String menuErrorMsg,
                      URL toolbarConfigUrl,
                      String toolbarErrorMsg) {

        GuiPluginsLifecycle lifecycle = new GuiPluginsLifecycle(getLogger());
        setPluginsLifecycle(lifecycle);
        addLifecycleListener(new MadGuiLifecycleTasks(lifecycle));

        PreferenceFactory.clearPreferences();
        configuration.setMenuConfigUrl(menuConfigUrl);
        configuration.setToolbarConfigUrl(toolbarConfigUrl);
        if (configuration.getMenuConfigUrl() == null) {
            getLogger().warn(menuErrorMsg);
        }
        if (configuration.getToolbarConfigUrl() == null) {
            getLogger().warn(toolbarErrorMsg);
        }
    }


    public MadGuiCoreConfiguration getConfiguration() {
        return configuration;
    }


    public void show(String[] arguments, ApplicationData data) {
        this.applicationData = data;
        show(arguments);
    }


    private void registerInternationalizationComponents(DefaultGuiContext guiContext) {
        TranslationManager translationManager = getGlobalComponent(TranslationManager.class);
        guiContext.putProperty(TranslationManager.TRANSLATION_MANAGER_PROPERTY, translationManager);
        TranslationNotifier translationNotifier = getGlobalComponent(TranslationNotifier.class);
        guiContext.putProperty(TranslationNotifier.TRANSLATION_NOTIFIER_PROPERTY, translationNotifier);
        JMenu menu = translationManager == null ?
                     null : InternationalizationGuiPlugin.getLanguagesMenu(translationNotifier);
        guiContext.putProperty("Menu Langues", menu);
    }


    String getApplicationTitle() {
        UserId userId = getGlobalComponent(UserId.class);

        StringBuilder title = new StringBuilder()
              .append(applicationData.getName())
              .append(" - ").append(applicationData.getVersion())
              .append(" - ").append(userId.getLogin());

        String environment = getContainerConfig().getParameter(USER_ENVIRONMENT);
        if (environment != null) {
            title.append(" - ").append(environment);
        }
        return title.toString();
    }


    @Override
    protected MainWindow createMainWindow() {
        MainWindow window = new MainWindow(applicationData);
        window.getGuiContext().putProperty(ModalityService.class, new ModalityService());
        return window;
    }


    private void finishDisplay(final LocalGuiContext menuContext,
                               final List<ComponentBuilder> componentBuilders) throws Exception {
        FutureTask<Exception> futureTask = new FutureTask<Exception>(new Callable<Exception>() {
            public Exception call() throws Exception {
                try {
                    getWindow().finishDisplay(menuContext, componentBuilders);
                    getWindow().getGuiContext().sendEvent(GuiEvent.LOGIN);
                }
                catch (Exception error) {
                    return error;
                }
                return null;
            }
        });
        SwingUtilities.invokeAndWait(futureTask);

        Exception exception = futureTask.get();
        if (exception != null) {
            throw exception;
        }
    }


    private static URL toResource(String name) {
        return MadGuiCore.class.getResource(name);
    }


    private class QuitListener implements Observer {
        public void update(Observable observable, Object argument) {
            if (argument == GuiEvent.QUIT) {
                try {
                    stop();
                    System.exit(0);
                }
                catch (Exception e) {
                    getLogger().warn("Quit avec erreur : " + e.getLocalizedMessage(), e);
                    try {
                        System.exit(-1);
                    }
                    catch (Exception exception) {
                        getLogger().debug("Quit en erreur avec erreur !", exception);
                    }
                }
            }
        }
    }
    private static class GuiClientConfigurationImpl implements MadGuiCoreConfiguration {
        private URL menuConfigUrl;
        private URL toolbarConfigUrl;
        private Dimension dimension;


        public URL getMenuConfigUrl() {
            return menuConfigUrl;
        }


        public void setMenuConfigUrl(URL menuConfigUrl) {
            this.menuConfigUrl = menuConfigUrl;
        }


        public URL getToolbarConfigUrl() {
            return toolbarConfigUrl;
        }


        public void setToolbarConfigUrl(URL toolbarConfigUrl) {
            this.toolbarConfigUrl = toolbarConfigUrl;
        }


        public Dimension getMainWindowSize() {
            return dimension;
        }


        public void setMainWindowSize(Dimension dimension) {
            this.dimension = dimension;
        }
    }
    private class MadGuiLifecycleTasks extends LifecycleListener {
        private LocalGuiContext menuContext;
        private ArrayList<ComponentBuilder> componentBuilders;
        private GuiPluginsLifecycle madLifecycle;


        MadGuiLifecycleTasks(GuiPluginsLifecycle lifecycle) {
            this.madLifecycle = lifecycle;
        }


        @Override
        public void beforeInitGui() throws Exception {
            if (getWindow() == null) {
                getLogger().info("Desactivation de l'initialisation GUI du socle (pas de fenetre disponible)");
                return;
            }
            getWindow().setTitle(getApplicationTitle());

            if (configuration.getMainWindowSize() != null) {
                getWindow().setSize(configuration.getMainWindowSize());
            }
            getWindow().setMenuConfigUrl(configuration.getMenuConfigUrl());
            getWindow().setToolbarConfigUrl(configuration.getToolbarConfigUrl());

            getWindow().getGuiContext().addObserver(new QuitListener());

            getWindow().getGuiContext().putProperty(GuiPlugin.AGENT_CONTAINER_KEY, getAgentContainer());
            getWindow().getGuiContext().setUser(getGlobalComponent(User.class));

            registerInternationalizationComponents(getWindow().getGuiContext());

            addGlobalComponent(AgentContainer.class, getAgentContainer());
            addGlobalComponent(getWindow().getGuiContext().getDesktopPane());

            menuContext = new LocalGuiContext(getWindow().getGuiContext());
            componentBuilders = new ArrayList<ComponentBuilder>();

            madLifecycle.setGuiConfiguration(new DefaultGuiConfiguration(createChildPicoContainer(),
                                                                         getWindow().getGuiContext(),
                                                                         menuContext,
                                                                         componentBuilders));
        }


        @Override
        public void afterInitGui() throws Exception {
            if (getWindow() == null) {
                return;
            }

            getWindow().initI18nComponents(getWindow().getGuiContext());

            finishDisplay(menuContext, componentBuilders);
        }


        @Override
        public void beforeStop() throws Exception {
            removeGlobalComponent(AgentContainer.class);
            removeGlobalComponent(JDesktopPane.class);
        }
    }
}
