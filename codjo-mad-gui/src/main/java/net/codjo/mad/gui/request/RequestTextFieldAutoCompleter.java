package net.codjo.mad.gui.request;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.text.JTextComponent;
import net.codjo.gui.toolkit.text.JTextFieldCodeLabelAutoCompleter;
import net.codjo.mad.client.request.FieldsList;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.client.request.Row;
/**
 *
 */
public class RequestTextFieldAutoCompleter extends JComponent {

    private JTextFieldCodeLabelAutoCompleter autoCompleter;
    private ListDataSource refDataSource;
    private String keyColName;
    private String labelColName;


    public RequestTextFieldAutoCompleter(JTextComponent textField,
                                         String handlerId,
                                         String keyColName,
                                         String labelColName) {
        this.keyColName = keyColName;
        this.labelColName = labelColName;
        autoCompleter = new JTextFieldCodeLabelAutoCompleter(textField, new LinkedHashMap<String, String>());
        refDataSource = new ListDataSource();
        refDataSource.setPageSize(100000);
        refDataSource.setLoadFactoryId(handlerId);
        refDataSource.setColumns(new String[]{keyColName, labelColName});
    }


    public RequestTextFieldAutoCompleter(JTextComponent textField,
                                         String handlerId,
                                         String keyColName,
                                         String labelColName,
                                         FieldsList selector) {
        this(textField, handlerId, keyColName, labelColName);
        refDataSource.setSelector(selector);
    }


    public void load() throws RequestException {
        refDataSource.load();
        Map<String, String> codeToLabel = new LinkedHashMap<String, String>();
        for (int i = 0; i < refDataSource.getTotalRowCount(); i++) {
            Row row = refDataSource.getRow(i);
            codeToLabel.put(row.getFieldValue(keyColName), row.getFieldValue(labelColName));
        }
        autoCompleter.updateAutoCompletedValues(codeToLabel);
    }


    public void setCode(String code) {
        autoCompleter.setCode(code);
    }


    public String getSelectedCode() {
        return autoCompleter.getSelectedCode();
    }


    public JTextComponent getTextComponent() {
        return autoCompleter.getTextComponent();
    }
}
