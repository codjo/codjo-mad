/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.mad.server.handler;
import net.codjo.mad.common.Log;
import net.codjo.test.common.LogString;
import org.w3c.dom.Node;
/**
 * Mock d'un {@link Handler}.
 *
 * @author $Author: gaudefr $
 * @version $Revision: 1.6 $
 */
public class HandlerMock extends AbstractHandler {
    static final String ID = "handlerMock";
    protected LogString log = new LogString();
    RuntimeException exception = null;
    String node = null;
    String result = null;
    private final String handlerId;
    String expectedNodeAsString;
    private boolean called = false;

    public HandlerMock() {
        this(ID, null, "ok");
    }


    HandlerMock(String handlerId, String expectedNodeAsString, String result) {
        this.handlerId = handlerId;
        this.expectedNodeAsString = expectedNodeAsString;
        this.result = result;
    }


    HandlerMock(String handlerId, String expectedNodeAsString, RuntimeException ex) {
        this.handlerId = handlerId;
        this.expectedNodeAsString = expectedNodeAsString;
        this.exception = ex;
    }

    public boolean isCalled() {
        return called;
    }


    public String getId() {
        return handlerId;
    }


    public String proceed(Node newNode) {
        log.call("proceed");
        called = true;
        try {
            this.node = XMLUtils.toString(newNode);
        }
        catch (Exception ex) {
            Log.error(ex);
        }
        if (exception != null) {
            throw exception;
        }
        return result;
    }


    public boolean verify() {
        boolean verify =
            isCalled()
            && (expectedNodeAsString == null || expectedNodeAsString.equals(node));
        if (!verify) {
            Log.debug("isCalled = " + isCalled());
            Log.debug("expected = " + expectedNodeAsString);
            Log.debug("received = " + node);
        }
        return verify;
    }


    public LogString getLog() {
        return log;
    }
}
