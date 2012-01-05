package net.codjo.mad.gui.framework;
import net.codjo.gui.toolkit.date.DateField;
import net.codjo.gui.toolkit.util.ErrorDialog;
import net.codjo.gui.toolkit.waiting.WaitingPanel;
import net.codjo.mad.client.request.FieldsList;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.gui.request.ListDataSource;
import net.codjo.mad.gui.request.RequestComboBox;
import net.codjo.mad.gui.request.RequestTable;
import net.codjo.mad.gui.request.factory.RequestFactory;
import net.codjo.mad.gui.request.wrapper.GuiWrapper;
import net.codjo.mad.gui.request.wrapper.GuiWrapperFactory;
import net.codjo.mad.gui.request.wrapper.UnsupportedComponentException;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EventObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
public class FilterPanel extends JPanel {
    private RequestTable requestTable;
    private WaitingPanel waitingPanel;
    private RequestFactory loadFactory;
    private Map<String, GuiWrapper> filterMap = new HashMap<String, GuiWrapper>();
    private Map<String, String> componentToFieldName = new HashMap<String, String>();
    private Map<String, List<String>> dependantFilterMap = new HashMap<String, List<String>>();
    private ActionListener reloadActionListener = new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
            reload(evt);
        }
    };
    private FocusAdapter reloadFocusListener = new FocusAdapter() {
        @Override
        public void focusLost(FocusEvent evt) {
            reload(evt);
        }
    };
    private ItemListener reloadItemListener = new ItemListener() {
        public void itemStateChanged(ItemEvent evt) {
            if (evt.getStateChange() == ItemEvent.SELECTED) {
                reload(evt);
            }
        }
    };
    private boolean withSearchButton = true;
    private JButton searchButton = new JButton("Afficher");
    private boolean filteringMutex = false;
    private boolean postponedLoad = false;


    public FilterPanel(RequestTable requestTable) {
        this(requestTable, null);
    }


    public FilterPanel(RequestTable requestTable, WaitingPanel waitingPanel) {
        setRequestTable(requestTable);
        setWaitingPanel(waitingPanel);

        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

        this.searchButton.setName("searchButton");
        this.searchButton.addActionListener(this.reloadActionListener);
        Box box = Box.createHorizontalBox();
        box.add(searchButton);
        box.add(Box.createHorizontalGlue());
        add(box);
    }


    public boolean isWithSearchButton() {
        return withSearchButton;
    }


    public void setWithSearchButton(boolean with) {
        if (withSearchButton == with) {
            return;
        }
        else if (!with) {
            searchButton.setVisible(false);
        }
        else {
            searchButton.setVisible(true);
        }
        withSearchButton = with;
    }


    public boolean isLoadPostponed() {
        return postponedLoad;
    }


    /**
     * Set to <code>false</code> to prevent load of <code>RequestComboBox</code> filters when {@link
     * #addComboFilter(String, String, String, String, boolean, String)} is called. If value is later on
     * changed to <code>true</code>, performs the load on every <code>RequestComboBox</code> filter itself.
     *
     * @param postpone flag to change load behavior of <code>RequestComboBox</code> filters
     */
    public void setPostponedLoad(boolean postpone) {
        if (postponedLoad && !postpone) {
            for (GuiWrapper guiWrapper : filterMap.values()) {
                JComponent filter = guiWrapper.getGuiComponent();
                if (filter instanceof RequestComboBox) {
                    RequestComboBox comboBox = (RequestComboBox)filter;
                    try {
                        comboBox.load();
                    }
                    catch (RequestException exc) {
                        ErrorDialog.show(getRequestTable(), exc.getMessage(), exc);
                    }
                }
            }
        }
        postponedLoad = postpone;
    }


    public RequestTable getRequestTable() {
        return requestTable;
    }


    public void setRequestTable(RequestTable requestTable) {
        this.requestTable = requestTable;
        this.loadFactory = requestTable.getDataSource().getLoadFactory();
    }


    public WaitingPanel getWaitingPanel() {
        return waitingPanel;
    }


    public void setWaitingPanel(WaitingPanel panel) {
        waitingPanel = panel;
    }


    private void runInWaitingPanel(Runnable functor) {
        if (getWaitingPanel() == null) {
            filteringMutex = true;
            functor.run();
            filteringMutex = false;
        }
        else {
            getWaitingPanel().exec(functor);
        }
    }


    public boolean isRunning() {
        return filteringMutex || (null != getWaitingPanel() && getWaitingPanel().isShowing());
    }


    protected void reload(final EventObject evt) {
        if (!isRunning()) {
            if (withSearchButton && null != evt.getSource() && searchButton != evt.getSource()) {
                if (dependantFilterMap.isEmpty()) {
                    return;
                }

                runInWaitingPanel(new Runnable() {
                    public void run() {
                        JComponent source = (JComponent)evt.getSource();
                        List<String> dependantFilters = dependantFilterMap.get(source.getName());
                        if (null != dependantFilters && !dependantFilters.isEmpty()) {
                            reloadDependant(source.getName(), dependantFilters);
                        }
                    }
                });
            }
            else {
                runInWaitingPanel(new Runnable() {
                    public void run() {
                        search();
                    }
                });
            }
        }
    }


    protected void reloadDependant(String sourceName, List<String> dependantNames) {
        GuiWrapper guiWrapper = filterMap.get(sourceName);
        String field = componentToFieldName.get(sourceName);
        String value = guiWrapper.getXmlValue();

        for (String dependantFilter : dependantNames) {
            JComponent dependant = filterMap.get(dependantFilter).getGuiComponent();
            if (dependant instanceof RequestComboBox) {
                try {
                    RequestComboBox depComboBox = (RequestComboBox)dependant;
                    String selectedValue = depComboBox.getSelectedValue(depComboBox.getModelFieldName());

                    FieldsList selector = depComboBox.getDataSource().getSelector();
                    if (null == selector) {
                        selector = new FieldsList(field, value);
                    }
                    else {
                        selector.addOrUpdateField(field, value);
                    }
                    depComboBox.getDataSource().setSelector(selector);
                    depComboBox.load();
                    depComboBox.setPreferredSize(depComboBox.getPreferredSizeForContent());

                    depComboBox.setSelectedItem(selectedValue);
                }
                catch (RequestException exc) {
                    ErrorDialog.show(getRequestTable(),
                                     "Erreur lors de la création de la dépendance du composant '"
                                     + dependant.getName() + "' par le composant '" + field + "'", exc);
                }
            }
        }
    }


    private void search() {
        FieldsList selector = getSelector();

        try {
            preSearch(selector);
        }
        catch (RequestException exc) {
            ErrorDialog.show(getRequestTable(), exc.getMessage(), exc);
            return;
        }
        RequestFactory oldLoadFactory = getRequestTable().getDataSource().getLoadFactory();
        if (loadFactory != null) {
            getRequestTable().getDataSource().setLoadFactory(loadFactory);
        }
        getRequestTable().setSelector(selector);
        getRequestTable().setCurrentPage(1);
        try {
            getRequestTable().load();
        }
        catch (RequestException exc) {
            getRequestTable().getDataSource().setLoadFactory(oldLoadFactory);
            ErrorDialog.show(getRequestTable(), exc.getMessage(), exc);
            handleLoadError(selector, exc);
        }
    }


    protected FieldsList getSelector() {
        FieldsList selector = new FieldsList();
        for (Entry<String, GuiWrapper> entry : filterMap.entrySet()) {
            String componentName = entry.getKey();
            GuiWrapper guiWrapper = entry.getValue();
            String fieldName = componentToFieldName.get(componentName);
            if (fieldName == null) {
                fieldName = componentName;
            }
            String value = guiWrapper.getXmlValue();
            if (!"null".equals(value)) {
                selector.addOrUpdateField(fieldName, value);
            }
        }
        return selector;
    }


    @SuppressWarnings({"UnusedDeclaration"})
    protected void preSearch(FieldsList selector) throws RequestException {
        //do nothing
    }


    @SuppressWarnings({"UnusedDeclaration"})
    protected void handleLoadError(FieldsList selector, RequestException exception) {
        //do nothing
    }


    public JButton getSearchButton() {
        if (withSearchButton) {
            return searchButton;
        }
        return null;
    }


    public void setSearchButtonLabel(String label) {
        if (withSearchButton) {
            searchButton.setText(label);
        }
    }


    public void setComponentToFieldName(String componentName, String fieldName) {
        if (!filterMap.containsKey(componentName)) {
            throw new IllegalArgumentException("Le composant '" + componentName + "' est inconnu");
        }

        componentToFieldName.put(componentName, fieldName);
    }


    public JComponent getFilter(String componentName) {
        return filterMap.get(componentName).getGuiComponent();
    }


    public Collection<JComponent> getFilters() {
        return new AbstractCollection<JComponent>() {
            @Override
            public Iterator<JComponent> iterator() {
                return new IteratorWrapper<GuiWrapper, JComponent>(filterMap.values().iterator()) {
                    public JComponent next() {
                        return wrapped().next().getGuiComponent();
                    }
                };
            }


            @Override
            public int size() {
                return filterMap.size();
            }
        };
    }


    public void limitFilter(final String filterToListenName, final String... dependantFiltersNames) {
        JComponent filterToListen = filterMap.get(filterToListenName).getGuiComponent();
        if (filterToListen == null) {
            throw new IllegalArgumentException("Le filtre '" + filterToListenName + "' n'existe pas");
        }
        if (!(filterToListen instanceof JComboBox || filterToListen instanceof JTextField)) {
            throw new IllegalArgumentException(
                  "Le composant filterToListen doit etre un champ texte ou une liste deroulante.");
        }

        Set<String> distinctDependantFiltersNames = new HashSet<String>(dependantFiltersNames.length);
        for (String dependantFiltersName : dependantFiltersNames) {
            JComponent dependantFilter = filterMap.get(dependantFiltersName).getGuiComponent();

            if (dependantFilter == null) {
                throw new IllegalArgumentException(
                      "Le filtre '" + dependantFiltersName + "' n'existe pas");
            }
            distinctDependantFiltersNames.add(dependantFiltersName);
        }

        List<String> depedants = dependantFilterMap.get(filterToListenName);
        if (depedants == null) {
            depedants = new ArrayList<String>();
            dependantFilterMap.put(filterToListenName, depedants);
        }
        depedants.addAll(distinctDependantFiltersNames);
    }


    public void addTextFilter(String label, String componentName, String fieldName) {
        addTextFilter(label, componentName, fieldName, 12);
    }


    public void addTextFilter(String label, String componentName, String fieldName, int textFieldLength) {
        if (componentName == null) {
            throw new IllegalArgumentException("Le paramètre componentName doit être non nul");
        }
        JTextField field = new JTextField(textFieldLength);
        field.setName(componentName);
        addTextFilter(label, field, fieldName);
    }


    public void addTextFilter(String label, final JTextField textField, String fieldName) {
        try {
            addFilter(label, textField, fieldName);
            setComponentToFieldName(textField.getName(), fieldName);
            textField.addFocusListener(reloadFocusListener);
            textField.addActionListener(reloadActionListener);
            getRequestTable().addPropertyChangeListener("SqlRequetor.load", new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    textField.removeActionListener(reloadActionListener);
                    textField.setText("");
                    textField.addActionListener(reloadActionListener);
                }
            });
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Le composant textField n'est pas correctement initialisé", e);
        }
    }


    public void addDateFilter(String label, String componentName, String fieldName) {
        if (componentName == null) {
            throw new IllegalArgumentException("Le paramètre componentName doit être non nul");
        }
        DateField dateField = new DateField();
        dateField.setName(componentName);
        addDateFilter(label, dateField, fieldName);
    }


    public void addDateFilter(String label, final DateField dateField, String fieldName) {
        try {
            addFilter(label, dateField, fieldName);
            setComponentToFieldName(dateField.getName(), fieldName);
            dateField.addFocusListener(reloadFocusListener);
            dateField.addActionListener(reloadActionListener);
            getRequestTable().addPropertyChangeListener("SqlRequetor.load", new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    dateField.removeActionListener(reloadActionListener);
                    dateField.setDate(null);
                    dateField.addActionListener(reloadActionListener);
                }
            });
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Le composant dateField n'est pas correctement initialisé", e);
        }
    }


    public void addComboFilter(String label,
                               String componentName,
                               String modelColumn,
                               String viewColumn,
                               boolean nullValue,
                               String handlerId) {
        if (componentName == null || modelColumn == null || handlerId == null) {
            throw new IllegalArgumentException(
                  "Les paramètres componentName, modelColumn et handlerId doivent être non nul");
        }

        ListDataSource source = new ListDataSource();
        if (viewColumn == null) {
            source.setColumns(new String[]{modelColumn});
        }
        else {
            source.setColumns(new String[]{modelColumn, viewColumn});
        }
        source.setLoadFactoryId(handlerId);
        RequestComboBox comboBox = new RequestComboBox();
        comboBox.setName(componentName);
        comboBox.initRequestComboBox(modelColumn, null == viewColumn ? modelColumn : viewColumn, nullValue);
        comboBox.setDataSource(source);

        if (!postponedLoad) {
            try {
                comboBox.load();
            }
            catch (RequestException exc) {
                ErrorDialog.show(getRequestTable(), exc.getMessage(), exc);
                return;
            }
        }
        comboBox.setPreferredSize(comboBox.getPreferredSizeForContent());

        addComboFilter(label, comboBox);
    }


    public void addComboFilter(String label, final JComboBox comboBox) throws IllegalArgumentException {
        try {
            String fieldName = null;
            if (comboBox instanceof RequestComboBox) {
                fieldName = ((RequestComboBox)comboBox).getModelFieldName();
            }
            addFilter(label, comboBox, fieldName);
            if (comboBox instanceof RequestComboBox) {
                setComponentToFieldName(comboBox.getName(), fieldName);
                comboBox.addItemListener(reloadItemListener);
            }
            getRequestTable().addPropertyChangeListener("SqlRequetor.load", new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    comboBox.removeItemListener(reloadItemListener);
                    comboBox.setSelectedIndex(0);
                    comboBox.addItemListener(reloadItemListener);
                }
            });
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Le paramètre comboBox n'est pas correctement initialisé");
        }
    }


    private void addFilter(String label, JComponent component, String fieldName)
          throws IllegalArgumentException {
        if (component.getName() == null) {
            throw new IllegalArgumentException("Le composant doit être nommé.");
        }
        if (filterMap.containsKey(component.getName())) {
            throw new IllegalArgumentException("Un composant avec le même nom existe déjà.");
        }
        try {
            filterMap.put(component.getName(), GuiWrapperFactory.wrapp(fieldName, component));
        }
        catch (UnsupportedComponentException e) {
            throw new IllegalArgumentException("Impossible d'ajouter un filtre", e);
        }

        Box outer = Box.createHorizontalBox();
        outer.add(new JLabel(label));
        outer.add(Box.createRigidArea(new Dimension(5, 0)));
        outer.add(component);
        outer.add(Box.createRigidArea(new Dimension(5, 0)));
        add(outer, getComponentCount() - 1);
    }


    private static abstract class IteratorWrapper<T, U> implements Iterator<U> {
        private final Iterator<T> iterator;


        protected IteratorWrapper(Iterator<T> iterator) {
            this.iterator = iterator;
        }


        public boolean hasNext() {
            return iterator.hasNext();
        }


        public void remove() {
            throw new UnsupportedOperationException();
        }


        protected Iterator<T> wrapped() {
            return iterator;
        }
    }
}
