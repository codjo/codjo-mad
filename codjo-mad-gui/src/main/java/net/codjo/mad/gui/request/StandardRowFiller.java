package net.codjo.mad.gui.request;
import net.codjo.mad.client.request.Row;
/**
 *
 */
public class StandardRowFiller implements RowFiller {
    private Preference preference;


    public StandardRowFiller(Preference preference) {
        this.preference = preference;
    }


    public void fillAddedRow(Row row, int idx, ListDataSource to) {
        String[] rowFillerList = preference.getColumnsName();
        for (String fieldName : rowFillerList) {
            safeAdd(row, fieldName, row.getFieldValue(fieldName));
        }
    }


    private void safeAdd(Row row, String fieldName, String value) {
        if (!row.contains(fieldName)) {
            row.addField(fieldName, value);
        }
        else {
            row.setFieldValue(fieldName, value);
        }
    }
}
