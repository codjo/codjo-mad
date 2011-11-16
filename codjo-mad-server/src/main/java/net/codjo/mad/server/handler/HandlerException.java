/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.mad.server.handler;
/**
 * Description of the Class
 *
 * @author $Author: gaudefr $
 * @version $Revision: 1.4 $
 */
public class HandlerException extends WithCauseException {
    public HandlerException(String msg) {
        super(msg, null);
    }


    public HandlerException(Exception cause) {
        super(cause.getMessage(), cause);
    }


    public HandlerException(String msg, Exception cause) {
        super(msg, cause);
    }
}
