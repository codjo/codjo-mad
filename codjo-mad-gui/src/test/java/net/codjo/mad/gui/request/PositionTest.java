package net.codjo.mad.gui.request;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import org.junit.Test;

public class PositionTest {

    @Test
    public void test_left() throws Exception {
        Position position = Position.left("toto");

        assertEquals("toto", position.getActionName());
        assertSame(Position.Type.LEFT, position.getType());
    }


    @Test
    public void test_right() throws Exception {
        Position position = Position.right("toto");

        assertEquals("toto", position.getActionName());
        assertSame(Position.Type.RIGHT, position.getType());
    }


    @Test
    public void test_last() throws Exception {
        Position position = Position.last();

        assertNull(position.getActionName());
        assertSame(Position.Type.LAST, position.getType());
    }
}
