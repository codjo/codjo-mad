package net.codjo.mad.client.request;
import java.util.Collections;
import junit.framework.TestCase;
/**
 *
 */
public class ResultTest extends TestCase {
    private FieldsList primaryKeys = new FieldsList("myid", "1");


    public void test_getRows_badIndexFail() {
        try {
            new Result(primaryKeys, new Row()).getRow(5);
            fail();
        }
        catch (IndexOutOfBoundsException ex) {
            assertEquals("Index: 5, Size: 1", ex.getMessage());
        }
        try {
            new Result().getRow(1);
            fail();
        }
        catch (IndexOutOfBoundsException ex) {
            assertEquals("Index: 1, Size: 0", ex.getMessage());
        }
    }


    public void test_containsField() throws Exception {
        Result result = new Result(primaryKeys, row("label", "euro"));
        assertTrue(result.containsField(0, "label"));
        assertFalse(result.containsField(0, "unknownColumn"));
    }


    public void test_containsField_badIndex() throws Exception {
        Result result = new Result(primaryKeys, row("label", "euro"));
        try {
            result.containsField(-5, "label");
            fail();
        }
        catch (IndexOutOfBoundsException ex) {
            assertEquals("-5", ex.getMessage());
        }
        try {
            result.containsField(625, "label");
            fail();
        }
        catch (IndexOutOfBoundsException ex) {
            assertEquals("Index: 625, Size: 1", ex.getMessage());
        }

        try {
            new Result().containsField(5, "acolumn");
            fail();
        }
        catch (IndexOutOfBoundsException ex) {
            assertEquals("Index: 5, Size: 0", ex.getMessage());
        }
    }


    private Row row(String key, String value) {
        return new Row(Collections.singletonMap(key, value));
    }
}
