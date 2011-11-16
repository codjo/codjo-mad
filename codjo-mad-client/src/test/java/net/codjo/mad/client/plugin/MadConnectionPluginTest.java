package net.codjo.mad.client.plugin;
import net.codjo.agent.AclMessage;
import net.codjo.agent.AclMessage.Performative;
import net.codjo.agent.Agent;
import net.codjo.agent.AgentContainerMock;
import net.codjo.agent.Aid;
import net.codjo.agent.ContainerConfiguration;
import static net.codjo.agent.MessageTemplate.and;
import static net.codjo.agent.MessageTemplate.matchPerformative;
import static net.codjo.agent.MessageTemplate.matchSender;
import net.codjo.agent.UserId;
import static net.codjo.agent.test.AgentAssert.log;
import net.codjo.agent.test.AgentContainerFixture;
import net.codjo.agent.test.Semaphore;
import net.codjo.agent.test.Story;
import net.codjo.agent.test.SubStep;
import net.codjo.agent.test.TesterAgent;
import static net.codjo.mad.client.plugin.MadConnectionPlugin.getComputerName;
import net.codjo.mad.client.request.CommandRequest;
import net.codjo.mad.client.request.MadServerFixture;
import net.codjo.mad.client.request.Request;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.client.request.Result;
import net.codjo.mad.client.request.ResultManager;
import net.codjo.mad.client.request.util.ServerWrapper;
import net.codjo.mad.client.request.util.ServerWrapperFactory;
import net.codjo.mad.common.ZipUtil;
import net.codjo.mad.common.message.InstituteAmbassadorProtocol;
import net.codjo.plugin.common.ApplicationCoreMock;
import net.codjo.security.client.plugin.SecurityClientPluginConfiguration;
import net.codjo.test.common.LogString;
import net.codjo.test.common.fixture.CompositeFixture;
import net.codjo.test.common.fixture.SystemExitFixture;
import java.util.regex.Pattern;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.fail;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class MadConnectionPluginTest {
    private static final SystemExitFixture exitFixture = new SystemExitFixture();
    public static final String LOGIN = "mylogin";
    public static final String PASSWORD = "mypassword";
    private UserId userID = UserId.createId(LOGIN, PASSWORD);
    private MadConnectionPlugin plugin;
    private LogString log = new LogString();
    private AgentContainerFixture fixture = new AgentContainerFixture();
    private MadServerFixture madServerFixture = new MadServerFixture();
    private CompositeFixture compositeFixture = new CompositeFixture(fixture, madServerFixture);
    private ApplicationCoreMock applicationCoreMock;
    private static final String AMBASSADOR = "ambassador";


    @BeforeClass
    public static void globalSetUp() {
        exitFixture.doSetUp();
    }


    @AfterClass
    public static void globalTearDown() {
        exitFixture.doTearDown();
    }


    @Before
    public void setUp() throws Exception {
        compositeFixture.doSetUp();
        applicationCoreMock = new ApplicationCoreMock(log);
        applicationCoreMock.addGlobalComponent(UserId.class, userID);
        log.clear();
        plugin = new MadConnectionPlugin(applicationCoreMock);
        plugin.setServerWrapper(madServerFixture.getServerWrapper());
    }


    @After
    public void tearDown() throws Exception {
        compositeFixture.doTearDown();
    }


    @Test
    public void test_start_timeout() throws Exception {
        initPlugin(LOGIN);
        plugin.setInstitueResponseTimeout(1);

        try {
            plugin.start(new AgentContainerMock(log));
        }
        catch (Exception e) {
            assertEquals("Le serveur ne semble pas répondre", e.getMessage());
        }

        String presidentName = String.format("President-%s-%s.*", LOGIN, getComputerName());
        log.assertContent(
              Pattern.compile(String.format("acceptNewAgent\\(%1$s\\), %1$s\\.start\\(\\)", presidentName)));
    }


    @Test
    public void test_presidentListener_override() throws Exception {
        final Semaphore semaphore = new Semaphore();

        fixture.startNewAgent(InstituteAmbassadorProtocol.SECRETARY_GENERAL_AGENT_NAME,
                              createSecretaryGeneral(""));

        PresidentListener killListener =
              new PresidentListener() {
                  public void stopped() {
                      log.call("stopped");
                      semaphore.release();
                  }
              };

        plugin = new MadConnectionPlugin(applicationCoreMock, killListener, "MyPresidentAgent-%s");
        initPlugin(LOGIN);
        plugin.start(fixture.getContainer());

        String presidentAgentName = String.format("MyPresidentAgent-%s", LOGIN);
        fixture.assertContainsAgent(presidentAgentName);

        fixture.getContainer().getAgent(presidentAgentName).kill();
        fixture.waitForAgentDeath(presidentAgentName);

        semaphore.acquire();

        log.assertContent("stopped()");
    }


    @Test
    public void test_presidentListener_default() throws Exception {
        fixture.startNewAgent(InstituteAmbassadorProtocol.SECRETARY_GENERAL_AGENT_NAME,
                              createSecretaryGeneral(""));

        plugin = new MadConnectionPlugin(applicationCoreMock,
                                         new DeathPresidentListener(applicationCoreMock),
                                         "MyPresidentAgent-%s");
        initPlugin(LOGIN);
        plugin.start(fixture.getContainer());

        String presidentAgentName = String.format("MyPresidentAgent-%s", LOGIN);
        fixture.assertContainsAgent(presidentAgentName);

        fixture.getContainer().getAgent(presidentAgentName).kill();
        fixture.waitForAgentDeath(presidentAgentName);

        fixture.assertUntilOk(log(log, "stop()"));
        assertEquals(DeathPresidentListener.INTERNAL_ERROR_PRESIDENT_KILLED,
                     exitFixture.getFirstExitValue());
    }


    @Test
    public void test_serverWrapper() throws Exception {
        Story story = new Story(fixture);

        story.record()
              .startTester(InstituteAmbassadorProtocol.SECRETARY_GENERAL_AGENT_NAME)
              .receiveMessage(and(matchPerformative(Performative.REQUEST),
                                  matchSender(new Aid(String.format("president_%s", LOGIN)))))
              .replyWith(Performative.INFORM, AMBASSADOR);

        story.record().startTester(AMBASSADOR)
              .receiveMessage(matchSender(new Aid(String.format("president_%s", LOGIN))))
              .replyWithByteSequence(Performative.INFORM, ZipUtil.zip("reply"));

        story.record().addAction(new net.codjo.agent.test.AgentContainerFixture.Runnable() {
            public void run() throws Exception {
                plugin = new MadConnectionPlugin(applicationCoreMock,
                                                 new PresidentListener() {
                                                     public void stopped() {
                                                         log.call("stopped");
                                                     }
                                                 },
                                                 "president_%s");

                initPlugin(LOGIN);
                plugin.start(fixture.getContainer());
            }
        });
        story.record().assertContainsAgent(String.format("president_%s", LOGIN));

        story.record().addAction(new net.codjo.agent.test.AgentContainerFixture.Runnable() {
            public void run() throws Exception {
                ServerWrapper wrapper = ServerWrapperFactory.createWrapper();
                String result = wrapper.sendWaitResponse("request", 5000);
                log.call("received", result);
            }
        });

        story.record().addAction(new net.codjo.agent.test.AgentContainerFixture.Runnable() {
            public void run() throws Exception {
                plugin.stop();
            }
        });

        story.record().addAssert(log(log, "received(reply), stopped()"));

        story.execute();
    }


    @Test
    public void test_ambassadorInstitutionFailure() throws Exception {
        Story story = new Story(fixture);

        story.record()
              .startTester(InstituteAmbassadorProtocol.SECRETARY_GENERAL_AGENT_NAME)
              .receiveMessage(and(matchPerformative(Performative.REQUEST),
                                  matchSender(new Aid(String.format("president_%s", LOGIN)))))
              .replyWithContent(Performative.FAILURE, new RuntimeException("runtime error"));

        story.record().addAction(new net.codjo.agent.test.AgentContainerFixture.Runnable() {
            public void run() throws Exception {
                plugin = new MadConnectionPlugin(applicationCoreMock,
                                                 new PresidentListener() {
                                                     public void stopped() {
                                                         log.call("stopped");
                                                     }
                                                 },
                                                 "president_%s");

                initPlugin(LOGIN);
                try {
                    plugin.start(fixture.getContainer());
                }
                catch (Exception e) {
                    log.info("failure " + e.getLocalizedMessage());
                }
            }
        });

        story.record().addAssert(log(log, "stopped(), failure Erreur interne grave (runtime error)"));

        story.execute();
    }


    @Test
    public void test_operation_sendRequest() throws Exception {
        Result result = MadServerFixture.createResult(new String[]{"id"}, new String[][]{{"0"}, {"1"}});

        madServerFixture.mockServerResult(result);

        Result actual = plugin.getOperations().sendRequest(new CommandRequest("doIt"));

        madServerFixture.assertResult(result, actual);
    }


    @Test
    public void test_operation_sendRequestTimeout() throws Exception {
        madServerFixture.setServerMock(new LogTimeoutStrategy(log));

        plugin.getOperations().sendRequest(new CommandRequest(), 10);

        log.assertContent("createResults(10)");
    }


    @Test
    public void test_operation_sendRequestGlobalTimeout() throws Exception {
        madServerFixture.setServerMock(new LogTimeoutStrategy(log));

        plugin.getConfiguration().setTimeout(10);

        plugin.getOperations().sendRequest(new CommandRequest());

        log.assertContent("createResults(10)");
    }


    @Test
    public void test_operation_sendRequestError() throws Exception {
        madServerFixture.mockServerError("Error");
        try {
            plugin.getOperations().sendRequest(new CommandRequest("doIt"));
            fail();
        }
        catch (RequestException ex) {
            assertEquals("Error", ex.getLocalizedMessage());
        }
    }


    @Test
    public void test_operation_sendMutlipleRequest() throws Exception {
        Result result1 = MadServerFixture.createResult(new String[]{"id"}, new String[][]{{"00"}, {"01"}});
        Result result2 = MadServerFixture.createResult(new String[]{"id"}, new String[][]{{"10"}, {"11"}});

        madServerFixture.mockServerResult(new Result[]{result1, result2});

        ResultManager resultManager =
              plugin.getOperations().sendRequests(new Request[]{new CommandRequest("first"),
                                                                new CommandRequest("second")});

        assertEquals(2, resultManager.getResultsCount());
    }


    @Test
    public void test_operation_sendMultipleRequestError() throws Exception {
        madServerFixture.mockServerError("Error");
        try {
            plugin.getOperations().sendRequests(new Request[]{new CommandRequest("first"),
                                                              new CommandRequest("second")});
            fail();
        }
        catch (RequestException ex) {
            assertEquals("Error", ex.getLocalizedMessage());
        }
    }


    @Test
    public void test_configuration_timeout() throws Exception {
        MadConnectionConfiguration configuration = plugin.getConfiguration();
        assertNotNull(configuration);
        assertEquals(MadConnectionConfiguration.DEFAULT_TIME_OUT, configuration.getTimeout());

        configuration.setTimeout(10);
        assertEquals(10, configuration.getTimeout());
    }


    private TesterAgent createSecretaryGeneral(String ambassadorName) {
        TesterAgent agent = new TesterAgent();
        agent.record()
              .receiveMessage(matchPerformative(Performative.REQUEST))
              .add(new SubStep() {
                  public void run(Agent agent, AclMessage message) {
                  }
              }).replyWith(Performative.INFORM, ambassadorName);
        return agent;
    }


    private void initPlugin(String login) throws Exception {
        ContainerConfiguration containerConfiguration = new ContainerConfiguration();
        containerConfiguration.setParameter(SecurityClientPluginConfiguration.LOGIN_PARAMETER, login);
        plugin.initContainer(containerConfiguration);
    }


    private static class LogTimeoutStrategy implements MadServerFixture.ServerMock {
        private LogString logString;


        LogTimeoutStrategy(LogString log) {
            logString = log;
        }


        public Result[] createResults(String[] requestIds, String xmlRequest, long timeout) {
            logString.call("createResults", timeout);
            return null;
        }
    }
}
