package net.codjo.mad.gui.request;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
/**
 * Created by IntelliJ IDEA. User: PALMONT Date: 15 avr. 2004 Time: 20:26:24 To change this template use File
 * | Settings | File Templates.
 */
public class GuiWrapperUndoListener implements UndoableEditListener {
    private DetailDataSource dataSource;
    private boolean posting = true;


    public GuiWrapperUndoListener(DetailDataSource dataSource) {
        this.dataSource = dataSource;
    }


    public void undoableEditHappened(UndoableEditEvent evt) {
        if (posting) {
            dataSource.postEdit(evt.getEdit());
        }
    }


    public void stopPosting() {
        posting = false;
    }


    public void startPosting() {
        posting = true;
    }
}
