package net.codjo.mad.gui.request.undo;
import javax.swing.undo.UndoableEdit;
import junit.framework.TestCase;
import org.easymock.MockControl;
/**
 *
 */
public class AddRowUndoableEditTest extends TestCase {
    private MockControl control;
    private UndoableEdit edit;
    private DataListUndoable mockDataList;
    private Object row;


    public AddRowUndoableEditTest(String str) {
        super(str);
    }


    public void test_undo() throws Exception {
        mockDataList.removeRow(row);
        control.setVoidCallable(1);
        control.replay();

        edit.undo();

        control.verify();
    }


    public void test_undo_redo() throws Exception {
        mockDataList.removeRow(row);
        control.setVoidCallable(1);

        mockDataList.addRow(50, row);
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
        edit = new AddRowUndoableEdit(mockDataList, row, 50);
    }
}
