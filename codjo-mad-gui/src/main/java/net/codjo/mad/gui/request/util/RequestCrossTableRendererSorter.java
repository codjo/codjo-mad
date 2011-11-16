package net.codjo.mad.gui.request.util;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
/**
 * 
 */
public class RequestCrossTableRendererSorter implements TableCellRenderer {
    private boolean ascending = true;
    private int columnSorted = -1;
    private JLabel renderer;
    private ImageIcon ascendingIcon;
    private ImageIcon descendingIcon;
    private final JTable table;

    public RequestCrossTableRendererSorter(JTable table) {
        this.table = table;
        initCustomHeaderRenderer();
        initColumnHeaderListener();
    }

    private void initCustomHeaderRenderer() {
        java.net.URL url;
        url = getClass().getResource("Ascending.gif");
        if (url != null) {
            ascendingIcon = new ImageIcon(Toolkit.getDefaultToolkit().createImage(url));
        }
        else {
            ascendingIcon = null;
        }

        url = getClass().getResource("Descending.gif");
        if (url != null) {
            descendingIcon = new ImageIcon(Toolkit.getDefaultToolkit().createImage(url));
        }
        else {
            descendingIcon = null;
        }

        renderer = new JLabel();
        renderer.setOpaque(true);
        renderer.setHorizontalAlignment(SwingConstants.CENTER);
        renderer.setHorizontalTextPosition(SwingConstants.RIGHT);
        renderer.setVerticalTextPosition(SwingConstants.CENTER);
        renderer.setBorder(new javax.swing.border.EtchedBorder(javax.swing.border.EtchedBorder.LOWERED));
    }


    private void initColumnHeaderListener() {
        MouseAdapter listMouseListener =
            new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent event) {
                    TableColumnModel columnModel = table.getColumnModel();
                    int viewColumn = columnModel.getColumnIndexAtX(event.getX());
                    int column = table.convertColumnIndexToModel(viewColumn);
                    if (event.getClickCount() > 1 && column != -1) {
                        ascending = !ascending;
                        DefaultTableModel model = (DefaultTableModel)table.getModel();
                        sortAllRowsBy(model, column);
                        changeHeaderRenderer(table, column);
                    }
                }
            };

        JTableHeader th = table.getTableHeader();
        th.addMouseListener(listMouseListener);
    }


    public Component getTableCellRendererComponent(JTable tbl, Object value, boolean selected,
        boolean hasFocus, int row, int col) {
        renderer.setText(value.toString());
        if (tbl.convertColumnIndexToModel(col) == columnSorted) {
            renderer.setIcon(ascending ? descendingIcon : ascendingIcon);
        }
        else {
            renderer.setIcon(null);
        }
        return renderer;
    }


    private void changeHeaderRenderer(JTable tableToChange, int column) {
        columnSorted = column;
        TableColumn tableColumn;
        int nbColumn = tableToChange.getColumnCount();
        for (int i = 0; i < nbColumn; i++) {
            tableColumn = tableToChange.getColumnModel().getColumn(i);
            tableColumn.setHeaderRenderer(this);
        }
    }


    private void sortAllRowsBy(DefaultTableModel model, int colIndex) {
        List data = model.getDataVector();
        Collections.sort(data, new ColumnSorter(colIndex, ascending));
        table.setModel(model);
    }

    private class ColumnSorter implements Comparator {
        int colIndex;
        boolean ascending;

        ColumnSorter(int colIndex, boolean ascending) {
            this.colIndex = colIndex;
            this.ascending = ascending;
        }

        public int compare(Object objectA, Object objectB) {
            List v1 = (Vector)objectA;
            List v2 = (Vector)objectB;
            Object o1 = v1.get(colIndex);
            Object o2 = v2.get(colIndex);

            if (o1 instanceof String && ((String)o1).length() == 0) {
                o1 = null;
            }
            if (o2 instanceof String && ((String)o2).length() == 0) {
                o2 = null;
            }

            if (o1 == null && o2 == null) {
                return 0;
            }
            else if (o1 == null) {
                return 1;
            }
            else if (o2 == null) {
                return -1;
            }
            else if (o1 instanceof Comparable) {
                if (ascending) {
                    return ((Comparable)o1).compareTo(o2);
                }
                else {
                    return ((Comparable)o2).compareTo(o1);
                }
            }
            else {
                if (ascending) {
                    return o1.toString().compareTo(o2.toString());
                }
                else {
                    return o2.toString().compareTo(o1.toString());
                }
            }
        }
    }
}
