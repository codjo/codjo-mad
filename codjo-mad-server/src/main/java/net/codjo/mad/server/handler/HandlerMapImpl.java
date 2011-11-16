/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.mad.server.handler;
import net.codjo.mad.common.Log;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoException;
import org.picocontainer.defaults.DefaultPicoContainer;
/**
 *
 */
class HandlerMapImpl implements HandlerMap {
    protected MutablePicoContainer handlerContainer;


    protected HandlerMapImpl() {
        handlerContainer = new DefaultPicoContainer();
    }


    protected HandlerMapImpl(MutablePicoContainer container) {
        handlerContainer = container;
    }


    public Handler getHandler(String handlerId) {
        Log.info("Execution du handler " + handlerId);
        try {
            return (Handler)handlerContainer.getComponentInstance(handlerId);
        }
        catch (PicoException e) {
            Log.error(
                  "Pico a rencontre un probleme lors de la recuperation du handler (id = " + handlerId + ")",
                  e);
            throw e;
        }
        catch (RuntimeException e) {
            Log.error("Pb dans l'instanciation du handler (id = " + handlerId + ")", e);
            throw e;
        }
    }


    public Set<String> getHandlerIdSet() {
        Set<String> handlerIdSet = new HashSet<String>();
        Collection componentAdapters = handlerContainer.getComponentAdapters();
        for (Object object : componentAdapters) {
            ComponentAdapter componentAdapter = (ComponentAdapter)object;
            if (Handler.class.isAssignableFrom(componentAdapter.getComponentImplementation())) {
                String handlerId = componentAdapter.getComponentKey().toString();
                handlerIdSet.add(handlerId);
            }
        }
        return handlerIdSet;
    }
}
