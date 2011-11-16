/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.mad.server.util;
import net.codjo.test.common.LogString;
import net.codjo.test.common.mock.DatabaseMock;
import junit.framework.TestCase;
/**
 * Classe de test de {@link DatabaseNoClose}.
 */
public class DatabaseNoCloseTest extends TestCase {
    private DatabaseNoClose databaseNoClose;
    private LogString log = new LogString();


    public void test_close() throws Exception {
        databaseNoClose.close();
        log.assertContent("");
    }


    public void test_closeInnerDb() throws Exception {
        databaseNoClose.closeInnerDb();
        log.assertContent("database.close()");
    }


    @Override
    protected void setUp() throws Exception {
        databaseNoClose =
              new DatabaseNoClose(new DatabaseMock(new LogString("database", log)));
    }
}
