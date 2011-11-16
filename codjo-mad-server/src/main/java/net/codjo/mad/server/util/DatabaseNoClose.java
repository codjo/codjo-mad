/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.mad.server.util;
import org.exolab.castor.jdo.Database;
import org.exolab.castor.jdo.PersistenceException;
/**
 * Encapsule une database castor en bloquant les closes. Pour fermer la database encapsulé, il faut appeler
 * {@link #closeInnerDb()}.
 *
 * <p> CLASSE DUPLIQUE DE AGF-MAD-SERVER </p>
 */
public class DatabaseNoClose extends DatabaseDecorator {
    public DatabaseNoClose(Database db) {
        super(db);
    }


    @Override
    public void close() {
    }


    public void closeInnerDb() throws PersistenceException {
        super.close();
    }
}
