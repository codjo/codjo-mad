/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.mad.server.handler;
import net.codjo.aspect.AspectContext;
import net.codjo.aspect.util.TransactionException;
import net.codjo.aspect.util.TransactionalManager;
import net.codjo.mad.server.MadTransaction;
import net.codjo.mad.server.MadTransactionFailureException;
/**
 * Classe Wrapper pour le TxManager
 */
class TxManagerWrapper implements TransactionalManager {
    public void begin(AspectContext ctxt) throws TransactionException {
        try {
            getTransaction(ctxt).begin();
        }
        catch (Exception e) {
            throw new TransactionException(e);
        }
    }


    public void flush(AspectContext ctxt) throws TransactionException {
        try {
            getTransaction(ctxt).flush();
        }
        catch (Exception e) {
            throw new TransactionException(e);
        }
    }


    public void commit(AspectContext ctxt) throws TransactionException {
        try {
            getTransaction(ctxt).commit();
        }
        catch (Exception e) {
            throw new TransactionException(e);
        }
    }


    public void rollback(AspectContext ctxt) throws TransactionException {
        try {
            getTransaction(ctxt).rollback();
        }
        catch (MadTransactionFailureException e) {
            throw new TransactionException(e);
        }
    }


    public void end(AspectContext ctxt) {}


    private MadTransaction getTransaction(AspectContext ctxt) {
        return (MadTransaction)ctxt.get(HandlerManager.MAD_TX_MANAGER);
    }
}
