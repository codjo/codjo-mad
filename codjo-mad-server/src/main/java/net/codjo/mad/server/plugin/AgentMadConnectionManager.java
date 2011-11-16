/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.mad.server.plugin;
import net.codjo.mad.server.MadConnectionManager;
import net.codjo.mad.server.util.ConnectionNoClose;
import net.codjo.mad.server.util.ConnectionNoCloseOrTx;
import net.codjo.mad.server.util.DatabaseNoClose;
import java.sql.Connection;
import java.sql.SQLException;
import org.exolab.castor.jdo.Database;
import org.exolab.castor.jdo.PersistenceException;
import org.exolab.castor.jdo.engine.DatabaseRegistry;
/**
 * ConnectionManager pour la couche Agent. Cette classe est utilisé dans lc
 */
class AgentMadConnectionManager implements MadConnectionManager {
    private ConnectionBuilder connectionBuilder;
    private ConnectionInformations informations;
    private ConnectionNoClose connection = null;
    private ConnectionNoClose txConnection = null;
    private DatabaseNoClose database = null;


    AgentMadConnectionManager(ConnectionBuilder builder, ConnectionInformations informations) {
        this.connectionBuilder = builder;
        this.informations = informations;
    }


    public Connection getConnection() throws SQLException {
        if (connection == null) {
            connection = new ConnectionNoClose(connectionBuilder.createConnection());
        }

        return connection;
    }


    public Connection getTxConnection() throws SQLException {
        if (txConnection == null) {
            txConnection = new ConnectionNoClose(connectionBuilder.createTxConnection());
        }

        return txConnection;
    }


    public Database getDatabase() throws PersistenceException {
        if (database == null) {
            forceCastorConnection();
            database = new DatabaseNoClose(connectionBuilder.createDatabase());
        }
        return database;
    }


    public int countUsedConnections() {
        return informations.countUsedConnections();
    }


    public int countUnusedConnections() {
        return informations.countUnusedConnections();
    }


    public boolean hasTxConnection() {
        return txConnection != null;
    }


    public boolean hasDatabase() {
        return database != null;
    }


    public ConnectionBuilder getConnectionBuilder() {
        return connectionBuilder;
    }


    public void setConnectionBuilder(ConnectionBuilder connectionBuilder) {
        this.connectionBuilder = connectionBuilder;
    }


    public void close() {
        if (hasDatabase()) {
            connectionBuilder.releaseDatabase(database.getDatabase());
        }
        if (connection != null) {
            connectionBuilder.releaseConnection(connection.getConnection());
        }
        if (txConnection != null) {
            connectionBuilder.releaseTxConnection(txConnection.getConnection());
        }
        DatabaseRegistry.setForcedConnection(null);
        connection = null;
        txConnection = null;
        database = null;
    }


    private void forceCastorConnection() throws PersistenceException {
        try {
            DatabaseRegistry.setForcedConnection(new ConnectionNoCloseOrTx(
                  getTxConnection()));
        }
        catch (SQLException e) {
            throw new PersistenceException("Impossible de récupérer une connexion transactionnelle",
                                           e);
        }
    }


    /**
     * Interface décrivant un builder de connection. Ce builder est responsable de l'allocation ainsi que de
     * la libération des ressources.
     */
    public static interface ConnectionBuilder {
        Connection createConnection() throws SQLException;


        void releaseConnection(Connection connection);


        Connection createTxConnection() throws SQLException;


        void releaseTxConnection(Connection connection);


        Database createDatabase() throws PersistenceException;


        void releaseDatabase(Database database);
    }

    public static interface ConnectionInformations {
        int countUsedConnections();


        int countUnusedConnections();
    }
}
