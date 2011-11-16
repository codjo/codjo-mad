package net.codjo.mad.gui.request.util.renderers;
import net.codjo.mad.gui.request.Column;
import net.codjo.mad.gui.request.Preference;
import net.codjo.mad.gui.request.RequestTable;
import javax.swing.JLabel;
import junit.framework.TestCase;
/**
 *
 */
public class HideInfiniteDateRendererTest extends TestCase {
    public void test_getTableCellRendererComponent()
            throws Exception {
        Preference preference = new Preference();
        Column column = new Column("column1", "libellé1");
        column.setFormat("date(yyyy-MM-dd)");
        preference.getColumns().add(column);
        HideInfiniteDateRenderer dateRenderer = new HideInfiniteDateRenderer();
        RequestTable table = new RequestTable();
        table.setPreference(preference);
        assertEquals("xxx",
            ((JLabel)dateRenderer.getTableCellRendererComponent(table, "xxx", false,
                false, 0, 0)).getText());

        assertEquals("2005-12-31",
            ((JLabel)dateRenderer.getTableCellRendererComponent(table, "2005-12-31",
                false, false, 0, 0)).getText());
        assertEquals("",
            ((JLabel)dateRenderer.getTableCellRendererComponent(table, "9999-12-31",
                false, false, 0, 0)).getText());
    }
}
