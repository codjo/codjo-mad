/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.mad.server.plugin;
import net.codjo.mad.server.MadConnectionManager;
import net.codjo.mad.server.MadTransaction;
import net.codjo.mad.server.MadTransactionFailureException;
import java.sql.Connection;
import java.sql.SQLException;
import org.apache.log4j.Logger;
import org.exolab.castor.jdo.Database;
import org.exolab.castor.jdo.PersistenceException;
/**
 * Transaction appliquè sur {@link AgentMadConnectionManager}.
 */
class AgentMadTransaction implements MadTransaction {
    private static final Logger LOG =
          Logger.getLogger(AgentMadTransaction.class.getName());
    private final AgentMadConnectionManager connectionManager;
    private boolean inTransaction = false;


    AgentMadTransaction(AgentMadConnectionManager connectionManager) {
        this.connectionManager = connectionManager;

        TransactionalConnectionBuilder builder =
              new TransactionalConnectionBuilder(connectionManager.getConnectionBuilder());
        connectionManager.setConnectionBuilder(builder);
    }


    public void begin() throws MadTransactionFailureException {
        assertNotInTransaction();

        beginJdbcTransaction();
        try {
            beginCastorTransaction();
        }
        catch (MadTransactionFailureException error) {
            silentRollbackJdbcTransaction();
            throw error;
        }
        inTransaction = true;
    }


    public void commit() throws MadTransactionFailureException {
        assertInTransaction();
        inTransaction = false;

        try {
            commitCastorTransaction();
        }
        catch (MadTransactionFailureException error) {
            silentRollbackJdbcTransaction();
            throw error;
        }

        commitJdbcTransaction();
    }


    public void flush() throws MadTransactionFailureException {
        assertInTransaction();
        if (!connectionManager.hasDatabase()) {
            return;
        }
        try {
            connectionManager.getDatabase().flush();
        }
        catch (PersistenceException e) {
            throw new MadTransactionFailureException("Impossible de flusher une transaction CASTOR !",
                                                     e);
        }
    }


    public void rollback() throws MadTransactionFailureException {
        assertInTransaction();
        inTransaction = false;
        try {
            rollbackCastorTransaction();
        }
        finally {
            rollbackJdbcTransaction();
        }
    }


    public void close() {
        if (inTransaction) {
            silentRollbackJdbcTransaction();
        }
        connectionManager.close();
    }


    public boolean isInTransaction() {
        return inTransaction;
    }


    public MadConnectionManager getConnectionManager() {
        return connectionManager;
    }


    private void beginJdbcTransaction() throws MadTransactionFailureException {
        if (!connectionManager.hasTxConnection()) {
            return;
        }
        try {
            connectionManager.getTxConnection().setAutoCommit(false);
        }
        catch (SQLException e) {
            throw new MadTransactionFailureException("Impossible de démarrer une transaction JDBC !",
                                                     e);
        }
    }


    private void beginCastorTransaction() throws MadTransactionFailureException {
        if (!connectionManager.hasDatabase()) {
            return;
        }
        try {
            connectionManager.getDatabase().begin();
        }
        catch (PersistenceException e) {
            throw new MadTransactionFailureException("Impossible de démarrer une transaction CASTOR !",
                                                     e);
        }
    }


    private void commitJdbcTransaction() throws MadTransactionFailureException {
        if (!connectionManager.hasTxConnection()) {
            return;
        }
        try {
            connectionManager.getTxConnection().commit();
            connectionManager.getTxConnection().setAutoCommit(false);
        }
        catch (SQLException e) {
            throw new MadTransactionFailureException("Impossible de commiter une transaction JDBC !",
                                                     e);
        }
    }


    private void commitCastorTransaction() throws MadTransactionFailureException {
        if (!connectionManager.hasDatabase()) {
            return;
        }
        try {
            connectionManager.getDatabase().commit();
        }
        catch (PersistenceException e) {
            throw new MadTransactionFailureException("Impossible de commiter une transaction CASTOR !",
                                                     e);
        }
    }


    private void rollbackJdbcTransaction() throws MadTransactionFailureException {
        if (!connectionManager.hasTxConnection()) {
            return;
        }
        try {
            connectionManager.getTxConnection().rollback();
            connectionManager.getTxConnection().setAutoCommit(true);
        }
        catch (SQLException e) {
            throw new MadTransactionFailureException("Impossible de rollback une transaction JDBC !",
                                                     e);
        }
    }


    private void silentRollbackJdbcTransaction() {
        try {
            rollbackJdbcTransaction();
        }
        catch (MadTransactionFailureException e) {
            LOG.error("Erreur durant un rollback d'urgence", e);
        }
    }


    private void rollbackCastorTransaction() throws MadTransactionFailureException {
        if (!connectionManager.hasDatabase()) {
            return;
        }
        try {
            connectionManager.getDatabase().rollback();
        }
        catch (PersistenceException e) {
            throw new MadTransactionFailureException("Impossible de rollback une transaction CASTOR !",
                                                     e);
        }
    }


    private void assertNotInTransaction() throws MadTransactionFailureException {
        if (inTransaction) {
            throw new MadTransactionFailureException("Transaction en cours !", null);
        }
    }


    private void assertInTransaction() throws MadTransactionFailureException {
        if (!inTransaction) {
            throw new MadTransactionFailureException("Aucune transaction en cours !", null);
        }
    }


    private class TransactionalConnectionBuilder
          implements AgentMadConnectionManager.ConnectionBuilder {
        private AgentMadConnectionManager.ConnectionBuilder builder;


        TransactionalConnectionBuilder(
              AgentMadConnectionManager.ConnectionBuilder connectionBuilder) {
            builder = connectionBuilder;
        }


        public Connection createConnection() throws SQLException {
            return builder.createConnection();
        }


        public void releaseConnection(Connection connection) {
            builder.releaseConnection(connection);
        }


        public Connection createTxConnection() throws SQLException {
            Connection txConnection = builder.createTxConnection();
            if (inTransaction) {
                txConnection.setAutoCommit(false);
            }
            return txConnection;
        }


        public void releaseTxConnection(Connection connection) {
            builder.releaseTxConnection(connection);
        }


        public Database createDatabase() throws PersistenceException {
            Database database = builder.createDatabase();
            if (inTransaction) {
                database.begin();
            }
            return database;
        }


        public void releaseDatabase(Database database) {
            builder.releaseDatabase(database);
        }
    }
}
