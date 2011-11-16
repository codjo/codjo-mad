/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.mad.server.handler;
import junit.framework.TestCase;
/**
 */
public class ErrorUtilTest extends TestCase {
    public ErrorUtilTest(String name) {
        super(name);
    }


    public void test_rootException() throws Exception {
        Exception root = new Exception();
        assertEquals(root, ErrorUtil.findRootException(root));
    }


    public void test_rootException_through_HandlerException()
          throws Exception {
        Exception root = new Exception();
        HandlerException first = new HandlerException(root);
        assertEquals(root, ErrorUtil.findRootException(first));
    }


    public void test_rootIsRequestFailureException() {
        Exception root = new Exception();
        HandlerException second = new HandlerException(root);
        RequestFailureException first = new RequestFailureException("", second);
        assertEquals(root, ErrorUtil.findRootException(first));
    }


    public void test_rootIsRequestFailureException_bis() {
        HandlerException root = new HandlerException("");
        RequestFailureException first = new RequestFailureException("", root);
        assertEquals(root, ErrorUtil.findRootException(first));
    }
}
