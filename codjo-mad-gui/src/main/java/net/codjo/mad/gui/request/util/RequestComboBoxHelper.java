package net.codjo.mad.gui.request.util;
import net.codjo.mad.gui.request.ListDataSource;
import net.codjo.mad.gui.request.RequestComboBox;

public class RequestComboBoxHelper {
    
    private RequestComboBoxHelper() {
    }


    public static ListDataSource prepareComboBox(String handlerId,
                                                  String keyColName,
                                                  String labelColName,
                                                  boolean containsNull, RequestComboBox refComboBox) {
        ListDataSource refDataSource = new ListDataSource();
        refDataSource.setPageSize(10000);
        refDataSource.setLoadFactoryId(handlerId);
        if (labelColName != null) {
            refDataSource.setColumns(new String[]{keyColName, labelColName});
        }
        else {
            labelColName = keyColName;
            refDataSource.setColumns(new String[]{keyColName});
        }
        refComboBox.setModelFieldName(keyColName);
        refComboBox.setRendererFieldName(labelColName);
        refComboBox.setContainsNullValue(containsNull);

        refComboBox.setDataSource(refDataSource);
        return refDataSource;
    }
}
