package net.codjo.mad.server.plugin;
import net.codjo.agent.Agent;
/**
 *
 */
public interface HandlerExecutor {

    public enum HandlerExecutionMode {
        SYNCHRONOUS,
        ASYNCHRONOUS;
    }


    void execute(String xmlRequests, HandlerExecutorCommand handlerExecutorCommand) throws Exception;


    interface HandlerExecutorCommand {
        void setResultSenderAgent(Agent agent);


        void execute() throws Exception;
    }
}
