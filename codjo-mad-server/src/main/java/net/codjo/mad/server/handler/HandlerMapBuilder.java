/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.mad.server.handler;
import net.codjo.agent.UserId;
/**
 * Builder de {@link HandlerMap}.
 */
public interface HandlerMapBuilder {
    void collectHandlerFrom(Class fromClass, String inPackage) throws BuildException;


    void addGlobalComponent(Class aClass);


    void addGlobalComponent(Object object);


    void addSessionComponent(Class aClass);


    void removeSessionComponent(Class aClass);


    void addUserHandler(Class<? extends Handler> handlerCommandClass);


    HandlerMap createHandlerMap(UserId userId, Object[] contextualInstances);


    /**
     * Exception du builder de {@link HandlerMap}.
     */
    public static class BuildException extends WithCauseException {
        public BuildException(String message, Exception cause) {
            super(message, cause);
        }
    }
}
