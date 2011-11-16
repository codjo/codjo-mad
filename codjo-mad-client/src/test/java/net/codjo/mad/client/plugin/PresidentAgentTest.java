package net.codjo.mad.client.plugin;
import net.codjo.agent.AclMessage;
import net.codjo.agent.AclMessage.Performative;
import net.codjo.agent.MessageTemplate;
import net.codjo.agent.MessageTemplate.MatchExpression;
import static net.codjo.agent.MessageTemplate.and;
import static net.codjo.agent.MessageTemplate.matchLanguage;
import static net.codjo.agent.MessageTemplate.matchPerformative;
import static net.codjo.agent.MessageTemplate.matchProtocol;
import static net.codjo.agent.MessageTemplate.matchWith;
import net.codjo.agent.UserId;
import static net.codjo.agent.test.AgentAssert.log;
import net.codjo.agent.test.AgentContainerFixture;
import net.codjo.agent.test.Story;
import net.codjo.mad.common.ZipUtil;
import net.codjo.mad.common.message.InstituteAmbassadorProtocol;
import net.codjo.mad.common.message.RequestProtocol;
import net.codjo.test.common.LogString;
import net.codjo.util.system.EventSynchronizer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
/**
 * Classe de test de {@link PresidentAgent}.
 */
public class PresidentAgentTest {
    private PresidentAgent agent;
    private LogString logString = new LogString();
    private Story story = new Story();
    private UserId userId;
    private static final String AMBASSADOR_NAME = "AmbassadorForYou";


    @Before
    public void setUp() throws Exception {
        userId = UserId.createId("bad", "badPwd");
        agent = new PresidentAgent(userId, new EventSynchronizer<InstitueEvent>());
        story.doSetUp();
    }


    @After
    public void tearDown() throws Exception {
        story.doTearDown();
    }


    @Test
    public void test_instituteAmbassador() throws Exception {
        story.record().startTester(AMBASSADOR_NAME)
              .receiveMessage(and(matchProtocol(RequestProtocol.ID), matchPerformative(Performative.REQUEST)))
              .replyWithByteSequence(Performative.INFORM, ZipUtil.zip("resultsForSender1")).then()
              .receiveMessage(and(matchProtocol(RequestProtocol.ID), matchPerformative(Performative.REQUEST)))
              .replyWithByteSequence(Performative.INFORM, ZipUtil.zip("resultsForSender2"));

        story.record().startTester(InstituteAmbassadorProtocol.SECRETARY_GENERAL_AGENT_NAME)
              .receiveMessage(matchInstituteAmbassadorInitialMessage())
              .replyWith(Performative.INFORM, AMBASSADOR_NAME);
        story.record().assertContainsAgent(InstituteAmbassadorProtocol.SECRETARY_GENERAL_AGENT_NAME);

        story.record().startAgent("PresidentAgent", agent);

        story.record().addAction(new AgentContainerFixture.Runnable() {
            public void run() throws Exception {
                agent.putO2AObject(new RequestSynchronizer() {
                    @Override
                    public void receiveResponse(String receivedResponse) {
                        new LogString("sender1", logString).call("receiveResponse", receivedResponse);
                    }
                }, false);
            }
        });

        story.record().addAction(new AgentContainerFixture.Runnable() {
            public void run() throws Exception {
                agent.putO2AObject(new RequestSynchronizer() {
                    @Override
                    public void receiveResponse(String receivedResponse) {
                        new LogString("sender2", logString).call("receiveResponse", receivedResponse);
                    }
                }, false);
            }
        });

        story.record().addAssert(log(logString, "sender1.receiveResponse(resultsForSender1), "
                                                + "sender2.receiveResponse(resultsForSender2)"));

        story.execute();
    }


    @Test
    public void test_instituteAmbassador_failure() throws Exception {
        story.record().startTester(InstituteAmbassadorProtocol.SECRETARY_GENERAL_AGENT_NAME)
              .receiveMessage(matchInstituteAmbassadorInitialMessage())
              .replyWithContent(Performative.FAILURE,
                                new RuntimeException("Error while creating ambassador"));
        story.record().assertContainsAgent(InstituteAmbassadorProtocol.SECRETARY_GENERAL_AGENT_NAME);

        story.record().startAgent("PresidentAgent", agent);

        story.record().assertNotContainsAgent("PresidentAgent");

        story.execute();
    }


    public void test_tearDownHandler() throws Exception {
        agent.setPresidentListener(new PresidentListener() {
            public void stopped() {
                logString.call("stopped");
            }
        });

        agent.tearDown();

        logString.assertContent("stopped()");
    }


    public void test_tearDownHandlerTwice() throws Exception {
        agent.setPresidentListener(new PresidentListener() {
            public void stopped() {
                logString.call("stopped");
            }
        });

        agent.tearDown();
        agent.tearDown();

        logString.assertContent("stopped()");
    }


    public void test_tearDownHandler_noHandler() throws Exception {
        agent.tearDown();
        logString.assertContent("");
    }


    private MessageTemplate matchInstituteAmbassadorInitialMessage() {
        return and(and(and(matchProtocol(InstituteAmbassadorProtocol.ID),
                           matchPerformative(Performative.REQUEST)),
                       matchLanguage(AclMessage.OBJECT_LANGUAGE)),
                   matchWith(new MatchExpression() {
                       public boolean match(AclMessage aclMessage) {
                           return aclMessage.decodeUserId().equals(userId);
                       }
                   }));
    }
}
