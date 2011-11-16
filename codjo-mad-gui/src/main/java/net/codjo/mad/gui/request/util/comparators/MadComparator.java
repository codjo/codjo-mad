package net.codjo.mad.gui.request.util.comparators;
import java.lang.reflect.Constructor;
import java.util.Comparator;

import static net.codjo.mad.gui.request.Column.Sorter;

public abstract class MadComparator implements Comparator {
    private String comparatorParameter;


    protected MadComparator(String type) {
        this.comparatorParameter = type;
    }


    public String getComparatorParameter() {
        return comparatorParameter;
    }


    public static Comparator newInstance(String type) {
        if (type == null) {
            return new StringMadComparator(type);
        }
        Sorter st = Sorter.getType(type);
        if (st != null) {
            switch (st) {
                case STRING:
                    return new StringMadComparator(st.name());
                case NUMERIC:
                    return new NumericMadComparator(st.name());
                case BOOLEAN:
                    return new BooleanMadComparator(st.name());
                case DATE:
                    return new DateMadComparator(st.name());
                default:
                    return newDynamicMadComparator(type);
            }
        }
        else {
            return newDynamicMadComparator(type);
        }
    }


    private static Comparator newDynamicMadComparator(String type) {
        int endIndex = type.indexOf("(");
        if (endIndex == -1) {
            endIndex = type.length();
        }
        Class clazz = null;
        try {
            clazz = Class.forName(type.substring(0, endIndex));
            Constructor constructor = clazz.getConstructor(String.class);
            return (MadComparator)constructor.newInstance(type);
        }
        catch (NoSuchMethodException e) {
            try {
                return (MadComparator)clazz.newInstance();
            }
            catch (Exception e1) {
                return new StringMadComparator(Sorter.STRING.name());
            }
        }
        catch (Exception e) {
            return new StringMadComparator(Sorter.STRING.name());
        }
    }
}
