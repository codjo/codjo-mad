package net.codjo.mad.gui.request.util.selection;
import net.codjo.mad.client.request.Row;
import net.codjo.mad.gui.request.DataSource;
import net.codjo.mad.gui.request.JoinKeys;
import net.codjo.mad.gui.request.RequestTable;
import net.codjo.mad.gui.request.RowFiller;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
/**
 * Logique de la selection. Elle est composée de deux tables ("from" et "to") avec passage de lignes de l'une
 * vers l'autre.
 *
 * <p></p>
 *
 * @version $Revision: 1.6 $
 * @see SelectionGui
 */
public class SelectionLogic {
    private SelectionGui gui;
    private JoinKeys joinKeys;
    private RowFiller rowFiller;
    private SelectRowAction selectAction;
    private SelectActionUpdater selectActionUpdater = new SelectActionUpdater();
    private UnselectRowAction unselectAction;
    private PropertyChangeListener unSelectButtonUpdater = new UnSelectActionUpdater();


    /**
     * Constructeur.
     *
     * @param gui       Composant graphique de selection
     * @param joinKeys  Clef de rapprochement entre les données 'from' (father) et 'to' (son).
     * @param rowFiller Filler permettant de remplir les nouvelles lignes de 'to' à partir de la ligne
     *                  selectionné dans 'from'
     */
    public SelectionLogic(SelectionGui gui, JoinKeys joinKeys, RowFiller rowFiller) {
        this.gui = gui;
        this.joinKeys = joinKeys;
        this.rowFiller = rowFiller;
        selectAction = new SelectRowAction(gui.getFromTable(), gui.getToTable());
        unselectAction = new UnselectRowAction(gui.getToTable(), gui.getFromTable());
    }


    public void start() {
        gui.getToTable().getDataSource().setRowFiller(rowFiller);

        // Init listeners
        gui.getFromTable().getSelectionModel().addListSelectionListener(selectActionUpdater);
        gui.getToTable().getDataSource().addPropertyChangeListener(DataSource.SELECTED_ROW_PROPERTY,
                                                                   unSelectButtonUpdater);

        // Init actions
        gui.setSelectAction(getSelectAction());
        gui.setUnSelectAction(getUnSelectAction());

        initFromRenderer();
    }


    public SelectRowAction getSelectAction() {
        return selectAction;
    }


    public UnselectRowAction getUnSelectAction() {
        return unselectAction;
    }


    public PropertyChangeListener getUnSelectButtonUpdater() {
        return unSelectButtonUpdater;
    }


    public ListSelectionListener getSelectActionUpdater() {
        return selectActionUpdater;
    }


    protected boolean hasBeenTransfered(int rowIdx) {
        Row currentRow = (Row)gui.getFromTable().getValueAt(rowIdx, RequestTable.ROW_COLUMN);
        if (currentRow == null) {
            return false;
        }

        for (int index = 0; index < gui.getToTable().getRowCount(); index++) {
            Row row = (Row)gui.getToTable().getValueAt(index, RequestTable.ROW_COLUMN);

            if (isMatching(currentRow, row)) {
                return true;
            }
        }

        return false;
    }


    protected boolean isMatching(Row currentRow, Row row) {
        int nbTrue = 0;
        boolean found = false;

        for (Iterator aIter = joinKeys.iterator(); aIter.hasNext();) {
            JoinKeys.Association association = (JoinKeys.Association)aIter.next();

            String fromValue = currentRow.getFieldValue(association.getFatherField());
            String toValue = row.getFieldValue(association.getSonField());

            if (fromValue.equals(toValue)) {
                nbTrue++;
            }
            else {
                break;
            }
        }

        if (nbTrue == joinKeys.size()) {
            found = true;
        }
        return found;
    }


    private void initFromRenderer() {
        ListSelectionRenderer fromRenderer = new ListSelectionRenderer(this);
        if (gui.getFromTable().getDefaultRenderer(String.class) == null) {
            gui.getFromTable().setDefaultRenderer(String.class, fromRenderer);
        }
        else {
            ListSelectionRenderer renderer = new ListSelectionRenderer(this,
                                                                       gui.getFromTable().getDefaultRenderer(
                                                                             String.class));
            gui.getFromTable().setDefaultRenderer(String.class, renderer);
        }
    }


    private class SelectActionUpdater implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent event) {
            if (!event.getValueIsAdjusting()) {
                boolean isTransfered = selectedRowHasBeenTransfered();
                selectAction.setEnabled(!isTransfered);
            }
        }


        private boolean selectedRowHasBeenTransfered() {
            final int[] selectedRows = gui.getFromTable().getSelectedRows();
            for (int selectedRow : selectedRows) {
                if (hasBeenTransfered(selectedRow)) {
                    return true;
                }
            }
            return false;
        }
    }

    private class UnSelectActionUpdater implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent event) {
            if (event.getNewValue() == null) {
                unselectAction.setEnabled(false);
            }
            else {
                unselectAction.setEnabled(true);
            }
        }
    }
}
