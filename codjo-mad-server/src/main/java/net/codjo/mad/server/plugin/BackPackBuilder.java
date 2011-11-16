package net.codjo.mad.server.plugin;
import net.codjo.aspect.AspectManager;
import net.codjo.mad.server.handler.AspectBranchLauncherFactory;
import net.codjo.mad.server.handler.HandlerListener;
import net.codjo.mad.server.handler.HandlerMapBuilder;
import net.codjo.mad.server.plugin.HandlerExecutor.HandlerExecutionMode;
import net.codjo.plugin.common.session.SessionManager;
import java.net.URL;
import java.util.List;
/**
 *
 */
public class BackPackBuilder {
    private BackPack backPack = new BackPack();


    private BackPackBuilder() {
    }


    public static BackPackBuilder init() {
        return new BackPackBuilder();
    }


    public BackPack get() {
        return backPack;
    }


    public BackPackBuilder setHandlerListeners(List<HandlerListener> listeners) {
        backPack.setHandlerListeners(listeners);
        return this;
    }


    public BackPackBuilder setAspectManager(AspectManager aspectManager) {
        backPack.setAspectManager(aspectManager);
        return this;
    }


    public BackPackBuilder setHandlerMapBuilder(HandlerMapBuilder handlerMapBuilder) {
        backPack.setHandlerMapBuilder(handlerMapBuilder);
        return this;
    }


    public BackPackBuilder setCastorConfig(URL castorConfig) {
        backPack.setCastorConfig(castorConfig);
        return this;
    }


    public BackPackBuilder setAspectBranchLauncherFactory(AspectBranchLauncherFactory factory) {
        backPack.setAspectBranchLauncherFactory(factory);
        return this;
    }


    public BackPackBuilder setSessionManager(SessionManager sessionManager) {
        backPack.setSessionManager(sessionManager);
        return this;
    }


    public BackPackBuilder setHandlerExecutorFactory(HandlerExecutorFactory factory) {
        backPack.setHandlerExecutorFactory(factory);
        return this;
    }


    public BackPackBuilder setHandlerExecutionMode(HandlerExecutionMode handlerExecutionMode) {
        backPack.setHandlerExecutionMode(handlerExecutionMode);
        return this;
    }
}
