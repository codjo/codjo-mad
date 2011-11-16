/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.mad.server.plugin;
import net.codjo.agent.AclMessage;
import net.codjo.agent.AgentMock;
import net.codjo.mad.common.ZipUtil;
import net.codjo.mad.common.message.RequestProtocol;
import net.codjo.mad.server.plugin.DefaultHandlerExecutorFactory.DefaultHandlerExecutor;
import net.codjo.mad.server.plugin.HandlerExecutor.HandlerExecutionMode;
import net.codjo.security.common.api.User;
import net.codjo.security.common.api.UserMock;
import net.codjo.sql.server.ConnectionPool;
import net.codjo.sql.server.ConnectionPoolMock;
import net.codjo.test.common.LogString;
import org.exolab.castor.jdo.JDO;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;
/**
 * Classe de test de {@link RequestParticipant}.
 */
public class RequestParticipantTest {
    public static final String RESULT_MOCK = "résultat";
    private RequestParticipant behaviour;
    private AgentMock agentMock;
    private ProcessorMock processor;


    @Before
    public void setUp() throws Exception {
        processor = new ProcessorMock();
        ConnectionPool pool = new ConnectionPoolMock(new LogString());
        User user = new UserMock().mockIsAllowedTo(false);
        agentMock = new AgentMock();

        DefaultHandlerExecutor handlerExecutor = new DefaultHandlerExecutor();
        behaviour = new RequestParticipant(handlerExecutor,
                                           HandlerExecutionMode.SYNCHRONOUS,
                                           processor,
                                           pool,
                                           new JDO(),
                                           user);
        behaviour.setAgent(agentMock);
    }


    @Test
    public void test_done() throws Exception {
        assertFalse(behaviour.done());
    }


    @Test
    public void test_action_listenOnlyMadRequestProtocolMessage() {
        behaviour.action();
        agentMock.getLog().assertContent("agent.receive((( Language: xml-language ) "
                                         + "AND ( Protocol: mad-request-protocol )))");
    }


    @Test
    public void test_action_receiveMessage() throws Exception {
        AclMessage request = buildRequestMessage();
        agentMock.mockReceive(request);

        behaviour.action();

        // Assert Response
        AclMessage response = agentMock.getLastSentMessage();
        assertNotNull(response);
        assertEquals(AclMessage.ZIP_ENCODING, response.getEncoding());
        assertEquals(request.getLanguage(), response.getLanguage());
        assertEquals(request.getProtocol(), response.getProtocol());
        assertEquals(request.getConversationId(), response.getConversationId());
        assertEquals(RESULT_MOCK, ZipUtil.unzip(response.getByteSequenceContent()));

        // Assert Contexte utilise
        assertEquals(request.getContent(), processor.lastRequest);
        assertNotNull(processor.lastContext);
    }


    @Test
    public void test_action_error() throws Exception {
        AclMessage request = buildRequestMessage();
        agentMock.mockReceive(request);
        processor.mockProceedError(new RuntimeException("erreur interne"));

        behaviour.action();

        // Assert Response
        AclMessage response = agentMock.getLastSentMessage();
        assertNotNull(response);
        assertEquals(AclMessage.ZIP_ENCODING, response.getEncoding());
        assertEquals(request.getLanguage(), response.getLanguage());
        assertEquals(request.getProtocol(), response.getProtocol());
        assertEquals(request.getConversationId(), response.getConversationId());
        assertEquals("<?xml version=\"1.0\"?><results>"
                     + "<error request_id = \"Interne\"><label><![CDATA[erreur interne]]>"
                     + "</label><type>class java.lang.RuntimeException</type></error></results>",
                     response.getContent());
    }


    private AclMessage buildRequestMessage() {
        AclMessage request = new AclMessage(AclMessage.Performative.REQUEST);
        request.setProtocol(RequestProtocol.ID);
        request.setLanguage(AclMessage.XML_LANGUAGE);
        request.setConversationId("conversationId");
        request.setContent("xxxxxx");
        return request;
    }
}
