package net.codjo.mad.gui.request;
import net.codjo.mad.client.request.FieldsList;
import net.codjo.mad.client.request.Request;
import net.codjo.mad.client.request.RequestSender;
import net.codjo.mad.client.request.Result;
import net.codjo.mad.client.request.Row;
import net.codjo.mad.client.request.SelectRequest;
import net.codjo.mad.client.request.util.RequestTestHelper;
import net.codjo.mad.gui.request.event.DataSourceAdapter;
import net.codjo.mad.gui.request.event.DataSourceEvent;
import net.codjo.mad.gui.request.factory.SelectFactory;
import net.codjo.mad.gui.request.util.MultiRequestsHelper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import junit.framework.TestCase;
import org.junit.Assert;
/**
 * Classe de test pour <code>ListDataSource</code>.
 *
 * @author $Author: duclosm $
 * @version $Revision: 1.15 $
 */
public class ListDataSourceTest extends TestCase {
    private static final String[] COLUMNS = {"pimsCode", "isin", "sicovam"};
    private static final String SELECT_ALL_ID = "selectAll";
    private static final String SELECT_BY_FILE_ID = "selectByID";
    private ListDataSource dataSource;
    private RequestTestHelper helper;


    /**
     * Test qu'une ligne ajouté est accessible normalement par getValueAt lorsque le dataSource est vide.
     */
    public void test_addRow_NoResult() throws Exception {
        assertEquals(0, dataSource.getAddedRow().length);

        Row newRow = new Row();
        newRow.addField("pimsCode", "new");

        int idx = dataSource.addRow(newRow);

        assertEquals("Indice de la nvlle ligne", 0, idx);
        assertEquals(1, dataSource.getRowCount());
        assertEquals("new", dataSource.getValueAt(0, "pimsCode"));
        assertEquals(1, dataSource.getAddedRow().length);
        assertEquals(newRow, dataSource.getAddedRow()[0]);
    }


    public void test_addRow_null() throws Exception {
        dataSource.setLoadResult(buildResult());
        assertEquals(2, dataSource.getRowCount());

        try {
            dataSource.addRow(null);
            fail("Ajouter null est refusé");
        }
        catch (IllegalArgumentException ex) {
            ;
        }

        assertEquals(2, dataSource.getRowCount());
    }


    public void test_getRecordCount_addRow() throws Exception {
        Result result = buildResult();
        result.setTotalRowCount(4);

        dataSource.setLoadResult(result);
        assertEquals(4, dataSource.getTotalRowCount());

        dataSource.addRow(new Row());

        assertEquals(5, dataSource.getTotalRowCount());
    }


    public void test_getRecordCount_bis() throws Exception {
        Result result = buildResult();
        result.setTotalRowCount(2);

        dataSource.setLoadResult(result);
        assertEquals(2, dataSource.getTotalRowCount());

        dataSource.addRow(new Row());

        assertEquals(3, dataSource.getTotalRowCount());
    }


    public void test_getRecordCount_ter() throws Exception {
        Result result = buildResult();
        result.setTotalRowCount(2);

        dataSource.setLoadResult(result);
        assertEquals(2, dataSource.getTotalRowCount());

        dataSource.removeRow(0);

        assertEquals(1, dataSource.getTotalRowCount());
    }


    /**
     * Test qu'une ligne ajouté 2x est refusé.
     */
    public void test_addRow_sameRowTwice() throws Exception {
        dataSource.setLoadResult(buildResult());
        assertEquals(2, dataSource.getRowCount());

        Row newRow = new Row();
        newRow.addField("pimsCode", "new");
        dataSource.addRow(newRow);

        try {
            dataSource.addRow(newRow);
            fail("Ajouter 2 fois la même instance est refusé");
        }
        catch (IllegalArgumentException ex) {
            ; // Ok
        }

        assertEquals(3, dataSource.getRowCount());
        assertEquals("new", dataSource.getValueAt(2, "pimsCode"));
    }


    /**
     * Test qu'une ligne ajouté puis supprimé n'apparait plus dans les listes ajout et suppr.
     */
    public void test_addRow_then_removeRow() throws Exception {
        assertEquals(0, dataSource.getAddedRow().length);

        Row newRow = new Row();
        newRow.addField("pimsCode", "new");

        int idx = dataSource.addRow(newRow);
        assertEquals(1, dataSource.getAddedRow().length);
        assertEquals(0, dataSource.getRemovedRow().length);

        dataSource.removeRow(idx);
        assertEquals(0, dataSource.getAddedRow().length);
        assertEquals(0, dataSource.getRemovedRow().length);
    }


    /**
     * Test qu'une ligne ajouté est accessible normalement par getValueAt lorsque le dataSource n'est pas
     * vide.
     */
    public void test_addRow_withResult() throws Exception {
        assertEquals(0, dataSource.getAddedRow().length);

        dataSource.setLoadResult(buildResult());
        assertEquals(2, dataSource.getRowCount());

        Row newRow = new Row();
        newRow.addField("pimsCode", "new");
        dataSource.addRow(newRow);

        assertEquals(3, dataSource.getRowCount());
        assertEquals("new", dataSource.getValueAt(2, "pimsCode"));
        assertEquals(1, dataSource.getAddedRow().length);
        assertEquals(newRow, dataSource.getAddedRow()[0]);
    }


    /**
     * Test la remise a zero d'une ListDataSource.
     */
    public void test_clear() throws Exception {
        dataSource.setLoadResult(buildResult());
        assertEquals(2, dataSource.getRowCount());

        // Ajout
        Row newRow = new Row();
        newRow.addField("pimsCode", "new");
        dataSource.addRow(newRow);

        // Delete
        dataSource.removeRow(0);

        // Update
        dataSource.setValue(0, "isin", "updateIsin");

        // Selection d'une ligne
        dataSource.setSelectedRow(dataSource.getRow(0));

        // Clear
        FakeDataSourceListener listener = new FakeDataSourceListener();
        dataSource.addDataSourceListener(listener);
        dataSource.clear();

        // Assert
        assertEquals(0, dataSource.getRowCount());
        assertEquals(0, dataSource.getAddedRow().length);
        assertEquals(0, dataSource.getRemovedRow().length);
        assertEquals(0, dataSource.getUpdatedRow().length);
        assertEquals(null, dataSource.getSelectedRow());
        assertTrue("loadEvent called", listener.loadEvent);
    }


    /**
     * Test que le chargement s'effectue correctement.
     */
    public void test_load() throws Exception {
        helper.setRequest(getRequestListForTest(1, ListDataSource.PAGE_SIZE));
        helper.setResult(getRequestResult());
        helper.activate();

        dataSource.setSelectedRow(new Row());

        FakeDataSourceListener listener = new FakeDataSourceListener();
        dataSource.addDataSourceListener(listener);

        dataSource.load();

        helper.verify();
        assertTrue(!dataSource.hasPreviousPage());
        assertEquals(1, dataSource.getLoadResult().getRowCount());
        assertEquals("666", dataSource.getLoadResult().getValue(0, "pimsCode"));
        assertTrue("loadEvent called", listener.loadEvent);
        assertNull(dataSource.getSelectedRow());
    }


    /**
     * Test que le chargement de la page suivante s'effectue correctement.
     */
    public void test_loadNextPage() throws Exception {
        helper.setRequest(getRequestListForTest(2, ListDataSource.PAGE_SIZE));
        helper.setResult(getRequestResult());
        helper.activate();

        Result result = new Result();
        for (int i = 0; i < ListDataSource.PAGE_SIZE; i++) {
            result.addRow(new Row());
        }
        result.setTotalRowCount(2 * ListDataSource.PAGE_SIZE);
        dataSource.setLoadResult(result);
        dataSource.loadNextPage();

        helper.verify();
        assertFalse(dataSource.hasNextPage());
        assertTrue(dataSource.hasPreviousPage());
        assertEquals(1, dataSource.getLoadResult().getRowCount());
        assertEquals("666", dataSource.getLoadResult().getValue(0, "pimsCode"));
    }


    /**
     * Test que le chargement de la page precedante s'effectue correctement.
     */
    public void test_loadPreviousPage() throws Exception {
        helper.setRequest(getRequestListForTest(1, ListDataSource.PAGE_SIZE));
        helper.setResult(getRequestResult());
        helper.activate();

        dataSource.setCurrentPage(2);
        dataSource.loadPreviousPage();

        helper.verify();
        assertEquals(1, dataSource.getLoadResult().getRowCount());
    }


    public void test_load_EditingMode_Add() throws Exception {
        helper.setRequest(getRequestListForTest(1, ListDataSource.PAGE_SIZE));
        helper.setResult(getRequestResult());
        helper.activate();

        dataSource.setLoadResult(buildResult());
        assertEquals(2, dataSource.getRowCount());

        Row newRow = new Row();
        newRow.addField("pimsCode", "new");
        dataSource.addRow(newRow);
        assertEquals(3, dataSource.getRowCount());

        dataSource.load();
        helper.verify();
        assertFalse(dataSource.hasBeenUpdated());

        dataSource.setLoadResult(new Result());
        assertFalse(dataSource.hasBeenUpdated());
    }


    public void test_load_EditingMode_Remove() throws Exception {
        dataSource.setLoadResult(buildResult());
        assertEquals(2, dataSource.getRowCount());

        dataSource.removeRow(0);

        dataSource.setLoadResult(new Result());
        assertFalse(dataSource.hasBeenUpdated());
    }


    public void test_load_EditingMode_Update() throws Exception {
        dataSource.setLoadResult(buildResult());
        assertEquals(2, dataSource.getRowCount());

        dataSource.setValue(0, "pimsCode", "bobo");

        dataSource.setLoadResult(new Result());
        assertFalse(dataSource.hasBeenUpdated());
    }


    /**
     * Test que le chargement s'effectue correctement, et que lorsque la factory de select est null, le
     * chargement provoque un vidage du DataSource..
     */
    public void test_load_and_loadEmpty() throws Exception {
        helper.setRequest(getRequestListForTest(1, ListDataSource.PAGE_SIZE));
        helper.setResult(getRequestResult());
        helper.activate();

        dataSource.load();

        helper.verify();
        assertEquals(1, dataSource.getLoadResult().getRowCount());

        dataSource.setLoadFactory(null);
        dataSource.load();
        assertEquals(0, dataSource.getLoadResult().getRowCount());
    }


    /**
     * Test que si aucune factory de load n'est positionne (pour les colonnes), alors aucune requete n'est
     * envoye aux serveur et qu'une erreur est lance.
     */
    public void test_load_noColumns() throws Exception {
        dataSource.setColumns(new String[]{});
        try {
            dataSource.load();
            fail("Le load doit echouer car aucune preference n'est positionné");
        }
        catch (IllegalStateException ex) {
            ; // Ok
        }
    }


    /**
     * Test que le chargement s'effectue correctement lorsqu'une clause de selection est spécifiée.
     */
    public void test_load_withSelector() throws Exception {
        helper.setRequest(getRequestListForTestWithSelector("fileId", "25"));
        helper.setResult(getRequestResult());
        helper.activate();

        dataSource.setLoadFactory(new SelectFactory(SELECT_BY_FILE_ID));

        FieldsList selector = new FieldsList();
        selector.addField("fileId", "25");
        dataSource.setSelector(selector);
        dataSource.load();

        helper.verify();
        assertTrue(!dataSource.hasPreviousPage());
        assertEquals(1, dataSource.getLoadResult().getRowCount());
        assertEquals("666", dataSource.getLoadResult().getValue(0, "pimsCode"));
    }


    /**
     * Test que l'event BeforeLoad est lance avant la construction de la requete.
     */
    public void test_load_beforeLoad() throws Exception {
        helper.setRequest(getRequestListForTestWithSelector("fileId", "25"));
        helper.setResult(getRequestResult());
        helper.activate();

        dataSource.setLoadFactory(new SelectFactory(SELECT_BY_FILE_ID));

        dataSource.addDataSourceListener(new DataSourceAdapter() {
            @Override
            public void beforeLoadEvent(DataSourceEvent event) {
                FieldsList selector = new FieldsList();
                selector.addField("fileId", "25");
                dataSource.setSelector(selector);
            }
        });
        dataSource.load();

        helper.verify();
        assertTrue(!dataSource.hasPreviousPage());
        assertEquals(1, dataSource.getLoadResult().getRowCount());
        assertEquals("666", dataSource.getLoadResult().getValue(0, "pimsCode"));
    }


    public void test_newRow() throws Exception {
        Row newRow = dataSource.newRow();
        assertEquals("Ligne vide", 0, newRow.getFieldCount());

        dataSource.setDefaultValue("pimsCode", "26");
        newRow = dataSource.newRow();
        assertEquals("Ligne avec 1 champ", 1, newRow.getFieldCount());
        assertEquals("26", newRow.getFieldValue("pimsCode"));
    }


    /**
     * Test qu'une ligne supprimé n'est plus accessible normalement par getValueAt.
     */
    public void test_removeRow() throws Exception {
        assertEquals(0, dataSource.getRemovedRow().length);

        dataSource.setLoadResult(buildResult());
        assertEquals(2, dataSource.getRowCount());

        Row deletedRow = dataSource.removeRow(0);

        assertEquals(1, dataSource.getRowCount());
        assertEquals("22", dataSource.getValueAt(0, "pimsCode"));
        assertEquals(1, dataSource.getRemovedRow().length);
        assertEquals(deletedRow, dataSource.getRemovedRow()[0]);
    }


    /**
     * Test que la suppression d'un mauvais indice est detecte.
     */
    public void test_removeRow_badIndex() throws Exception {
        dataSource.setLoadResult(buildResult());
        assertEquals(2, dataSource.getRowCount());

        try {
            dataSource.removeRow(100);
            fail("Suppression avec un mauvais idx");
        }
        catch (Exception ex) {
            ; // Ok
        }
        try {
            dataSource.removeRow(-1);
            fail("Suppression avec un mauvais idx");
        }
        catch (Exception ex) {
            ; // Ok
        }
    }


    /**
     * Test l'etat (Edition ou non) apres un addSaveRequestTo.
     */
    public void test_save_EditState() throws Exception {
        dataSource.setDeleteFactoryId("deleteStuff");
        dataSource.setUpdateFactoryId("updateStuff");
        dataSource.setInsertFactoryId("insertStuff");

        dataSource.setLoadResult(buildResult());
        assertEquals(2, dataSource.getRowCount());

        // Ajout
        Row newRow = new Row();
        newRow.addField("pimsCode", "new");
        dataSource.addRow(newRow);

        // Simulation envoie au serveur
        MockMultiRequestsHelper mrh = new MockMultiRequestsHelper();
        dataSource.addSaveRequestTo(mrh);
        assertEquals("insertStuff,", mrh.submitQuery);

        mrh = new MockMultiRequestsHelper();
        dataSource.addSaveRequestTo(mrh);
        assertEquals("insertStuff,", mrh.submitQuery);

        // Test retour de query
        RequestSubmiter insertStuffSubmiter = mrh.getSubmiters().get(0);
        insertStuffSubmiter.setResult(buildOneRowOneFieldResult("pimsCode", "gex"));

        // Plus de requete à envoyer (car deja fait)
        mrh = new MockMultiRequestsHelper();
        dataSource.addSaveRequestTo(mrh);
        assertEquals("", mrh.submitQuery);

        // Pas en mode Edition (sinon ca balancerait une Exception)
        dataSource.setLoadResult(new Result());
    }


    /**
     * Verifie que le resultat d'un addSaveRequestTo est pris en compte par le datasource.
     */
    public void test_save_QueryResult() throws Exception {
        dataSource.setDeleteFactoryId("deleteStuff");
        dataSource.setUpdateFactoryId("updateStuff");
        dataSource.setInsertFactoryId("insertStuff");

        // Ajout
        Row newRow = new Row();
        newRow.addField("pimsCode", "new");
        dataSource.addRow(newRow);

        // Simulation envoie au serveur
        MockMultiRequestsHelper mrh = new MockMultiRequestsHelper();
        dataSource.addSaveRequestTo(mrh);
        assertEquals("insertStuff,", mrh.submitQuery);
        RequestSubmiter insertStuffSubmiter = mrh.getSubmiters().get(0);

        // Simulation retour serveur
        insertStuffSubmiter.setResult(buildOneRowOneFieldResult("pimsCode", "toto"));

        // Assert
        assertEquals("toto", dataSource.getValueAt(0, "pimsCode"));
    }

     public void test_save_QueryWithEmptyResult() throws Exception {
        dataSource.setDeleteFactoryId("deleteStuff");
        dataSource.setUpdateFactoryId("updateStuff");
        dataSource.setInsertFactoryId("insertStuff");

        // Ajout
        Row newRow = new Row();
        newRow.addField("pimsCode", "new");
        dataSource.addRow(newRow);

        // Simulation envoie au serveur
        MockMultiRequestsHelper mrh = new MockMultiRequestsHelper();
        dataSource.addSaveRequestTo(mrh);
        assertEquals("insertStuff,", mrh.submitQuery);
        RequestSubmiter insertStuffSubmiter = mrh.getSubmiters().get(0);

        // Simulation retour serveur sans result du handler
        insertStuffSubmiter.setResult(new Result());

        // Assert
        assertEquals("new", dataSource.getValueAt(0, "pimsCode"));
    }


    /**
     * Test qu'aucune requete n'est faite si il n'y a pas de modification en cours.
     */
    public void test_save_noEdit() throws Exception {
        dataSource.setDeleteFactoryId("deleteStuff");
        dataSource.setUpdateFactoryId("updateStuff");
        dataSource.setInsertFactoryId("insertStuff");

        dataSource.setLoadResult(buildResult());
        assertEquals(2, dataSource.getRowCount());

        FakeDataSourceListener listener = new FakeDataSourceListener();
        dataSource.addDataSourceListener(listener);

        MockMultiRequestsHelper mrh = new MockMultiRequestsHelper();
        dataSource.addSaveRequestTo(mrh);
        assertEquals("Aucune requte envoyé", "", mrh.submitQuery);
        assertEquals(true, listener.beforeSaveEvent);
    }


    /**
     * Test Erreur lors de l'envoie si il manque les factory.
     */
    public void test_save_noFactory() throws Exception {
        dataSource.setLoadResult(buildResult());
        assertEquals(2, dataSource.getRowCount());

        // Ajout
        Row newRow = new Row();
        newRow.addField("pimsCode", "new");
        dataSource.addRow(newRow);

        // Delete
        dataSource.removeRow(0);

        // Update
        dataSource.setValue(0, "isin", "updateIsin");

        // Essaye.
        try {
            dataSource.addSaveRequestTo(new MockMultiRequestsHelper());
            fail("pas de factory delete");
        }
        catch (Exception ex) {
            ; // Ok
        }

        dataSource.setDeleteFactoryId("deleteStuff");
        try {
            dataSource.addSaveRequestTo(new MockMultiRequestsHelper());
            fail("pas de factory update");
        }
        catch (Exception ex) {
            ; // Ok
        }
        dataSource.setUpdateFactoryId("updateStuff");

        try {
            dataSource.addSaveRequestTo(new MockMultiRequestsHelper());
            fail("pas de factory insert");
        }
        catch (Exception ex) {
            ; // Ok
        }
        dataSource.setInsertFactoryId("insertStuff");

        // Cas OK
        MockMultiRequestsHelper mrh = new MockMultiRequestsHelper();
        dataSource.addSaveRequestTo(mrh);
        assertEquals("deleteStuff,updateStuff,insertStuff,", mrh.submitQuery);
    }


    /**
     * Test update possible sans les factory de delete et insert.
     */
    public void test_save_updateFactoryOnly() throws Exception {
        dataSource.setLoadResult(buildResult());
        assertEquals(2, dataSource.getRowCount());

        // Update
        dataSource.setValue(0, "isin", "updateIsin");
        dataSource.setUpdateFactoryId("updateStuff");

        MockMultiRequestsHelper mrh = new MockMultiRequestsHelper();
        dataSource.addSaveRequestTo(mrh);
        assertEquals("updateStuff,", mrh.submitQuery);
    }


    /**
     * Test delete possible sans les factory de update et insert.
     */
    public void test_save_deleteFactoryOnly() throws Exception {
        dataSource.setLoadResult(buildResult());
        assertEquals(2, dataSource.getRowCount());

        // Delete
        dataSource.removeRow(0);
        dataSource.setDeleteFactoryId("deleteStuff");

        MockMultiRequestsHelper mrh = new MockMultiRequestsHelper();
        dataSource.addSaveRequestTo(mrh);
        assertEquals("deleteStuff,", mrh.submitQuery);
    }


    /**
     * Test insert possible sans les factory de delete et update.
     */
    public void test_save_insertFactoryOnly() throws Exception {
        dataSource.setLoadResult(buildResult());
        assertEquals(2, dataSource.getRowCount());

        // Ajout
        Row newRow = new Row();
        newRow.addField("pimsCode", "new");
        dataSource.addRow(newRow);
        dataSource.setInsertFactoryId("insertStuff");

        MockMultiRequestsHelper mrh = new MockMultiRequestsHelper();
        dataSource.addSaveRequestTo(mrh);
        assertEquals("insertStuff,", mrh.submitQuery);
    }


    /**
     * Test l'ordre d'envoie des requetes.
     */
    public void test_save_queryOrder() throws Exception {
        dataSource.setDeleteFactoryId("deleteStuff");
        dataSource.setUpdateFactoryId("updateStuff");
        dataSource.setInsertFactoryId("insertStuff");

        dataSource.setLoadResult(buildResult());
        assertEquals(2, dataSource.getRowCount());

        // Ajout
        Row newRow = new Row();
        newRow.addField("pimsCode", "new");
        dataSource.addRow(newRow);

        // Delete
        dataSource.removeRow(0);

        // Update
        dataSource.setValue(0, "isin", "updateIsin");

        MockMultiRequestsHelper mrh = new MockMultiRequestsHelper();
        dataSource.addSaveRequestTo(mrh);

        assertEquals("deleteStuff,updateStuff,insertStuff,", mrh.submitQuery);
    }


    /**
     * Test qu'une ligne ajouté est remplit par le filler.
     */
    public void test_setRowFiller() throws Exception {
        assertEquals(0, dataSource.getAddedRow().length);

        Row newRow = new Row();
        newRow.addField("pimsCode", "null");
        newRow.addField("siCode", "null");

        MockRowFiller filler = new MockRowFiller();
        dataSource.setRowFiller(filler);

        dataSource.addRow(newRow);

        assertTrue("Filler est appelé", filler.fillAddedRowCalled);
        assertEquals("Indice de la nvlle ligne", 0, filler.idx);
        assertEquals("Ligne", newRow, filler.row);
        assertEquals("Datasource", dataSource, filler.lds);
    }


    /**
     * Test qu'une ligne ajouté puis modifié n'est pas dans la liste des modifié.
     */
    public void test_setValue_addedRow() throws Exception {
        assertEquals(0, dataSource.getUpdatedRow().length);

        Row newRow = new Row();
        newRow.addField("pimsCode", "new");
        dataSource.addRow(newRow);

        dataSource.setValue(0, "pimsCode", "nvMAJ");

        assertEquals(0, dataSource.getUpdatedRow().length);
        assertEquals("nvMAJ", dataSource.getValueAt(0, "pimsCode"));
    }


    public void test_apply() throws Exception {
        dataSource.setLoadResult(buildResult());
        assertEquals(0, dataSource.getUpdatedRow().length);

        dataSource.apply("sicovam", "newsico");
        assertEquals("newsico", dataSource.getValueAt(0, "sicovam"));
        assertEquals("newsico", dataSource.getValueAt(1, "sicovam"));

        assertEquals(2, dataSource.getUpdatedRow().length);
    }


    /**
     * Test le cas avec des arguments incorrecte.
     */
    public void test_setValue_failure() throws Exception {
        dataSource.setLoadResult(buildResult());
        assertEquals(2, dataSource.getRowCount());

        try {
            dataSource.setValue(100, "pimsCode", "null");
            fail("Modification avec un mauvais idx");
        }
        catch (Exception ex) {
            ; // Ok
        }

        try {
            dataSource.setValue(0, "unknown field name", "00");
            fail("Modification avec un nom de champs");
        }
        catch (Exception ex) {
            ; // Ok
        }
    }


    /**
     * Test la detection d'une modification sur une ligne provenant de la BD.
     */
    public void test_setValue_rowFromDB() throws Exception {
        dataSource.setLoadResult(buildResult());
        assertEquals(0, dataSource.getUpdatedRow().length);

        dataSource.setValue(0, "pimsCode", "nv");

        assertEquals(1, dataSource.getUpdatedRow().length);
        assertEquals("nv", dataSource.getValueAt(0, "pimsCode"));

        dataSource.setValue(0, "pimsCode", "nvNew");

        assertEquals(1, dataSource.getUpdatedRow().length);
        assertEquals("nvNew", dataSource.getValueAt(0, "pimsCode"));
    }


    /**
     * Test que repositionner la même valeur n'est pas detectée comme une modification .
     */
    public void test_setValue_sameValue() throws Exception {
        dataSource.setLoadResult(buildResult());

        assertEquals("11", dataSource.getValueAt(0, "pimsCode"));
        assertEquals(0, dataSource.getUpdatedRow().length);

        dataSource.setValue(0, "pimsCode", "11");

        assertEquals("11", dataSource.getValueAt(0, "pimsCode"));
        assertEquals(0, dataSource.getUpdatedRow().length);
    }


    /**
     * Test qu'une ligne modifié puis supprimé n'apparait plus dans la liste modifié.
     */
    public void test_setValue_then_removeRow() throws Exception {
        dataSource.setLoadResult(buildResult());

        dataSource.setValue(0, "pimsCode", "nv");
        assertEquals(1, dataSource.getUpdatedRow().length);
        assertEquals(0, dataSource.getRemovedRow().length);

        dataSource.removeRow(0);
        assertEquals(0, dataSource.getUpdatedRow().length);
        assertEquals(1, dataSource.getRemovedRow().length);
    }


    public void test_updateWithSameValue() throws Exception {
        dataSource.setLoadResult(buildResult());

        dataSource.setValue(0, "isin", "isin1");
        assertFalse(dataSource.hasBeenUpdated());
        dataSource.setValue(0, "isin", "new");
        assertTrue(dataSource.hasBeenUpdated());
    }


    public void test_declare() throws Exception {
        dataSource.setColumns(new String[]{"col1", "col2"});
        dataSource.declare("col3");
        assertEquals("col1", dataSource.getColumns()[0]);
        assertEquals("col2", dataSource.getColumns()[1]);
        assertEquals("col3", dataSource.getColumns()[2]);

        dataSource.declare("col3");
        assertEquals(3, dataSource.getColumns().length);
        assertEquals("col1", dataSource.getColumns()[0]);
        assertEquals("col2", dataSource.getColumns()[1]);
        assertEquals("col3", dataSource.getColumns()[2]);
    }


    public void test_selectedRow() throws Exception {
        Assert.assertNull(dataSource.getSelectedRow());

        dataSource.setSelectedRow(new Row());
        Assert.assertNotNull(dataSource.getSelectedRow());

        dataSource.setSelectedRow(null);
        Assert.assertNull(dataSource.getSelectedRow());

        dataSource.setSelection(new SelectionDataSource((Row)null));
        Assert.assertNull(dataSource.getSelectedRow());

        dataSource.setSelection(null);
        Assert.assertNull(dataSource.getSelectedRow());

        final Row expectedRow = new Row();
        dataSource.setSelection(new SelectionDataSource(expectedRow));
        Assert.assertEquals(expectedRow, dataSource.getSelectedRow());

        dataSource.setSelection(new SelectionDataSource(new Row[]{}));
        Assert.assertNull(dataSource.getSelectedRow());

        dataSource.setSelection(new SelectionDataSource(new Row[]{expectedRow}));
        Assert.assertEquals(expectedRow, dataSource.getSelectedRow());

        dataSource.setSelection(new SelectionDataSource(new Row[]{expectedRow, new Row()}));
        Assert.assertEquals(expectedRow, dataSource.getSelectedRow());
    }


    public void testGetDistinctNotNullValues() {
        Result result = new Result();
        result.setRows(buildRows(new String[]{"int", "code", "label"},
                                 new String[][]{
                                       {"11", "theCode", "label1"},
                                       {"22", "theCode", null},
                                       {"33", "theCode", null},
                                       {"44", "theCode", "label2"},
                                       {"55", "theCode", "label2"}
                                 }));
        dataSource.setLoadResult(result);
        assertDistinctValues("int", "11", "22", "33", "44", "55");
        assertDistinctValues("code", "theCode");
        assertDistinctValues("label", "label1", "label2");
    }


    public void testGetDistinctNotNullValuesWithoutResult() {
        assertTrue(dataSource.getDistinctNotNullValues("name").isEmpty());
    }


    @Override
    protected void setUp() {
        helper = new RequestTestHelper();
        dataSource = new ListDataSource();
        Preference pref = new Preference();
        pref.setSelectAllId(SELECT_ALL_ID);
        dataSource.setLoadFactory(pref.getSelectAll());
        dataSource.setColumns(COLUMNS);
    }


    @Override
    protected void tearDown() {
        helper.tearDown();
    }


    private void assertDistinctValues(String fieldName, String... expectedValues) {
        Set<String> actualValues = dataSource.getDistinctNotNullValues(fieldName);
        assertEquals(expectedValues.length, actualValues.size());
        Iterator<String> iterator = actualValues.iterator();
        for (String expectedValue : expectedValues) {
            assertEquals(expectedValue, iterator.next());
        }
    }


    private Request[] getRequestListForTest(int pageNumber, int pageSize) throws Exception {
        SelectRequest select = new SelectRequest();
        select.setId(SELECT_ALL_ID);
        // On fait ce truc bizzare pour que les attributs soit dans le même
        //  ordre que la requete envoye par la table.
        select.setAttributes(buildFields(COLUMNS));
        select.setPage(pageNumber, pageSize);

        return new Request[]{select};
    }


    private Request[] getRequestListForTestWithSelector(String name, String value) throws Exception {
        SelectRequest select = new SelectRequest();
        select.setId(SELECT_BY_FILE_ID);
        // On fait ce truc bizzare pour que les attributs soit dans le même
        //  ordre que la requete envoye par la table.
        select.setAttributes(buildFields(COLUMNS));

        select.addSelector(name, value);
        select.setPage(1, ListDataSource.PAGE_SIZE);

        return new Request[]{select};
    }


    private String getRequestResult() {
        return "<?xml version=\"1.0\"?>" + "<results>" + "     <result request_id=\""
               + (helper.getRequestId(0)) + "\">" + "        <primarykey>"
               + "           <field name=\"pimsCode\"/>" + "        </primarykey>"
               + "        <row>" + "           <field name=\"pimsCode\">666</field>"
               + "           <field name=\"isin\">isinVal</field>"
               + "           <field name=\"sicovam\">sicovamVal</field>" + "        </row>"
               + "     </result>" + "</results>";
    }


    private String[] buildFields(String[] attributes) {
        final Set<String> stringSet = new HashSet<String>();
        stringSet.addAll(Arrays.asList(attributes));
        return stringSet.toArray(new String[stringSet.size()]);
    }


    private Result buildOneRowOneFieldResult(String name, String val) {
        Result result = new Result();
        Row row = new Row();
        row.addField(name, val);
        result.addRow(row);
        return result;
    }


    private Result buildResult() {
        Result result = new Result();
        result.setPrimaryKey("pimsCode");
        result.setRows(buildRows(COLUMNS,
                                 new String[][]{
                                       {"11", "isin1", "sico1"},
                                       {"22", "isin2", "sico2"}
                                 }));
        return result;
    }


    private List<Row> buildRows(String[] fields, String[][] value) {
        List<Row> rowList = new ArrayList<Row>();
        for (String[] rowValues : value) {
            Row row = new Row();
            for (int col = 0; col < fields.length; col++) {
                row.addField(fields[col], rowValues[col]);
            }
            rowList.add(row);
        }
        return rowList;
    }


    public void test_setLoadManager_doLoad() throws Exception {
        Mock.LoadManager loadManagerMock = new Mock.LoadManager();
        dataSource.setLoadManager(loadManagerMock);
        assertSame(loadManagerMock, dataSource.getLoadManager());

        FakeDataSourceListener listener = new FakeDataSourceListener();
        dataSource.addDataSourceListener(listener);

        dataSource.load();

        assertTrue(loadManagerMock.doLoadCalled);
        assertTrue(listener.beforeLoadEvent);
        assertTrue(listener.loadEvent);
    }


    public void test_setLoadManager_addLoadRequestTo() throws Exception {
        Mock.LoadManager loadManagerMock = new Mock.LoadManager();
        dataSource.setLoadManager(loadManagerMock);

        dataSource.addLoadRequestTo(null);

        assertTrue(loadManagerMock.addLoadRequestToIsCalled);
    }


    private static class MockMultiRequestsHelper extends MultiRequestsHelper {
        String submitQuery = "";


        MockMultiRequestsHelper() {
            super(new RequestSender());
        }


        @Override
        public void addSubmiter(RequestSubmiter submiter) {
            super.addSubmiter(submiter);
            try {
                submitQuery += submiter.buildRequest().getHandlerId() + ",";
            }
            catch (Exception ex) {
                submitQuery += "ERROR" + ex.getMessage();
            }
        }
    }

    private class MockRowFiller implements RowFiller {
        boolean fillAddedRowCalled = false;
        int idx;
        ListDataSource lds;
        Row row;


        public void fillAddedRow(Row addedRow, int addedIdx, ListDataSource listDataSource) {
            fillAddedRowCalled = true;
            this.idx = addedIdx;
            this.row = addedRow;
            this.lds = listDataSource;
        }
    }
}
