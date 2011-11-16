/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.mad.server.handler.aspect;
import net.codjo.mad.server.handler.XMLUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import junit.framework.TestCase;
/**
 * Classe de test de {@link QueryManager}.
 */
public class QueryManagerTest extends TestCase {
    private QueryManagerImpl manager;

    public void test_commandRequest() {
        assertSingleRequest("MyCommand", toMap("period", "200405"));
    }


    public void test_commandRequest_noArgs() {
        assertSingleRequest("SimpleCommand", Collections.emptyMap());
    }


    public void test_selectRequest() {
        assertSingleRequest("selectByFile", toMap("importSettingsId", "1"));
    }


    public void test_selectRequest_noSelector() {
        assertSingleRequest("selectAll", Collections.emptyMap());
    }


    public void test_deleteRequest() {
        assertSingleRequest("deleteImportSettings", toMap("importSettingsId", "1"));
    }


    public void test_updateRequest() {
        assertSingleRequest("updateCompany",
            toMap(new String[][] {
                    {"label", "bobo"},
                    {"period", "200405"}
                }));
    }


    public void test_insertRequest() {
        assertSingleRequest("newManagementCompany",
            toMap(new String[][] {
                    {"mgtCompanyLabel", "null"},
                    {"period", "200405"}
                }));
    }


    public void test_insertRequest_noRow() {
        assertSingleRequest("newNoRow", Collections.emptyMap());
    }


    public void test_multipleRequest() {
        Query[] query = manager.getQuery("2Times");

        assertEquals("2 commandes à la fois", 2, query.length);
    }


    public void test_insertRequest_unknownHandler() {
        try {
            manager.getQuery("unknown");
            fail("Le handler est inconnu");
        }
        catch (IllegalArgumentException error) {
            assertEquals("Handler 'unknown' est introuvable !", error.getMessage());
        }
    }


    public void test_insertRequest_unknownTypeHandler() {
        try {
            manager.getQuery("badQuery");
            fail("Le type du handler est inconnu (badQuery)");
        }
        catch (IllegalArgumentException error) {
            assertEquals("Le type de handler 'bad' est inconnu !", error.getMessage());
        }
    }


    public void test_getHandlerList() {
        String[] exp =
            new String[] {
                "newManagementCompany", "newNoRow", "updateCompany",
                "deleteImportSettings", "selectByFile", "selectAll", "SimpleCommand",
                "MyCommand", "badQuery", "2Times", "2Times", "sqlRocks", "xmlForEver"
            };
        List expected = Arrays.asList(exp);
        assertEquals(expected, Arrays.asList(manager.getHandlerIdList()));
    }


    public void test_getUser() {
        assertEquals("roses", manager.getUser());
    }


    public void test_sqlRequest_selectors() {
        assertSingleRequest("sqlRocks",
                toMap(new String[][]{{"title", "Life, the universe and everything"},
                                     {"author", "Douglas N. Adams"}}));
    }


    public void test_sqlRequest_arguments() {
        assertSingleRequest("xmlForEver",
                toMap(new String[][]{{"mostIntelligentSpecies","mice"},
                                     {"secondMostIntelligentSpecies","dolphins"},
                                     {"thirdMostIntelligentSpecies","monkeys"}}));
    }


    @Override
    protected void setUp() throws Exception {
        manager = new QueryManagerImpl();
        manager.setQueries(XMLUtils.parse(loadFile("QueryManagerTest.xml")));
    }


    private Map toMap(String key, String arg) {
        return toMap(new String[][] {
                {key, arg}
            });
    }


    private Map toMap(String[][] content) {
        Map<String, String> map = new HashMap<String, String>();
        for (String[] aContent : content) {
            map.put(aContent[0], aContent[1]);
        }
        return map;
    }


    private String loadFile(String fileName) throws IOException {
        StringBuilder buffer = new StringBuilder();
        InputStream stream = QueryManagerTest.class.getResourceAsStream(fileName);
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String line = reader.readLine();
            while (line != null) {
                buffer.append(line);
                line = reader.readLine();
            }
        }
        finally {
            stream.close();
        }
        return buffer.toString();
    }


    private void assertSingleRequest(String handlerId, Map values) {
        Query cmd = manager.getQuery(handlerId)[0];

        assertNotNull("La requete est trouvé", cmd);
        assertEquals(handlerId, cmd.getId());
        assertEquals(values, cmd.rowToMap());
    }
}
