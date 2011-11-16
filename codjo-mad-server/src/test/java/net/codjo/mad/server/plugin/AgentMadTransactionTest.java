package net.codjo.mad.server.plugin;
import net.codjo.mad.server.MadTransactionFailureException;
import net.codjo.mad.server.plugin.AgentMadConnectionManagerTest.ConnectionInformationsMock;
import net.codjo.mad.server.util.ConnectionDecorator;
import net.codjo.test.common.LogString;
import net.codjo.test.common.mock.ConnectionMock;
import net.codjo.test.common.mock.DatabaseMock;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import junit.framework.TestCase;
import org.exolab.castor.jdo.TransactionAbortedException;
import org.exolab.castor.jdo.TransactionNotInProgressException;

public class AgentMadTransactionTest extends TestCase {
    private static final String FAILURE_MESSAGE = "DummyDatabaseMock_FailureMessage";
    private static final String JDBC_ROLLBACK =
          "txConnection.rollback(), txConnection.setAutoCommit(true)";
    private AgentMadTransaction transaction;
    private LogString log = new LogString();
    private AgentMadConnectionManager connectionManager;
    private AgentMadConnectionManagerTest.ConnectionBuilderMock builderMock;


    public void test_getConnectionManager() throws Exception {
        assertSame(connectionManager, transaction.getConnectionManager());
    }


    public void test_close() throws Exception {
        Connection connection = connectionManager.getConnection();
        log.clear();

        transaction.close();
        log.assertContent("builder.releaseConnection(" + realConnection(connection) + ")");
    }


    public void test_close_noConnection() throws Exception {
        transaction.close();
        log.assertContent("");
    }


    public void test_close_inTransaction() throws Exception {
        Connection txConnection = connectionManager.getTxConnection();
        transaction.begin();
        log.clear();

        transaction.close();

        log.assertContent(JDBC_ROLLBACK + ", builder.releaseTxConnection("
                          + realConnection(txConnection) + ")");
    }


    public void test_begin_inTransaction() throws Exception {
        transaction.begin();
        try {
            transaction.begin();
            fail();
        }
        catch (MadTransactionFailureException ex) {
            assertEquals("Transaction en cours !", ex.getMessage());
        }
    }


    public void test_begin_afterGetTxConnection()
          throws Exception {
        connectionManager.getTxConnection();
        log.assertContent("builder.createTxConnection()");
        log.clear();

        transaction.begin();

        log.assertContent("txConnection.setAutoCommit(false)");
    }


    public void test_begin_beforeGetTxConnection()
          throws Exception {
        transaction.begin();
        log.assertContent("");

        connectionManager.getTxConnection();
        connectionManager.getTxConnection();

        log.assertContent(
              "builder.createTxConnection(), txConnection.setAutoCommit(false)");
    }


    public void test_begin_afterGetDatabase() throws Exception {
        connectionManager.getDatabase();
        log.assertContent("builder.createTxConnection(), builder.createDatabase()");

        log.clear();
        transaction.begin();

        log.assertContent("txConnection.setAutoCommit(false), database.begin()");
    }


    public void test_begin_beforeGetDatabase() throws Exception {
        transaction.begin();
        log.assertContent("");

        connectionManager.getDatabase();
        log.assertContent(
              "builder.createTxConnection(), txConnection.setAutoCommit(false)"
              + ", builder.createDatabase(), database.begin()");

        log.clear();
        connectionManager.getDatabase();
        log.assertContent("");
    }


    public void test_commit_notInTransaction() throws Exception {
        try {
            transaction.commit();
            fail();
        }
        catch (MadTransactionFailureException ex) {
            assertEquals("Aucune transaction en cours !", ex.getMessage());
        }
    }


    public void test_commit_beforeTxConnection() throws Exception {
        transaction.begin();
        transaction.commit();

        log.clear();
        connectionManager.getTxConnection();

        log.assertContent("builder.createTxConnection()");
    }


    public void test_commit_txConnection() throws Exception {
        connectionManager.getTxConnection();
        transaction.begin();

        log.clear();
        transaction.commit();
        log.assertContent("txConnection.commit(), txConnection.setAutoCommit(false)");
    }


    public void test_commit_database() throws Exception {
        connectionManager.getDatabase();
        transaction.begin();
        log.clear();

        transaction.commit();
        log.assertContent("database.commit()"
                          + ", txConnection.commit(), txConnection.setAutoCommit(false)");
    }


    public void test_rollback_notInTransaction() throws Exception {
        try {
            transaction.rollback();
            fail();
        }
        catch (MadTransactionFailureException ex) {
            assertEquals("Aucune transaction en cours !", ex.getMessage());
        }
    }


    public void test_rollback_beforeGetTxConnection()
          throws Exception {
        transaction.begin();
        transaction.rollback();

        log.clear();
        connectionManager.getTxConnection();

        log.assertContent("builder.createTxConnection()");
    }


    public void test_rollback_txConnection() throws Exception {
        connectionManager.getTxConnection();
        transaction.begin();
        log.clear();

        transaction.rollback();
        log.assertContent(JDBC_ROLLBACK);
    }


    public void test_rollback_database() throws Exception {
        connectionManager.getDatabase();
        transaction.begin();
        log.clear();

        transaction.rollback();
        log.assertContent(
              "database.rollback(), txConnection.rollback(), txConnection.setAutoCommit(true)");
    }


    public void test_flush_notInTransaction() throws Exception {
        try {
            transaction.flush();
            fail();
        }
        catch (MadTransactionFailureException ex) {
            assertEquals("Aucune transaction en cours !", ex.getMessage());
        }
    }


    public void test_flush_noDatabase() throws Exception {
        transaction.begin();
        log.clear();
        transaction.flush();
        log.assertContent("");
    }


    public void test_flush_database() throws Exception {
        connectionManager.getDatabase();
        transaction.begin();
        log.clear();

        transaction.flush();
        log.assertContent("database.flush()");
    }


    public void test_error_beginAfterGetRessources()
          throws Exception {
        builderMock.mockGetDatabase(new DatabaseMock() {
            @Override
            public void begin() throws TransactionAbortedException {
                throw new TransactionAbortedException(FAILURE_MESSAGE);
            }
        });
        connectionManager.getDatabase();
        log.clear();

        try {
            transaction.begin();
            fail();
        }
        catch (MadTransactionFailureException ex) {
            assertEquals(FAILURE_MESSAGE, ex.getCause().getMessage());
        }

        log.assertContent("txConnection.setAutoCommit(false), " + JDBC_ROLLBACK);
        assertFalse(transaction.isInTransaction());
    }


    public void test_error_beginBeforeGetRessources()
          throws Exception {
        // Transaction commence
        transaction.begin();

        // Get en echec ...
        builderMock.mockGetTxConnection(new DummyConnectionMock(
              new LogString("dummy", log)));
        try {
            connectionManager.getTxConnection();
            fail();
        }
        catch (SQLException ex) {
            assertEquals(FAILURE_MESSAGE, ex.getMessage());
        }
        log.assertContent("builder.createTxConnection(), dummy.setAutoCommit(false)");

        // ... mais transaction toujours active
        assertTrue(transaction.isInTransaction());

        // La transaction peux continuer
        ConnectionMock connection =
              new ConnectionMock(new LogString("txConnection", log));
        builderMock.mockGetTxConnection(connection);

        connectionManager.getTxConnection();
    }


    public void test_error_commit() throws Exception {
        DatabaseMock databaseWithCommitFailure =
              new DatabaseMock() {
                  @Override
                  public void commit() throws TransactionAbortedException {
                      throw new TransactionAbortedException(FAILURE_MESSAGE);
                  }
              };
        builderMock.mockGetDatabase(databaseWithCommitFailure);

        connectionManager.getDatabase();
        transaction.begin();
        log.clear();

        try {
            transaction.commit();
            fail();
        }
        catch (MadTransactionFailureException ex) {
            assertEquals(FAILURE_MESSAGE, ex.getCause().getMessage());
        }

        log.assertContent(JDBC_ROLLBACK);
        assertFalse(transaction.isInTransaction());
    }


    public void test_error_rollback() throws Exception {
        DatabaseMock databaseWithFailure =
              new DatabaseMock() {
                  @Override
                  public void rollback() throws TransactionNotInProgressException {
                      throw new TransactionNotInProgressException(FAILURE_MESSAGE);
                  }
              };
        builderMock.mockGetDatabase(databaseWithFailure);

        connectionManager.getDatabase();
        transaction.begin();
        log.clear();

        try {
            transaction.rollback();
            fail();
        }
        catch (MadTransactionFailureException ex) {
            assertEquals(FAILURE_MESSAGE, ex.getCause().getMessage());
        }

        log.assertContent(JDBC_ROLLBACK);
        assertFalse(transaction.isInTransaction());
    }


    @Override
    protected void setUp() throws Exception {
        builderMock =
              new AgentMadConnectionManagerTest.ConnectionBuilderMock(new LogString(
                    "builder",
                    log));
        connectionManager = new AgentMadConnectionManager(builderMock, new ConnectionInformationsMock());
        transaction = new AgentMadTransaction(connectionManager);
    }


    private Connection realConnection(Connection connection) {
        return ((ConnectionDecorator)connection).getConnection();
    }


    private static class DummyConnectionMock extends ConnectionMock {
        DummyConnectionMock(LogString logString) {
            super(logString);
        }


        @Override
        public void setAutoCommit(boolean autoCommit)
              throws SQLException {
            super.setAutoCommit(autoCommit);
            if (!autoCommit) {
                throw new SQLException(FAILURE_MESSAGE);
            }
        }


        @Override
        public Savepoint setSavepoint() throws SQLException {
            return null;
        }


        @Override
        public void releaseSavepoint(Savepoint savepoint)
              throws SQLException {
        }


        @Override
        public void rollback(Savepoint savepoint)
              throws SQLException {
        }


        @Override
        public Savepoint setSavepoint(String name)
              throws SQLException {
            return null;
        }
    }
}
