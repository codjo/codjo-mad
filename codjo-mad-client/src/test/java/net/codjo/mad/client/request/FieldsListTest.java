package net.codjo.mad.client.request;
import junit.framework.TestCase;
/**
 * Classe de test de {@link FieldsList}.
 */
public class FieldsListTest extends TestCase {
    private FieldsList list;


    public void test_getFieldValue() throws Exception {
        list.addField("a", "1");
        list.addField("b", "2");

        assertEquals("1", list.getFieldValue("a"));
        assertEquals("2", list.getFieldValue("b"));
    }


    public void test_getFieldValue_failure() throws Exception {
        list.addField("a", "1");

        try {
            list.getFieldValue("z");
            fail("Col Z est inconnue");
        }
        catch (IllegalArgumentException ex) {
            assertEquals("Le champ n'est pas défini : z", ex.getMessage());
        }

        try {
            list.getFieldValue(null);
            fail("Col Z est inconnue");
        }
        catch (IllegalArgumentException ex) {
            ; // Ok
        }
    }


    public void test_getFieldValue_failureNoDefinedFields() throws Exception {
        try {
            list.getFieldValue("z");
            fail();
        }
        catch (IllegalArgumentException ex) {
            assertEquals("Le champ n'est pas défini : z", ex.getMessage());
        }
    }


/*
    public void test_equals() throws Exception {
        list.addField("olivier", "coach");
        list.addField("sebastien", "coach2");
        list.addField("segolene", "coach2");

        //noinspection ObjectEqualsNull
        assertEquals(false, list.equals(null));
        assertEquals(false, list.equals(new FieldsList()));
        FieldsList newFieldList = new FieldsList();
        newFieldList.addField("olivier", "coach");
        newFieldList.addField("sebastien", "coach2");
        newFieldList.addField("segolene", "coach2");
        assertEquals(true, list.equals(newFieldList));
    }


    public void test_hashCode() throws Exception {

        assertEquals(0, list.hashCode());

        list.addField("olivier", "coach");
        list.addField("sebastien", "coach2");
        list.addField("segolene", "coach2");
        FieldsList newFieldList = new FieldsList();
        newFieldList.addField("olivier", "coach");
        newFieldList.addField("sebastien", "coach2");
        newFieldList.addField("segolene", "coach2");
        assertTrue(list.hashCode() == newFieldList.hashCode());
    }

*/

    @Override
    protected void setUp() throws Exception {
        list = new FieldsList();
    }
}
