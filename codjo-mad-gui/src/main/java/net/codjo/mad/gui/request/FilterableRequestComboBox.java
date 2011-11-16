package net.codjo.mad.gui.request;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ListCellRenderer;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

public class FilterableRequestComboBox extends RequestComboBox {
    private DefaultComboBoxModel filteredModel = new FilteredComboBoxModel();
    private List<Integer> filteredIndexToInnerIndex = new ArrayList<Integer>();
    private StringBuilder filter = new StringBuilder();
    private boolean keepFilter;
    private ComboBoxModel currentModel = model;
    protected FilterableComboBoxRenderer filterableComboBoxRenderer = new FilterableComboBoxRenderer();
    private boolean isFiltering = false;


    public FilterableRequestComboBox() {
        addPopupMenuListener(new FilteredPopupMenuListener());
        addKeyListener(new FilterKeyListener());
    }


    @Override
    public String getRendererFieldName() {
        return super.getRendererFieldName() == null ? getModelFieldName() : super.getRendererFieldName();
    }


    protected void setInternalModel(ComboBoxModel model) {
        modelSorter.setModel(model);
        currentModel = model;
    }


    @Override
    public void setRenderer(ListCellRenderer aRenderer) {
        if (aRenderer.getClass().getSuperclass() != BasicComboBoxRenderer.class) {
            throw new RuntimeException("Impossible de changer de renderer!");
        }
    }


    @Override
    public void setRendererFieldName(String rendererFieldName) {
        this.rendererFieldName = rendererFieldName;
        super.setRenderer(filterableComboBoxRenderer);
        setKeySelectionManager(null);
        model.fireAllContentsHasChangedEvent();
    }


    private void emptyFilter() {
        if (filter.length() > 0) {
            filter.delete(0, filter.length());
        }
    }


    private void updateFilteredModel() {

        String selectedItem = (String)filteredModel.getSelectedItem();

        if (selectedItem != null && !isFiltering) {
            for (int index = 0; index < getDataSource().getRowCount(); index++) {
                String valueAt = getDataSource().getValueAt(index, getRendererFieldName());
                if (selectedItem.equals(valueAt)) {
                    return;
                }
            }
        }

        filteredModel.removeAllElements();
        filteredIndexToInnerIndex.clear();

        for (int index = 0; index < getDataSource().getRowCount(); index++) {
            String rendererValue = rendererValue(getDataSource().getValueAt(index, getRendererFieldName()));
            if (matchFilter(rendererValue)) {
                Object currentElement = getDataSource().getValueAt(index, getRendererFieldName());
                filteredModel.addElement(currentElement);
                filteredIndexToInnerIndex.add(index);
            }
        }

        if (modelMissMatch()) {
            setInternalModel(filteredModel);
        }
    }


    private boolean modelMissMatch() {
        return model.getSize() != filteredModel.getSize();
    }


    private boolean matchFilter(String element) {
        return filter.toString().length() == 0 || element.equals(NULL_LABEL) || element.toLowerCase()
              .startsWith(filter.toString().toLowerCase());
    }


    protected class FilterableComboBoxRenderer extends RequestComboBox.ComboBoxRenderer {
        @Override
        public String getElementAt(int viewIndex) {
            if (currentModel == filteredModel) {
                viewIndex = filteredIndexToInnerIndex.get(viewIndex);
                return rendererValue(getDataSource().getValueAt(viewIndex, getRendererFieldName()));
            }
            else {
                return super.getElementAt(viewIndex);
            }
        }
    }

    private class FilterKeyListener extends KeyAdapter {

        FilterKeyListener() {
        }


        @Override
        public void keyPressed(KeyEvent event) {
            if (Character.isLetterOrDigit(event.getKeyChar())
                || event.getKeyCode() == KeyEvent.VK_SPACE) {
                isFiltering = true;
                filter.append(event.getKeyChar());
                applyFilter();
            }
            else if (event.getKeyCode() == KeyEvent.VK_BACK_SPACE && filter.length() > 0) {
                isFiltering = true;
                filter.deleteCharAt(filter.length() - 1);
                applyFilter();
            }
            else if (event.getKeyCode() == KeyEvent.VK_ESCAPE) {
                isFiltering = false;
                emptyFilter();
            }
            else if (event.getKeyCode() == KeyEvent.VK_ENTER) {
                isFiltering = false;
                emptyFilter();
            }
        }


        private void applyFilter() {
            keepFilter = true;
            hidePopup();
            keepFilter = false;
            showPopup();
            revalidate();
        }
    }

    private class FilteredComboBoxModel extends DefaultComboBoxModel {

        @Override
        public void setSelectedItem(Object anObject) {
            super.setSelectedItem(anObject);
            model.setSelectedItem(anObject);
            isFiltering = false;
        }


        @Override
        public Object getElementAt(int index) {

            if (modelSorter.modelIndexToViewIndex(index) != index) {
                index = modelSorter.modelIndexToViewIndex(index);
            }
            if (index >= 0 || index < filteredIndexToInnerIndex.size()) {
                return getDataSource().getValueAt(filteredIndexToInnerIndex.get(index), getModelFieldName());
            }
            return null;
        }
    }

    private class FilteredPopupMenuListener implements PopupMenuListener {
        private boolean willBecomeVisible;


        public void popupMenuWillBecomeVisible(PopupMenuEvent event) {
            if (!willBecomeVisible) {
                updateFilteredModel();
                willBecomeVisible = true;
                try {
                    FilterableRequestComboBox.this.getUI()
                          .setPopupVisible(FilterableRequestComboBox.this, true);
                }
                finally {
                    willBecomeVisible = false;
                }
            }
        }


        public void popupMenuWillBecomeInvisible(PopupMenuEvent event) {
            if (!keepFilter) {
                if (getSelectedItem() == null) {
                    setSelectedIndex(isContainsNullValue() ? 0 : -1);
                }
                emptyFilter();
            }
            if (modelMissMatch()) {
                setInternalModel(model);
            }
        }


        public void popupMenuCanceled(PopupMenuEvent event) {

        }
    }
}