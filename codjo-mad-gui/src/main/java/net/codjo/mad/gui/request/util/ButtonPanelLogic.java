package net.codjo.mad.gui.request.util;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JInternalFrame;
import net.codjo.i18n.gui.InternationalizableContainer;
import net.codjo.i18n.gui.TranslationNotifier;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.gui.request.DataSource;
import net.codjo.mad.gui.request.DetailDataSource;
import net.codjo.mad.gui.request.ErrorHandler;
import net.codjo.mad.gui.request.GuiLogic;
import net.codjo.mad.gui.request.ListDataSource;
import net.codjo.mad.gui.request.archive.ArchiveManager;
import net.codjo.mad.gui.request.archive.ArchiveManagerFactory;
import net.codjo.mad.gui.request.event.DataSourceAdapter;
import net.codjo.mad.gui.request.event.DataSourceEvent;
import net.codjo.mad.gui.request.undo.DataSourceUndoManager;
import net.codjo.mad.gui.request.undo.DataSourceUndoManagerInterface;
import net.codjo.mad.gui.request.undo.DataSourceUndoManagerStub;
/**
 * Logic du composant ButtonPanel.
 */
public class ButtonPanelLogic implements GuiLogic<ButtonPanelGui> {
    public static final String ARCHIVE_DATE_PROPERTY = "archive.date";
    public static final String SAVE_ERROR = "SAVE_ERROR";
    public static final String ARCHIVE_ERROR = "ARCHIVE_ERROR";
    private static final int CANCEL_ACTION = 1;
    private static final int SAVE_ACTION = 0;
    private ArchiveManagerFactory archiveManagerFactory;
    private DetailDataSource mainDataSource = null;
    private List<DataSource> subDataSourceList = new ArrayList<DataSource>();
    private final DataSourceUndoManagerInterface undoManager;
    private OkButtonUpdater okButtonUpdater = new OkButtonUpdater();
    private ArchiveButtonUpdater archiveButtonUpdater = new ArchiveButtonUpdater();
    private ButtonPanelGui gui;
    private ArchiveManager archiveManager = null;
    private Date archiveDate = null;
    private String archiveId;
    private boolean closeOnSave = true;
    private boolean closeOnCancel = true;
    private ErrorHandler errorHandler = new DefaultErrorHandler("");
    private PropertyChangeSupport propertySupport = new PropertyChangeSupport(this);
    private ErrorCheckerList errorCheckerList = new ErrorCheckerList();
    private boolean mustSave = true;
    protected ListDataSource listDataSource;
    private ButtonLogicValidator buttonLogicValidator = ButtonLogicValidator.ALWAYS_VALID;


    public ButtonPanelLogic() {
        this(new ButtonPanelGui(), true);
    }


    public ButtonPanelLogic(ButtonPanelGui customizedGui) {
        this(customizedGui, true);
    }


    public ButtonPanelLogic(ButtonPanelGui customizedGui, boolean enableUndoRedo) {
        this.gui = customizedGui;
        if (!enableUndoRedo) {
            undoManager = new DataSourceUndoManagerStub();
        }
        else {
            undoManager = new DataSourceUndoManager();
        }
        gui.getRedoButton().setAction(undoManager.getRedoAction());
        gui.getUndoButton().setAction(undoManager.getUndoAction());

        gui.getArchiveButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                setArchiveDate(getArchiveManager().askArchiveDate(gui.getArchiveButton()));
            }
        });
        gui.getCancelButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                executeCancel();
            }
        });
        gui.getOkButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                executeOk();
            }
        });
        gui.getWhatsNewButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                executeWhatsNewButton();
            }
        });
    }


    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(listener);
    }


    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.removePropertyChangeListener(listener);
    }


    public void setErrorHandler(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }


    public void setArchiveManager(ArchiveManager archiveManager) {
        this.archiveManager = archiveManager;
    }


    public ArchiveManager getArchiveManager() {
        if (archiveManager == null) {
            archiveManager = getArchiveManagerFactory().newArchiveManager(mainDataSource);
        }
        return archiveManager;
    }


    public ButtonPanelGui getGui() {
        return gui;
    }


    /**
     * Retourne <code>true</code>s'il y a quelque chose à sauvegarder, c'est à dire si au moins une des
     * <code>DataSource</code> a été modifiée.
     *
     * @return <code>true</code>s'il y a quelque chose à sauvegarder
     */
    public boolean canSave() {
        for (DataSource subDataSource : subDataSourceList) {
            if (subDataSource.hasBeenUpdated()) {
                return buttonLogicValidator.isValid();
            }
        }

        return mainDataSource.getSaveFactory() != null && mainDataSource.hasBeenUpdated()
               && buttonLogicValidator.isValid();
    }


    void cancelChanges() {
        if (mainDataSource != null) {
            mainDataSource.setLoadResult(mainDataSource.getLoadResult());
        }
    }


    protected void executeOk() {
        boolean doArchive = false;
        try {
            displayWaitCursor();
            if (isArchivable() && isUserRequestToArchive()) {
                doArchive = true;
                doArchive();
            }
            else {
                if (isMustSave()) {
                    doSave();
                }
            }
            if (!errorCheckerList.hasError()) {
                if (!isCloseOnSave()) {
                    doReload();
                }
                closeDetailWindow(SAVE_ACTION);
            }
            else {
                errorCheckerList.clearError();
            }
        }
        catch (RequestException ex) {
            handleError(doArchive, ex);
        }
        catch (RuntimeException ex) {
            handleError(doArchive, ex);
        }
        finally {
            displayDefaultCursor();
        }
    }


    protected void doReload() throws RequestException {
        mainDataSource.load();
    }


    private void handleError(boolean doArchive, Exception ex) {
        if (doArchive) {
            errorHandler.handleError(ARCHIVE_ERROR, ex);
        }
        else {
            errorHandler.handleError(SAVE_ERROR, ex);
        }
    }


    private void updateArchiveButtonState() {
        if (!isUserRequestToArchive() && isArchivable() && !canSave()) {
            gui.getArchiveButton().setEnabled(true);
        }
        else {
            gui.getArchiveButton().setEnabled(false);
        }
    }


    protected void executeCancel() {
        try {
            displayWaitCursor();
            if (getDetailWindow() != null) {
                closeDetailWindow(CANCEL_ACTION);
            }
            else {
                cancelChanges();
            }
        }
        finally {
            displayDefaultCursor();
        }
    }


    private void executeWhatsNewButton() {
        getArchiveManager().displayWhatsNewWindow(findDetailWindow(gui.getParent()));
    }


    public void doSave() throws RequestException {
        mainDataSource.save();
        // Les DataLink se chargent de sauver les fils

        if (listDataSource != null) {
            listDataSource.save();
        }
    }


    private void doArchive() throws RequestException {
        getArchiveManager().doArchive(archiveId, archiveDate);
    }


    public boolean hasArchiveManagerFactory() {
        return archiveManagerFactory != null;
    }


    private void setArchiveDate(final Date newArchiveDate) {
        Date oldDate = archiveDate;
        archiveDate = newArchiveDate;

        if (isUserRequestToArchive()) {
            getArchiveManager().updateDSWithArchiveDate(archiveDate);
            getArchiveManager().startArchive(newArchiveDate);
            updateArchiveButtonState();
            undoManager.discardAllEdits();
        }

        propertySupport.firePropertyChange(ARCHIVE_DATE_PROPERTY, oldDate, newArchiveDate);
    }


    private boolean isUserRequestToArchive() {
        return archiveDate != null;
    }


    private void setDataSource() {
        undoManager.setFather(mainDataSource);
        if (hasDataSource()) {
            removeListeners();
        }

        if (hasDataSource()) {
            gui.getCancelButton().setEnabled(true);
            addListeners();
        }

        archiveManager = null;
        updateArchiveButtonState();
        undoManager.discardAllEdits();
    }


    private boolean isArchivable() {
        return archiveId != null && hasDataSource() && hasLoadFactory() && hasArchiveManagerFactory();
    }


    protected void removeListeners() {
        removeListenersFrom(mainDataSource);
        mainDataSource.removeDataSourceListener(okButtonUpdater);

        for (DataSource subDataSource : subDataSourceList) {
            removeListenersFrom(subDataSource);
        }

        undoManager.stopListeningDataSource();
    }


    protected void addListeners() {
        addListenersTo(mainDataSource);
        mainDataSource.addDataSourceListener(okButtonUpdater);

        for (DataSource subDataSource : subDataSourceList) {
            addListenersTo(subDataSource);
        }

        undoManager.startListeningDataSource();
    }


    public void showUndoRedo(boolean show) {
        gui.getUndoButton().setVisible(show);
        gui.getRedoButton().setVisible(show);
    }


    protected void removeListenersFrom(DataSource datasource) {
        datasource.removePropertyChangeListener(okButtonUpdater);

        if (hasArchiveManagerFactory()) {
            datasource.removePropertyChangeListener(archiveButtonUpdater);
            datasource.removeDataSourceListener(archiveButtonUpdater);
        }
    }


    protected void addListenersTo(DataSource datasource) {
        datasource.addPropertyChangeListener(okButtonUpdater);

        if (hasArchiveManagerFactory()) {
            datasource.addPropertyChangeListener(archiveButtonUpdater);
            datasource.addDataSourceListener(archiveButtonUpdater);
        }
    }


    public DetailDataSource getMainDataSource() {
        if (listDataSource != null) {
            throw new RuntimeException(
                  "Vous ne pouvez pas utiliser getMainDataSource() si le ButtonPanelLogic gère un ListDataSource.");
        }
        return mainDataSource;
    }


    public void setMainDataSource(ListDataSource listDataSource) {
        this.listDataSource = listDataSource;
        DetailDataSource detail = new DetailDataSource(listDataSource.getGuiContext());
        setMainDataSource(detail);
        addSubDataSource(listDataSource);

        gui.setTranslationBackpack(listDataSource.getGuiContext());
    }


    public void setMainDataSource(DetailDataSource mainDataSource) {
        this.mainDataSource = mainDataSource;
        setDataSource();

        gui.setTranslationBackpack(mainDataSource.getGuiContext());
    }


    public List<DataSource> getSubDataSourceList() {
        return subDataSourceList;
    }


    private boolean hasDataSource() {
        return mainDataSource != null;
    }


    private boolean hasLoadFactory() {
        return mainDataSource.getLoadFactory() != null;
    }


    private ArchiveManagerFactory getArchiveManagerFactory() {
        if (archiveManagerFactory == null) {
            throw new IllegalStateException("Aucun Gestionnaire d'historisation "
                                            + "n'est configuré (cf.setArchiveManagerFactory())");
        }

        return archiveManagerFactory;
    }


    public void setArchiveManagerFactory(ArchiveManagerFactory factory) {
        archiveManagerFactory = factory;
    }


    public void setArchiveRequestId(String archiveId) {
        this.archiveId = archiveId;
        if (archiveId != null) {
            gui.addArchiveButton();
            gui.addWhatsNewButton();
        }
        else {
            gui.removeArchiveButton();
            gui.removeWhatsNewButton();
        }
        updateArchiveButtonState();
    }


    public void addSubDataSource(DataSource subDataSource) {
        subDataSourceList.add(subDataSource);
        subDataSource.addPropertyChangeListener(okButtonUpdater);

        if (hasArchiveManagerFactory()) {
            subDataSource.addPropertyChangeListener(archiveButtonUpdater);
            subDataSource.addDataSourceListener(archiveButtonUpdater);
        }
        undoManager.addSon(subDataSource);
    }


    public boolean isCloseOnSave() {
        return closeOnSave;
    }


    public void setCloseOnSave(boolean closeOnSave) {
        this.closeOnSave = closeOnSave;
    }


    public boolean isCloseOnCancel() {
        return closeOnCancel;
    }


    public void setCloseOnCancel(boolean closeOnCancel) {
        this.closeOnCancel = closeOnCancel;
    }


    public void addErrorChecker(ErrorChecker errorChecker) {
        errorCheckerList.add(errorChecker);
    }


    private JInternalFrame getDetailWindow() {
        return findDetailWindow(gui);
    }


    private JInternalFrame findDetailWindow(Component comp) {
        if (comp == null) {
            return null;
        }
        if (comp instanceof JInternalFrame) {
            return (JInternalFrame)comp;
        }
        return findDetailWindow(comp.getParent());
    }


    protected void closeDetailWindow(int action) {
        if (getDetailWindow() == null) {
            return;
        }
        if (action == SAVE_ACTION && !closeOnSave) {
            return;
        }
        if (action == CANCEL_ACTION && !closeOnCancel) {
            return;
        }

        try {
            getDetailWindow().setClosed(true);
        }
        catch (java.beans.PropertyVetoException e) {
            ; // Erreur lors de la fermeture de la window
        }
        getDetailWindow().setVisible(false);
        getDetailWindow().dispose();
    }


    public boolean isMustSave() {
        return mustSave;
    }


    public void setMustSave(boolean save) {
        mustSave = save;
    }


    public void startSnapshotMode() {
        if (mainDataSource == null) {
            return;
        }
        undoManager.stopListeningDataSource();
        undoManager.startSnapshotMode();
    }


    public void stopSnapshotMode() {
        if (mainDataSource == null) {
            return;
        }
        undoManager.stopSnapshotMode();
        undoManager.startListeningDataSource();
    }


    public boolean isRunningUndoOrRedo() {
        return undoManager.isRunningUndoOrRedo();
    }


    public void setButtonLogicValidator(ButtonLogicValidator buttonLogicValidator) {
        this.buttonLogicValidator = buttonLogicValidator;
    }


    public DataSourceUndoManagerInterface getUndoManager() {
        return undoManager;
    }


    private void displayWaitCursor() {
        JInternalFrame frame = getDetailWindow();
        if (frame != null) {
            frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        }
    }


    private void displayDefaultCursor() {
        JInternalFrame frame = getDetailWindow();
        if (frame != null) {
            frame.setCursor(Cursor.getDefaultCursor());
        }
    }


    private class OkButtonUpdater extends DataSourceAdapter implements PropertyChangeListener {
        @Override
        public void loadEvent(DataSourceEvent event) {
            checkChange();
        }


        public void propertyChange(PropertyChangeEvent event) {
            checkChange();
        }


        @Override
        public void saveEvent(DataSourceEvent event) {
            checkChange();
        }


        private void checkChange() {
            gui.getOkButton().setEnabled(canSave());
        }
    }

    private class ArchiveButtonUpdater extends DataSourceAdapter implements PropertyChangeListener {
        @Override
        public void loadEvent(DataSourceEvent event) {
            resetArchiveState();
        }


        public void propertyChange(PropertyChangeEvent event) {
            updateArchiveButtonState();
        }


        @Override
        public void saveEvent(DataSourceEvent event) {
            resetArchiveState();
        }


        private void resetArchiveState() {
            setArchiveDate(null);
            gui.getArchiveButton().setEnabled(isArchivable());
        }
    }
}
