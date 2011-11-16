package net.codjo.mad.gui.request;
import net.codjo.mad.gui.request.util.PreferenceRenderer;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
/**
 * 
 */
public class RequestCrossTableSum extends JTable {
    private static final Object COLUMN_WIDTH_PROPERTY = "width";
    private RequestCrossTablePanel crossTablePanel;
    private String[] columnNamesNotSummable;
    private ScrollBarSynchroniserListener scrollBarListener;
    private String precisionString = "##0.00";

    public RequestCrossTableSum() {}


    public RequestCrossTableSum(RequestCrossTablePanel panel, String[] columnNamesNotSummable) {
        this.columnNamesNotSummable = columnNamesNotSummable;
        initRequestCrossTablePanel(panel);
    }

    public void setDecimalFormat(String format) {
        precisionString = format;
    }


    public void setNotSummableColumns(String[] columnNamesNotSummable) {
        this.columnNamesNotSummable = columnNamesNotSummable;
    }


    public void refreshModel() {
        RequestTableSumModel requestTableSumModel = (RequestTableSumModel)getModel();
        requestTableSumModel.updateFromModel(crossTablePanel.getModel());
        initTableColumnModel();
        repaint();
    }


    public void initRequestCrossTablePanel(RequestCrossTablePanel panel) {
        this.crossTablePanel = panel;
        initializeTableSumModels();
        setCellRenderer(new DefaultSummableCellRenderer());
        if (getName() == null) {
            setName(crossTablePanel.getName() + ".Sum");
        }
    }

    public void initScrollPaneListener() {
        JScrollPane scrollPane = null;
        JScrollPane sumScrollPane = null;

        if (getParent() != null && getParent().getParent() instanceof JScrollPane) {
            sumScrollPane = (JScrollPane)getParent().getParent();
        }
        if (crossTablePanel.getTable().getParent() != null
                && crossTablePanel.getTable().getParent().getParent() instanceof JScrollPane) {
            scrollPane = (JScrollPane)crossTablePanel.getTable().getParent().getParent();
        }
        scrollBarListener = new RequestCrossTableSum.ScrollBarSynchroniserListener(scrollPane, sumScrollPane);

        addAncestorListener(new ScrollPanelListener());
        crossTablePanel.addAncestorListener(new ScrollPanelListener());
    }


    @Override
    public TableCellRenderer getCellRenderer(int row, int column) {
        return getDefaultRenderer(getColumnClass(column));
    }


    public void setCellRenderer(TableCellRenderer renderer) {
        setDefaultRenderer(BigDecimal.class, renderer);
    }


    @Override
    public Class<?> getColumnClass(int column) {
        return BigDecimal.class;
    }


    private void initializeTableSumModels() {
        setPreferredScrollableViewportSize(new Dimension(450, 20));

        RequestTableSumModel requestTableSumModel = new RequestTableSumModel();

        setModel(requestTableSumModel);
        setTableHeader(null);
        setEnabled(false);
        initTableColumnModel();
        initTableSumListeners();
    }


    private void initTableSumListeners() {
        crossTablePanel.getModel().addTableModelListener(new TableSumModelListener());

        crossTablePanel.getColumnModel().addColumnModelListener(new TableSumColumnModelListener());

        for (int indexColumn = 0; indexColumn < crossTablePanel.getColumnModel().getColumnCount();
                indexColumn++) {
            crossTablePanel.getColumnModel().getColumn(indexColumn).addPropertyChangeListener(new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent evt) {
                        if (evt.getPropertyName().equals(COLUMN_WIDTH_PROPERTY)) {
                            TableColumn column = (TableColumn)evt.getSource();
                            int newWidth = (Integer)evt.getNewValue();
                            int viewColumnIndex = convertColumnIndexToView(column.getModelIndex());
                            getColumnModel().getColumn(viewColumnIndex).setPreferredWidth(newWidth);
                        }
                    }
                });
        }
    }


    private void initTableColumnModel() {
        setColumnModel(new DefaultTableColumnModel());
        TableColumnModel tableColumnModel = crossTablePanel.getColumnModel();
        for (int index = 0; index < tableColumnModel.getColumnCount(); index++) {
            TableColumn tableColumn = tableColumnModel.getColumn(index);
            getColumnModel().addColumn(tableColumn);
        }
    }

    private class RequestTableSumModel extends AbstractTableModel {
        private Map<String, BigDecimal> summableColumns;

        RequestTableSumModel() {
            initSummableColumnList();
        }

        private boolean isSummable(String columnName) {
            int length = columnNamesNotSummable.length;

            for (int i = 0; i < length; ++i) {
                if (columnNamesNotSummable[i].equals(columnName)) {
                    return false;
                }
            }

            return true;
        }


        public void initSummableColumnList() {
            summableColumns = new HashMap<String, BigDecimal>();

            Enumeration<TableColumn> enumCol = crossTablePanel.getColumnModel().getColumns();
            for (; enumCol.hasMoreElements();) {
                TableColumn col = enumCol.nextElement();
                if (isSummable((String)col.getIdentifier())) {
                    summableColumns.put((String)col.getIdentifier(), new BigDecimal(0));
                    col.getHeaderValue();
                }
            }
        }


        public BigDecimal getValueAt(int row, int column) {
            String columnName = crossTablePanel.getModel().getColumnName(column);
            if (summableColumns.containsKey(columnName)) {
                return summableColumns.get(columnName);
            }
            return null;
        }


        public void updateFromModel(TableModel tableModel) {
            if (tableModel.getRowCount() > 0) {
                initSummableColumnList();
                for (int rowIndex = 0; rowIndex < tableModel.getRowCount(); rowIndex++) {
                    for (String columnName : summableColumns.keySet()) {
                        BigDecimal oldValue = summableColumns.get(columnName);
                        int columnViewIndex = crossTablePanel.convertFieldNameToModelIndex(columnName);
                        BigDecimal newValue;
                        Object fieldValue = tableModel.getValueAt(rowIndex, columnViewIndex);

                        if (fieldValue == null || "null".equals(fieldValue)) {
                            newValue = oldValue;
                        }
                        else {
                            newValue = oldValue.add(new BigDecimal(fieldValue.toString()));
                        }

                        summableColumns.put(columnName, newValue);
                    }
                }
            }
            else {
                for (String columnName : summableColumns.keySet()) {
                    summableColumns.put(columnName, null);
                }
            }
        }


        public int getColumnCount() {
            return crossTablePanel.getColumnModel().getColumnCount();
        }


        public int getRowCount() {
            return 1;
        }


        public Map getSummableColumns() {
            return summableColumns;
        }
    }


    private class DefaultSummableCellRenderer implements TableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
            boolean hasFocus, int row, int column) {
            BigDecimal sumValue = (BigDecimal)value;
            String sumToString = "";

            if (sumValue != null) {
                sumToString = sumValue.toString();
            }
            sumToString = PreferenceRenderer.stringToNumeric(sumToString, precisionString);

            DefaultTableCellRenderer tableCellRenderer = new DefaultTableCellRenderer();
            tableCellRenderer.setHorizontalAlignment(SwingConstants.RIGHT);

            JLabel component =
                (JLabel)tableCellRenderer.getTableCellRendererComponent(table, sumToString, isSelected,
                    hasFocus, row, column);

            Font font = component.getFont();
            component.setFont(font.deriveFont(Font.BOLD));

            return component;
        }
    }


    private class TableSumColumnModelListener implements TableColumnModelListener {
        public void columnMoved(TableColumnModelEvent evt) {
            int fromIndex = evt.getFromIndex();
            int toIndex = evt.getToIndex();
            if (fromIndex != toIndex) {
                getColumnModel().moveColumn(fromIndex, toIndex);
            }
        }


        public void columnMarginChanged(ChangeEvent evt) {}


        public void columnSelectionChanged(ListSelectionEvent evt) {}


        public void columnAdded(TableColumnModelEvent evt) {}


        public void columnRemoved(TableColumnModelEvent evt) {}
    }


    private class TableSumModelListener implements TableModelListener {
        public void tableChanged(TableModelEvent event) {
            refreshModel();
        }
    }


    private static class ScrollBarSynchroniserListener implements PropertyChangeListener {
        private JScrollPane scrollPane = null;
        private JScrollPane sumScrollPane = null;

        ScrollBarSynchroniserListener() {}


        ScrollBarSynchroniserListener(JScrollPane scrollPane, JScrollPane sumScrollPane) {
            setSumScrollPane(sumScrollPane);
            setScrollPane(scrollPane);
        }

        public void propertyChange(PropertyChangeEvent evt) {
            if (scrollPane != null && sumScrollPane != null) {
                if (scrollPane.getVerticalScrollBar().isVisible()) {
                    sumScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
                }
                else {
                    sumScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
                }
            }
        }


        private void setScrollPane(JScrollPane scrollPane) {
            if (this.scrollPane != scrollPane) {
                if (this.scrollPane != null) {
                    this.scrollPane.getAccessibleContext().removePropertyChangeListener(this);
                }
                this.scrollPane = scrollPane;
                if (this.scrollPane != null) {
                    this.scrollPane.getAccessibleContext().addPropertyChangeListener(this);
                }
            }
        }


        private void setSumScrollPane(JScrollPane sumScrollPane) {
            if (this.sumScrollPane != sumScrollPane) {
                this.sumScrollPane = sumScrollPane;
            }
        }
    }


    /**
     * Listener permettant de synchroniser les barres de défilement de la RequestTable et de sa
     * RequestTableSum.
     */
    private class ScrollPanelListener implements AncestorListener {
        public void ancestorAdded(AncestorEvent event) {
            if (event.getAncestor() == crossTablePanel.getTable()) {
                Container container = crossTablePanel.getTable().getParent().getParent();
                if (container instanceof JScrollPane) {
                    scrollBarListener.setScrollPane((JScrollPane)container);
                }
            }
            if (event.getAncestor() == RequestCrossTableSum.this) {
                Container container = RequestCrossTableSum.this.getParent().getParent();
                if (container instanceof JScrollPane) {
                    scrollBarListener.setSumScrollPane((JScrollPane)container);
                }
            }
        }


        public void ancestorRemoved(AncestorEvent event) {}


        public void ancestorMoved(AncestorEvent event) {}
    }
}
