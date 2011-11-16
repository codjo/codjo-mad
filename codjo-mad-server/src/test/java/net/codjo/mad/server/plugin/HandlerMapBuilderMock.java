/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.mad.server.plugin;
import net.codjo.agent.UserId;
import net.codjo.mad.server.handler.AbstractHandlerMapBuilder;
import net.codjo.mad.server.handler.Handler;
import net.codjo.mad.server.handler.HandlerMap;
import net.codjo.test.common.LogString;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import org.picocontainer.defaults.DefaultPicoContainer;
/**
 *
 */
public class HandlerMapBuilderMock extends AbstractHandlerMapBuilder {
    private LogString log;


    public HandlerMapBuilderMock(LogString log) {
        this.log = log;
    }


    @Override
    public HandlerMap createHandlerMap(UserId userId, Object[] contextualInstances) {
        log.call("createHandlerMap", toString(userId), toString(contextualInstances));
        return new HandlerMapMock();
    }


    private String toString(UserId userId) {
        return String.format("UserId(%s)", userId.getLogin());
    }


    public void collectHandlerFrom(Class aClass, String string) {
        log.call("collectHandlerFrom", aClass, string);
    }


    public void collectHandlerFrom(String resource, String inPackage) throws BuildException {
        log.call("collectHandlerFrom", resource, inPackage);
    }


    @Override
    protected void registerHandler(DefaultPicoContainer container, Map.Entry entry) {
        log.call("registerHandler");
    }


    @Override
    protected HandlerMap generateHandlerMap(DefaultPicoContainer container) {
        log.call("generateHandlerMap");
        return new HandlerMapMock();
    }


    public void collectHandlerFrom(Class[] classes, String string)
          throws BuildException {
        log.call("collectHandlerFrom", Arrays.asList(classes), string);
    }


    private String toString(Object[] objects) {
        StringBuilder buffer = new StringBuilder("{");
        for (Object object : objects) {
            if (buffer.length() != 1) {
                buffer.append(", ");
            }
            buffer.append(toSimpleName(object.getClass()));
        }
        return buffer.append("}").toString();
    }


    private String toSimpleName(Class aClass) {
        String name = aClass.getName();
        return name.substring(name.lastIndexOf(".") + 1, name.length());
    }


    private static class HandlerMapMock implements HandlerMap {
        public Handler getHandler(String string) {
            return null;
        }


        public Set<String> getHandlerIdSet() {
            return Collections.emptySet();
        }
    }
}
