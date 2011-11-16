package net.codjo.mad.gui.request.wrapper;
import net.codjo.gui.toolkit.date.TimestampDateField;
import java.sql.Timestamp;
import junit.framework.TestCase;
/**
 * DOCUMENT ME!
 *
 * @author $Author: palmont $
 * @version $Revision: 1.3 $
 */
public class TimestampDateFieldWrapperTest extends TestCase {
    public TimestampDateFieldWrapperTest(String str) {
        super(str);
    }


    public void test_wrapp() throws Exception {
        TimestampDateField comp = new TimestampDateField();
        Timestamp date = Timestamp.valueOf("1970-01-06 12:05:00.25");
        comp.setDate(date);

        GuiWrapper wrapper = new TimestampDateFieldWrapper("dbfield", comp);

        assertEquals("1970-01-06 12:05:00.25", wrapper.getXmlValue());

        wrapper.setXmlValue("1970-01-07 12:05:00.25");
        assertEquals("1970-01-07 12:05:00.25", comp.getDate().toString());
    }


    public void test_getDisplayValue() throws Exception {
        TimestampDateField comp = new TimestampDateField();
        Timestamp date = Timestamp.valueOf("1970-01-06 12:05:00.25");
        comp.setDate(date);

        GuiWrapper wrapper = new TimestampDateFieldWrapper("dbfield", comp);

        assertEquals("1970-01-06 12:05:00.25", wrapper.getDisplayValue());

        wrapper.setXmlValue(AbstractWrapper.NULL);
        assertEquals("", wrapper.getDisplayValue());
    }
}
