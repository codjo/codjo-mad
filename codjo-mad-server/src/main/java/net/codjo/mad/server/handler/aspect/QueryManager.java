/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.mad.server.handler.aspect;
/**
 * Manager sur la requête courante.
 */
public interface QueryManager {
    String[] getHandlerIdList();


    Query[] getQuery(String handlerId);


    String getUser();
}
