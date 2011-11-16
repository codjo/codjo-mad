package net.codjo.mad.gui.framework;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
/**
 *
 */
public class SimpleListPanelTest {
    private SimpleListPanel simpleListPanel = new SimpleListPanel();


    @Test
    public void test_getHeaderPanel() throws Exception {
        assertNotNull(simpleListPanel.getHeaderPanel());
    }
}
