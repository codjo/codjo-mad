/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.mad.server.handler.sql;
import net.codjo.mad.server.handler.HandlerException;
import java.util.Map;
/**
 * Interface utilisée par les Handlers factory.
 */
public interface QueryBuilder {
    String buildQuery(Map<String, String> args, SqlHandler sqlHandler) throws HandlerException;
}
