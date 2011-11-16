package net.codjo.mad.gui.base;
import net.codjo.mad.common.structure.StructureReader;
import net.codjo.mad.gui.framework.AbstractGuiAction;
import net.codjo.mad.gui.framework.LocalGuiContext;
import net.codjo.mad.gui.framework.MutableGuiContext;
import net.codjo.mad.gui.structure.StructureCache;
import net.codjo.plugin.common.ApplicationPlugin;
import java.util.List;
import javax.swing.Action;
import javax.swing.JMenuItem;
import org.apache.log4j.Logger;
import org.picocontainer.MutablePicoContainer;
/**
 *
 */
public class DefaultGuiConfiguration implements GuiConfiguration {
    private final Logger logger = Logger.getLogger(DefaultGuiConfiguration.class);
    private final MutablePicoContainer picoContainer;
    private final MutableGuiContext guiContext;
    private final LocalGuiContext menuContext;
    private final List<ComponentBuilder> statusBarComponentBuilders;
    private final StructureCache structureCache;


    public DefaultGuiConfiguration(MutablePicoContainer picoContainer,
                                   MutableGuiContext guiContext,
                                   LocalGuiContext menuContext) throws Exception {
        this(picoContainer, guiContext, menuContext, null);
    }


    public DefaultGuiConfiguration(MutablePicoContainer picoContainer,
                                   MutableGuiContext guiContext,
                                   LocalGuiContext menuContext,
                                   List<ComponentBuilder> statusBarComponentBuilders) throws Exception {
        this.picoContainer = picoContainer;
        this.guiContext = guiContext;
        this.menuContext = menuContext;
        this.statusBarComponentBuilders = statusBarComponentBuilders;
        structureCache = new StructureCache();

        picoContainer.registerComponentInstance(guiContext);
        guiContext.putProperty(StructureCache.STRUCTURE_CACHE, structureCache);
    }


    public StructureReader getStructureReader() {
        return structureCache.getStructureReader();
    }


    public MutableGuiContext getGuiContext() {
        return guiContext;
    }


    public void registerAction(ApplicationPlugin plugin, String actionId, Action action) {
        logger.info("register action '" + actionId + "' from '" + plugin + "' : " + action);
        JMenuItem item = new JMenuItem(action);
        menuContext.putProperty(plugin.getClass().getName() + "#" + actionId, item);
        menuContext.putProperty(plugin.getClass().getSimpleName() + "#" + actionId, item);
        if (action instanceof AbstractGuiAction) {
            ((AbstractGuiAction)action).setSecurityFunction(plugin.getClass().getName() + "#" + actionId);
            logger.info("action instanceof AbstractGuiAction => setSecurityFunction() : "
                        + plugin.getClass().getName() + "#" + actionId);
        }
    }


    public void registerAction(ApplicationPlugin plugin, String actionId, Class<? extends Action> action) {
        picoContainer.registerComponentImplementation(action);
        registerAction(plugin, actionId, (Action)picoContainer.getComponentInstanceOfType(action));
    }


    public void addToStatusBar(ComponentBuilder builder) {
        if (statusBarComponentBuilders == null) {
            throw new UnsupportedOperationException("Impossible d'enregister l'action !!!");
        }
        statusBarComponentBuilders.add(builder);
    }
}
