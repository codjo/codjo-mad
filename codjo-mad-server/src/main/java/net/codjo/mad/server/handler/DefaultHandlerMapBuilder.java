/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.mad.server.handler;
import net.codjo.mad.common.Log;
import net.codjo.reflect.collect.ClassCollector;
import net.codjo.reflect.collect.ClassFilter;
import net.codjo.reflect.collect.InheritanceFilter;
import net.codjo.reflect.collect.PackageFilter;
import java.lang.reflect.Modifier;
import java.util.Map;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;
/**
 * Builder de {@link DefaultHandlerMap}.
 */
public class DefaultHandlerMapBuilder extends AbstractHandlerMapBuilder {
    public DefaultHandlerMapBuilder() {
    }


    public DefaultHandlerMapBuilder(MutablePicoContainer parentContainer) {
        super(parentContainer);
    }


    public void collectHandlerFrom(Class fromClass, String inPackage) throws BuildException {
        try {
            ClassCollector collector = new ClassCollector(fromClass);
            collector.addClassFilter(new PackageFilter(inPackage, true));
            collector.addClassFilter(new InheritanceFilter(Handler.class, false));
            collector.addClassFilter(new OnlyPublicClassFilter());
            collector.addClassFilter(new RemoveHandlerCommandFilter());

            Class[] handlerClasses = collector.collect();
            for (Class handlerClass : handlerClasses) {
                addHandler(handlerClass);
            }
        }
        catch (Exception e) {
            throw new BuildException("Recuperation d'handler en echec (" + fromClass
                                     + ", " + inPackage + ") : " + e.getLocalizedMessage(), e);
        }
    }


    void addHandler(Class handlerClass) {
        String id = getIdFromHandlerName(handlerClass.getName());
        Log.info("\tHandler " + id);
        if ("".equals(id.trim())) {
            Log.error("Handler avec un nom invalide de classe " + handlerClass);
        }
        handlerMap.put(id, handlerClass);
    }


    @Override
    protected void registerHandler(DefaultPicoContainer container, Map.Entry entry) {
        container.registerComponentImplementation(entry.getKey(), (Class)entry.getValue());
    }


    @Override
    protected HandlerMap generateHandlerMap(DefaultPicoContainer container) {
        return new DefaultHandlerMap(container);
    }


    private static class OnlyPublicClassFilter implements ClassFilter {
        public boolean accept(String fullClassName) throws ClassNotFoundException {
            Class currentClass = Class.forName(fullClassName);
            return Modifier.isPublic(currentClass.getModifiers());
        }
    }

    private static class RemoveHandlerCommandFilter implements ClassFilter {
        public boolean accept(String fullClassName) throws ClassNotFoundException {
            Class currentClass = Class.forName(fullClassName);
            return !HandlerCommand.class.isAssignableFrom(currentClass);
        }
    }
}
