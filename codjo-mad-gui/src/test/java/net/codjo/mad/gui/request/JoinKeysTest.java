package net.codjo.mad.gui.request;
import java.util.Iterator;
import junit.framework.TestCase;
/**
 * Classe de test de <code>JoinKeys</code>.
 */
public class JoinKeysTest extends TestCase {
    private static final String COMMON_B = "commonB";
    private static final String COMMON_C = "commonC";

    public void test_constructor() throws Exception {
        JoinKeys jks = new JoinKeys(COMMON_B);

        Iterator iter = jks.iterator();

        assertTrue("Il y a des clefs de jointure", iter.hasNext());
        JoinKeys.Association asso = (JoinKeys.Association)iter.next();
        assertEquals("pk du pere ", COMMON_B, asso.getFatherField());
        assertEquals("pk du fils ", COMMON_B, asso.getSonField());

        assertFalse(iter.hasNext());
    }


    public void test_constructor_array() throws Exception {
        JoinKeys jks = new JoinKeys(new String[] {COMMON_B, COMMON_C});

        Iterator iter = jks.iterator();

        assertTrue("JoinKeys non vide", iter.hasNext());
        JoinKeys.Association asso = (JoinKeys.Association)iter.next();
        assertEquals("pk du pere", COMMON_B, asso.getFatherField());
        assertEquals("pk du fils", COMMON_B, asso.getSonField());

        assertTrue("il existe des cles de jointure", iter.hasNext());
        asso = (JoinKeys.Association)iter.next();
        assertEquals("pk du pere", COMMON_C, asso.getFatherField());
        assertEquals("pk du fils", COMMON_C, asso.getSonField());

        assertFalse(iter.hasNext());
    }


    public void test_iterator() throws Exception {
        JoinKeys jks = new JoinKeys();
        jks.addAssociation("fatherA", "sonA");
        jks.addAssociation(COMMON_B);

        Iterator iter = jks.iterator();

        assertTrue("JoinKeys non vide", iter.hasNext());
        JoinKeys.Association asso = (JoinKeys.Association)iter.next();
        assertEquals("fatherA", asso.getFatherField());
        assertEquals("sonA", asso.getSonField());

        assertTrue("JoinKeys contient des cles", iter.hasNext());
        asso = (JoinKeys.Association)iter.next();
        assertEquals("pk du pere", COMMON_B, asso.getFatherField());
        assertEquals("pk du fils", COMMON_B, asso.getSonField());

        assertFalse(iter.hasNext());
    }


    public void test_iterator_readOnly() throws Exception {
        JoinKeys jks = new JoinKeys();
        jks.addAssociation("fatherA", "sonA");
        jks.addAssociation(COMMON_B);

        Iterator iter = jks.iterator();

        assertTrue("JoinKeys contient des cles", iter.hasNext());
        iter.next();

        try {
            iter.remove();
            fail("Iterator en lecture seule !");
        }
        catch (UnsupportedOperationException ex) {
            ; // Ok
        }
    }
}
