package net.codjo.mad.gui.i18n;
import net.codjo.i18n.common.Language;
import net.codjo.i18n.common.TranslationManager;
import net.codjo.i18n.gui.TranslationNotifier;
import net.codjo.mad.gui.request.Column;
import net.codjo.mad.gui.request.Preference;
import net.codjo.mad.gui.request.RequestTable;
import net.codjo.mad.gui.util.InternationalizableGuiContext;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.ListResourceBundle;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import junit.framework.TestCase;

public class InternationalizableRequestTableTest extends TestCase {
    private RequestTable requestTable;
    private TranslationNotifier translationNotifier;
    private InternationalizableRequestTable internationalizableTable;


    @Override
    protected void setUp() throws Exception {
        requestTable = new RequestTable();
        Preference preference = new Preference("myPreference");

        List<Column> columns = new ArrayList<Column>();
        columns.add(new Column("field1", "campo 1"));
        columns.add(new Column("field2", "campo 2"));
        columns.add(new Column("field3", "campo 3"));
        preference.setColumns(columns);

        requestTable.setPreference(preference);

        InternationalizableGuiContext guiContext = new InternationalizableGuiContext();
        TranslationManager translationManager =
              InternationalizationUtil.retrieveTranslationManager(guiContext);
        translationManager.addBundle(new MyFrenchResources(), Language.FR);
        translationManager.addBundle(new MyEnglishResources(), Language.EN);

        translationNotifier = InternationalizationUtil.retrieveTranslationNotifier(guiContext);

        internationalizableTable = new InternationalizableRequestTable(preference, requestTable);
    }


    public void test_nominal() throws Exception {
        assertHeader(new String[]{"campo 1", "campo 2", "campo 3"}, requestTable);

        translationNotifier.addInternationalizableComponent(internationalizableTable);
        assertHeader(new String[]{"mon champ1", "mon champ2", "mon champ3"}, requestTable);

        translationNotifier.setLanguage(Language.EN);
        assertHeader(new String[]{"my field1", "my field2", "my field3"}, requestTable);

        translationNotifier.setLanguage(Language.FR);
        assertHeader(new String[]{"mon champ1", "mon champ2", "mon champ3"}, requestTable);
    }


    public void test_moveColumn() {
        assertHeader(new String[]{"campo 1", "campo 2", "campo 3"}, requestTable);
        requestTable.moveColumn(1, 2);
        assertHeader(new String[]{"campo 1", "campo 3", "campo 2"}, requestTable);

        translationNotifier.addInternationalizableComponent(internationalizableTable);
        assertHeader(new String[]{"mon champ1", "mon champ3", "mon champ2"}, requestTable);

        translationNotifier.setLanguage(Language.EN);
        assertHeader(new String[]{"my field1", "my field3", "my field2"}, requestTable);

        requestTable.moveColumn(2, 0);
        assertHeader(new String[]{"my field2", "my field1", "my field3"}, requestTable);
    }


    private void assertHeader(String[] expectedHeader, JTable table) {
        assertEquals(expectedHeader.length, table.getColumnCount());
        Enumeration<TableColumn> columns = table.getTableHeader().getColumnModel().getColumns();
        while (columns.hasMoreElements()) {
            TableColumn tableColumn = columns.nextElement();
            int column = table.convertColumnIndexToView(tableColumn.getModelIndex());
            TableCellRenderer headerRenderer = tableColumn.getHeaderRenderer();
            JLabel headerLabel = (JLabel)headerRenderer.getTableCellRendererComponent(table,
                                                                                      tableColumn.getHeaderValue(),
                                                                                      false,
                                                                                      false,
                                                                                      0,
                                                                                      column);
            assertEquals(expectedHeader[column], headerLabel.getText());
        }
    }


    private static class MyFrenchResources extends ListResourceBundle {
        private static final Object[][] CONTENTS = new Object[][]{
              {"myPreference.field1", "mon champ1"},
              {"myPreference.field2", "mon champ2"},
              {"myPreference.field3", "mon champ3"},
        };


        @Override
        public Object[][] getContents() {
            return CONTENTS;
        }
    }

    private static class MyEnglishResources extends ListResourceBundle {
        private static final Object[][] CONTENTS = new Object[][]{
              {"myPreference.field1", "my field1"},
              {"myPreference.field2", "my field2"},
              {"myPreference.field3", "my field3"},
        };


        @Override
        public Object[][] getContents() {
            return CONTENTS;
        }
    }
}
