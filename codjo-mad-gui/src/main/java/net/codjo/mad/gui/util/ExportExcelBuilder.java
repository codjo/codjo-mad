package net.codjo.mad.gui.util;
import net.codjo.gui.toolkit.table.GroupColumn;
import net.codjo.gui.toolkit.table.GroupableTableHeader;
import net.codjo.gui.toolkit.util.ErrorDialog;
import net.codjo.mad.client.request.Field;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.client.request.Row;
import net.codjo.mad.common.WindowsHelper;
import net.codjo.mad.gui.framework.GuiContext;
import net.codjo.mad.gui.request.Column;
import net.codjo.mad.gui.request.Column.Sorter;
import net.codjo.mad.gui.request.ListDataSource;
import net.codjo.mad.gui.request.RequestTable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class ExportExcelBuilder {
    private static final Logger LOG = Logger.getLogger(ExportExcelBuilder.class);
    private static final Pattern TIMESTAMP_PATTERN = Pattern.compile(
          "(\\d\\d\\d\\d)-(\\d\\d)-(\\d\\d)( (\\d\\d):(\\d\\d):(\\d\\d)(\\.(\\d\\d\\d))?)?.*");
    private static final short HSSF_DATE_STYLE_ID = (short)14; //see HSSFDataFormat
    private static final int MAX_NUMBER_OF_FILES = 10;

    private GuiContext context;
    private List<ExportExcel> exports = new ArrayList<ExportExcel>();
    private RenderValueFunctor renderValueFunctor = new DefaultRenderValueFunctor();


    public ExportExcelBuilder(GuiContext context) {
        this.context = context;
    }


    public void addAll(RequestTable... tables) {
        for (RequestTable table : tables) {
            add(table);
        }
        exports.toArray();
    }


    public ExportExcelBuilder add(RequestTable table) {
        exports.add(new ExportExcel(table));
        return this;
    }


    public ExportExcelBuilder add(RequestTable table, Object[][]... headers) {
        ExportExcelBuilder.ExportExcel export = new ExportExcel(table);
        export.setHeaderData(headers);
        exports.add(export);
        return this;
    }


    public ExportExcelBuilder add(RequestTable table, Iterator<Row> iterator) {
        ExportExcelBuilder.ExportExcel export = new ExportExcel(table);
        export.setRowIterator(iterator);
        exports.add(export);
        return this;
    }


    public ExportExcelBuilder add(RequestTable table, Iterator<Row> iterator, Object[][]... headers) {
        ExportExcelBuilder.ExportExcel export = new ExportExcel(table);
        export.setRowIterator(iterator);
        export.setHeaderData(headers);
        exports.add(export);
        return this;
    }


    public List<RequestTable> getTables() {
        List<RequestTable> tables = new ArrayList<RequestTable>(exports.size());
        for (ExportExcel export : exports) {
            tables.add(export.getTable());
        }
        return tables;
    }


    public void generate(boolean exportAllPages) throws RequestException {
        try {
            if (exports.isEmpty()) {
                throw new RequestException("Table list is empty.");
            }

            HSSFWorkbook wb = new HSSFWorkbook();
            buildExcelData(exportAllPages, wb);
            createAndOpenExportFile(wb);
        }
        catch (RequestException ex) {
            LOG.error(ex.getMessage(), ex);
            context.displayInfo(ex.getLocalizedMessage());
            throw new RequestException(ex.getLocalizedMessage());
        }
    }


    void buildExcelData(boolean exportAllPages, HSSFWorkbook wb) throws RequestException {
        HSSFSheet sheet = wb.createSheet();
        HSSFCellStyle dateStyle = wb.createCellStyle();
        dateStyle.setDataFormat(HSSF_DATE_STYLE_ID);

        for (ExportExcel export : exports) {
            LOG.debug("Exporting table "+export.getTable().getName());
            export.setDateStyle(dateStyle);
            export.processHeader(sheet);
            export.processSorters();
            export.processDataRows(sheet);

            if (exportAllPages) {
                RequestTable table = export.getTable();
                while (table.hasNextPage()) {
                    table.loadNextPage();
                    export.processDataRows(sheet);
                }
            }
        }
    }


    private void createAndOpenExportFile(HSSFWorkbook wb) {
        try {
            final File file = ExportUtil.generateFile(context, MAX_NUMBER_OF_FILES);
            final FileOutputStream fileOutputStream = new FileOutputStream(file);
            wb.write(fileOutputStream);
            fileOutputStream.close();

            openWindowsFile(file);
            context.displayInfo("Export excel to " + file);
        }
        catch (IOException e) {
            ErrorDialog.show(exports.get(0).getTable(), "Erreur lors de l'export Excel", e);
        }
    }


    void openWindowsFile(File file) {
        WindowsHelper.openWindowsFile(file);
    }


    public void setRenderValueFunctor(RenderValueFunctor renderValueFunctor) {
        this.renderValueFunctor = renderValueFunctor;
    }


    public static Object[][] createColumnHeader(RequestTable jTable) {
        JTableHeader tableHeader = jTable.getTableHeader();
        if (tableHeader instanceof GroupableTableHeader) {
            return createGroupableColumnHeader(tableHeader, jTable);
        }
        else {
            return createDefaultColumnHeader(tableHeader);
        }
    }


    private static Object[][] createDefaultColumnHeader(JTableHeader tableHeader) {
        TableColumnModel columnModel = tableHeader.getColumnModel();
        int columnCount = columnModel.getColumnCount();
        Object[][] headerArray = new Object[1][columnCount];
        for (int colIndex = 0; colIndex < columnCount; colIndex++) {
            headerArray[0][colIndex] = columnModel.getColumn(colIndex).getHeaderValue();
        }
        return headerArray;
    }


    private static Object[][] createGroupableColumnHeader(JTableHeader tableHeader, RequestTable jTable) {
        GroupableTableHeader header = (GroupableTableHeader)tableHeader;

        int columnCount = jTable.getColumnCount();
        int headerRowCount = header.getRowCount();
        Object[][] headerArray = new Object[headerRowCount][columnCount];
        TableColumnModel columnModel = jTable.getColumnModel();
        for (int col = 0; col < columnCount; col++) {
            TableColumn tableColumn = columnModel.getColumn(col);
            int row = 0;
            for (Iterator<GroupColumn> it = header.getColumnGroups(tableColumn); it.hasNext();) {
                GroupColumn groupColumn = it.next();
                headerArray[row][col] = groupColumn.getHeaderValue();
                row++;
            }

            headerArray[headerRowCount - 1][col] = jTable.getColumnName(col);
        }
        return headerArray;
    }


    static Calendar createCalendar(String value) {
        Matcher matcher = TIMESTAMP_PATTERN.matcher(value);
        if (!matcher.matches()) {
            LOG.warn("Value is not in a valid date format (yyyy-MM-dd[ HH:mm:ss[.SSS]]): " + value);
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("Universal"));
        cal.setTimeInMillis(0L);
        try {
            cal.set(Calendar.YEAR, Integer.parseInt(matcher.group(1)));
            cal.set(Calendar.MONTH, Integer.parseInt(matcher.group(2)) - 1);
            cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(matcher.group(3)));
            if (null != matcher.group(4)) {
                cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(matcher.group(5)));
                cal.set(Calendar.MINUTE, Integer.parseInt(matcher.group(6)));
                cal.set(Calendar.SECOND, Integer.parseInt(matcher.group(7)));
                if (null != matcher.group(8)) {
                    cal.set(Calendar.MILLISECOND, Integer.parseInt(matcher.group(9)));
                }
            }
        }
        catch (NumberFormatException e) {
            LOG.warn("Could read date from string " + matcher.group(0), e);
            return null;
        }
        return cal;
    }


    private class ExportExcel {
        private RequestTable table;
        private HSSFCellStyle dateStyle;
        private Iterator<Row> rowIterator;
        private Object[][][] headerData;
        private Map<String, Sorter> sorters;


        ExportExcel(RequestTable table) {
            this.table = table;
        }


        RequestTable getTable() {
            return table;
        }


        void setDateStyle(HSSFCellStyle dateStyle) {
            this.dateStyle = dateStyle;
        }


        void setHeaderData(Object[][][] headers) {
            this.headerData = headers;
        }


        void setRowIterator(Iterator<Row> iterator) {
            this.rowIterator = iterator;
        }


        private Iterator<Row> getRowIterator() {
            if (null != rowIterator) {
                return rowIterator;
            }
            List<Row> list = new ArrayList<Row>(table.getRowCount());
            for (int row = 0; row < table.getRowCount(); row++) {
                Row renderedRow = new Row();
                for (int col = 0; col < table.getColumnCount(); col++) {
                    String field = table.getDataSource().getColumns()[table.convertColumnIndexToModel(col)];
                    String value = renderValueFunctor.getRenderedValue(table, row, col).toString();
                    renderedRow.addField(field, value);
                }
                list.add(renderedRow);
            }
            return list.iterator();
        }


        void processHeader(HSSFSheet sheet) {
            if (headerData != null) {
                int rownum = sheet.getLastRowNum();
                if (rownum != 0) {
                    rownum++;
                }
                for (Object[][] headerRow : headerData) {
                    for (Object[] headerCell : headerRow) {
                        HSSFRow excelRow = sheet.createRow(rownum);
                        for (short index = 0; index < headerCell.length; index++) {
                            if (null == headerCell[index]) {
                                continue;
                            }
                            addCellToRow(excelRow, index, String.valueOf(headerCell[index]), Sorter.STRING);
                        }
                        rownum++;
                    }
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Headers "+ Arrays.asList(headerRow));
                    }
                }
            }
        }


        void processSorters() {
            List<Column> columns = table.getPreference().getColumns();
            sorters = new HashMap<String, Sorter>(columns.size());
            for (Column column : columns) {
                Sorter value = Sorter.getType(column.getSorter());
                sorters.put(column.getFieldName(), value);
                LOG.debug(column.getFieldName()+" uses sorter "+value);
            }
        }


        void processDataRows(HSSFSheet sheet) {
            int rowCount = 0;
            for (Iterator it = getRowIterator(); it.hasNext(); rowCount++) {
                Row row = (Row)it.next();
                HSSFRow excelRow = sheet.createRow(sheet.getLastRowNum() + 1);
                for (int i = 0; i < row.getFieldCount(); i++) {
                    Field field = row.getField(i);
                    String value = field.getValue();
                    Sorter sorter = sorters.get(field.getName());
                    if (LOG.isDebugEnabled()) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("[").append(rowCount).append(",").append(i).append("] assuming type ");
                        sb.append(sorter).append(" exporting \"").append(value).append("\"");
                        LOG.debug(sb.toString());
                    }

                    if (null == sorter || Sorter.STRING == sorter) {
                        HSSFCell cell = excelRow.createCell(i, HSSFCell.CELL_TYPE_STRING);
                        cell.setCellValue(new HSSFRichTextString(value));
                    }
                    else {
                        addCellToRow(excelRow, i, value, sorter);
                    }
                }
            }
            LOG.debug("Exported "+rowCount+" rows");
        }


        private void addCellToRow(HSSFRow excelRow, int index, String value, Sorter type) {
            HSSFCell cell = createCell(excelRow, index, type);

            switch (type) {
                case BOOLEAN:
                    cell.setCellValue(Boolean.parseBoolean(value));
                    break;
                case DATE:
                    Calendar cal = createCalendar(value);
                    if (null != cal) {
                        cell.setCellValue(cal);
                    }
                    else {
                        cell.setCellValue(new HSSFRichTextString(value));
                    }
                    break;
                case NUMERIC:
                    if (null == value || ListDataSource.NULL.equals(value) || 0 == value.trim().length()) {
                        cell.setCellType(HSSFCell.CELL_TYPE_BLANK);
                        return;
                    }
                    value = value.replaceAll(" ", "");
                    if (context.hasProperty(ExportUtil.DECIMAL_SEPARATOR)) {
                        String separator = (String)context.getProperty(ExportUtil.DECIMAL_SEPARATOR);
                        if ('.' != separator.charAt(0)) {
                            value = value.replace(separator.charAt(0), '.');
                        }
                    }
                    try {
                        cell.setCellValue(Double.parseDouble(value));
                    }
                    catch (NumberFormatException e) {
                        LOG.warn("Could not parse number " + value, e);
                        cell.setCellValue(new HSSFRichTextString(value));
                    }
                    break;
                case STRING:
                default:
                    cell.setCellValue(new HSSFRichTextString(value));
            }
        }


        private HSSFCell createCell(HSSFRow row, int index, Sorter type) {
            HSSFCell cell;
            switch (type) {
                case STRING:
                    cell = row.createCell(index, HSSFCell.CELL_TYPE_STRING);
                    break;
                case NUMERIC:
                    cell = row.createCell(index, HSSFCell.CELL_TYPE_NUMERIC);
                    break;
                case BOOLEAN:
                    cell = row.createCell(index, HSSFCell.CELL_TYPE_BOOLEAN);
                    break;
                case DATE:
                    cell = row.createCell(index, HSSFCell.CELL_TYPE_NUMERIC);
                    break;
                default:
                    cell = row.createCell(index);
            }
            if (Sorter.DATE == type && null != dateStyle) {
                cell.setCellStyle(dateStyle);
            }
            return cell;
        }
    }
}