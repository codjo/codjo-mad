package net.codjo.mad.gui.request.util.selection;
import net.codjo.mad.client.request.Row;
import net.codjo.mad.gui.request.Column;
import net.codjo.mad.gui.request.JoinKeys;
import net.codjo.mad.gui.request.ListDataSource;
import net.codjo.mad.gui.request.Preference;
import net.codjo.mad.gui.request.RowFiller;
import java.awt.Color;
import java.awt.Component;
import java.util.Arrays;
import javax.swing.Action;
import javax.swing.table.TableCellRenderer;
import junit.framework.TestCase;
/**
 * Classe de test de {@link SelectionLogic}.
 */
public class SelectionLogicTest extends TestCase {
    private SelectionLogic logic;
    private MockGui gui;
    private MockRowFiller filler;
    private JoinKeys from2To;


    /**
     * Teste qu'une ligne a été ajouté dans la table To.
     */
    public void testAddSelectRowAction() {
        assertEquals(0, gui.getToTable().getRowCount());

        final Row row = getFromDS().getRow(1);
        getFromDS().setSelectedRow(row);

        logic.getSelectAction().actionPerformed(null);

        assertEquals("La ligne se retrouve dans 'to'", 1, getToDS().getRowCount());
        assertEquals("Le filler est appelé", 1, filler.fillAddedRowCalled);
        assertSame(row, filler.addedRow);
    }


    /**
     * Teste la re-sélection d'une ligne.
     *
     * @see SelectionLogicTest#testAddSelectSameRowAction()
     */
    public void testAddSelectSameRowAction() {
        assertEquals(0, gui.getToTable().getRowCount());

        Action action = logic.getSelectAction();

        getFromDS().setSelectedRow(getFromDS().getRow(1));
        action.actionPerformed(null);

        assertEquals(1, getToDS().getRowCount());
        assertFalse(action.isEnabled());

        getFromDS().setSelectedRow(getFromDS().getRow(2));
        assertTrue("Ligne non présente dans 'to' : action enabled", action.isEnabled());
        getFromDS().setSelectedRow(getFromDS().getRow(1));
        assertFalse("Ligne déjà présente dans 'to' : action disabled", action.isEnabled());
    }


    /**
     * Teste la déselection d'une ligne.
     */
    public void testUnSelectRowAction() {
        Action action = logic.getSelectAction();
        getFromDS().setSelectedRow(getFromDS().getRow(1));
        action.actionPerformed(null);
        assertEquals(getToDS().getRowCount(), 1);

        Action unSelect = logic.getUnSelectAction();
        getToDS().setSelectedRow(getToDS().getRow(0));
        unSelect.actionPerformed(null);
        assertEquals(getToDS().getRowCount(), 0);

        getFromDS().setSelectedRow(getFromDS().getRow(1));
        assertTrue(action.isEnabled());
    }


    public void testHasBeenSelected() throws Exception {
        getToDS().addRow(newRow("fromId", "2", "Id2", "2"));
        assertTrue("La ligne n'est pas sélectionnée ", logic.hasBeenTransfered(1));
        assertFalse("La ligne est  sélectionnée ", logic.hasBeenTransfered(0));
        assertFalse("La ligne est  sélectionnée ", logic.hasBeenTransfered(2));
    }


    /**
     * Teste l'activation du bouton de désélection.
     *
     * @see SelectionLogicTest#testEnableUnSelectRowAction()
     */
    public void testEnableUnSelectRowAction() {
        // Insertion d'une ligne dans la table To
        Action action = logic.getSelectAction();
        getFromDS().setSelectedRow(getFromDS().getRow(1));
        action.actionPerformed(null);
        assertEquals(getToDS().getRowCount(), 1);

        Action unSelect = logic.getUnSelectAction();
        assertFalse(unSelect.isEnabled());
        getToDS().setSelectedRow(getToDS().getRow(0));
        assertTrue(unSelect.isEnabled());
        unSelect.actionPerformed(null);
        assertFalse(unSelect.isEnabled());
    }


    /**
     * Teste l'activation du bouton de désélection.
     */
    public void testMultipleSelectionRowAction() {
        Action action = logic.getSelectAction();

        // Selection des 3 premi?res lignes
        gui.getFromTable().setRowSelectionInterval(0, 2);
        action.actionPerformed(null);
        assertEquals(getToDS().getRowCount(), 3);

        Action unSelect = logic.getUnSelectAction();
        gui.getToTable().setRowSelectionInterval(0, 1);
        unSelect.actionPerformed(null);
        assertEquals(getToDS().getRowCount(), 1);
    }


    public void testRenderer() throws Exception {
        // Transfert une ligne
        final Row row = getFromDS().getRow(0);
        getToDS().addRow(row);

        // Le renderer sur 'from' met en évidence la ligne transférée
        Component result = applyRenderer(getFromDS().getRow(0));
        assertEquals(Color.lightGray, result.getBackground());
    }


    public void testInitGui() throws Exception {
        assertSame(logic.getSelectAction(), gui.addAction);
        assertSame(logic.getUnSelectAction(), gui.removeAction);
    }


    private Component applyRenderer(final Row row) {
        final TableCellRenderer defaultRenderer =
              gui.getFromTable().getDefaultRenderer(String.class);

        return defaultRenderer.getTableCellRendererComponent(gui.getFromTable(),
                                                             row.getField(0), false, false, 0, 0);
    }


    @Override
    protected void setUp() throws Exception {
        gui = new MockGui();
        from2To = new JoinKeys("fromId");
        filler = new MockRowFiller();
        logic = new SelectionLogic(gui, from2To, filler);
        logic.start();

        ListDataSource fromDS = gui.getFromTable().getDataSource();
        fromDS.addRow(newRow("fromId", "1"));
        fromDS.addRow(newRow("fromId", "2"));
        fromDS.addRow(newRow("fromId", "3"));
        fromDS.addRow(newRow("fromId", "4"));
        fromDS.addRow(newRow("fromId", "5"));
    }


    private Row newRow(String name, String value) {
        Row row = new Row();
        row.addField(name, value);
        return row;
    }


    private Row newRow(String name1, String value1, String name2, String value2) {
        Row row = new Row();
        row.addField(name1, value1);
        row.addField(name2, value2);
        return row;
    }


    private ListDataSource getFromDS() {
        return gui.getFromTable().getDataSource();
    }


    private ListDataSource getToDS() {
        return gui.getToTable().getDataSource();
    }


    private static class MockRowFiller implements RowFiller {
        private int fillAddedRowCalled = 0;
        private Row addedRow;


        public void fillAddedRow(Row row, int idx, ListDataSource lds) {
            fillAddedRowCalled++;
            addedRow = row;
        }
    }

    private static class MockGui extends SelectionGui {
        Action addAction;
        Action removeAction;


        MockGui() {
            super(null);
            Preference pref = new Preference();
            Column fromId = new Column("fromId", "fromId");
            pref.setColumns(Arrays.asList(fromId));
            getFromTable().setPreference(pref);
            getToTable().setPreference(pref);
        }


        @Override
        public void setSelectAction(Action addAction) {
            this.addAction = addAction;
        }


        @Override
        public void setUnSelectAction(Action removeAction) {
            this.removeAction = removeAction;
        }
    }
}
