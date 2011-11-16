/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.mad.server.util;
import net.codjo.test.common.LogString;
import org.exolab.castor.jdo.ClassNotPersistenceCapableException;
import org.exolab.castor.jdo.Database;
import org.exolab.castor.jdo.DuplicateIdentityException;
import org.exolab.castor.jdo.LockNotGrantedException;
import org.exolab.castor.jdo.OQLQuery;
import org.exolab.castor.jdo.ObjectNotFoundException;
import org.exolab.castor.jdo.ObjectNotPersistentException;
import org.exolab.castor.jdo.PersistenceException;
import org.exolab.castor.jdo.Query;
import org.exolab.castor.jdo.QueryException;
import org.exolab.castor.jdo.TransactionAbortedException;
import org.exolab.castor.jdo.TransactionNotInProgressException;
import org.exolab.castor.persist.PersistenceInfoGroup;
import org.exolab.castor.persist.spi.Complex;
/**
 * Mock de {@link org.exolab.castor.jdo.Database}
 */
public class DatabaseMock implements Database {
    private LogString log = new LogString();
    private TransactionAbortedException flushException;

    public DatabaseMock() {}

    public void setLog(LogString logString) {
        this.log = logString;
    }


    public void flush() throws TransactionAbortedException {
        log.call("flush");
        if (flushException != null) {
            throw flushException;
        }
    }


    public void mockFlushFailure(TransactionAbortedException newFlushException) {
        flushException = newFlushException;
    }


    public OQLQuery getOQLQuery() {
        return null;
    }


    public OQLQuery getOQLQuery(String query) throws QueryException {
        return null;
    }


    public Query getQuery() {
        return null;
    }


    public PersistenceInfoGroup getScope() {
        return null;
    }


    public Object load(Class aClass, Object object)
            throws TransactionNotInProgressException, ObjectNotFoundException, 
                LockNotGrantedException, PersistenceException {
        return null;
    }


    public Object load(Class aClass, Complex complex)
            throws ObjectNotFoundException, LockNotGrantedException, 
                TransactionNotInProgressException, PersistenceException {
        return null;
    }


    public Object load(Class aClass, Object object, short index)
            throws TransactionNotInProgressException, ObjectNotFoundException, 
                LockNotGrantedException, PersistenceException {
        return null;
    }


    public Object load(Class aClass, Complex complex, short index)
            throws ObjectNotFoundException, LockNotGrantedException, 
                TransactionNotInProgressException, PersistenceException {
        return null;
    }


    public Object load(Class aClass, Object o1, Object o2)
            throws ObjectNotFoundException, LockNotGrantedException, 
                TransactionNotInProgressException, PersistenceException {
        return null;
    }


    public void create(Object object)
            throws ClassNotPersistenceCapableException, DuplicateIdentityException, 
                TransactionNotInProgressException, PersistenceException {}


    public void remove(Object object)
            throws ObjectNotPersistentException, LockNotGrantedException, 
                TransactionNotInProgressException, PersistenceException {}


    public void update(Object object)
            throws ClassNotPersistenceCapableException, TransactionNotInProgressException, 
                PersistenceException {}


    public void lock(Object object)
            throws LockNotGrantedException, ObjectNotPersistentException, 
                TransactionNotInProgressException, PersistenceException {}


    public void begin() throws PersistenceException {}


    public boolean isAutoStore() {
        return false;
    }


    public void setAutoStore(boolean autoStore) {}


    public void commit()
            throws TransactionNotInProgressException, TransactionAbortedException {}


    public void rollback() throws TransactionNotInProgressException {}


    public boolean isActive() {
        return false;
    }


    public boolean isClosed() {
        return false;
    }


    public void close() throws PersistenceException {}


    public boolean isPersistent(Object object) {
        return false;
    }


    public Object getIdentity(Object object) {
        return null;
    }


    public ClassLoader getClassLoader() {
        return null;
    }


    public void makePersistent(Object object)
            throws ClassNotPersistenceCapableException, DuplicateIdentityException, 
                PersistenceException {}


    public void deletePersistent(Object object)
            throws ObjectNotPersistentException, LockNotGrantedException, 
                PersistenceException {}


    public void checkpoint()
            throws TransactionNotInProgressException, TransactionAbortedException {}
}
