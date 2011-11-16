package net.codjo.mad.gui.request.undo;
/**
 * DOCUMENT ME!
 *
 * @author $Author: gaudefr $
 * @version $Revision: 1.4 $
 */
public class RemoveRowUndoableEdit extends BasicUndoableEdit {
    public RemoveRowUndoableEdit(DataListUndoable lds, Object row, int idx) {
        super(lds, row, idx);
    }

    public String getPresentationName() {
        return "Suppression d'une ligne";
    }


    public void redo() {
        super.redo();
        getDataList().removeRow(getRow());
    }


    public void undo() {
        super.undo();
        getDataList().addRow(getIdx(), getRow());
    }
}
