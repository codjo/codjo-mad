/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.mad.server;
import net.codjo.test.common.mock.ConnectionMock;
import java.sql.Connection;
import java.sql.SQLException;
import org.exolab.castor.jdo.Database;

public class MadConnectionManagerMock implements MadConnectionManager {
    private Connection connectionMock;
    private Connection txConnectionMock;


    public MadConnectionManagerMock() {
        connectionMock = new ConnectionMock();
        txConnectionMock = new ConnectionMock();
    }


    public Connection getConnection() throws SQLException {
        return connectionMock;
    }


    public void mockGetConnection(Connection connection) {
        connectionMock = connection;
    }


    public Connection getTxConnection() throws SQLException {
        return txConnectionMock;
    }


    public Database getDatabase() {
        return null;
    }


    public int countUsedConnections() {
        return 0;
    }


    public int countUnusedConnections() {
        return 0;
    }
}
