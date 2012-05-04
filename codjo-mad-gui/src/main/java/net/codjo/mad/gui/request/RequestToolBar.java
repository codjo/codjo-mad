package net.codjo.mad.gui.request;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import net.codjo.i18n.gui.InternationalizableJLabel;
import net.codjo.i18n.gui.TranslationNotifier;
import net.codjo.mad.gui.framework.GuiContext;
import net.codjo.mad.gui.i18n.InternationalizationUtil;
import net.codjo.mad.gui.request.Position.Type;
import net.codjo.mad.gui.request.action.AddAction;
import net.codjo.mad.gui.request.action.DeleteAction;
import net.codjo.mad.gui.request.action.DetailWindowBuilder;
import net.codjo.mad.gui.request.action.EditAction;
import net.codjo.mad.gui.request.action.EnableStateUpdater;
import net.codjo.mad.gui.request.action.ExportAllPagesAction;
import net.codjo.mad.gui.request.action.FatherContainer;
import net.codjo.mad.gui.request.action.NextPageAction;
import net.codjo.mad.gui.request.action.PreviousPageAction;
import net.codjo.mad.gui.request.action.ReloadAction;
import net.codjo.mad.gui.request.action.SubmitAction;
import net.codjo.mad.gui.request.event.DataSourceEvent;
import net.codjo.mad.gui.request.event.DataSourceListener;
import net.codjo.mad.gui.request.requetor.ClearAction;
import net.codjo.mad.gui.request.requetor.FindAction;
import net.codjo.mad.gui.request.undo.DataSourceUndoManager;

public class RequestToolBar extends JToolBar implements FatherContainer {
    private static final String REQUETOR_EMPTY_REQUEST = "RequestToolBar.requetorEmptyRequest";
    private static final String REQUETOR_EMPTY_RESULT = "RequestToolBar.requetorEmptyResult";
    private static final String NAME_EXTENSION = ".ToolBar";

    public static final String ACTION_ADD = "AddAction";
    public static final String ACTION_EDIT = "EditAction";
    public static final String ACTION_RELOAD = "ReloadAction";
    public static final String ACTION_DELETE = "DeleteAction";
    public static final String ACTION_LOAD = "LoadAction";
    public static final String ACTION_FIND = ACTION_LOAD;
    public static final String ACTION_CLEAR = "ClearAction";
    public static final String ACTION_NEXT_PAGE = "NextPageAction";
    public static final String ACTION_PREVIOUS_PAGE = "PreviousPageAction";
    public static final String ACTION_EXPORT_ALL_PAGES = "ExportAllPagesAction";
    static final String ACTION_SAVE = "SaveAction";
    static final String ACTION_UNDO = "Undo";
    static final String ACTION_REDO = "Redo";

    private PopupHelper popupHelper = new PopupHelper(this);
    private Map<String, Action> allActions = new MyHashMap();
    private boolean hasValidationButton;
    private boolean hasExcelButton;
    private DataSourceUndoManager undoManager;
    private JPopupMenu popupMenu = new JPopupMenu();
    private RequestTable father;
    private DataSource fatherDS;
    private GuiContext guiContext;
    private RequestTable table;
    private JLabel requetorStatus = new JLabel(REQUETOR_EMPTY_REQUEST);
    private boolean hasNavigator = true;
    private boolean hasRecordCountField = true;
    private JPanel leftComponentPanel = new JPanel(new BorderLayout());
    private RequestRecordCountField requestRecordCountField = new RequestRecordCountField();
    private RequestRecordNavigator requestRecordNavigator = new RequestRecordNavigator();
    private TranslationNotifier translationNotifier;
    private InternationalizableJLabel i18nRequetorStatus;


    public RequestToolBar() {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        setFloatable(false);
        setName(NAME_EXTENSION);
    }


    public Component getLeftComponent() {
        final Component[] content = new Component[1];
        Runnable getter = new Runnable() {
            public void run() {
                if (0 != leftComponentPanel.getComponentCount()) {
                    content[0] = leftComponentPanel.getComponent(0);
                }
            }
        };
        if (SwingUtilities.isEventDispatchThread()) {
            getter.run();
        }
        else {
            try {
                SwingUtilities.invokeAndWait(getter);
            }
            catch (Exception exc) {
                return null;
            }
        }
        return content[0];
    }


    public void setLeftComponent(final Component comp) {
        Runnable setter = new Runnable() {
            public void run() {
                leftComponentPanel.removeAll();
                leftComponentPanel.add(comp);
                leftComponentPanel.invalidate();
            }
        };
        if (SwingUtilities.isEventDispatchThread()) {
            setter.run();
        }
        else {
            SwingUtilities.invokeLater(setter);
        }
    }


    public JPopupMenu getPopupMenu() {
        return popupMenu;
    }


    public static void doEffect(JToolBar toolBar) {
        for (Component component : toolBar.getComponents()) {
            if (component instanceof JButton) {
                configureButton((JButton)component);
            }
        }
        Dimension maximumSize = new Dimension(toolBar.getPreferredSize().width, 36);
        toolBar.setPreferredSize(maximumSize);
    }


    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        for (Object value : allActions.values()) {
            Action action = (Action)value;
            action.setEnabled(enabled);
        }
    }


    /**
     * Connecte cette toolbar à une table père.
     *
     * <p> Si aucune ligne n'est sélectionner dans la table père alors toutes les actions sont disabled, de plus les
     * écran détail ouvert doivent posséder deux arguments : (1) DetailDataSource et (2) une Row contenant les data
     * sélectionnées dans la table père. </p>
     *
     * @param father          la table père.
     * @param joinKey         La nouvelle valeur de father
     * @param selectHandlerId Identifiant du handler de select
     * @param guiContext      Contexte
     *
     * @deprecated utiliser la version {@link #setFather(DataSource, JoinKeys)}
     */
    @Deprecated
    public void setFather(RequestTable father,
                          String joinKey,
                          String selectHandlerId,
                          GuiContext guiContext) {
        setFather(father.getDataSource(), new JoinKeys(joinKey));
        this.father = father;
        this.requestRecordCountField.initialize(father, guiContext);
        this.requestRecordNavigator.initialize(father, guiContext);
    }


    /**
     * Connecte cette toolbar à une table père.
     *
     * <p> Si aucune ligne n'est sélectionner dans la table père alors toutes les actions sont disabled, de plus les
     * écran détail ouvert doivent posséder deux arguments : (1) DetailDataSource et (2) une Row contenant les data
     * sélectionnées dans la table père. </p>
     *
     * @param father   la table père.
     * @param joinKeys La clef de jointure
     */
    public void setFather(DataSource father, JoinKeys joinKeys) {
        setFatherLink(new DataLink(father, table.getDataSource(), joinKeys));
    }


    public void setFather(DataSource father, JoinKeys joinKeys, DataLink.Policy policy) {
        DataLink datalink = new DataLink(father, table.getDataSource(), joinKeys);
        datalink.setLoadPolicy(policy);
        setFatherLink(datalink);
    }


    /**
     * Connecte cette toolbar à une table père.
     *
     * <p> Si aucune ligne n'est sélectionner dans la table père alors toutes les actions sont disabled, de plus les
     * écran détail ouvert doivent posséder deux arguments : (1) DetailDataSource et (2) une Row contenant les data
     * sélectionnées dans la table père. </p>
     *
     * @param datalink Le lien entre le pere et le fils
     *
     * @throws IllegalArgumentException Argument invalide
     */
    public void setFatherLink(DataLink datalink) {
        if (datalink.getSon() != table.getDataSource() || this.fatherDS != null || this.table == null) {
            throw new IllegalArgumentException();
        }
        this.fatherDS = datalink.getFather();
        datalink.start();
        fatherDS.addPropertyChangeListener(DataSource.SELECTED_ROW_PROPERTY,
                                           new AutomaticUpdateState(datalink.getFather(), this));
    }


    public void setHasValidationButton(boolean hasValidationButton) {
        this.hasValidationButton = hasValidationButton;
    }


    public void setHasUndoRedoButtons(boolean hasUndoRedoButtons) {
        if (hasUndoRedoButtons) {
            this.undoManager = new DataSourceUndoManager();
        }
    }


    public void setHasExcelButton(boolean hasExcelButton) {
        this.hasExcelButton = hasExcelButton;
    }


    public void setHasNavigatorButton(boolean hasNavigator) {
        this.hasNavigator = hasNavigator;
    }


    public void setHasRecordCountField(boolean hasRecordCountField) {
        this.hasRecordCountField = hasRecordCountField;
    }


    private boolean hasUndoRedoButtons() {
        return this.undoManager != null;
    }


    /**
     * @deprecated utiliser {@link #getFatherDataSource()}
     */
    @Deprecated
    public RequestTable getFather() {
        return father;
    }


    public DataSource getFatherDataSource() {
        return fatherDS;
    }


    public void init(GuiContext guiCtxt, RequestTable requestTable) {
        if (requestTable == null) {
            throw new NullPointerException();
        }

        this.table = requestTable;
        this.guiContext = guiCtxt;
        if (table.getDataSource() != null && table.getDataSource().getGuiContext() == null) {
            table.getDataSource().setGuiContext(guiContext);
        }

        translationNotifier = InternationalizationUtil.retrieveTranslationNotifier(guiContext);

        setName(table.getName() + NAME_EXTENSION);

        removeAll();
        allActions.clear();
        popupMenu.removeAll();

        buildActions();
        buildGui();

        requestTable.removeMouseListener(popupHelper);
        requestTable.addMouseListener(popupHelper);
        requestRecordNavigator.initialize(requestTable, guiContext);
        requestRecordCountField.initialize(requestTable, guiContext);
        translationNotifier.addInternationalizable(requestRecordCountField);

        i18nRequetorStatus = new InternationalizableJLabel(REQUETOR_EMPTY_REQUEST, requetorStatus);
        translationNotifier.addInternationalizableComponent(i18nRequetorStatus);

        repaint();
    }


    /**
     * @see #getAction(String)
     * @deprecated
     */
    @Deprecated
    public Action getAction(Class actionClass) {
        return allActions.get(actionClass.getName());
    }


    public Action getAction(String actionName) {
        return allActions.get(actionName);
    }


    /**
     * @see #removeAction(String)
     * @deprecated
     */
    @Deprecated
    public void removeAction(Class actionClass) {
        removeAction(actionClass.getName());
    }


    public void removeAction(String actionName) {
        Action action = allActions.get(actionName);
        if (action == null) {
            throw new IllegalArgumentException("Action inconnue " + actionName);
        }
        removeActionFromToolbar(action);
        removeActionFromPopupMenu(action);
    }


    public void setDeleteConfirmMessage(String confirmMessage) {
        DeleteAction deleteAction = ((DeleteAction)getAction(ACTION_DELETE));
        if (deleteAction != null) {
            deleteAction.setConfirmMessage(confirmMessage);
        }
        else {
            throw new NullPointerException("L'action " + ACTION_DELETE + " n'existe pas.");
        }
    }


    public void setSqlRequetorMandatoryClause(String clause) {
        ((FindAction)allActions.get(ACTION_FIND)).setSqlRequetorMandatoryClause(clause);
    }


    public void setSqlRequetorOrderClause(String clause) {
        ((FindAction)allActions.get(ACTION_FIND)).setSqlRequetorOrderClause(clause);
    }


    public void removeActionFromPopupMenu(Action action) {
        for (int i = 0; i < popupMenu.getComponentCount(); i++) {
            if (popupMenu.getComponent(i) instanceof JMenuItem
                && ((JMenuItem)popupMenu.getComponent(i)).getAction() == action) {
                popupMenu.remove(i);
                return;
            }
        }
    }


    public void removeActionFromToolbar(Action action) {
        Component[] components = getComponents();
        for (int i = 0; i < components.length; i++) {
            Component component = components[i];
            if (component instanceof JButton
                && ((JButton)component).getAction() == action) {
                remove(i);
                return;
            }
        }
    }


    public void replace(String actionName, Action newAction) {
        Action oldAction = getAction(actionName);
        if (oldAction == null) {
            throw new IllegalArgumentException("L'action " + actionName + " n'existe pas.");
        }

        if (oldAction.getClass().equals(newAction.getClass())) {
            return;
        }

        JButton button = getButtonInToolBar(oldAction);
        JMenuItem menuItem = getMenuItemInPopUpMenu(oldAction);

        registerAction(actionName, newAction);

        if (button != null) {
            button.setAction(newAction);
            button.setText(null);
            translationNotifier.addInternationalizableComponent(button,
                                                                null,
                                                                oldAction.getClass().getName() + ".tooltip");
            button.setName(table.getName() + ".repB." + newAction.getClass().getName());
        }
        if (menuItem != null) {
            menuItem.setAction(newAction);
            menuItem.setName(table.getName() + ".repM." + newAction.getClass().getName());
        }
    }


    /**
     * @see #add(javax.swing.Action, String, Position, boolean)
     * @deprecated
     */
    @Deprecated
    public void add(Action newAction, String actionName, boolean inPopUp) {
        Class newActionClass = newAction.getClass();

        // on ne peut ajouter une action déjà existante
        if (!allActions.containsKey(newActionClass.getName())) {
            allActions.put(newActionClass.getName(), newAction);
            JButton newActionButton = new JButton(newAction);
            JMenuItem newActionMenuItem = new JMenuItem(newAction);

            newActionButton.setName(table.getName() + ".newB." + newActionClass.getName());
            newActionMenuItem.setName(table.getName() + ".newM." + newActionClass.getName());

            newActionButton.setText(null);
            configureButton(newActionButton);
            addComponent(newActionButton, Position.left(actionName));
            if (inPopUp) {
                addMenuItem(newActionMenuItem, Position.left(actionName));
            }
        }
    }


    public JButton add(Action action, String actionName, Position position) {
        return add(action, actionName, position, false);
    }


    public JButton add(Action newAction, String actionName, Position position, boolean inPopUp) {
        if (allActions.containsKey(actionName)) {
            throw new IllegalArgumentException("L'action " + actionName + " existe déjà.");
        }

        allActions.put(actionName, newAction);
        JButton newActionButton = new JButton(newAction);
        newActionButton.setName(table.getName() + "." + actionName);
        newActionButton.setText(null);
        configureButton(newActionButton);
        addComponent(newActionButton, position);

        if (inPopUp) {
            JMenuItem newActionMenuItem = new JMenuItem(newAction);
            newActionMenuItem.setText((String)newAction.getValue(Action.NAME));
            newActionMenuItem.setName(table.getName() + ".popup." + actionName);
            addMenuItem(newActionMenuItem, position);
        }
        return newActionButton;
    }


    /**
     * @see #addComponent(javax.swing.JComponent, Position)
     * @deprecated
     */
    @Deprecated
    public void addComponentInToolBar(JComponent jComponent, String actionName) {
        if (getAction(actionName) == null) {
            addComponent(jComponent, Position.last());
        }
        else {
            addComponent(jComponent, Position.left(actionName));
        }
    }


    public void addComponent(JComponent component, Position position) {
        int index = -1;
        if (position.getType() != Position.Type.LAST) {
            String actionName = position.getActionName();
            Action action = getAction(actionName);
            if (action == null) {
                throw new IllegalArgumentException(("L'action " + actionName + " n'existe pas."));
            }

            for (int i = 0; i < getComponents().length; i++) {
                Component aComponent = getComponents()[i];
                if (aComponent instanceof JButton && ((JButton)aComponent).getAction() == action) {
                    switch (position.getType()) {
                        case RIGHT:
                            index = i + 1;
                            break;
                        case LEFT:
                        case LAST:
                        default:
                            index = i;
                    }
                    break;
                }
            }
        }

        if (JButton.class.isInstance(component)) {
            JButton button = (JButton)component;
            Action action = button.getAction();
            if (null != action) {
                translationNotifier.addInternationalizableComponent(button,
                                                                    null,
                                                                    action.getClass().getName() + ".tooltip");
            }
        }

        add(component, index);
    }


    public JButton getButtonInToolBar(Action action) {
        for (Component component : getComponents()) {
            if (component instanceof JButton
                && (((JButton)component).getAction() == action)) {
                return (JButton)component;
            }
        }

        return null;
    }


    JMenuItem getMenuItemInPopUpMenu(Action action) {
        for (int i = 0; i < getPopupMenu().getComponentCount(); i++) {
            Component comp = getPopupMenu().getComponent(i);
            if (comp instanceof JMenuItem && ((JMenuItem)comp).getAction() == action) {
                return (JMenuItem)comp;
            }
        }
        return null;
    }


    private void addSubmitButton() {
        SubmitAction submitAction = new SubmitAction(guiContext, table);
        JButton submitButton = createButton(submitAction, ACTION_SAVE);

        EnableStateUpdater updater = new EnableStateUpdater(submitAction, table, true);
        this.table.getDataSource().addPropertyChangeListener(updater);
        this.table.getDataSource().addDataSourceListener(updater);

        addComponent(submitButton, Position.last());
    }


    private void buildActions() {
        registerAction(ACTION_PREVIOUS_PAGE, new PreviousPageAction(guiContext, table));
        registerAction(ACTION_NEXT_PAGE, new NextPageAction(guiContext, table));
        registerAction(ACTION_ADD,
                       new AddAction(guiContext, table, new DetailWindowBuilder((FatherContainer)this)));
        registerAction(ACTION_EDIT,
                       new EditAction(guiContext, table, new DetailWindowBuilder((FatherContainer)this)));
        registerAction(ACTION_DELETE, new DeleteAction(guiContext, table));
        registerAction(ACTION_RELOAD, new ReloadAction(guiContext, table));
        registerAction(ACTION_LOAD, new FindAction(guiContext, table));
        registerAction(ACTION_CLEAR, new ClearAction(guiContext, table));
        registerAction(ACTION_EXPORT_ALL_PAGES, new ExportAllPagesAction(guiContext, table));
    }


    private void registerAction(String actionName, Action action, String... otherActionNames) {
        allActions.put(actionName, action);
        allActions.put(action.getClass().getName(), action);
        for (String otherActionName : otherActionNames) {
            allActions.put(otherActionName, action);
        }
    }


    private void buildGui() {
        add(leftComponentPanel);
        if (hasRecordCountField) {
            requestRecordCountField.setBackground(getBackground());
            leftComponentPanel.add(requestRecordCountField, BorderLayout.CENTER);
        }
        add(Box.createHorizontalGlue());
        addSeparator();

        if (!table.isEditable()) {
            if (table.getPreference().getRequetor() != null) {
                add(requetorStatus);
                addSeparator();
                table.getDataSource().addDataSourceListener(new MyDataSourceListener());
            }
        }

        if (hasUndoRedoButtons()) {
            undoManager.setFather(table.getDataSource());
            undoManager.startListeningDataSource();
            JButton undoButton = createButton(undoManager.getUndoAction(), ACTION_UNDO);
            addComponent(undoButton, Position.last());
            JButton redoButton = createButton(undoManager.getRedoAction(), ACTION_REDO);
            addComponent(redoButton, Position.last());
            addSeparator();
        }

        JButton reloadButton = createButton(ReloadAction.class, ACTION_RELOAD);
        addComponent(reloadButton, Position.last());
        EnableStateUpdater updater = new EnableStateUpdater(getAction(ReloadAction.class.getName()),
                                                            table,
                                                            false);
        table.getDataSource().addDataSourceListener(updater);
        table.getDataSource().addPropertyChangeListener(updater);

        if (!table.isEditable()) {
            if (table.getPreference().getRequetor() != null) {
                JButton findButton = createButton(FindAction.class, ACTION_FIND);
                addComponent(findButton, Position.last());
                JButton clearButton = createButton(ClearAction.class, ACTION_CLEAR);
                addComponent(clearButton, Position.last());
            }
        }
        addSeparator();

        JButton previousPageButton = createButton(PreviousPageAction.class, ACTION_PREVIOUS_PAGE);
        addComponent(previousPageButton, Position.last());
        if (hasNavigator) {
            addComponent(this.requestRecordNavigator, Position.last());
        }
        JButton nextPageButton = createButton(NextPageAction.class, ACTION_NEXT_PAGE);
        addComponent(nextPageButton, Position.last());
        addSeparator();
        JButton addButton = createButton(AddAction.class, ACTION_ADD);
        addComponent(addButton, Position.last());
        JButton deleteButton = createButton(DeleteAction.class, ACTION_DELETE);
        addComponent(deleteButton, Position.last());
        JButton editButton = createButton(EditAction.class, ACTION_EDIT);
        addComponent(editButton, Position.last());

        if (hasValidationButton) {
            addSubmitButton();
        }

        if (hasExcelButton) {
            addSeparator();
            JButton exportButton = createButton(ExportAllPagesAction.class, ACTION_EXPORT_ALL_PAGES);
            addComponent(exportButton, Position.last());
        }

        doEffect(this);

        addMenuItem(new JMenuItem(getAction(EditAction.class.getName())), Position.last());
        addMenuItem(new JMenuItem(getAction(DeleteAction.class.getName())), Position.last());
    }


    private JButton createButton(Class actionClass, String actionType) {
        return createButton(getAction(actionClass.getName()), actionType);
    }


    private JButton createButton(Action action, String actionType) {
        JButton button = new JButton(action);
        button.setName(table.getName() + "." + actionType);
        button.setText(null);
        return button;
    }


    private void addMenuItem(JMenuItem menuItem, Position position) {
        if (menuItem.getAction() != null) {
            String i18nKey = menuItem.getAction().getClass().getName();
            translationNotifier.addInternationalizableComponent(menuItem, i18nKey, i18nKey + ".tooltip");
        }

        Type positionType = position.getType();
        Action positionAction = getAction(position.getActionName());
        if (Type.LAST != positionType && positionAction != null) {
            int positionIndex = -1;
            for (int i = 0; i < popupMenu.getComponentCount(); i++) {
                Component comp = popupMenu.getComponent(i);
                if (comp instanceof JMenuItem && ((JMenuItem)comp).getAction() == positionAction) {
                    positionIndex = i;
                    break;
                }
            }
            if (-1 == positionIndex) {
                throw new IllegalArgumentException(String.format(
                      "Cannot add action %s because the action %s is not in popup",
                      menuItem.getName(),
                      position.getActionName()));
            }

            switch (positionType) {
                case LEFT:
                    popupMenu.add(menuItem, positionIndex);
                    break;
                case RIGHT:
                    popupMenu.add(menuItem, positionIndex + 1);
                    break;
                case LAST:
                    popupMenu.add(menuItem);
                    break;
            }
        }
        else {
            popupMenu.add(menuItem);
        }
    }


    private static void configureButton(JButton button) {
        button.setMargin(new Insets(1, 3, 1, 3));
    }


    public void disablePopupHelper() {
        table.removeMouseListener(this.popupHelper);
        this.popupHelper = null;
    }


    public RequestTable getTable() {
        return table;
    }


    private static class AutomaticUpdateState implements PropertyChangeListener {
        private DataSource father;
        private RequestToolBar toolbar;


        AutomaticUpdateState(DataSource father, RequestToolBar toolbar) {
            this.father = father;
            this.toolbar = toolbar;
            updateStateFromFather();
        }


        public void propertyChange(PropertyChangeEvent evt) {
            updateStateFromFather();
        }


        /**
         * Maj de l'état Enable de la toolbar en fonction du père (si une ligne est selectionné).
         */
        protected void updateStateFromFather() {
            if (father.getSelectedRow() == null) {
                toolbar.setEnabled(false);
            }
            else {
                toolbar.setEnabled(true);
            }
        }
    }

    private static class PopupHelper extends java.awt.event.MouseAdapter {
        private RequestToolBar requestToolBar;


        private PopupHelper(RequestToolBar requestToolBar) {
            this.requestToolBar = requestToolBar;
        }


        @Override
        public void mouseClicked(MouseEvent event) {
            tableMouseClicked(event);
        }


        @Override
        public void mousePressed(MouseEvent event) {
            tableMousePressed(event);
        }


        @Override
        public void mouseReleased(MouseEvent event) {
            maybeShowPopup(event);
        }


        protected void tableMouseClicked(MouseEvent event) {
            Action editAction = requestToolBar.getAction(ACTION_EDIT);
            if (event.getClickCount() == 2 && editAction != null && editAction.isEnabled()) {
                editAction.actionPerformed(new ActionEvent(this, 0, "Modification"));
            }
        }


        void tableMousePressed(MouseEvent event) {
            if (javax.swing.SwingUtilities.isRightMouseButton(event)) {
                int row = requestToolBar.getTable().rowAtPoint(event.getPoint());
                if (row != -1) {
                    requestToolBar.getTable().setRowSelectionInterval(row, row);
                }
            }
            maybeShowPopup(event);
        }


        /**
         * Affiche le popupMenu si necessaire
         *
         * @param event L'événement souris
         */
        private void maybeShowPopup(MouseEvent event) {
            if (event.isPopupTrigger()) {
                requestToolBar.getPopupMenu().show(event.getComponent(), event.getX(), event.getY());
            }
        }
    }

    private class MyDataSourceListener implements DataSourceListener {
        public void loadEvent(DataSourceEvent event) {
            translationNotifier.removeInternationalizable(i18nRequetorStatus);
            if (table.getDataSource().getLoadFactory() == null) {
                i18nRequetorStatus = new InternationalizableJLabel(REQUETOR_EMPTY_REQUEST, requetorStatus);
                translationNotifier.addInternationalizableComponent(i18nRequetorStatus);
            }
            else if (table.getDataSource().getRowCount() == 0) {
                i18nRequetorStatus = new InternationalizableJLabel(REQUETOR_EMPTY_RESULT, requetorStatus);
                translationNotifier.addInternationalizableComponent(i18nRequetorStatus);
            }
            else {
                requetorStatus.setText("");
            }
        }


        public void beforeLoadEvent(DataSourceEvent event) {
        }


        public void beforeSaveEvent(DataSourceEvent event) {
        }


        public void saveEvent(DataSourceEvent event) {
        }
    }

    private static class MyHashMap extends HashMap<String, Action> {
        private final Map<Action, Set<String>> actionToNames = new HashMap<Action, Set<String>>();


        @Override
        public Action put(String key, Action value) {
            if (containsKey(key)) {
                remove(key);
            }

            Set<String> names = actionToNames.get(value);
            if (names == null) {
                names = new TreeSet<String>();
                actionToNames.put(value, names);
            }
            names.add(key);
            return super.put(key, value);
        }


        @Override
        public Action remove(Object key) {
            Action action = get(key);
            if (action != null) {
                Collection<String> names = actionToNames.get(action);
                for (String name : names) {
                    super.remove(name);
                }
            }
            return action;
        }


        @Override
        public void clear() {
            actionToNames.clear();
            super.clear();
        }
    }
}
