package net.codjo.mad.client.plugin;
import net.codjo.agent.AgentContainer;
import net.codjo.agent.BadControllerException;
import net.codjo.agent.ContainerConfiguration;
import net.codjo.agent.ContainerFailureException;
import net.codjo.test.common.LogString;
/**
 * Classe Mock de {@link net.codjo.mad.client.plugin.MadConnectionPlugin}.
 */
public class MadConnectionPluginMock extends MadConnectionPlugin {
    private final MadConnectionOperationsMock operationsMock;


    public MadConnectionPluginMock() {
        super(null);
        operationsMock = new MadConnectionOperationsMock();
    }


    public MadConnectionPluginMock(LogString logString) {
        super(null);
        operationsMock = new MadConnectionOperationsMock(logString);
    }


    @Override
    public void initContainer(ContainerConfiguration configuration) {
    }


    @Override
    public void start(AgentContainer agentContainer)
          throws ContainerFailureException, InterruptedException {
    }


    @Override
    public void stop() throws BadControllerException {
    }


    @Override
    public MadConnectionOperations getOperations() {
        return operationsMock;
    }
}
