package net.codjo.mad.gui.framework;
import net.codjo.security.common.api.User;
import net.codjo.security.common.api.UserMock;
import java.util.Observable;
import java.util.Observer;
import junit.framework.TestCase;
/**
 */
public class DefaultGuiContextTest extends TestCase {

    public void testSendEvent() {
        DefaultGuiContext ctxt = new DefaultGuiContext();

        FakeObserver fakeObserver = new FakeObserver();
        ctxt.addObserver(fakeObserver);

        ctxt.sendEvent(GuiEvent.QUIT);

        assertEquals(GuiEvent.QUIT, fakeObserver.eventReceived);
    }


    public void test_getProperty() {
        DefaultGuiContext ctxt = new DefaultGuiContext();

        try {
            ctxt.getProperty("NA");
            fail("La property NA n'existe pas");
        }
        catch (Exception ex) {
            ctxt.putProperty("test", "value");
            assertEquals("value", ctxt.getProperty("test"));
        }
    }


    public void test_hasProperty() {
        DefaultGuiContext ctxt = new DefaultGuiContext();

        assertTrue(!ctxt.hasProperty("NA"));

        ctxt.putProperty("test", "value");
        assertTrue(ctxt.hasProperty("test"));
    }


    public void test_getUser() throws Exception {
        DefaultGuiContext context = new DefaultGuiContext();
        User user = new UserMock();
        context.setUser(user);
        assertSame(user, context.getUser());
    }


    public void test_getUser_undefined() throws Exception {
        DefaultGuiContext context = new DefaultGuiContext();
        try {
            context.getUser();
            fail();
        }
        catch (IllegalStateException ex) {
            assertEquals(DefaultGuiContext.UNDEFINED_USER, ex.getLocalizedMessage());
        }
    }


    public void test_sender() throws Exception {
        DefaultGuiContext context = new DefaultGuiContext();
        assertNull(context.getSender());

        Sender sender = new Sender();
        context.setSender(sender);
        assertSame(sender, context.getSender());
    }


    public static class FakeObserver implements Observer {
        Object eventReceived;


        public void update(Observable observer, Object arg) {
            eventReceived = arg;
        }
    }
}
