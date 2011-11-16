package net.codjo.mad.server.plugin;
import net.codjo.aspect.AspectManager;
import net.codjo.mad.server.handler.AspectBranchLauncherFactory;
import net.codjo.mad.server.handler.HandlerListener;
import net.codjo.mad.server.handler.HandlerMapBuilder;
import net.codjo.mad.server.plugin.HandlerExecutor.HandlerExecutionMode;
import net.codjo.plugin.common.session.SessionManager;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import org.exolab.castor.jdo.JDO;
/**
 *
 */
public class BackPack {
    private JDO jdo;
    private HandlerMapBuilder handlerMapBuilder;
    private List<HandlerListener> handlerListeners = Collections.emptyList();
    private AspectBranchLauncherFactory aspectBranchLauncherFactory;
    private SessionManager sessionManager;
    private AspectManager aspectManager;
    private HandlerExecutorFactory handlerExecutorFactory;
    private HandlerExecutionMode handlerExecutionMode;


    BackPack() {
        sessionManager = new SessionManager();
        aspectManager = new AspectManager();
        jdo = new JDO("DirectUseDB");
        jdo.setClassLoader(getClass().getClassLoader());
    }


    void setCastorConfig(URL castorConfig) {
        jdo.setConfiguration(castorConfig.toString());
    }


    public JDO getJdo() {
        return jdo;
    }


    public HandlerMapBuilder getHandlerMapBuilder() {
        return handlerMapBuilder;
    }


    void setHandlerMapBuilder(HandlerMapBuilder handlerMapBuilder) {
        this.handlerMapBuilder = handlerMapBuilder;
    }


    public List<HandlerListener> getHandlerListeners() {
        return handlerListeners;
    }


    void setHandlerListeners(List<HandlerListener> handlerListeners) {
        this.handlerListeners = handlerListeners;
    }


    public AspectBranchLauncherFactory getAspectBranchLauncherFactory() {
        return aspectBranchLauncherFactory;
    }


    void setAspectBranchLauncherFactory(AspectBranchLauncherFactory aspectBranchLauncherFactory) {
        this.aspectBranchLauncherFactory = aspectBranchLauncherFactory;
    }


    void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }


    public SessionManager getSessionManager() {
        return sessionManager;
    }


    void setAspectManager(AspectManager aspectManager) {
        this.aspectManager = aspectManager;
    }


    public AspectManager getAspectManager() {
        return aspectManager;
    }


    public HandlerExecutorFactory getHandlerExecutorFactory() {
        return handlerExecutorFactory;
    }


    public void setHandlerExecutorFactory(HandlerExecutorFactory handlerExecutorFactory) {
        this.handlerExecutorFactory = handlerExecutorFactory;
    }


    public HandlerExecutionMode getHandlerExecutionMode() {
        return handlerExecutionMode;
    }


    public void setHandlerExecutionMode(HandlerExecutionMode handlerExecutionMode) {
        this.handlerExecutionMode = handlerExecutionMode;
    }
}
