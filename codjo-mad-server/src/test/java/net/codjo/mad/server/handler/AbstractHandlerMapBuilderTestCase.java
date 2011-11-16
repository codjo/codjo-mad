/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.mad.server.handler;
import static net.codjo.mad.server.handler.AbstractHandlerMapBuilder.getIdFromHandlerName;
import static net.codjo.test.common.matcher.JUnitMatchers.*;
import junit.framework.TestCase;
import static org.junit.Assert.assertThat;
/**
 *
 */
public abstract class AbstractHandlerMapBuilderTestCase extends TestCase {

    public void test_getHandler_noUsableConstructor() throws Exception {
        assertGettingHandlerFailure(UnbuildableHandler.class,
                                    "has unsatisfied dependency");
    }


    public void test_getHandler_noPublicConstructor() throws Exception {
        assertGettingHandlerFailure(WithPrivateConstructorHandler.class,
                                    "the constructors were not accessible");
    }


    public void test_getHandler_constructorWithRuntimeException() throws Exception {
        assertGettingHandlerFailure(ConstructorWithRuntimeExceptionHandler.class,
                                    ConstructorWithRuntimeExceptionHandler.FAILING_MESSAGE);
    }


    public void test_getHandler_constructorWithException() throws Exception {
        assertGettingHandlerFailure(ConstructorWithExceptionHandler.class,
                                    ConstructorWithExceptionHandler.FAILING_MESSAGE);
    }


    public void test_getIdFromHandlerName() {
        String result = getIdFromHandlerName("a.b.c.tototo.tititi.tututu.mimiHandler");
        assertEquals("mimi", result);
    }


    public void test_getIdFromHandlerName_doNotEndWithHandler() {
        String result = getIdFromHandlerName("net.codjo.Mimi");
        assertEquals("mimi", result);
    }


    public void test_getIdFromHandlerName_forHandlerCommand() {
        String result = getIdFromHandlerName("net.codjo.MimiCommand");
        assertEquals("mimi", result);
    }


    private void assertGettingHandlerFailure(Class<? extends Handler> clazz, String failureMessage) {
        HandlerMapBuilder builder = createHandlerMapBuilder();
        builder.addUserHandler(clazz);

        try {
            builder.createHandlerMap(null, new Object[]{}).getHandler(handlerIdOf(clazz));
            fail();
        }
        catch (Exception ex) {
            assertThat(ex.getMessage(), containsString(failureMessage));
        }
    }


    protected abstract HandlerMapBuilder createHandlerMapBuilder();


    protected String handlerIdOf(Class<? extends Handler> clazz) {
        return getIdFromHandlerName(clazz.getName());
    }


    public static class UnbuildableHandler extends HandlerMock {
        @SuppressWarnings({"UnusedDeclaration"})
        public UnbuildableHandler(UnbuildableHandler self) {

        }
    }
    public static class WithPrivateConstructorHandler extends HandlerMock {
        private WithPrivateConstructorHandler() {
        }
    }
    public static class ConstructorWithRuntimeExceptionHandler extends HandlerMock {
        private static final String FAILING_MESSAGE = "failing message";


        public ConstructorWithRuntimeExceptionHandler() {
            throw new IllegalStateException(FAILING_MESSAGE);
        }
    }
    public static class ConstructorWithExceptionHandler extends HandlerMock {
        private static final String FAILING_MESSAGE = "failing message";


        public ConstructorWithExceptionHandler() throws Exception {
            throw new Exception(FAILING_MESSAGE);
        }
    }
}
