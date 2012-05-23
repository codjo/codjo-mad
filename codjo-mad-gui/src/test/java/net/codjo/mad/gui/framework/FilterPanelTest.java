package net.codjo.mad.gui.framework;
import java.sql.Date;
import java.util.Arrays;
import java.util.Calendar;
import javax.swing.JComboBox;
import junit.framework.Assert;
import net.codjo.gui.toolkit.date.DateField;
import net.codjo.mad.client.request.MadServerFixture;
import net.codjo.mad.client.request.MadServerFixture.WrapperMock;
import net.codjo.mad.gui.request.Column;
import net.codjo.mad.gui.request.Preference;
import net.codjo.mad.gui.request.RequestTable;
import net.codjo.mad.gui.request.factory.SelectFactory;
import net.codjo.security.common.api.UserMock;
import org.uispec4j.Button;
import org.uispec4j.ComboBox;
import org.uispec4j.Panel;
import org.uispec4j.TextBox;
import org.uispec4j.UISpecTestCase;

public class FilterPanelTest extends UISpecTestCase {
    private RequestTable requestTable;
    private FilterPanel filterPanel;
    private MadServerFixture fixture = new MadServerFixture();
    private Button searchButton;
    private Button clearButton;
    private static final String[] COLUMNS_NAMES = new String[]{"name", "address", "zipCode", "city"};


    public void test_filterAutomatic() throws Exception {

        filterPanel.setWithSearchButton(false);
        initDataFilter();

        Panel panel = new Panel(filterPanel);
        TextBox nameTextBox = panel.getTextBox("nameFilter");
        TextBox addressTextBox = panel.getTextBox("addressFilter");
        ComboBox zipCodeComboBox = panel.getComboBox("zipCodeFilter");
        ComboBox cityComboBox = panel.getComboBox("cityFilter");

        assertStateInitialFilter(nameTextBox, addressTextBox, zipCodeComboBox, cityComboBox);

        assertFilterResult(COLUMNS_NAMES,
                           new String[][]{{"Montgomery Scott", "Le Lido", "75008", "FR1"}},
                           nameTextBox, 0, 1);

        assertFilterResult(COLUMNS_NAMES, new String[][]{
              {"James T. Kirk", "NGC 1701 Enterprise", "Narita", "JP1"},
              {"Spock", "Vulcan Gentleman's Club", "ZA98", "UK1"},
              {"Leonard H. McCoy", "Le Lido", "75008", "FR1"},
              {"Montgomery Scott", "Le Lido", "75008", "FR1"}
        }, nameTextBox, 4);

        assertFilterResult(COLUMNS_NAMES,
                           new String[][]{{"Leonard H. McCoy", "Le Lido", "75008", "FR1"},
                                          {"Montgomery Scott", "Le Lido", "75008", "FR1"}},
                           cityComboBox, "Paris", 2);
    }


    public void test_clearButtonFilter() throws Exception {
        initDataFilter();
        filterPanel.addDateFilter("Date", "dateFilter", "date");

        filterPanel.setWithClearButton(true);
        Panel panel = new Panel(filterPanel);

        TextBox nameFilter = panel.getTextBox("nameFilter");
        JComboBox zipComboBox = (JComboBox)filterPanel.getFilter("zipCodeFilter");
        DateField dateField = (DateField)filterPanel.getFilter("dateFilter");

        nameFilter.setText("Toto");
        zipComboBox.setSelectedIndex(3);
        java.util.Date date = Calendar.getInstance().getTime();
        dateField.setDate(date);

        Assert.assertEquals("Toto", nameFilter.getText());
        Assert.assertEquals(3, zipComboBox.getSelectedIndex());
        Assert.assertEquals(date, dateField.getDate());
        assertEquals(0, requestTable.getRowCount());

        fixture.mockServerResult(COLUMNS_NAMES, new String[][]{
              {"James T. Kirk", "NGC 1701 Enterprise", "Narita", "JP1"},
              {"Spock", "Vulcan Gentleman's Club", "ZA98", "UK1"},
              {"Leonard H. McCoy", "Le Lido", "75008", "FR1"},
              {"Montgomery Scott", "Le Lido", "75008", "FR1"}
        });
        clearButton.click();
        assertEquals(4, requestTable.getRowCount());
        Assert.assertEquals("", nameFilter.getText());
        Assert.assertEquals(0, zipComboBox.getSelectedIndex());
        Assert.assertEquals(null, dateField.getDate());
    }


    public void test_filterWithSearchButton() throws Exception {
        initDataFilter();

        Panel panel = new Panel(filterPanel);
        TextBox nameTextBox = panel.getTextBox("nameFilter");
        TextBox addressTextBox = panel.getTextBox("addressFilter");
        ComboBox zipCodeComboBox = panel.getComboBox("zipCodeFilter");
        ComboBox cityComboBox = panel.getComboBox("cityFilter");

        assertStateInitialFilter(nameTextBox, addressTextBox, zipCodeComboBox, cityComboBox);

        fixture.mockServerResult(COLUMNS_NAMES, new String[][]{
              {"James T. Kirk", "NGC 1701 Enterprise", "Narita", "JP1"},
              {"Spock", "Vulcan Gentleman's Club", "ZA98", "UK1"},
              {"Leonard H. McCoy", "Le Lido", "75008", "FR1"},
              {"Montgomery Scott", "Le Lido", "75008", "FR1"}
        });
        searchButton.click();
        assertEquals(4, requestTable.getRowCount());

        assertStateInitialFilter(nameTextBox, addressTextBox, zipCodeComboBox, cityComboBox);

        assertFilterResult(COLUMNS_NAMES,
                           new String[][]{{"Montgomery Scott", "Le Lido", "75008", "FR1"}},
                           nameTextBox, 0, 1);

        assertFilterResult(COLUMNS_NAMES, new String[][]{
              {"James T. Kirk", "NGC 1701 Enterprise", "Narita", "JP1"},
              {"Spock", "Vulcan Gentleman's Club", "ZA98", "UK1"},
              {"Leonard H. McCoy", "Le Lido", "75008", "FR1"},
              {"Montgomery Scott", "Le Lido", "75008", "FR1"}
        }, nameTextBox, 4);

        assertFilterResult(COLUMNS_NAMES,
                           new String[][]{{"Leonard H. McCoy", "Le Lido", "75008", "FR1"},
                                          {"Montgomery Scott", "Le Lido", "75008", "FR1"}},
                           cityComboBox, "Paris", 2);
    }


    public void test_interdependance() throws Exception {
        initDataFilter();

        filterPanel.limitFilter("cityFilter", "zipCodeFilter");

        Panel panel = new Panel(filterPanel);
        ComboBox zipCodeComboBox = panel.getComboBox("zipCodeFilter");
        ComboBox cityComboBox = panel.getComboBox("cityFilter");

        zipCodeComboBox.contains(new String[]{"75008", "75116", "Narita", "ZA98"}).check();
        cityComboBox.contains(new String[]{"Paris", "Londres", "Tokyo"}).check();

        fixture.mockServerResult(new String[]{"zipCode"}, new String[][]{{"75008"}, {"75116"}});
        cityComboBox.select("Paris");
        zipCodeComboBox.contains(new String[]{"75008", "75116"});

        fixture.mockServerResult(new String[]{"zipCode"},
                                 new String[][]{{"75008"}, {"75116"}, {"Narita"}, {"ZA98"}});
        cityComboBox.select(" ");
        zipCodeComboBox.contains(new String[]{"75008", "75116", "Narita", "ZA98"});
    }


    public void test_interdependanceCroisee() throws Exception {
        initDataFilter();

        filterPanel.limitFilter("cityFilter", "zipCodeFilter");
        filterPanel.limitFilter("zipCodeFilter", "cityFilter");

        Panel panel = new Panel(filterPanel);
        ComboBox zipCodeComboBox = panel.getComboBox("zipCodeFilter");
        ComboBox cityComboBox = panel.getComboBox("cityFilter");

        zipCodeComboBox.contains(new String[]{"75008", "75116", "Narita", "ZA98"}).check();
        cityComboBox.contains(new String[]{"Paris", "Londres", "Tokyo"}).check();

        fixture.mockServerResult(new String[]{"zipCode"}, new String[][]{{"75008"}, {"75116"}});
        cityComboBox.select("Paris");
        zipCodeComboBox.contains(new String[]{"75008", "75116"});

        fixture.mockServerResult(new String[]{"zipCode"},
                                 new String[][]{{"75008"}, {"75116"}, {"Narita"}, {"ZA98"}});
        cityComboBox.select(" ");
        zipCodeComboBox.contains(new String[]{"75008", "75116", "Narita", "ZA98"});
    }


    public void test_renommageBoutonRecherche() {
        filterPanel.setSearchButtonLabel("Cherche Lycos");

        assertEquals("Cherche Lycos", searchButton.getLabel());
    }


    public void test_setWithSearchButton_buttonMustBecomeInvisible() throws Exception {
        assertTrue(searchButton.isVisible());

        filterPanel.setWithSearchButton(false);

        assertFalse(searchButton.isVisible());
    }


    public void test_addDateFilter() throws Exception {
        DateField dateField = new DateField();
        dateField.setName("birthday");
        filterPanel.addDateFilter("Anniversaire", dateField, "birthday");
        dateField.setDate(Date.valueOf("2007-06-05"));
        fixture.mockServerResult(new String[]{"birthday"}, new String[][]{{"2007-06-05 01:02:03.456"}});

        searchButton.click();

        fixture.assertSentRequests(
              "<requests>" +
              "  <select request_id=\"1\">" +
              "    <id>selectAllHandlerId</id>" +
              "    <selector>" +
              "      <field name=\"birthday\">2007-06-05</field>" +
              "    </selector>" +
              "    <attributes>" +
              "      <name>zipCode</name>" +
              "      <name>address</name>" +
              "      <name>name</name>" +
              "      <name>city</name>" +
              "      <name>birthday</name>" +
              "    </attributes>" +
              "  <page num=\"1\" rows=\"1000\"/>" +
              "  </select>" +
              "</requests>");
    }


    public void test_clearPanel() throws Exception {
        fixture.mockServerResult(new String[]{"zipCode"},
                                 new String[][]{{"75008"}, {"75116"}, {"Narita"}, {"ZA98"}});
        filterPanel.addComboFilter("Code postal", "zipCodeFilter", "zipCode", null, true, "selectAllZipCode");
        JComboBox combo = (JComboBox)filterPanel.getFilter("zipCodeFilter");
        combo.setSelectedIndex(3);

        requestTable.firePropertyChange("SqlRequetor.load", true, false);

        Assert.assertEquals(0, combo.getSelectedIndex());
    }


    public void test_retrieveHandlerId() throws Exception {
        fixture.mockServerResult(new String[]{"birthday"}, new String[][]{{"2007-06-05 01:02:03.456"}});

        requestTable.getDataSource().setLoadFactory(new SelectFactory("Another Handler Id"));
        searchButton.click();

        fixture.assertSentRequests(
              "<requests>" +
              "  <select request_id=\"1\">" +
              "    <id>selectAllHandlerId</id>" +
              "    <attributes>" +
              "      <name>zipCode</name>" +
              "      <name>address</name>" +
              "      <name>name</name>" +
              "      <name>city</name>" +
              "      <name>birthday</name>" +
              "    </attributes>" +
              "  <page num=\"1\" rows=\"1000\"/>" +
              "  </select>" +
              "</requests>");

        assertEquals("selectAllHandlerId", requestTable.getDataSource().getLoadFactory().getId());
    }


    private void initDataFilter() {
        filterPanel.addTextFilter("Nom", "nameFilter", "name");
        filterPanel.addTextFilter("Adresse", "addressFilter", "address");
        fixture.mockServerResult(new String[]{"zipCode"},
                                 new String[][]{{"75008"}, {"75116"}, {"Narita"}, {"ZA98"}});
        filterPanel.addComboFilter("Code postal", "zipCodeFilter", "zipCode", null, true, "selectAllZipCode");
        fixture.mockServerResult(new String[]{"cityId", "cityName"}, new String[][]{
              {"FR1", "Paris"},
              {"UK1", "Londres"},
              {"JP1", "Tokyo"}
        });
        filterPanel.addComboFilter("Ville", "cityFilter", "cityId", "cityName", true, "selectAllCity");
    }


    private void assertStateInitialFilter(TextBox nameTextBox,
                                          TextBox addressTextBox,
                                          ComboBox zipCodeComboBox,
                                          ComboBox cityComboBox) throws Exception {
        nameTextBox.textIsEmpty().check();
        addressTextBox.textIsEmpty().check();
        zipCodeComboBox.contains(new String[]{"75008", "75116", "Narita", "ZA98"}).check();
        cityComboBox.contains(new String[]{"Paris", "Londres", "Tokyo"}).check();
    }


    private void assertFilterResult(String[] columnsNames,
                                    String[][] columnsDatas,
                                    TextBox nameTextBox,
                                    int selectedDataId, int expectedRowcount) {
        fixture.mockServerResult(columnsNames, columnsDatas);
        nameTextBox.setText(columnsDatas[selectedDataId][selectedDataId]);
        if (filterPanel.isWithSearchButton()) {
            searchButton.click();
        }
        assertEquals(expectedRowcount, requestTable.getRowCount());
    }


    private void assertFilterResult(String[] columnsNames,
                                    String[][] columnsDatas,
                                    ComboBox comboBox,
                                    String selectedData, int expectedRowcount) {
        fixture.mockServerResult(columnsNames, columnsDatas);
        comboBox.select(selectedData);
        if (filterPanel.isWithSearchButton()) {
            searchButton.click();
        }
        assertEquals(expectedRowcount, requestTable.getRowCount());
    }


    private void assertFilterResult(String[] columnsNames,
                                    String[][] columnsDatas,
                                    TextBox nameTextBox,
                                    int expectedRowcount) {
        fixture.mockServerResult(columnsNames, columnsDatas);
        nameTextBox.setText("");
        if (filterPanel.isWithSearchButton()) {
            searchButton.click();
        }
        assertEquals(expectedRowcount, requestTable.getRowCount());
    }


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        fixture.doSetUp();
        ((WrapperMock)fixture.getServerWrapper()).setBuildDefaultEnabled(false);
        requestTable = new RequestTable();

        Preference pref = new Preference();
        pref.setId("prefId");
        pref.setColumns(Arrays.asList(new Column("name", "Nom"),
                                      new Column("address", "Adresse"),
                                      new Column("zipCode", "Code postal"),
                                      new Column("city", "Ville"),
                                      new Column("birthday", "Anniversaire")));
        pref.setSelectAllId("selectAllHandlerId");
        requestTable.setPreference(pref);

        DefaultGuiContext guiContext = new DefaultGuiContext();
        guiContext.setUser(new UserMock().mockIsAllowedTo(true));
        guiContext.setSender(new Sender(fixture.getOperations()));
        requestTable.getDataSource().setGuiContext(guiContext);

        filterPanel = new FilterPanel(requestTable);
        searchButton = new Panel(filterPanel).getButton("searchButton");
        clearButton = new Panel(filterPanel).getButton("clearButton");
    }


    @Override
    protected void tearDown() throws Exception {
        fixture.doTearDown();
        super.tearDown();
    }
}
