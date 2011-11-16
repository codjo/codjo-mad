/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.mad.server.handler;
import net.codjo.test.common.LogString;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
/**
 * Classe de test de {@link LazyHandlerMapBuilder}.
 */
public class LazyHandlerMapBuilderTest extends AbstractHandlerMapBuilderTestCase {
    private LazyHandlerMapBuilder builder;


    public void test_getHandler() throws Exception {
        builder.addHandler("bobo", HandlerMock.class.getName());

        Handler handler = builder.createHandlerMap(null, new Object[]{}).getHandler("bobo");

        assertNotNull(handler);
        assertTrue(handler instanceof HandlerMock);
    }


    public void test_getHandler_unknown() throws Exception {
        builder.addHandler("bobo", HandlerMock.class.getName());

        Handler handler = builder.createHandlerMap(null, new Object[]{}).getHandler("baba");

        assertNull(handler);
    }


    public void test_createHandlerMap_withContextualInstance()
          throws Exception {
        builder.addHandler("bobo", HandlerMockUsingLogString.class.getName());

        Handler handler = builder.createHandlerMap(null, new Object[]{new LogString()}).getHandler("bobo");

        assertTrue(handler instanceof HandlerMockUsingLogString);
    }


    public void test_addSessionComponent() throws Exception {
        builder.addSessionComponent(LogString.class);
        builder.addHandler("bobo", HandlerMockUsingLogString.class.getName());

        Handler handler = builder.createHandlerMap(null, new Object[]{}).getHandler("bobo");

        assertTrue(handler instanceof HandlerMockUsingLogString);
    }


    public void test_removeSessionComponent() throws Exception {
        builder.addSessionComponent(LogString.class);
        builder.removeSessionComponent(LogString.class);
        builder.addHandler("bobo", HandlerMockUsingLogString.class.getName());

        Handler handler = builder.createHandlerMap(null, new Object[]{}).getHandler("bobo");

        assertNull(((HandlerMockUsingLogString)handler).getLog());
    }


    public void test_addHandlerCommand() throws Exception {
        builder.addUserHandler(HandlerCommandMock.class);

        Handler handler = builder.createHandlerMap(null, new Object[]{})
              .getHandler(HandlerCommandMock.MOCK_ID);

        assertTrue(handler instanceof HandlerCommandMock);
    }


    public void test_collectHandlerFrom_empty() throws Exception {
        builder.collectHandlerFrom(LazyHandlerMapBuilder.class, "org.apache");

        HandlerMap handlerMap = builder.createHandlerMap(null, new Object[]{});
        assertNotNull(handlerMap);
        assertEquals(Collections.<String>emptySet(), handlerMap.getHandlerIdSet());
    }


    public void test_collectHandlerFrom() throws Exception {
        builder.collectHandlerFrom(LazyHandlerMapBuilderTest.class, "net.codjo.mad.server.handler");

        HandlerMap first = builder.createHandlerMap(null, new Object[]{});
        HandlerMap second = builder.createHandlerMap(null, new Object[]{});

        assertContent("[dummy]", first);
        assertContent("[dummy]", second);

        assertNotSame(second, first);
    }


    private void assertContent(String expected, HandlerMap actual) {
        Set<String> handlerIdSet = actual.getHandlerIdSet();
        assertEquals(expected, new TreeSet<String>(handlerIdSet).toString());
    }


    @Override
    protected void setUp() throws Exception {
        builder = new LazyHandlerMapBuilder();
    }


    @Override
    protected HandlerMapBuilder createHandlerMapBuilder() {
        return builder;
    }
}
