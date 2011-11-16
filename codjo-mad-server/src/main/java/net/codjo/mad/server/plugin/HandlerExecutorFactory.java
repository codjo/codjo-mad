package net.codjo.mad.server.plugin;
import net.codjo.agent.UserId;
/**
 *
 */
public interface HandlerExecutorFactory {

    HandlerExecutor create(UserId userId) throws Exception;
}
