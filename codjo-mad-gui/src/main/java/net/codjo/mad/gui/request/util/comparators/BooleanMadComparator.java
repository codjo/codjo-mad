package net.codjo.mad.gui.request.util.comparators;

/**
 *
 */
public class BooleanMadComparator extends MadComparator {
    public BooleanMadComparator(String comparatorParameter) {
        super(comparatorParameter);
    }

    public int compare(Object value1, Object value2) {
        int boolean1 = getBooleanValue((String)value1);
        int boolean2 = getBooleanValue((String)value2);

        /* true = 1
         * false = -1
         *     Gauche           Droite            Resultat
         *     false(-1)        false(-1)             0
         *     false(-1)        true(1)               -1
         *     true(1)          false(-1)             1
         *     true(1)          true(1)               0
         */

        // Tordu n'est ce pas ? Une fois n'est pas coutume ! :)
        return (boolean1 - boolean2) / 2;
    }


    private int getBooleanValue(String value) {
        if ("1".equals(value)
                || "vrai".equalsIgnoreCase(value)
                || "true".equalsIgnoreCase(value)
                || "yes".equalsIgnoreCase(value)) {
            return 1;
        }
        return -1;
    }
}
