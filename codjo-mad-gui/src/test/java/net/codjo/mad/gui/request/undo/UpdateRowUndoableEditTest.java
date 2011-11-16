package net.codjo.mad.gui.request.undo;
import javax.swing.undo.UndoableEdit;
import junit.framework.TestCase;
import org.easymock.MockControl;
/**
 *
 */
public class UpdateRowUndoableEditTest extends TestCase {
    private Object columnId;
    private MockControl control;
    private UndoableEdit edit;
    private DataListUndoable mockDataList;
    private Object newValue;
    private Object oldValue;
    private Object row;


    public UpdateRowUndoableEditTest(String str) {
        super(str);
    }


    public void test_undo() throws Exception {
        mockDataList.setValue(row, columnId, oldValue);
        control.setVoidCallable(1);
        control.replay();

        edit.undo();

        control.verify();
    }


    public void test_undo_redo() throws Exception {
        mockDataList.setValue(row, columnId, oldValue);
        control.setVoidCallable(1);

        mockDataList.setValue(row, columnId, newValue);
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
        columnId = "columnId";
        oldValue = "oldValue";
        newValue = "newValue";
        edit = new UpdateRowUndoableEdit(mockDataList, row, columnId, oldValue, newValue);
    }
}
