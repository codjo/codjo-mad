package net.codjo.mad.gui.util;
import net.codjo.mad.client.request.Row;
import net.codjo.mad.gui.request.RequestTable;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

// Classe permettant de selectionner une ligne dans une RequestTable suite à une saisie clavier.
// Nb : pour que ce composant fonctionne, une ligne doit être sélectionnée dans la table

public class SelectRowKeyAdapter extends KeyAdapter {
    private String columnName;


    public SelectRowKeyAdapter(String columnName) {
        this.columnName = columnName;
    }


    @Override
    public void keyPressed(KeyEvent event) {
        RequestTable table = (RequestTable)event.getSource();
        Row selectedRow = (Row)table.getValueAt(table.getSelectedRow(), -1);
        for (int i = 0; i < table.getRowCount(); i++) {
            Row row = (Row)table.getValueAt(i, -1);
            String actionCode = row.getFieldValue(columnName);
            if (!actionCode.equals(selectedRow.getFieldValue(columnName)) && actionCode.toUpperCase()
                  .startsWith(String.valueOf(event.getKeyChar()).toUpperCase())) {
                table.getSelectionModel().setSelectionInterval(i, i);
                return;
            }
        }
    }
}
