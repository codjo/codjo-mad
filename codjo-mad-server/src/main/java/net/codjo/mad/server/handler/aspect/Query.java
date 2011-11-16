/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.mad.server.handler.aspect;
import java.beans.IntrospectionException;
import java.util.Map;
/**
 * Description d'une requete émise par l'utilisateur.
 * 
 * <p>
 * <b>NB</b> : CRUD signifie Create Read Update Delete.
 * </p>
 *
 * @see AbstractQuery
 */
public interface Query {
    String getId();


    Map rowToMap();


    void toBean(Object bean) throws IntrospectionException;
}
