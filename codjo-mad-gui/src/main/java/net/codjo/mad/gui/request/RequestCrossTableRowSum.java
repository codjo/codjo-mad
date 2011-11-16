package net.codjo.mad.gui.request;
import net.codjo.mad.gui.request.util.PreferenceRenderer;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
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
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
/**
 *
 */
public class RequestCrossTableRowSum extends JTable {
    private RequestCrossTablePanel crossTablePanel;
    private String[] columnNamesNotSummable;
    private String precisionString = "##0.00";
    private JScrollPane rowSumScrollPane;
    private JScrollPane crossTableScrollPane;


    public RequestCrossTableRowSum() {
    }


    public RequestCrossTableRowSum(RequestCrossTablePanel panel, String[] columnNamesNotSummable) {
        this.columnNamesNotSummable = columnNamesNotSummable;
        initRequestCrossTablePanel(panel);
    }


    public void initRequestCrossTablePanel(RequestCrossTablePanel panel) {
        this.crossTablePanel = panel;

        initializeTableSumModels();
        setTableHeaderEmpty();
        setCellRenderer(new DefaultSummableCellRenderer());
        if (getName() == null) {
            setName(crossTablePanel.getName() + ".RowSum");
        }

        initVerticalScrollPaneListener();

        initCrossTableCellEditorListener();

        initCrossTableHeaderSortListener();
    }


    private void initCrossTableHeaderSortListener() {
        MouseAdapter listMouseListener =
              new MouseAdapter() {
                  @Override
                  public void mousePressed(MouseEvent event) {
                      if (event.getClickCount() > 1) {
                          refreshModel();
                      }
                  }
              };
        crossTablePanel.getTable().getTableHeader().addMouseListener(listMouseListener);
    }


    private void initCrossTableCellEditorListener() {
        crossTablePanel.getTable().addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if ("tableCellEditor".equals(evt.getPropertyName())) {
                    if ((evt.getOldValue() == null && evt.getNewValue() != null)
                        || (evt.getOldValue() != null && evt.getNewValue() != null && !evt.getOldValue()
                          .equals(evt.getNewValue()))
                        || (evt.getOldValue() != null && evt.getNewValue() == null)) {

                        refreshModel();
                    }
                }
            }
        });
    }


    public void refreshModel() {
        RequestTableRowSumModel requestTableRowSumModel = (RequestTableRowSumModel)getModel();
        requestTableRowSumModel.updateFromModel(crossTablePanel.getModel());
        setTableHeaderEmpty();
    }


    public void init() {
        rowSumScrollPane = (JScrollPane)getParent().getParent();
        crossTableScrollPane = (JScrollPane)crossTablePanel.getTable().getParent().getParent();
        rowSumScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        crossTableScrollPane.getHorizontalScrollBar().addComponentListener(new MyComponentListener());
    }


    public void initVerticalScrollPaneListener() {
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


    public void setDecimalFormat(String format) {
        precisionString = format;
    }


    public void setNotSummableColumns(String[] columnNamesNotSummable) {
        this.columnNamesNotSummable = columnNamesNotSummable;
    }


    private void initializeTableSumModels() {
        setPreferredScrollableViewportSize(new Dimension(450, 20));

        RequestTableRowSumModel requestTableRowSumModel = new RequestTableRowSumModel();

        setModel(requestTableRowSumModel);
        initTableSumListeners();
        setEnabled(false);
    }


    private void initTableSumListeners() {
        crossTablePanel.getColumnModel().addColumnModelListener(new TableSumColumnModelListener());
    }


    private void setTableHeaderEmpty() {
        getColumnModel().getColumn(0).setHeaderValue(" ");
    }


    private class RequestTableRowSumModel extends AbstractTableModel {
        private List<String> summableColumns = new ArrayList<String>();
        private List<BigDecimal> sumRows = new ArrayList<BigDecimal>();


        RequestTableRowSumModel() {
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
            summableColumns = new ArrayList<String>();

            Enumeration<TableColumn> enumCol = crossTablePanel.getColumnModel().getColumns();
            for (; enumCol.hasMoreElements();) {
                TableColumn col = enumCol.nextElement();
                if (isSummable((String)col.getIdentifier())) {
                    summableColumns.add((String)col.getIdentifier());
                    col.getHeaderValue();
                }
            }
        }


        public BigDecimal getValueAt(int row, int column) {
            return sumRows.get(row);
        }


        public void updateFromModel(TableModel tableModel) {
            BigDecimal newValue;

            sumRows = new ArrayList<BigDecimal>();
            if (tableModel.getRowCount() > 0) {
                initSummableColumnList();
                for (int j = 0; j < tableModel.getRowCount(); j++) {
                    sumRows.add(null);
                }
                for (int rowIndex = 0; rowIndex < tableModel.getRowCount(); rowIndex++) {
                    for (Object summableColumn : summableColumns) {
                        String columnName = (String)summableColumn;
                        BigDecimal oldValue = sumRows.get(rowIndex);
                        int columnIndex = crossTablePanel.getTable().getColumnModel()
                              .getColumnIndex(columnName);
                        int columnModelIndex = crossTablePanel.getTable()
                              .convertColumnIndexToModel(columnIndex);

                        String fieldValue = (String)tableModel.getValueAt(rowIndex, columnModelIndex);
                        BigDecimal newBigDecimalValue;
                        if (fieldValue == null || "null".equals(fieldValue) || "".equals(fieldValue)) {
                            newValue = oldValue;
                        }
                        else {
                            if (oldValue == null) {
                                oldValue = new BigDecimal(0);
                            }
                            try {
                                newBigDecimalValue = new BigDecimal(fieldValue);

                                newValue = oldValue.add(newBigDecimalValue);
                            }
                            catch (NumberFormatException ex) {
                                newValue = oldValue;
                            }
                        }
                        sumRows.set(rowIndex, newValue);
                    }
                }
            }
            fireTableDataChanged();
        }


        public int getColumnCount() {
            return 1;
        }


        public int getRowCount() {
            return sumRows.size();
        }


        public List<String> getSummableColumns() {
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

    /**
     * Listener permettant de synchroniser les barres de défilement de la RequestTable et de sa
     * RequestTableSum.
     */
    private class ScrollPanelListener implements AncestorListener {
        public void ancestorAdded(AncestorEvent event) {
        }


        public void ancestorRemoved(AncestorEvent event) {

        }


        public void ancestorMoved(AncestorEvent event) {
            if (rowSumScrollPane != null) {
                rowSumScrollPane.getVerticalScrollBar()
                      .setModel(crossTableScrollPane.getVerticalScrollBar().getModel());
            }
        }
    }

    private class MyComponentListener implements ComponentListener {

        public void componentResized(ComponentEvent event) {
        }


        public void componentMoved(ComponentEvent event) {
        }


        public void componentShown(ComponentEvent event) {
            JScrollPane sumScrollPane = (JScrollPane)getParent().getParent();
            JScrollPane scrollPane = ((JScrollPane)crossTablePanel.getTable().getParent().getParent());
            if (scrollPane.getHorizontalScrollBar().isVisible()) {
                sumScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
            }
            else {
                sumScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            }
        }


        public void componentHidden(ComponentEvent event) {
            JScrollPane sumScrollPane = (JScrollPane)getParent().getParent();
            JScrollPane scrollPane = ((JScrollPane)crossTablePanel.getTable().getParent().getParent());
            if (scrollPane.getHorizontalScrollBar().isVisible()) {
                sumScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
            }
            else {
                sumScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            }
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


        public void columnMarginChanged(ChangeEvent evt) {
        }


        public void columnSelectionChanged(ListSelectionEvent evt) {
        }


        public void columnAdded(TableColumnModelEvent evt) {
        }


        public void columnRemoved(TableColumnModelEvent evt) {
        }
    }
}
