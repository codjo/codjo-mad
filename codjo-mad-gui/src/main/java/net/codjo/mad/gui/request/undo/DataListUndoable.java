package net.codjo.mad.gui.request.undo;
/**
 * Interface d'un objet contenant plusieurs ligne pouvant gérer l'annuler/refaire.
 *
 * @author $Author: gaudefr $
 * @version $Revision: 1.3 $
 */
public interface DataListUndoable {
    public void addRow(int idx, Object row);


    public void removeRow(Object row);


    public void setValue(Object row, Object columnId, Object value);
}
