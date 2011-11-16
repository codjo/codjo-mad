/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.mad.server.handler;
import net.codjo.mad.server.MadRequestContext;
import net.codjo.mad.server.MadRequestContextMock;
import net.codjo.mad.server.MadTransactionMock;
import net.codjo.security.server.api.UserFactoryMock;
import net.codjo.test.common.LogString;
import junit.framework.TestCase;
/**
 * Classe de test de {@link HandlerContext}.
 */
public class HandlerContextTest extends TestCase {
    private UserFactoryMock userFactoryMock;
    private LogString log;


    public void test_madRequestContext() throws Exception {
        MadRequestContext madRequestContext =
              new MadRequestContextMock(new MadTransactionMock(new LogString("tx", log)),
                                        SecurityContextMock.userIsInAllRole(),
                                        userFactoryMock);

        assertTrue(MadRequestContext.class.isAssignableFrom(HandlerContext.class));

        HandlerContext handlerContext = new HandlerContext(madRequestContext);

        assertSame(madRequestContext.getTransaction(), handlerContext.getTransaction());
        assertSame(madRequestContext.getConnectionManager(),
                   handlerContext.getConnectionManager());
        assertSame(madRequestContext.getUserProfil(), handlerContext.getUserProfil());

        assertSame(madRequestContext.getConnectionManager().getConnection(),
                   handlerContext.getConnection());
        assertSame(madRequestContext.getConnectionManager().getTxConnection(),
                   handlerContext.getTxConnection());

        assertNull(handlerContext.getUser());
    }


    @Override
    protected void setUp() throws Exception {
        log = new LogString();
        userFactoryMock = new UserFactoryMock();
    }
}
