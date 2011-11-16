/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.mad.server.plugin;
import net.codjo.mad.server.MadConnectionManager;
import net.codjo.mad.server.MadRequestContext;
import net.codjo.mad.server.MadTransaction;
import net.codjo.mad.server.plugin.AgentMadConnectionManager.ConnectionBuilder;
import net.codjo.mad.server.plugin.AgentMadConnectionManager.ConnectionInformations;
import net.codjo.security.common.api.User;
import net.codjo.sql.server.ConnectionPool;
import java.sql.Connection;
import java.sql.SQLException;
import org.apache.log4j.Logger;
import org.exolab.castor.jdo.Database;
import org.exolab.castor.jdo.JDO;
import org.exolab.castor.jdo.PersistenceException;
/**
 * Contexte d'une requete dans le monde Agent.
 */
public class AgentMadRequestContext implements MadRequestContext {
    private static final Logger LOG =
          Logger.getLogger(AgentMadRequestContext.class.getName());
    private User user;
    private AgentMadTransaction agentMadTransaction;


    public AgentMadRequestContext(ConnectionPool pool, JDO jdo, User user) {
        this.user = user;
        agentMadTransaction =
              new AgentMadTransaction(new AgentMadConnectionManager(
                    new DefaultConnectionBuilder(pool, jdo),
                    new DefaultConnectionInformations(pool)));
    }


    public MadTransaction getTransaction() {
        assertContextNotClosed();
        return agentMadTransaction;
    }


    public MadConnectionManager getConnectionManager() {
        assertContextNotClosed();
        return agentMadTransaction.getConnectionManager();
    }


    public User getUserProfil() {
        assertContextNotClosed();
        return user;
    }


    public void close() {
        agentMadTransaction.close();
        agentMadTransaction = null;
        user = null;
    }


    private void assertContextNotClosed() {
        if (agentMadTransaction == null) {
            throw new ContextClosedException();
        }
    }


    /**
     * Exception lancé lorsque l'on tente d'utiliser un {@link AgentMadRequestContext} fermé.
     *
     * @see AgentMadRequestContext#close()
     */
    public static class ContextClosedException extends RuntimeException {
    }

    private static class DefaultConnectionBuilder implements ConnectionBuilder {
        private final ConnectionPool pool;
        private final JDO jdo;


        DefaultConnectionBuilder(ConnectionPool pool, JDO jdo) {
            this.pool = pool;
            this.jdo = jdo;
        }


        public Connection createConnection() throws SQLException {
            return pool.getConnection();
        }


        public void releaseConnection(Connection connection) {
            if (connection == null) {
                return;
            }
            try {
                pool.releaseConnection(connection);
            }
            catch (SQLException error) {
                LOG.error("Impossible de 'release' une connection JDBC !", error);
            }
        }


        public Connection createTxConnection() throws SQLException {
            return pool.getConnection();
        }


        public void releaseTxConnection(Connection connection) {
            releaseConnection(connection);
        }


        public Database createDatabase() throws PersistenceException {
            return jdo.getDatabase();
        }


        public void releaseDatabase(Database database) {
            if (database == null) {
                return;
            }
            try {
                database.close();
            }
            catch (PersistenceException error) {
                LOG.error("Impossible de fermer CASTOR !", error);
            }
        }
    }

    private static class DefaultConnectionInformations implements ConnectionInformations {
        private ConnectionPool pool;


        private DefaultConnectionInformations(ConnectionPool pool) {
            this.pool = pool;
        }


        public int countUsedConnections() {
            return pool.getAllConnectionsSize() - pool.getUnusedConnectionsSize();
        }


        public int countUnusedConnections() {
            return pool.getUnusedConnectionsSize();
        }
    }
}
