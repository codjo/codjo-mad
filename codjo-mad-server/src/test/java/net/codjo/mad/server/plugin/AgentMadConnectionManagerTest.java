package net.codjo.mad.server.plugin;
import net.codjo.mad.server.plugin.AgentMadConnectionManager.ConnectionInformations;
import net.codjo.mad.server.util.ConnectionDecorator;
import net.codjo.mad.server.util.DatabaseDecorator;
import net.codjo.test.common.LogString;
import net.codjo.test.common.mock.ConnectionMock;
import net.codjo.test.common.mock.DatabaseMock;
import java.sql.Connection;
import java.sql.SQLException;
import junit.framework.TestCase;
import org.exolab.castor.jdo.Database;
import org.exolab.castor.jdo.PersistenceException;
import org.exolab.castor.jdo.engine.DatabaseRegistry;

public class AgentMadConnectionManagerTest extends TestCase {
    private AgentMadConnectionManager connectionManager;
    private LogString logString = new LogString();


    public void test_getConnection() throws Exception {
        Connection connection = connectionManager.getConnection();
        logString.assertContent("builder.createConnection()");
        assertNotNull(connection);

        connectionManager.getConnection();
        logString.assertContent("builder.createConnection()");

        assertSame(connection, connectionManager.getConnection());
    }


    public void test_getConnection_noClose() throws Exception {
        Connection connection = connectionManager.getConnection();
        logString.clear();

        connection.close();
        logString.assertContent("");
    }


    public void test_getTxConnection() throws Exception {
        Connection connection = connectionManager.getTxConnection();
        logString.assertContent("builder.createTxConnection()");
        assertNotNull(connection);

        connectionManager.getTxConnection();
        logString.assertContent("builder.createTxConnection()");

        assertSame(connection, connectionManager.getTxConnection());
    }


    public void test_getTxConnection_noClose() throws Exception {
        Connection connection = connectionManager.getTxConnection();
        logString.clear();

        connection.close();
        logString.assertContent("");
    }


    public void test_getDatabase() throws Exception {
        String callListForGetDatabase =
              "builder.createTxConnection(), builder.createDatabase()";

        Database database = connectionManager.getDatabase();
        logString.assertContent(callListForGetDatabase);
        assertNotNull(database);
        assertNotNull(DatabaseRegistry.getForcedConnection());

        DatabaseRegistry.getForcedConnection().close();
        logString.assertContent(callListForGetDatabase);

        connectionManager.getDatabase();
        logString.assertContent(callListForGetDatabase);

        assertSame(database, connectionManager.getDatabase());
    }


    public void test_getDatabase_noClose() throws Exception {
        Database database = connectionManager.getDatabase();
        logString.clear();

        database.close();
        logString.assertContent("");
    }


    public void test_close_connection() throws Exception {
        Connection connection = connectionManager.getConnection();
        logString.clear();

        connectionManager.close();
        assertLog("builder.releaseConnection(" + realConnection(connection) + ")");
    }


    public void test_close_txConnection() throws Exception {
        Connection connection = connectionManager.getTxConnection();
        logString.clear();

        connectionManager.close();
        assertLog("builder.releaseTxConnection(" + realConnection(connection) + ")");
    }


    public void test_close_database() throws Exception {
        Database database = connectionManager.getDatabase();
        Connection connection = connectionManager.getTxConnection();
        logString.clear();

        connectionManager.close();
        assertLog("builder.releaseDatabase("
                  + ((DatabaseDecorator)database).getDatabase()
                  + "), builder.releaseTxConnection(" + realConnection(connection) + ")");
        assertNull(DatabaseRegistry.getForcedConnection());
    }


    @Override
    protected void setUp() throws Exception {
        connectionManager =
              new AgentMadConnectionManager(
                    new ConnectionBuilderMock(new LogString("builder", logString)),
                    new ConnectionInformationsMock());
    }


    private void assertLog(String callList) {
        logString.assertContent(callList);
    }


    private Connection realConnection(Connection connection) {
        return ((ConnectionDecorator)connection).getConnection();
    }


    public static class ConnectionBuilderMock
          implements AgentMadConnectionManager.ConnectionBuilder {
        private final LogString logString;
        private Database databaseMock;
        private Connection txConnectionMock;


        ConnectionBuilderMock(LogString logString) {
            this.logString = logString;
        }


        public Connection createConnection() throws SQLException {
            logString.call("createConnection");
            return new ConnectionMock(new LogString("connection", logString));
        }


        public void releaseConnection(Connection connection) {
            logString.call("releaseConnection", connection);
        }


        public Connection createTxConnection() throws SQLException {
            logString.call("createTxConnection");
            if (txConnectionMock != null) {
                return txConnectionMock;
            }
            return new ConnectionMock(new LogString("txConnection", logString));
        }


        public void releaseTxConnection(Connection connection) {
            logString.call("releaseTxConnection", connection);
        }


        public Database createDatabase() throws PersistenceException {
            logString.call("createDatabase");
            if (databaseMock != null) {
                return databaseMock;
            }
            return new DatabaseMock(new LogString("database", logString));
        }


        public void releaseDatabase(Database database) {
            logString.call("releaseDatabase", database);
        }


        public void mockGetDatabase(Database database) {
            databaseMock = database;
        }


        public void mockGetTxConnection(Connection connection) {
            txConnectionMock = connection;
        }
    }

    public static class ConnectionInformationsMock implements ConnectionInformations {
        public int countUsedConnections() {
            return 0;
        }


        public int countUnusedConnections() {
            return 0;
        }
    }
}
