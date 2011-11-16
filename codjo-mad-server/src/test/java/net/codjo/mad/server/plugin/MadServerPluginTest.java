/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.mad.server.plugin;
import net.codjo.agent.AgentContainerMock;
import net.codjo.agent.ContainerConfigurationMock;
import net.codjo.agent.test.AgentContainerFixture;
import net.codjo.mad.common.message.InstituteAmbassadorProtocol;
import net.codjo.mad.server.handler.AspectBranchLauncherFactory;
import net.codjo.mad.server.handler.Handler;
import net.codjo.mad.server.handler.HandlerCommand;
import net.codjo.mad.server.handler.HandlerListener;
import net.codjo.mad.server.handler.HandlerListenerMock;
import net.codjo.mad.server.handler.HandlerMapBuilder;
import net.codjo.mad.server.handler.sql.SqlHandler;
import net.codjo.mad.server.plugin.MadServerPlugin.MadServerOperationsImpl;
import net.codjo.mad.server.structure.GetStructureCommand;
import net.codjo.test.common.LogString;
import net.codjo.test.common.matcher.JUnitMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static net.codjo.test.common.matcher.JUnitMatchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class MadServerPluginTest {
    private LogString log = new LogString();
    private AgentContainerFixture fixture = new AgentContainerFixture();
    private MadServerPlugin plugin;


    @Before
    public void setUp() throws Exception {
        fixture.doSetUp();
        plugin = new MadServerPlugin("MadServerPluginTest_Castor.xml", MadServerPluginTest.class);
    }


    @After
    public void tearDown() throws Exception {
        fixture.doTearDown();
    }


    @Test
    public void test_other() throws Exception {
        plugin.initContainer(new ContainerConfigurationMock(log));
        plugin.stop();
        log.assertContent("");
    }


    @Test
    public void test_start() throws Exception {
        plugin.start(fixture.getContainer());
        fixture.assertContainsAgent(InstituteAmbassadorProtocol.SECRETARY_GENERAL_AGENT_NAME);
    }


    @Test
    public void test_setHandlerMapBuilder() throws Exception {
        plugin.getConfiguration().setHandlerMapBuilder(new HandlerMapBuilderMock(log) {
            @Override
            public void addUserHandler(Class<? extends Handler> userHandlerClass) {
                log.call("addUserHandler", userHandlerClass.getSimpleName());
            }
        });

        plugin.getConfiguration().addHandlerCommand(MyHandlerCommand.class);

        log.assertContent("addUserHandler(MyHandlerCommand)");
    }


    @Test
    public void test_defaultHandler_getStructure() throws Exception {
        plugin.start(new AgentContainerMock(new LogString()));

        Handler handler = plugin.getConfiguration().getHandlerMapBuilder()
              .createHandlerMap(null, new Object[0])
              .getHandler(new GetStructureCommand(null).getId());

        assertNotNull(handler);
        assertEquals(GetStructureCommand.class, handler.getClass());
    }


    @Test
    public void test_addHandlerCommand() throws Exception {
        MadServerPlugin.MadServerPluginConfiguration configuration = plugin.getConfiguration();
        assertNotNull(configuration);
        configuration.addHandlerCommand(MyHandlerCommand.class);
        Handler handler = getHandler(new MyHandlerCommand().getId());

        assertNotNull(handler);
        assertEquals(MyHandlerCommand.class, handler.getClass());
    }


    @Test
    public void test_addHandlerSql() throws Exception {
        MadServerPlugin.MadServerPluginConfiguration configuration = plugin.getConfiguration();
        configuration.addHandlerSql(MyHandlerSql.class);

        HandlerMapBuilder builder = plugin.getConfiguration().getHandlerMapBuilder();
        Handler handler = builder.createHandlerMap(null,
                                                   new Object[0]).getHandler(new MyHandlerSql().getId());

        assertNotNull(handler);
        assertEquals(MyHandlerSql.class, handler.getClass());
    }


    @Test
    public void test_getOperations_handlerListener() throws Exception {
        HandlerListener listener1 = new HandlerListenerMock();
        HandlerListener listener2 = new HandlerListenerMock();
        MadServerOperationsImpl operations = (MadServerOperationsImpl)plugin.getOperations();
        assertTrue(operations.getHandlerListeners().isEmpty());

        operations.addHandlerListener(listener1);
        operations.addHandlerListener(listener2);

        assertEquals(2, operations.getHandlerListeners().size());
        assertThat(operations.getHandlerListeners(), JUnitMatchers.hasItems(listener1, listener2));

        operations.removeHandlerListener(listener1);

        assertEquals(1, operations.getHandlerListeners().size());
        assertThat(operations.getHandlerListeners(), JUnitMatchers.hasItems(listener2));
    }


    @Test
    public void test_getConfiguration_sessionComponent() throws Exception {
        plugin.getConfiguration().addHandlerCommand(MyHandlerCommand.class);

        plugin.getConfiguration().addSessionComponent(MyBean.class);

        MyHandlerCommand handler = (MyHandlerCommand)getHandler(new MyHandlerCommand().getId());
        assertNotNull(handler.getBean());

        plugin.getConfiguration().removeSessionComponent(MyBean.class);

        handler = (MyHandlerCommand)getHandler(new MyHandlerCommand().getId());
        assertNull(handler.getBean());
    }


    @Test
    public void test_getConfiguration_addGlobalComponent_class() throws Exception {
        plugin.getConfiguration().addGlobalComponent(MyBean.class);
        plugin.getConfiguration().addHandlerCommand(MyHandlerCommand.class);

        Handler handler = getHandler(new MyHandlerCommand().getId());

        assertNotNull(((MyHandlerCommand)handler).getBean());
    }


    @Test
    public void test_getConfiguration_addGlobalComponent_instance() throws Exception {
        MyBean bean = new MyBean();

        plugin.getConfiguration().addGlobalComponent(bean);
        plugin.getConfiguration().addHandlerCommand(MyHandlerCommand.class);

        Handler handler = getHandler(new MyHandlerCommand().getId());

        assertSame(bean, ((MyHandlerCommand)handler).getBean());
    }


    @Test
    public void test_getConfiguration_aspectBranchLauncherFactory() throws Exception {
        AspectBranchLauncherFactory factory = plugin.getConfiguration().getAspectBranchLauncherFactory();
        assertNotNull(factory);
        try {
            factory.create();
            fail();
        }
        catch (UnsupportedOperationException ex) {
            assertThat(ex.getLocalizedMessage(),
                       equalTo("Aucun moteur permettant de gérer les aspects en mode fork n'a été configuré."
                               + " Avez-vous ajouté le plugin WorkflowServerPlugin après MadServerPlugin ?"));
        }
    }


    private Handler getHandler(String handlerId) {
        HandlerMapBuilder builder = plugin.getConfiguration().getHandlerMapBuilder();
        return builder.createHandlerMap(null, new Object[0]).getHandler(handlerId);
    }


    @SuppressWarnings({"ClassMayBeInterface"})
    public static class MyBean {

    }
    public static class MyHandlerCommand extends HandlerCommand {
        private MyBean bean;


        public MyHandlerCommand() {
        }


        public MyHandlerCommand(MyBean bean) {
            this.bean = bean;
        }


        @Override
        public CommandResult executeQuery(CommandQuery query) {
            return null;
        }


        public MyBean getBean() {
            return bean;
        }
    }

    public static class MyHandlerSql extends SqlHandler {

        public MyHandlerSql() {
            super(new String[0], "", null);
        }
    }
}
