/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.mad.server.structure;
import net.codjo.mad.server.handler.HandlerContext;
import net.codjo.util.file.FileUtil;
import net.codjo.test.common.XmlUtil;
import java.sql.Connection;
import java.sql.SQLException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;

public class StructureHomeTest {
    private StructureHome structureHome = new StructureHome();


    @Test
    public void test_defaultStructurePath() throws Exception {
        assertEquals("/conf/structure.xml", structureHome.getDefaultStructurePath());
    }


    @Test
    public void test_badStructurePath() throws Exception {
        try {
            structureHome.getStructure();
            fail();
        }
        catch (IllegalArgumentException ex) {
            assertEquals("La resource '/conf/structure.xml' est introuvable", ex.getMessage());
        }
    }


    @Test
    public void test_load() throws Exception {
        // Recuperation de la structure
        structureHome.setDefaultStructurePath("/conf/test/structure.xml");
        String xmlFile = structureHome.getStructure();

        // Verification du resultat
        String etalon = FileUtil.loadContent(getClass().getResource("/conf/test/structureEtalon.xml"));
        XmlUtil.assertEquals(etalon, xmlFile);
    }


    public class HandlerContextMock extends HandlerContext {
        private Connection connection;


        public HandlerContextMock(Connection connection) {
            this.connection = connection;
        }


        @Override
        public Connection getConnection() throws SQLException {
            return connection;
        }
    }
}
