package net.codjo.mad.gui.structure;
import net.codjo.mad.common.structure.Structure;
import java.awt.Component;
import java.util.HashMap;
import java.util.Map;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
/**
 * Renderer pour une structure.
 *
 * @author $Author: gaudefr $
 * @version $Revision: 1.3 $
 */
public class StructureRenderer implements ListCellRenderer, TableCellRenderer {
    private DefaultListCellRenderer renderer = new DefaultListCellRenderer();
    private Map structures = new HashMap();
    private DefaultTableCellRenderer tableRenderer = new DefaultTableCellRenderer();

    public StructureRenderer(Map structures) {
        setStructures(structures);
    }


    public StructureRenderer() {}

    public void setStructures(Map structures) {
        this.structures = structures;
    }


    public Component getListCellRendererComponent(JList list, Object value, int index,
        boolean isSelected, boolean cellHasFocus) {
        return renderer.getListCellRendererComponent(list, transformValue(value), index,
            isSelected, cellHasFocus);
    }


    public Map getStructures() {
        return structures;
    }


    public Component getTableCellRendererComponent(JTable table, Object value,
        boolean isSelected, boolean hasFocus, int row, int column) {
        return tableRenderer.getTableCellRendererComponent(table, transformValue(value),
            isSelected, hasFocus, row, column);
    }


    protected String transformValue(Object value) {
        if (value == null) {
            return "";
        }
        if (structures.get(value) == null) {
            return value.toString();
        }
        return ((Structure)structures.get(value)).getLabel();
    }
}
