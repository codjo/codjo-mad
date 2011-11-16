/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.mad.server.handler;
import org.w3c.dom.Node;
/**
 * Interface decrivant un handler.
 *
 * @author $Author: gaudefr $
 * @version $Revision: 1.3 $
 */
public interface Handler {
    public String proceed(Node node) throws HandlerException;


    public String getId();


    public void setContext(HandlerContext handlerContext);
}
