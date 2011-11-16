package net.codjo.mad.client.request;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
/**
 */
public class FieldsList {
    private static final Logger APP = Logger.getLogger(FieldsList.class);
    private List<Field> fields = new ArrayList<Field>();


    public FieldsList() {
    }


    public FieldsList(FieldsList fl) {
        for (Field field : fl.getFields()) {
            addField(field.getName(), field.getValue());
        }
    }


    public FieldsList(String fieldName, String value) {
        addField(fieldName, value);
    }


    /**
     * Constructeur .Ajoute tous les champs contenus dans <code>fields</code> a ce nouveau FieldsList.
     *
     * @param fields Map de (key = nom du champ, value(String) = valeur du champs )
     */
    public FieldsList(Map<String, String> fields) {
        addAllField(fields);
    }


    public void setFieldValue(String fieldName, String value) {
        for (Field item : getFields()) {
            if (fieldName.equals(item.getName())) {
                item.setValue(value);
                return;
            }
        }
        APP.error("setFieldValue(" + fieldName + "," + value + ") champs inexistant");
        APP.error("\tChamps definit = " + fields);
        throw new IllegalArgumentException("Le champs n'est pas defini : " + fieldName);
    }


    @Deprecated
    public void setFields(java.util.Collection fields) {
        // UGLY : méthode necessaire pour castor (mapping xml)
        //noinspection unchecked
        setFields((ArrayList)fields);
    }


    public void setFields(List<Field> fields) {
        this.fields = fields;
    }


    public Field getField(int idx) {
        if (fields == null) {
            throw new IllegalArgumentException("Pas de field ");
        }
        return fields.get(idx);
    }


    public int getFieldCount() {
        if (fields == null) {
            return 0;
        }
        return fields.size();
    }


    public String getFieldValue(String fieldName) {
        if (fieldName == null) {
            throw new IllegalArgumentException("Nom de champ non renseigné");
        }

        if (fields != null) {
            for (Field item : getFields()) {
                if (fieldName.equals(item.getName())) {
                    return item.getValue();
                }
            }
        }
        APP.error("Champs présents : " + toString());
        throw new IllegalArgumentException("Le champ n'est pas défini : " + fieldName);
    }


    public List<Field> getFields() {
        return fields;
    }


    /**
     * Fusionne tous les champs contenus dans <code>newList</code> a ce FieldsList. Si le champ existe deja il
     * est mise-a-jours, sinon il est cree.
     *
     * @param newList La liste maitresse lors de la fusion
     */
    public void updateWith(FieldsList newList) {
        for (Field item : newList.getFields()) {
            if (contains(item.getName())) {
                setFieldValue(item.getName(), item.getValue());
            }
            else {
                addField(item.getName(), item.getValue());
            }
        }
    }


    /**
     * Ajoute tous les champs contenus dans <code>fieldsMap</code> a ce FieldsList.
     *
     * @param fieldsMap Map de (key = nom du champ, value(String) = valeur du champ )
     */
    public void addAllField(Map<String, String> fieldsMap) {
        for (Map.Entry<String, String> item : fieldsMap.entrySet()) {
            addField(item.getKey(), item.getValue());
        }
    }


    public void addField(String field, String value) {
        if (fields == null) {
            fields = new ArrayList<Field>();
        }
        else if (contains(field)) {
            throw new IllegalArgumentException("Le champ " + field + " existe déjà");
        }
        fields.add(new Field(field, value));
    }


    /**
     * Indique si la liste contient un champs.
     *
     * @param fieldName nom du champs
     *
     * @return <code>true</code> si le champs existe dans la liste.
     */
    public boolean contains(String fieldName) {
        if (fields == null) {
            return false;
        }
        for (Iterator<String> names = fieldNames(); names.hasNext();) {
            if (fieldName.equals(names.next())) {
                return true;
            }
        }
        return false;
    }


    public void addOrUpdateField(String field, String value) {
        if (contains(field)) {
            setFieldValue(field, value);
        }
        else {
            addField(field, value);
        }
    }


    public Iterator<String> fieldNames() {
        return new FieldNameIterator(this);
    }


    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<String, String>();
        for (Field item : getFields()) {
            map.put(item.getName(), item.getValue());
        }
        return map;
    }


    @Override
    public String toString() {
        return (fields != null) ? fields.toString() : "[empty]";
    }

/*

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        FieldsList that = (FieldsList)object;
        return (fields == null ? that.fields == null : fields.equals(that.fields));
    }


    @Override
    public int hashCode() {
        return (fields != null ? fields.hashCode() : 0);
    }

*/

    private static final class FieldNameIterator implements Iterator<String> {
        private Iterator<Field> fieldsIterator;


        FieldNameIterator(FieldsList pkFiledList) {
            fieldsIterator = pkFiledList.getFields().iterator();
        }


        public boolean hasNext() {
            return fieldsIterator.hasNext();
        }


        public String next() {
            return fieldsIterator.next().getName();
        }


        public void remove() {
            fieldsIterator.remove();
        }
    }
}
