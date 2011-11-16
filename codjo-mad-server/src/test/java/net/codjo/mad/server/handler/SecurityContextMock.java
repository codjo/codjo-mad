/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.mad.server.handler;
import net.codjo.security.common.api.SecurityContext;
/**
 * Mock de {@link net.codjo.security.common.api.SecurityContext}.
 */
public class SecurityContextMock implements SecurityContext {
    private boolean callerInRole;


    public static SecurityContext userIsInAllRole() {
        return new SecurityContextMock(true);
    }


    private SecurityContextMock(boolean isCallerInRole) {
        callerInRole = isCallerInRole;
    }


    public boolean isCallerInRole(String roleId) {
        return callerInRole;
    }
}
