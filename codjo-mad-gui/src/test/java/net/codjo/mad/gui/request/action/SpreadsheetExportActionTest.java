package net.codjo.mad.gui.request.action;
import net.codjo.mad.gui.framework.DefaultGuiContext;
import net.codjo.mad.gui.framework.LocalGuiContext;
import net.codjo.mad.gui.request.Preference;
import net.codjo.mad.gui.request.RequestTable;
import net.codjo.mad.gui.request.factory.SelectFactory;
import net.codjo.mad.gui.util.ExportUtil;
import net.codjo.security.common.api.UserMock;
import junit.framework.TestCase;

public class SpreadsheetExportActionTest extends TestCase {
    LocalGuiContext guiContext;
    DefaultGuiContext defaultGuiContext;
    SpreadsheetExportAction exportAction;
    UserMock userMock;


    @Override
    protected void setUp() throws Exception {

        super.setUp();
        defaultGuiContext = new DefaultGuiContext();

        guiContext = new LocalGuiContext(defaultGuiContext);
        guiContext.putProperty(ExportUtil.FILE_BASE_NAME, "fichier");
        guiContext.putProperty(ExportUtil.FILE_EXT, ".ext");
    }


    private RequestTable createTable(String handlerName) {
        Preference preference = new Preference();
        preference.setSelectAll(new SelectFactory(handlerName));
        RequestTable table = new RequestTable();
        table.setPreference(preference);
        return table;
    }


    public void test_allowedActionOnSeveralTables() {
        RequestTable[] requestTables = new RequestTable[2];
        requestTables[0] = createTable("selectAllTable1");
        requestTables[1] = createTable("selectAllTable2");
        userMock = new UserMock();
        defaultGuiContext.setUser(userMock);

        assertTrue(createActionWithRights(requestTables, true, true, true).isEnabled());
        assertFalse(createActionWithRights(requestTables, false, true, true).isEnabled());
        assertFalse(createActionWithRights(requestTables, false, false, true).isEnabled());
        assertFalse(createActionWithRights(requestTables, true, false, true).isEnabled());
        assertFalse(createActionWithRights(requestTables, true, true, false).isEnabled());
        assertFalse(createActionWithRights(requestTables, false, true, false).isEnabled());
        assertFalse(createActionWithRights(requestTables, false, false, false).isEnabled());
        assertFalse(createActionWithRights(requestTables, true, false, false).isEnabled());
    }


    public void test_allowedActionForOneTable() {
        RequestTable table = createTable("selectAll");
        userMock = new UserMock();
        defaultGuiContext.setUser(userMock);

        assertTrue(createActionWithRights(table, true, true).isEnabled());
        assertFalse(createActionWithRights(table, false, false).isEnabled());
        assertFalse(createActionWithRights(table, true, false).isEnabled());
        assertFalse(createActionWithRights(table, false, true).isEnabled());
    }


    private SpreadsheetExportAction createActionWithRights(RequestTable[] requestTable,
                                                boolean handlerRight1,
                                                boolean handlerRight2,
                                                boolean actionRight) {
        userMock.mockIsAllowedTo("selectAllTable1", handlerRight1);
        userMock.mockIsAllowedTo("selectAllTable2", handlerRight2);
        userMock.mockIsAllowedTo("net.codjo.mad.gui.request.action.SpreadsheetExportAction", actionRight);

        return new SpreadsheetExportAction(guiContext, requestTable);
    }


    private SpreadsheetExportAction createActionWithRights(RequestTable requestTable,
                                                boolean handlerRight1,
                                                boolean actionRight) {
        userMock.mockIsAllowedTo("selectAll", handlerRight1);
        userMock.mockIsAllowedTo("net.codjo.mad.gui.request.action.SpreadsheetExportAction", actionRight);

        return new SpreadsheetExportAction(guiContext, requestTable);
    }
}
