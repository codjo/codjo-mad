package net.codjo.mad.gui.util;
import net.codjo.mad.client.request.Field;
import net.codjo.mad.client.request.Row;
import net.codjo.mad.gui.request.PreferenceFactory;
import net.codjo.mad.gui.request.RequestTable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import org.uispec4j.Key;
import org.uispec4j.Table;
import org.uispec4j.UISpecTestCase;
import org.uispec4j.utils.KeyUtils;
import org.xml.sax.InputSource;

public class SelectRowKeyAdapterTest extends UISpecTestCase {
    private static final String PREFERENCE =
          "<?xml version=\"1.0\"?>                                             "
          + "<preferenceList>                                                  "
          + "  <preference id=\"myList\">                                      "
          + "        <selectAll>selectAllMyList</selectAll>                    "
          + "        <selectByPk>selectMyListById</selectByPk>                 "
          + "        <column fieldName=\"code\" label=\"My code\" />           "
          + "        <column fieldName=\"label\" label=\"My label\"/>          "
          + "    </preference>                                                 "
          + "</preferenceList>                                                 ";
    private RequestTable table;
    private Table tableTest;


    public void test_selectionByKeyboard() {
        table.getDataSource().addRow(getRow("A1", "Code 1"));
        table.getDataSource().addRow(getRow("B1", "Code 2"));
        table.getDataSource().addRow(getRow("a1", "Code 3"));

        tableTest.selectRow(0);

        KeyUtils.pressKey(tableTest.getJTable(), Key.B);

        assertTrue(tableTest.selectionEquals(new boolean[][]{
              {false},
              {true},
              {false}
        }));

        KeyUtils.pressKey(tableTest.getJTable(), Key.Z);

        assertTrue(tableTest.selectionEquals(new boolean[][]{
              {false},
              {true},
              {false}
        }));

        KeyUtils.pressKey(tableTest.getJTable(), Key.shift(Key.A));

        assertTrue(tableTest.selectionEquals(new boolean[][]{
              {true},
              {false},
              {false}
        }));

        KeyUtils.pressKey(tableTest.getJTable(), Key.A);

        assertTrue(tableTest.selectionEquals(new boolean[][]{
              {false},
              {false},
              {true}
        }));
    }


    private static Row getRow(String code, String label) {
        Row row = new Row();
        List<Field> fields = new ArrayList<Field>();
        fields.add(new Field("code", code));
        fields.add(new Field("label", label));

        row.setFields(fields);
        return row;
    }


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        PreferenceFactory.loadMapping(new InputSource(new StringReader(PREFERENCE)));
        table = new RequestTable();
        table.setPreference("myList");
        table.addKeyListener(new SelectRowKeyAdapter("code"));
        tableTest = new Table(table);
    }
}
