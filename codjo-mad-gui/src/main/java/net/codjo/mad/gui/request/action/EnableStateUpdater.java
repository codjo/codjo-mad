package net.codjo.mad.gui.request.action;
import net.codjo.mad.gui.request.RequestTable;
import net.codjo.mad.gui.request.event.DataSourceAdapter;
import net.codjo.mad.gui.request.event.DataSourceEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Action;

public class EnableStateUpdater extends DataSourceAdapter implements PropertyChangeListener {
    private final Action action;
    private final RequestTable requestTable;
    private final boolean enabledWhenModified;


    public EnableStateUpdater(Action action, RequestTable requestTable, boolean enabledWhenModified) {
        this.action = action;
        this.requestTable = requestTable;
        this.enabledWhenModified = enabledWhenModified;
    }


    @Override
    public void loadEvent(DataSourceEvent event) {
        checkChange();
    }


    public void propertyChange(PropertyChangeEvent event) {
        checkChange();
    }


    @Override
    public void saveEvent(DataSourceEvent event) {
        checkChange();
    }


    private void checkChange() {
        if (requestTable.getDataSource().hasBeenUpdated()) {
            action.setEnabled(enabledWhenModified);
        }
        else {
            action.setEnabled(!enabledWhenModified);
        }
    }
}
