package net.codjo.mad.gui.base;
import net.codjo.test.common.LogString;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
/**
 *
 */
public class ActionMock extends AbstractAction {

    public ActionMock() {
    }


    public ActionMock(LogString log) {
        log.info("new ActionMock(LogString)");
    }


    public ActionMock(String name) {
        super(name);
    }


    public void actionPerformed(ActionEvent event) {
    }
}
