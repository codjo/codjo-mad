package net.codjo.mad.gui.request.undo;
/**
 * DOCUMENT ME!
 *
 * @author $Author: gaudefr $
 * @version $Revision: 1.4 $
 */
public class AddRowUndoableEdit extends BasicUndoableEdit {
    public AddRowUndoableEdit(DataListUndoable lds, Object row, int idx) {
        super(lds, row, idx);
    }

    public String getPresentationName() {
        return "Ajout d'une ligne";
    }


    public void redo() {
        super.redo();
        getDataList().addRow(getIdx(), getRow());
    }


    public void undo() {
        super.undo();
        getDataList().removeRow(getRow());
    }
}
