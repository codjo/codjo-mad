package net.codjo.mad.gui.request.util.comparators;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
/**
 * Class pour comparer des dates
 */
public class DateMadComparator extends MadComparator {
    private String pattern;
    private static final Date MIN_DATE;


    static {
        Calendar calendar = Calendar.getInstance();
        calendar.set(0, 0, 0, 0, 0, 0);
        MIN_DATE = calendar.getTime();
    }


    public DateMadComparator(String type) {
        super(type);
        int beginIndex = type.indexOf("(") + 1;
        int endIndex = type.indexOf(")");
        pattern = "yyyy-MM-dd";
        if (beginIndex > 0 && endIndex > beginIndex) {
            pattern = type.substring(beginIndex, endIndex);
        }
    }


    public int compare(Object value1, Object value2) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        Date date1;
        try {
            date1 = dateFormat.parse((String)value1);
        }
        catch (ParseException e) {
            date1 = MIN_DATE;
        }
        Date date2;
        try {
            date2 = dateFormat.parse((String)value2);
        }
        catch (ParseException e) {
            date2 = MIN_DATE;
        }
        return date1.compareTo(date2);
    }
}
