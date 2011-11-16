/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.mad.server.util;
import net.codjo.test.common.LogCallAssert;
import net.codjo.test.common.LogString;
import net.codjo.test.common.mock.ConnectionMock;
import java.sql.Connection;
import java.sql.Savepoint;
import junit.framework.TestCase;
/**
 * Classe de test de {@link ConnectionDecorator}.
 */
public class ConnectionDecoratorTest extends TestCase {
    private ConnectionDecorator decorator;
    private Connection connection;
    private LogString log;


    public void test_getConnection() throws Exception {
        assertSame(connection, decorator.getConnection());
    }


    public void test_delegate() throws Exception {
        LogCallAssert logCallAssert = new LogCallAssert(Connection.class);
        logCallAssert.assertCalls(decorator, log);
    }


    @Override
    protected void setUp() throws Exception {
        log = new LogString();
        connection = new ConnectionMockJDK14(log);
        decorator = new ConnectionDecorator(connection);
    }


    private static class ConnectionMockJDK14 extends ConnectionMock {
        private final LogString log;


        ConnectionMockJDK14(LogString connectionLog) {
            super(connectionLog);
            this.log = connectionLog;
        }


        @Override
        public Savepoint setSavepoint() {
            log.call("setSavepoint");
            return null;
        }


        @Override
        public void releaseSavepoint(Savepoint savepoint) {
            log.call("releaseSavepoint", savepoint);
        }


        @Override
        public void rollback(Savepoint savepoint) {
            log.call("rollback", savepoint);
        }


        @Override
        public Savepoint setSavepoint(String name) {
            log.call("setSavepoint", name);
            return null;
        }
    }
}
