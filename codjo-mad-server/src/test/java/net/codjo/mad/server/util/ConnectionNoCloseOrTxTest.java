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
 * Classe de test de {@link ConnectionNoCloseOrTx}.
 */
public class ConnectionNoCloseOrTxTest extends TestCase {
    private ConnectionNoCloseOrTx decorator;
    private LogString log;


    public void test_close() throws Exception {
        decorator.close();
        log.assertContent("");
    }


    public void test_txCalls() throws Exception {
        decorator.setAutoCommit(true);
        decorator.commit();
        decorator.rollback();
        decorator.rollback(null);

        log.assertContent("");
    }


    @Override
    protected void setUp() throws Exception {
        log = new LogString();
        decorator =
              new ConnectionNoCloseOrTx(new ConnectionMock(new LogString("connection", log)));
    }
}
