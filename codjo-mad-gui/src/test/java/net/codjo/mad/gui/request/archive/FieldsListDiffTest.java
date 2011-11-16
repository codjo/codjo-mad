package net.codjo.mad.gui.request.archive;
import net.codjo.mad.client.request.Result;
import junit.framework.TestCase;
/**
 * DOCUMENT ME!
 *
 * @author $Author: palmont $
 * @version $Revision: 1.3 $
 */
public class FieldsListDiffTest extends TestCase {
    FieldsListDiff diff;


    public FieldsListDiffTest(String str) {
        super(str);
    }


    /**
     * Cas ou left et right n'ont pas les mêmes champs.
     *
     * @throws Exception
     */
    public void test_diff_incoherence() throws Exception {
        diff.getLeftFields().addField("names", "boris");

        diff.getRightFields().addField("name", "boris");

        Result result = diff.diff();

        assertEquals(2, result.getRowCount());

        assertEquals("name", result.getValue(0, FieldsListDiff.FIELD_NAME_COLUMN));
        assertEquals(FieldsListDiff.NOT_DEFINED,
                     result.getValue(0, FieldsListDiff.LEFT_VALUE_COLUMN));
        assertEquals("boris", result.getValue(0, FieldsListDiff.RIGHT_VALUE_COLUMN));

        assertEquals("names", result.getValue(1, FieldsListDiff.FIELD_NAME_COLUMN));
        assertEquals("boris", result.getValue(1, FieldsListDiff.LEFT_VALUE_COLUMN));
        assertEquals(FieldsListDiff.NOT_DEFINED,
                     result.getValue(1, FieldsListDiff.RIGHT_VALUE_COLUMN));
    }


    /**
     * Cas ou il n'y a pas de diff.
     *
     * @throws Exception
     */
    public void test_diff_none() throws Exception {
        diff.getLeftFields().addField("name", "boris");
        diff.getLeftFields().addField("id", "6");

        diff.getRightFields().addField("name", "boris");
        diff.getRightFields().addField("id", "6");

        Result result = diff.diff();

        assertEquals(0, result.getRowCount());
    }


    /**
     * Test la structure du retour d'un diff.
     *
     * @throws Exception
     */
    public void test_diff_structure() throws Exception {
        diff.getLeftFields().addField("name", "boris");
        diff.getLeftFields().addField("id", "5");

        diff.getRightFields().addField("name", "boris");
        diff.getRightFields().addField("id", "6");

        Result result = diff.diff();

        assertEquals(1, result.getRowCount());
        assertEquals("id", result.getValue(0, FieldsListDiff.FIELD_NAME_COLUMN));
        assertEquals("5", result.getValue(0, FieldsListDiff.LEFT_VALUE_COLUMN));
        assertEquals("6", result.getValue(0, FieldsListDiff.RIGHT_VALUE_COLUMN));
    }


    /**
     * Cas ou des champs ne sont pas pris en compte dans le diff.
     *
     * @throws Exception
     */
    public void test_diff_whitExcepts() throws Exception {
        diff.getLeftFields().addField("name", "boris");
        diff.getLeftFields().addField("id", "5");

        diff.getRightFields().addField("name", "boris");
        diff.getRightFields().addField("id", "6");

        Result result = diff.diff();

        assertEquals(1, result.getRowCount());
        assertEquals("id", result.getValue(0, FieldsListDiff.FIELD_NAME_COLUMN));
        assertEquals("5", result.getValue(0, FieldsListDiff.LEFT_VALUE_COLUMN));
        assertEquals("6", result.getValue(0, FieldsListDiff.RIGHT_VALUE_COLUMN));
    }


    /**
     * Test que les champs d'audit se trouvent a la fin.
     *
     * @throws Exception
     */
    public void test_auditAtEnd() throws Exception {
        diff.getLeftFields().addField("B", "B comme Batman");
        diff.getLeftFields().addField(FieldsListDiff.AUDIT_FIELDS[4], "4");
        diff.getLeftFields().addField(FieldsListDiff.AUDIT_FIELDS[3], "3");
        diff.getLeftFields().addField(FieldsListDiff.AUDIT_FIELDS[2], "2");
        diff.getLeftFields().addField(FieldsListDiff.AUDIT_FIELDS[1], "1");
        diff.getLeftFields().addField(FieldsListDiff.AUDIT_FIELDS[0], "0");
        diff.getLeftFields().addField("A", "A comme Arrivederci");

        diff.getRightFields().addField("B", "B comme Boris");
        diff.getRightFields().addField(FieldsListDiff.AUDIT_FIELDS[4], "4 modifie");
        diff.getRightFields().addField(FieldsListDiff.AUDIT_FIELDS[3], "3 modifie");
        diff.getRightFields().addField(FieldsListDiff.AUDIT_FIELDS[2], "2 modifie");
        diff.getRightFields().addField(FieldsListDiff.AUDIT_FIELDS[1], "1 modifie");
        diff.getRightFields().addField(FieldsListDiff.AUDIT_FIELDS[0], "0 modifie");
        diff.getRightFields().addField("A", "A comme A bientôt");

        Result result = diff.diff();

        assertEquals(7, result.getRowCount());
        assertEquals("A", result.getValue(0, FieldsListDiff.FIELD_NAME_COLUMN));
        assertEquals("B", result.getValue(1, FieldsListDiff.FIELD_NAME_COLUMN));
        assertEquals("creationBy", FieldsListDiff.AUDIT_FIELDS[0],
                     result.getValue(2, FieldsListDiff.FIELD_NAME_COLUMN));
        assertEquals("creationDatetime", FieldsListDiff.AUDIT_FIELDS[1],
                     result.getValue(3, FieldsListDiff.FIELD_NAME_COLUMN));
        assertEquals("updateBy", FieldsListDiff.AUDIT_FIELDS[2],
                     result.getValue(4, FieldsListDiff.FIELD_NAME_COLUMN));
        assertEquals("updateDatetime", FieldsListDiff.AUDIT_FIELDS[3],
                     result.getValue(5, FieldsListDiff.FIELD_NAME_COLUMN));
        assertEquals("comment", FieldsListDiff.AUDIT_FIELDS[4],
                     result.getValue(6, FieldsListDiff.FIELD_NAME_COLUMN));
    }


    @Override
    protected void setUp() {
        diff = new FieldsListDiff();
    }


    @Override
    protected void tearDown() {
    }
}
