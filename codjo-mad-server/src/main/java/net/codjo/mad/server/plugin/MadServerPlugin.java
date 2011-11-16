/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.mad.server.plugin;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import net.codjo.agent.AgentContainer;
import net.codjo.agent.ContainerConfiguration;
import net.codjo.agent.ContainerFailureException;
import net.codjo.aspect.AspectConfigException;
import net.codjo.aspect.AspectManager;
import net.codjo.mad.server.handler.AspectBranchLauncherFactory;
import net.codjo.mad.server.handler.AspectLauncher;
import net.codjo.mad.server.handler.HandlerCommand;
import net.codjo.mad.server.handler.HandlerListener;
import net.codjo.mad.server.handler.HandlerMapBuilder;
import net.codjo.mad.server.handler.HandlerMapBuilder.BuildException;
import net.codjo.mad.server.handler.LazyHandlerMapBuilder;
import net.codjo.mad.server.handler.sql.SqlHandler;
import net.codjo.mad.server.plugin.HandlerExecutor.HandlerExecutionMode;
import net.codjo.mad.server.structure.GetStructureCommand;
import net.codjo.mad.server.structure.StructureHome;
import net.codjo.plugin.common.ApplicationCore;
import net.codjo.plugin.common.session.SessionManager;
import net.codjo.plugin.server.ServerCore;
import net.codjo.plugin.server.ServerPlugin;
import org.picocontainer.MutablePicoContainer;

import static net.codjo.mad.common.message.InstituteAmbassadorProtocol.SECRETARY_GENERAL_AGENT_NAME;

public class MadServerPlugin implements ServerPlugin {
    static final String MAD_HANDLER_DISCOVERY_ERROR =
          "Impossible de trouver le handler de reference 'DummyGeneratedHandler'";
    private MadServerPluginConfiguration configuration = new MadServerPluginConfiguration();
    private MadServerOperationsImpl operations = new MadServerOperationsImpl();
    private String configFilePath;
    private final Class forConfigClass;
    private final Class forHandlerClass;
    private HandlerMapBuilder builder;
    private final SessionManager sessionManager;
    private AspectBranchLauncherFactory factory = new FailingAspectLauncherFactory();


    public MadServerPlugin(ApplicationCore applicationCore, SessionManager sessionManager) {
        this("/conf/castor-config.xml", MadServerPlugin.class, findMadHandler(),
             applicationCore, sessionManager);
    }


    public MadServerPlugin(String configurationFilePath, Class forConfigClass) {
        this(configurationFilePath, forConfigClass, forConfigClass, new ServerCore(), new SessionManager());
    }


    private MadServerPlugin(String configurationFilePath, Class forConfigClass, Class forHandlersClasses,
                            ApplicationCore core, SessionManager sessionManager) {
        this.sessionManager = sessionManager;
        this.configFilePath = configurationFilePath;
        this.forConfigClass = forConfigClass;
        this.forHandlerClass = forHandlersClasses;
        this.builder = createHandlerMapBuilder(core);
    }


    public void initContainer(ContainerConfiguration containerConfiguration) {
    }


    public void start(AgentContainer agentContainer)
          throws HandlerMapBuilder.BuildException, ContainerFailureException, AspectConfigException {
        collectDefaultHandlers(builder, forHandlerClass);

        URL resourceURL = forConfigClass.getResource(configFilePath);
        if (resourceURL == null) {
            throw new IllegalStateException("Aucune configuration disponible pour Castor : "
                                            + "la resource '" + configFilePath + "' est introuvable.");
        }

        //noinspection deprecation
        operations.backpack =
              BackPackBuilder.init()
                    .setSessionManager(sessionManager)
                    .setHandlerMapBuilder(builder)
                    .setCastorConfig(resourceURL)
                    .setHandlerListeners(operations.getHandlerListeners())
                    .setAspectManager(newAspectManager())
                    .setAspectBranchLauncherFactory(factory)
                    .setHandlerExecutorFactory(configuration.handlerExecutorFactory)
                    .setHandlerExecutionMode(configuration.handlerExecutionMode)
                    .get();
        if (operations.backpack.getHandlerExecutorFactory() == null) {
            operations.backpack
                  .setHandlerExecutorFactory(new DefaultHandlerExecutorFactory());
        }

        SecretaryGeneralAgent agent = new SecretaryGeneralAgent(operations.backpack);
        agentContainer.acceptNewAgent(SECRETARY_GENERAL_AGENT_NAME, agent).start();
    }


    public void stop() {
    }


    public MadServerPluginConfiguration getConfiguration() {
        return configuration;
    }


    public MadServerOperations getOperations() {
        return operations;
    }


    private static Class findMadHandler() {
        try {
            return Class.forName("net.codjo.mad.server.data.DummyGeneratedHandler");
        }
        catch (ClassNotFoundException cause) {
            IllegalArgumentException exception = new IllegalArgumentException(MAD_HANDLER_DISCOVERY_ERROR);
            exception.initCause(cause);
            throw exception;
        }
    }


    private static HandlerMapBuilder createHandlerMapBuilder(ApplicationCore core) {
        MutablePicoContainer pico = core.createChildPicoContainer();
        pico.registerComponentImplementation(StructureHome.class);
        return new LazyHandlerMapBuilder(pico);
    }


    private static AspectManager newAspectManager() throws AspectConfigException {
        AspectManager aspectManager = new AspectManager();
        aspectManager.load();
        return aspectManager;
    }


    private static void collectDefaultHandlers(HandlerMapBuilder builder,
                                               Class forHandlerClass) throws BuildException {
        builder.collectHandlerFrom(forHandlerClass, "net.codjo");
        builder.collectHandlerFrom(forHandlerClass, "com.agf");
        builder.addUserHandler(GetStructureCommand.class);
    }


    public class MadServerPluginConfiguration {
        private HandlerExecutorFactory handlerExecutorFactory;
        private HandlerExecutionMode handlerExecutionMode = HandlerExecutionMode.SYNCHRONOUS;


        public void setAspectBranchLauncherFactory(AspectBranchLauncherFactory factory) {
            MadServerPlugin.this.factory = factory;
        }


        public AspectBranchLauncherFactory getAspectBranchLauncherFactory() {
            return factory;
        }


        public void addGlobalComponent(Class aClass) {
            builder.addGlobalComponent(aClass);
        }


        public void addGlobalComponent(Object object) {
            builder.addGlobalComponent(object);
        }


        public void addSessionComponent(Class aClass) {
            builder.addSessionComponent(aClass);
        }


        public void removeSessionComponent(Class aClass) {
            builder.removeSessionComponent(aClass);
        }


        public void addHandlerCommand(Class<? extends HandlerCommand> handlerCommandClass) {
            builder.addUserHandler(handlerCommandClass);
        }


        public void addHandlerSql(Class<? extends SqlHandler> handlerCommandClass) {
            builder.addUserHandler(handlerCommandClass);
        }


        public HandlerMapBuilder getHandlerMapBuilder() {
            return builder;
        }


        public void setHandlerMapBuilder(HandlerMapBuilder handlerMapBuilder) {
            builder = handlerMapBuilder;
        }


        public void setHandlerExecutorFactory(HandlerExecutorFactory handlerExecutor,
                                              HandlerExecutionMode mode) {
            this.handlerExecutorFactory = handlerExecutor;
            this.handlerExecutionMode = mode;
        }
    }

    static class MadServerOperationsImpl implements MadServerOperations {
        private List<HandlerListener> listeners = new ArrayList<HandlerListener>();
        private BackPack backpack;


        public void addHandlerListener(HandlerListener listener) {
            listeners.add(listener);
        }


        public void removeHandlerListener(HandlerListener listener) {
            listeners.remove(listener);
        }


        public AspectLauncher createAspectLauncher() {
            return new AspectLauncher(backpack.getAspectManager(),
                                      backpack.getJdo(),
                                      backpack.getAspectBranchLauncherFactory());
        }


        public List<HandlerListener> getHandlerListeners() {
            return listeners;
        }
    }
}
