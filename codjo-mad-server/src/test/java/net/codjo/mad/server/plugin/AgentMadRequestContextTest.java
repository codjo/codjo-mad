/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.mad.server.plugin;
import net.codjo.mad.server.MadConnectionManager;
import net.codjo.mad.server.MadTransaction;
import net.codjo.mad.server.util.ConnectionDecorator;
import net.codjo.security.common.api.UserMock;
import net.codjo.sql.server.ConnectionPoolMock;
import net.codjo.test.common.LogString;
import net.codjo.test.common.mock.DatabaseMock;
import java.sql.Connection;
import junit.framework.TestCase;
import org.exolab.castor.jdo.Database;
import org.exolab.castor.jdo.JDO;
import org.exolab.castor.jdo.PersistenceException;
/**
 * Classe de test de {@link AgentMadRequestContext}.
 */
public class AgentMadRequestContextTest extends TestCase {
    private LogString logString = new LogString();
    private AgentMadRequestContext requestContext;
    private UserMock userMock;


    public void test_getTransaction() throws Exception {
        MadTransaction transaction = requestContext.getTransaction();

        assertNotNull(transaction);
        assertSame(transaction, requestContext.getTransaction());
    }


    public void test_getTransaction_connection() throws Exception {
        MadTransaction transaction = requestContext.getTransaction();

        requestContext.getConnectionManager().getTxConnection();
        assertLog("pool.getConnection()");

        transaction.begin();
        assertLog("pool.getConnection(), connection.setAutoCommit(false)");
    }


    public void test_getTransaction_database() throws Exception {
        MadTransaction transaction = requestContext.getTransaction();

        requestContext.getConnectionManager().getDatabase();
        assertLog("pool.getConnection(), jdo.getDatabase()");

        logString.clear();
        transaction.begin();
        assertLog("connection.setAutoCommit(false), database.begin()");
    }


    public void test_getConnectionManager() throws Exception {
        MadConnectionManager connectionManager = requestContext.getConnectionManager();

        assertNotNull(connectionManager);
        assertSame(connectionManager, requestContext.getConnectionManager());
    }


    public void test_getConnectionManager_connection()
          throws Exception {
        MadConnectionManager connectionManager = requestContext.getConnectionManager();

        assertNotNull(connectionManager.getConnection());
        assertLog("pool.getConnection()");

        logString.clear();

        assertNotNull(connectionManager.getTxConnection());
        assertLog("pool.getConnection()");
    }


    public void test_getConnectionManager_database()
          throws Exception {
        MadConnectionManager connectionManager = requestContext.getConnectionManager();

        assertNotNull(connectionManager.getDatabase());
        assertLog("pool.getConnection(), jdo.getDatabase()");
    }


    public void test_close() throws Exception {
        assertNotNull(requestContext.getTransaction());
        assertNotNull(requestContext.getConnectionManager());
        assertNotNull(requestContext.getUserProfil());
        requestContext.close();

        try {
            requestContext.getTransaction();
            fail();
        }
        catch (AgentMadRequestContext.ContextClosedException ex) {
            ;
        }
        try {
            requestContext.getConnectionManager();
            fail();
        }
        catch (AgentMadRequestContext.ContextClosedException ex) {
            ;
        }
        try {
            requestContext.getUserProfil();
            fail();
        }
        catch (AgentMadRequestContext.ContextClosedException ex) {
            ;
        }
    }


    public void test_close_ressources() throws Exception {
        Connection connection = requestContext.getConnectionManager().getConnection();
        Connection txConnection = requestContext.getConnectionManager().getTxConnection();
        requestContext.getConnectionManager().getDatabase();
        logString.clear();

        requestContext.close();

        String callList = "database.close()";
        callList += ", pool.releaseConnection(" + realConnection(connection) + ")";
        callList += ", pool.releaseConnection(" + realConnection(txConnection) + ")";
        assertLog(callList);
    }


    public void test_getUser() throws Exception {
        assertSame(userMock, requestContext.getUserProfil());
    }


    @Override
    protected void setUp() throws Exception {
        ConnectionPoolMock connectionPoolMock =
              new ConnectionPoolMock(new LogString("pool", logString));
        JDOMock jdoMock = new JDOMock(new LogString("jdo", logString));

        userMock = new UserMock();
        requestContext =
              new AgentMadRequestContext(connectionPoolMock, jdoMock, userMock);
    }


    private void assertLog(String callList) {
        logString.assertContent(callList);
    }


    private Connection realConnection(Connection connection) {
        return ((ConnectionDecorator)connection).getConnection();
    }


    private static class JDOMock extends JDO {
        private final LogString logString;


        JDOMock(LogString logString) {
            this.logString = logString;
        }


        @Override
        public Database getDatabase()
              throws PersistenceException {
            logString.call("getDatabase");
            return new DatabaseMock(new LogString("database", logString));
        }
    }
}
