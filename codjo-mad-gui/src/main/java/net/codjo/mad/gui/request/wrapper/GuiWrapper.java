package net.codjo.mad.gui.request.wrapper;
import net.codjo.mad.gui.request.FieldType;
import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import javax.swing.event.UndoableEditListener;
/**
 * Encapsule un composant graphique pour pouvoir obtenir de manière uniforme sa représentation en String
 * (format XML).
 *
 * @author $Author: gaudefr $
 * @version $Revision: 1.4 $
 * @see net.codjo.mad.gui.request.DetailDataSource
 */
public interface GuiWrapper {
    public void addPropertyChangeListener(PropertyChangeListener listener);


    public void removePropertyChangeListener(PropertyChangeListener listener);


    public void addUndoableEditListener(UndoableEditListener listener);


    public void removeUndoableEditListener(UndoableEditListener listener);


    public String getFieldName();


    public Integer getUpdateOrder();


    public JComponent getGuiComponent();


    public FieldType getFieldType();


    public String getXmlValue();


    public String getDisplayValue();


    public void setXmlValue(String value);
}
