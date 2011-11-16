package net.codjo.mad.gui.request;
import net.codjo.mad.client.request.Row;
/**
 * Interface qui prend en charge la construction d'une Row pour un ajout dans une table
 * éditable.
 *
 * @author $Author: gaudefr $
 * @version $Revision: 1.3 $
 */
public interface RowFiller {
    /**
     * Remplit la nouvelle ligne <code>row</code> qui est inséré à la ligne
     * <code>idx</code> dans le datasource <code>lds</code>
     *
     * @param row la nouvelle ligne
     * @param idx idx de la nouvelle ligne
     * @param lds datasource.
     */
    public void fillAddedRow(Row row, int idx, ListDataSource lds);
}
