/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.mad.server.util;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.Map;
/**
 * Classer permettant de simplifier une délégation de connexion JDBC.
 *
 * @version $Revision: 1.3 $
 */
public abstract class ConnectionAdapterMock implements Connection {
    public Statement createStatement() throws SQLException {
        return getSubConnection().createStatement();
    }


    public PreparedStatement prepareStatement(String sql)
          throws SQLException {
        return getSubConnection().prepareStatement(sql);
    }


    public CallableStatement prepareCall(String sql)
          throws SQLException {
        return getSubConnection().prepareCall(sql);
    }


    public String nativeSQL(String sql) throws SQLException {
        return getSubConnection().nativeSQL(sql);
    }


    public void setAutoCommit(boolean autoCommit)
          throws SQLException {
        getSubConnection().setAutoCommit(autoCommit);
    }


    public boolean getAutoCommit() throws SQLException {
        return getSubConnection().getAutoCommit();
    }


    public void commit() throws SQLException {
        getSubConnection().commit();
    }


    public void rollback() throws SQLException {
        getSubConnection().rollback();
    }


    public boolean isClosed() throws SQLException {
        return getSubConnection().isClosed();
    }


    public DatabaseMetaData getMetaData() throws SQLException {
        return getSubConnection().getMetaData();
    }


    public void setReadOnly(boolean readOnly) throws SQLException {
        getSubConnection().setReadOnly(readOnly);
    }


    public boolean isReadOnly() throws SQLException {
        return getSubConnection().isReadOnly();
    }


    public void setCatalog(String catalog) throws SQLException {
        getSubConnection().setCatalog(catalog);
    }


    public String getCatalog() throws SQLException {
        return getSubConnection().getCatalog();
    }


    public void setTransactionIsolation(int level)
          throws SQLException {
        getSubConnection().setTransactionIsolation(level);
    }


    public int getTransactionIsolation() throws SQLException {
        return getSubConnection().getTransactionIsolation();
    }


    public SQLWarning getWarnings() throws SQLException {
        return getSubConnection().getWarnings();
    }


    public void clearWarnings() throws SQLException {
        getSubConnection().clearWarnings();
    }


    public Statement createStatement(int resultSetType, int resultSetConcurrency)
          throws SQLException {
        return getSubConnection().createStatement(resultSetType, resultSetConcurrency);
    }


    public PreparedStatement prepareStatement(String sql, int resultSetType,
                                              int resultSetConcurrency) throws SQLException {
        return getSubConnection().prepareStatement(sql, resultSetType,
                                                   resultSetConcurrency);
    }


    public CallableStatement prepareCall(String sql, int resultSetType,
                                         int resultSetConcurrency) throws SQLException {
        return getSubConnection().prepareCall(sql, resultSetType, resultSetConcurrency);
    }


    public java.util.Map<String, Class<?>> getTypeMap() throws SQLException {
        return getSubConnection().getTypeMap();
    }


    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        getSubConnection().setTypeMap(map);
    }


    protected abstract Connection getSubConnection()
          throws SQLException;


    public void setHoldability(int holdability) throws SQLException {
        getSubConnection().setHoldability(holdability);
    }


    public int getHoldability() throws SQLException {
        return getSubConnection().getHoldability();
    }


    public Savepoint setSavepoint() throws SQLException {
        return getSubConnection().setSavepoint();
    }


    public Savepoint setSavepoint(String name) throws SQLException {
        return getSubConnection().setSavepoint(name);
    }


    public void rollback(Savepoint savepoint) throws SQLException {
        getSubConnection().rollback(savepoint);
    }


    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        getSubConnection().releaseSavepoint(savepoint);
    }


    public Statement createStatement(int resultSetType,
                                     int resultSetConcurrency,
                                     int resultSetHoldability) throws SQLException {
        return getSubConnection().createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
    }


    public PreparedStatement prepareStatement(String sql,
                                              int resultSetType,
                                              int resultSetConcurrency,
                                              int resultSetHoldability) throws SQLException {
        return getSubConnection()
              .prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }


    public CallableStatement prepareCall(String sql,
                                         int resultSetType,
                                         int resultSetConcurrency,
                                         int resultSetHoldability) throws SQLException {
        return getSubConnection().prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }


    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        return getSubConnection().prepareStatement(sql, autoGeneratedKeys);
    }


    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        return getSubConnection().prepareStatement(sql, columnIndexes);
    }


    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        return getSubConnection().prepareStatement(sql, columnNames);
    }
}
