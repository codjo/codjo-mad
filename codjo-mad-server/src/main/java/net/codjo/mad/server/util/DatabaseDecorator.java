/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.mad.server.util;
import org.exolab.castor.jdo.Database;
import org.exolab.castor.jdo.OQLQuery;
import org.exolab.castor.jdo.PersistenceException;
import org.exolab.castor.jdo.Query;
import org.exolab.castor.jdo.QueryException;
import org.exolab.castor.jdo.TransactionAbortedException;
import org.exolab.castor.jdo.TransactionNotInProgressException;
import org.exolab.castor.persist.PersistenceInfoGroup;
import org.exolab.castor.persist.spi.Complex;
/**
 * Wrapper sur une Database castor. Tous les appels sont délégué sur la véritable database.
 *
 * <p> CLASSE DUPLIQUE DE AGF-MAD-SERVER (ne pas oublier de remonter le test) </p>
 */
public abstract class DatabaseDecorator implements Database {
    private Database database;


    protected DatabaseDecorator(Database db) {
        this.database = db;
    }


    public Database getDatabase() {
        return database;
    }


    public void begin() throws PersistenceException {
        database.begin();
    }


    public void commit()
          throws TransactionNotInProgressException, TransactionAbortedException {
        database.commit();
    }


    public void rollback() throws TransactionNotInProgressException {
        database.rollback();
    }


    public void setAutoStore(boolean autoStore) {
        database.setAutoStore(autoStore);
    }


    public boolean isActive() {
        return database.isActive();
    }


    public boolean isAutoStore() {
        return database.isAutoStore();
    }


    public ClassLoader getClassLoader() {
        return database.getClassLoader();
    }


    public boolean isClosed() {
        return database.isClosed();
    }


    public Object getIdentity(Object object) {
        return database.getIdentity(object);
    }


    public OQLQuery getOQLQuery() {
        return database.getOQLQuery();
    }


    public OQLQuery getOQLQuery(String oql) throws QueryException {
        return database.getOQLQuery(oql);
    }


    public boolean isPersistent(Object object) {
        return database.isPersistent(object);
    }


    public Query getQuery() {
        return database.getQuery();
    }


    public PersistenceInfoGroup getScope() {
        return database.getScope();
    }


    public void checkpoint()
          throws TransactionNotInProgressException, TransactionAbortedException {
        database.checkpoint();
    }


    public void close() throws PersistenceException {
        database.close();
    }


    public void create(Object object) throws PersistenceException {
        database.create(object);
    }


    public void deletePersistent(Object object) throws PersistenceException {
        database.deletePersistent(object);
    }


    public Object load(Class type, Object identity)
          throws PersistenceException {
        return database.load(type, identity);
    }


    public Object load(Class type, Complex identity)
          throws PersistenceException {
        return database.load(type, identity);
    }


    public Object load(Class type, Object identity, short accessMode)
          throws PersistenceException {
        return database.load(type, identity, accessMode);
    }


    public Object load(Class type, Complex identity, short accessMode)
          throws PersistenceException {
        return database.load(type, identity, accessMode);
    }


    public Object load(Class type, Object identity, Object object)
          throws PersistenceException {
        return database.load(type, identity, object);
    }


    public void lock(Object object) throws PersistenceException {
        database.lock(object);
    }


    public void makePersistent(Object object) throws PersistenceException {
        database.makePersistent(object);
    }


    public void remove(Object object) throws PersistenceException {
        database.remove(object);
    }


    public void update(Object object) throws PersistenceException {
        database.update(object);
    }


    public void flush() throws TransactionAbortedException {
        database.flush();
    }
}
