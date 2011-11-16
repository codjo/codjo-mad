package net.codjo.mad.gui.base;
import net.codjo.mad.gui.framework.AbstractGuiAction;
import net.codjo.mad.gui.framework.DefaultGuiContext;
import java.awt.event.ActionEvent;
/**
 *
 */
public class GuiActionMock extends AbstractGuiAction {

    public GuiActionMock(DefaultGuiContext guiContext) {
        super(guiContext, "MyGuiActionMock", null);
    }


    @Override
    public String getSecurityFunction() {
        return super.getSecurityFunction();
    }


    public void actionPerformed(ActionEvent event) {
    }
}
