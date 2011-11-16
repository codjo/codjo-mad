package net.codjo.mad.gui.request.action;
import net.codjo.i18n.common.TranslationManager;
import net.codjo.i18n.gui.TranslationNotifier;
import net.codjo.mad.gui.framework.GuiContext;
import net.codjo.mad.gui.i18n.InternationalizationUtil;
import net.codjo.mad.gui.request.DetailDataSource;
import net.codjo.mad.gui.request.RequestTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
/**
 * Action de modification d'une ligne via un ecran détail.
 */
public class EditAction extends AbstractWindowAction {
    private TranslationManager translationManager;
    private TranslationNotifier translationNotifier;


    /**
     * Use {@link #EditAction(GuiContext, RequestTable, DetailWindowBuilder)} instead
     *
     * @Deprecated Use {@link #EditAction(GuiContext, RequestTable, DetailWindowBuilder)} instead
     */
    @SuppressWarnings({"UnusedDeclaration"})
    @Deprecated
    public EditAction(GuiContext ctxt,
                      RequestTable table,
                      DetailWindowBuilder builder,
                      String name,
                      String description,
                      String iconId) {
        this(ctxt, table, builder);
    }


    public EditAction(GuiContext ctxt, RequestTable table, DetailWindowBuilder builder) {
        super(ctxt, table, builder, "Editer", "Editer l'enregistrement sélectionné", "mad.edit");
        translationManager = InternationalizationUtil.retrieveTranslationManager(ctxt);
        translationNotifier = InternationalizationUtil.retrieveTranslationNotifier(ctxt);

        setEnabled(false);
        getTable().getSelectionModel().addListSelectionListener(new TableSelectionListener());
    }


    @Override
    public void setEnabled(boolean enabled) {
        setDescriptions();
        super.setEnabled(enabled & isActivable());
    }


    protected void setDescriptions() {
        if (isVisualizeOnly()) {
            putValue(SHORT_DESCRIPTION,
                     translationManager.translate(EditAction.class.getName() + "#VisualizeOnly.tooltip",
                                                  translationNotifier.getLanguage()));
        }
        else {
            putValue(SHORT_DESCRIPTION,
                     translationManager.translate(EditAction.class.getName() + ".tooltip",
                                                  translationNotifier.getLanguage()));
        }
    }


    @Override
    protected String getSecurityFunction() {
        if ((getTable() == null)
            || (getPreference() == null)
            || (getPreference().getSelectByPk() == null)) {
            return super.getSecurityFunction();
        }
        return getPreference().getSelectByPk().getId();
    }


    @Override
    protected void modifiableTableAction() {
    }


    @Override
    protected DetailDataSource newDetailDataSource() {
        final DetailDataSource dataSource = getTable().getSelectedRowDataSource(getGuiContext());

        if (dataSource != null
            && dataSource.getSaveFactory() != null
            && !getGuiContext().getUser().isAllowedTo(dataSource.getSaveFactory().getId())) {
            dataSource.setSaveFactory(null);
        }

        return dataSource;
    }


    @Override
    protected boolean isActivable() {
        if (getTable().isEditable()) {
            return false;
        }
        ListSelectionModel lsm = getTable().getSelectionModel();
        return !lsm.isSelectionEmpty() && getTable().getSelectedRowCount() == 1
               && getPreference().getDetailWindowClass() != null
               && getPreference().getSelectByPk() != null;
    }


    protected boolean isVisualizeOnly() {
        return getPreference().getUpdate() == null;
    }


    private class TableSelectionListener implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent event) {
            if (event.getValueIsAdjusting()) {
                return;
            }
            setEnabled(true);
        }
    }
}
