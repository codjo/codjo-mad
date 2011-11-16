/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.mad.server;
/**
 * Exception lancé lors d'une erreur lors de la transaction.
 *
 * @see MadTransaction
 */
public class MadTransactionFailureException extends Exception {
    private Exception cause;

    public MadTransactionFailureException(String message, Exception cause) {
        super(message);
        this.cause = cause;
    }

    public Exception getCause() {
        return cause;
    }
}
