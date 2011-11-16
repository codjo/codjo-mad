package net.codjo.mad.gui.request.undo;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.UIManager;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;
/**
 * Manager Undo/Redo.
 *
 * <p> Utiliser la méthode getUndoableEditListener() pour récuperer le listener utilisé par ce manager. </p>
 *
 * @author $Author: gaudefr $
 * @version $Revision: 1.5 $
 */
public class FieldUndoManager {
    private FieldUndoableEditListener listener = new FieldUndoableEditListener();
    private RedoAction redoAction = new RedoAction();
    private InternalUndoManager undo = new InternalUndoManager();
    private UndoAction undoAction = new UndoAction();
    private boolean isRunningUndoOrRedo = false;


    public FieldUndoManager() {
        undo.setLimit(30);
    }


    public Action getRedoAction() {
        return redoAction;
    }


    public Action getUndoAction() {
        return undoAction;
    }


    public UndoableEditListener getUndoableEditListener() {
        return listener;
    }


    public void discardAllEdits() {
        undo.discardAllEdits();
        undoAction.updateUndoState();
        redoAction.updateRedoState();
    }


    public boolean isRunningUndoOrRedo() {
        return isRunningUndoOrRedo;
    }


    private class InternalUndoManager extends UndoManager {
        public UndoableEdit removeLastEdit() {
            int count = edits.size();
            if (count > 0) {
                return edits.remove(count - 1);
            }
            return null;
        }
    }

    private class FieldUndoableEditListener implements UndoableEditListener {
        public void undoableEditHappened(UndoableEditEvent event) {
            UndoableEdit undoableEdit = event.getEdit();
            if (undoableEdit instanceof SnapshotEdit) {
                final UndoableEdit lastEdit = undo.removeLastEdit();
                if (lastEdit == null) {
                    undo.addEdit(undoableEdit);
                    updateUndoRedoStates();
                }
                else {
                    CompoundEdit compoundEdit = createCompoundEdit(lastEdit);
                    compoundEdit.addEdit(lastEdit);
                    compoundEdit.addEdit(undoableEdit);
                    compoundEdit.end();
                    undo.addEdit(compoundEdit);
                    updateUndoRedoStates();
                }
            }
            else {
                undo.addEdit(undoableEdit);
                updateUndoRedoStates();
            }
        }


        private void updateUndoRedoStates() {
            undoAction.updateUndoState();
            redoAction.updateRedoState();
        }


        private CompoundEdit createCompoundEdit(final UndoableEdit lastEdit) {
            return new CompoundEdit() {
                public String getPresentationName() {
                    return lastEdit.getPresentationName();
                }


                public String getRedoPresentationName() {
                    return lastEdit.getRedoPresentationName();
                }


                public String getUndoPresentationName() {
                    return lastEdit.getUndoPresentationName();
                }
            };
        }
    }

    private class RedoAction extends AbstractAction {
        RedoAction() {
            putValue(SMALL_ICON, UIManager.getIcon("mad.redo"));
            setEnabled(false);
        }


        public void actionPerformed(java.awt.event.ActionEvent arg0) {
            isRunningUndoOrRedo = true;
            try {
                undo.redo();
            }
            catch (CannotRedoException ex) {
                ; //Echec
            }
            updateRedoState();
            undoAction.updateUndoState();
            isRunningUndoOrRedo = false;
        }


        public void updateRedoState() {
            setEnabled(undo.canRedo());
            putValue(SHORT_DESCRIPTION, undo.getRedoPresentationName());
        }
    }

    private class UndoAction extends AbstractAction {
        UndoAction() {
            putValue(SMALL_ICON, UIManager.getIcon("mad.undo"));
            setEnabled(false);
        }


        public void actionPerformed(java.awt.event.ActionEvent arg0) {
            isRunningUndoOrRedo = true;
            try {
                undo.undo();
            }
            catch (CannotUndoException ex) {
                ; // echec
            }
            updateUndoState();
            redoAction.updateRedoState();
            isRunningUndoOrRedo = false;
        }


        public void updateUndoState() {
            setEnabled(undo.canUndo());
            putValue(SHORT_DESCRIPTION, undo.getUndoPresentationName());
        }
    }
}
