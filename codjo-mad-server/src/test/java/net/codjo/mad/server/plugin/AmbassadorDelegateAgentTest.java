package net.codjo.mad.server.plugin;
import net.codjo.agent.AclMessage.Performative;
import net.codjo.agent.Agent;
import static net.codjo.agent.test.MessageBuilder.message;
import net.codjo.security.common.api.UserMock;
import net.codjo.sql.server.ConnectionPoolMock;
import org.exolab.castor.jdo.JDO;
import org.junit.Test;
/**
 *
 */
public class AmbassadorDelegateAgentTest extends AmbassadorTestCase {
    private HandlerExecutorMock handlerExecutor = new HandlerExecutorMock();


    @Test
    public void test_executeHandler() throws Exception {
        processorMock.mockProceed("<request><id>1</id></request>", "result1");

        story.record().startTester("president")
              .receiveMessage(matchRequest("result1", "delegate"));

        story.record().startAgent("delegate", new AmbassadorDelegateAgent(
              message(Performative.QUERY)
                    .withContent("<request><id>1</id></request>")
                    .to("delegate")
                    .replyTo("president").get(),
              handlerExecutor,
              processorMock,
              new ConnectionPoolMock(),
              new JDO(),
              new UserMock()));

        story.execute();
    }


    @Test
    public void test_executeHandler_setResultSenderAgent() throws Exception {
        processorMock.mockProceed("<request><id>1</id></request>", "result1");

        Agent handlerJobAgent = new Agent();
        handlerExecutor.mockSetResultSenderAgent(handlerJobAgent);

        story.record().startAgent("handlerJob", handlerJobAgent);

        story.record().startTester("president")
              .receiveMessage(matchRequest("result1", "handlerJob"));

        story.record().startAgent("delegate", new AmbassadorDelegateAgent(
              message(Performative.QUERY)
                    .withContent("<request><id>1</id></request>")
                    .to("delegate")
                    .replyTo("president").get(),
              handlerExecutor,
              processorMock,
              new ConnectionPoolMock(),
              new JDO(),
              new UserMock()));

        story.execute();
    }
}
