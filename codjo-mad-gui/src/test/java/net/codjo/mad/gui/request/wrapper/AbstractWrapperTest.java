package net.codjo.mad.gui.request.wrapper;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;
import junit.framework.TestCase;
import org.uispec4j.UISpecTestCase;
/**
 * DOCUMENT ME!
 *
 * @author $Author: crego $
 * @version $Revision: 1.5 $
 */
public abstract class AbstractWrapperTest extends TestCase {
    protected AbstractWrapperTest() {}


    protected AbstractWrapperTest(String str) {
        super(str);
    }

    /**
     * Verifie que le wrapper lance un evenement lorsque l'on change la valeur
     * directement par le composant GUI.
     *
     * @throws Exception
     */
    public void test_event_triggered_by_gui() throws Exception {
        GuiWrapper wrapper = getWrapper();
        FakeChangeListener listener = new FakeChangeListener();
        wrapper.addPropertyChangeListener(listener);

        String initialValue = wrapper.getXmlValue();
        changeValueThroughGUI();
        String newValue = wrapper.getXmlValue();

        assertTrue(listener.hasBeenCalled());
        assertEquals(1, listener.nbOfCall);
        assertEquals(wrapper.getFieldName(), listener.evt.getPropertyName());
        assertEquals(initialValue, listener.evt.getOldValue());
        assertEquals(newValue, listener.evt.getNewValue());
    }


    /**
     * Verifie que le wrapper lance un evenement lorsque l'on change la valeur avec
     * <code>setXmlValue</code>.
     *
     * @throws Exception
     */
    public void test_event_triggered_through_wrapper()
            throws Exception {
        GuiWrapper wrapper = getWrapper();
        FakeChangeListener listener = new FakeChangeListener();
        wrapper.addPropertyChangeListener(listener);

        String initialValue = wrapper.getXmlValue();
        changeValueThroughWrapper();
        String newValue = wrapper.getXmlValue();

        assertTrue(listener.hasBeenCalled());
        assertEquals(1, listener.nbOfCall);
        assertEquals(wrapper.getFieldName(), listener.evt.getPropertyName());
        assertEquals(initialValue, listener.evt.getOldValue());
        assertEquals(newValue, listener.evt.getNewValue());
    }


    /**
     * Verifie que le wrapper gere correctement la valeur null.
     *
     * @throws Exception
     */
    public void test_wrapper_manage_null_value() throws Exception {
        GuiWrapper wrapper = getWrapper();

        wrapper.setXmlValue("null");

        assertEquals("null", wrapper.getXmlValue());
    }


    /**
     * Verifie que le wrapper gere la mécanique undo/redo.
     *
     * @throws Exception
     */
    public void test_undo_redo() throws Exception {
        GuiWrapper wrapper = getWrapper();
        FakeUndoListener listener = new FakeUndoListener();
        wrapper.addUndoableEditListener(listener);

        String initialValue = wrapper.getXmlValue();
        changeValueThroughWrapper();

        assertTrue(listener.hasBeenCalled());
        listener.undoEdit.undo();
        assertEquals(initialValue, wrapper.getXmlValue());
    }


    protected abstract GuiWrapper getWrapper();


    protected abstract void changeValueThroughGUI();


    protected abstract void changeValueThroughWrapper();

    public static class FakeChangeListener implements PropertyChangeListener {
        public int nbOfCall = 0;
        public PropertyChangeEvent evt;

        public boolean hasBeenCalled() {
            return nbOfCall > 0;
        }


        public void propertyChange(PropertyChangeEvent evt) {
            this.evt = evt;
            nbOfCall++;
        }
    }


    public class FakeUndoListener implements UndoableEditListener {
        public UndoManager undoMngr = new UndoManager();
        public UndoableEdit undoEdit;

        public boolean hasBeenCalled() {
            return undoEdit != null;
        }


        public void undoableEditHappened(UndoableEditEvent event) {
            undoEdit = event.getEdit();
            undoMngr.addEdit(undoEdit);
        }
    }
}
