package net.codjo.mad.client.plugin;
import net.codjo.agent.AgentControllerMock;
import junit.framework.TestCase;
/**
 * Classe de test de {@link net.codjo.mad.client.plugin.RequestSynchronizer}.
 */
public class RequestSynchronizerTest extends TestCase {
    private RequestSynchronizer synchronizer;
    private AgentControllerMock agent;


    public void test_waitLoginEvent_eventReceivedBeforeMethodCall()
          throws Exception {
        String expected = "expected";

        synchronizer.receiveResponse(expected);

        String actual = synchronizer.sendRequest("myrequest", 1500);

        assertSame(expected, actual);

        agent.getLog().assertContent("putO2AObject(" + synchronizer + ")");
        assertEquals("myrequest", synchronizer.getRequest());
    }


    public void test_waitLoginEvent_wait() throws Exception {
        String expected = "expected";

        doReceivedInASeparateThread(expected, 500);

        String actual = synchronizer.sendRequest("re", 1500);

        assertSame(expected, actual);
        agent.getLog().assertContent("putO2AObject(" + synchronizer + ")");
    }


    @Override
    protected void setUp() throws Exception {
        agent = new AgentControllerMock();
        synchronizer = new RequestSynchronizer(agent);
    }


    private void doReceivedInASeparateThread(final String expected, final int millis) {
        final Runnable runnable =
              new Runnable() {
                  public void run() {
                      doWait(millis);
                      synchronizer.receiveResponse(expected);
                  }
              };
        new Thread(runnable).start();
    }


    private void doWait(final int millis) {
        try {
            Thread.sleep(millis);
        }
        catch (InterruptedException e) {
            ;
        }
    }
}
