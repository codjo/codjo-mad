package net.codjo.mad.gui.request;

import net.codjo.mad.client.request.Row;
import static net.codjo.mad.gui.request.CustomAutoCompleteComboDecorator.decorate;
import net.codjo.mad.gui.request.event.DataSourceAdapter;
import net.codjo.mad.gui.request.event.DataSourceEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.jdesktop.swingx.autocomplete.ObjectToStringConverter;

public class AutoCompleteRequestComboBox extends RequestComboBox {

    public AutoCompleteRequestComboBox() {
        decorate(this, getDataConverter());
    }


    public AutoCompleteRequestComboBox(boolean isEditable) {
        this.setEditable(isEditable);
        decorate(this, getDataConverter());
    }


    protected CustomBasicDataConverter getDataConverter() {
        return new CustomBasicDataConverter(this);
    }


    protected static class CustomBasicDataConverter extends ObjectToStringConverter {
        private Map<String, String> map = new HashMap<String, String>();
        private RequestComboBox comboBox;


        protected CustomBasicDataConverter(AutoCompleteRequestComboBox comboBox) {
            this.comboBox = comboBox;
            this.comboBox.getDataSource().addDataSourceListener(new DataSourceAdapter() {
                @Override
                public void beforeLoadEvent(DataSourceEvent event) {
                    map.clear();
                }
            });
        }


        @Override
        public String getPreferredStringForItem(Object object) {

            if (object == null) {
                return "";
            }
            if (map.isEmpty()) {
                loadMap();
            }

            String renderValue = comboBox.rendererValue(map.get(object.toString()));
            if (renderValue == null) {

                return "";
            }

            return renderValue;
        }


        private void loadMap() {
            final Iterator<Row> rowIterator = comboBox.getDataSource().rows();
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                map.put(row.getFieldValue(comboBox.getModelFieldName()),
                        row.getFieldValue(comboBox.getRendererFieldName()));
            }
        }
    }
}
