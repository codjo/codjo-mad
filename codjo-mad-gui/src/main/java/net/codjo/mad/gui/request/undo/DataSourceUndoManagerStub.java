package net.codjo.mad.gui.request.undo;
import net.codjo.mad.gui.request.DataSource;
import javax.swing.Action;

public class DataSourceUndoManagerStub implements DataSourceUndoManagerInterface {
    public void startListeningDataSource() {
    }


    public void stopListeningDataSource() {
    }


    public void discardAllEdits() {
    }


    public Action getRedoAction() {
        return null;
    }


    public Action getUndoAction() {
        return null;
    }


    public void setFather(DataSource father) {
    }


    public void addSon(DataSource son) {
    }


    public void startSnapshotMode() {
    }


    public void stopSnapshotMode() {
    }


    public boolean isRunningUndoOrRedo() {
        return false;
    }
}
