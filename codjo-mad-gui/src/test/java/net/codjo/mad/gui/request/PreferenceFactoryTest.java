package net.codjo.mad.gui.request;
import net.codjo.mad.gui.request.PreferenceFactory.BuildException;
import net.codjo.mad.gui.request.factory.CommandFactory;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.xml.sax.InputSource;
/**
 * Teste la factory des préférences.
 *
 * @author $Author: villard $
 * @version $Revision: 1.21 $
 */
public class PreferenceFactoryTest {
    private static final String PREFERENCE_CONFIG =
          "<?xml version='1.0'?>                                           "
          + "<preferenceList>                                                "
          + "<!-- Hello la famille c'est noel --> "
          + "  <preference id='PortfolioCodificationWindow'"
          + "    detailWindowClassName='"
          + PortfolioCodificationDetailWindow.class.getName() + "'>"
          + "    <!-- Hello la famille c'est noel -->                       "
          + "    <selectByPk>selectCodificationPtfById</selectByPk>"
          + "    <selectAll>selectAllCodificationPtf</selectAll>"
          + "    <insert>newCodificationPtf</insert>"
          + "    <update>updateCodificationPtf</update>"
          + "    <delete>deleteCodificationPtf</delete>"
          + "    <column fieldName='pimsCode' label='Code Pims' minSize='50' maxSize='100'/>"
          + "    <column fieldName='portfolioCode' label='Code portefeuille' minSize='50' maxSize='100'/>"
          + "    <column fieldName='label' label='Libellé' minSize='300' maxSize='0'/>"
          + "    <column fieldName='sicovamCode' label='Code Sicovam' minSize='50' maxSize='100'/>"
          + "  </preference>                                                "
          + "  <preference id='FundPriceWindow'" + "    detailWindowClassName='"
          + PortfolioCodificationDetailWindow.class.getName() + "'>           "
          + "    <selectByPk>selectFundPricefById</selectByPk>                 "
          + "    <selectAll>selectAllFundPrice</selectAll>                     "
          + "    <insert type='command'>newFundPriceCmd</insert>             "
          + "    <update type='command'>updateFundPrice</update>             "
          + "    <delete type='net.codjo.mad.gui.request.factory.CommandFactory'>deleteFundPrice</delete>"
          + "    <requetor>fundPriceRequetorHandler</requetor>"
          + "    <column fieldName='pimsCode' label='Code Pims' maxSize='50'/>"
          + "    <column fieldName='netFundPrice' label='VL' minSize='20'/>"
          + "  </preference>                                                  "
          + "</preferenceList>";
    private static final String PREFERENCE_CONFIG_2 =
          "<?xml version='1.0'?>                                           "
          + "<preferenceList>                                                "
          + "<!-- c'est les vacances !!! --> " + "  <preference id='MyWindowCrosoftWindow'"
          + "    detailWindowClassName='" + MyWindowCrosoft.class.getName() + "'>"
          + "    <!-- c'est les vacances !!! -->                       "
          + "    <selectByPk>selectMyWindowCrosoftById</selectByPk>"
          + "    <selectAll>selectAllMyWindowCrosoft</selectAll>"
          + "    <insert>newMyWindowCrosoft</insert>"
          + "    <update>updateMyWindowCrosoft</update>"
          + "    <delete>deleteMyWindowCrosoft</delete>"
          + "    <column fieldName='pimsCode' label='Code Pims de LU' minSize='66' maxSize='666'/>"
          + "    <column fieldName='portfolioCode' label='Code portefeuille' minSize='50' maxSize='100'/>"
          + "    <column fieldName='label' label='Libellé' minSize='300' maxSize='0'/>"
          + "    <column fieldName='sicovamCode' label='Code Sicovam' minSize='50' maxSize='100'/>"
          + "  </preference>                                                "
          + "</preferenceList>";
    private static final String PREFERENCE_CONFIG_3 =
          "<?xml version='1.0'?>                                           "
          + "<preferenceList>                                                "
          + "<!-- c'est les vacances !!! --> " + "  <preference id='MyWindowCrosoftWindow'"
          + "    detailWindowClassName='" + MyWindowCrosoft.class.getName() + "'>"
          + "    <selectAll>selectAllMyWindowCrosoft</selectAll>"
          + "    <column fieldName='amount' label='' minSize='66' maxSize='666' format='0.00' sorter='Numeric' summable='true'/>"
          + "    <column fieldName='code' label='' minSize='50' maxSize='100' format='# ##0'/>"
          + "    <column fieldName='ok' label='' minSize='300' maxSize='0' sorter='Boolean'/>"
          + "    <column fieldName='withRenderer' label='' renderer='net.codjo.mad.gui.MyRenderer'/>"
          + "  </preference>                                                "
          + "</preferenceList>";
    private static final String PREFERENCE_CONFIG_ERROR =
          "<?xml version='1.0'?>                                           "
          + "<preferenceList>                                                "
          + "  <preference id='PortfolioCodificationWindow'"
          + "    detailWindowClassName='"
          + PortfolioCodificationDetailWindow.class.getName() + "'>"
          + "    <selectByPk>selectCodificationPtfById</selectByPk>"
          + "    <titi>unknown</titi>"
          + "    <column fieldName='pimsCode' label='Code Pims' minSize='50' maxSize='100'/>"
          + "  </preference>                                                "
          + "</preferenceList>";
    private static final String COLUMNS_SIZE_PREFERENCE =
          "<?xml version='1.0'?>                                           "
          + "<preferenceList>                                                "
          + "  <preference id='ColumnsSizeTest'>"
          + "    <column fieldName='preferredSizeColumn' label='' preferredSize='100'/>"
          + "    <column fieldName='noSizeColumn' label=''/>"
          + "    <column fieldName='fullSizeColumn' label='' minSize='15' maxSize='300' preferredSize='150'/>"
          + "  </preference>                                                "
          + "</preferenceList>";
    private static final String HIDDEN_COLUMNS_PREFERENCE =
          "<?xml version='1.0'?>                                               "
          + "<preferenceList>                                                    "
          + "  <preference id='HiddenColumnsTest'>                          "
          + "    <column fieldName='column1' label='' preferredSize='100'/>"
          + "    <hidden>                                                        "
          + "        <column fieldName='field1' label='label1' preferredSize='50'/>                              "
          + "        <column fieldName='field2'/>                              "
          + "    </hidden>                                                       "
          + "  </preference>                                                     "
          + "</preferenceList>                                                   ";
    private static final String HIDDEN_COLUMNS_WITH_BAD_PREFERENCE =
          "<?xml version='1.0'?>                                                 "
          + "<preferenceList>                                                    "
          + "  <preference id='HiddenColumnsWithBadNodeTest'>                    "
          + "    <hidden>                                                        "
          + "        <column fieldName='field1'/>                                "
          + "        <bad/>                                                      "
          + "    </hidden>                                                       "
          + "  </preference>                                                     "
          + "</preferenceList>                                                   ";
    private static final String ENTITY_PREFERENCE =
          "<?xml version='1.0'?>                                                 "
          + "<preferenceList>                                                    "
          + "  <preference id='EntityTest'>                                      "
          + "    <entity>MyEntity</entity>                                       "
          + "  </preference>                                                     "
          + "</preferenceList>                                                   ";
    private static final String PREFERENCE_CONFIG_4 =
          "<?xml version='1.0'?>                                           "
          + "<preferenceList>                                                "
          + "<!-- c'est les vacances !!! --> " + "  <preference id='MyWindowCrosoftWindow'"
          + "    detailWindowClassName='" + MyWindowCrosoft.class.getName() + "'>"
          + "    <selectAll>selectAllMyWindowCrosoft</selectAll>"
          + "    <column fieldName='amount' label='' minSize='66' maxSize='666' format='0.00' sorter='Numeric' summableLabel='Total' />"
          + "    <column fieldName='code' label='' minSize='50' maxSize='100' format='# ##0'  summable='true' />"
          + "    <column fieldName='ok' label='' minSize='300' maxSize='0' sorter='Boolean'/>"
          + "    <column fieldName='withRenderer' label='' renderer='net.codjo.mad.gui.MyRenderer'/>"
          + "  </preference>                                                "
          + "</preferenceList>";
    private static final String PREFERENCE_CONFIG_5 =
          "<?xml version='1.0'?>                                           "
          + "<preferenceList>                                                "
          + "<!-- c'est les vacances !!! --> " + "  <preference id='MyWindowCrosoftWindow'"
          + "    detailWindowClassName='" + MyWindowCrosoft.class.getName() + "'>"
          + "    <selectAll>selectAllMyWindowCrosoft</selectAll>"
          + "    <column fieldName='amount' label='' minSize='66' maxSize='666' format='0.00' sorter='Numeric' summable='true' summableLabel='Tototatal' />"
          + "    <column fieldName='code' label='' minSize='50' maxSize='100' format='# ##0'/>"
          + "    <column fieldName='ok' label='' minSize='300' maxSize='0' sorter='Boolean'/>"
          + "    <column fieldName='withRenderer' label='' renderer='net.codjo.mad.gui.MyRenderer'/>"
          + "  </preference>                                                "
          + "</preferenceList>";


    @Test
    public void testBuildPreferenceManager() {
        PreferenceFactory.loadMapping(new InputSource(new StringReader(PREFERENCE_CONFIG)));

        // Test Preference PortfolioCodificationWindow
        Preference prefPtfCodif = PreferenceFactory.getPreference("PortfolioCodificationWindow");
        assertNotNull(prefPtfCodif);
        assertEquals(4, prefPtfCodif.getColumns().size());
        assertEquals(PortfolioCodificationDetailWindow.class, prefPtfCodif.getDetailWindowClass());
        assertEquals("pimsCode", prefPtfCodif.getColumns().get(0).getFieldName());
        assertEquals("Code Pims", prefPtfCodif.getColumns().get(0).getLabel());
        assertEquals("50", Integer.toString(prefPtfCodif.getColumns().get(0).getMinSize()));
        assertEquals("100", Integer.toString(prefPtfCodif.getColumns().get(0).getMaxSize()));
        Preference prefFundPrice = PreferenceFactory.getPreference("FundPriceWindow");
        assertNotNull(prefFundPrice);
        assertEquals(2, prefFundPrice.getColumns().size());

        // Test Preference FundPriceWindow
        Preference customPref = PreferenceFactory.getPreference("FundPriceWindow");
        assertEquals(CommandFactory.class, customPref.getDelete().getClass());
        assertEquals(CommandFactory.class, customPref.getInsert().getClass());
        assertEquals(CommandFactory.class, customPref.getUpdate().getClass());
        assertNotNull(customPref.getRequetor());
        assertEquals("fundPriceRequetorHandler", customPref.getRequetor().getId());

        // Test de l'ajout de la préférence MyWindowCrosoftWindow au PreferenceFactory à partir d'un "fichier"
        PreferenceFactory.addMapping(new InputSource(new StringReader(PREFERENCE_CONFIG_2)));

        Preference prefCrosoft = PreferenceFactory.getPreference("MyWindowCrosoftWindow");
        assertNotNull(prefCrosoft);
        assertEquals(4, prefCrosoft.getColumns().size());
        assertEquals(MyWindowCrosoft.class, prefCrosoft.getDetailWindowClass());
        assertEquals("pimsCode", prefCrosoft.getColumns().get(0).getFieldName());
        assertEquals("Code Pims de LU", prefCrosoft.getColumns().get(0).getLabel());
        assertEquals("66", Integer.toString(prefCrosoft.getColumns().get(0).getMinSize()));
        assertEquals("666", Integer.toString(prefCrosoft.getColumns().get(0).getMaxSize()));

        // Test de l'ajout d'une préférence créé à la "main" au PreferenceFactory
        PreferenceFactory.addPreference(getExecutionListLauncherPreference());
        Preference prefDataProcess
              = PreferenceFactory.getPreference("DataProcessExecutionListLauncherWindow");
        assertNotNull(prefDataProcess);
        assertEquals(5, prefDataProcess.getColumns().size());
        assertEquals("selectAllExecutionListStatusByRepositoryAndFamily",
                     prefDataProcess.getSelectAll().getId());
        assertEquals("DataProcessExecutionListLauncherWindow", prefDataProcess.getId());
    }


    @Test
    public void test_formatSortAndRenderer() {
        PreferenceFactory.loadMapping(new InputSource(new StringReader(PREFERENCE_CONFIG_3)));

        Preference prefCrosoft = PreferenceFactory.getPreference("MyWindowCrosoftWindow");
        assertNotNull(prefCrosoft);

        assertEquals("0.00", prefCrosoft.getColumns().get(0).getFormat());
        assertEquals("Numeric", prefCrosoft.getColumns().get(0).getSorter());

        assertEquals("# ##0", prefCrosoft.getColumns().get(1).getFormat());
        assertNull(prefCrosoft.getColumns().get(1).getSorter());

        assertNull(prefCrosoft.getColumns().get(2).getFormat());
        assertEquals("Boolean", prefCrosoft.getColumns().get(2).getSorter());

        assertNull(prefCrosoft.getColumns().get(3).getFormat());
        assertEquals("net.codjo.mad.gui.MyRenderer", prefCrosoft.getColumns().get(3).getRenderer());
    }


    @Test
    public void test_summableLabel() {
        PreferenceFactory.loadMapping(new InputSource(new StringReader(PREFERENCE_CONFIG_4)));

        Preference prefCrosoft = PreferenceFactory.getPreference("MyWindowCrosoftWindow");

        assertEquals("Total", prefCrosoft.getColumns().get(0).getSummableLabel());
        assertTrue(prefCrosoft.getColumns().get(1).isSummable());
    }


    @Test
    public void test_exceptionSummableLabel() {
        try {
            PreferenceFactory.loadMapping(new InputSource(new StringReader(PREFERENCE_CONFIG_5)));
        }
        catch (BuildException e) {
            assertEquals("Impossible d'utiliser les balises summable et summableLabel en même temps : amount",
                         e.getMessage());
        }
    }


    @Test
    public void test_summable() {
        PreferenceFactory.loadMapping(new InputSource(new StringReader(PREFERENCE_CONFIG_3)));

        Preference prefCrosoft = PreferenceFactory.getPreference("MyWindowCrosoftWindow");

        assertTrue(prefCrosoft.getColumns().get(0).isSummable());
        assertFalse(prefCrosoft.getColumns().get(1).isSummable());
    }


    @Test
    public void test_columnsSize() {
        PreferenceFactory.loadMapping(new InputSource(new StringReader(COLUMNS_SIZE_PREFERENCE)));

        Preference preference = PreferenceFactory.getPreference("ColumnsSizeTest");
        assertNotNull(preference);

        checkColumnSizes(preference, "preferredSizeColumn", 0, 0, 100);
        checkColumnSizes(preference, "noSizeColumn", 0, 0, 0);
        checkColumnSizes(preference, "fullSizeColumn", 15, 300, 150);
    }


    @Test
    public void test_hiddenColumns() {
        PreferenceFactory.loadMapping(new InputSource(new StringReader(HIDDEN_COLUMNS_PREFERENCE)));

        Preference preference = PreferenceFactory.getPreference("HiddenColumnsTest");
        assertNotNull(preference);

        assertEquals(2, preference.getHiddenColumns().size());

        assertEquals(preference.getHiddenColumns().get(0).getFieldName(), "field1");
        assertEquals(preference.getHiddenColumns().get(0).getLabel(), "label1");
        assertEquals(preference.getHiddenColumns().get(0).getPreferredSize(), 50);
        assertEquals(preference.getHiddenColumns().get(1).getFieldName(), "field2");

        assertEquals(1, preference.getColumns().size());

        assertEquals("column1", preference.getColumnsName()[0]);
    }


    @Test
    public void test_hiddenColumns_badNode() {
        try {
            PreferenceFactory.loadMapping(new InputSource(
                  new StringReader(HIDDEN_COLUMNS_WITH_BAD_PREFERENCE)));
            fail();
        }
        catch (PreferenceFactory.BuildException ex) {
            assertEquals("Balise inconnue: bad(null)", ex.getLocalizedMessage());
        }
    }


    @Test
    public void test_entity() {
        PreferenceFactory.loadMapping(new InputSource(new StringReader(ENTITY_PREFERENCE)));

        Preference preference = PreferenceFactory.getPreference("EntityTest");
        assertNotNull(preference);

        assertEquals("MyEntity", preference.getEntity());
    }


    @Test
    public void test_buildPreferenceManager_error() {
        try {
            PreferenceFactory.loadMapping(new InputSource(new StringReader(PREFERENCE_CONFIG_ERROR)));
            fail("Balise titi inconnue");
        }
        catch (PreferenceFactory.BuildException ex) {
            ; // Impossible de charger les preferences !
        }
    }


    @Test
    public void test_buildPreferenceFileNotFound() {
        try {
            PreferenceFactory.loadMapping(null);
            fail();
        }
        catch (IllegalArgumentException e) {
            assertEquals("configPreference ne peut pas être null.", e.getMessage());
        }
    }


    @Test
    public void test_clearPreferences() {
        PreferenceFactory.loadMapping(new InputSource(new StringReader(PREFERENCE_CONFIG_3)));
        assertNotNull(PreferenceFactory.getPreference("MyWindowCrosoftWindow"));

        PreferenceFactory.clearPreferences();
        try {
            PreferenceFactory.getPreference("MyWindowCrosoftWindow");
            fail();
        }
        catch (Exception e) {
            assertEquals("L'identifiant 'MyWindowCrosoftWindow' est inconnu.", e.getMessage());
        }
    }


    private static void checkColumnSizes(Preference preference, String columnName,
                                         int expectedMin, int expectedMax, int expectedPreferred) {
        assertEquals(expectedMin, getColumn(preference, columnName).getMinSize());
        assertEquals(expectedMax, getColumn(preference, columnName).getMaxSize());
        assertEquals(expectedPreferred, getColumn(preference, columnName).getPreferredSize());
    }


    private static Column getColumn(Preference preference, String name) {
        return preference.getColumns().get(preference.getColumnIndex(name));
    }


    /**
     * Création d'une préférence pour les tests
     *
     * @return créé et retourne une preference
     */
    private static Preference getExecutionListLauncherPreference() {
        Preference preference = new Preference();
        List<Column> columns = new ArrayList<Column>();
        columns.add(new Column("priority", "Priorité", 0, 50, 25));
        columns.add(new Column("executionListType", "Type", 0, 30, 15));
        columns.add(new Column("executionListName", "Titre", 0, 260, 130));
        columns.add(new Column("status", "Statut", 0, 50, 25));
        columns.add(new Column("executionDate", "Date et heure", 0, 160, 80));
        preference.setColumns(columns);
        preference.setSelectAllId("selectAllExecutionListStatusByRepositoryAndFamily");
        preference.setId("DataProcessExecutionListLauncherWindow");
        return preference;
    }


    /**
     * Mock une fenetre detail.
     */
    private static class PortfolioCodificationDetailWindow {
        private PortfolioCodificationDetailWindow() {
        }
    }

    /**
     * Une autre fenêtre detail pour les tests.
     */
    private static class MyWindowCrosoft {
        private MyWindowCrosoft() {
        }
    }
}
