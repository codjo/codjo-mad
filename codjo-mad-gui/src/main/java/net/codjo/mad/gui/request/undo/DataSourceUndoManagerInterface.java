package net.codjo.mad.gui.request.undo;
import javax.swing.Action;
import net.codjo.mad.gui.request.DataSource;
/**
 *
 */
public interface DataSourceUndoManagerInterface {
    void startListeningDataSource();


    void stopListeningDataSource();


    void discardAllEdits();


    Action getRedoAction();


    Action getUndoAction();


    void setFather(DataSource father);


    void addSon(DataSource son);


    void startSnapshotMode();


    void stopSnapshotMode();


    boolean isRunningUndoOrRedo();
}
