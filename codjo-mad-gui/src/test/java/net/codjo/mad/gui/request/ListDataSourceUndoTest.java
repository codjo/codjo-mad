package net.codjo.mad.gui.request;
import net.codjo.mad.client.request.Field;
import net.codjo.mad.client.request.Result;
import net.codjo.mad.client.request.Row;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;
import junit.framework.TestCase;
/**
 * Description of the Class
 *
 * @author $Author: duclosm $
 * @version $Revision: 1.4 $
 */
public class ListDataSourceUndoTest extends TestCase {
    private static final String[] COLUMNS = {"pimsCode", "isin", "sicovam"};
    private ListDataSource dataSource;
    private FakeUndoListener listener;


    /**
     * Test l'evt Undo sur un ajout de ligne.
     *
     * @throws Exception
     */
    public void test_undo_redo_addRow() throws Exception {
        Row row = new Row();

        assertEquals(2, dataSource.getRowCount());
        dataSource.addRow(row);
        assertEquals(3, dataSource.getRowCount());

        assertTrue("La modification est détecté", listener.hasBeenCalled());
        assertEquals("Un seul event lance", 1, listener.nbOfCall);

        undo();
        assertEquals("L'annulation a enlever la ligne", 2, dataSource.getRowCount());
        assertEquals("L'annulation n'a pas lancé de nouveau event", 1, listener.nbOfCall);
        assertEquals("Aucune ligne en ajout", 0, dataSource.getAddedRow().length);

        redo();
        assertEquals("L'annulation a de nouveau ajouter la ligne", 3, dataSource.getRowCount());
        assertEquals("REDO n'a pas lancé de nouveau event", 1, listener.nbOfCall);
        assertEquals("Une ligne en ajout", 1, dataSource.getAddedRow().length);
    }


    /**
     * Test l'evt Undo sur une suppression de ligne.
     *
     * @throws Exception
     */
    public void test_undo_redo_removeRow() throws Exception {
        assertEquals(2, dataSource.getRowCount());
        dataSource.removeRow(0);
        assertEquals(1, dataSource.getRowCount());

        assertTrue("La modification est détecté", listener.hasBeenCalled());
        assertEquals("Un seul event lance", 1, listener.nbOfCall);

        undo();
        assertEquals("L'annulation a ajoute la ligne", 2, dataSource.getRowCount());
        assertEquals("L'annulation n'a pas lancé de nouveau event", 1, listener.nbOfCall);
        assertEquals("Acune ligne en suppression", 0, dataSource.getAddedRow().length);

        redo();
        assertEquals("L'annulation a de nouveau supprime la ligne", 1, dataSource.getRowCount());
        assertEquals("REDO n'a pas lancé de nouveau event", 1, listener.nbOfCall);
        assertEquals("Une ligne en suppression", 0, dataSource.getAddedRow().length);
    }


    /**
     * Test l'evt Undo sur une modification d'un champ d'une ligne.
     *
     * @throws Exception
     */
    public void test_undo_redo_updateRow() throws Exception {
        assertEquals("isin1", dataSource.getValueAt(0, "isin"));
        dataSource.setValue(0, "isin", "newIsin");
        assertEquals("newIsin", dataSource.getValueAt(0, "isin"));

        assertTrue("La modification est détecté", listener.hasBeenCalled());
        assertEquals("Un seul event lance", 1, listener.nbOfCall);

        undo();
        assertEquals("Restauration du champs", "isin1", dataSource.getValueAt(0, "isin"));
        assertEquals("L'annulation n'a pas lancé de nouveau event", 1, listener.nbOfCall);

        redo();
        assertEquals("REDO fonctionne", "newIsin", dataSource.getValueAt(0, "isin"));
        assertEquals("REDO n'a pas lancé de nouveau event", 1, listener.nbOfCall);
    }


    public void test_snapshotMode() throws Exception {
        assertDatasourceEquals(new String[][]{
              {"11", "isin1", "sico1"},
              {"22", "isin2", "sico2"}
        },
                               dataSource);

        dataSource.startSnapshotMode();
        dataSource.setValue(0, "isin", "newIsin1");
        dataSource.setValue(1, "sicovam", "newSicovam2");
        dataSource.stopSnapshotMode();
        assertDatasourceEquals(new String[][]{
              {"11", "newIsin1", "sico1"},
              {"22", "isin2", "newSicovam2"}
        },
                               dataSource);

        dataSource.setValue(0, "sicovam", "newSicovam1");
        assertDatasourceEquals(new String[][]{
              {"11", "newIsin1", "newSicovam1"},
              {"22", "isin2", "newSicovam2"}
        },
                               dataSource);

        undo();
        assertDatasourceEquals(new String[][]{
              {"11", "newIsin1", "sico1"},
              {"22", "isin2", "newSicovam2"}
        },
                               dataSource);

        undo();
        assertDatasourceEquals(new String[][]{
              {"11", "isin1", "sico1"},
              {"22", "isin2", "sico2"}
        },
                               dataSource);

        redo();
        assertDatasourceEquals(new String[][]{
              {"11", "newIsin1", "sico1"},
              {"22", "isin2", "newSicovam2"}
        },
                               dataSource);

        redo();
        assertDatasourceEquals(new String[][]{
              {"11", "newIsin1", "newSicovam1"},
              {"22", "isin2", "newSicovam2"}
        },
                               dataSource);

        dataSource.startSnapshotMode();
        Row newRow = new Row();
        List<Field> fields = new ArrayList<Field>();
        fields.add(new Field("pimsCode", "33"));
        fields.add(new Field("isin", "isin3"));
        fields.add(new Field("sicovam", "sico3"));
        newRow.setFields(fields);
        dataSource.addRow(newRow);
        dataSource.removeRow(0);
        dataSource.setValue(1, "isin", "newIsin3");
        dataSource.stopSnapshotMode();

        assertDatasourceEquals(new String[][]{
              {"22", "isin2", "newSicovam2"},
              {"33", "newIsin3", "sico3"}
        },
                               dataSource);

        dataSource.removeRow(0);
        assertDatasourceEquals(new String[][]{
              {"33", "newIsin3", "sico3"}
        },
                               dataSource);

        undo();
        assertDatasourceEquals(new String[][]{
              {"33", "newIsin3", "sico3"},
              {"22", "isin2", "newSicovam2"},
        },
                               dataSource);

        undo();
        assertDatasourceEquals(new String[][]{
              {"22", "isin2", "newSicovam2"},
              {"11", "newIsin1", "newSicovam1"},
        },
                               dataSource);

        redo();
        assertDatasourceEquals(new String[][]{
              {"22", "isin2", "newSicovam2"},
              {"33", "newIsin3", "sico3"}
        },
                               dataSource);
    }


    private void assertDatasourceEquals(String[][] expected, ListDataSource dataSource) {
        assertEquals(expected.length, dataSource.getRowCount());
        for (int i = 0; i < expected.length; i++) {
            String[] expectedRow = expected[i];
            Row row = dataSource.getRow(i);
            assertEquals(expectedRow.length, row.getFieldCount());
            for (int j = 0; j < expectedRow.length; j++) {
                String value = expectedRow[j];
                assertEquals(value, row.getField(j).getValue());
            }
        }
    }


    protected void setUp() {
        dataSource = new ListDataSource();
        dataSource.setLoadResult(buildResult());
        listener = new FakeUndoListener();
        dataSource.addUndoableEditListener(listener);
    }


    private void redo() {
        listener.undoMngr.redo();
    }


    private void undo() {
        listener.undoMngr.undo();
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


    private List buildRows(String[] fields, String[][] value) {
        List rowList = new ArrayList();
        for (int line = 0; line < value.length; line++) {
            Row row = new Row();
            for (int col = 0; col < fields.length; col++) {
                row.addField(fields[col], value[line][col]);
            }
            rowList.add(row);
        }
        return rowList;
    }


    private class FakeUndoListener implements UndoableEditListener {
        protected int nbOfCall = 0;
        protected UndoManager undoMngr = new UndoManager();
        protected UndoableEdit undoEdit;


        public boolean hasBeenCalled() {
            return undoEdit != null;
        }


        public void undoableEditHappened(UndoableEditEvent event) {
            undoEdit = event.getEdit();
            undoMngr.addEdit(undoEdit);
            nbOfCall++;
        }
    }
}
