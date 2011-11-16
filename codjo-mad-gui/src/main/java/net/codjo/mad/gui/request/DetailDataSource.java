package net.codjo.mad.gui.request;
import net.codjo.gui.toolkit.number.NumberField;
import net.codjo.gui.toolkit.readonly.ReadOnlyManager;
import net.codjo.gui.toolkit.text.DocumentWithMaxSizeService;
import net.codjo.gui.toolkit.util.DecimalFormatEnum;
import net.codjo.gui.toolkit.util.GuiUtil;
import net.codjo.mad.client.request.FieldsList;
import net.codjo.mad.client.request.Request;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.client.request.RequestSender;
import net.codjo.mad.client.request.Result;
import net.codjo.mad.client.request.Row;
import net.codjo.mad.common.structure.FieldStructure;
import net.codjo.mad.common.structure.StructureReader;
import net.codjo.mad.common.structure.TableStructure;
import net.codjo.mad.gui.framework.GuiContext;
import net.codjo.mad.gui.request.event.DataSourceSupport;
import net.codjo.mad.gui.request.factory.RequestFactory;
import net.codjo.mad.gui.request.undo.FieldSnapshotEdit;
import net.codjo.mad.gui.request.util.MultiRequestsHelper;
import net.codjo.mad.gui.request.wrapper.FieldSetter;
import net.codjo.mad.gui.request.wrapper.GuiWrapper;
import net.codjo.mad.gui.request.wrapper.GuiWrapperFactory;
import net.codjo.mad.gui.request.wrapper.UnsupportedComponentException;
import net.codjo.mad.gui.structure.StructureCache;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.text.JTextComponent;
import javax.swing.undo.UndoableEditSupport;
import org.apache.log4j.Logger;
/*
 * Classe helper des IHM détails.
 *
 * @author $Author: gaudefr $
 * @version $Revision: 1.28 $
 */
public class DetailDataSource extends AbstractDataSource {
    public static final Integer HIGH_PRIORITY = -1000;
    public static final Integer LOW_PRIORITY = 1000;
    public static final Integer NORMAL_PRIORITY = 0;
    public static final String SAVE_FACTORY_PROPERTY = "saveFactory";
    public static final String UPDATE_PRIORITY = "UPDATE_ORDER";
    private static final String LF = "\n";
    private static final String TAB = "\t";
    private static final Logger LOG = Logger.getLogger(DetailDataSource.class);
    private Map<String, GuiWrapper> declaredFields = new HashMap<String, GuiWrapper>();
    private UndoableEditSupport undoSnapshotSupport = new UndoableEditSupport();
    private GuiWrapperChangeListener guiChangeListener = new GuiWrapperChangeListener(this);
    private GuiWrapperUndoListener guiUndoListener = new GuiWrapperUndoListener(this);
    private Result loadResult;
    private RequestFactory saveFactory;
    private Result saveResult;
    private ReadOnlyManager readOnlyManager;
    private boolean readOnlyManagement;
    private List<FieldSetter> undoSetters;
    private StructureReader structureReader;
    private List<String> requiredFields = new ArrayList<String>();
    private Row selectedLoadResultRow;


    public DetailDataSource(GuiContext guiContext) {
        setGuiContext(guiContext);
        setLoadManager(new DefaultLoadManager());
        setSelectedRow(new Row());
        undoSnapshotSupport.addUndoableEditListener(guiUndoListener);
        try {
            setStructureReader(
                  ((StructureCache)guiContext
                        .getProperty(StructureCache.STRUCTURE_CACHE)).getStructureReader());
        }
        catch (Exception e) {
            LOG.warn("StructureReader est introuvable !");
        }
    }


    public DetailDataSource(GuiContext guiContext,
                            RequestSender sender,
                            FieldsList selector,
                            RequestFactory loadFactory,
                            RequestFactory saveFactory) {
        this(guiContext);
        if (sender == null) {
            throw new IllegalArgumentException("écran détail mal initialisé");
        }
        setRequestSender(sender);
        setLoadFactory(loadFactory);
        setSaveFactory(saveFactory);
        setSelector(selector);
    }


    public GuiWrapperUndoListener getGuiUndoListener() {
        return guiUndoListener;
    }


    public void useReadOnlyManagement() {
        this.readOnlyManagement = true;
    }


    public void setFieldValue(String fieldName, String xmlValue) {
        GuiWrapper wrapper = declaredFields.get(fieldName);
        if (wrapper == null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Liste des champs connus : " + getDeclaredFields());
            }
            throw new IllegalArgumentException("Champ inconnu " + fieldName);
        }
        wrapper.setXmlValue((xmlValue == null ? NULL : xmlValue));
    }


    public void setInsertFactory(RequestFactory insertFactory) {
        setSaveFactory(insertFactory);
    }


    public void setLoadResult(Result loadResult) {
        fireBeforeLoadEvent(null);
        setLoadResultImpl(loadResult);
    }


    public void setSaveFactory(RequestFactory saveFactory) {
        createReadOnlyManager(saveFactory);

        RequestFactory old = this.saveFactory;
        this.saveFactory = saveFactory;
        firePropertyChange(SAVE_FACTORY_PROPERTY, old, this.saveFactory);
    }


    public void setSaveResult(Result saveResult) {
        fireBeforeSaveEvent(null);
        setSaveResultImpl(saveResult);
    }


    public void setUpdateFactory(RequestFactory updateFactory) {
        setSaveFactory(updateFactory);
    }


    public Map<String, GuiWrapper> getDeclaredFields() {
        return Collections.unmodifiableMap(declaredFields);
    }


    public String getFieldValue(String fieldName) {
        if (getSaveResult() != null && getSaveResult().containsField(0, fieldName)) {
            return getSaveResult().getValue(0, fieldName);
        }
        return getSelectedRow().getFieldValue(fieldName);
    }


    public boolean isFieldNull(String fieldName) {
        return NULL.equals(getFieldValue(fieldName));
    }


    public boolean isFieldRequired(String fieldName) {
        return requiredFields.contains(fieldName);
    }


    public Result getLoadResult() {
        return loadResult;
    }


    public RequestFactory getSaveFactory() {
        return saveFactory;
    }


    public Result getSaveResult() {
        return this.saveResult;
    }


    public int getTotalRowCount() {
        if (loadResult != null) {
            return loadResult.getTotalRowCount();
        }
        else {
            return 0;
        }
    }


    public StructureReader getStructureReader() {
        return structureReader;
    }


    public void setStructureReader(StructureReader structureReader) {
        this.structureReader = structureReader;
    }


    public void setSelectedRowIndex(int rowIndex) {
        setSelectedRow(loadResult.getRow(rowIndex));
    }


    @Override
    public void setSelectedRow(Row selectedRow) {
        super.setSelectedRow(selectedRow != null ? new Row(selectedRow) : newRowWhithDefaultValues());
        selectedLoadResultRow = selectedRow == null ? newRowWhithDefaultValues() : selectedRow;
    }


    public Request buildSaveRequest(RequestFactory factory) {
        return buildRequest(factory, true);
    }


    public void declare(final String fieldName) {
        if (!declaredFields.containsKey(fieldName)) {
            GuiWrapper wrapper = GuiWrapperFactory.wrapp(fieldName);
            declare(fieldName, wrapper);
        }
    }


    public void declare(final String fieldName, JComponent fieldContainer) {
        if (fieldContainer == null) {
            throw new IllegalArgumentException();
        }
        try {
            fillInfoStructure(fieldName, fieldContainer);
            addReadOnlyComponent(fieldContainer);
            GuiWrapper wrapper = GuiWrapperFactory.wrapp(fieldName, fieldContainer);
            fieldContainer.setName(fieldName);

            declare(fieldName, wrapper);
        }
        catch (UnsupportedComponentException ex) {
            throw new IllegalArgumentException("Type de composant non supportée " + ex.toString()
                                               + "(cf net.codjo.pims.gui.request.wrapper.GuiWrapperFactory)");
        }
    }


    public boolean hasBeenUpdated() {
        return hasBeenUpdated((String[])null);
    }


    public boolean hasBeenUpdated(String... fieldNames) {
        if (loadResult != null) {
            return guiHasChangedFromLoad(fieldNames);
        }
        else {
            return guiHasChangedFromInitialValue(fieldNames);
        }
    }


    public void clear() {
        setSelector(null);
        setLoadResult(new Result());
    }


    public void addSaveRequestTo(MultiRequestsHelper helper) {
        boolean somethingToSave = getSaveFactory() != null && hasBeenUpdated();
        if (somethingToSave) {
            helper.addSubmiter(new SaveSubmiter(getSaveFactory()));
        }

        fireBeforeSaveEvent(helper);

        if (!somethingToSave) {
            fireSaveEvent(null);
        }
    }


    public void apply(String fieldName, String fieldValue) {
        setFieldValue(fieldName, fieldValue);
    }


    public void saveUsing(RequestFactory factory)
          throws RequestException {
        MultiRequestsHelper helper = new MultiRequestsHelper(getRequestSender());

        SaveSubmiter submitter = new SaveSubmiter(factory);
        helper.addSubmiter(submitter);
        fireBeforeSaveEvent(helper);
        helper.sendRequest();
    }


    public void startSnapshotMode() {
        undoSetters = getSettersForCurrentWrappers();
    }


    public void stopSnapshotMode() {
        List<FieldSetter> redoSetters = getSettersForCurrentWrappers();
        if ((!undoSetters.isEmpty()) && (!redoSetters.isEmpty())) {
            undoSnapshotSupport.postEdit(new FieldSnapshotEdit(undoSetters, redoSetters));
        }
    }


    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("DataSource").append(LF);
        buffer.append(TAB).append("LoadFactory =").append(getLoadFactory()).append(LF);
        buffer.append(TAB).append("SaveFactory =").append(getSaveFactory()).append(LF);
        buffer.append(TAB).append("selector =").append(getSelector()).append(LF);

        buffer.append(TAB).append("----------------------------").append(LF);
        buffer.append(TAB).append("DeclaredFields =").append(getDeclaredFields()).append(LF);
        buffer.append(TAB).append("newRowWhithDefaultValues  =").append(newRowWhithDefaultValues())
              .append(LF);

        buffer.append(TAB).append("----------------------------").append(LF);
        buffer.append(TAB).append("LoadResult =").append(getLoadResult()).append(LF);

        return buffer.toString();
    }


    /**
     * Construit un request pour la ligne demandée à l'écran détail.
     *
     * @return une requete select.
     */
    public Request buildSelectRequest() {
        return buildRequest(getLoadFactory(), false);
    }


    private boolean isValidResult(final Result result) {
        return result != null && result.getRowCount() > 0;
    }


    private FieldsList buildPkFieldList() {
        FieldsList pks = new FieldsList();
        for (Iterator i = getLoadResult().primaryKeys(); i.hasNext();) {
            String pkName = (String)i.next();
            pks.addField(pkName, selectedLoadResultRow.getFieldValue(pkName));
        }
        return pks;
    }


    private List<FieldSetter> getSettersForCurrentWrappers() {
        List<FieldSetter> setters = new ArrayList<FieldSetter>();
        for (Map.Entry<String, GuiWrapper> item : declaredFields.entrySet()) {
            GuiWrapper guiWrapper = item.getValue();
            String guiValue = guiWrapper.getXmlValue();
            setters.add(new FieldSetter(guiWrapper, guiValue, guiUndoListener));
        }
        return setters;
    }


    private Request buildRequest(RequestFactory factory, boolean saveMode) {
        if (factory.needsSelector() && !saveMode) {
            factory.init(new FieldsList(getSelector()));
        }
        else if (factory.needsSelector() && saveMode) {
            factory.init(buildPkFieldList());
        }

        Map<String, String> fields = new HashMap<String, String>();
        for (Map.Entry<String, GuiWrapper> item : declaredFields.entrySet()) {
            GuiWrapper wrapper = item.getValue();
            if (wrapper.getFieldType() != FieldType.READ_ONLY || !saveMode) {
                fields.put(item.getKey(), item.getValue().getXmlValue());
            }
        }
        return factory.buildRequest(fields);
    }


    private void declare(final String fieldName, final GuiWrapper wrapper) {
        declaredFields.put(fieldName, wrapper);
        if (!getSelectedRow().contains(fieldName)) {
            getSelectedRow().addField(fieldName, wrapper.getXmlValue());
        }

        wrapper.addPropertyChangeListener(guiChangeListener);
        wrapper.addUndoableEditListener(guiUndoListener);

        setDefaultValue(fieldName, wrapper.getXmlValue());
    }


    private boolean guiHasChangedFromInitialValue(String... fieldNames) {
        if (fieldNames == null || fieldNames.length == 0) {
            for (Map.Entry<String, GuiWrapper> item : declaredFields.entrySet()) {
                Object initialValue = getDefaultValue(item.getKey());
                Object guiValue = item.getValue().getXmlValue();
                if (valueHasChanged(initialValue, guiValue)) {
                    return true;
                }
            }
        }
        else {
            for (String fieldName : fieldNames) {
                Object prevValue = getDefaultValue(fieldName);
                Object guiValue = declaredFields.get(fieldName).getXmlValue();
                if (valueHasChanged(prevValue, guiValue)) {
                    return true;
                }
            }
        }
        return false;
    }


    private static boolean valueHasChanged(Object prevValue, Object newValue) {
        if (prevValue == null) {
            return !(newValue == null);
        }
        else {
            return !prevValue.equals(newValue);
        }
    }


    private boolean guiHasChangedFromLoad(String... fieldNames) {
        if (fieldNames == null || fieldNames.length == 0) {
            for (Map.Entry<String, GuiWrapper> item : declaredFields.entrySet()) {
                Object prevValue = determinePreviousValue(item.getKey());
                Object guiValue = item.getValue().getXmlValue();
                if (valueHasChanged(prevValue, guiValue)) {
                    return true;
                }
            }
        }
        else {
            for (String fieldName : fieldNames) {
                Object prevValue = determinePreviousValue(fieldName);
                Object guiValue = declaredFields.get(fieldName).getXmlValue();
                if (valueHasChanged(prevValue, guiValue)) {
                    return true;
                }
            }
        }
        return false;
    }


    private Object determinePreviousValue(final String fieldName) {
        Object prevValue;
        if (!selectedLoadResultRow.contains(fieldName)) {
            prevValue = getDefaultValue(fieldName);
        }
        else {
            prevValue = selectedLoadResultRow.getFieldValue(fieldName);
        }

        return prevValue;
    }


    public void updateGuiFields() {
        List<GuiWrapper> allWrappers = allWrappersOrdered();
        if (loadResult != null) {
            for (GuiWrapper wrapper : allWrappers) {
                wrapper.setXmlValue(selectedLoadResultRow.getFieldValue(wrapper.getFieldName()));
            }
        }
        else {
            for (GuiWrapper wrapper : allWrappers) {
                wrapper.setXmlValue(getDefaultValue(wrapper.getFieldName()));
            }
        }
    }


    private List<GuiWrapper> allWrappersOrdered() {
        List<GuiWrapper> allWrappers = new ArrayList<GuiWrapper>(declaredFields.values());
        Collections.sort(allWrappers,
                         new Comparator<GuiWrapper>() {
                             public int compare(GuiWrapper object, GuiWrapper object1) {
                                 return object.getUpdateOrder().compareTo(object1.getUpdateOrder());
                             }
                         });
        return allWrappers;
    }


    private void setLoadResultImpl(final Result loadResult) {
        Result result = null;
        if (isValidResult(loadResult)) {
            result = loadResult;
        }

        this.saveResult = null;

        this.loadResult = result != null ? result : null;
        setSelectedRow(result != null && result.getRowCount() > 0 ? loadResult.getRow(0) : null);

        updateGuiFields();
        fireLoadEvent(result);
    }


    private void setSaveResultImpl(Result saveResult) {
        this.saveResult = saveResult;
        updateGuiFieldsForSave(saveResult);
        fireSaveEvent(saveResult);
    }


    private void updateGuiFieldsForSave(Result result) {

        // Si résultat null ou vide 
        if (result == null || result.getRowCount() == 0) {
            return;
        }

        Row row = result.getRow(0);
        for (GuiWrapper wrapper : allWrappersOrdered()) {
            if (row.contains(wrapper.getFieldName())) {
                wrapper.setXmlValue(row.getFieldValue(wrapper.getFieldName()));
            }
        }
    }


    private void addReadOnlyComponent(JComponent fieldContainer) {
        if (!readOnlyManagement) {
            return;
        }
        readOnlyManager.addReadOnlyComponent(fieldContainer);
    }


    private void createReadOnlyManager(RequestFactory requestSaveFactory) {
        boolean isReadOnly = requestSaveFactory == null
                             || !getGuiContext().getUser().isAllowedTo(requestSaveFactory.getId());
        readOnlyManager = new ReadOnlyManager(isReadOnly);
    }


    private void fillInfoStructure(String fieldName, JComponent fieldContainer) {
        if (getEntityName() != null && getStructureReader() != null) {
            if (!getStructureReader().containsTableByJavaName(getEntityName())) {
                LOG.warn("Structure not found for entity '" + getEntityName() + "' (field '" + fieldName
                         + "') : maxLength not set");
                return;
            }

            TableStructure tableStructure = getStructureReader().getTableByJavaName(getEntityName());
            FieldStructure field = tableStructure.getFieldByJava(fieldName);
            if (field == null) {
                LOG.warn("Unknown field '" + fieldName + "' for entity '" + getEntityName()
                         + "' : maxLength not set");
                return;
            }

            initMaxLength(fieldContainer, field);
            initRequiredFields(fieldName, field);
        }
    }


    private void initRequiredFields(String fieldName, FieldStructure field) {
        if (field.isSqlRequired()) {
            requiredFields.add(fieldName);
        }
    }


    private void initMaxLength(JComponent fieldContainer, FieldStructure field) {
        if (Types.INTEGER == field.getSqlType()) {
            if (fieldContainer instanceof NumberField) {
                ((NumberField)fieldContainer).applyDecimalFormat(
                      GuiUtil.computeFormatPattern(0, DecimalFormatEnum.FULL));
            }
        }
        else {
            String precision = field.getSqlPrecision();
            if (precision != null) {
                int integerLength;
                int decimalLength = 0;
                int digitIndex = precision.indexOf(',');
                if (digitIndex < 0) {
                    digitIndex = precision.length();
                }
                integerLength = Integer.parseInt(precision.substring(0, digitIndex));
                if (digitIndex + 1 < precision.length()) {
                    decimalLength = Integer
                          .parseInt(precision.substring(digitIndex + 1, precision.length()));
                }
                int maxTextLength = integerLength + (decimalLength > 0 ? 1 : 0) + decimalLength;

                if (fieldContainer instanceof NumberField) {
                    NumberField numberField = (NumberField)fieldContainer;
                    numberField.setMaximumFractionDigits(decimalLength);
                    numberField.setMaximumIntegerDigits(integerLength - decimalLength);
                    numberField.applyDecimalFormat(GuiUtil.computeFormatPattern(decimalLength,
                                                                                DecimalFormatEnum.FULL));
                }
                else if (fieldContainer instanceof JTextComponent) {
                    DocumentWithMaxSizeService.install((JTextComponent)fieldContainer, maxTextLength);
                }
            }
        }
    }


    private final class SaveSubmiter implements RequestSubmiter {
        private RequestFactory factory;


        SaveSubmiter(RequestFactory factory) {
            this.factory = factory;
        }


        public Request buildRequest() {
            return DetailDataSource.this.buildRequest(factory, true);
        }


        public void setResult(Result result) {
            setSaveResultImpl(result);
        }
    }

    private class LoadSubmiter implements RequestSubmiter {
        public Request buildRequest() {
            return DetailDataSource.this.buildRequest(getLoadFactory(), false);
        }


        public void setResult(Result result) {
            setLoadResultImpl(result);
        }
    }

    private class DefaultLoadManager implements LoadManager {
        public void doLoad(DataSourceSupport support)
              throws RequestException {
            MultiRequestsHelper helper = new MultiRequestsHelper(getRequestSender());
            DetailDataSource.this.addLoadRequestTo(helper);
            helper.sendRequest();
        }


        public void addLoadRequestTo(MultiRequestsHelper helper) {
            if (getLoadFactory() != null) {
                helper.addSubmiter(new LoadSubmiter());
            }

            fireBeforeLoadEvent(helper);
        }
    }
}
