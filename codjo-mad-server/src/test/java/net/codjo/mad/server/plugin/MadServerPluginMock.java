/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.mad.server.plugin;
import net.codjo.agent.AgentContainer;
import net.codjo.agent.ContainerConfiguration;
import net.codjo.agent.ContainerFailureException;
import net.codjo.mad.server.handler.AspectLauncher;
import net.codjo.mad.server.handler.HandlerCommand;
import net.codjo.mad.server.handler.HandlerMapBuilder;
import net.codjo.test.common.LogString;
/**
 *
 */
public class MadServerPluginMock extends MadServerPlugin {
    private LogString log = new LogString();


    public MadServerPluginMock() {
        this(new LogString());
    }


    public MadServerPluginMock(LogString log) {
        super("/conf/castor-config.xml", MadServerPluginMock.class);
        this.log = log;
    }


    @Override
    public MadServerPluginConfiguration getConfiguration() {
        return new MadServerPluginConfigurationMock(new LogString("madServerPluginConfiguration", log));
    }


    @Override
    public MadServerOperations getOperations() {
        return new MadServerOperationsMock();
    }


    @Override
    public void start(AgentContainer agentContainer) throws HandlerMapBuilder.BuildException,
                                                            ContainerFailureException {
        log.call("start");
    }


    @Override
    public void initContainer(ContainerConfiguration containerConfiguration) {
        log.call("initContainer");
    }


    @Override
    public void stop() {
        log.call("stop");
    }


    private String toSimpleName(Class aClass) {
        String name = aClass.getName();
        return name.substring(name.lastIndexOf(".") + 1, name.length());
    }


    private class MadServerOperationsMock extends MadServerOperationsImpl {
        @Override
        public AspectLauncher createAspectLauncher() {
            return null;
        }
    }

    private class MadServerPluginConfigurationMock extends MadServerPluginConfiguration {
        private final LogString log;


        private MadServerPluginConfigurationMock(LogString log) {
            this.log = log;
        }


        @Override
        public void addSessionComponent(Class aClass) {
            log.call("addSessionComponent", toSimpleName(aClass));
        }


        @Override
        public void removeSessionComponent(Class aClass) {
            log.call("removeSessionComponent", toSimpleName(aClass));
        }


        @Override
        public void addHandlerCommand(Class<? extends HandlerCommand> handlerCommandClass) {
            log.call("addHandlerCommand", toSimpleName(handlerCommandClass));
        }
    }
}
