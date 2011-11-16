package net.codjo.mad.gui.request;
import net.codjo.i18n.common.Language;
import net.codjo.i18n.common.TranslationManager;
import net.codjo.i18n.gui.Internationalizable;
import net.codjo.i18n.gui.TranslationNotifier;
import net.codjo.mad.gui.framework.GuiContext;
import net.codjo.mad.gui.i18n.InternationalizationUtil;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
/**
 * Associé à une request requestTable et un datasource afin d'aficher la position dans le jeu
 * d'enregistrement
 */
public class RequestRecordCountField extends JLabel implements Internationalizable {
    private RecordCountFieldUpdater recordCountFieldUpdater;
    private String format;
    private int totalRowCount;
    private int start;
    private int end;


    public RequestRecordCountField() {
        initGui();
    }


    public void initialize(final RequestTable requestTable, GuiContext guiContext) {
        TranslationNotifier notifier = InternationalizationUtil.retrieveTranslationNotifier(guiContext);
        TranslationManager manager = InternationalizationUtil.retrieveTranslationManager(guiContext);
        notifier.addInternationalizable(this);
        updateTranslation(notifier.getLanguage(), manager);

        computePagesPosition(requestTable);
        setName(requestTable.getName() + ".RequestRecordCountField");

        recordCountFieldUpdater = new RecordCountFieldUpdater(requestTable);
        requestTable.getModel().addTableModelListener(recordCountFieldUpdater);

        requestTable.addPropertyChangeListener("model",
                                               new PropertyChangeListener() {
                                                   public void propertyChange(PropertyChangeEvent evt) {
                                                       TableModel oldModel = (TableModel)evt.getOldValue();
                                                       TableModel newModel = (TableModel)evt.getNewValue();
                                                       oldModel.removeTableModelListener(
                                                             recordCountFieldUpdater);
                                                       newModel.addTableModelListener(recordCountFieldUpdater);
                                                       computePagesPosition(requestTable);
                                                   }
                                               });
    }


    private void initGui() {
        Dimension minimumSize = new Dimension(60, 15);
        this.setMinimumSize(minimumSize);
        this.setPreferredSize(minimumSize);
        this.setText("...");
        this.setBorder(BorderFactory.createEmptyBorder());
        this.setOpaque(false);
    }


    private synchronized void computePagesPosition(RequestTable requestTable) {
        ListDataSource listDataSource = requestTable.getDataSource();
        int currentPage = listDataSource.getCurrentPage();
        totalRowCount = listDataSource.getTotalRowCount();

        if (requestTable.getRowCount() == 0) {
            start = 0;
            end = 0;
        }
        else {
            int pageSize = listDataSource.getPageSize();
            start = (1 + (currentPage - 1) * pageSize);
            end = start + requestTable.getRowCount() - 1;
        }

        updateText();
    }


    private synchronized void updateText() {
        String text = String.format(format, start, end, totalRowCount);
        if (getText().length() != text.length()) {
            Dimension minimumSize = getMinimumSize();
            Dimension size = new Dimension(minimumSize);
            FontMetrics metrics = getFontMetrics(getFont());
            size.setSize(Math.max(metrics.stringWidth(text), minimumSize.getWidth()), size.getHeight());
            setPreferredSize(size);
        }
        setText(text);
    }


    public void updateTranslation(Language language, TranslationManager translator) {
        format = translator.translate(RequestRecordCountField.class.getName(), language);
        updateText();
    }


    private class RecordCountFieldUpdater implements TableModelListener {
        private final RequestTable requestTable;


        RecordCountFieldUpdater(RequestTable requestTable) {
            this.requestTable = requestTable;
        }


        public void tableChanged(TableModelEvent event) {
            computePagesPosition(requestTable);
        }
    }
}
