/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.mad.server.util;
import java.sql.Connection;
import java.sql.Savepoint;
/**
 * Decorateur bloquant le close et les actions transactionnelles (begin, commit, etc.).
 */
public class ConnectionNoCloseOrTx extends ConnectionNoClose {
    public ConnectionNoCloseOrTx(Connection txConnection) {
        super(txConnection);
    }


    @Override
    public void setAutoCommit(boolean autoCommit) {
    }


    @Override
    public void commit() {
    }


    @Override
    public void rollback() {
    }


    @Override
    public void rollback(Savepoint savepoint) {
    }
}
