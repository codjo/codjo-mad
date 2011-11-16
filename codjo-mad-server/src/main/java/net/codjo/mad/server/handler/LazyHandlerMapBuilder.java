/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.mad.server.handler;
import net.codjo.mad.common.Log;
import net.codjo.reflect.collect.ResourceCollector;
import net.codjo.reflect.collect.ResourceFilter;
import java.io.IOException;
import java.util.Map;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.ConstructorInjectionComponentAdapter;
import org.picocontainer.defaults.DefaultPicoContainer;
/**
 * Implementation d'un {@link HandlerMap} utilisant le principe de "Lazy Loading".
 *
 * <p> Les handlers sont collectés avec un {@link net.codjo.reflect.collect.ResourceCollector} et sont créés à
 * la demande. </p>
 */
public class LazyHandlerMapBuilder extends AbstractHandlerMapBuilder {
    private static final String CLASS_EXTENSION = ".class";


    public LazyHandlerMapBuilder() {
    }


    public LazyHandlerMapBuilder(MutablePicoContainer parentContainer) {
        super(parentContainer);
    }


    public void collectHandlerFrom(Class fromClass, String inPackage) throws BuildException {
        collectHandlerFrom(new ResourceCollector(fromClass), inPackage, fromClass);
    }


    @Override
    protected void registerHandler(DefaultPicoContainer container, Map.Entry entry) {
        LazyComponentAdapter handlerComponent =
              new LazyComponentAdapter((String)entry.getKey(), (String)entry.getValue());
        container.registerComponent(handlerComponent);
    }


    @Override
    protected HandlerMap generateHandlerMap(DefaultPicoContainer container) {
        return new HandlerMapImpl(container);
    }


    void addHandler(String id, String className) {
        Log.info("\tHandler " + id);
        handlerMap.put(id, className);
    }


    private void collectHandlerFrom(ResourceCollector collector, String inPackage,
                                    Class fromClass) throws BuildException {
        collector.setExcludeClassFile(false);
        collector.addResourceFilter(new HandlerFilter(inPackage));

        try {
            String[] resources = collector.collect();
            for (String handlerName : resources) {
                String className = convertToClassName(handlerName);
                String id = getIdFromHandlerName(className);

                addHandler(id, className);
            }
        }
        catch (IOException e) {
            throw new BuildException("Recuperation d'handler en echec (" + fromClass
                                     + ", " + inPackage + ") : " + e.getLocalizedMessage(), e);
        }
    }


    private static String convertToClassName(String handlerName) {
        return handlerName.substring(1, handlerName.length() - CLASS_EXTENSION.length())
              .replace('/', '.').replace('\\', '.');
    }


    private static class HandlerFilter implements ResourceFilter {
        private String inPackage;


        HandlerFilter(String inPackage) {
            this.inPackage = inPackage;
        }


        public boolean accept(String resourcePath) {
            if (!resourcePath.endsWith(CLASS_EXTENSION)) {
                return false;
            }
            if (resourcePath.contains("Abstract") || resourcePath.contains("$")) {
                return false;
            }

            String className = convertToClassName(resourcePath);
            if (className.endsWith(".Handler")) {
                return false;
            }
            if (!className.endsWith("Handler")) {
                return false;
            }
            return className.startsWith(inPackage);
        }
    }

    private static class LazyComponentAdapter extends ConstructorInjectionComponentAdapter {
        private String className;
        private Class implementation;


        LazyComponentAdapter(String key, String className)
              throws org.picocontainer.defaults.AssignabilityRegistrationException,
                     org.picocontainer.defaults.NotConcreteRegistrationException {
            super(key, String.class);
            this.className = className;
        }


        public String getClassName() {
            return className;
        }


        @Override
        public Class getComponentImplementation() {
            if (className == null) {
                return String.class;
            }

            try {
                if (implementation == null) {
                    implementation = Class.forName(className);
                }
                return implementation;
            }
            catch (ClassNotFoundException e) {
                return null;
            }
        }
    }
}
