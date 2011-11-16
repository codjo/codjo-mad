/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.mad.server.util;
import java.sql.Connection;
import java.sql.SQLException;
/**
 * Decorateur de connection bloquant l'appel close.
 */
public class ConnectionNoClose extends ConnectionDecorator {
    public ConnectionNoClose(Connection subConnection) {
        super(subConnection);
    }


    @Override
    public void close() {
    }


    public void closeInnerConnection() throws SQLException {
        super.close();
    }
}
