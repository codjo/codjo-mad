package net.codjo.mad.gui.request.action;
import net.codjo.mad.client.plugin.MadConnectionOperationsMock;
import net.codjo.mad.gui.MadGuiContext;
import net.codjo.mad.gui.framework.DefaultGuiContext;
import net.codjo.mad.gui.framework.Sender;
import net.codjo.mad.gui.request.Mock;
import net.codjo.mad.gui.request.RequestTable;
import net.codjo.security.common.api.UserMock;
import net.codjo.test.common.LogString;
import java.awt.Component;
import javax.swing.JButton;
import org.uispec4j.Button;
import org.uispec4j.Trigger;
import org.uispec4j.UISpecTestCase;
import org.uispec4j.Window;
import org.uispec4j.finder.ComponentMatcher;
import org.uispec4j.interception.WindowHandler;
import org.uispec4j.interception.WindowInterceptor;

public class DeleteActionTest extends UISpecTestCase {
    private DeleteAction deleteAction;
    private LogString logString;
    private Button testButton;


    public void test_confirmMessage() throws Exception {
        WindowInterceptor.init(new Trigger() {
            public void run() throws Exception {
                testButton.click();
            }
        }).process(new WindowHandler() {
            @Override
            public Trigger process(Window window) throws Exception {
                window.assertTitleEquals("Confirmation de suppression");
                return window.getButton(new LabelComponentMatcher("Oui")).triggerClick();
            }
        }).run();

        logString.assertContent("sendRequest()");
    }


    public void test_noConfirmMessage() throws Exception {
        deleteAction.setConfirmMessage(null);

        testButton.click();

        logString.assertContent("sendRequest()");
    }


    public void test_confirmMessageCancel() throws Exception {
        WindowInterceptor.init(new Trigger() {
            public void run() throws Exception {
                testButton.click();
            }
        }).process(new WindowHandler() {
            @Override
            public Trigger process(Window window) throws Exception {
                window.assertTitleEquals("Confirmation de suppression");
                return window.getButton(new LabelComponentMatcher("Non")).triggerClick();
            }
        }).run();


        assertTrue(logString.getContent().length() == 0);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        logString = new LogString();

        DefaultGuiContext guiContext = new MadGuiContext();
        guiContext.setUser(new UserMock());
        guiContext.setSender(new Sender(new MadConnectionOperationsMock(logString)));
        RequestTable table = new RequestTable(new Mock.ListDataSource());

        deleteAction = new DeleteAction(guiContext, table);

        JButton deleteButton = new JButton(deleteAction);
        deleteButton.setEnabled(true);
        testButton = new Button(deleteButton);
    }


    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }


    private static class LabelComponentMatcher implements ComponentMatcher {
        private String label;


        private LabelComponentMatcher(String label) {
            this.label = label;
        }


        public boolean matches(Component component) {
            boolean matches = false;
            if (component instanceof JButton) {
                matches = label.equals(((JButton)component).getText());
            }
            return matches;
        }
    }
}
