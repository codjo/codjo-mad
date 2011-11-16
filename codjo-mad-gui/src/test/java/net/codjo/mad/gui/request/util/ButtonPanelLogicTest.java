package net.codjo.mad.gui.request.util;
import net.codjo.mad.client.request.FieldsList;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.client.request.RequestSender;
import net.codjo.mad.client.request.Result;
import net.codjo.mad.client.request.Row;
import net.codjo.mad.gui.framework.DefaultGuiContext;
import net.codjo.mad.gui.request.DataLink;
import net.codjo.mad.gui.request.DetailDataSource;
import net.codjo.mad.gui.request.ErrorHandler;
import net.codjo.mad.gui.request.JoinKeys;
import net.codjo.mad.gui.request.ListDataSource;
import net.codjo.mad.gui.request.Mock;
import net.codjo.mad.gui.request.Preference;
import net.codjo.mad.gui.request.undo.DataSourceUndoManager;
import net.codjo.mad.gui.request.undo.DataSourceUndoManagerStub;
import net.codjo.mad.gui.request.archive.ArchiveManager;
import net.codjo.mad.gui.request.archive.ArchiveManagerFactory;
import net.codjo.mad.gui.request.factory.RequestFactory;
import net.codjo.mad.gui.request.factory.SelectFactory;
import net.codjo.security.common.api.UserMock;
import net.codjo.test.common.LogString;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JTextField;
import junit.framework.TestCase;
/**
 * Test de la classe ButtonPanelLogic.
 *
 * @version $Revision: 1.14 $
 */
public class ButtonPanelLogicTest extends TestCase {
    private static final String SELECT_ALL_ID = "selectAll";
    private static final String COLUMN_ID_SON1 = "idSon1";
    private static final String COLUMN_ID_SON2 = "idSon2";
    private static final String COLUMN_FATHERDATA = "fatherData";
    private static final String[] COLUMNS_SON1 = {COLUMN_ID_SON1, "label"};
    private static final String[] COLUMNS_SON2 = {COLUMN_ID_SON2, "label"};
    private Preference pref;
    private ButtonPanelLogic logic;


    @Override
    protected void setUp() throws Exception {
        logic = new ButtonPanelLogic();
        pref = new Preference();
        pref.setSelectByPkId("selectCodificationPtfById");
        pref.setUpdateId("updateCodificationPtf");
        pref.setInsertId("insertCodificationPtf");
    }


    public void test_customizedGui() throws Exception {
        ButtonPanelGui customizedGui = new ButtonPanelGui();
        assertSame(customizedGui, new ButtonPanelLogic(customizedGui).getGui());
    }


    public void test_canSave_withFatherOnly() throws Exception {
        // Créer les intervenants
        DetailDataSource father = createFather("1", "3", "data init");
        logic.setMainDataSource(father);

        // Le contrôleur vient d'être initalisé, il ne doit pas être modifié
        assertFalse(logic.canSave());

        // Modifier le père : le contrôleur doit être modifié
        JTextField field = new JTextField();
        father.declare(COLUMN_FATHERDATA, field);
        field.setText("data modif");
        assertTrue(logic.canSave());

        // Annuler les modifications : le contrôleur doit revenir à l'état non modifié
        logic.cancelChanges();
        assertFalse(logic.canSave());
    }


    public void test_mustSave() throws Exception {
        DetailDataSource father = createFather("1", "3", "data init");

        logic.setMainDataSource(father);

        JTextField field = new JTextField();
        father.declare(COLUMN_FATHERDATA, field);
        field.setText("new data modif");

        logic.getGui().getOkButton().doClick();
        ((MockDetailDataSource)father).getLogString().assertContent("sauvegarde");
        ((MockDetailDataSource)father).getLogString().clear();

        logic.setMustSave(false);
        field = new JTextField();
        father.declare(COLUMN_FATHERDATA, field);
        field.setText("new data modif");

        ((MockDetailDataSource)father).getLogString().assertContent("");
    }


    public void test_canSave_withValidator() throws Exception {
        // Créer les intervenants
        DetailDataSource father = createFather("1", "3", "data père");
        MockListDataSource son =
              createSon(COLUMNS_SON1, COLUMN_ID_SON1, "1", "label fils init");

        logic.setMainDataSource(father);
        JoinKeys joinKeys = new JoinKeys();
        joinKeys.addAssociation(COLUMN_ID_SON1);
        logic.addSubDataSource(son);
        new DataLink(father, son, joinKeys).start();

        // Modifier le fils : le contrôleur doit être modifié
        son.addRow(createRow(COLUMN_ID_SON1, "9", "label", "label fils modif"));
        assertTrue(logic.canSave());

        logic.setButtonLogicValidator(new ButtonLogicValidator() {
            public boolean isValid() {
                return false;
            }
        });

        assertFalse(logic.canSave());
    }


    public void test_canSave_withOneSon() throws Exception {
        // Créer les intervenants
        DetailDataSource father = createFather("1", "3", "data père");
        MockListDataSource son =
              createSon(COLUMNS_SON1, COLUMN_ID_SON1, "1", "label fils init");

        logic.setMainDataSource(father);
        JoinKeys joinKeys = new JoinKeys();
        joinKeys.addAssociation(COLUMN_ID_SON1);
        logic.addSubDataSource(son);
        new DataLink(father, son, joinKeys).start();

        // Le contrôleur vient d'être initalisé, il ne doit pas être modifié
        assertFalse(logic.canSave());

        // Modifier le fils : le contrôleur doit être modifié
        son.addRow(createRow(COLUMN_ID_SON1, "9", "label", "label fils modif"));
        assertTrue(logic.canSave());

        // Annuler les modifications : le contrôleur doit revenir à l'état non modifié
        logic.cancelChanges();
        assertFalse(logic.canSave());
    }


    public void test_withTwoSons() throws Exception {
        // Créer les intervenants
        DetailDataSource father = createFather("1", "3", "data père");
        MockListDataSource son1 =
              createSon(COLUMNS_SON1, COLUMN_ID_SON1, "1", "label fils 1");
        MockListDataSource son2 =
              createSon(COLUMNS_SON2, COLUMN_ID_SON2, "3", "label fils 2");

        logic.setMainDataSource(father);
        JoinKeys joinKeys1 = new JoinKeys();
        joinKeys1.addAssociation(COLUMN_ID_SON1);
        logic.addSubDataSource(son1);
        new DataLink(father, son1, joinKeys1).start();
        JoinKeys joinKeys2 = new JoinKeys();
        joinKeys2.addAssociation(COLUMN_ID_SON1);
        logic.addSubDataSource(son2);
        new DataLink(father, son2, joinKeys2).start();

        // Le contrôleur vient d'être initalisé, il ne doit pas être modifié
        assertFalse(logic.canSave());

        // Modifications sur le fils 1 seulement
        // Modifier le fils 1 : le contrôleur doit être modifié
        son1.addRow(createRow(COLUMN_ID_SON1, "9", "label", "lab modif 9"));
        assertTrue(logic.canSave());

        // Annuler les modifications : le contrôleur doit revenir à l'état non modifié
        logic.cancelChanges();
        assertFalse(logic.canSave());

        // Modifications sur le fils 2 seulement
        // Modifier le fils 2 : le contrôleur doit être modifié
        son2.addRow(createRow(COLUMN_ID_SON2, "10", "label", "lab modif 10"));
        assertTrue(logic.canSave());

        // Annuler les modifications : le contrôleur doit revenir à l'état non modifié
        logic.cancelChanges();
        assertFalse(logic.canSave());

        // Modifications sur le fils 1 et le fils 2 en même temps
        // Modifier le fils 1 : le contrôleur doit être modifié
        son1.addRow(createRow(COLUMN_ID_SON1, "11", "label", "lab modif 11"));
        assertTrue(logic.canSave());

        // Modifier le fils 2 : le contrôleur doit être modifié
        son2.addRow(createRow(COLUMN_ID_SON2, "12", "label", "lab modif 12"));
        assertTrue(logic.canSave());

        // Annuler les modifications : le contrôleur doit revenir à l'état non modifié
        logic.cancelChanges();
        assertFalse(logic.canSave());
    }


    /**
     * Teste que le DetailDataSource transfère bien les événements du fils en les renommant, et qu'il tient
     * compte des modifications sur le fils
     */
    public void test_son_changeEvent() throws Exception {
        DetailDataSource father = createFather();
        father.declare("sicovamCode");
        father.setLoadResult(getDetailResult());
        logic.setMainDataSource(father);

        // Connecte le LDS
        Mock.ListDataSource son = new Mock.ListDataSource();
        fillWithTwoRow(son);
        new DataLink(father, son, new JoinKeys("sicovamCode")).start();
        logic.addSubDataSource(son);

        // Modification
        assertFalse("Father non modifié", father.hasBeenUpdated());
        assertFalse("Son non modifié", son.hasBeenUpdated());
        assertFalse("Bouton valide disabled", logic.getGui().getOkButton().isEnabled());

        son.setValue(0, "sicovamCode", "nv");

        assertFalse("Father non modifié", father.hasBeenUpdated());
        assertTrue("Son modifié", son.hasBeenUpdated());
        assertTrue("Bouton valide enabled", logic.getGui().getOkButton().isEnabled());
    }


    /**
     * Teste que le DetailDataSource transfère bien les événements du deuxieme fils et qu'il tient compte des
     * modifications sur le fils
     */
    public void test_son2_changeEvent() throws Exception {
        DetailDataSource father = createFather();
        father.declare("sicovamCode");
        father.setLoadResult(getDetailResult());

        // Creation des deux fils
        Mock.ListDataSource son1 = new Mock.ListDataSource();
        fillWithTwoRow(son1);
        Mock.ListDataSource son2 = new Mock.ListDataSource();
        fillWithTwoRow(son2);

        // Connection du pere avec les deux fils
        new DataLink(father, son1, new JoinKeys("sicovamCode")).start();
        new DataLink(father, son2, new JoinKeys("sicovamCode")).start();
        logic.setMainDataSource(father);
        logic.addSubDataSource(son1);
        logic.addSubDataSource(son2);

        // Modification de son1
        assertFalse("Father non modifié", father.hasBeenUpdated());
        assertFalse("Son1 non modifié", son1.hasBeenUpdated());
        assertFalse("Son2 non modifié", son2.hasBeenUpdated());
        assertFalse("Bouton valide disabled", logic.getGui().getOkButton().isEnabled());

        son1.setValue(0, "sicovamCode", "nv");

        assertFalse("Father non modifié", father.hasBeenUpdated());
        assertTrue("Son1 modifié", son1.hasBeenUpdated());
        assertFalse("Son2 non modifié", son2.hasBeenUpdated());
        assertTrue("Bouton valide enabled", logic.getGui().getOkButton().isEnabled());
    }


    public void test_archive_default() throws Exception {
        DetailDataSource father = createFather();
        logic.setMainDataSource(father);

        try {
            logic.getArchiveManager();

            fail("Pas de ArchiveManager ou factory par défaut");
        }
        catch (IllegalStateException ex) {
            ; // Ok
        }

        // creation d'un archiveManager
        Mock.ArchiveManagerFactory mam = new Mock.ArchiveManagerFactory();
        logic.setArchiveManagerFactory(mam);

        // Construction
        ArchiveManager mng = logic.getArchiveManager();
        assertSame(mam.getLastBuiltManager(), mng);
        assertSame(father, ((Mock.ArchiveManager)mng).dataSource);
        assertSame(mng, logic.getArchiveManager());

        // Override
        mng = new Mock.ArchiveManager(null);
        logic.setArchiveManager(mng);
        assertSame(mng, logic.getArchiveManager());
    }


    public void test_archive_gui() throws Exception {
        // creation d'un archiveManager
        Mock.ArchiveManagerFactory mam = new Mock.ArchiveManagerFactory();
        logic.setArchiveManagerFactory(mam);

        //  Init Pere
        DetailDataSource father = createFather();
        father.setLoadFactoryId("loadId");
        logic.setMainDataSource(father);

        // Pas dans Gui
        JButton archiveButton = logic.getGui().getArchiveButton();
        JButton whatsNewButton = logic.getGui().getWhatsNewButton();
        assertFalse(guiContains(archiveButton));
        assertFalse(guiContains(whatsNewButton));

        // Positionne un archive Id
        logic.setArchiveRequestId("archiveId");

        // Gui is up and running !
        assertTrue(guiContains(archiveButton));
        assertTrue(guiContains(whatsNewButton));
        assertTrue(archiveButton.isEnabled());
        assertTrue(whatsNewButton.isEnabled());

        // Modification
        father.declare("boboField");
        father.setFieldValue("boboField", "boboValue");
        assertTrue(father.hasBeenUpdated());
        assertFalse(archiveButton.isEnabled());

        // Retour à l'état initial
        father.setFieldValue("boboField", "null");
        assertTrue(archiveButton.isEnabled());
    }


    public void test_archive_gui_with_son_datasource() throws Exception {
        // creation d'un archiveManager
        Mock.ArchiveManagerFactory mam = new Mock.ArchiveManagerFactory();
        logic.setArchiveManagerFactory(mam);

        // Créer les intervenants
        DetailDataSource father = createFather("1", "3", "data père");
        MockListDataSource son =
              createSon(COLUMNS_SON1, COLUMN_ID_SON1, "1", "label fils init");

        logic.setMainDataSource(father);
        JoinKeys joinKeys = new JoinKeys();
        joinKeys.addAssociation(COLUMN_ID_SON1);
        logic.addSubDataSource(son);
        new DataLink(father, son, joinKeys).start();

        // Pas dans Gui
        JButton archiveButton = logic.getGui().getArchiveButton();
        assertFalse(guiContains(archiveButton));

        // Positionne un archive Id
        logic.setArchiveRequestId("archiveId");

        // Gui is up and running !
        assertTrue(guiContains(archiveButton));
        assertTrue(archiveButton.isEnabled());

        // Modification du fils : le bouton Archive doit être désactivé
        Row row = createRow(COLUMN_ID_SON1, "9", "label", "label fils modif");
        son.addRow(row);

        assertFalse(archiveButton.isEnabled());

        // Annuler les modifications : le contrôleur doit revenir à l'état non modifié
        logic.cancelChanges();
        assertTrue(archiveButton.isEnabled());
    }


    public void test_archive_story() throws Exception {
        // creation d'un archiveManager
        Mock.ArchiveManagerFactory mam = new Mock.ArchiveManagerFactory();
        logic.setArchiveManagerFactory(mam);

        //  Init Pere
        DetailDataSource father = createFather();
        father.setLoadFactoryId("loadId");
        father.declare("boboField");
        logic.setMainDataSource(father);
        logic.setArchiveRequestId("archiveId");

        // Démarrage de l'historisation !
        logic.getGui().getArchiveButton().doClick();
        Mock.ArchiveManager manager = mam.getLastBuiltManager();
        assertTrue("Méthode askArchiveDate appelée", manager.askArchiveDate_called);

        assertTrue("Méthode updateDSWithArchiveDate appelée",
                   manager.updateDSWithArchiveDate_called);
        assertEquals("Argument de updateDSWithArchiveDate est correct",
                     manager.askArchiveDate_result, manager.updateDSWithArchiveDate_arg);

        assertTrue("Méthode startArchive appelée", manager.startArchive_called);
        assertEquals("Argument de startArchive est correct",
                     manager.askArchiveDate_result, manager.startArchive_arg);

        // Modification
        father.setFieldValue("boboField", "boboValue");
        assertFalse(logic.getGui().getArchiveButton().isEnabled());

        // Validation de l'historisation
        logic.getGui().getOkButton().doClick();

        assertTrue("Méthode doArchive appelée", manager.doArchive_called);
        assertEquals("Argument date de doArchive est correct",
                     manager.askArchiveDate_result, manager.doArchive_arg_date);
        assertEquals("Argument id de doArchive est correct", "archiveId",
                     manager.doArchive_arg_id);
    }


    public void test_archive_withError() throws Exception {
        // creation d'un archiveManager
        ArchiveManagerFactory mam = new MockArchiveErrorManagerFactory();
        logic.setArchiveManagerFactory(mam);

        JInternalFrame frame = new JInternalFrame();
        frame.getContentPane().add(logic.getGui());
        frame.setVisible(true);

        MockErrorHandler errorHandler = new MockErrorHandler();
        logic.setErrorHandler(errorHandler);

        //  Init Pere
        DetailDataSource father = createFather();
        father.setLoadFactoryId("loadId");
        father.declare("boboField");
        logic.setMainDataSource(father);
        logic.setArchiveRequestId("archiveId");

        // Démarrage de l'historisation !
        logic.getGui().getArchiveButton().doClick();
        // Modification
        father.setFieldValue("boboField", "boboValue");
        assertFalse(logic.getGui().getArchiveButton().isEnabled());
        // Validation de l'historisation qui génère une erreur
        logic.getGui().getOkButton().doClick();

        // La fenêtre n'est pas fermée
        assertEquals(ButtonPanelLogic.ARCHIVE_ERROR, errorHandler.getErrorId());
        assertTrue(frame.isVisible());
    }


    public void test_undoRedo_story_father() throws Exception {
        //  Init père
        DetailDataSource father = createFather();
        father.declare("boboField");
        logic.setMainDataSource(father);
        assertEquals("null", father.getFieldValue("boboField"));
        assertFalse(logic.getGui().getUndoButton().isEnabled());
        assertFalse(logic.getGui().getRedoButton().isEnabled());

        // Modification
        father.setFieldValue("boboField", "boboValue");
        assertEquals("boboValue", father.getFieldValue("boboField"));
        assertTrue(logic.getGui().getUndoButton().isEnabled());
        assertFalse(logic.getGui().getRedoButton().isEnabled());

        // Undo
        logic.getGui().getUndoButton().doClick();
        assertEquals("null", father.getFieldValue("boboField"));
        assertFalse(logic.getGui().getUndoButton().isEnabled());
        assertTrue(logic.getGui().getRedoButton().isEnabled());

        // Redo
        logic.getGui().getRedoButton().doClick();
        assertEquals("boboValue", father.getFieldValue("boboField"));
        assertTrue(logic.getGui().getUndoButton().isEnabled());
        assertFalse(logic.getGui().getRedoButton().isEnabled());
    }


    public void test_undoRedo_story_son() throws Exception {
        //  Init père
        DetailDataSource father = createFather();
        father.declare("id");
        father.declare("fatherField");

        // Init fils
        ListDataSource son = new ListDataSource();
        Result loadResult = new Result();
        loadResult.addRow(createRow("id", "1", "sonField", "son init"));
        son.setLoadResult(loadResult);

        // Création du lien père - fils
        logic.setMainDataSource(father);
        JoinKeys joinKeys = new JoinKeys();
        joinKeys.addAssociation("id");
        DataLink dataLink = new DataLink(father, son, joinKeys);
        dataLink.start();
        logic.addSubDataSource(son);

        // Etat initial
        assertEquals("son init", son.getValueAt(0, "sonField"));
        assertFalse(logic.getGui().getUndoButton().isEnabled());
        assertFalse(logic.getGui().getRedoButton().isEnabled());

        // Modification fils
        son.setValue(0, "sonField", "son modif");
        assertEquals("son modif", son.getValueAt(0, "sonField"));
        assertTrue(logic.getGui().getUndoButton().isEnabled());
        assertFalse(logic.getGui().getRedoButton().isEnabled());

        // Undo
        logic.getGui().getUndoButton().doClick();
        assertEquals("son init", son.getValueAt(0, "sonField"));
        assertFalse(logic.getGui().getUndoButton().isEnabled());
        assertTrue(logic.getGui().getRedoButton().isEnabled());

        // Redo
        logic.getGui().getRedoButton().doClick();
        assertEquals("son modif", son.getValueAt(0, "sonField"));
        assertTrue(logic.getGui().getUndoButton().isEnabled());
        assertFalse(logic.getGui().getRedoButton().isEnabled());
    }


    public void test_closeOnButtons() throws Exception {
        ButtonPanelGui buttonPanelGui = new ButtonPanelGui();
        MockButtonPanelGui mock = new MockButtonPanelGui(buttonPanelGui);
        logic = new ButtonPanelLogic(buttonPanelGui);
        MockDetailDataSource dataSource = new MockDetailDataSource();
        logic.setMainDataSource(dataSource);

        buttonPanelGui.getOkButton().doClick();
        mock.getLog().assertContent("setVisible(false)");
        mock.getLog().clear();

        logic.setCloseOnSave(false);
        buttonPanelGui.getOkButton().doClick();
        mock.getLog().assertContent("");
        mock.getLog().clear();

        buttonPanelGui.getCancelButton().doClick();
        mock.getLog().assertContent("setVisible(false), setVisible(false), setVisible(false)");
        mock.getLog().clear();

        logic.setCloseOnCancel(false);
        buttonPanelGui.getCancelButton().doClick();
        mock.getLog().assertContent("");
        mock.getLog().clear();
    }


    public void test_buttonPanelLogicWithListDataSource() throws Exception {
        MockListDataSource listDataSource = new MockListDataSource(new String[]{
              "column1", "column2", "column3"
        });
        Result result = new Result();
        result.addRow(createRow("column1", "0", "column2", "label", "column3", "de cadix"));
        result.addRow(createRow("column1", "1", "column2", "a des yeux", "column3", "de velours"));
        listDataSource.setResult(result);
        listDataSource.load();

        ButtonPanelGui buttonPanelGui = new ButtonPanelGui();
        ButtonPanelLogic buttonPanelLogic = new ButtonPanelLogic(buttonPanelGui);
        buttonPanelLogic.setMainDataSource(listDataSource);

        assertFalse(buttonPanelLogic.canSave());

        listDataSource.setValue(0, "column2", "la belle");

        assertTrue(buttonPanelLogic.canSave());

        buttonPanelGui.getOkButton().doClick();
        listDataSource.getLog().assertContent("save");
    }


    public void test_getMainDataSourceWithListDataSource() throws Exception {
        logic.setMainDataSource(new ListDataSource());
        try {
            logic.getMainDataSource();
            fail();
        }
        catch (RuntimeException e) {
            assertEquals(
                  "Vous ne pouvez pas utiliser getMainDataSource() si le ButtonPanelLogic gère un ListDataSource.",
                  e.getMessage());
        }
    }


    public void test_enableUndoRedo() {
        assertTrue(logic.getUndoManager() instanceof DataSourceUndoManager);
        ButtonPanelLogic disabledLogic = new ButtonPanelLogic(new ButtonPanelGui(), false);
        assertTrue(disabledLogic.getUndoManager() instanceof DataSourceUndoManagerStub);
    }


    private DetailDataSource createFather(String idSon1, String idSon2, String fatherData)
          throws Exception {
        Preference preference = new Preference();
        preference.setSelectByPkId("selectCodesByPk");
        preference.setInsertId("insertCode");

        MockDetailDataSource father =
              new MockDetailDataSource(new RequestSender(), null,
                                       preference.getSelectByPk(), preference.getInsert());

        father.declare(COLUMN_ID_SON1);
        father.declare(COLUMN_ID_SON2);
        father.declare(COLUMN_FATHERDATA);

        Result res = new Result();
        res.addRow(createRow(COLUMN_ID_SON1, idSon1, COLUMN_ID_SON2, idSon2,
                             COLUMN_FATHERDATA, fatherData));
        father.setLoadResult(res);
        father.getLogString().clear();
        return father;
    }


    private MockListDataSource createSon(String[] columns, String idColumnName,
                                         String id, String label) {
        MockListDataSource son = new MockListDataSource(columns);
        Result res = new Result();
        res.addRow(createRow(idColumnName, id, "label", label));
        son.setResult(res);
        return son;
    }


    private Row createRow(String name1, String value1, String name2, String value2) {
        Row row = new Row();
        row.addField(name1, value1);
        row.addField(name2, value2);
        return row;
    }


    private Row createRow(String name1, String value1, String name2, String value2,
                          String name3, String value3) {
        Row row = new Row();
        row.addField(name1, value1);
        row.addField(name2, value2);
        row.addField(name3, value3);
        return row;
    }


    private Result getDetailResult() {
        Result result = new Result();
        result.setPrimaryKey("pimsCode");
        result.setRequestId("10");

        Row row = new Row();
        row.addField("pimsCode", "999");
        row.addField("sicovamCode", "12");
        row.addField("isinCode", "1");

        List<Row> rows = new ArrayList<Row>();
        rows.add(row);
        result.setRows(rows);
        return result;
    }


    private void fillWithTwoRow(final Mock.ListDataSource lds) {
        Mock.fillWithTwoRow(lds);
    }


    private DetailDataSource createFather() {
        return new DetailDataSource(createGuiContext(), new RequestSender(), null, null, pref.getInsert());
    }


    private static DefaultGuiContext createGuiContext() {
        DefaultGuiContext guiContext = new DefaultGuiContext();
        guiContext.setUser(new UserMock().mockIsAllowedTo(true));
        return guiContext;
    }


    private boolean guiContains(JButton archiveButton) {
        return Arrays.asList(logic.getGui().getComponents()).contains(archiveButton);
    }


    private class MockButtonPanelGui extends JInternalFrame {
        private LogString log;


        private MockButtonPanelGui(ButtonPanelGui buttonPanelGui) {
            getContentPane().add(buttonPanelGui);
        }


        @Override
        public void setVisible(boolean visible) {
            createLogIfNeeded();
            log.call("setVisible", visible);
        }


        private void createLogIfNeeded() {
            if (log == null) {
                log = new LogString();
            }
        }


        public LogString getLog() {
            return log;
        }
    }

    private class MockDetailDataSource extends DetailDataSource {
        private LogString logString = new LogString();


        private MockDetailDataSource() {
            super(new DefaultGuiContext());
        }


        private MockDetailDataSource(RequestSender sender, FieldsList selectors,
                                     RequestFactory loadFactory, RequestFactory saveFactory) {
            super(createGuiContext(), sender, selectors, loadFactory, saveFactory);
        }


        @Override
        public void saveUsing(RequestFactory factory)
              throws RequestException {
            fireBeforeSaveEvent(null);
            Result res = new Result();
            res.addRow(createRow("idSon1", "1", "idSon2", "3", COLUMN_FATHERDATA,
                                 "data modif"));
            fireSaveEvent(res);
        }


        @Override
        public void save() throws RequestException {
            logString.info("sauvegarde");
        }


        @Override
        public void setLoadResult(Result loadResult) {
            super.setLoadResult(loadResult);
            logString.info("setLoadResult");
        }


        public LogString getLogString() {
            return logString;
        }
    }

    private class MockListDataSource extends ListDataSource {
        private LogString logString = new LogString();
        private Result result;
        private String[] columns;


        MockListDataSource(String[] columns) {
            this.columns = columns;
        }


        public void setResult(Result result) {
            this.result = result;
        }


        @Override
        public RequestFactory getLoadFactory() {
            return new SelectFactory(SELECT_ALL_ID);
        }


        @Override
        public void load() throws RequestException {
            setColumns(columns);
            setSelector(null);
            setLoadResult(result);
            RequestFactory loadFactory = new SelectFactory(SELECT_ALL_ID);
            setLoadFactory(loadFactory);
        }


        @Override
        public void save() throws RequestException {
            logString.info("save");
        }


        public LogString getLog() {
            return logString;
        }
    }

    private class MockArchiveErrorManagerFactory extends Mock.ArchiveManager
          implements ArchiveManagerFactory {
        MockArchiveErrorManagerFactory() {
            this(null);
        }


        MockArchiveErrorManagerFactory(DetailDataSource ds) {
            super(ds);
        }


        public ArchiveManager newArchiveManager(DetailDataSource ds) {
            return new MockArchiveErrorManagerFactory(ds);
        }


        @Override
        public void doArchive(String archiveId, Date archiveDate)
              throws RequestException {
            throw new RequestException("Grooooosse erreur d'archivage");
        }
    }

    private class MockErrorHandler implements ErrorHandler {
        private String errorId;


        public void handleError(String errorIdParam, Exception ex) {
            this.errorId = errorIdParam;
        }


        public String getErrorId() {
            return errorId;
        }
    }
}
