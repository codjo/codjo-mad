package net.codjo.mad.gui.request;
import net.codjo.gui.toolkit.combo.ComboBoxPopupWidthMaximizer;
import net.codjo.gui.toolkit.combo.ComboBoxPopupWidthMaximizer.ItemSizeGetter;
import net.codjo.gui.toolkit.swing.ComboBoxModelSorter;
import net.codjo.mad.client.request.Request;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.client.request.Result;
import net.codjo.mad.client.request.Row;
import net.codjo.mad.gui.request.event.DataSourceAdapter;
import net.codjo.mad.gui.request.event.DataSourceEvent;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.MutableComboBoxModel;
import javax.swing.event.ListDataListener;
/**
 * DOCUMENT ME!
 *
 * @author $Author: gaudefr $
 * @version $Revision: 1.21 $
 */
public class RequestComboBox extends JComboBox {
    public static final String NULL = "null";
    public static final String NULL_LABEL = " ";
    private static final int NULL_INDEX = 0;
    private static final ComboBoxModel EMPTY_MODEL = new EmptyComboBoxModel();
    private boolean containsNullValue = false;
    private String nullLabel = NULL_LABEL;
    private ListDataSource dataSource = new ListDataSource();
    protected RequestComboModel model = new RequestComboModel();
    protected final ComboBoxModelSorter modelSorter;
    private DataSourceUpdateListener dataUpdateListener = new DataSourceUpdateListener();
    private SelectedRowUpdater selectedRowUpdater = new SelectedRowUpdater();
    protected String modelFieldName;
    protected String rendererFieldName;
    private Comparator<String> customComparator;


    public RequestComboBox() {
        modelSorter = new ComboBoxModelSorter(EMPTY_MODEL, new DefaultDataComparator());
        setModel(modelSorter);
        dataSource.setPageSize(1000);
        setDataSource(dataSource);
        addItemListener(selectedRowUpdater);

        ComboBoxPopupWidthMaximizer.install(this, new ItemSizeGetter() {
            public Dimension getPreferredSizeForItems() {
                return getPreferredSizeForContent();
            }
        });
    }


    public final void load() throws RequestException {
        if (needsLoad()) {
            prepareLoadRequest();
            getDataSource().load();
        }
        else {
            clearModel();
        }
    }


    String rendererValue(String value) {
        if (NULL.equals(value)) {
            return nullLabel;
        }
        else {
            return value;
        }
    }


    public void setColumns(String[] cols) {
        dataSource.setColumns(cols);
    }


    public void setContainsNullValue(boolean containsNullValue) {
        this.containsNullValue = containsNullValue;
    }


    public void setNullValueLabel(String nullValueLabel) {
        setContainsNullValue(true);
        this.nullLabel = nullValueLabel;
    }


    public void setDataSource(ListDataSource dataSource) {
        this.dataSource.removePropertyChangeListener(DataSource.SELECTED_ROW_PROPERTY,
                                                     selectedRowUpdater);
        dataUpdateListener.stopListening(this.dataSource);

        this.dataSource = dataSource;

        dataUpdateListener.startListening(this.dataSource);
        this.dataSource.addPropertyChangeListener(DataSource.SELECTED_ROW_PROPERTY,
                                                  selectedRowUpdater);

        model.fireAllContentsHasChangedEvent();
    }


    public void setModelFieldName(String modelFieldName) {
        this.modelFieldName = modelFieldName;

        if (modelFieldName != null) {
            modelSorter.setModel(model);
        }
        else {
            modelSorter.setModel(EMPTY_MODEL);
        }
    }


    public void setRendererFieldName(String rendererFieldName) {
        this.rendererFieldName = rendererFieldName;
        this.setRenderer(new ComboBoxRenderer());
        setKeySelectionManager(new RequestKeySelectionManager());
        model.fireAllContentsHasChangedEvent();
    }


    public void setSelectFactoryId(String selectFactoryId) {
        dataSource.setLoadFactoryId(selectFactoryId);
    }


    public String[] getColumns() {
        return dataSource.getColumns();
    }


    public boolean isContainsNullValue() {
        return containsNullValue;
    }


    public ListDataSource getDataSource() {
        return dataSource;
    }


    public RequestSubmiter getLoadSubmiter() {
        return new RequestSubmiter() {
            public void setResult(Result result) {
                getDataSource().setLoadResult(result);
            }


            public Request buildRequest() {
                return buildLoadRequest();
            }
        };
    }


    public boolean isLoaded() {
        return getDataSource().getLoadResult() != null;
    }


    public String getModelFieldName() {
        return modelFieldName;
    }


    public String getRendererFieldName() {
        return rendererFieldName;
    }


    public String getSelectFactoryId() {
        if (dataSource.getLoadFactory() == null) {
            return null;
        }
        return dataSource.getLoadFactory().getId();
    }


    public String getSelectedValue(String name) {
        int idx = getSelectedIndex();
        if (idx == -1) {
            return NULL;
        }
        return getValueAt(idx, name);
    }


    public String getSelectedValueToDisplay(String fieldName) {
        return rendererValue(getSelectedValue(fieldName));
    }


    public String getValueAt(int viewIndex, String name) {
        return getDataSource().getValueAt(viewIndexToDataSourceIndex(viewIndex), name);
    }


    public int dataSourceIndexToViewIndex(int dataSourceIndex) {
        return modelSorter.modelIndexToViewIndex(dataSourceIndex);
    }


    public int viewIndexToDataSourceIndex(int viewIndex) {
        return modelSorter.viewIndexToModelIndex(viewIndex);
    }


    public boolean isSortEnabled() {
        return modelSorter.isSortEnabled();
    }


    public void setSortEnabled(boolean isSorted) {
        modelSorter.setSortEnabled(isSorted);
    }


    public void setCustomComparator(Comparator<String> comparator) {
        customComparator = comparator;
    }


    public static RequestComboBox createRequestComboBox(String code, String label,
                                                        boolean containsNullValue) {
        final RequestComboBox comboBox = new RequestComboBox();
        comboBox.initRequestComboBox(code, label, containsNullValue);
        return comboBox;
    }


    public void initRequestComboBox(String code, String label, boolean containsNulls) {
        setModelFieldName(code);
        if (label != null) {
            setRendererFieldName(label);
        }
        setContainsNullValue(containsNulls);
    }


    public void initRequestComboBox(String code, String label) {
        initRequestComboBox(code, label, false);
    }


    public void initRequestComboBox(String code, String label, String nullValueLabel) {
        initRequestComboBox(code, label, true);
        setNullValueLabel(nullValueLabel);
    }


    public Dimension getPreferredSizeForContent() {
        FontMetrics fontMetrics = getFontMetrics(getFont());
        String fieldName = getValidFieldName();
        int widest = 0;
        for (int i = 0; i < getItemCount(); i++) {
            String itemValue = rendererValue(getValueAt(i, fieldName));
            if (widest < fontMetrics.stringWidth(itemValue)) {
                widest = fontMetrics.stringWidth(itemValue);
            }
        }
        return new Dimension(Math.max(widest, 70), getHeight());
    }


    protected void clearModel() {
        getDataSource().setLoadResult(new Result());
    }


    /**
     * Indique si la combo doit être chargée ou vidée.
     *
     * @return true si le load doit être effectué, si false le model est rempli avec un model vide.
     */
    protected boolean needsLoad() {
        return true;
    }


    protected void prepareLoadRequest() {
    }


    private void addNullRow() {
        Row nullRow = new Row();
        nullRow.addField(getModelFieldName(), NULL);

        if (getRendererFieldName() != null && !getRendererFieldName().equals(getModelFieldName())) {
            nullRow.addField(getRendererFieldName(), NULL);
        }

        if (getDataSource().getLoadResult().getRows() == null) {
            getDataSource().getLoadResult().setRows(new ArrayList<Row>());
        }

        Iterator<String> fieldNameIterator = determineFieldNameIterator();
        if (fieldNameIterator != null) {
            fillFromNames(nullRow, fieldNameIterator);
        }

        getDataSource().getLoadResult().getRows().add(NULL_INDEX, nullRow);
    }


    private String getValidFieldName() {
        return rendererFieldName == null ? modelFieldName : rendererFieldName;
    }


    private Iterator<String> determineFieldNameIterator() {
        Iterator<String> fieldNameIterator = null;
        if (getDataSource().getLoadResult().getRowCount() != 0) {
            Row oneRow = getDataSource().getLoadResult().getRows().get(0);
            fieldNameIterator = oneRow.fieldNames();
        }
        else if (getDataSource().getColumns() != null) {
            fieldNameIterator = Arrays.asList(getDataSource().getColumns()).iterator();
        }
        return fieldNameIterator;
    }


    private Request buildLoadRequest() {
        if (needsLoad()) {
            prepareLoadRequest();
            return getDataSource().buildLoadRequest();
        }
        else {
            return null;
        }
    }


    private void fillFromNames(Row nullRow, Iterator<String> fieldNames) {
        while (fieldNames.hasNext()) {
            String fieldName = fieldNames.next();
            if (!nullRow.contains(fieldName)) {
                nullRow.addField(fieldName, NULL);
            }
        }
    }


    class RequestKeySelectionManager implements KeySelectionManager, Serializable {
        public int selectionForKey(char aKey, ComboBoxModel aModel) {
            int it;
            int size;
            int currentSelection = -1;
            String elmt;
            String pattern;

            Object selectedItem = aModel.getSelectedItem();

            if (selectedItem != null) {
                for (it = 0, size = aModel.getSize(); it < size; it++) {
                    if (selectedItem == aModel.getElementAt(it)) {
                        currentSelection = it;
                        break;
                    }
                }
            }

            pattern = ("" + aKey).toLowerCase();
            aKey = pattern.charAt(0);

            for (it = ++currentSelection, size = aModel.getSize(); it < size; it++) {
                Object elem = getValueAt(it, getRendererFieldName());
                if (elem != null && elem.toString() != null) {
                    elmt = elem.toString().toLowerCase();
                    if (elmt.length() > 0 && elmt.charAt(0) == aKey) {
                        return it;
                    }
                }
            }

            for (it = 0; it < currentSelection; it++) {
                Object elem = getValueAt(it, getRendererFieldName());
                if (elem != null && elem.toString() != null) {
                    elmt = elem.toString().toLowerCase();
                    if (elmt.length() > 0 && elmt.charAt(0) == aKey) {
                        return it;
                    }
                }
            }
            return -1;
        }
    }

    private class DataSourceUpdateListener extends DataSourceAdapter implements PropertyChangeListener {

        @Override
        public void loadEvent(DataSourceEvent event) {
            notifyDataUpdate();
        }


        public void propertyChange(PropertyChangeEvent evt) {
            notifyDataUpdate();
        }


        private void notifyDataUpdate() {
            if (isContainsNullValue() && !modelContainsNull()) {
                addNullRow();
            }
            model.fireAllContentsHasChangedEvent();
        }


        private boolean modelContainsNull() {
            ListDataSource lds = getDataSource();
            if (lds.getLoadResult() == null) {
                return false;
            }

            Result loadresult = lds.getLoadResult();
            for (int i = 0; i < loadresult.getRowCount(); i++) {
                if (NULL.equals(loadresult.getRow(i).getFieldValue(getModelFieldName()))) {
                    return true;
                }
            }
            return false;
        }


        public void startListening(ListDataSource dataSource) {
            dataSource.addDataSourceListener(this);
            dataSource.addPropertyChangeListener(ListDataSource.CONTENT_PROPERTY, this);
        }


        public void stopListening(ListDataSource dataSource) {
            dataSource.removeDataSourceListener(this);
            dataSource.removePropertyChangeListener(this);
        }
    }

    protected class ComboBoxRenderer extends JLabel implements ListCellRenderer {
        ComboBoxRenderer() {
            setOpaque(true);
        }


        public String getElementAt(int viewIndex) {
            return rendererValue(getValueAt(viewIndex, getRendererFieldName()));
        }


        public Component getListCellRendererComponent(JList list, Object value,
                                                      int index, boolean isSelected, boolean cellHasFocus) {
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            }
            else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }

            if (index != -1) {
                this.setText(getElementAt(index));
            }
            else if (getSelectedIndex() != -1) {
                this.setText(getElementAt(getSelectedIndex()));
            }
            else {
                this.setText(nullLabel);
            }
            return this;
        }
    }

    protected class RequestComboModel extends AbstractListModel implements MutableComboBoxModel {
        private Object selectedObject;


        public void setSelectedItem(Object anObject) {
            if ((selectedObject != null && !selectedObject.equals(anObject))
                || selectedObject == null
                   && anObject != null) {
                selectedObject = anObject;
                fireContentsChanged(this, -1, -1);
            }
        }


        public Object getElementAt(int index) {
            return getDataSource().getValueAt(index, getModelFieldName());
        }


        public Object getSelectedItem() {
            return selectedObject;
        }


        public int getSize() {
            return getDataSource().getRowCount();
        }


        /**
         * Adds an item to the end of the model.
         *
         * @param obj the <code>Object</code> to be added
         */
        public void addElement(Object obj) {
            throw new IllegalArgumentException("impossible d'inserer dans le combo");
        }


        public void fireAllContentsHasChangedEvent() {
            fireContentsChanged(this, -1, -1);
        }


        /**
         * Adds an item at a specific index
         *
         * @param obj   the <code>Object</code> to be added
         * @param index location to add the object
         */
        public void insertElementAt(Object obj, int index) {
            throw new IllegalArgumentException("impossible d'inserer dans le combo");
        }


        /**
         * Removes an item from the model.
         *
         * @param obj the <code>Object</code> to be removed
         */
        public void removeElement(Object obj) {
            throw new IllegalArgumentException("impossible de supprimer dans le combo");
        }


        /**
         * Removes an item at a specific index
         *
         * @param index location of object to be removed
         */
        public void removeElementAt(int index) {
            getDataSource().removeRow(index);
        }
    }

    private class SelectedRowUpdater implements ItemListener, PropertyChangeListener {
        private boolean isUpdating = false;


        public void itemStateChanged(ItemEvent event) {
            if (isUpdating) {
                return;
            }
            isUpdating = true;
            try {
                if (getSelectedIndex() != -1) {
                    int dataSourceIndex = viewIndexToDataSourceIndex(getSelectedIndex());
                    dataSource.setSelectedRow(dataSource.getRow(dataSourceIndex));
                    setToolTipText(getSelectedValueToDisplay(getValidFieldName()));
                }
                else {
                    dataSource.setSelectedRow(null);
                    setToolTipText(null);
                }
            }
            finally {
                isUpdating = false;
            }
        }


        public void propertyChange(PropertyChangeEvent evt) {
            if (isUpdating) {
                return;
            }
            isUpdating = true;
            try {
                if (!isContainsNullValue() && dataSource.getSelectedRow() == null) {
                    setSelectedIndex(-1);
                    setToolTipText(null);
                }
                else if (isContainsNullValue() && dataSource.getSelectedRow() == null) {
                    if (dataSource.getRowCount() > 0) {
                        setSelectedIndex(NULL_INDEX);
                        setToolTipText(null);
                    }
                }
                else {
                    int dataSourceIndex = dataSource.getSelectedRowIndex();
                    try {
                        setSelectedIndex(dataSourceIndexToViewIndex(dataSourceIndex));
                        setToolTipText(getSelectedValueToDisplay(getValidFieldName()));
                    }
                    catch (IllegalArgumentException iae) {
                        model.fireAllContentsHasChangedEvent();
                        setSelectedIndex(dataSourceIndexToViewIndex(dataSourceIndex));
                        setToolTipText(getSelectedValueToDisplay(getValidFieldName()));
                    }
                }
            }
            finally {
                isUpdating = false;
            }
        }
    }

    private static class EmptyComboBoxModel implements ComboBoxModel {
        public void setSelectedItem(Object anItem) {
        }


        public Object getSelectedItem() {
            return null;
        }


        public int getSize() {
            return 0;
        }


        public Object getElementAt(int index) {
            return null;
        }


        public void addListDataListener(ListDataListener listener) {
        }


        public void removeListDataListener(ListDataListener listener) {
        }
    }

    private class DefaultDataComparator implements ComboBoxModelSorter.DataComparator {
        public int compare(ComboBoxModel subModel, int indexValue1, int indexValue2) {
            if (containsNullValue && indexValue1 == NULL_INDEX) {
                return -1;
            }
            if (containsNullValue && indexValue2 == NULL_INDEX) {
                return 1;
            }

            String fieldName = getModelFieldName();
            if (getRendererFieldName() != null) {
                fieldName = getRendererFieldName();
            }

            String value1 = getDataSource().getValueAt(indexValue1, fieldName);
            String value2 = getDataSource().getValueAt(indexValue2, fieldName);
            if (customComparator != null) {
                return customComparator.compare(value1, value2);
            }
            return value1.compareToIgnoreCase(value2);
        }
    }
}
