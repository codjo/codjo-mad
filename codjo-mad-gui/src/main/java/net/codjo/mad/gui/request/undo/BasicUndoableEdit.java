package net.codjo.mad.gui.request.undo;
import javax.swing.undo.AbstractUndoableEdit;
/**
 * Description of the Class
 *
 * @author $Author: gaudefr $
 * @version $Revision: 1.5 $
 */
class BasicUndoableEdit extends AbstractUndoableEdit {
    private DataListUndoable dataList;
    private int idx;
    private Object row;

    BasicUndoableEdit(DataListUndoable lds, Object row, int idx) {
        this.row = row;
        this.dataList = lds;
        this.idx = idx;
    }

    public DataListUndoable getDataList() {
        return dataList;
    }


    public int getIdx() {
        return idx;
    }


    public Object getRow() {
        return row;
    }
}
