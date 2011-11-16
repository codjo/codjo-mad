/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.mad.server;
import net.codjo.agent.UserId;
import net.codjo.security.common.api.SecurityContext;
import net.codjo.security.common.api.User;
import net.codjo.security.server.api.UserFactory;

/**
 *
 */
public class MadRequestContextMock implements MadRequestContext {
    private MadTransaction madTransactionMock;
    private SecurityContext securityContextMock;
    private UserFactory userFactory;
    private MadConnectionManager connectionManager;


    public MadRequestContextMock(MadTransaction madTransactionMock,
                                 SecurityContext securityContextMock, UserFactory userFactory) {
        this.madTransactionMock = madTransactionMock;
        this.securityContextMock = securityContextMock;
        this.userFactory = userFactory;
        this.connectionManager = new MadConnectionManagerMock();
    }


    public MadTransaction getTransaction() {
        return madTransactionMock;
    }


    public MadConnectionManager getConnectionManager() {
        return connectionManager;
    }


    public User getUserProfil() {
        return userFactory.getUser(UserId.createId("", ""), securityContextMock);
    }


    public void close() {
    }
}
