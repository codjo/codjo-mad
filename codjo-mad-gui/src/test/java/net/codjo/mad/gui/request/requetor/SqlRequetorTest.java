package net.codjo.mad.gui.request.requetor;
import net.codjo.mad.client.request.FieldsList;
import net.codjo.mad.common.structure.DefaultStructureReader;
import net.codjo.mad.common.structure.StructureReader;
import net.codjo.mad.gui.framework.DefaultGuiContext;
import net.codjo.mad.gui.request.Mock.ListDataSource;
import net.codjo.mad.gui.request.Preference;
import net.codjo.mad.gui.request.RequestTable;
import net.codjo.mad.gui.request.factory.RequetorFactory;
import net.codjo.security.common.api.UserMock;
import java.awt.Component;
import java.awt.Container;
import java.io.IOException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.UIManager;
import javax.xml.parsers.ParserConfigurationException;
import junit.framework.TestCase;
import org.apache.log4j.BasicConfigurator;
import org.xml.sax.SAXException;

public class SqlRequetorTest extends TestCase {
    private static final int EQUAL_OPERATOR = 0;
    private SqlRequetor sqlRequetor;
    private SqlRequetor sqlRequetorWithAlias;
    private SqlRequetor sqlRequetorWithoutAllLink;
    private RequetorParameters parameters;
    private RequetorParameters parametersWithAlias;


    public void test_linksCombo() throws Exception {
        JComboBox linksCombo = (JComboBox)findComponentWithName("linkTablesComboBox", sqlRequetor);
        JList linkFieldsList = (JList)findComponentWithName("listLinkFields", sqlRequetor);

        assertContent("Emetteur Secteur, Emetteur Rating, Emetteur lié, ", linksCombo.getModel());
        assertEquals(SqlRequetor.EMPTY_LINK, linksCombo.getSelectedItem());
        assertFalse(linkFieldsList.isVisible());

        linksCombo.setSelectedIndex(1);

        assertEquals(parameters.getLinks()[1], linksCombo.getSelectedItem());
        assertEquals("AP_ISSUER_RATINGS", ((Link)linksCombo.getSelectedItem()).getTo());
        assertTrue(linkFieldsList.isVisible());
        assertContent("ISSUER_CODE, QUANTITY", linkFieldsList.getModel());

        linksCombo.setSelectedItem(SqlRequetor.EMPTY_LINK);

        assertFalse(linkFieldsList.isVisible());
    }


    public void test_linksCombo_withAlias() throws Exception {
        JComboBox linksCombo =
              (JComboBox)findComponentWithName("linkTablesComboBox", sqlRequetorWithAlias);
        JList linkFieldsList =
              (JList)findComponentWithName("listLinkFields", sqlRequetorWithAlias);

        assertContent("Personne (sponsor), Personne (manager), ", linksCombo.getModel());
        assertEquals(SqlRequetor.EMPTY_LINK, linksCombo.getSelectedItem());
        assertFalse(linkFieldsList.isVisible());

        linksCombo.setSelectedIndex(0);

        assertEquals(parametersWithAlias.getLinks()[0], linksCombo.getSelectedItem());
        assertEquals("REF_PERSON as sponsor", ((Link)linksCombo.getSelectedItem()).getTo());
        assertTrue(linkFieldsList.isVisible());
        assertContent("PERSON_CODE", linkFieldsList.getModel());

        linksCombo.setSelectedItem(SqlRequetor.EMPTY_LINK);

        assertFalse(linkFieldsList.isVisible());
    }


    public void test_buildSelect_whereOnRootTable() throws Exception {
        JList listCurrentFields = (JList)findComponentWithName("listCurrentFields", sqlRequetor);
        JList listOperators = (JList)findComponentWithName("listOperators", sqlRequetor);
        JList whereClause = (JList)findComponentWithName("listSqlRequest", sqlRequetor);
        JTextField textFieldValue = (JTextField)findComponentWithName("textFieldValue", sqlRequetor);

        assertContent("ISSUER_CODE, ID", listCurrentFields.getModel());

        listCurrentFields.setSelectedIndex(0);
        listOperators.setSelectedIndex(EQUAL_OPERATOR);
        textFieldValue.setText("kaikeSoje");

        assertContent("AP_ISSUER.ISSUER_CODE = 'kaikeSoje'", whereClause.getModel());

        assertFromClauseNoAlias(" where (AP_ISSUER.ISSUER_CODE = 'kaikeSoje')",
                                sqlRequetor.getQueryFromClause());

        assertEquals(
              "SELECT AP_ISSUER.ISSUER_CODE,AP_ISSUER_RATINGS_SECTOR.ISSUER_LABEL,AP_ISSUER_RATINGS_SECTOR.DATE_BEGIN,AP_ISSUER_RATINGS.QUANTITY"
              + sqlRequetor.getQueryFromClause(),
              sqlRequetor.getSearchQuery());
    }


    public void test_buildSelect_whereOnRootTableWithAlias()
          throws Exception {
        JList listCurrentFields =
              (JList)findComponentWithName("listCurrentFields", sqlRequetorWithAlias);
        JList listOperators =
              (JList)findComponentWithName("listOperators", sqlRequetorWithAlias);
        JList whereClause =
              (JList)findComponentWithName("listSqlRequest", sqlRequetorWithAlias);
        JTextField textFieldValue =
              (JTextField)findComponentWithName("textFieldValue", sqlRequetorWithAlias);

        assertContent("SHARE_MANAGER_CODE, SHARE_SPONSOR_CODE, SHARE_MANAGER_DATE",
                      listCurrentFields.getModel());

        listCurrentFields.setSelectedIndex(0);
        listOperators.setSelectedIndex(EQUAL_OPERATOR);
        textFieldValue.setText("kaikeSoje");

        assertContent("AP_CAKE_SHARE.SHARE_MANAGER_CODE = 'kaikeSoje'",
                      whereClause.getModel());

        assertFromClauseWithAlias(" where (AP_CAKE_SHARE.SHARE_MANAGER_CODE = 'kaikeSoje')",
                                  sqlRequetorWithAlias.getQueryFromClause());

        assertEquals(
              "SELECT sponsor.PERSON_CODE,manager.PERSON_CODE,AP_CAKE_SHARE.SHARE_SPONSOR_CODE"
              + sqlRequetorWithAlias.getQueryFromClause(),
              sqlRequetorWithAlias.getSearchQuery());
    }


    public void test_whereOnLinkedTable() throws Exception {
        JList listOperators = (JList)findComponentWithName("listOperators", sqlRequetor);
        JList whereClause = (JList)findComponentWithName("listSqlRequest", sqlRequetor);
        JTextField textFieldValue =
              (JTextField)findComponentWithName("textFieldValue", sqlRequetor);
        JComboBox linksCombo =
              (JComboBox)findComponentWithName("linkTablesComboBox", sqlRequetor);
        JList linkFieldsList =
              (JList)findComponentWithName("listLinkFields", sqlRequetor);

        linksCombo.setSelectedIndex(1);
        linkFieldsList.setSelectedIndex(0);
        listOperators.setSelectedIndex(EQUAL_OPERATOR);
        textFieldValue.setText("kaikeSoje");

        assertContent("AP_ISSUER_RATINGS.ISSUER_CODE = 'kaikeSoje'",
                      whereClause.getModel());

        assertFromClauseNoAlias(" where (AP_ISSUER_RATINGS.ISSUER_CODE = 'kaikeSoje')",
                                sqlRequetor.getQueryFromClause());
    }


    public void test_whereOffLinkedTableNoAlias()
          throws Exception {
        sqlRequetorWithoutAllLink.setMandatoryClause(
              "AP_ISSUER_RATINGS.ISSUER_CODE = 'kaikeSoje'");

        assertFromClauseNoAlias(" where (AP_ISSUER_RATINGS.ISSUER_CODE = 'kaikeSoje')",
                                sqlRequetorWithoutAllLink.getQueryFromClause());
    }


    public void test_whereOnLinkedTable_withAlias()
          throws Exception {
        JList listOperators =
              (JList)findComponentWithName("listOperators", sqlRequetorWithAlias);
        JList whereClause =
              (JList)findComponentWithName("listSqlRequest", sqlRequetorWithAlias);
        JTextField textFieldValue =
              (JTextField)findComponentWithName("textFieldValue", sqlRequetorWithAlias);
        JComboBox linksCombo =
              (JComboBox)findComponentWithName("linkTablesComboBox", sqlRequetorWithAlias);
        JList linkFieldsList =
              (JList)findComponentWithName("listLinkFields", sqlRequetorWithAlias);

        linksCombo.setSelectedIndex(0);
        linkFieldsList.setSelectedIndex(0);
        listOperators.setSelectedIndex(EQUAL_OPERATOR);
        textFieldValue.setText("kaikeSoje");

        assertContent("sponsor.PERSON_CODE = 'kaikeSoje'", whereClause.getModel());

        assertFromClauseWithAlias(" where (sponsor.PERSON_CODE = 'kaikeSoje')",
                                  sqlRequetorWithAlias.getQueryFromClause());
    }


    public void test_whereOnLinkedTable_and() throws Exception {
        JList listOperators =
              (JList)findComponentWithName("listOperators", sqlRequetorWithAlias);
        JList whereClause =
              (JList)findComponentWithName("listSqlRequest", sqlRequetorWithAlias);
        JTextField textFieldValue =
              (JTextField)findComponentWithName("textFieldValue", sqlRequetorWithAlias);
        JComboBox linksCombo =
              (JComboBox)findComponentWithName("linkTablesComboBox", sqlRequetorWithAlias);
        JList linkFieldsList =
              (JList)findComponentWithName("listLinkFields", sqlRequetorWithAlias);
        JButton addButton =
              (JButton)findComponentWithName("buttonAdd", sqlRequetorWithAlias);

        linksCombo.setSelectedIndex(0);
        linkFieldsList.setSelectedIndex(0);
        listOperators.setSelectedIndex(EQUAL_OPERATOR);
        textFieldValue.setText("kaikeSoje");
        addButton.doClick();

        assertContent("sponsor.PERSON_CODE = 'kaikeSoje',  and ", whereClause.getModel());
    }


    public void test_appendSqlAfterWhere() throws Exception {
        String currentSql = sqlRequetor.getQueryFromClause();
        String orderByClause = "PK";
        sqlRequetor.setOrderClause(orderByClause);
        assertEquals(currentSql + " order by " + orderByClause,
                     sqlRequetor.getQueryFromClause());
    }


    public void test_keepSelectorAfterValidate() throws Exception {
        DefaultGuiContext defaultGuiContext = new DefaultGuiContext();
        defaultGuiContext.setUser(new UserMock().mockIsAllowedTo(true));

        RequestTable table = new RequestTable(new ListDataSource());
        Preference preference = new Preference("myPreference");
        preference.setRequetor(new RequetorFactory());
        table.setPreference(preference);
        FieldsList selector = new FieldsList("field_un", "value");
        table.getDataSource().setSelector(selector);

        sqlRequetor = new SqlRequetor(new FindAction(defaultGuiContext, table), parameters);

        JList listCurrentFields = (JList)findComponentWithName("listCurrentFields", sqlRequetor);
        JList listOperators = (JList)findComponentWithName("listOperators", sqlRequetor);
        JTextField textFieldValue = (JTextField)findComponentWithName("textFieldValue", sqlRequetor);
        JButton validateButton = (JButton)findComponentWithName("validateButton", sqlRequetor);

        listCurrentFields.setSelectedIndex(0);
        listOperators.setSelectedIndex(EQUAL_OPERATOR);
        textFieldValue.setText("kaikeSoje");
        validateButton.doClick();

        assertSame(selector, table.getDataSource().getSelector());
        assertEquals(1, selector.getFieldCount());
    }


    @Override
    protected void setUp() throws Exception {
        UIManager.put("mad.load",
                      new ImageIcon(SqlRequetorTest.class.getResource("/images/idea/filter.png")));

        StructureReader structureReader = createStructureReader();
        LinkFamilyReader linkFamilyReader = createLinkFamilyReader(structureReader);

        parameters = createParameters(linkFamilyReader, structureReader);

        DefaultGuiContext defaultGuiContext = new DefaultGuiContext();
        defaultGuiContext.setUser(new UserMock().mockIsAllowedTo(true));

        sqlRequetor = new SqlRequetor(new FindAction(defaultGuiContext, new RequestTable()), parameters);

        RequetorParameters parametersWithoutAllLink = createParametersWithoutAllLink(linkFamilyReader,
                                                                                     structureReader);

        sqlRequetorWithoutAllLink =
              new SqlRequetor(new FindAction(defaultGuiContext, new RequestTable()),
                              parametersWithoutAllLink);

        parametersWithAlias =
              createParametersWithAlias(linkFamilyReader, structureReader);
        sqlRequetorWithAlias =
              new SqlRequetor(new FindAction(defaultGuiContext, new RequestTable()),
                              parametersWithAlias);
    }


    private static void assertFromClauseNoAlias(String expectedWhereClause,
                                                String queryFromClause) {
        assertContains(
              "INNER JOIN AP_ISSUER_RATINGS_SECTOR ON AP_ISSUER.ISSUER_CODE = AP_ISSUER_RATINGS_SECTOR.ISSUER_CODE AND AP_ISSUER.ID = AP_ISSUER_RATINGS_SECTOR.ISSUER_ID",
              queryFromClause);
        assertContains(
              "INNER JOIN AP_ISSUER_RATINGS ON AP_ISSUER.ISSUER_CODE = AP_ISSUER_RATINGS.ISSUER_CODE",
              queryFromClause);
        assertContains("from AP_ISSUER ", queryFromClause);
        assertContains(expectedWhereClause, queryFromClause);
        assertEquals(2, countOccurenceOf("INNER JOIN", queryFromClause));
    }


    private static void assertFromClauseWithAlias(String expectedWhereClause,
                                                  String queryFromClause) {
        assertContains(
              "INNER JOIN REF_PERSON as sponsor ON AP_CAKE_SHARE.SHARE_SPONSOR_CODE = sponsor.PERSON_CODE",
              queryFromClause);
        assertContains(
              "INNER JOIN REF_PERSON as manager ON AP_CAKE_SHARE.SHARE_MANAGER_CODE = manager.PERSON_CODE",
              queryFromClause);
        assertContains("from AP_CAKE_SHARE ", queryFromClause);
        assertContains(expectedWhereClause, queryFromClause);
        assertEquals(2, countOccurenceOf("INNER JOIN", queryFromClause));
    }


    private static int countOccurenceOf(String occurence, String myString) {
        int indexOfOccurence = myString.indexOf(occurence);
        if (indexOfOccurence == -1) {
            return 0;
        }
        return countOccurenceOf(occurence,
                                myString.substring(indexOfOccurence + occurence.length() + 1,
                                                   myString.length())) + 1;
    }


    private static void assertContains(String expected, String queryFromClause) {
        assertTrue(expected + " dans " + queryFromClause, queryFromClause.contains(expected));
    }


    private static void displayGui(RequetorParameters params)
          throws InterruptedException {
        JFrame frame = new JFrame("Test SqlRequetor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        DefaultGuiContext ctx = new DefaultGuiContext();
        ctx.setUser(new UserMock());
        frame.add(new SqlRequetor(new FindAction(ctx, new RequestTable()), params).getContentPane());
        frame.setSize(800, 600);
        frame.setVisible(true);
    }


    private static LinkFamilyReader createLinkFamilyReader(
          StructureReader structureReader)
          throws IOException, ParserConfigurationException, SAXException {
        return new DefaultLinkFamilyReader(RequetorParametersTest.class.getResourceAsStream(
              "RequetorParamTest_tableLinks.xml"), structureReader);
    }


    private static StructureReader createStructureReader()
          throws IOException, ParserConfigurationException, SAXException {
        return new DefaultStructureReader(RequetorParametersTest.class.getResourceAsStream(
              "RequetorParamTest_structure.xml"));
    }


    private static RequetorParameters createParametersWithAlias(
          LinkFamilyReader linkFamilyReader,
          StructureReader structureReader) {
        LinkFamily family = linkFamilyReader.getFamily("CakeShareWithAlias");
        String[] aliasColNamesForSelect =
              new String[]{"sponsor.personCode", "manager.personCode", "shareSponsorCode"};
        return new RequetorParameters(structureReader, family, aliasColNamesForSelect);
    }


    private static RequetorParameters createParameters(
          LinkFamilyReader linkFamilyReader,
          StructureReader structureReader) {
        LinkFamily family = linkFamilyReader.getFamily("Issuer");
        String[] issuerColNamesForSelect =
              new String[]{"issuerCode", "issuerLabel", "dateBegin", "quantity"};
        return new RequetorParameters(structureReader, family, issuerColNamesForSelect);
    }


    private static RequetorParameters createParametersWithoutAllLink(
          LinkFamilyReader linkFamilyReader,
          StructureReader structureReader) {
        LinkFamily family = linkFamilyReader.getFamily("Issuer");
        String[] issuerColNamesForSelect =
              new String[]{"issuerCode", "issuerLabel", "dateBegin"};
        return new RequetorParameters(structureReader, family, issuerColNamesForSelect);
    }


    private static Component findComponentWithName(String name, Component component) {
        if (component == null
            || name.equals(component.getName())
            || !(component instanceof Container)) {
            return component;
        }

        Container container = (Container)component;
        final Component[] components = container.getComponents();
        for (Component subComponent : components) {
            Component result = findComponentWithName(name, subComponent);
            if (result != null) {
                return result;
            }
        }
        return null;
    }


    private static void assertContent(String expectedContent, ListModel model) {
        StringBuilder actual = new StringBuilder();
        for (int i = 0; i < model.getSize(); i++) {
            actual.append(model.getElementAt(i));
            if (i + 1 < model.getSize()) {
                actual.append(", ");
            }
        }
        assertEquals(expectedContent, actual.toString());
    }


    public static void main(String[] args) throws Exception {
        BasicConfigurator.configure();
        StructureReader structureReader = createStructureReader();
        LinkFamilyReader linkFamilyReader = createLinkFamilyReader(structureReader);

        RequetorParameters parametersWithAlias =
              createParametersWithAlias(linkFamilyReader, structureReader);

        displayGui(parametersWithAlias);
    }
}
