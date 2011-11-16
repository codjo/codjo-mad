/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.mad.server;
import net.codjo.test.common.LogString;

/**
 *
 */
public class MadTransactionMock implements MadTransaction {
    private LogString log;


    public MadTransactionMock() {
        this(new LogString());
    }


    public MadTransactionMock(LogString log) {
        this.log = log;
    }


    public void assertLog(String expected) {
        log.assertContent(expected);
    }


    public void begin() {
        log.call("begin");
    }


    public void commit() {
        log.call("commit");
    }


    public void flush() {
        log.call("flush");
    }


    public void rollback() {
        log.call("rollback");
    }
}
