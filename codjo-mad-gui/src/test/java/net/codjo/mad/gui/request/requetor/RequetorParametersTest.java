package net.codjo.mad.gui.request.requetor;
import net.codjo.mad.common.structure.StructureReader;
import net.codjo.mad.common.structure.DefaultStructureReader;
import java.util.Arrays;
import java.util.Collection;
import junit.framework.TestCase;
/**
 * DOCUMENT ME!
 *
 * @version $Revision: 1.6 $
 */
public class RequetorParametersTest extends TestCase {
    private RequetorParameters parameters;
    private LinkFamily family;
    private RequetorParameters parametersWithAlias;
    private StructureReader structureReader;


    public void test_getLinkedTable() throws Exception {
        Link[] links = parameters.getLinks();
        assertEquals(3, links.length);
        assertEquals(structureReader.getTableBySqlName("AP_ISSUER_RATINGS_SECTOR"), links[0].getToTable());
        assertEquals(structureReader.getTableBySqlName("AP_ISSUER_RATINGS"), links[1].getToTable());
        assertEquals(structureReader.getTableBySqlName("AP_ISSUER_LINK"), links[2].getToTable());
    }


    public void test_getLinkedTable_withAlias() throws Exception {
        Link[] links = parametersWithAlias.getLinks();
        assertEquals(2, links.length);
        assertEquals("REF_PERSON as sponsor", links[0].getTo());
        assertEquals(structureReader.getTableBySqlName("REF_PERSON"),
            links[0].getToTable());
        assertEquals("REF_PERSON as manager", links[1].getTo());
    }


    public void test_getJoinKeyToRootTableFor() throws Exception {
        Link link = family.getLink(0);
        assertEquals("AP_ISSUER_RATINGS_SECTOR", link.getTo());

        String[][] jks = parameters.getJoinKeyToRootTableFor(link);

        assertEquals(2, jks.length);
        assertEquals("[ISSUER_CODE, ISSUER_CODE, =]", Arrays.asList(jks[0]).toString());
        assertEquals("[ID, ISSUER_ID, =]", Arrays.asList(jks[1]).toString());
    }


    public void test_basic() throws Exception {
        Link rootLink = parameters.getRootLink();
        assertEquals(family.getRoot(), rootLink.getTo());
        assertNull(rootLink.getFrom());
        assertEquals(structureReader.getTableBySqlName(family.getRoot()),
            rootLink.getToTable());

        assertEquals("AP_ISSUER", parameters.getLink("AP_ISSUER").getTo());
        assertEquals("AP_ISSUER_RATINGS_SECTOR", parameters.getLink("AP_ISSUER_RATINGS_SECTOR").getTo());
    }


    public void test_basic_error() throws Exception {
        try {
            parameters.getLink("BOBO");
            fail("BOBO ne fait pas partie de la famille");
        }
        catch (Exception e) {
            ; // echec
        }
    }


    public void test_getSelectClause() throws Exception {
        assertEquals("SELECT AP_ISSUER.ISSUER_CODE,AP_ISSUER_RATINGS_SECTOR.ISSUER_LABEL,AP_ISSUER_RATINGS_SECTOR.DATE_BEGIN,AP_ISSUER_RATINGS.QUANTITY",
            parameters.getSelectClause());
    }


    public void test_getSelectClause_withAlias() throws Exception {
        assertEquals("SELECT sponsor.PERSON_CODE,manager.PERSON_CODE,AP_CAKE_SHARE.SHARE_SPONSOR_CODE",
            parametersWithAlias.getSelectClause());
    }


    public void test_getLinkedFieldsUsedInSelectClause()
            throws Exception {
        Collection fields = parameters.getLinkedFieldsUsedInSelectClause();
        assertEquals(3, fields.size());
        assertContains("Date de début", fields.toString());
        assertContains("Libellé", fields.toString());
        assertContains("Quantité", fields.toString());
    }


    public void test_getLinkedFieldsUsedInSelectClause_withAlias()
            throws Exception {
        Collection fields = parametersWithAlias.getLinkedFieldsUsedInSelectClause();
        assertEquals(2, fields.size());
        assertContains("Code person", fields.toString());
        assertContains("Code person", fields.toString());
    }


    public void test_getLinksUsedInSelectClause()
            throws Exception {
        Collection links = parameters.getLinksUsedInSelectClause();
        assertEquals(2, links.size());
        assertContains("Emetteur Rating", links.toString());
        assertContains("Emetteur Secteur", links.toString());
    }


    public void test_getLinksUsedInSelectClause_withAlias()
            throws Exception {
        Collection links = parametersWithAlias.getLinksUsedInSelectClause();
        assertEquals(2, links.size());
        assertContains("Personne (sponsor)", links.toString());
        assertContains("Personne (manager)", links.toString());
    }


    public void test_getSelectClause_nok() throws Exception {
        String[] colNamesForSelect = new String[] {"issuerCode", "someBadField"};
        parameters = new RequetorParameters(structureReader, family, colNamesForSelect);

        try {
            parameters.getSelectClause();
            fail("Le champ someBadField n'existe pas et aurait dû provoquer une erreur.");
        }
        catch (IllegalArgumentException e) {
            ; // Cas normal
        }
    }


    @Override
    protected void setUp() throws Exception {
        structureReader =
            new DefaultStructureReader(RequetorParametersTest.class.getResourceAsStream(
                    "RequetorParamTest_structure.xml"));
        LinkFamilyReader linkFamilyReader
              = new DefaultLinkFamilyReader(RequetorParametersTest.class.getResourceAsStream(
              "RequetorParamTest_tableLinks.xml"), structureReader);

        family = linkFamilyReader.getFamily("Issuer");
        String[] issuerColNamesForSelect =
            new String[] {"issuerCode", "issuerLabel", "dateBegin", "quantity"};
        parameters =
            new RequetorParameters(structureReader, family, issuerColNamesForSelect);

        LinkFamily familyWithAlias = linkFamilyReader.getFamily("CakeShareWithAlias");
        String[] aliasColNamesForSelect =
            new String[] {"sponsor.personCode", "manager.personCode", "shareSponsorCode"};
        parametersWithAlias =
            new RequetorParameters(structureReader, familyWithAlias,
                aliasColNamesForSelect);
    }


    private static void assertContains(String expected, String actual) {
        assertTrue(expected + " dans " + actual, actual.contains(expected));
    }
}
