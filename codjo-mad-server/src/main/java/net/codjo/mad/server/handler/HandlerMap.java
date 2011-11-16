/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.mad.server.handler;
import java.util.Set;
/**
 * Classe contenant les handler pouvant être utilisés.
 */
public interface HandlerMap {
    Handler getHandler(String handlerId);


    Set<String> getHandlerIdSet();
}
