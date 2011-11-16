package net.codjo.mad.gui.request.util.comparators;

/**
 *
 */
public class StringMadComparator extends MadComparator {
    public StringMadComparator(String type) {
        super(type);
    }


    public int compare(Object value1, Object value2) {
         if ("null".equals(value1) && "null".equals(value2)) {
            return 0;
        }

        if ("null".equals(value1)) {
            return -1;
        }

        if ("null".equals(value2)) {
            return 1;
        }

        int diff = ((String)value1).toUpperCase().compareTo(((String)value2).toUpperCase());
        if (diff < 0) {
            return -1;
        }
        if (diff > 0) {
            return 1;
        }
        return 0;
    }
}
