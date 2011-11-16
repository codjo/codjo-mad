/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.mad.server;
/**
 * Abstraction d'un transaction Manager
 */
public interface MadTransaction {
    public void begin() throws MadTransactionFailureException;


    public void commit() throws MadTransactionFailureException;


    public void flush() throws MadTransactionFailureException;


    public void rollback() throws MadTransactionFailureException;
}
