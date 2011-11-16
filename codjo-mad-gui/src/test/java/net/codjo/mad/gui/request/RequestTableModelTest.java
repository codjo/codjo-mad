package net.codjo.mad.gui.request;
import net.codjo.mad.client.request.Result;
import net.codjo.mad.client.request.Row;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
/**
 * DOCUMENT ME!
 *
 * @author $Author: galaber $
 * @version $Revision: 1.6 $
 */
public class RequestTableModelTest extends TestCase {
    private static final String[] COLUMNS = {"pimsCode", "isin", "sicovam"};
    ListDataSource lds;
    RequestTableModel model;
    Preference preference;

    public RequestTableModelTest(String str) {
        super(str);
    }

    public void test_getColumnName() {
        model.initializeModel(RequestTableModel.createColumnFields(preference));
        model.setListDataSource(lds);

        assertEquals("Code Isin", model.getColumnName(0));
        assertEquals("Code Sicovam", model.getColumnName(1));
    }


    public void test_getRowCount() {
        assertEquals(0, model.getRowCount());
        assertEquals(0, model.getColumnCount());

        model.initializeModel(RequestTableModel.createColumnFields(preference));
        model.setListDataSource(lds);

        assertEquals(2, model.getRowCount());
        assertEquals(2, model.getColumnCount());
    }


    public void test_getRowCount_noPreference() {
        assertEquals(0, model.getRowCount());
        assertEquals(0, model.getColumnCount());

        model.setListDataSource(lds);

        assertEquals(2, model.getRowCount());
        assertEquals(0, model.getColumnCount());
    }


    public void test_getValueAt() {
        model.initializeModel(RequestTableModel.createColumnFields(preference));
        model.setListDataSource(lds);

        assertEquals("isin1", model.getValueAt(0, 0));
        assertEquals("isin2", model.getValueAt(1, 0));

        assertEquals("sico1", model.getValueAt(0, 1));
        assertEquals("sico2", model.getValueAt(1, 1));
    }


    public void test_setValueAt() {
        model.initializeModel(RequestTableModel.createColumnFields(preference));
        model.setListDataSource(lds);

        try {
            model.setValueAt("bobo", 0, 0);
            fail("Le model n'est pas modifiable ! ");
        }
        catch (IllegalStateException ex) {
            ; // Impossible de modifier la colonne bobo en lecture seule
        }

        model.setEditable(true);

        assertEquals("isin1", model.getValueAt(0, 0));
        model.setValueAt("bobo", 0, 0);
        assertEquals("bobo", model.getValueAt(0, 0));

        assertEquals("sico1", model.getValueAt(0, 1));
        model.setValueAt("bobi", 0, 1);
        assertEquals("bobi", model.getValueAt(0, 1));
    }


    public void test_setValueAt_Pk() {
        preference = buildPreferenceWithDisplayedPK();
        lds = buildListDataSource();
        model.initializeModel(RequestTableModel.createColumnFields(preference));
        model.setListDataSource(lds);

        model.setEditable(true, new String[] {"sicovam"});

        assertEquals("Nombre de colonne ", 3, model.getColumnCount());
        assertEquals("Code Isin", model.getColumnName(0));
        assertTrue("Colonne isin editable", model.isCellEditable(0, 0));
        assertEquals("Code Sicovam", model.getColumnName(1));
        assertFalse("Colonne sico non editable", model.isCellEditable(0, 1));
        assertEquals("Code Pims PK", model.getColumnName(2));
        assertFalse("Colonne PK n'est pas editable", model.isCellEditable(0, 2));

        try {
            model.setValueAt("bobo", 0, 1);
            fail("La col sicovam n'est pas modifiable ! ");
        }
        catch (IllegalStateException ex) {
            ; // Impossible de modifier la colonne bobo en lecture seule
        }

        lds.getLoadResult().setPrimaryKeys(null);
        assertTrue("Colonne PK est editable", model.isCellEditable(0, 2));
    }


    public void test_setValueAt_Pk_newRow() throws Exception {
        preference = buildPreferenceWithDisplayedPK();
        lds = buildListDataSource();
        model.initializeModel(RequestTableModel.createColumnFields(preference));
        model.setListDataSource(lds);

        model.setEditable(true);

        lds.addRow(buildResult().getRow(0));

        assertEquals("Nombre de colonne ", 3, model.getColumnCount());
        assertEquals("Code Pims PK", model.getColumnName(2));
        assertFalse("Colonne PK n'est pas editable", model.isCellEditable(0, 2));
        assertTrue("Colonne PK est editable (car nouvelle ligne)",
            model.isCellEditable(lds.getRowCount() - 1, 2));
    }


    public void test_hiddenColumns() throws Exception {
        preference = buildPreferenceWithDisplayedPK();

        List<Column> list = new ArrayList<Column>();
        list.add(new Column("isin", "Code Isin"));
        list.add(new Column("pimsCode", "Code Pims PK"));
        preference.setColumns(list);
        preference.getHiddenColumns().add(new Column("sicovam", "Code sicovam"));

        lds = buildListDataSource();
        model.initializeModel(RequestTableModel.createColumnFields(preference));
        model.setListDataSource(lds);

        lds.addRow(buildResult().getRow(0));

        assertEquals("Nombre de colonne ", 3, model.getColumnCount());
        assertEquals("Code sicovam", model.getColumnName(2));
    }


    protected void setUp() {
        model = new RequestTableModel();
        preference = buildPreference();
        lds = buildListDataSource();
    }


    protected void tearDown() {}


    private ListDataSource buildListDataSource() {
        ListDataSource listDataSource = new ListDataSource();
        listDataSource.setColumns(preference.getColumnsName());
        listDataSource.setLoadResult(buildResult());
        return listDataSource;
    }


    private Preference buildPreference() {
        Preference newPreference = new Preference();
        List columnsList = new ArrayList();
        Column col1 = new Column();
        col1.setFieldName("isin");
        col1.setLabel("Code Isin");
        Column col2 = new Column();
        col2.setFieldName("sicovam");
        col2.setLabel("Code Sicovam");
        columnsList.add(col1);
        columnsList.add(col2);
        newPreference.setColumns(columnsList);
        return newPreference;
    }


    private Preference buildPreferenceWithDisplayedPK() {
        Preference newPreference = new Preference();
        List columnsList = new ArrayList();
        columnsList.add(new Column("isin", "Code Isin"));
        columnsList.add(new Column("sicovam", "Code Sicovam"));
        columnsList.add(new Column("pimsCode", "Code Pims PK"));
        newPreference.setColumns(columnsList);
        return newPreference;
    }


    private Result buildResult() {
        Result result = new Result();
        result.setPrimaryKey("pimsCode");
        result.setRows(buildRows(COLUMNS,
                new String[][] {
                    {"11", "isin1", "sico1"},
                    {"22", "isin2", "sico2"}
                }));
        return result;
    }


    private List buildRows(String[] fields, String[][] value) {
        List rowList = new ArrayList();
        for (int line = 0; line < value.length; line++) {
            Row row = new Row();
            for (int col = 0; col < fields.length; col++) {
                row.addField(fields[col], value[line][col]);
            }
            rowList.add(row);
        }
        return rowList;
    }
}
