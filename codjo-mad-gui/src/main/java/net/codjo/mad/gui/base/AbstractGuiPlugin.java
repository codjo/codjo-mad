package net.codjo.mad.gui.base;
import net.codjo.agent.AgentContainer;
import net.codjo.agent.ContainerConfiguration;
/**
 *
 */
public abstract class AbstractGuiPlugin implements GuiPlugin {
    public void initContainer(ContainerConfiguration configuration) throws Exception {
    }


    public void start(AgentContainer agentContainer) throws Exception {
    }


    public void stop() throws Exception {
    }
}
