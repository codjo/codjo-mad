package net.codjo.mad.gui.request.wrapper;
import net.codjo.gui.toolkit.date.DateField;
import net.codjo.gui.toolkit.date.NoNullDateField;
import net.codjo.mad.gui.framework.DefaultGuiContext;
import net.codjo.mad.gui.request.DetailDataSource;
import net.codjo.mad.gui.request.util.ButtonPanelLogic;
import java.util.Date;
import org.uispec4j.Panel;
/**
 * DOCUMENT ME!
 *
 * @author $Author: duclosm $
 * @version $Revision: 1.4 $
 */
public class DateFieldWrapperTest extends AbstractWrapperTest {
    private DateField comp;
    private GuiWrapper wrapper;


    public DateFieldWrapperTest(String str) {
        super(str);
    }


    @Override
    public void test_wrapper_manage_null_value() throws Exception {
        super.test_wrapper_manage_null_value();
        assertEquals(null, comp.getDate());
    }


    public void test_wrapp() throws Exception {
        DateField comp1 = new DateField();
        Date date = java.sql.Date.valueOf("1970-01-06");
        comp1.setDate(date);

        GuiWrapper wrapper1 = new DateFieldWrapper("dbfield", comp1);
        assertEquals("1970-01-06", wrapper1.getXmlValue());

        wrapper1.setXmlValue("2002-05-31");
        assertEquals("2002-05-31", comp1.getDate().toString());
    }


    public void test_getDisplayValue() {
        DateField comp1 = new DateField();
        GuiWrapper wrapper1 = new DateFieldWrapper("dbfield", comp1);
        comp1.setDate(java.sql.Date.valueOf("1970-01-06"));
        assertEquals("1970-01-06", wrapper1.getDisplayValue());
        comp1.setDate(null);
        assertEquals("", wrapper1.getDisplayValue());
    }


    public void test_undoRedo() throws Exception {
        NoNullDateField dateField = new NoNullDateField();
        Panel dateFieldWrapper = new Panel(dateField);

        DetailDataSource dataSource = new DetailDataSource(new DefaultGuiContext());
        dataSource.declare("date", dateField);

        ButtonPanelLogic buttonPanelLogic = new ButtonPanelLogic();
        buttonPanelLogic.setMainDataSource(dataSource);

        assertDateField(dateFieldWrapper, "", "", "");

        dateField.setDate(java.sql.Date.valueOf("2007-08-23"));

        assertDateField(dateFieldWrapper, "23", "08", "2007");

        buttonPanelLogic.getGui().getUndoButton().doClick();

        assertDateField(dateFieldWrapper, "", "", "");
    }


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        comp = new DateField();
        wrapper = new DateFieldWrapper("dbfield", comp);
    }


    @Override
    protected GuiWrapper getWrapper() {
        return wrapper;
    }


    @Override
    protected void changeValueThroughGUI() {
        comp.setDate(new Date(50000000));
    }


    @Override
    protected void changeValueThroughWrapper() {
        wrapper.setXmlValue("2002-05-31");
    }


    private void assertDateField(Panel dateFieldWrapper,
                                 String expectedDay,
                                 String expectedMonth,
                                 String expectedYear) {
        AssertionError exception;
        long begin = System.currentTimeMillis();
        do {
            try {
                assertEquals(expectedDay, dateFieldWrapper.getTextBox("date.dayField").getText());
                assertEquals(expectedMonth, dateFieldWrapper.getTextBox("date.monthField").getText());
                assertEquals(expectedYear, dateFieldWrapper.getTextBox("date.yearField").getText());
                return;
            }
            catch (AssertionError ex) {
                exception = ex;
                try {
                    Thread.sleep(100);
                }
                catch (InterruptedException e) {
                    ;
                }
            }
        }
        while (System.currentTimeMillis() - begin < 1000);

        throw exception;
    }
}
