package net.codjo.mad.gui.request.util.comparators;

/**
 *
 */
public class NumericMadComparator extends MadComparator {
    public NumericMadComparator(String type) {
        super(type);
    }


    public int compare(Object value1, Object value2) {
        Double number1;
        try {
            //TODO see net.codjo.mad.gui.request.util.PreferenceRenderer.stringToNumeric(String, String)
            number1 = new Double(value1.toString().replaceAll(" ",""));
        }
        catch (NumberFormatException e) {
            number1 = Double.NEGATIVE_INFINITY;
        }
        Double number2;
        try {
            number2 = new Double(value2.toString().replaceAll(" ",""));
        }
        catch (NumberFormatException e) {
            number2 = Double.NEGATIVE_INFINITY;
        }
        return number1.compareTo(number2);
    }
}
