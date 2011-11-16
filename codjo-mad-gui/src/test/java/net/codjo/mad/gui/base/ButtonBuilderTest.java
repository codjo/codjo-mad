package net.codjo.mad.gui.base;
import javax.swing.JButton;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
/**
 *
 */
public class ButtonBuilderTest {

    @Test
    public void test_build() throws Exception {
        JButton button = new ButtonBuilder("name of the button", new ActionMock()).build();

        assertEquals("name of the button", button.getName());
    }
}
