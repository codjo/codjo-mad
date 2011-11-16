package net.codjo.mad.gui.request;
import net.codjo.mad.client.request.Field;
import net.codjo.mad.client.request.FieldsList;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.client.request.Row;
import net.codjo.mad.gui.request.event.DataSourceAdapter;
import net.codjo.mad.gui.request.event.DataSourceEvent;
import net.codjo.mad.gui.request.util.DefaultErrorHandler;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import java.util.List;
/**
 * Lien entre 2 datasources. Utiliser le HerrorHandler pour faire une gestion d'erreur fine.
 */
public class DataLink {
    public static final String LOAD_ERROR = "LOAD_ERROR";
    public static final String SAVE_ERROR = "SAVE_ERROR";
    private final DataSource father;
    private final DataSource son;
    private final JoinKeys joinKeys;
    private final SonLoader sonLoader = new SonLoader();
    private final SonSaver sonSaver = new SonSaver();
    private ErrorHandler errorHandler = new DefaultErrorHandler("Erreur de lien");
    private Policy loadPolicy = Policy.AFTER_FATHER;
    private Policy savePolicy = Policy.AFTER_FATHER;


    public DataLink(DataSource father, DataSource son, JoinKeys joinKeys) {
        this.father = father;
        this.son = son;
        this.joinKeys = joinKeys;

        Iterator iterator = joinKeys.iterator();
        while (iterator.hasNext()) {
            JoinKeys.Association association = (JoinKeys.Association)iterator.next();
            father.declare(association.getFatherField());
            son.declare(association.getSonField());
        }
    }


    public ErrorHandler getErrorHandler() {
        return errorHandler;
    }


    public void setErrorHandler(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }


    public void start() {
        if (loadPolicy != Policy.NONE) {
            father.addDataSourceListener(sonLoader);
            father.addPropertyChangeListener(DataSource.SELECTED_ROW_PROPERTY, sonLoader);
        }
        if (savePolicy != Policy.NONE) {
            father.addDataSourceListener(sonSaver);
        }
    }


    public void stop() {
        father.removeDataSourceListener(sonLoader);
        father.removePropertyChangeListener(DataSource.SELECTED_ROW_PROPERTY, sonLoader);
        father.removeDataSourceListener(sonSaver);
    }


    public void setSavePolicy(Policy policy) {
        this.savePolicy = policy;
    }


    public void setLoadPolicy(Policy policy) {
        this.loadPolicy = policy;
    }


    public DataSource getFather() {
        return father;
    }


    public DataSource getSon() {
        return son;
    }


    /**
     * Enumeration de strategie possible pour le fonctionnement d'un DataLink.
     */
    public static final class Policy {
        public static final Policy NONE = new Policy("NONE");
        public static final Policy WITH_FATHER = new Policy("WITH_FATHER");
        public static final Policy AFTER_FATHER = new Policy("AFTER_FATHER");
        private final String myName; // for debug only


        private Policy(String name) {
            myName = name;
        }


        @Override
        public String toString() {
            return myName;
        }
    }

    /**
     * Ecoute les events du pere pour faire la gestion du addSaveRequestTo du fils.
     */
    private class SonSaver extends DataSourceAdapter {
        @Override
        public void beforeSaveEvent(DataSourceEvent event) {
            Row fatherRow = father.getSelectedRow();
            if (fatherRow == null || savePolicy != Policy.WITH_FATHER) {
                return;
            }

            updateSonFieldsWith(fatherRow);
            son.addSaveRequestTo(event.getMultiRequestHelper());
        }


        @Override
        public void saveEvent(DataSourceEvent event) {
            Row fatherRow = father.getSelectedRow();
            if (fatherRow == null || savePolicy != Policy.AFTER_FATHER) {
                return;
            }

            updateSonFieldsWith(fatherRow);
            try {
                son.save();
            }
            catch (RequestException loadError) {
                getErrorHandler().handleError(SAVE_ERROR, loadError);
            }
        }


        private void updateSonFieldsWith(Row fatherRow) {
            for (Iterator iter = joinKeys.iterator(); iter.hasNext();) {
                JoinKeys.Association association = (JoinKeys.Association)iter.next();
                son.apply(association.getSonField(),
                          fatherRow.getFieldValue(association.getFatherField()));
            }
        }
    }

    /**
     * Ecoute les events du pere pour faire la gestion du load du fils.
     */
    private class SonLoader extends DataSourceAdapter implements PropertyChangeListener {
        private boolean loading = false;


        @Override
        public void beforeLoadEvent(DataSourceEvent event) {
            if (loadPolicy != Policy.WITH_FATHER) {
                return;
            }

            if (father.getSelector() == null) {
                son.clear();
                return;
            }

            son.setSelector(newSelectorFromFather(father.getSelector()));
            son.getLoadManager().addLoadRequestTo(event.getMultiRequestHelper());
            loading = true;
        }


        public void propertyChange(PropertyChangeEvent evt) {
            // En mode Load With Father, le chargement du fils a déjà débuté
            // dans beforeLoadEvent().
            if (loading) {
                loading = false;
                return;
            }

            if (father.getSelectedRow() == null) {
                son.clear();
                return;
            }

            FieldsList selector = newSelectorFromFather(father.getSelectedRow());

            if (isAllSelectorsNull(selector)) {
                son.clear();
                return;
            }

            son.setSelector(selector);
            try {
                son.load();
            }
            catch (RequestException loadError) {
                getErrorHandler().handleError(LOAD_ERROR, loadError);
            }
        }


        private boolean isAllSelectorsNull(FieldsList sonSelectors) {
            List<Field> fields = sonSelectors.getFields();
            for (Field field : fields) {
                if (!"null".equals(field.getValue())) {
                    return false;
                }
            }
            return true;
        }


        private FieldsList newSelectorFromFather(FieldsList selectorFather) {
            FieldsList selectorSon = new FieldsList();
            for (Iterator iter = joinKeys.iterator(); iter.hasNext();) {
                JoinKeys.Association association = (JoinKeys.Association)iter.next();
                selectorSon.addField(association.getSonField(),
                                     selectorFather.getFieldValue(association.getFatherField()));
            }
            return selectorSon;
        }
    }
}
