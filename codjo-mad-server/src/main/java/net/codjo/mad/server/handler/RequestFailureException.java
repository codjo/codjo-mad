/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.mad.server.handler;
/**
 * Exception lancée lors d'un echec d'un handler.
 *
 * @author $Author: gaudefr $
 * @version $Revision: 1.6 $
 */
public class RequestFailureException extends WithCauseException {
    private String errorXml;
    private String requestId;

    public RequestFailureException(String requestId, Exception cause) {
        this(null, requestId, cause);
    }


    public RequestFailureException(String message, String requestId, Exception cause) {
        super(message, cause);
        this.requestId = requestId;
        if (message != null) {
            errorXml = buildErrorNode(message, cause, requestId);
        }
        else {
            errorXml = buildErrorNode(cause, requestId);
        }
    }

    public static String buildErrorNode(Throwable error, String requestId) {
        return buildErrorNode(error.getMessage(), error, requestId);
    }


    public static String buildErrorNode(String message, Throwable error, String requestId) {
        StringBuffer errorNode =
            new StringBuffer("<?xml version=\"1.0\"?>" + "<results><error request_id = ");
        errorNode.append("\"").append(requestId).append("\"").append(">");
        errorNode.append("<label><![CDATA[").append(message).append("]]>").append("</label>");
        errorNode.append("<type>").append(error.getClass()).append("</type>");
        errorNode.append("</error></results>");
        return errorNode.toString();
    }


    public String getErrorXml() {
        return errorXml;
    }


    public String getRequestId() {
        return requestId;
    }
}
