package net.codjo.mad.client.plugin;
import net.codjo.agent.AgentContainer;
import net.codjo.agent.AgentController;
import net.codjo.agent.BadControllerException;
import net.codjo.agent.ContainerConfiguration;
import net.codjo.agent.UserId;
import net.codjo.agent.util.IdUtil;
import net.codjo.mad.client.request.Request;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.client.request.RequestSender;
import net.codjo.mad.client.request.Result;
import net.codjo.mad.client.request.ResultManager;
import net.codjo.mad.client.request.util.ServerWrapper;
import net.codjo.mad.client.request.util.ServerWrapperFactory;
import net.codjo.plugin.common.AbstractApplicationPlugin;
import net.codjo.plugin.common.ApplicationCore;
import net.codjo.security.client.plugin.SecurityClientPluginConfiguration;
import net.codjo.util.system.EventSynchronizer;
import java.net.InetAddress;
import java.net.UnknownHostException;
/**
 * Plugin MAD permettant de connecter un client.
 */
public class MadConnectionPlugin extends AbstractApplicationPlugin {
    /**
     * @deprecated Use {@link net.codjo.security.client.plugin.SecurityClientPluginConfiguration#LOGIN_PARAMETER}
     */
    @Deprecated
    public static final String LOGIN_PARAMETER = SecurityClientPluginConfiguration.LOGIN_PARAMETER;
    /**
     * @deprecated Use {@link net.codjo.security.client.plugin.SecurityClientPluginConfiguration#PASSWORD_PARAMETER}
     */
    @Deprecated
    public static final String PASSWORD_PARAMETER = SecurityClientPluginConfiguration.PASSWORD_PARAMETER;
    /**
     * @deprecated Use {@link net.codjo.security.client.plugin.SecurityClientPluginConfiguration#LDAP_PARAMETER}
     */
    @Deprecated
    public static final String LDAP_PARAMETER = SecurityClientPluginConfiguration.LDAP_PARAMETER;

    private final PresidentListener killListener;
    private final ApplicationCore applicationCore;
    private final String presidentNamePattern;
    private final MadConnectionConfiguration configuration = new MadConnectionConfigurationImpl();
    private final MadConnectionOperations operations = new MadConnectionOperationsImpl(configuration);

    private String login;
    private AgentController guiAgent;
    private ServerWrapper serverWrapper;
    private int institueResponseTimeout = -1;


    public MadConnectionPlugin(ApplicationCore applicationCore) {
        this(applicationCore, new DeathPresidentListener(applicationCore), createPresidentName());
    }


    MadConnectionPlugin(ApplicationCore applicationCore,
                        PresidentListener presidentListener,
                        String presidentNamePattern) {
        killListener = presidentListener;
        this.applicationCore = applicationCore;
        this.presidentNamePattern = presidentNamePattern;
    }


    @Override
    public void initContainer(ContainerConfiguration containerConfiguration) throws Exception {
        login = containerConfiguration.getParameter(SecurityClientPluginConfiguration.LOGIN_PARAMETER);
        setServerWrapper(new AgentServerWrapper());
    }


    @Override
    public void start(AgentContainer agentContainer) throws Exception {
        EventSynchronizer<InstitueEvent> synchroniser = new EventSynchronizer<InstitueEvent>();
        if (institueResponseTimeout != -1) {
            synchroniser.setTimeout(institueResponseTimeout);
        }

        PresidentAgent presidentAgent = new PresidentAgent(applicationCore.getGlobalComponent(UserId.class),
                                                           synchroniser);
        presidentAgent.setPresidentListener(killListener);
        guiAgent = agentContainer.acceptNewAgent(String.format(presidentNamePattern, login), presidentAgent);
        guiAgent.start();

        InstitueEvent event = synchroniser.waitEvent();

        if (event == null) {
            throw new RuntimeException("Le serveur ne semble pas répondre");
        }
        if (event.hasFailed()) {
            throw new Exception("Erreur interne grave (" + event.getErrorMessage() + ")");
        }
    }


    @Override
    public void stop() throws Exception {
        if (guiAgent != null) {
            guiAgent.kill();
        }
    }


    @Deprecated
    public UserId getUserId() {
        return applicationCore.getGlobalComponent(UserId.class);
    }


    public MadConnectionOperations getOperations() {
        return operations;
    }


    public MadConnectionConfiguration getConfiguration() {
        return configuration;
    }


    protected void setServerWrapper(ServerWrapper serverWrapper) {
        this.serverWrapper = serverWrapper;
        ServerWrapperFactory.setPrototype(serverWrapper);
    }


    private static String createPresidentName() {
        return "President-%s-" + getComputerName() + IdUtil.createUniqueId(MadConnectionPlugin.class);
    }


    static String getComputerName() {
        try {
            return InetAddress.getLocalHost().getCanonicalHostName();
        }
        catch (UnknownHostException e) {
            return "Unknown";
        }
    }


    void setInstitueResponseTimeout(int institueResponseTimeout) {
        this.institueResponseTimeout = institueResponseTimeout;
    }


    private static class MadConnectionConfigurationImpl implements MadConnectionConfiguration {
        private long timeout = DEFAULT_TIME_OUT;


        public long getTimeout() {
            return timeout;
        }


        public void setTimeout(long timeout) {
            this.timeout = timeout;
        }
    }

    private class MadConnectionOperationsImpl implements MadConnectionOperations {
        private MadConnectionConfiguration configuration;


        MadConnectionOperationsImpl(MadConnectionConfiguration configuration) {
            this.configuration = configuration;
        }


        public Result sendRequest(Request request)
              throws RequestException {
            ResultManager manager = sendRequests(new Request[]{request}, configuration.getTimeout());
            return manager.getResult(request.getRequestId());
        }


        public Result sendRequest(Request request, long timeout) throws RequestException {
            ResultManager manager = sendRequests(new Request[]{request}, timeout);
            return manager.getResult(request.getRequestId());
        }


        public ResultManager sendRequests(Request[] requests) throws RequestException {
            return sendRequests(requests, configuration.getTimeout());
        }


        public ResultManager sendRequests(Request[] request, long timeout) throws RequestException {
            ResultManager manager = new RequestSender(timeout).send(request, serverWrapper);
            if (manager.hasError()) {
                throw new RequestException(manager.getErrorResult());
            }
            return manager;
        }
    }

    private class AgentServerWrapper implements ServerWrapper {
        public ServerWrapper copy() {
            return this;
        }


        public void close() {
        }


        public String sendWaitResponse(String text, long timeout) {
            try {
                return new RequestSynchronizer(guiAgent).sendRequest(text, timeout);
            }
            catch (BadControllerException e) {
                throw new InternalError(e.getLocalizedMessage());
            }
            catch (InterruptedException e) {
                throw new InternalError(e.getLocalizedMessage());
            }
        }
    }
}
