package net.codjo.mad.gui.framework;
import java.awt.Dimension;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
public class SimpleListGuiTest {

    @Test
    public void test_constructor() throws Exception {
        SimpleListGui simpleListGui = new SimpleListGui("Test", new Dimension(800, 600));

        assertEquals("Test", simpleListGui.getTitle());
        assertEquals(800, simpleListGui.getPreferredSize().getWidth(), 0);
        assertEquals(600, simpleListGui.getPreferredSize().getHeight(), 0);
    }
}
