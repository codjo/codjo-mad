package net.codjo.mad.gui.request;
import net.codjo.gui.toolkit.number.NumberField;
import net.codjo.mad.client.request.FieldsList;
import net.codjo.mad.client.request.InsertRequest;
import net.codjo.mad.client.request.Request;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.client.request.RequestSender;
import net.codjo.mad.client.request.Result;
import net.codjo.mad.client.request.Row;
import net.codjo.mad.client.request.UpdateRequest;
import net.codjo.mad.client.request.util.RequestTestHelper;
import net.codjo.mad.common.structure.DefaultStructureReader;
import static net.codjo.mad.gui.request.AbstractDataSource.NULL;
import net.codjo.mad.gui.request.factory.SelectFactory;
import net.codjo.mad.gui.request.factory.UpdateFactory;
import net.codjo.mad.gui.request.util.MultiRequestsHelper;
import net.codjo.security.common.api.UserMock;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTextField;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;
import org.uispec4j.TextBox;
import org.uispec4j.assertion.Assertion;
/**
 * Classe de test de <code>DetailDataSource</code>.
 *
 * @author $Author: catteao $
 * @version $Revision: 1.20 $
 */
public class DetailDataSourceTest extends AbstractDataSourceTestCase {
    private RequestTestHelper requestHelper;


    @Override
    protected void setUp() {
        super.setUp();

        requestHelper = new RequestTestHelper();
    }


    @Override
    protected void tearDown() {
        requestHelper.tearDown();
    }


    public void test_selectedRow_default() throws Exception {
        DetailDataSource dataSource = buildDataSource(0);

        Row selectedRow = dataSource.getSelectedRow();
        assertNotNull(selectedRow);
        assertEquals(0, selectedRow.getFieldCount());
    }


    public void test_clear() throws Exception {
        DetailDataSource dataSource = buildDataSource(0);
        dataSource.setSelector(new FieldsList("fieldName", "value"));

        dataSource.clear();

        assertNull(dataSource.getSelector());
    }


    public void test_selectedRow_declare() throws Exception {
        DetailDataSource dataSource = buildDataSource(0);

        JTextField field = new JTextField();
        dataSource.declare("col1", field);

        dataSource.declare("col2");

        Row selectedRow = dataSource.getSelectedRow();
        assertNotNull(selectedRow);

        assertEquals(2, selectedRow.getFieldCount());
        assertTrue(selectedRow.contains("col1"));
        assertTrue(selectedRow.contains("col2"));
        assertEquals(NULL, selectedRow.getFieldValue("col1"));
        assertEquals(NULL, selectedRow.getFieldValue("col2"));
    }


    public void test_selectedRow_declare_WithDefaults()
          throws Exception {
        DetailDataSource dataSource = buildDataSource(0);

        JTextField field = new JTextField();
        field.setText("a");
        dataSource.declare("col1", field);

        Row selectedRow = dataSource.getSelectedRow();
        assertEquals("a", selectedRow.getFieldValue("col1"));
    }


    public void test_selectedRow_load() throws Exception {
        DetailDataSource dataSource = buildDataSource(0);

        dataSource.declare("col1");
        dataSource.declare("col2");

        Row loadedRow = new Row();
        loadedRow.addField("col1", "a");
        loadedRow.addField("col2", "b");

        Result loadResult = new Result();
        loadResult.addRow(loadedRow);
        dataSource.setLoadResult(loadResult);

        Row selectedRow = dataSource.getSelectedRow();
        assertNotNull(selectedRow);
        assertNotSame(loadedRow, selectedRow);
        assertEquals(2, selectedRow.getFieldCount());
        assertEquals("a", selectedRow.getFieldValue("col1"));
        assertEquals("b", selectedRow.getFieldValue("col2"));
    }


    public void test_selectedRow_load_noDeclare() throws Exception {
        DetailDataSource dataSource = buildDataSource(0);

        Row loadedRow = new Row();
        loadedRow.addField("col1", "a");
        loadedRow.addField("col2", "b");

        Result loadResult = new Result();
        loadResult.addRow(loadedRow);
        dataSource.setLoadResult(loadResult);

        Row selectedRow = dataSource.getSelectedRow();
        assertNotNull(selectedRow);
        assertNotSame(loadedRow, selectedRow);
        assertEquals(2, selectedRow.getFieldCount());
        assertEquals("a", selectedRow.getFieldValue("col1"));
        assertEquals("b", selectedRow.getFieldValue("col2"));
    }


    public void test_selectedRow_set() throws Exception {
        DetailDataSource dataSource = buildDataSource(0);

        dataSource.declare("col1");

        JTextField fieldCol2 = new JTextField();
        dataSource.declare("col2", fieldCol2);

        dataSource.setFieldValue("col1", "a");
        fieldCol2.setText("b");

        Row selectedRow = dataSource.getSelectedRow();
        assertNotNull(selectedRow);
        assertEquals(2, selectedRow.getFieldCount());
        assertEquals("a", selectedRow.getFieldValue("col1"));
        assertEquals("b", selectedRow.getFieldValue("col2"));
    }


    public void test_selectedRow_save() throws Exception {
        DetailDataSource helper = new DetailDataSource(guiContext, new RequestSender(), null, null,
                                                       pref.getInsert());

        helper.declare("sicovamCode");
        helper.setFieldValue("sicovamCode", "nvSicoCode");

        // Init Le simulateur de serveur
        requestHelper.setRequest(new Request[]{buildInsertRequest()});
        requestHelper.setResult(getResult());
        requestHelper.activate();

        // Lance l'enregistrement
        helper.save();

        // Verification
        requestHelper.verify();

        assertEquals("12", helper.getFieldValue("sicovamCode"));
        assertEquals("12", helper.getSelectedRow().getFieldValue("sicovamCode"));
    }


    /**
     * Test que le helper enivoie un PropertyChangeEvent quand l'IHM change.
     */
    public void test_firePropertyChangeEvent() throws Exception {
        DetailDataSource helper = buildDataSource(0);

        JTextField field = new JTextField();
        helper.declare("sicovamCode", field);
        FakeChangeListener listener = new FakeChangeListener();
        helper.addPropertyChangeListener(listener);
        field.setText("new");

        assertTrue(listener.hasBeenCalled());
        assertEquals(1, listener.nbOfCall);
        assertEquals("sicovamCode", listener.evt.getPropertyName());
        assertEquals(NULL, listener.evt.getOldValue());
        assertEquals("new", listener.evt.getNewValue());
    }


    /**
     * Teste que le helper envoie un PropertyChangeEvent quand l'IHM change.
     */
    public void test_fireUndoableEditEvent() throws Exception {
        DetailDataSource helper = buildDataSource(0);

        JTextField field = new JTextField();
        helper.declare("sicovamCode", field);
        FakeUndoListener listener = new FakeUndoListener();
        helper.addUndoableEditListener(listener);
        field.setText("new");

        assertTrue(listener.hasBeenCalled());
        assertEquals(1, listener.nbOfCall);

        listener.undoMngr.undo();

        assertEquals("", field.getText());
    }


    /**
     * Test que le helper permet de recuperer la valeur d'un champ.
     */
    public void test_getFieldValue() throws Exception {
        DetailDataSource helper = new DetailDataSource(guiContext, new RequestSender(), null, null,
                                                       pref.getInsert());
        JTextField field = new JTextField();

        helper.declare("sicovamCode", field);
        field.setText("nvSicoCode");

        assertEquals("nvSicoCode", helper.getFieldValue("sicovamCode"));
    }


    public void test_getFieldValue_failure() throws Exception {
        DetailDataSource helper = new DetailDataSource(guiContext);
        try {
            helper.getFieldValue("unknownFieldName");
            fail();
        }
        catch (IllegalArgumentException ex) {
            assertEquals("Le champ n'est pas défini : unknownFieldName", ex.getMessage());
        }
    }


    public void test_isFieldNull() throws Exception {
        DetailDataSource dataSource = new DetailDataSource(guiContext, new RequestSender(), null, null,
                                                           pref.getInsert());

        dataSource.declare("sicovamCode", new JTextField());

        dataSource.setFieldValue("sicovamCode", NULL);
        assertTrue(dataSource.isFieldNull("sicovamCode"));

        dataSource.setFieldValue("sicovamCode", "my value");
        assertFalse(dataSource.isFieldNull("sicovamCode"));
    }


    /**
     * Test que le helper n'envoie pas de requête qd il n'y a pas de changement des données.
     */
    public void test_hasBeenUpdated() throws Exception {
        DetailDataSource helper = buildDataSource(0, pref.getSelectByPk(), pref.getUpdate());

        JTextField field = new JTextField();
        helper.declare("sicovamCode", field);

        helper.setLoadResult(getDetailResult());

        assertFalse(helper.hasBeenUpdated());
        field.setText("newValue");
        assertTrue(helper.hasBeenUpdated());
    }


    public void test_hasBeenUpdated_byFieldName() throws Exception {
        DetailDataSource helper = buildDataSource(0, pref.getSelectByPk(), pref.getUpdate());

        JTextField field = new JTextField();
        helper.declare("sicovamCode", field);
        helper.declare("isinCode");

        helper.setLoadResult(getDetailResult());

        assertFalse(helper.hasBeenUpdated("sicovamCode"));
        assertFalse(helper.hasBeenUpdated("isinCode"));
        field.setText("newValue");
        assertTrue(helper.hasBeenUpdated("sicovamCode"));
        assertFalse(helper.hasBeenUpdated("isinCode"));
    }


    /**
     * Test que le helper indique un changement des qu'un champ est ajoute apres le chargement, et que sa
     * valeur est differente de la valeur initiale.
     */
    public void test_guiHasChanged_newField() throws Exception {
        DetailDataSource helper = buildDataSource(0, pref.getSelectByPk(), pref.getUpdate());
        JTextField field = new JTextField();
        helper.declare("sicovamCode", field);
        helper.setLoadResult(getDetailResult());

        JTextField newField = new JTextField("initiale");
        helper.declare("notLoadedField", newField);
        assertFalse(helper.hasBeenUpdated());

        newField.setText("valeur changé");
        assertTrue(helper.hasBeenUpdated());

        newField.setText("initiale");
        assertFalse(helper.hasBeenUpdated());
    }


    /**
     * Test que la méthode renvoie false qd il n'y a pas de chargement de données et qu'aucun champs ne
     * possède une valeur (sauf chaine vide).
     */
    public void test_guiHasChanged_noLoad() throws Exception {
        DetailDataSource helper = buildDataSource(0, null, pref.getUpdate());

        JTextField field = new JTextField();
        field.setText("initialValue");
        helper.declare("sicovamCode", field);

        assertTrue(!helper.hasBeenUpdated());
        field.setText("value");
        assertTrue(helper.hasBeenUpdated());
        field.setText("initialValue");
        assertTrue(!helper.hasBeenUpdated());
    }


    /**
     * Test que le helper MAJ les bons attributs dans l'IHM.
     */
    public void test_loadDetail() throws Exception {
        DetailDataSource helper = buildDataSource(0);
        JTextField field = new JTextField();
        helper.declare("sicovamCode", field);
        requestHelper.setRequest(new Request[]{helper.buildSelectRequest()});
        requestHelper.setResult(getResult());
        requestHelper.activate();

        helper.load();

        requestHelper.verify();
        assertEquals("12", field.getText());

        assertTrue("ihm non modifié", !helper.hasBeenUpdated());
        assertEquals("Prec addSaveRequestTo non gardé", null, helper.getSaveResult());
    }


    /**
     * Teste que la cohérence du DetailDataSource avec plus de 1 ligne chargée
     */
    public void test_loadDetailWithSeveralRows() throws Exception {

        DetailDataSource helper = buildDataSource(-1, pref.getSelectAll(), null);

        JTextField pimsCodeField = new JTextField();
        helper.declare("pimsCode", pimsCodeField);
        JTextField isinCodeField = new JTextField();
        helper.declare("isinCode", isinCodeField);
        JTextField sicovamCodeField = new JTextField();
        helper.declare("sicovamCode", sicovamCodeField);

        requestHelper.setRequest(new Request[]{helper.buildSelectRequest()});
        requestHelper.setResult(get2Results());
        requestHelper.activate();

        helper.load();
        //requestHelper.verify();
        assertEquals("999", pimsCodeField.getText());
        assertEquals("1", isinCodeField.getText());
        assertEquals("12", sicovamCodeField.getText());

        assertTrue("ihm non modifié", !helper.hasBeenUpdated());
        assertEquals("Prec addSaveRequestTo non gardé", null, helper.getSaveResult());

        //On passe au detail du 2nd resultat
        helper.setSelectedRowIndex(1);
        helper.updateGuiFields();

        //requestHelper.verify();
        assertEquals("1000", pimsCodeField.getText());
        assertEquals("2", isinCodeField.getText());
        assertEquals("13", sicovamCodeField.getText());
    }


    /**
     * Teste que la cohérence du DetailDataSource avec plus de 1 ligne chargée
     */
    public void test_saveDetailForSpecificRow() throws Exception {
        DetailDataSource helper = buildDataSource(-1, pref.getSelectAll(), pref.getUpdate());

        JTextField pimsCodeField = new JTextField();
        helper.declare("pimsCode", pimsCodeField);
        JTextField isinCodeField = new JTextField();
        helper.declare("isinCode", isinCodeField);
        JTextField sicovamCodeField = new JTextField();
        helper.declare("sicovamCode", sicovamCodeField);

        requestHelper.setRequest(new Request[]{helper.buildSelectRequest()});
        requestHelper.setResult(get2Results());
        requestHelper.activate();
        helper.load();
        requestHelper.verify();

        //On passe au detail du 2nd resultat
        helper.setSelectedRowIndex(1);
        helper.updateGuiFields();
        sicovamCodeField.setText("88");

        requestHelper = new RequestTestHelper();
        requestHelper.setRequest(new Request[]{buildUpdateRequest("1000", "88", "2")});
        requestHelper.setResult(getUpdateForSpecificRow());
        requestHelper.activate();
        helper.save();
        requestHelper.verify();
    }


    /**
     * Test que le helper MAJ les bons attributs dans l'IHM.
     */
    public void test_loadDetail_error() throws Exception {
        DetailDataSource helper = buildDataSource(99);
        try {
            helper.load();
            fail("L'indice de ligne 99 n'est pas definit");
        }
        catch (NullPointerException ex) {
        }
    }


    /**
     * Test que les evt sont lance correctement.
     */
    public void test_loadDetail_event() throws Exception {
        DetailDataSource helper = buildDataSource(0);
        JTextField field = new JTextField();
        helper.declare("sicovamCode", field);
        requestHelper.setRequest(new Request[]{helper.buildSelectRequest()});
        requestHelper.setResult(getResult());
        requestHelper.activate();

        FakeDataSourceListener listener = new FakeDataSourceListener();
        helper.addDataSourceListener(listener);

        helper.load();

        requestHelper.verify();
        assertEquals("12", field.getText());
        assertTrue("beforeLoadEvent called", listener.beforeLoadEvent);
        assertNotNull("beforeLoadEvent called avec MRH",
                      listener.lastBeforeLoadEvent.getMultiRequestHelper());
        assertTrue("loadEvent called", listener.loadEvent);
    }


    /**
     * Test que les evt sont lance correctement lorsque le resultat ne contient aucune ligne.
     */
    public void test_loadDetail_event_norow() throws Exception {
        DetailDataSource datasource = buildDataSource(0);
        datasource.declare("sicovamCode");

        FakeDataSourceListener listener = new FakeDataSourceListener();
        datasource.addDataSourceListener(listener);

        // Positionne un result sans ligne
        Result rs = new Result();
        rs.addPrimaryKey("pimsCode");
        datasource.setLoadResult(rs);

        assertTrue("beforeLoadEvent called", listener.beforeLoadEvent);
        assertTrue("loadEvent called", listener.loadEvent);

        assertNull("result du DS", datasource.getLoadResult());
        assertNull("result pour beforeLoadEvent", listener.lastBeforeLoadEvent.getResult());
        assertNull("result pour LoadEvent", listener.lastLoadEvent.getResult());
    }


    /**
     * Test que le helper MAJ les bons attributs dans l'IHM.
     */
    public void test_loadDetail_noSelect() throws Exception {
        pref.setSelectByPk(null);

        DetailDataSource helper = buildDataSource(0);

        JTextField field = new JTextField();
        helper.declare("sicovamCode", field);
        field.setText("empty");

        helper.load();

        assertEquals("empty", field.getText());
    }


    /**
     * Test que le helper maj l'IHM en tenant compte de l'ordre.
     */
    public void test_load_updateOrder() throws Exception {
        DetailDataSource helper = buildDataSource(0);

        JTextField dbFieldNamefield = new JTextField();
        helper.declare("dbFieldName", dbFieldNamefield);

        JTextField labelfield = new JTextField();
        labelfield.putClientProperty(DetailDataSource.UPDATE_PRIORITY, DetailDataSource.LOW_PRIORITY);
        helper.declare("labelfield", labelfield);

        JTextField dbTableNamefield = new JTextField();
        helper.declare("dbTableName", dbTableNamefield);
        dbTableNamefield.putClientProperty(DetailDataSource.UPDATE_PRIORITY, DetailDataSource.HIGH_PRIORITY);

        FakeChangeListener listener = new FakeChangeListener();
        helper.addPropertyChangeListener(listener);

        helper.setLoadResult(getDetailResultForUpdateOrder());

        assertTrue(listener.hasBeenCalled());
        assertEquals(4, listener.nbOfCall);
        assertEquals("[selectedRow, dbTableName, dbFieldName, labelfield]", listener.fieldUpdated.toString());
    }


    public void test_setLoadManager_doLoad() throws Exception {
        Mock.LoadManager loadManagerMock = new Mock.LoadManager();
        DetailDataSource dataSource = buildDataSource(0);
        dataSource.setLoadManager(loadManagerMock);
        assertSame(loadManagerMock, dataSource.getLoadManager());

        FakeDataSourceListener listener = new FakeDataSourceListener();
        dataSource.addDataSourceListener(listener);

        dataSource.load();

        assertTrue(loadManagerMock.doLoadCalled);
        assertTrue(listener.beforeLoadEvent);
        assertTrue(listener.loadEvent);
    }


    public void test_setLoadManager_addLoadRequestTo()
          throws Exception {
        Mock.LoadManager loadManagerMock = new Mock.LoadManager();
        DetailDataSource dataSource = buildDataSource(0);
        dataSource.setLoadManager(loadManagerMock);

        dataSource.addLoadRequestTo(null);

        assertTrue(loadManagerMock.addLoadRequestToIsCalled);
    }


    /**
     * Test que le helper est capable de gérer des attributs sans représentation IHM (exemple en mode
     * insert).
     */
    public void test_saveDetail_AttributeWithNoGui()
          throws Exception {
        DetailDataSource helper = new DetailDataSource(guiContext, new RequestSender(), null, null,
                                                       pref.getInsert());

        helper.declare("sicovamCode");
        assertEquals(NULL, helper.getFieldValue("sicovamCode"));
        helper.setFieldValue("sicovamCode", "nvSicoCode");

        // Init Le simulateur de serveur pour receptionner la requete update
        requestHelper.setRequest(new Request[]{buildInsertRequest()});
        requestHelper.setResult(getResult());
        requestHelper.activate();

        // Lance l'enregistrement
        helper.save();

        // Verification
        requestHelper.verify();

        assertEquals("12", helper.getFieldValue("sicovamCode"));
    }


    /**
     * Test que le helper gere correctement les erreurs.
     */
    public void test_saveDetail_errorManagement()
          throws Exception {
        String errorResult =
              "<?xml version=\"1.0\"?>" + "<results>" + "  <error request_id = \"258879\">"
              + "    <label>une erreur</label>" + "    <type>class java.lang.RuntimeException</type>"
              + "  </error>" + "</results>";

        DetailDataSource helper = buildDataSource(0, pref.getSelectByPk(), pref.getUpdate());
        helper.setLoadResult(getDetailResult());
        JTextField field = new JTextField();

        helper.declare("sicovamCode", field);
        field.setText("nvSicoCode");

        // Init Le simulateur de serveur pour receptionner la requete update
        requestHelper.setRequest(new Request[]{buildUpdateRequest()});
        requestHelper.setResult(errorResult);
        requestHelper.activate();

        // Lance l'enregistrement
        try {
            helper.save();
            fail("une erreur est retourné par le serveur");
        }
        catch (RequestException ex) {
        }

        // Verification
        requestHelper.verify();
    }


    /**
     * Test que le helper envoie la requete d'update correctement.
     */
    public void test_saveDetail_insert() throws Exception {
        DetailDataSource helper = new DetailDataSource(guiContext, new RequestSender(), null, null,
                                                       pref.getInsert());
        JTextField field = new JTextField();

        helper.declare("sicovamCode", field);
        field.setText("nvSicoCode");

        // Init Le simulateur de serveur pour receptionner la requete update
        requestHelper.setRequest(new Request[]{buildInsertRequest()});
        requestHelper.setResult(getUpdateResult());
        requestHelper.activate();

        // Lance l'enregistrement
        helper.save();

        // Verification
        requestHelper.verify();

        assertEquals("999", helper.getFieldValue("pimsCode"));
    }


    /**
     * teste qu'il n'y a pas de addSaveRequestTo qd il n'y a pas de changement d'IHM.
     */
    public void test_saveDetail_noGuiChange() throws Exception {
        DetailDataSource helper = buildDataSource(0, pref.getSelectByPk(), pref.getUpdate());
        JTextField field = new JTextField();
        helper.declare("sicovamCode", field);

        helper.setLoadResult(getDetailResult());
        // Lance l'enregistrement
        helper.save();
    }


    /**
     * Test que le helper n'envoie pas de requete addSaveRequestTo s'il n'y a pas de factory configurée.
     */
    public void test_saveDetail_noSave() throws Exception {
        DetailDataSource helper = buildDataSource(0, pref.getSelectByPk(), null);
        JTextField field = new JTextField();
        helper.declare("sicovamCode", field);
        field.setText("nvSicoCode");

        // Lance l'enregistrement
        helper.save();
    }


    /**
     * Test que le helper envoie la requete d'update correctement.
     */
    public void test_saveDetail_update() throws Exception {
        DetailDataSource helper = buildDataSource(0, pref.getSelectByPk(), pref.getUpdate());
        helper.setLoadResult(getDetailResult());
        JTextField field = new JTextField();

        helper.declare("sicovamCode", field);
        field.setText("nvSicoCode");

        // Init Le simulateur de serveur pour receptionner la requete update
        requestHelper.setRequest(new Request[]{buildUpdateRequest()});
        requestHelper.setResult(getUpdateResult());
        requestHelper.activate();

        // Lance l'enregistrement
        helper.save();

        // Verification
        requestHelper.verify();
    }


    /**
     * Test que le helper envoie la requete d'update correctement, et que le getFieldValue marche même si le
     * resultat du addSaveRequestTo ne renvoie pas le champ.
     */
    public void test_saveDetail_updateAndGetField()
          throws Exception {
        DetailDataSource helper = buildDataSource(0, pref.getSelectByPk(), pref.getUpdate());
        helper.setLoadResult(getDetailResult());

        JTextField field = new JTextField();
        helper.declare("sicovamCode", field);
        field.setText("nvSicoCode");

        // Init Le simulateur de serveur pour receptionner la requete update
        requestHelper.setRequest(new Request[]{buildUpdateRequest()});
        requestHelper.setResult(getUpdateResult());
        requestHelper.activate();

        // Lance l'enregistrement
        helper.save();

        // Verification
        requestHelper.verify();

        // Verification que getFieldValue sur sicovamCode ne fait pas d'erreur
        assertEquals("nvSicoCode", helper.getFieldValue("sicovamCode"));
    }


    /**
     * Test que le helper gere correctement la PK lors d'un update avec un des selecteurs != de la pk.
     */
    public void test_saveDetail_update_Pk() throws Exception {
        DetailDataSource helper = buildDataSource(0, new SelectFactory("selectValid"), pref.getUpdate());

        JTextField field = new JTextField();
        helper.declare("sicovamCode", field);

        JTextField pimsCodeField = new JTextField();
        helper.declare("pimsCode", pimsCodeField);

        // Preperation du chargement
        FieldsList selector = new FieldsList();
        selector.addField("date", "2002-12-01");
        helper.setSelector(selector);

        // Simulation load initial
        helper.setLoadResult(getDetailResult());

        // Modification dans l'IHM
        field.setText("nvSicoCode");
        pimsCodeField.setText("NOEL");

        // Init Le simulateur de serveur pour receptionner la requete update
        requestHelper.setRequest(new Request[]{buildUpdateRequest()});
        requestHelper.setResult(getUpdateResult());
        requestHelper.activate();

        // Lance l'enregistrement
        helper.save();

        // Verification
        requestHelper.verify();
    }


    /**
     * Test que le helper envoie la requete d'update correctement.
     */
    public void test_saveDetail_update_withReadOnlyField()
          throws Exception {
        DetailDataSource helper = buildDataSource(0, pref.getSelectByPk(), pref.getUpdate());
        helper.setLoadResult(getDetailResult());
        JTextField field = new JTextField();
        helper.declare("sicovamCode", field);
        field.setText("nvSicoCode");

        JTextField readOnly = new JTextField();
        readOnly.putClientProperty(FieldType.EDIT_MODE, FieldType.READ_ONLY);
        helper.declare("readOnly", readOnly);

        // Init Le simulateur de serveur pour receptionner la requete update
        requestHelper.setRequest(new Request[]{buildUpdateRequest()});
        requestHelper.setResult(getUpdateResult());
        requestHelper.activate();

        // Lance l'enregistrement
        helper.save();

        // Verification
        requestHelper.verify();
    }


    /**
     * Test que le helper permet de changer la valeur d'un champ
     */
    public void test_setFieldValue() throws Exception {
        DetailDataSource helper = new DetailDataSource(guiContext, new RequestSender(), null, null,
                                                       pref.getInsert());
        JTextField field = new JTextField();

        helper.declare("sicovamCode", field);
        field.setText("nvSicoCode");

        // Via setFieldValue()
        helper.setFieldValue("sicovamCode", "54");
        assertEquals("54", field.getText());

        // Via apply()
        helper.apply("sicovamCode", "89");
        assertEquals("89", field.getText());
    }


    /**
     * Test que on peut changer la valeur initial
     */
    public void test_setInitialGuiValue() throws Exception {
        DetailDataSource helper = new DetailDataSource(guiContext, new RequestSender(), null, null,
                                                       pref.getInsert());

        JTextField field = new JTextField();
        field.setText("nvSicoCode");
        helper.declare("sicovamCode", field);

        assertTrue(!helper.hasBeenUpdated());

        helper.setFieldValue("sicovamCode", "54");

        assertTrue("Sicovam changé", helper.hasBeenUpdated());

        helper.setDefaultValue("sicovamCode", "54");

        assertTrue("Sicovam inchangé par rapport a la valeur initiale", !helper.hasBeenUpdated());
    }


    public void test_setInitialGuiValue_ComboBox()
          throws Exception {
        DetailDataSource helper = new DetailDataSource(guiContext, new RequestSender(), null, null,
                                                       pref.getInsert());

        // Build a RequestComboBox
        RequestComboBox field = new RequestComboBox();
        field.setModelFieldName("ref_code");
        field.setColumns(new String[]{"ref_code"});
        field.setContainsNullValue(true);

        // Declaration
        field.setSelectedItem(NULL);
        helper.declare("sicovamCode", field);

        // Changement de la valeur par defaut
        helper.setDefaultValue("sicovamCode", "val-b");

        // Simulation du chargement de la combo (2 lignes)
        Result res = new Result();
        Row rowA = new Row();
        rowA.addField("ref_code", "val-a");
        Row rowB = new Row();
        rowB.addField("ref_code", "val-b");
        res.addRow(rowA);
        res.addRow(rowB);
        field.getDataSource().setLoadResult(res);

        // Chargement du datasource a vide
        helper.setLoadResult(null);

        // Verification de la prise en compte de la val par defaut.
        assertEquals("val-b", field.getSelectedItem());
    }


    public void test_setInitialGuiValue_null() throws Exception {
        DetailDataSource helper = new DetailDataSource(guiContext, new RequestSender(), null, null,
                                                       pref.getInsert());

        JTextField field = new JTextField();
        field.setText("nvSicoCode");
        helper.declare("sicovamCode", field);

        try {
            helper.setDefaultValue("sicovamCode", null);
            fail("Valeur null refusé");
        }
        catch (Exception ex) {
        }
    }


    public void test_setInitialGuiValue_reInit() throws Exception {
        DetailDataSource helper = buildDataSource(0, pref.getSelectByPk(), pref.getUpdate());

        JTextField field = new JTextField();
        helper.declare("sicovamCode", field);

        helper.setDefaultValue("sicovamCode", "bob");

        assertTrue(helper.hasBeenUpdated());

        helper.setLoadResult(null);

        assertEquals("bob", helper.getFieldValue("sicovamCode"));

        assertTrue(!helper.hasBeenUpdated());
    }


    public void test_setResult_no_Row() throws Exception {
        DetailDataSource helper = buildDataSource(0, pref.getSelectByPk(), pref.getUpdate());

        JTextField field = new JTextField();
        helper.declare("sicovamCode", field);
        Result res = new Result();
        helper.setLoadResult(res);

        assertTrue(!helper.hasBeenUpdated());
    }


    /**
     * Test que le helper envoie un evenement après un enregistrement
     */
    public void test_setSaveResult() throws Exception {
        DetailDataSource helper = buildDataSource(0, pref.getSelectByPk(), pref.getUpdate());

        Result saveResult = new Result();
        saveResult.addPrimaryKey("pimsCode");
        Row row = new Row();
        row.addField("pimsCode", "999");
        saveResult.addRow(row);

        FakeDataSourceListener listener = new FakeDataSourceListener();
        helper.addDataSourceListener(listener);

        helper.setSaveResult(saveResult);

        assertEquals(true, listener.hasBeenCalled());
        assertEquals(true, listener.beforeSaveEvent);
        assertEquals(true, listener.saveEvent);
        assertEquals(1, listener.nbOfCall);
        assertEquals(helper, listener.lastEvt.getDataSource());
        assertEquals(saveResult, listener.lastEvt.getResult());
    }


    /**
     * Teste que le DetailDataSource envoie bien ses événements beforeSave et save même lorsu'il n'a pas été
     * modifié.
     */
    public void test_save_notModified() throws Exception {
        DetailDataSource helper = buildDataSource(0, pref.getSelectByPk(), pref.getUpdate());

        FakeDataSourceListener listener = new FakeDataSourceListener();
        helper.addDataSourceListener(listener);

        helper.save();

        assertEquals(true, listener.beforeSaveEvent);
        assertEquals(true, listener.saveEvent);
    }


    public void test_columnsList() throws Exception {
        DetailDataSource dataSource = buildDataSource(0);

        JTextField field = new JTextField();
        dataSource.declare("col1", field);

        dataSource.declare("col2");

        assertEquals("2 colonnes déclarées", 2, dataSource.getDeclaredFields().size());
        assertEquals("[col2, col1]", dataSource.getDeclaredFields().keySet().toString());
    }


    public void test_useReadOnlyManagementWithConsultationUser() {

        guiContext.setUser(new UserMock().mockIsAllowedTo(false));

        final JTextField field = registerTextField();

        assertTrue(new Assertion() {
            @Override
            public void check() throws Exception {
                assertFalse(field.isEnabled());
            }
        });
    }


    public void test_useReadOnlyManagementWithSuperUser()
          throws Exception {

        final JTextField field = registerTextField();

        assertTrue(field.isEnabled());
    }


    public void test_declare_lengthOkForTextComponent() throws Exception {
        DetailDataSource dataSource = buildDataSource(0);
        dataSource.setEntityName("tableData");
        dataSource.setStructureReader(new DefaultStructureReader(getClass().getResourceAsStream(
              "structure.xml")));

        JTextField textField = new JTextField();
        TextBox textBox = new TextBox(textField);
        dataSource.declare("portfolio", textField);

        textBox.insertText("1234567", 0);

        assertEquals("123456", textBox.getText());
    }


    public void test_declare_lengthForField() throws Exception {
        DetailDataSource dataSourceWithEntityName = buildDataSource(0);
        dataSourceWithEntityName.setEntityName("tableData");
        dataSourceWithEntityName.setStructureReader(new DefaultStructureReader(getClass().getResourceAsStream(
              "structure.xml")));

        NumberField sensiField = new NumberField();
        NumberField alcatelField = new NumberField();
        NumberField bnpField = new NumberField();
        dataSourceWithEntityName.declare("sensi", sensiField);
        dataSourceWithEntityName.declare("alcatel", alcatelField);
        dataSourceWithEntityName.declare("bnp", bnpField);

        assertPrecision(sensiField, 2, 3);
        assertDecimalFormat(sensiField, ' ', "#,##0.000");

        assertPrecision(alcatelField, -1, -1);
        assertDecimalFormat(alcatelField, ' ', "#,##0");

        assertPrecision(bnpField, 5, 0);
        assertDecimalFormat(bnpField, ' ', "#,##0");

        DetailDataSource dataSourceWithoutEntityName = buildDataSource(0);
        dataSourceWithoutEntityName.setStructureReader(new DefaultStructureReader(getClass().getResourceAsStream(
              "structure.xml")));
        NumberField numberField2 = new NumberField();
        dataSourceWithoutEntityName.declare("sensi", numberField2);

        assertEquals(-1, numberField2.getMaximumIntegerDigits());
        assertEquals(-1, numberField2.getMaximumFractionDigits());
        assertNull(numberField2.getRenderer().getFormat());
    }


    public void test_declare_lengthForField_unknownColumn() throws Exception {
        DetailDataSource dataSource = buildDataSource(0);
        dataSource.setEntityName("tableData");
        dataSource.setStructureReader(new DefaultStructureReader(getClass().getResourceAsStream(
              "structure.xml")));

        NumberField numberField = new NumberField();
        TextBox textBox = new TextBox(numberField);
        dataSource.declare("unknown column", numberField);

        textBox.insertText("1,1234567", 0);

        assertEquals("1,1234567", textBox.getText());

        textBox.setText("1234,1234567");

        assertEquals("1234,1234567", textBox.getText());
    }


    public void test_declare_requiredFields() throws Exception {
        DetailDataSource dataSource = buildDataSource(0);
        dataSource.setEntityName("tableData");
        dataSource.setStructureReader(new DefaultStructureReader(getClass().getResourceAsStream(
              "structure.xml")));

        dataSource.declare("portfolio", new JTextField());
        dataSource.declare("sensi", new JTextField());

        assertTrue(dataSource.isFieldRequired("portfolio"));
        assertFalse(dataSource.isFieldRequired("sensi"));
    }


    private Result getDetailResult() {
        Result result = new Result();
        result.setPrimaryKey("pimsCode");
        result.setRequestId("10");
        List<Row> rows = new ArrayList<Row>();

        Row row = new Row();
        row.addField("pimsCode", "999");
        row.addField("sicovamCode", "12");
        row.addField("isinCode", "1");
        rows.add(row);

        result.setRows(rows);
        return result;
    }


    private Result getDetailResultForUpdateOrder() {
        Result result = new Result();
        result.setPrimaryKey("dbFieldName");
        result.setRequestId("10");
        List<Row> rows = new ArrayList<Row>();

        Row row = new Row();
        row.addField("dbFieldName", "999");
        row.addField("dbTableName", "12");
        row.addField("labelfield", "12");
        rows.add(row);

        result.setRows(rows);
        return result;
    }


    private void assertPrecision(NumberField sensiField, int expectedMaxInteger, int expectedMaxFraction) {
        assertEquals(expectedMaxInteger, sensiField.getMaximumIntegerDigits());
        assertEquals(expectedMaxFraction, sensiField.getMaximumFractionDigits());
    }


    private void assertDecimalFormat(NumberField numberField,
                                     char expectedGroupingSeparator,
                                     String expectedPattern) {
        DecimalFormat decimalFormat = (DecimalFormat)numberField.getRenderer().getFormat();
        assertEquals(expectedGroupingSeparator,
                     decimalFormat.getDecimalFormatSymbols().getGroupingSeparator());
        assertEquals(expectedPattern, decimalFormat.toPattern());
    }


    private String getFirstRow() {
        return "        <row>"
               + "           <field name=\"pimsCode\">999</field>"
               + "           <field name=\"isinCode\">1</field>"
               + "           <field name=\"sicovamCode\">12</field>" + "        </row>";
    }


    private String getSecondRow() {
        return "        <row>"
               + "           <field name=\"pimsCode\">1000</field>"
               + "           <field name=\"isinCode\">2</field>"
               + "           <field name=\"sicovamCode\">13</field>" + "        </row>";
    }


    private String getResult() {
        return "<?xml version=\"1.0\"?>" + "<results>" + "     <result request_id=\""
               + (requestHelper.getRequestId(0)) + "\">" + "        <primarykey>"
               + "           <field name=\"pimsCode\"/>" + "        </primarykey>"
               + getFirstRow() +
               "     </result>"
               + "</results>";
    }


    private String get2Results() {
        return "<?xml version=\"1.0\"?>"
               + "<results>"
               + "     <result request_id=\"" + (requestHelper.getRequestId(0)) + "\">"
               + "        <primarykey>" + "           <field name=\"pimsCode\"/>" + "        </primarykey>"
               + getFirstRow()
               + getSecondRow()
               + "     </result>"
               + "</results>";
    }


    private String getUpdateResult() {
        return "<?xml version=\"1.0\"?>" + "<results>"
               + "     <result request_id=\"" + (requestHelper.getRequestId(0)) + "\">"
               + "        <primarykey>" + "           <field name=\"pimsCode\"/>" + "        </primarykey>"
               + "        <row>" + "           <field name=\"pimsCode\">999</field>" + "        </row>"
               + "     </result>"
               + "</results>";
    }


    private String getUpdateForSpecificRow() {
        return "<?xml version=\"1.0\"?>" + "<results>"
               + "     <result request_id=\"" + (requestHelper.getRequestId(0)) + "\">"
               + "        <primarykey>" + "           <field name=\"pimsCode\"/>" + "        </primarykey>"
               + "        <row>" + "           <field name=\"pimsCode\">1000</field>" + "        </row>"
               + "     </result>"
               + "</results>";
    }


    private Request buildInsertRequest() {
        InsertRequest request = new InsertRequest();
        request.setId("insertCodificationPtf");
        request.addField("sicovamCode", "nvSicoCode");
        return request;
    }


    private Request buildUpdateRequest() {
        return buildUpdateRequest("999", "nvSicoCode", null);
    }


    private Request buildUpdateRequest(String pimsCode, String sicovamCode, String isinCode) {
        UpdateRequest request = new UpdateRequest();
        request.setId("updateCodificationPtf");
        request.addPrimaryKey("pimsCode", pimsCode);
        request.addField("sicovamCode", sicovamCode);
        if (isinCode != null) {
            request.addField("isinCode", isinCode);
        }
        return request;
    }


    private void fillWithTwoRow(final MockListDataSource lds) {
        Result resultLDS = new Result();
        Row bRow = new Row();
        bRow.addField("sicovamCode", "12");
        resultLDS.addRow(bRow);
        Row aRow = new Row();
        aRow.addField("sicovamCode", "12");
        resultLDS.addRow(aRow);
        lds.setLoadResult(resultLDS);
    }


    private JTextField registerTextField() {
        DetailDataSource dataSource = buildDataSource(0);
        dataSource.useReadOnlyManagement();
        dataSource.setSaveFactory(new UpdateFactory());
        JTextField field = new JTextField();
        dataSource.declare("sicovamCode", field);
        return field;
    }


    private static class FakeChangeListener implements PropertyChangeListener {
        private List<String> fieldUpdated = new ArrayList<String>();
        private int nbOfCall = 0;
        private PropertyChangeEvent evt;


        public boolean hasBeenCalled() {
            return nbOfCall > 0;
        }


        public void propertyChange(PropertyChangeEvent event) {
            this.evt = event;
            if ("loadFactory".equals(event.getPropertyName())) {
                try {
                    throw new IllegalArgumentException("ggr");
                }
                catch (Exception ex) {
                    ;
                }
            }
            fieldUpdated.add(event.getPropertyName());
            nbOfCall++;
        }
    }

    private class FakeUndoListener implements UndoableEditListener {
        private int nbOfCall = 0;
        private UndoManager undoMngr = new UndoManager();
        private UndoableEdit undoEdit;


        public boolean hasBeenCalled() {
            return undoEdit != null;
        }


        public void undoableEditHappened(UndoableEditEvent event) {
            undoEdit = event.getEdit();
            undoMngr.addEdit(undoEdit);
            nbOfCall++;
        }
    }

    private class MockListDataSource extends ListDataSource {
        private boolean automaticFillAtLoad = true;


        @Override
        public void setValue(int row, String columnId, String value) {
            super.setValue(row, columnId, value);
        }


        @Override
        public void clear() {
            super.clear();
        }


        @Override
        public void load() throws RequestException {
            if (automaticFillAtLoad) {
                fillWithTwoRow(this);
            }
        }


        @Override
        public void addSaveRequestTo(MultiRequestsHelper mrh) {
            try {
                Thread.sleep(100);
            }
            catch (InterruptedException ex) {
                ;
            }
        }
    }
}
