/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.mad.server.handler;
import net.codjo.agent.UserId;
import net.codjo.mad.common.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.ConstructorInjectionComponentAdapterFactory;
import org.picocontainer.defaults.DefaultPicoContainer;
/**
 *
 */
public abstract class AbstractHandlerMapBuilder implements HandlerMapBuilder {
    protected Map<String, Object> handlerMap = new HashMap<String, Object>();
    protected Map<String, Class> userHandlerMap = new HashMap<String, Class>();
    protected MutablePicoContainer parentContainer = null;
    protected List<Class> implementations = new ArrayList<Class>();


    protected AbstractHandlerMapBuilder() {
    }


    protected AbstractHandlerMapBuilder(MutablePicoContainer parentContainer) {
        this.parentContainer = parentContainer;
    }


    public HandlerMap createHandlerMap(UserId userId, Object[] contextualInstances) {
        DefaultPicoContainer container =
              new DefaultPicoContainer(new ConstructorInjectionComponentAdapterFactory(), parentContainer);

        for (Class implementation : implementations) {
            container.registerComponentImplementation(implementation);
        }

        for (Object contextualInstance : contextualInstances) {
            container.registerComponentInstance(contextualInstance);
        }

        for (Map.Entry<String, Class> entry : userHandlerMap.entrySet()) {
            String handlerId = entry.getKey();
            container.registerComponentImplementation(handlerId, entry.getValue());
        }

        for (Map.Entry<String, Object> entry : handlerMap.entrySet()) {
            registerHandler(container, entry);
        }

        return generateHandlerMap(container);
    }


    protected abstract void registerHandler(DefaultPicoContainer container, Map.Entry entry);


    protected abstract HandlerMap generateHandlerMap(DefaultPicoContainer container);


    public void addGlobalComponent(Class aClass) {
        parentContainer.registerComponentImplementation(aClass);
    }


    public void addGlobalComponent(Object object) {
        parentContainer.registerComponentInstance(object);
    }


    public void addSessionComponent(Class aClass) {
        implementations.add(aClass);
    }


    public void removeSessionComponent(Class aClass) {
        implementations.remove(aClass);
    }


    public void addUserHandler(Class<? extends Handler> userHandlerClass) {
        String handlerId = getIdFromHandlerName(userHandlerClass.getName());
        Log.info("\tUserHandler " + handlerId);
        userHandlerMap.put(handlerId, userHandlerClass);
    }


    public static String getIdFromHandlerName(String handlerClassName) {
        int start = handlerClassName.lastIndexOf('.');
        int end = handlerClassName.length();
        if (handlerClassName.endsWith("Handler")) {
            end -= "Handler".length();
        }
        else if (handlerClassName.endsWith("Command")) {
            end -= "Command".length();
        }

        String name = handlerClassName.substring(start + 1, end);
        return Character.toLowerCase(name.charAt(0)) + name.substring(1);
    }
}
