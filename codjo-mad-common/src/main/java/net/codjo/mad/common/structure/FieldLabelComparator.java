package net.codjo.mad.common.structure;
import java.util.Comparator;
import java.util.Map;
/**
 * Comparateur servant à trier les champs en fonction de leur traduction.
 */
public class FieldLabelComparator implements Comparator {
    private Map traductTable;


    /**
     * Constructeur.
     *
     * @param traductTable Map contenant les traductions (key = nom sql / value = net.codjo.mad.common.structure.Structure)
     *
     * @throws IllegalArgumentException lorsque traductTable est null
     */
    public FieldLabelComparator(Map traductTable) {
        if (traductTable == null) {
            throw new IllegalArgumentException();
        }
        this.traductTable = traductTable;
    }


    /**
     * Compare deux champs
     *
     * @param field1 Le nom physique du premier champ
     * @param field2 Le nom physique du deuxième champ
     *
     * @return La comparaison / libellés des deux champs (s'ils existent)
     */
    public int compare(Object field1, Object field2) {
        String lib1;
        String lib2;
        if (traductTable.containsKey(field1)) {
            lib1 = ((Structure)traductTable.get(field1)).getLabel();
        }
        else {
            lib1 = (String)field1;
        }

        if (traductTable.containsKey(field2)) {
            lib2 = ((Structure)traductTable.get(field2)).getLabel();
        }
        else {
            lib2 = (String)field2;
        }
        return lib1.compareToIgnoreCase(lib2);
    }


    @Override
    public int hashCode() {
        return super.hashCode();
    }


    /**
     * Teste l'égalité d'un obj avec le comparateur MAIS A QUOI CA SERT ???
     *
     * @param obj Un obj
     *
     * @return Egalité ?
     */
    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }
}
