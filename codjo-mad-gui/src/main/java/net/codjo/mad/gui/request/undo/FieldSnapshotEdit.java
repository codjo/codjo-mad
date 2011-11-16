package net.codjo.mad.gui.request.undo;
import net.codjo.mad.gui.request.wrapper.FieldSetter;
import java.util.List;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
/**
 *
 */
public class FieldSnapshotEdit extends AbstractUndoableEdit implements SnapshotEdit {
    private List<FieldSetter> undoSetters;
    private List<FieldSetter> redoSetters;


    public FieldSnapshotEdit(List<FieldSetter> undoSetters, List<FieldSetter> redoSetters) {
        this.undoSetters = undoSetters;
        this.redoSetters = redoSetters;
    }


    @Override
    public void undo() throws CannotUndoException {
        super.undo();
        applySnapshot(undoSetters);
    }


    @Override
    public void redo() throws CannotRedoException {
        super.redo();
        applySnapshot(redoSetters);
    }


    private void applySnapshot(List<FieldSetter> setters) {
        for (FieldSetter setter : setters) {
            setter.run();
        }
    }
}
