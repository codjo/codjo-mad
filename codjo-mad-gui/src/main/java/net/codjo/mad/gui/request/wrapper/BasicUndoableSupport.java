package net.codjo.mad.gui.request.wrapper;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Hashtable;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.StateEdit;
import javax.swing.undo.StateEditable;
import javax.swing.undo.UndoableEditSupport;
import net.codjo.mad.gui.request.GuiWrapperUndoListener;
/**
 * Description of the Class
 *
 * @author $Author: gaudefr $
 * @version $Revision: 1.6 $
 */
public class BasicUndoableSupport {
    private WrapperListener listener = new WrapperListener();
    private UndoableEditSupport support = new UndoableEditSupport(this);
    private WrapperState wrapperState = new WrapperState();
    private GuiWrapper wrapper;

    public BasicUndoableSupport(GuiWrapper wrapper) {
        this.wrapper = wrapper;
        wrapper.addPropertyChangeListener(listener);
    }

    public void addUndoableEditListener(UndoableEditListener listenerParam) {
        support.addUndoableEditListener(listenerParam);
    }


    public void removeUndoableEditListener(UndoableEditListener listenerParam) {
        support.removeUndoableEditListener(listenerParam);
    }

    private class WrapperListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            StateEdit edit = buildUndoableEdit(evt);
            support.postEdit(edit);
        }


        private StateEdit buildUndoableEdit(final PropertyChangeEvent evt) {
            wrapperState.state = (String)evt.getOldValue();
            StateEdit edit =
                new StateEdit(wrapperState,
                    wrapper.getFieldName() + " = " + evt.getNewValue() + " (avant = "
                    + evt.getOldValue() + ")");
            wrapperState.state = (String)evt.getNewValue();
            edit.end();

            return edit;
        }
    }


    private class WrapperState implements StateEditable {
        private String state = null;

        public void restoreState(Hashtable st) {
            wrapper.removePropertyChangeListener(listener);
            UndoableEditListener[] editListeners = support.getUndoableEditListeners();
            for (UndoableEditListener editListener : editListeners) {
                if (editListener instanceof GuiWrapperUndoListener) {
                    ((GuiWrapperUndoListener)editListener).stopPosting();
                }
            }
            try {
                String value = (String)st.get("WRAPPER_STATE");
                wrapper.setXmlValue(value);
            }
            finally {
                wrapper.addPropertyChangeListener(listener);
                for (UndoableEditListener editListener : editListeners) {
                    if (editListener instanceof GuiWrapperUndoListener) {
                        ((GuiWrapperUndoListener)editListener).startPosting();
                    }
                }
            }
        }


        public void storeState(Hashtable st) {
            st.put("WRAPPER_STATE", state);
        }
    }
}
