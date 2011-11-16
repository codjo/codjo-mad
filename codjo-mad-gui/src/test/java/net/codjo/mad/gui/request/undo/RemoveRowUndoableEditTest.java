package net.codjo.mad.gui.request.undo;
import javax.swing.undo.UndoableEdit;
import junit.framework.TestCase;
import org.easymock.MockControl;
/**
 *
 */
public class RemoveRowUndoableEditTest extends TestCase {
    private MockControl control;
    private UndoableEdit edit;
    private DataListUndoable mockDataList;
    private Object row;


    public RemoveRowUndoableEditTest(String str) {
        super(str);
    }


    public void test_undo() throws Exception {
        mockDataList.addRow(50, row);
        control.setVoidCallable(1);
        control.replay();

        edit.undo();

        control.verify();
    }


    public void test_undo_redo() throws Exception {
        mockDataList.addRow(50, row);
        control.setVoidCallable(1);

        mockDataList.removeRow(row);
        control.setVoidCallable(1);

        control.replay();

        edit.undo();
        edit.redo();

        control.verify();
    }


    @Override
    protected void setUp() {
        control = MockControl.createControl(DataListUndoable.class);
        mockDataList = (DataListUndoable)control.getMock();
        row = new Object();
        edit = new RemoveRowUndoableEdit(mockDataList, row, 50);
    }
}
