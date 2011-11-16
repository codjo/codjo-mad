package net.codjo.mad.gui.request;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
/**
 * Definition d'une clef de jointure.
 */
public class JoinKeys {
    private List fatherToSonAssociations = new ArrayList();

    public JoinKeys() {}


    public JoinKeys(String field) {
        addAssociation(field);
    }


    public JoinKeys(String[] fieldList) {
        for (int idx = 0; idx < fieldList.length; idx++) {
            addAssociation(fieldList[idx]);
        }
    }

    public void addAssociation(String fatherField, String sonField) {
        fatherToSonAssociations.add(new Association(fatherField, sonField));
    }


    public void addAssociation(String field) {
        addAssociation(field, field);
    }


    public Iterator iterator() {
        return Collections.unmodifiableList(fatherToSonAssociations).iterator();
    }


    public int size() {
        return fatherToSonAssociations.size();
    }

    /**
     * Représente une association entre le datasource père et un datasource fils. Cet
     * objet ne porte que sur un seul champ.
     *
     * @see JoinKeys#addAssociation(String)
     * @see JoinKeys#addAssociation(String)
     */
    public static class Association {
        private String sonField;
        private String fatherField;

        public Association(String fatherField, String sonField) {
            this.fatherField = fatherField;
            this.sonField = sonField;
        }

        public String getSonField() {
            return sonField;
        }


        public String getFatherField() {
            return fatherField;
        }
    }
}
