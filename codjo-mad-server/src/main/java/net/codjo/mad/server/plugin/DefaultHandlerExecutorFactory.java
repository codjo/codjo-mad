package net.codjo.mad.server.plugin;
import net.codjo.agent.UserId;
/**
 *
 */
class DefaultHandlerExecutorFactory implements HandlerExecutorFactory {

    public HandlerExecutor create(UserId userId) throws Exception {
        return new DefaultHandlerExecutor();
    }


    static class DefaultHandlerExecutor implements HandlerExecutor {
        public void execute(String xmlRequests, HandlerExecutorCommand handlerExecutorCommand)
              throws Exception {
            handlerExecutorCommand.execute();
        }
    }
}
