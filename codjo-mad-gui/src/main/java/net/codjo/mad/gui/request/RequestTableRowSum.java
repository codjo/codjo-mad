package net.codjo.mad.gui.request;
import net.codjo.mad.gui.request.util.PreferenceRenderer;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
/**
 *
 */
public class RequestTableRowSum extends JTable {
    private RequestTable requestTable;

    public RequestTableRowSum() {
    }


    public RequestTableRowSum(RequestTable requestTable) {
        initRequestTable(requestTable);
    }


    public void initRequestTable(RequestTable table) {
        this.requestTable = table;
        init();
        setCellRenderer(new RequestTableRowSum.DefaultSummableCellRenderer());

        if (getName() == null) {
            setName(requestTable.getName() + ".RowSum");
        }

        initScrollPaneListener();
    }


    private void initScrollPaneListener() {
        addAncestorListener(new ScrollPanelListener());
        requestTable.addAncestorListener(new ScrollPanelListener());
    }


    @Override
    public TableCellRenderer getCellRenderer(int row, int column) {
        return getDefaultRenderer(getColumnClass(column));
    }


    public void setCellRenderer(final TableCellRenderer renderer) {
        setDefaultRenderer(RequestTableRowSum.Sum.class, renderer);
    }


    @Override
    public Object getValueAt(int row, int column) {
        BigDecimal modelValue = (BigDecimal)super.getValueAt(row, column);
        RequestTableRowSum.Sum sum = new RequestTableRowSum.Sum();
        sum.setValue(modelValue);
        return sum;
    }


    @Override
    public Class getColumnClass(int column) {
        return RequestTableRowSum.Sum.class;
    }


    private void init() {
        setPreferredScrollableViewportSize(new Dimension(100, requestTable.getHeight()));
        final RequestTableRowSum.RequestTableRowSumModel requestTableRowSumModel =
              new RequestTableRowSum.RequestTableRowSumModel(requestTable.getPreference());
        setModel(requestTableRowSumModel);
        setEnabled(false);
        initTableColumnModel();
        initTableSumListeners();
    }


    private void initTableSumListeners() {
        requestTable.getModel().addTableModelListener(new RequestTableRowSum.TableSumModelListener());
    }


    private void initTableColumnModel() {
        getColumnModel().getColumn(0).setHeaderValue(" ");
        getColumnModel().getColumn(0).setHeaderRenderer(new MyTitleRenderer());
    }


    public void initParentListeners() {
        JScrollPane sumScrollPane = (JScrollPane)getParent().getParent();
        JScrollPane scrollPane = (JScrollPane)requestTable.getParent().getParent();
        sumScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.getHorizontalScrollBar().addComponentListener(new MyComponentListener());
    }


    private class MyTitleRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table,
                                                       Object value,
                                                       boolean isSelected,
                                                       boolean hasFocus,
                                                       int row,
                                                       int column) {
            Component component = super
                  .getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            Font font = component.getFont();
            component.setFont(font.deriveFont(Font.BOLD));
            ((JLabel)component).setOpaque(false);
            ((JLabel)component).setHorizontalAlignment(SwingConstants.CENTER);
            ((JLabel)component).setHorizontalTextPosition(SwingConstants.RIGHT);
            ((JLabel)component).setVerticalTextPosition(SwingConstants.CENTER);
            ((JLabel)component)
                  .setBorder(new javax.swing.border.EtchedBorder(javax.swing.border.EtchedBorder.LOWERED));
            return component;
        }
    }
    public class Sum {
        private BigDecimal value;


        public BigDecimal getValue() {
            return value;
        }


        public void setValue(BigDecimal value) {
            this.value = value;
        }


        @Override
        public String toString() {
            return value.toString();
        }
    }

    private class RequestTableRowSumModel extends AbstractTableModel {
        private List<String> summableColumns;
        private List<BigDecimal> sumRows;
        private final Preference preference;


        RequestTableRowSumModel(Preference preference) {
            this.preference = preference;
            initSummableColumnList();
        }


        public void initSummableColumnList() {
            summableColumns = new ArrayList<String>();
            for (Object obj : preference.getColumns()) {
                Column column = (Column)obj;
                if (column.isSummable()) {
                    summableColumns.add(column.getFieldName());
                }
            }
        }


        public Object getValueAt(int row, int column) {
            return sumRows.get(row);
        }


        public void updateFromModel(TableModel tableModel) {
            BigDecimal newValue;

            sumRows = new ArrayList<BigDecimal>();
            if (tableModel.getRowCount() <= 0) {
                return;
            }

            initSummableColumnList();
            for (int j = 0; j < tableModel.getRowCount(); j++) {
                sumRows.add(null);
            }
            for (int rowIndex = 0; rowIndex < tableModel.getRowCount(); rowIndex++) {
                for (Object summableColumn : summableColumns) {
                    String columnName = (String)summableColumn;
                    BigDecimal oldValue = sumRows.get(rowIndex);
                    int columnViewIndex = requestTable.convertFieldNameToViewIndex(columnName);
                    int columnModelIndex = requestTable.convertColumnIndexToModel(columnViewIndex);
                    String fieldValue = tableModel.getValueAt(rowIndex, columnModelIndex).toString();
                    if ("null".equals(fieldValue)) {
                        newValue = oldValue;
                    }
                    else {
                        if (oldValue == null) {
                            oldValue = new BigDecimal(0);
                        }
                        newValue = oldValue.add(new BigDecimal(fieldValue));
                    }
                    sumRows.set(rowIndex, newValue);
                }
            }
        }


        public int getColumnCount() {
            return 1;
        }


        public int getRowCount() {
            return sumRows.size();
        }


        public List getSummableColumns() {
            return summableColumns;
        }
    }

    private class DefaultSummableCellRenderer extends PreferenceRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table,
                                                       Object value,
                                                       boolean isSelected,
                                                       boolean hasFocus,
                                                       int row,
                                                       int column) {
            RequestTableRowSum.Sum sum = (RequestTableRowSum.Sum)value;
            BigDecimal sumValue = sum.getValue();
            String sumToString = "";

            if (sumValue != null) {
                sumToString = sumValue.toString();
            }
            JLabel component =
                  (JLabel)super.getTableCellRendererComponent(requestTable, sumToString,
                                                              isSelected, hasFocus, row, column);

            Font font = component.getFont();
            component.setFont(font.deriveFont(Font.BOLD));

            return component;
        }
    }

    private class TableSumModelListener implements TableModelListener {
        public void tableChanged(TableModelEvent event) {
            RequestTableRowSum.RequestTableRowSumModel requestTableRowSumModel
                  = (RequestTableRowSum.RequestTableRowSumModel)getModel();
            requestTableRowSumModel.updateFromModel(requestTable.getModel());
            repaint();
        }
    }

    /**
     * Listener permettant de synchroniser les barres de défilement de la RequestTable et de sa
     * RequestTableSum.
     */
    public class ScrollPanelListener implements AncestorListener {
        public void ancestorAdded(AncestorEvent event) {
        }


        public void ancestorRemoved(AncestorEvent event) {

        }


        public void ancestorMoved(AncestorEvent event) {
            JScrollPane sumScrollPane = (JScrollPane)getParent().getParent();
            JScrollPane scrollPane = (JScrollPane)requestTable.getParent().getParent();
            sumScrollPane.getVerticalScrollBar().setModel(scrollPane.getVerticalScrollBar().getModel());
        }
    }
    private class MyComponentListener implements ComponentListener {

        public void componentResized(ComponentEvent event) {
        }


        public void componentMoved(ComponentEvent event) {
        }


        public void componentShown(ComponentEvent event) {
            JScrollPane sumScrollPane = (JScrollPane)getParent().getParent();
            JScrollPane scrollPane = ((JScrollPane)requestTable.getParent().getParent());
            if (scrollPane.getHorizontalScrollBar().isVisible()) {
                sumScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
            }
            else {
                sumScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            }
        }


        public void componentHidden(ComponentEvent event) {
            JScrollPane sumScrollPane = (JScrollPane)getParent().getParent();
            JScrollPane scrollPane = ((JScrollPane)requestTable.getParent().getParent());
            if (scrollPane.getHorizontalScrollBar().isVisible()) {
                sumScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
            }
            else {
                sumScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            }
        }
    }
}
