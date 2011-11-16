package net.codjo.mad.gui.request.util.editors;

import net.codjo.gui.toolkit.util.ErrorDialog;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.client.request.Result;
import net.codjo.mad.client.request.Row;
import net.codjo.mad.gui.request.ListDataSource;
import net.codjo.mad.gui.request.RequestComboBox;
import java.awt.Component;
import java.util.Collection;
import java.util.EventObject;
import java.util.LinkedList;
import java.util.List;
import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import org.apache.log4j.Logger;

/**
 * Cell editor (pour tables contennant RequestComboBox) permettant d'exclure de la liste des choix de la combo
 * les elements déjà presents dans la table.
 */
public class RemainingChoiceComboCellEditor extends DefaultCellEditor {
    private static final Logger LOG = Logger.getLogger(RemainingChoiceComboCellEditor.class.getName());
    private final RequestComboBox cellComboBox;
    private final ListDataSource alreadyChosenDataSource;
    private final String fieldName;
    private String refFieldName;
    private Result refResult = new Result();


    public RemainingChoiceComboCellEditor(RequestComboBox cellComboBox,
                                          ListDataSource alreadyChosenDataSource,
                                          String fieldName) {
        super(cellComboBox);
        this.cellComboBox = cellComboBox;
        this.alreadyChosenDataSource = alreadyChosenDataSource;
        this.fieldName = fieldName;
        this.refFieldName = fieldName;

        loadRefComboBox();
    }


    public RemainingChoiceComboCellEditor(RequestComboBox cellComboBox,
                                          ListDataSource alreadyChosenDataSource,
                                          String fieldName,
                                          String refFieldName) {
        this(cellComboBox, alreadyChosenDataSource, fieldName);
        this.refFieldName = refFieldName;
    }


    public ListDataSource getAlreadyChosenDataSource() {
        return alreadyChosenDataSource;
    }


    public String getFieldName() {
        return fieldName;
    }


    public RequestComboBox getCellComboBox() {
        return cellComboBox;
    }


    @Override
    public boolean isCellEditable(EventObject anEvent) {
        computeRemainingChoices("This cannot be a valid entry!");
        return cellComboBox.getDataSource().getRowCount() > 0;
    }


    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row, int column) {
        computeRemainingChoices((String)value);
        return super.getTableCellEditorComponent(table, value, isSelected, row, column);
    }


    protected void handleErrorOnLoad(RequestException exception) {
        ErrorDialog.show(null, exception.getLocalizedMessage(), exception);
    }


    private void refreshComboDataSource() {
        cellComboBox.getDataSource().setLoadResult(duplicateResult(refResult));
    }


    private void computeRemainingChoices(String selectedValue) {
        refreshComboDataSource();

        List<Row> tableRows = alreadyChosenDataSource.getLoadResult().getRows();
        List<Row> refRows = refResult.getRows();

        Collection<Row> rowsToRemove = new LinkedList<Row>();
        for (Row refRow : refRows) {
            if (selectedValue != null) {
                for (Row tableRow : tableRows) {
                    String tableCode = tableRow.getFieldValue(fieldName);
                    if (refRow.getFieldValue(refFieldName).equals(tableCode) &&
                        !selectedValue.equals(tableCode)) {
                        rowsToRemove.add(refRow);
                    }
                }
            }
        }

        for (Row rowToRemove : rowsToRemove) {
            cellComboBox.getDataSource().removeRow(rowToRemove);
        }
    }


    private void loadRefComboBox() {
        ListDataSource cellComboBoxDataSource = cellComboBox.getDataSource();
        try {
            cellComboBoxDataSource.load();
            refResult = duplicateResult(cellComboBoxDataSource.getLoadResult());
        }
        catch (RequestException exception) {
            LOG.error("Impossible de charger la comboBox.", exception);
            handleErrorOnLoad(exception);
        }
    }


    private Result duplicateResult(Result result) {
        Result newResult = new Result();
        for (int i = 0; i < result.getRowCount(); i++) {
            newResult.addRow(result.getRow(i));
        }
        return newResult;
    }
}
