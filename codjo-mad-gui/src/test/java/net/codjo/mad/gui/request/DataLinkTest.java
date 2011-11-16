package net.codjo.mad.gui.request;
import net.codjo.mad.client.request.FieldsList;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.client.request.Row;
import net.codjo.mad.gui.framework.DefaultGuiContext;
import net.codjo.mad.gui.request.util.DefaultErrorHandler;
import junit.framework.TestCase;
/**
 * Test de la classe DataLink.
 */
public class DataLinkTest extends TestCase {
    private Mock.DataSource father;
    private Mock.DataSource son;
    private JoinKeys joinKeys;
    private Row fatherRow;
    private DataLink link;


    public void test_save_WithFather() throws Exception {
        link.setSavePolicy(DataLink.Policy.WITH_FATHER);
        link.start();

        // Before Save Event
        father.mockBeforeSaveEvent();
        assertEquals("Le fils est maj avec les valeurs du père", 2, son.apply_called);
        assertEquals("valueA", son.apply_args.get("sonA"));
        assertEquals("valueB", son.apply_args.get("sonB"));

        assertEquals("Le fils a ajouté sa requête", 1, son.addSaveRequestTo_called);

        // Evenement parasite ne faisant rien
        father.setSelectedRow(null);
        father.mockSaveEvent();

        assertEquals("Le fils n'a pas ajouté de nouvelle requête", 1,
                     son.addSaveRequestTo_called);
    }


    public void test_save_WithFather_selectedRow_null()
          throws Exception {
        link.setSavePolicy(DataLink.Policy.WITH_FATHER);
        link.start();

        father.setSelectedRow(null);

        // Before Save Event
        father.mockBeforeSaveEvent();
        assertEquals("Le fils n'est pas maj avec les valeurs du père", 0, son.apply_called);
        assertEquals("Le fils n'a pas ajouté sa requête", 0, son.addSaveRequestTo_called);
        assertEquals("Le fils n'est pas addSaveRequestTo", 0, son.save_called);
    }


    public void test_save_WithFather_start_stop()
          throws Exception {
        link.setLoadPolicy(DataLink.Policy.NONE);
        link.setSavePolicy(DataLink.Policy.WITH_FATHER);

        father.mockBeforeSaveEvent();
        assertEquals("Le fils n'a pas ajouté sa requête", 0, son.addSaveRequestTo_called);

        link.start();
        link.stop();

        father.mockBeforeSaveEvent();
        assertEquals("Le fils n'a pas ajouté sa requête", 0, son.addSaveRequestTo_called);

        link.start();
        father.mockBeforeSaveEvent();
        assertEquals("Le fils a ajouté sa requête", 1, son.addSaveRequestTo_called);

        link.stop();

        father.mockBeforeSaveEvent();
        assertEquals("Le fils n'a pas ajouté de requête supplémentaire", 1,
                     son.addSaveRequestTo_called);
    }


    public void test_save_AfterFather() throws Exception {
        link.setSavePolicy(DataLink.Policy.AFTER_FATHER);
        link.start();

        // Evenement parasite ne faisant rien
        father.mockBeforeSaveEvent();
        father.setSelectedRow(null);
        father.setSelectedRow(fatherRow);
        assertEquals("Le fils n'est pas maj avec les valeurs du père", 0, son.apply_called);
        assertEquals("Le fils n'a pas ajouté sa requête", 0, son.addSaveRequestTo_called);
        assertEquals("Le fils n'est pas addSaveRequestTo", 0, son.save_called);

        // After Save Event
        father.mockSaveEvent();
        assertEquals("Le fils est maj avec les valeurs du père", 2, son.apply_called);
        assertEquals("valueA", son.apply_args.get("sonA"));
        assertEquals("valueB", son.apply_args.get("sonB"));

        assertEquals("Le fils n'a pas ajouté de requête", 0, son.addSaveRequestTo_called);
        assertEquals("Le fils est addSaveRequestTo", 1, son.save_called);
    }


    public void test_save_AfterFather_selectedRow_null()
          throws Exception {
        link.setSavePolicy(DataLink.Policy.AFTER_FATHER);
        link.start();

        // Evenement parasite ne faisant rien
        father.mockBeforeSaveEvent();
        father.setSelectedRow(null);
        assertEquals("Le fils n'est pas maj avec les valeurs du père", 0, son.apply_called);
        assertEquals("Le fils n'a pas ajouté sa requête", 0, son.addSaveRequestTo_called);
        assertEquals("Le fils n'est pas addSaveRequestTo", 0, son.save_called);

        // After Save Event (fais rien car selectedRow est null)
        father.mockSaveEvent();
        assertEquals("Le fils n'est pas maj avec les valeurs du père", 0, son.apply_called);
        assertEquals("Le fils n'a pas ajouté sa requête", 0, son.addSaveRequestTo_called);
        assertEquals("Le fils n'est pas addSaveRequestTo", 0, son.save_called);
    }


    public void test_save_errorHandler() throws Exception {
        Mock.ErrorHandler mockError = new Mock.ErrorHandler();
        link.setErrorHandler(mockError);

        link.setLoadPolicy(DataLink.Policy.AFTER_FATHER);
        link.start();

        son.save_error = new RequestException("bobo");
        father.mockSaveEvent();

        assertEquals(DataLink.SAVE_ERROR, mockError.errorId);
        assertSame(son.save_error, mockError.ex);
    }


    public void test_save_None() throws Exception {
        link.setSavePolicy(DataLink.Policy.NONE);
        link.setLoadPolicy(DataLink.Policy.NONE);
        link.start();

        father.mockBeforeSaveEvent();
        father.mockSaveEvent();
        father.setSelectedRow(new Row());

        assertEquals("Le fils n'est pas maj avec les valeurs du père", 0, son.apply_called);
        assertEquals("Le fils n'a pas ajouté sa requête", 0, son.addSaveRequestTo_called);
        assertEquals("Le fils n'est pas addSaveRequestTo", 0, son.save_called);
    }


    public void test_load_WithFather() throws Exception {
        Mock.LoadManager loadManager = ((Mock.LoadManager)son.getLoadManager());

        link.setLoadPolicy(DataLink.Policy.WITH_FATHER);
        link.start();

        father.mockBeforeLoadEvent();
        assertEquals("Le fils a ajouté sa requête", 1,
                     loadManager.addLoadRequestToCall);

        assertSonSelectorFromFatherSelector();

        father.setSelectedRow(null);

        father.mockLoadEvent();
        assertEquals("Le fils a ajouté sa requête 1 seule fois", 1,
                     loadManager.addLoadRequestToCall);

        // Changement de selection (exemple d'un ListDataSource)
        father.setSelectedRow(fatherRow);
        assertEquals("Le fils se charge entierement une seule fois", 1, son.load_called);
        assertSonSelectorFromFatherSelectedRow();

        // Changement de selection (exemple d'un ListDataSource)
        father.setSelectedRow(null);
        father.mockPropertyChangeEvent();
        assertEquals("Pas de load appelé sur le fils (en plus)", 1, son.load_called);
        assertEquals("vidage du fils", 1, son.clear_called);
    }


    /**
     * Cas où le père n'a pas de sélectors : on ne peut pas charger le fils
     *
     * @throws Exception
     */
    public void test_load_WithFather_selector_null()
          throws Exception {
        father.setSelector(null);

        link.setLoadPolicy(DataLink.Policy.WITH_FATHER);
        link.start();

        father.mockBeforeLoadEvent();
        assertEquals("Le fils a n'a pas ajouté sa requête", 0, son.getAddLoadRequestCall());
        assertEquals("Le fils a été vidé", 1, son.clear_called);
    }


    public void test_load_WithFather_start_stop()
          throws Exception {
        father.setSelectedRow(null);
        link.setLoadPolicy(DataLink.Policy.WITH_FATHER);

        father.mockBeforeLoadEvent();
        assertEquals("Le fils n'a pas ajouté sa requête", 0, son.getAddLoadRequestCall());

        link.start();
        link.stop();

        father.mockBeforeLoadEvent();
        assertEquals("Le fils n'a pas ajouté sa requête", 0, son.getAddLoadRequestCall());

        link.start();

        father.setSelectedRow(fatherRow);
        assertEquals("Le fils se charge entierement une seule fois", 1, son.load_called);

        link.stop();

        father.setSelectedRow(new Row());
        assertEquals("Le fils ne s'est pas charge à nouveau", 1, son.load_called);
    }


    public void test_load_errorHandler() throws Exception {
        Mock.ErrorHandler mockError = new Mock.ErrorHandler();
        link.setErrorHandler(mockError);

        link.setLoadPolicy(DataLink.Policy.WITH_FATHER);
        father.setSelectedRow(null);
        link.start();

        son.load_error = new RequestException("bobo");
        father.setSelectedRow(fatherRow);

        assertEquals(DataLink.LOAD_ERROR, mockError.errorId);
        assertSame(son.load_error, mockError.ex);
    }


    public void test_errorHandler() throws Exception {
        assertNotNull(link.getErrorHandler());
        assertEquals(DefaultErrorHandler.class, link.getErrorHandler().getClass());
    }


    public void test_load_AfterFather() throws Exception {
        link.setLoadPolicy(DataLink.Policy.AFTER_FATHER);
        link.start();

        father.mockBeforeLoadEvent();
        assertEquals("Le fils n'a pas ajouté sa requête", 0, son.getAddLoadRequestCall());

        father.setSelectedRow(null);
        assertEquals("Le fils a été vidé", 1, son.clear_called);

        father.mockLoadEvent();

        // Changement de selection (exemple d'un ListDataSource)
        father.setSelectedRow(fatherRow);
        assertEquals("Le fils se charge entierement une seule fois", 1, son.load_called);
        assertSonSelectorFromFatherSelectedRow();

        // Changement de selection (exemple d'un ListDataSource)
        father.setSelectedRow(null);
        assertEquals("Le fils a été vidé", 2, son.clear_called);
    }


    public void test_load_None() throws Exception {
        link.setLoadPolicy(DataLink.Policy.NONE);
        link.start();

        father.mockBeforeLoadEvent();
        assertEquals("Le fils n'a pas ajouté sa requête", 0, son.getAddLoadRequestCall());

        father.setSelectedRow(null);
        assertEquals("Le fils n'a pas été vidé", 0, son.clear_called);

        father.mockLoadEvent();
        assertEquals("Le fils se charge pas du tout", 0, son.load_called);
    }


    public void test_notDeclaredJoinKeys_List_to_Detail()
          throws Exception {
        ListDataSource fatherDataSource = new ListDataSource();
        DetailDataSource sonDataSource = newDetailDataSource();

        fatherDataSource.setColumns(new String[]{"colFath1"});

        sonDataSource.declare("colSon1");
        // Configuration Lien Pere/Fils
        JoinKeys keys = new JoinKeys();
        keys.addAssociation("joinKeyFather", "joinKeySon");
        keys.addAssociation("joinKeySame");
        new DataLink(fatherDataSource, sonDataSource, keys);

        assertEquals(3, fatherDataSource.getColumns().length);
        assertEquals("colFath1", fatherDataSource.getColumns()[0]);
        assertEquals("joinKeyFather", fatherDataSource.getColumns()[1]);
        assertEquals("joinKeySame", fatherDataSource.getColumns()[2]);

        assertEquals("[joinKeySon, colSon1, joinKeySame]",
                     sonDataSource.getDeclaredFields().keySet().toString());
    }


    public void test_notDeclaredJoinKeys_Detail_to_List()
          throws Exception {
        DetailDataSource fatherDataSource = newDetailDataSource();
        ListDataSource sonDataSource = new ListDataSource();

        sonDataSource.setColumns(new String[]{"colSon1"});

        fatherDataSource.declare("colFath1");
        // Configuration Lien Pere/Fils
        JoinKeys keys = new JoinKeys();
        keys.addAssociation("joinKeyFather", "joinKeySon");
        keys.addAssociation("joinKeySame");
        new DataLink(fatherDataSource, sonDataSource, keys).start();

        assertEquals(3, sonDataSource.getColumns().length);
        assertEquals("colSon1", sonDataSource.getColumns()[0]);
        assertEquals("joinKeySon", sonDataSource.getColumns()[1]);
        assertEquals("joinKeySame", sonDataSource.getColumns()[2]);

        assertEquals("[joinKeyFather, joinKeySame, colFath1]",
                     fatherDataSource.getDeclaredFields().keySet().toString());
    }


    public void test_noLoadSon_when_allSonSelectorsNull() throws Exception {
        DetailDataSource fatherDetail = newDetailDataSource();
        fatherDetail.declare("fatherA");
        fatherDetail.declare("fatherB");

        link = new DataLink(fatherDetail, son, joinKeys);
        link.setLoadPolicy(DataLink.Policy.AFTER_FATHER);
        link.start();

        fatherDetail.clear();

        assertEquals("Le fils n'est pas loadé", 0, son.load_called);
        assertEquals("Le fils a été vidé", 1, son.clear_called);
    }


    @Override
    protected void setUp() throws Exception {
        father = new Mock.DataSource();
        son = new Mock.DataSource();

        // Pk fatherA / sonA  et fatherB / sonB
        fatherRow = new Row();
        fatherRow.addField("fatherA", "valueA");
        fatherRow.addField("fatherB", "valueB");
        fatherRow.addField("other", "pas touche");
        father.setSelectedRow(fatherRow);

        FieldsList selector = new Row();
        selector.addField("fatherA", "selectorA");
        selector.addField("fatherB", "selectorB");
        selector.addField("other", "selector pas touche");
        father.setSelector(selector);

        joinKeys = new JoinKeys();
        joinKeys.addAssociation("fatherA", "sonA");
        joinKeys.addAssociation("fatherB", "sonB");

        link = new DataLink(father, son, joinKeys);
    }


    private void assertSonSelectorFromFatherSelectedRow() {
        assertNotNull(son.getSelector());
        assertEquals(2, son.getSelector().getFieldCount());
        assertEquals("valueA", son.getSelector().getFieldValue("sonA"));
        assertEquals("valueB", son.getSelector().getFieldValue("sonB"));
    }


    private void assertSonSelectorFromFatherSelector() {
        assertNotNull(son.getSelector());
        assertEquals(2, son.getSelector().getFieldCount());
        assertEquals("selectorA", son.getSelector().getFieldValue("sonA"));
        assertEquals("selectorB", son.getSelector().getFieldValue("sonB"));
    }


    private DetailDataSource newDetailDataSource() {
        return new DetailDataSource(new DefaultGuiContext());
    }
}
