package net.codjo.mad.gui.request.undo;
import net.codjo.mad.gui.request.DataSource;
import net.codjo.mad.gui.request.event.DataSourceAdapter;
import net.codjo.mad.gui.request.event.DataSourceEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;
/**
 * Gestionnaire de Undo/Redo pour un datasource.
 *
 * @version $Revision: 1.6 $
 */
public class DataSourceUndoManager implements DataSourceUndoManagerInterface {
    private UndoManagerUpdaterListener undoMgrUpdaterListener = new UndoManagerUpdaterListener();
    private FieldUndoManager undoManager = new FieldUndoManager();
    private DataSource father = null;
    private List<DataSource> sonList = new ArrayList<DataSource>();
    private boolean listening = false;


    public void startListeningDataSource() {
        this.father.removeDataSourceListener(undoMgrUpdaterListener);
        this.father.addDataSourceListener(undoMgrUpdaterListener);

        startFor(father);
        for (DataSource son : sonList) {
            startFor(son);
        }

        listening = true;
    }


    public void stopListeningDataSource() {
        this.father.removeDataSourceListener(undoMgrUpdaterListener);

        stopFor(father);
        for (DataSource son : sonList) {
            stopFor(son);
        }

        listening = false;
    }


    public void discardAllEdits() {
        undoManager.discardAllEdits();
    }


    public Action getRedoAction() {
        return undoManager.getRedoAction();
    }


    public Action getUndoAction() {
        return undoManager.getUndoAction();
    }


    public void setFather(DataSource father) {
        if (this.father != null) {
            stopListeningDataSource();
        }
        this.father = father;
        undoManager.discardAllEdits();
    }


    public void addSon(DataSource son) {
        sonList.add(son);

        if (listening) {
            startFor(son);
        }
    }


    private void startFor(DataSource dataSource) {
        stopFor(dataSource);
        dataSource.addUndoableEditListener(undoManager.getUndoableEditListener());
    }


    private void stopFor(DataSource dataSource) {
        dataSource.removeUndoableEditListener(undoManager.getUndoableEditListener());
    }


    public void startSnapshotMode() {
        startSnapshotModeFor(father);
        for (DataSource son : sonList) {
            startSnapshotModeFor(son);
        }
    }


    private void startSnapshotModeFor(DataSource dataSource) {
        dataSource.startSnapshotMode();
    }


    public void stopSnapshotMode() {
        stopSnapshotModeFor(father);
        for (DataSource son : sonList) {
            stopSnapshotModeFor(son);
        }
    }


    private void stopSnapshotModeFor(DataSource dataSource) {
        startListeningDataSource();
        dataSource.stopSnapshotMode();
        stopListeningDataSource();
    }


    public boolean isRunningUndoOrRedo() {
        return undoManager.isRunningUndoOrRedo();
    }


    private class UndoManagerUpdaterListener extends DataSourceAdapter {
        @Override
        public void loadEvent(DataSourceEvent event) {
            undoManager.discardAllEdits();
        }


        @Override
        public void saveEvent(DataSourceEvent event) {
            undoManager.discardAllEdits();
        }
    }
}
