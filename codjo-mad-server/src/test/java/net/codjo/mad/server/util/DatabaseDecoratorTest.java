/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.mad.server.util;
import net.codjo.test.common.LogCallAssert;
import net.codjo.test.common.LogString;
import net.codjo.test.common.mock.DatabaseMock;
import junit.framework.TestCase;
import org.exolab.castor.jdo.Database;
/**
 * Classe de test de {@link net.codjo.mad.server.util.DatabaseDecorator}.
 */
public class DatabaseDecoratorTest extends TestCase {
    private DatabaseDecorator decorator;
    private LogString logString;
    private DatabaseMock database;


    public void test_getDatabase() throws Exception {
        assertSame(database, decorator.getDatabase());
    }


    public void test_delegate() throws Exception {
        LogCallAssert logCallAssert = new LogCallAssert(Database.class);
        logCallAssert.assertCalls(decorator, logString);
    }


    @Override
    protected void setUp() throws Exception {
        logString = new LogString();
        database = new DatabaseMock(logString);
        decorator = new DatabaseDecorator(database) {
        }
              ;
    }
}
