package net.codjo.mad.gui.framework;
import net.codjo.security.common.api.UserMock;
import junit.framework.TestCase;
/**
 */
public class LocalGuiContextTest extends TestCase {

    public void test_getProperty() {
        DefaultGuiContext guiCtxt = new DefaultGuiContext();
        guiCtxt.putProperty("essai", "val");
        guiCtxt.putProperty("toto", "totoVal");

        LocalGuiContext localguicontext = new LocalGuiContext(guiCtxt);

        assertEquals("val", localguicontext.getProperty("essai"));
        assertEquals("totoVal", localguicontext.getProperty("toto"));

        localguicontext.putProperty("toto", "override");

        assertEquals("val", localguicontext.getProperty("essai"));
        assertEquals("override", localguicontext.getProperty("toto"));
        assertEquals("totoVal", guiCtxt.getProperty("toto"));
    }


    public void test_getUser() throws Exception {
        DefaultGuiContext guiCtxt = new DefaultGuiContext();
        LocalGuiContext localguicontext = new LocalGuiContext(guiCtxt);

        try {
            localguicontext.getUser();
            fail();
        }
        catch (IllegalStateException ex) {
            assertEquals(DefaultGuiContext.UNDEFINED_USER, ex.getLocalizedMessage());
        }
        guiCtxt.setUser(new UserMock());
        assertSame(guiCtxt.getUser(), localguicontext.getUser());
    }


    public void test_getSender() throws Exception {
        DefaultGuiContext guiCtxt = new DefaultGuiContext();
        LocalGuiContext localguicontext = new LocalGuiContext(guiCtxt);

        assertNull(localguicontext.getSender());

        guiCtxt.setSender(new Sender());
        assertSame(guiCtxt.getSender(), localguicontext.getSender());
    }


    public void test_getSender_local() throws Exception {
        DefaultGuiContext guiCtxt = new DefaultGuiContext();
        guiCtxt.setSender(new Sender());

        LocalGuiContext localguicontext = new LocalGuiContext(guiCtxt);
        Sender localSender = new Sender();
        localguicontext.setSender(localSender);

        assertSame(localSender, localguicontext.getSender());
        assertNotSame(guiCtxt.getSender(), localguicontext.getSender());
    }
}
