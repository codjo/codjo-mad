package net.codjo.mad.gui.framework;
/**
 * Interface designant un Contexte dont les proprietes peuvent changer.
 *
 * @version $Revision: 1.3 $
 */
public interface MutableGuiContext extends GuiContext {
    public void putProperty(Object propertyName, Object value);


    public void setSender(Sender sender);
}
