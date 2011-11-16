package net.codjo.mad.gui.request;
import net.codjo.mad.client.request.FieldsList;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.gui.request.util.RequestComboBoxHelper;

public class RequestComboBoxFactory {

    private RequestComboBoxFactory() {
    }


    public static RequestComboBox createRequestCombobox(String handlerId,
                                                        String keyColName,
                                                        boolean containsNull)
          throws RequestException {

        return createRequestCombobox(handlerId, keyColName, null, containsNull);
    }


    public static RequestComboBox createRequestCombobox(String handlerId,
                                                        String keyColName,
                                                        String labelColName,
                                                        boolean containsNull)
          throws RequestException {

        RequestComboBox refComboBox = new RequestComboBox();

        ListDataSource refDataSource = RequestComboBoxHelper.prepareComboBox(handlerId,
                                                       keyColName,
                                                       labelColName,
                                                       containsNull,
                                                       refComboBox);
        return fillComboBox(refComboBox, refDataSource);
    }


    private static RequestComboBox fillComboBox(RequestComboBox refComboBox, ListDataSource refDataSource)
          throws RequestException {
        refDataSource.load();
        if (refDataSource.getRowCount() > 0) {
            refComboBox.setSelectedIndex(0);
        }
        return refComboBox;
    }


    public static RequestComboBox createRequestCombobox(String handlerId,
                                                        String keyColName,
                                                        String labelColName,
                                                        boolean containsNull,
                                                        FieldsList selector) throws RequestException {
        RequestComboBox refComboBox = new RequestComboBox();
        ListDataSource refDataSource = RequestComboBoxHelper.prepareComboBox(handlerId,
                                                       keyColName,
                                                       labelColName,
                                                       containsNull,
                                                       refComboBox);
        refDataSource.setSelector(selector);
        return fillComboBox(refComboBox, refDataSource);
    }
}
