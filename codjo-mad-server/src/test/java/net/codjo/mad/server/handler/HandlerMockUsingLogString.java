/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.mad.server.handler;
import net.codjo.test.common.LogString;
/**
 *
 */
public class HandlerMockUsingLogString extends HandlerMock {
    public static final String MOCK_ID = "handlerMockUsingLogString";


    public HandlerMockUsingLogString() {
        super(MOCK_ID, null, "ok");
        this.log = null;
    }


    public HandlerMockUsingLogString(LogString log) {
        this();
        this.log = log;
        log.call("constructor");
    }
}
