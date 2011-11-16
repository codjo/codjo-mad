/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.mad.server.handler.aspect;
import java.util.HashMap;
import java.util.Map;
/**
 * Classe permettant de mocker la classe {@link AbstractQuery} à des fins de test
 * notamment.
 *
 * @version $Revision: 1.2 $
 */
public class QueryMock extends AbstractQuery {
    private String id;
    private Map map;

    public QueryMock() {}


    public QueryMock(String id, Map map) {
        this.id = id;
        this.map = map;
    }


    public QueryMock(String id, String[][] row) {
        this.id = id;
        mockRowToMap(row);
    }

    /**
     * DOCUMENT ME!
     *
     * @return
     *
     * @see #mockGetId(String)
     */
    public String getId() {
        return id;
    }


    /**
     * DOCUMENT ME!
     *
     * @return
     *
     * @see #mockRowToMap(java.util.Map)
     * @see #mockRowToMap(String[][])
     */
    public Map rowToMap() {
        return map;
    }


    /**
     * DOCUMENT ME!
     *
     * @param mockedId
     *
     * @see #getId()
     */
    public void mockGetId(String mockedId) {
        id = mockedId;
    }


    /**
     * DOCUMENT ME!
     *
     * @param mockedMap
     *
     * @see #rowToMap()
     */
    public void mockRowToMap(Map mockedMap) {
        map = mockedMap;
    }


    /**
     * Permet de mocker la map retourner à partir d'un tableau. La i eme colonne est
     * représentée par le tableau row[i], row[i][0] étant le nom de la colonne, et
     * row[i][1] sa valeur.
     *
     * @param row
     *
     * @see #rowToMap()
     */
    public void mockRowToMap(String[][] row) {
        Map mockedMap = new HashMap();
        for (int i = 0; i < row.length; i++) {
            String[] field = row[i];
            map.put(field[0], field[1]);
        }
        mockRowToMap(mockedMap);
    }
}
