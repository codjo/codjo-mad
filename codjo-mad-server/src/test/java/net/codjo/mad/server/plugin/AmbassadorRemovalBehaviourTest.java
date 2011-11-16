/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.mad.server.plugin;
import net.codjo.agent.Agent;
import net.codjo.agent.Aid;
import net.codjo.agent.UserId;
import net.codjo.agent.test.AgentContainerFixture;
import net.codjo.agent.test.DummyAgent;
import net.codjo.agent.test.Story;
import net.codjo.plugin.common.session.SessionListenerMock;
import net.codjo.plugin.common.session.SessionManager;
import net.codjo.test.common.LogString;
import junit.framework.TestCase;
/**
 * Classe de test de {@link AmbassadorRemovalBehaviour}.
 */
public class AmbassadorRemovalBehaviourTest extends TestCase {
    private Story story = new Story();
    private LogString log = new LogString();
    private AmbassadorRemovalBehaviour removalBehaviour;
    private SessionManager sessionManager = new SessionManager();
    private final UserId userId = UserId.decodeUserId("smith/6e594b55386d41376c37593d/5/1");


    public void test_isKnownPresidentAID() throws Exception {
        removalBehaviour.declare(new Aid("president"), new Aid("Ambassador"), userId);
        removalBehaviour.declare(new Aid("president1"), new Aid("Ambassador1"), UserId.createId("l", "p"));

        assertTrue(removalBehaviour.isKnownPresidentAID(new Aid("president")));
        assertTrue(removalBehaviour.isKnownPresidentAID(new Aid("president1")));
        assertFalse(removalBehaviour.isKnownPresidentAID(new Aid("unknwown")));
    }


    public void test_kill() throws Exception {
        sessionManager.addListener(new SessionListenerMock(new LogString("listener", log)));
        removalBehaviour.declare(new Aid("President"), new Aid("Ambassador"), userId);

        story.record().startAgent("SG", createSecretaryGeneralAgent(removalBehaviour));
        story.record().assertContainsAgent("SG");

        story.record().startAgent("President", createAgent());
        story.record().assertContainsAgent("President");

        story.record().startAgent("Ambassador", createAgent());
        story.record().assertContainsAgent("Ambassador");

        story.record().addAction(killAgent("President"));
        story.record().assertNotContainsAgent("President");

        story.record().assertNotContainsAgent("Ambassador");

        story.execute();

        assertFalse(removalBehaviour.isKnownPresidentAID(new Aid("president")));
    }


    public void test_kill_unknown() throws Exception {
        removalBehaviour.killAmbassadorOf(new Aid("TesKiToa"));
    }


    public void test_kill_ambassadorAlreadyDied() throws Exception {
        sessionManager.addListener(new SessionListenerMock(new LogString("listener", log)));
        removalBehaviour.declare(new Aid("President"), new Aid("Ambassador"), userId);

        story.record().startAgent("SG", createSecretaryGeneralAgent(removalBehaviour));
        story.record().assertContainsAgent("SG");

        story.record().startAgent("President", createAgent());
        story.record().assertContainsAgent("President");

        story.record().addAction(killAgent("President"));
        story.record().assertNotContainsAgent("President");

        story.record().assertNotContainsAgent("kamikaze-of-Ambassador");

        story.execute();

        assertFalse(removalBehaviour.isKnownPresidentAID(new Aid("president")));
    }


    private AgentContainerFixture.Runnable killAgent(final String agentName) {
        return new AgentContainerFixture.Runnable() {
            public void run() throws Exception {
                Thread.sleep(100);
                story.getAgentContainerFixture().killAgent(agentName);
            }
        };
    }


    @Override
    protected void setUp() throws Exception {
        removalBehaviour = new AmbassadorRemovalBehaviour();
        story.doSetUp();
    }


    @Override
    protected void tearDown() throws Exception {
        story.doTearDown();
    }


    private DummyAgent createAgent() {
        return new DummyAgent();
    }


    private Agent createSecretaryGeneralAgent(AmbassadorRemovalBehaviour behaviour) {
        return new DummyAgent(behaviour);
    }
}
