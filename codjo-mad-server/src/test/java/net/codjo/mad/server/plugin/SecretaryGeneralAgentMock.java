/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.mad.server.plugin;
import net.codjo.agent.AclMessage;
import net.codjo.agent.AgentMock;
import net.codjo.agent.MessageTemplate;
import net.codjo.agent.ServiceException;
import net.codjo.agent.ServiceHelper;
import net.codjo.security.server.api.SecurityServiceHelper;
import net.codjo.security.server.api.SecurityServiceHelperMock;
import net.codjo.sql.server.JdbcServiceHelper;
import net.codjo.sql.server.JdbcServiceHelperMock;
import net.codjo.test.common.LogString;
/**
 * Mock de la classe {@link SecretaryGeneralAgent}.
 */
class SecretaryGeneralAgentMock extends SecretaryGeneralAgent {
    private AgentMock myAgentMock;
    private JdbcServiceHelperMock jdbcServiceHelper;
    private AclMessage lastSentMessage;
    private SecurityServiceHelperMock securityServiceHelper;


    SecretaryGeneralAgentMock() {
        myAgentMock = new AgentMock();
        jdbcServiceHelper = new JdbcServiceHelperMock(getLog());
        securityServiceHelper = new SecurityServiceHelperMock(getLog());
    }


    @Override
    protected void setup() {
    }


    public LogString getLog() {
        return myAgentMock.getLog();
    }


    @Override
    public void send(AclMessage aclMessage) {
        myAgentMock.send(aclMessage);
        lastSentMessage = aclMessage;
    }


    @Override
    public AclMessage receive(MessageTemplate template) {
        return myAgentMock.receive(template);
    }


    @Override
    public AclMessage receive() {
        return myAgentMock.receive();
    }


    public void mockReceiveContent(AclMessage aclMessage) {
        myAgentMock.mockReceive(aclMessage);
    }


    public void mockResponse(AclMessage aclMessage) {
        myAgentMock.mockResponse(aclMessage);
    }


    public SecurityServiceHelperMock getSecurityServiceHelperMock() {
        return securityServiceHelper;
    }


    public JdbcServiceHelperMock getJdbcServiceHelperMock() {
        return jdbcServiceHelper;
    }


    @Override
    public ServiceHelper getHelper(String serviceName)
          throws ServiceException {
        if (SecurityServiceHelper.NAME.equals(serviceName)) {
            return securityServiceHelper;
        }
        else if (JdbcServiceHelper.NAME.equals(serviceName)) {
            return jdbcServiceHelper;
        }
        throw new ServiceException("Service inconnu : " + serviceName);
    }


    public AclMessage getLastSentMessage() {
        return lastSentMessage;
    }
}
