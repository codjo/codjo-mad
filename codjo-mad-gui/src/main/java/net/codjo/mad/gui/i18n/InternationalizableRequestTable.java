package net.codjo.mad.gui.i18n;
import net.codjo.i18n.common.Language;
import net.codjo.i18n.common.TranslationManager;
import net.codjo.i18n.gui.AbstractInternationalizableComponent;
import net.codjo.mad.gui.request.Column;
import net.codjo.mad.gui.request.Preference;
import net.codjo.mad.gui.request.RequestTable;
import java.awt.Component;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

public class InternationalizableRequestTable extends AbstractInternationalizableComponent<RequestTable> {
    private Preference preference;
    private Reference<RequestTable> reference;


    public InternationalizableRequestTable(Preference preference, RequestTable table) {
        super(preference.getId());
        this.preference = preference;
        this.reference = new WeakReference<RequestTable>(table);
    }


    public void updateTranslation(Language language, TranslationManager translator) {
        RequestTable table = reference.get();
        if (table == null) {
            return;
        }

        setTranslationRenderers(table, language, translator);

        table.getTableHeader().repaint();
    }


    private void setTranslationRenderers(RequestTable table,
                                         Language language,
                                         TranslationManager translator) {
        List<Column> columns = preference.getColumns();
        for (Column column : columns) {
            String fieldName = column.getFieldName();
            int index = table.convertFieldNameToViewIndex(fieldName);
            TableColumn tableColumn = table.getTableHeader().getColumnModel().getColumn(index);
            TableCellRenderer headerRenderer = tableColumn.getHeaderRenderer();

            InternationalizationHeaderRenderer i18nRenderer;
            if (!InternationalizationHeaderRenderer.class.isInstance(headerRenderer)) {
                i18nRenderer = new InternationalizationHeaderRenderer(computeColumnKey(fieldName),
                                                                      headerRenderer,
                                                                      translator);
                tableColumn.setHeaderRenderer(i18nRenderer);
            }
            else {
                i18nRenderer = (InternationalizationHeaderRenderer)headerRenderer;
            }
            i18nRenderer.setLanguage(language);
        }
    }


    private String computeColumnKey(String columnName) {
        return preference.getId() + "." + columnName;
    }


    public RequestTable getComponent() {
        return reference.get();
    }


    private class InternationalizationHeaderRenderer extends DefaultTableCellRenderer {
        private String key;
        private TableCellRenderer headerRenderer;
        private TranslationManager translationManager;
        private Language language;


        private InternationalizationHeaderRenderer(String key,
                                                   TableCellRenderer headerRenderer,
                                                   TranslationManager manager) {
            this.key = key;
            this.headerRenderer = headerRenderer;
            this.translationManager = manager;
        }


        private void setLanguage(Language language) {
            this.language = language;
        }


        @Override
        public Component getTableCellRendererComponent(JTable table,
                                                       Object value,
                                                       boolean isSelected,
                                                       boolean hasFocus,
                                                       int row,
                                                       int column) {
            return headerRenderer.getTableCellRendererComponent(table,
                                                                translateLabel(),
                                                                isSelected,
                                                                hasFocus,
                                                                row,
                                                                column);
        }


        private String translateLabel() {
            return translationManager.translate(key, language);
        }
    }
}