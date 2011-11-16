package net.codjo.mad.gui.base;
import net.codjo.agent.AgentContainer;
import net.codjo.agent.ContainerConfiguration;
import net.codjo.agent.UserId;
import net.codjo.agent.test.AgentContainerFixture;
import net.codjo.mad.gui.request.Preference;
import net.codjo.mad.gui.request.PreferenceFactory;
import net.codjo.mad.gui.util.ApplicationData;
import net.codjo.plugin.common.ApplicationCoreTestCase;
import net.codjo.plugin.common.ApplicationPlugin;
import net.codjo.plugin.common.DefaultPluginsLifecycleTest.CoreWrapperMock;
import net.codjo.plugin.common.PluginsLifecycle.LifecycleListener;
import net.codjo.plugin.gui.GuiPluginsLifecycle;
import net.codjo.security.client.plugin.SecurityClientPluginConfiguration;
import net.codjo.test.common.LogString;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.log4j.Logger;
/**
 *
 */
public class MadGuiCoreTest extends ApplicationCoreTestCase<MadGuiCore> {
    private AgentContainerFixture fixture = new AgentContainerFixture();
    private MadGuiCore madGuiCore = new MadGuiCore();


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        fixture.doSetUp();
    }


    @Override
    protected void tearDown() throws Exception {
        fixture.doTearDown();
        super.tearDown();
    }


    public void test_cleanPreferences() throws Exception {
        PreferenceFactory.initFactory();
        PreferenceFactory.addPreference(new Preference("old-preferences"));

        new MadGuiCore();

        try {
            PreferenceFactory.getPreference("old-preferences");
            fail();
        }
        catch (IllegalArgumentException ex) {
            assertEquals("L'identifiant 'old-preferences' est inconnu.", ex.getMessage());
        }
    }


    public void test_initGui() throws Exception {
        madGuiCore.addGlobalComponent(log);

        madGuiCore.addPlugin(MyMadGuiPlugin.class);
        madGuiCore.addPlugin(MyStandardGuiPlugin.class);

        List<ApplicationPlugin> plugins = madGuiCore.getPlugins();

        GuiPluginsLifecycle pluginsLifecycle = new GuiPluginsLifecycle(Logger.getRootLogger());

        pluginsLifecycle.setGuiConfiguration(new GuiConfigurationMock(log));

        pluginsLifecycle
              .start(plugins,
                     new CoreWrapperMock(),
                     new ArrayList<LifecycleListener>());

        log.assertContent("madGuiPlugin.initContainer(null), standardGuiPlugin.initContainer(null)"
                          + ", madGuiPlugin.initGui(GuiConfigurationMock)"
                          + ", standardGuiPlugin.initGui(GuiConfigurationMock)");
    }


    public void test_getApplicationTitle() throws Exception {
        madGuiCore.addGlobalComponent(UserId.class, UserId.createId("myLogin", "myPassword"));
        Properties props = new Properties();
        props.setProperty("application.name", "ride-gui");
        props.setProperty("application.version", "1.77-SNAPSHOT");

        assertApplicationTitle("ride-gui - 1.77-SNAPSHOT - myLogin", props);
    }


    public void test_getApplicationTitle_withEnvironment() throws Exception {
        madGuiCore.getContainerConfig().setParameter("user.environment", "MyEnvironment");
        madGuiCore.addGlobalComponent(UserId.class, UserId.createId("myLogin", "myPassword"));
        Properties props = new Properties();
        props.setProperty("application.name", "ride-gui");
        props.setProperty("application.version", "1.77-SNAPSHOT");

        assertApplicationTitle("ride-gui - 1.77-SNAPSHOT - myLogin - MyEnvironment", props);
    }


    @Override
    protected void preApplicationCoreTest() {
        fixture.startContainer();
    }


    @Override
    protected MadGuiCore createApplicationCore() throws Exception {
        return new MadGuiCore();
    }


    @Override
    protected String getExpectedContainerName() {
        return "madGuiCore";
    }


    @Override
    protected String getTestConfigFileName() {
        return "MadGuiCoreTest.properties";
    }


    private void assertApplicationTitle(String expected, Properties props) {
        madGuiCore.applicationData = new ApplicationData(props);
        assertEquals(expected, madGuiCore.getApplicationTitle());
    }


    public class RetryConnectionMadGuiPlugin extends AbstractGuiPlugin {
        private int startCount = 0;


        public void initGui(GuiConfiguration configuration) throws Exception {
        }


        @Override
        public void start(AgentContainer agentContainer) throws Exception {
            startCount++;
            if (startCount == 1) {
                throw new RuntimeException("Pas de réponse");
            }
        }


        public int getStartCount() {
            return startCount;
        }


        public void resetStartCount() {
            startCount = 0;
        }
    }

    public static class MyMadGuiPlugin extends AbstractGuiPlugin {
        private final LogString log;


        public MyMadGuiPlugin(LogString log) {
            this.log = new LogString("madGuiPlugin", log);
        }


        @Override
        public void initContainer(ContainerConfiguration containerConfiguration) throws Exception {
            log.call("initContainer",
                     containerConfiguration.getParameter(SecurityClientPluginConfiguration.LDAP_PARAMETER));
        }


        public void initGui(GuiConfiguration configuration) throws Exception {
            log.call("initGui", configuration.getClass().getSimpleName());
        }
    }

    public static class MyStandardGuiPlugin extends net.codjo.plugin.gui.AbstractGuiPlugin {
        private final LogString log;


        public MyStandardGuiPlugin(LogString log) {
            this.log = new LogString("standardGuiPlugin", log);
        }


        @Override
        public void initContainer(ContainerConfiguration containerConfiguration) throws Exception {
            log.call("initContainer",
                     containerConfiguration.getParameter(SecurityClientPluginConfiguration.LDAP_PARAMETER));
        }


        @Override
        public void initGui(net.codjo.plugin.gui.GuiConfiguration configuration) throws Exception {
            log.call("initGui", configuration.getClass().getSimpleName());
        }
    }
}
