/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.mad.server.util;
import net.codjo.test.common.LogString;
import net.codjo.test.common.mock.ConnectionMock;
import junit.framework.TestCase;
/**
 * Classe de test de {@link net.codjo.mad.server.util.ConnectionNoClose}.
 */
public class ConnectionNoCloseTest extends TestCase {
    private ConnectionNoClose connectionNoClose;
    private LogString log = new LogString();


    public void test_close() throws Exception {
        connectionNoClose.close();
        log.assertContent("");
    }


    public void test_closeInnerConnection() throws Exception {
        connectionNoClose.closeInnerConnection();
        log.assertContent("subConnection.close()");
    }


    @Override
    protected void setUp() throws Exception {
        connectionNoClose =
              new ConnectionNoClose(new ConnectionMock(new LogString("subConnection", log)).getStub());
    }
}
