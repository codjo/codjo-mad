package net.codjo.mad.gui.base;
import net.codjo.plugin.common.ApplicationPlugin;
/**
 *
 */
public interface GuiPlugin extends ApplicationPlugin, net.codjo.plugin.gui.GuiPlugin<GuiConfiguration> {
    public static String AGENT_CONTAINER_KEY = "GuiContext.AGENT_CONTAINER_KEY";
}
