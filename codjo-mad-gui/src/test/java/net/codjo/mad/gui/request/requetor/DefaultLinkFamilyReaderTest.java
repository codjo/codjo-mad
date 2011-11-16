package net.codjo.mad.gui.request.requetor;
import net.codjo.mad.common.structure.StructureReader;
import net.codjo.mad.common.structure.TableStructure;
import net.codjo.mad.common.structure.DefaultStructureReader;
import java.util.Arrays;
import junit.framework.TestCase;
/**
 * Test de la classe {@link LinkFamilyReader}.
 */
public class DefaultLinkFamilyReaderTest extends TestCase {
    private LinkFamilyReader reader;
    private StructureReader structureReader;


    public void test_getLinkFamily_error() throws Exception {
        try {
            reader.getFamily("TOTO");
            fail("La famille TOTO est inconnue");
        }
        catch (IllegalArgumentException ex) {
            ; //erreur
        }
    }


    public void test_getLinkFamily() throws Exception {
        LinkFamily family = reader.getFamily("Issuer");

        assertEquals(2, family.size());
        assertEquals("AP_ISSUER", family.getRoot());

        assertEquals("AP_ISSUER", family.getLink(0).getFrom());
        assertEquals(structureReader.getTableBySqlName("AP_ISSUER"),
                     family.getLink(0).getFromTable());

        assertEquals("AP_ISSUER_RATINGS", family.getLink(0).getTo());
        TableStructure issuerRatingsTable =
              structureReader.getTableBySqlName("AP_ISSUER_RATINGS");
        assertEquals(issuerRatingsTable, family.getLink(0).getToTable());
        assertEquals(issuerRatingsTable.getLabel(), family.getLink(0).toString());

        Link link1 = family.getLink(0);
        String[][] keys1 = link1.keysToArray();
        assertEquals("[ISSUER_CODE, ISSUER_CODE, =]", Arrays.asList(keys1[0]).toString());

        String[][] keys2 = family.getLink(1).keysToArray();
        assertEquals("[ISSUER_CODE, ISSUER_CODE, =]", Arrays.asList(keys2[0]).toString());
        assertEquals("[ID, ISSUER_ID, =]", Arrays.asList(keys2[1]).toString());
    }


    public void test_getLinkFamily_withAlias() throws Exception {
        LinkFamily family = reader.getFamily("CakeShareWithAlias");

        assertEquals(2, family.size());
        assertEquals("AP_CAKE_SHARE", family.getRoot());

        assertEquals("AP_CAKE_SHARE", family.getLink(0).getFrom());
        assertEquals("REF_PERSON as sponsor", family.getLink(0).getTo());
        TableStructure personTable = structureReader.getTableBySqlName("REF_PERSON");
        assertEquals(personTable, family.getLink(0).getToTable());
        assertEquals(personTable.getLabel() + " (sponsor)", family.getLink(0).toString());

        String[][] keys1 = family.getLink(0).keysToArray();
        assertEquals("[SHARE_SPONSOR_CODE, PERSON_CODE, <=]",
                     Arrays.asList(keys1[0]).toString());

        assertEquals("AP_CAKE_SHARE", family.getLink(1).getFrom());
        assertEquals("REF_PERSON as manager", family.getLink(1).getTo());
        assertEquals(personTable, family.getLink(1).getToTable());
        assertEquals(personTable.getLabel() + " (manager)", family.getLink(1).toString());

        String[][] keys2 = family.getLink(1).keysToArray();
        assertEquals("[SHARE_MANAGER_CODE, PERSON_CODE, =]",
                     Arrays.asList(keys2[0]).toString());
    }


    @Override
    protected void setUp() throws Exception {
        structureReader =
              new DefaultStructureReader(RequetorParametersTest.class.getResourceAsStream(
                    "RequetorParamTest_structure.xml"));
        reader =
              new DefaultLinkFamilyReader(DefaultLinkFamilyReaderTest.class.getResourceAsStream(
                    "LinkFamilyReaderTest_tableLinks.xml"), structureReader);
    }
}
