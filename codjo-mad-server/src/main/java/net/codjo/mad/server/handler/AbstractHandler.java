/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.mad.server.handler;
/**
 * Classe de base des Handler.
 *
 * @author $Author: gaudefr $
 * @version $Revision: 1.3 $
 */
public abstract class AbstractHandler implements Handler {
    private HandlerContext context;

    protected AbstractHandler() {}

    public void setContext(HandlerContext context) {
        this.context = context;
    }


    public HandlerContext getContext() {
        return context;
    }
}
