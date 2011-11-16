package net.codjo.mad.gui.base;
import net.codjo.mad.common.structure.StructureReader;
import net.codjo.mad.gui.framework.MutableGuiContext;
/**
 *
 */
public interface GuiConfiguration extends net.codjo.plugin.gui.GuiConfiguration {
    StructureReader getStructureReader();


    MutableGuiContext getGuiContext();


    void addToStatusBar(ComponentBuilder builder);
}
