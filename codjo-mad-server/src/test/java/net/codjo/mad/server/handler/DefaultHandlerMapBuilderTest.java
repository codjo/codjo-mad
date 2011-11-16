/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.mad.server.handler;
import net.codjo.security.common.api.UserMock;
import net.codjo.test.common.LogString;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.picocontainer.MutablePicoContainer;
/**
 * Classe de test de {@link DefaultHandlerMapBuilder}.
 */
public class DefaultHandlerMapBuilderTest extends AbstractHandlerMapBuilderTestCase {
    private DefaultHandlerMapBuilder builder;


    public void test_createHandlerMap() throws Exception {
        Object[] configs = {new StringBuffer("config")};
        DefaultHandlerMap handlerMap =
              (DefaultHandlerMap)builder.createHandlerMap(null, configs);

        MutablePicoContainer picoContainer = handlerMap.getPicoContainer();
        assertEquals("config",
                     picoContainer.getComponentInstance(StringBuffer.class).toString());
    }


    public void test_collectHandlerFrom_empty() throws Exception {
        builder.collectHandlerFrom(DefaultHandlerMapTest.class, "org.apache");

        HandlerMap handlerMap = builder.createHandlerMap(null, new Object[0]);
        assertNotNull(handlerMap);
        assertEquals(Collections.<String>emptySet(), handlerMap.getHandlerIdSet());
    }


    public void test_collectHandlerFrom() throws Exception {
        builder.collectHandlerFrom(DefaultHandlerMapTest.class, "net.codjo.mad.server.handler");

        Set<String> expectedSet = new HashSet<String>();
        expectedSet.add(handlerIdOf(DummyHandler.class));
        expectedSet.add(handlerIdOf(HandlerMock.class));
        expectedSet.add(handlerIdOf(HandlerMockUsingLogString.class));
        expectedSet.add(handlerIdOf(ConstructorWithExceptionHandler.class));
        expectedSet.add(handlerIdOf(UnbuildableHandler.class));
        expectedSet.add(handlerIdOf(WithPrivateConstructorHandler.class));
        expectedSet.add(handlerIdOf(ConstructorWithRuntimeExceptionHandler.class));
        // Pour éviter la dépendance du package handler vers sql
        expectedSet.add("handlerSqlMock");

        assertEquals(expectedSet,
                     builder.createHandlerMap(null, new Object[]{new UserMock()}).getHandlerIdSet());
    }


    public void test_addSessionComponent() throws Exception {
        builder.addSessionComponent(LogString.class);
        builder.addHandler(HandlerMockUsingLogString.class);

        Handler handler = builder.createHandlerMap(null, new Object[0]).getHandler(HandlerMockUsingLogString.MOCK_ID);

        assertNotNull(handler);
        assertTrue(handler instanceof HandlerMockUsingLogString);
        assertNotNull(((HandlerMockUsingLogString)handler).getLog());
    }


    public void test_removeSessionComponent() throws Exception {
        builder.addSessionComponent(LogString.class);
        builder.removeSessionComponent(LogString.class);
        builder.addHandler(HandlerMockUsingLogString.class);

        Handler handler = builder.createHandlerMap(null, new Object[0]).getHandler(HandlerMockUsingLogString.MOCK_ID);

        assertNull(((HandlerMockUsingLogString)handler).getLog());
    }


    public void test_addHandlerCommand() throws Exception {
        builder.addUserHandler(HandlerCommandMock.class);

        Handler handler = builder.createHandlerMap(null, new Object[0])
              .getHandler(HandlerCommandMock.MOCK_ID);

        assertNotNull(handler);
        assertTrue(handler instanceof HandlerCommandMock);
    }


    @Override
    protected void setUp() throws Exception {
        builder = new DefaultHandlerMapBuilder();
    }


    @Override
    protected HandlerMapBuilder createHandlerMapBuilder() {
        return builder;
    }
}
