package net.codjo.mad.gui.request;
import net.codjo.gui.toolkit.util.TableUtil;
import net.codjo.mad.gui.request.util.PreferenceRenderer;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

public class RequestTableSum extends JTable {
    private RequestTable requestTable;


    public RequestTableSum() {
    }


    public RequestTableSum(RequestTable requestTable) {
        initRequestTable(requestTable);
    }


    public void initRequestTable(RequestTable table) {
        this.requestTable = table;
        init();
        setCellRenderer(new DefaultSummableCellRenderer());

        if (getName() == null) {
            setName(requestTable.getName() + ".Sum");
        }

        synchronizeScrollbars();
    }


    private void synchronizeScrollbars() {
        JViewport viewport = null;
        JViewport sumViewport = null;

        if ((getParent() != null) && (getParent() instanceof JViewport)) {
            sumViewport = (JViewport)getParent();
        }
        if ((requestTable.getParent() != null)
            && (requestTable.getParent() instanceof JViewport)) {
            viewport = (JViewport)requestTable.getParent();
        }

        if ((viewport != null) && (sumViewport != null)) {
            TableUtil.synchronizeHorizontalScrollbars(viewport, sumViewport);

            if (viewport.getParent() != null && viewport.getParent() instanceof JScrollPane
                && sumViewport.getParent() != null && sumViewport.getParent() instanceof JScrollPane) {
                synchronizeVerticalScrollBars((JScrollPane)viewport.getParent(),
                                              (JScrollPane)sumViewport.getParent());
            }
        }
    }


    private static void synchronizeVerticalScrollBars(final JScrollPane scrollPane,
                                                      final JScrollPane sumScrollPane) {
        final Border originalBorder = scrollPane.getBorder();

        scrollPane.getViewport().addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent event) {
                JScrollBar scrollBar = scrollPane.getVerticalScrollBar();
                if (scrollBar.isShowing()) {
                    sumScrollPane.setBorder(BorderFactory.createCompoundBorder(originalBorder,
                                                                               new EmptyBorder(0, 0, 0,
                                                                                               scrollBar.getWidth())));
                }
                else {
                    sumScrollPane.setBorder(originalBorder);
                }
            }
        });
    }


    @Override
    public TableCellRenderer getCellRenderer(int row, int column) {
        return getDefaultRenderer(getColumnClass(column));
    }


    public void setCellRenderer(final TableCellRenderer renderer) {
        setDefaultRenderer(Sum.class, renderer);
    }


    @Override
    public Object getValueAt(int row, int column) {
        return super.getValueAt(row, column);
    }


    @Override
    public Class getColumnClass(int column) {
        return Sum.class;
    }


    private void init() {
        setPreferredScrollableViewportSize(new Dimension(450, 20));
        final RequestTableSumModel requestTableSumModel = new RequestTableSumModel(
              requestTable.getPreference());
        setModel(requestTableSumModel);
        setTableHeader(null);
        setEnabled(false);
        initTableColumnModel();
        TableUtil.synchronizeTableColumns(requestTable, this);
    }


    private void initTableColumnModel() {
        requestTable.getModel().addTableModelListener(new TableSumModelListener());

        TableColumnModel tableColumnModel = requestTable.getColumnModel();
        for (int index = 0; index < tableColumnModel.getColumnCount(); index++) {
            TableColumn tableColumn = tableColumnModel.getColumn(index);
            TableColumn sumColumn = getColumnModel().getColumn(index);
            sumColumn.setHeaderValue(tableColumn.getHeaderValue());
            sumColumn.setPreferredWidth(tableColumn.getPreferredWidth());
        }
    }


    public abstract class AbstractCellContent<T> {
        private T value;


        protected AbstractCellContent() {

        }


        protected AbstractCellContent(T value) {
            this.value = value;
        }


        public T getValue() {
            return value;
        }


        public void setValue(T value) {
            this.value = value;
        }


        @Override
        public String toString() {
            return value.toString();
        }
    }

    public class Sum extends AbstractCellContent<BigDecimal> {
        public Sum(BigDecimal value) {
            super(value);
        }
    }

    public class Label extends AbstractCellContent<String> {

        public Label(String value) {
            super(value);
        }
    }

    public class Empty extends AbstractCellContent<Object> {

        public Empty() {
            super("null");
        }
    }

    private class RequestTableSumModel extends AbstractTableModel {
        private Map<String, BigDecimal> summableColumns;
        private Map<String, String> labeledColumns;
        private final Preference preference;


        RequestTableSumModel(Preference preference) {
            this.preference = preference;
            initSummableColumnList();
            initLabelColumnList();
        }


        public void initSummableColumnList() {
            summableColumns = new HashMap<String, BigDecimal>();
            for (Column column : preference.getColumns()) {
                if (column.isSummable()) {
                    summableColumns.put(column.getFieldName(), new BigDecimal(0));
                }
            }
        }


        public void initLabelColumnList() {
            labeledColumns = new HashMap<String, String>();
            for (Column column : preference.getColumns()) {
                String label = column.getSummableLabel();
                if (label != null) {
                    labeledColumns.put(column.getFieldName(), label);
                }
            }
        }


        public Object getValueAt(int row, int column) {
            String[] columnsName = preference.getColumnsName();
            String columnName = columnsName[column];
            if (summableColumns.containsKey(columnName)) {
                return new Sum(summableColumns.get(columnName));
            }
            else if (labeledColumns.containsKey(columnName)) {
                return new Label(labeledColumns.get(columnName));
            }
            return new Empty();
        }


        public void updateFromModel(TableModel tableModel) {
            synchronized (tableModel) {
                if (tableModel.getRowCount() > 0) {
                    initSummableColumnList();
                    for (int rowIndex = 0; rowIndex < tableModel.getRowCount(); rowIndex++) {
                        for (String columnName : summableColumns.keySet()) {
                            BigDecimal oldValue = summableColumns.get(columnName);
                            int columnViewIndex = requestTable.convertFieldNameToViewIndex(columnName);
                            int columnModelIndex = convertColumnIndexToModel(columnViewIndex);
                            BigDecimal newValue;
                            String fieldValue = tableModel.getValueAt(rowIndex, columnModelIndex).toString();
                            if ("null".equals(fieldValue)) {
                                newValue = oldValue;
                            } else {
                                newValue = oldValue.add(new BigDecimal(fieldValue));
                            }
                            summableColumns.put(columnName, newValue);
                        }
                    }
                } else {
                    summableColumns.clear();
                }
            }
            fireTableDataChanged();
        }


        public int getColumnCount() {
            return preference.getColumns().size();
        }


        public int getRowCount() {
            return 1;
        }


        public Map<String, BigDecimal> getSummableColumns() {
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

            String sumToString = "";
            if (value != null) {
                AbstractCellContent sum = (AbstractCellContent)value;

                Object sumValue = sum.getValue();

                if (sumValue != null) {
                    sumToString = sumValue.toString();
                }
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
            RequestTableSumModel requestTableSumModel = (RequestTableSumModel)getModel();
            requestTableSumModel.updateFromModel(requestTable.getModel());
            repaint();
        }
    }
}
