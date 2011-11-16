package net.codjo.mad.gui.request;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
/**
 * Cette classe dispatche les <code>PropertyChangeEvent</code> d'un
 * <code>GuiWrapper</code> au travers du <code>DetailDataSource</code>.
 *
 * @version $Revision: 1.5 $
 */
class GuiWrapperChangeListener implements PropertyChangeListener {
    private AbstractDataSource dataSource;

    GuiWrapperChangeListener(AbstractDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void propertyChange(PropertyChangeEvent evt) {
        dataSource.getSelectedRow().setFieldValue(evt.getPropertyName(),
            (String)evt.getNewValue());
        dataSource.firePropertyChange(evt);
    }
}
