/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.mad.server.plugin;
import net.codjo.agent.AclMessage;
import net.codjo.agent.AclMessage.Performative;
import net.codjo.agent.Aid;
import net.codjo.agent.UserId;
import static net.codjo.agent.protocol.RequestProtocol.QUERY;
import net.codjo.agent.test.AgentContainerFixture;
import net.codjo.agent.test.DummyAgent;
import static net.codjo.agent.test.MessageBuilder.message;
import static net.codjo.mad.common.message.RequestProtocol.ID;
import net.codjo.mad.server.plugin.HandlerExecutor.HandlerExecutionMode;
import net.codjo.security.common.api.UserMock;
import net.codjo.sql.server.ConnectionPoolMock;
import org.exolab.castor.jdo.JDO;
import org.junit.Before;
import org.junit.Test;
/**
 *
 */
public class AmbassadorAgentTest extends AmbassadorTestCase {
    private AgentContainerFixture fixture = new AgentContainerFixture();
    private UserId userId = UserId.createId("login", "password");


    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        fixture = story.getAgentContainerFixture();
    }


    @Test
    public void test_understandQueryProtocol() throws Exception {
        AmbassadorAgent ambassador = new AmbassadorAgent(userId);
        fixture.startNewAgent("ambassador", ambassador);

        DummyAgent initiator = new DummyAgent();
        fixture.startNewAgent("initiator", initiator);

        AclMessage query = new AclMessage(AclMessage.Performative.QUERY);
        query.setContent("userId");
        query.setProtocol(QUERY);
        query.addReceiver(new Aid("ambassador"));
        fixture.sendMessage(initiator, query);

        AclMessage result = fixture.receiveMessage(initiator);

        fixture.assertMessage(result, QUERY, AclMessage.Performative.INFORM, userId);
    }


    @Test
    public void test_synchronousRequest() throws Exception {
        processorMock
              .mockProceed("<request><id>1</id></request>", "result1")
              .mockProceed("<request><id>2</id></request>", "result2");

        story.record().startAgent("myAmbassador",
                                  new AmbassadorAgent(userId,
                                                      new HandlerExecutorMock(),
                                                      HandlerExecutionMode.SYNCHRONOUS,
                                                      processorMock,
                                                      new ConnectionPoolMock(),
                                                      new JDO(),
                                                      new UserMock()));

        story.record().startTester("president")
              .send(message(Performative.REQUEST).usingLanguage(AclMessage.XML_LANGUAGE)
                    .usingProtocol(ID).to("myAmbassador").withContent("<request><id>1</id></request>")).then()
              .send(message(Performative.REQUEST).usingLanguage(AclMessage.XML_LANGUAGE)
                    .usingProtocol(ID).to("myAmbassador").withContent("<request><id>2</id></request>")).then()
              .receiveMessage(matchRequest("result1", "myAmbassador")).then()
              .receiveMessage(matchRequest("result2", "myAmbassador"));

        story.execute();
    }


    @Test
    public void test_asynchronousRequest() throws Exception {
        processorMock
              .mockProceed("<request><id>1</id></request>", "result1")
              .mockProceed("<request><id>2</id></request>", "result2");

        HandlerExecutorMock executorMock = new HandlerExecutorMock();
        executorMock.mockBlockExecution("<request><id>1</id></request>");
        story.record().startAgent("ambassador",
                                  new AmbassadorAgent(userId,
                                                      executorMock,
                                                      HandlerExecutionMode.ASYNCHRONOUS,
                                                      processorMock,
                                                      new ConnectionPoolMock(),
                                                      new JDO(),
                                                      new UserMock()));

        story.record().startTester("president")
              .send(message(Performative.REQUEST).usingLanguage(AclMessage.XML_LANGUAGE)
                    .usingProtocol(ID).to("ambassador").withContent("<request><id>1</id></request>")).then()
              .send(message(Performative.REQUEST).usingLanguage(AclMessage.XML_LANGUAGE)
                    .usingProtocol(ID).to("ambassador").withContent("<request><id>2</id></request>")).then()
              .receiveMessage(matchRequest("result2", "ambassador-delegate")).then()
              .receiveMessage(matchRequest("result1", "ambassador-delegate"));

        story.execute();
    }
}
