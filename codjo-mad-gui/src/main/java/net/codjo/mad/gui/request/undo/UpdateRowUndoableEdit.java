package net.codjo.mad.gui.request.undo;
/**
 * DOCUMENT ME!
 *
 * @author $Author: gaudefr $
 * @version $Revision: 1.4 $
 */
public class UpdateRowUndoableEdit extends BasicUndoableEdit {
    private Object columnId;
    private Object newValue;
    private Object oldValue;

    public UpdateRowUndoableEdit(DataListUndoable lds, Object row, Object columnId,
        Object oldValue, Object newValue) {
        super(lds, row, -1);
        this.columnId = columnId;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public String getPresentationName() {
        return "Modification de " + columnId + " en " + newValue;
    }


    public void redo() {
        super.redo();
        getDataList().setValue(getRow(), columnId, newValue);
    }


    public void undo() {
        super.undo();
        getDataList().setValue(getRow(), columnId, oldValue);
    }
}
