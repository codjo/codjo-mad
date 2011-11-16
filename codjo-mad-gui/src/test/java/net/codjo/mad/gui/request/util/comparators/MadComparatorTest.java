package net.codjo.mad.gui.request.util.comparators;
import static net.codjo.mad.gui.request.Column.Sorter;
import junit.framework.TestCase;
import org.junit.Test;
import java.util.Calendar;
import java.util.Date;
/**
 * Classe de test de {@link MadComparator}.
 */
public class MadComparatorTest extends TestCase {
    public void test_numeric() throws Exception {
        NumericMadComparator numericMadComparator = new NumericMadComparator(Sorter.NUMERIC.name());
        Double double1 = new Double("1.5");
        Double double2 = new Double("-1.7");
        Double double3 = new Double("0");
        assertEquals(-1, numericMadComparator.compare(double2, double3));
        assertEquals(0, numericMadComparator.compare(double3, double3));
        assertEquals(1, numericMadComparator.compare(double1, double2));
        assertEquals(-1, numericMadComparator.compare("", double3));
        assertEquals(0, numericMadComparator.compare("", ""));
    }


    public void test_boolean() throws Exception {
        BooleanMadComparator booleanMadComparator = new BooleanMadComparator(Sorter.BOOLEAN.name());
        String boolean1 = "yes";
        String boolean2 = "0";
        String boolean3 = "false";
        assertEquals(-1, booleanMadComparator.compare(boolean2, boolean1));
        assertEquals(0, booleanMadComparator.compare(boolean2, boolean3));
        assertEquals(1, booleanMadComparator.compare(boolean1, boolean3));
        assertEquals(0, booleanMadComparator.compare("", boolean3));
        assertEquals(0, booleanMadComparator.compare("", ""));
    }


    public void test_date() throws Exception {
        DateMadComparator dateMadComparator = new DateMadComparator(Sorter.DATE.name());
        String date1 = "2005-10-06";
        String date2 = "1900-01-01";
        assertEquals(-1, dateMadComparator.compare(date2, date1));
        assertEquals(0, dateMadComparator.compare(date2, date2));
        assertEquals(1, dateMadComparator.compare(date1, date2));

        String string1 = "a";
        String string2 = "1";
        assertEquals(0, dateMadComparator.compare(string1, string2));
        assertEquals(0, dateMadComparator.compare(string2, string2));
        assertEquals(0, dateMadComparator.compare(string2, string1));
    }


    public void test_dateWithNull() throws Exception {
        DateMadComparator dateMadComparator = new DateMadComparator(Sorter.DATE.name());
        String date1 = "2005-10-06";
        String nullDate = "null";
        assertEquals(-1, dateMadComparator.compare(nullDate, date1));
        assertEquals(0, dateMadComparator.compare(nullDate, nullDate));
        assertEquals(1, dateMadComparator.compare(date1, nullDate));
    }



    public void test_string() throws Exception {
        StringMadComparator stringMadComparator = new StringMadComparator(Sorter.STRING.name());
        String string1 = "a";
        String string2 = "1";
        assertEquals(1, stringMadComparator.compare(string1, string2));
        assertEquals(0, stringMadComparator.compare(string2, string2));
        assertEquals(-1, stringMadComparator.compare(string2, string1));
    }


    public void test_stringWithNull() throws Exception {
        StringMadComparator stringMadComparator = new StringMadComparator(Sorter.STRING.name());
        String str = "a";
        String nullString = "null";
        assertEquals(-1, stringMadComparator.compare(nullString, str));
        assertEquals(0, stringMadComparator.compare(nullString, nullString));
        assertEquals(1, stringMadComparator.compare(str, nullString));
    }


    public void test_string_upper_case() throws Exception {
        StringMadComparator stringMadComparator = new StringMadComparator(Sorter.STRING.name());
        String string1 = "AL";
        String string2 = "Ac";
        assertEquals(1, stringMadComparator.compare(string1, string2));
        assertEquals(0, stringMadComparator.compare(string2, string2));
        assertEquals(-1, stringMadComparator.compare(string2, string1));
    }
}
