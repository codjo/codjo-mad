/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.mad.server.handler.aspect;
import org.w3c.dom.Document;

/**
 *
 */
public class QueryManagerBuilder {
    private QueryManagerImpl queryManager = new QueryManagerImpl();


    public QueryManager build(Document queries) {
        queryManager.setQueries(queries);
        return queryManager;
    }
}
