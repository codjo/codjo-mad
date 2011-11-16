/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.mad.server.handler;
import java.util.Collections;
import junit.framework.TestCase;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.DuplicateComponentKeyRegistrationException;
import org.w3c.dom.Node;
/**
 * Classe de test de {@link HandlerMap}.
 */
public class DefaultHandlerMapTest extends TestCase {
    private DefaultHandlerMap defaultHandlerMap;


    public void test_constructor() throws Exception {
        Handler handlerMock = new HandlerMock();

        defaultHandlerMap.addHandler(handlerMock);
        assertSame(handlerMock, defaultHandlerMap.getHandler(HandlerMock.ID));
    }


    public void test_constructor_empty() throws Exception {
        assertEquals(Collections.<String>emptySet(), defaultHandlerMap.getHandlerIdSet());
    }


    public void test_constructor_withParentContainer() throws Exception {
        MutablePicoContainer parentContainer = new DefaultPicoContainer();
        Integer componentInstance = 8;
        parentContainer.registerComponentInstance("toRegister", componentInstance);
        defaultHandlerMap = new DefaultHandlerMap(parentContainer);

        MutablePicoContainer handlercontainer = defaultHandlerMap.getPicoContainer();
        assertNotNull(handlercontainer);

        assertSame(componentInstance, handlercontainer.getComponentInstance("toRegister"));
    }


    public void test_addHandler_sameId() throws Exception {
        Handler handlerMock = new HandlerMock();
        defaultHandlerMap.addHandler(handlerMock);

        try {
            defaultHandlerMap.addHandler(new MyBadHandler());
            fail();
        }
        catch (DuplicateComponentKeyRegistrationException ex) {
            assertEquals("Key " + HandlerMock.ID + " duplicated", ex.getMessage());
        }

        assertSame(handlerMock, defaultHandlerMap.getHandler(HandlerMock.ID));
    }


    public void test_getHandlerIdSet() throws Exception {
        defaultHandlerMap.addHandler(new HandlerMock());

        assertEquals(Collections.singleton(HandlerMock.ID), defaultHandlerMap.getHandlerIdSet());
    }


    @Override
    protected void setUp() throws Exception {
        defaultHandlerMap = new DefaultHandlerMap();
    }


    private class MyBadHandler implements Handler {
        public String proceed(Node node) {
            return null;
        }


        public String getId() {
            return HandlerMock.ID;
        }


        public void setContext(HandlerContext handlerContext) {
        }
    }
}
