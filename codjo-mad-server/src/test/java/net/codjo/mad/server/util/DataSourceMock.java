/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.mad.server.util;
import net.codjo.test.common.LogString;
import net.codjo.test.common.mock.ConnectionMock;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
/**
 * Mock de DataSource J2EE.
 */
public class DataSourceMock implements DataSource {
    private LogString log = new LogString();
    private ConnectionMock connectionMock = new ConnectionMock();

    public Connection getConnection() throws SQLException {
        log.call("getConnection");
        return connectionMock.getStub();
    }


    public Connection getConnection(String s1, String s2)
            throws SQLException {
        return null;
    }


    public PrintWriter getLogWriter() throws SQLException {
        return null;
    }


    public int getLoginTimeout() throws SQLException {
        return 0;
    }


    public void setLogWriter(PrintWriter printWriter)
            throws SQLException {}


    public void setLoginTimeout(int timeout) throws SQLException {}


    public void setLog(LogString log) {
        this.log = log;
    }
}
