package net.codjo.mad.gui.util;
import net.codjo.gui.toolkit.table.GroupableTableHeaderBuilder;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.client.request.Result;
import net.codjo.mad.client.request.Row;
import net.codjo.mad.gui.framework.DefaultGuiContext;
import net.codjo.mad.gui.framework.GuiContext;
import net.codjo.mad.gui.framework.LocalGuiContext;
import net.codjo.mad.gui.request.Column;
import net.codjo.mad.gui.request.Column.Sorter;
import net.codjo.mad.gui.request.ListDataSource;
import net.codjo.mad.gui.request.Preference;
import net.codjo.mad.gui.request.RequestTable;
import net.codjo.test.common.LogString;
import net.codjo.test.common.excel.ExcelUtil;
import java.awt.BorderLayout;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

public class ExportExcelBuilderTest {
    private String[] headerWith3Columns = {"lettreLabel", "chiffreLabel", "lettre_chiffreLabel"};
    private String[] headerWith2Columns = {"totoLabel", "tataLabel", ""};
    private RequestTable tableWith3Columns;
    private RequestTable tableWith2Columns;
    private ExportExcelBuilder exportExcelBuilder;
    private LogString log = new LogString();


    @Before
    public void setUp() {
        exportExcelBuilder = new ExportExcelBuilder(createGuiContext()) {
            @Override
            void openWindowsFile(File file) {
            }
        };

        createTables();
    }


    private GuiContext createGuiContext() {
        LocalGuiContext guiContext = new LocalGuiContext(new DefaultGuiContext()) {
            @Override
            public void displayInfo(String msg) {
                super.displayInfo(msg);
                log.info(msg);
            }
        };

        guiContext.putProperty(ExportUtil.FILE_BASE_NAME, "fichier");
        guiContext.putProperty(ExportUtil.FILE_EXT, ".xls");
        guiContext.putProperty(ExportUtil.DECIMAL_SEPARATOR, ",");
        return guiContext;
    }


    private void createTables() {
        tableWith3Columns = createTable(new String[]{"lettre", "chiffre", "lettre_chiffre"},
                                        new String[][]{
                                              {"a", "1", "a1"},
                                              {"b", "2", "b2"},
                                              {"c", "3", "c3"},
                                              {"d", "4", "d4"},
                                        }, "Label");

        tableWith2Columns = createTable(new String[]{"toto", "tata"},
                                        new String[][]{
                                              {"x", "9"},
                                              {"y", "8"},
                                              {"z", "7"}}, "Label");
    }


    public static void main(String[] args) {
        new ExportExcelBuilderTest().showTables();
    }


    private void showTables() {
        createTables();
        GroupableTableHeaderBuilder.install(tableWith3Columns)
              .createGroupColumn("Groupe 1", 0, 1)
              .build();

        JFrame jFrame = new JFrame("ExportExcelBuilderTest");
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.getContentPane().add(new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                                                   new JScrollPane(tableWith3Columns),
                                                   new JScrollPane(tableWith2Columns)), BorderLayout.CENTER);
        jFrame.pack();
        jFrame.setVisible(true);
    }


    @Test
    public void test_generateFile() throws Exception {
        HSSFWorkbook wb = new HSSFWorkbook();
        exportExcelBuilder.add(tableWith3Columns, ExportExcelBuilder.createColumnHeader(tableWith3Columns))
              .buildExcelData(true, wb);

        assertEquals("", log.getContent());
        exportExcelBuilder.generate(false);
        assertEquals("Export excel to " + ExportUtil.computeTempFileName("fichier.xls"), log.getContent());
    }


    @Test
    public void test_single_table() throws Exception {
        HSSFWorkbook wb = new HSSFWorkbook();
        exportExcelBuilder.add(tableWith3Columns, ExportExcelBuilder.createColumnHeader(tableWith3Columns))
              .buildExcelData(true, wb);
        assertArrayEquals(new String[][]{
              headerWith3Columns,
              {"a", "1", "a1"},
              {"b", "2", "b2"},
              {"c", "3", "c3"},
              {"d", "4", "d4"}}, extract(wb, 5, 3));
    }


    @Test
    public void test_multiple_tables() throws Exception {
        HSSFWorkbook wb = new HSSFWorkbook();
        exportExcelBuilder
              .add(tableWith3Columns, ExportExcelBuilder.createColumnHeader(tableWith3Columns))
              .add(tableWith2Columns, ExportExcelBuilder.createColumnHeader(tableWith2Columns))
              .buildExcelData(true, wb);
        assertArrayEquals(new String[][]{
              headerWith3Columns,
              {"a", "1", "a1"},
              {"b", "2", "b2"},
              {"c", "3", "c3"},
              {"d", "4", "d4"},
              headerWith2Columns,
              {"x", "9", ""},
              {"y", "8", ""},
              {"z", "7", ""},
        }, extract(wb, 9, 3));
    }


    @Test
    public void test_compareContent() throws Exception {
        LocalGuiContext context = (LocalGuiContext)createGuiContext();
        context.putProperty(ExportUtil.DECIMAL_SEPARATOR, ".");
        exportExcelBuilder = new ExportExcelBuilder(context) {
            @Override
            void openWindowsFile(File file) {
            }
        };

        RequestTable table = createTable(
              new String[]{"vl", "netAsset", "origin", "pipo"},
              new String[][]{
                    {"105201.58", "51201.95", "", "tutu"},
                    {"165012.03", "7810.58", "USA", ""},
              }, ""
        );
        table.getPreference().getColumns().get(0).setSorter("numeric");
        table.getPreference().getColumns().get(1).setSorter("numeric");

        HSSFWorkbook actualWorkbook = new HSSFWorkbook();
        exportExcelBuilder.add(table, ExportExcelBuilder.createColumnHeader(table))
              .buildExcelData(true, actualWorkbook);

        final HSSFWorkbook expectWorkbook = ExcelUtil.loadWorkbook(new File(getClass().getResource(
              "expected_content.xls").toURI()));

        ExcelUtil.compare(expectWorkbook, actualWorkbook, "Sheet0", null);
    }


    @Test
    public void test_multiple_table_with_customized_header() throws Exception {
        HSSFWorkbook wb = new HSSFWorkbook();
        exportExcelBuilder
              .add(tableWith3Columns, ExportExcelBuilder.createColumnHeader(tableWith3Columns))
              .add(tableWith2Columns)
              .buildExcelData(true, wb);
        assertArrayEquals(new String[][]{
              headerWith3Columns,
              {"a", "1", "a1"},
              {"b", "2", "b2"},
              {"c", "3", "c3"},
              {"d", "4", "d4"},
              {"x", "9", ""},
              {"y", "8", ""},
              {"z", "7", ""},
        }, extract(wb, 8, 3));
    }


    @Test
    public void test_multiple_table_with_groupable_header() throws Exception {
        HSSFWorkbook wb = new HSSFWorkbook();
        GroupableTableHeaderBuilder.install(tableWith3Columns)
              .createGroupColumn("Groupe 1", 0, 1)
              .build();
        exportExcelBuilder
              .add(tableWith3Columns, ExportExcelBuilder.createColumnHeader(tableWith3Columns))
              .add(tableWith2Columns, ExportExcelBuilder.createColumnHeader(tableWith2Columns))
              .buildExcelData(true, wb);
        assertArrayEquals(new String[][]{
              {"Groupe 1", "Groupe 1", ""},
              headerWith3Columns,
              {"a", "1", "a1"},
              {"b", "2", "b2"},
              {"c", "3", "c3"},
              {"d", "4", "d4"},
              headerWith2Columns,
              {"x", "9", ""},
              {"y", "8", ""},
              {"z", "7", ""},
        }, extract(wb, 10, 3));
    }


    @Test
    public void test_multiple_header() throws Exception {
        HSSFWorkbook wb = new HSSFWorkbook();
        GroupableTableHeaderBuilder.install(tableWith3Columns)
              .createGroupColumn("Groupe 1", 0, 1)
              .build();
        Object[][] mainHeader = ExportExcelBuilder.createColumnHeader(tableWith3Columns);
        Object[][] upperHeader = new Object[][]{{"Casse", "toi", "pauvre mec!"}};
        Object[][] lowerHeader = new Object[][]{{"pauvre fille!"}, {"t chiante"}};

        exportExcelBuilder
              .add(tableWith3Columns, upperHeader, mainHeader, lowerHeader)
              .add(tableWith2Columns, ExportExcelBuilder.createColumnHeader(tableWith2Columns))
              .buildExcelData(true, wb);
        assertArrayEquals(new String[][]{
              {"Casse", "toi", "pauvre mec!"},
              {"Groupe 1", "Groupe 1", ""},
              headerWith3Columns,
              {"pauvre fille!", "", ""},
              {"t chiante", "", ""},
              {"a", "1", "a1"},
              {"b", "2", "b2"},
              {"c", "3", "c3"},
              {"d", "4", "d4"},
              headerWith2Columns,
              {"x", "9", ""},
              {"y", "8", ""},
              {"z", "7", ""},
        }, extract(wb, 13, 3));
    }


    @Test
    public void test_multi_pages() throws Exception {
        String[][] datas = new String[][]{
              {"a", "1", "a1"},
              {"b", "2", "b2"},
              {"c", "3", "c3"},
              {"d", "4", "d4"},
        };
        String[] columnNames = new String[]{"lettre", "chiffre", "lettre_chiffre"};

        OnePageOneRowListDataSource dataSource = new OnePageOneRowListDataSource(columnNames, datas);

        tableWith3Columns = createTable(columnNames, datas, dataSource, "Label");
        dataSource.load();

        HSSFWorkbook wb = new HSSFWorkbook();
        exportExcelBuilder.add(tableWith3Columns, ExportExcelBuilder.createColumnHeader(tableWith3Columns))
              .buildExcelData(true, wb);
        assertArrayEquals(new String[][]{
              headerWith3Columns,
              {"a", "1", "a1"},
              {"b", "2", "b2"},
              {"c", "3", "c3"},
              {"d", "4", "d4"}}, extract(wb, 5, 3));
    }


    @Test
    public void test_multi_pages_disabled() throws Exception {
        String[][] datas = new String[][]{
              {"a", "1", "a1"},
              {"b", "2", "b2"},
              {"c", "3", "c3"},
              {"d", "4", "d4"},
        };
        String[] columnNames = new String[]{"lettre", "chiffre", "lettre_chiffre"};

        OnePageOneRowListDataSource dataSource = new OnePageOneRowListDataSource(columnNames, datas);

        tableWith3Columns = createTable(columnNames, datas, dataSource, "Label");
        dataSource.load();

        HSSFWorkbook wb = new HSSFWorkbook();
        exportExcelBuilder.add(tableWith3Columns, ExportExcelBuilder.createColumnHeader(tableWith3Columns))
              .buildExcelData(false, wb);
        assertArrayEquals(new String[][]{
              headerWith3Columns,
              {"a", "1", "a1"},}
              , extract(wb, 2, 3));
    }


    @Test
    public void test_decimalSeparator() throws Exception {
        RequestTable table = createTable(new String[]{"withSorter", "noSorter"},
                                         new String[][]{{"1,5", "2,0"},
                                                        {"2 345,0", "6 543,2"}}, "Label");
        table.getPreference().getColumns().get(0).setSorter("numeric");

        HSSFWorkbook wb = new HSSFWorkbook();
        exportExcelBuilder.add(table, ExportExcelBuilder.createColumnHeader(table))
              .buildExcelData(true, wb);
        assertArrayEquals(new String[][]{
              {"withSorterLabel", "noSorterLabel"},
              {"1.5", "2,0"},
              {"2345.0", "6 543,2"}}
              , extract(wb, 3, 2));
    }


    @Test
    public void test_otherDecimalSeparator() throws Exception {
        LocalGuiContext context = (LocalGuiContext)createGuiContext();
        context.putProperty(ExportUtil.DECIMAL_SEPARATOR, ":");
        exportExcelBuilder = new ExportExcelBuilder(context) {
            @Override
            void openWindowsFile(File file) {
            }
        };

        RequestTable table = createTable(new String[]{"sorter"}, new String[][]{{"1:5"}, {"2:0"}}, "Label");
        table.getPreference().getColumns().get(0).setSorter("numeric");

        HSSFWorkbook wb = new HSSFWorkbook();
        exportExcelBuilder.add(table, ExportExcelBuilder.createColumnHeader(table))
              .buildExcelData(true, wb);
        assertArrayEquals(new String[][]{{"sorterLabel"}, {"1.5"}, {"2.0"}}, extract(wb, 3, 1));
    }


    @Test
    public void test_cellFormat() throws Exception {
        String[] cols = {"sorterString", "sorterNumeric", "sorterDate", "sorterBoolean", "noSorter"};
        ListDataSource src = new ListDataSource();
        src.setColumns(cols);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date date0 = new Date(1000000000000L);
        src.addRow(createRow(cols, "-0.05", "-0.06", dateFormat.format(date0), "tRUe", "-0.07"));
        Date date1 = new Date(1500000000000L);
        src.addRow(createRow(cols, "3.14", "3.15", dateFormat.format(date1), "fAlSe", "3.16"));
        Date date2 = new Date(1800000000000L);
        src.addRow(createRow(cols, "4 567,1000", "5 678,2000", dateFormat.format(date2), "0", "2028-08-16"));
        RequestTable table = createTable(cols, new String[0][0], src, "Label");
        List<Column> columns = table.getPreference().getColumns();
        columns.get(0).setSorter(Sorter.STRING.name());
        columns.get(1).setSorter(Sorter.NUMERIC.name());
        columns.get(2).setSorter(Sorter.DATE.name());
        columns.get(3).setSorter(Sorter.BOOLEAN.name());

        table.moveColumn(0, 2); //play with view and model column indexes

        HSSFWorkbook wb = new HSSFWorkbook();
        exportExcelBuilder.add(table, ExportExcelBuilder.createColumnHeader(table))
              .buildExcelData(true, wb);

        boolean headerSkipped = false;
        for (Iterator it = wb.getSheetAt(0).iterator(); it.hasNext();) {
            HSSFRow row = (HSSFRow)it.next();
            if (!headerSkipped) {
                row = (HSSFRow)it.next();
                headerSkipped = true;
            }
            // column indexes in Excel match column indexes in table view
            assertTrue("Expected STRING", HSSFCell.CELL_TYPE_STRING == row.getCell(2).getCellType());
            assertTrue("Expected NUMBER", HSSFCell.CELL_TYPE_NUMERIC == row.getCell(0).getCellType());
            assertTrue("Expected NUMBER", HSSFCell.CELL_TYPE_NUMERIC == row.getCell(1).getCellType());
            assertTrue("Expected BOOLEAN", HSSFCell.CELL_TYPE_BOOLEAN == row.getCell(3).getCellType());
            assertTrue("Expected STRING", HSSFCell.CELL_TYPE_STRING == row.getCell(4).getCellType());
        }

        assertArrayEquals(new String[][]{
              {"sorterNumericLabel", "sorterDateLabel", "sorterStringLabel", "sorterBooleanLabel",
               "noSorterLabel"},
              {"-0.06", String.valueOf(HSSFDateUtil.getExcelDate(date0)), "-0.05", "TRUE", "-0.07"},
              {"3.15", String.valueOf(HSSFDateUtil.getExcelDate(date1)), "3.14", "FALSE", "3.16"},
              {"5678.2", String.valueOf(HSSFDateUtil.getExcelDate(date2)), "4 567,1000", "FALSE",
               "2028-08-16"}
        }, extract(wb, 4, 5));
    }


    @Test
    public void testCreateCalendar() {
        Calendar cal = ExportExcelBuilder.createCalendar("2010-08-22");
        assertEquals(2010, cal.get(Calendar.YEAR));
        assertEquals(7, cal.get(Calendar.MONTH));
        assertEquals(22, cal.get(Calendar.DAY_OF_MONTH));
        assertEquals(0, cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(0, cal.get(Calendar.MINUTE));
        assertEquals(0, cal.get(Calendar.SECOND));
        assertEquals(0, cal.get(Calendar.MILLISECOND));

        cal = ExportExcelBuilder.createCalendar("2010-08-27 23:42:31.865");
        assertEquals(2010, cal.get(Calendar.YEAR));
        assertEquals(7, cal.get(Calendar.MONTH));
        assertEquals(27, cal.get(Calendar.DAY_OF_MONTH));
        assertEquals(23, cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(42, cal.get(Calendar.MINUTE));
        assertEquals(31, cal.get(Calendar.SECOND));
        assertEquals(865, cal.get(Calendar.MILLISECOND));

        cal = ExportExcelBuilder.createCalendar("foobar");
        assertNull(cal);
    }


    private static RequestTable createTable(String[] columnNames, String[][] values, String suffix) {
        return createTable(columnNames, values, new ListDataSource(), suffix);
    }


    private static RequestTable createTable(String[] columnNames, String[][] values,
                                            ListDataSource listDataSource, String suffix) {
        Preference preference = new Preference();
        for (String columnName : columnNames) {
            preference.getColumns().add(new Column(columnName, columnName + suffix));
        }
        RequestTable table = new RequestTable(listDataSource);
        table.setPreference(preference);
        listDataSource.setColumns(columnNames);
        for (String[] row : values) {
            listDataSource.addRow(createRow(columnNames, row));
        }
        return table;
    }


    private static Row createRow(String[] columnNames, Object... values) {
        Map<String, Object> map = new HashMap<String, Object>();
        for (int i = 0; i < columnNames.length; i++) {
            map.put(columnNames[i], values[i]);
        }
        return new Row(map);
    }


    private static String[][] extract(HSSFWorkbook wb, int lineWithHeaderLength, int columnLength) {
        int index = 0;
        HSSFSheet sheet = wb.getSheetAt(0);
        String[][] extract = new String[lineWithHeaderLength][columnLength];
        for (Iterator it = sheet.iterator(); it.hasNext();) {
            HSSFRow row = (HSSFRow)it.next();
            Iterator iter = row.iterator();
            int indexColumn = 0;
            while (iter.hasNext()) {
                HSSFCell cell = row.getCell(indexColumn);
                if (HSSFCell.CELL_TYPE_NUMERIC == cell.getCellType()) {
                    extract[index][indexColumn] = Double.toString(cell.getNumericCellValue());
                }
                else {
                    extract[index][indexColumn] = cell.toString();
                }
                indexColumn++;
                iter.next();
            }
            while (indexColumn < columnLength) {
                extract[index][indexColumn] = "";
                indexColumn++;
            }
            index++;
        }
        return extract;
    }


    private class OnePageOneRowListDataSource extends ListDataSource {
        private Row[] rows;


        private OnePageOneRowListDataSource(String[] columnNames, String[][] datas) {
            List<Row> rowList = new ArrayList<Row>();
            for (String[] data : datas) {
                rowList.add(createRow(columnNames, data));
            }
            rows = rowList.toArray(new Row[rowList.size()]);
            setPageSize(1);
        }


        @Override
        public void load() throws RequestException {
            super.load();
            setRow(rows[0]);
        }


        @Override
        public void loadNextPage() throws RequestException {
            super.loadNextPage();
            if (getCurrentPage() <= rows.length) {
                setRow(rows[getCurrentPage() - 1]);
            }
            else {
                setLoadResult(null);
            }
        }


        private void setRow(Row row) {
            Result result = new Result();
            result.addRow(row);
            setLoadResult(result);
        }


        @Override
        public int getTotalRowCount() {
            return rows.length;
        }
    }
}