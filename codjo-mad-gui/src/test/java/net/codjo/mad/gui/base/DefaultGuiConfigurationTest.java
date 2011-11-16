package net.codjo.mad.gui.base;
import net.codjo.mad.client.request.MadServerFixture;
import net.codjo.mad.gui.framework.DefaultGuiContext;
import net.codjo.mad.gui.framework.LocalGuiContext;
import net.codjo.security.common.api.UserMock;
import net.codjo.test.common.LogString;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;
/**
 * Classe de test de {@link DefaultGuiConfiguration}.
 */
public class DefaultGuiConfigurationTest {
    private MadServerFixture fixture = new MadServerFixture();
    private LogString log = new LogString();
    private DefaultGuiContext root = new DefaultGuiContext();
    private LocalGuiContext menu = new LocalGuiContext(root);
    private MutablePicoContainer pico = new DefaultPicoContainer();


    @Before
    public void setUp() throws Exception {
        fixture.doSetUp();
        mockGetStructure();
    }


    @After
    public void tearDown() throws Exception {
        fixture.doTearDown();
    }


    @Test
    public void test_guiContextPutInPico() throws Exception {
        new DefaultGuiConfiguration(pico, root, menu);

        assertSame(root, pico.getComponentInstance(root.getClass()));
    }


    @Test
    public void test_registerAction() throws Exception {
        DefaultGuiConfiguration configuration = new DefaultGuiConfiguration(pico, root, menu);

        configuration.registerAction(new GuiPluginMock(), "myActionId", new ActionMock());

        assertNotNull(menu.getProperty(GuiPluginMock.class.getName() + "#myActionId"));
        assertNotNull(menu.getProperty("GuiPluginMock#myActionId"));
    }


    @Test
    public void test_registerActionWithGuiAction() throws Exception {
        DefaultGuiConfiguration configuration = new DefaultGuiConfiguration(pico, root, menu);
        root.setUser(new UserMock());

        GuiActionMock guiAction = new GuiActionMock(root);
        configuration.registerAction(new GuiPluginMock(), "myActionId", guiAction);

        assertEquals("net.codjo.mad.gui.base.GuiPluginMock#myActionId", guiAction.getSecurityFunction());
    }


    @Test
    public void test_registerAction_usingPicocontainer() throws Exception {
        pico.registerComponentInstance(log);
        DefaultGuiConfiguration configuration = new DefaultGuiConfiguration(pico, root, menu);

        configuration.registerAction(new GuiPluginMock(), "myActionId", ActionMock.class);

        assertNotNull(menu.getProperty(GuiPluginMock.class.getName() + "#myActionId"));
        assertNotNull(menu.getProperty("GuiPluginMock#myActionId"));

        log.assertContent("new ActionMock(LogString)");
    }


    @Test
    public void test_addToStatusBar() throws Exception {
        List<ComponentBuilder> componentBuilders = new ArrayList<ComponentBuilder>();
        DefaultGuiConfiguration configuration = new DefaultGuiConfiguration(pico,
                                                                            root,
                                                                            menu,
                                                                            componentBuilders);

        ComponentBuilderMock builder1 = new ComponentBuilderMock();
        configuration.addToStatusBar(builder1);
        ComponentBuilderMock builder2 = new ComponentBuilderMock();
        configuration.addToStatusBar(builder2);

        assertEquals(2, componentBuilders.size());
        assertSame(builder1, componentBuilders.get(0));
        assertSame(builder2, componentBuilders.get(1));
    }


    @Test
    public void test_addToStatusBar_noStatusBarComponentBuilders() throws Exception {
        DefaultGuiConfiguration configuration = new DefaultGuiConfiguration(pico,
                                                                            root,
                                                                            menu);

        try {
            configuration.addToStatusBar(new ComponentBuilderMock());
            fail();
        }
        catch (Exception e) {
            assertEquals("Impossible d'enregister l'action !!!", e.getLocalizedMessage());
        }
    }


    private void mockGetStructure() {
        fixture.mockServerResult(new String[]{""}, new String[][]{{"<structure/>"}});
    }


    private class ComponentBuilderMock implements ComponentBuilder {
        ComponentBuilderMock() {
        }


        public JComponent build() {
            return null;  // Todo
        }
    }
}
