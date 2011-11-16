package net.codjo.mad.server.plugin;
import net.codjo.agent.Agent;
import java.util.ArrayList;
import java.util.List;
/**
 *
 */
public class HandlerExecutorMock implements HandlerExecutor {
    private final List<String> blocked = new ArrayList<String>();
    private Agent resultSenderAgent;


    public void execute(String xmlRequests, HandlerExecutorCommand handlerExecutorCommand) throws Exception {
        if (blocked.contains(xmlRequests)) {
            long start = System.currentTimeMillis();
            while (System.currentTimeMillis() - start < 500) {
            }
        }
        if (resultSenderAgent != null) {
            handlerExecutorCommand.setResultSenderAgent(resultSenderAgent);
        }
        handlerExecutorCommand.execute();
    }


    public HandlerExecutorMock mockBlockExecution(String request) {
        blocked.add(request);
        return this;
    }


    public HandlerExecutorMock mockSetResultSenderAgent(Agent agent) {
        resultSenderAgent = agent;
        return this;
    }
}
