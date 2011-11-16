package net.codjo.mad.gui.request;
import net.codjo.mad.client.request.Request;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.client.request.RequestSender;
import net.codjo.mad.client.request.Result;
import net.codjo.mad.client.request.ResultManager;
import net.codjo.mad.client.request.Row;
import net.codjo.mad.gui.request.event.DataSourceSupport;
import net.codjo.mad.gui.request.factory.RequestFactory;
import net.codjo.mad.gui.request.util.MultiRequestsHelper;
import net.codjo.test.common.LogString;
import java.awt.Component;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JInternalFrame;
/**
 * Classe regroupant des classes Mocks.
 */
public class Mock {
    private Mock() {
    }


    public static void fillWithTwoRow(final Mock.ListDataSource lds) {
        Result resultLDS = new Result();
        Row bRow = new Row();
        bRow.addField("sicovamCode", "12");
        resultLDS.addRow(bRow);
        Row aRow = new Row();
        aRow.addField("sicovamCode", "12");
        resultLDS.addRow(aRow);
        lds.setLoadResult(resultLDS);
    }


    public static class ListDataSource extends net.codjo.mad.gui.request.ListDataSource {
        public boolean automaticFillAtLoad = true;
        public boolean clearHasBeenCalled = false;
        public int loadCalledNb = 0;
        public boolean loadHasBeenCalled = false;
        public int saveCalledNb = 0;
        public boolean saveHasBeenCalled = false;
        public boolean setValueBeforeLoad = true;
        public boolean setValueHasBeenCalled = false;
        public boolean saveBeforeLoad = false;
        public long saveTime = 0;


        public void setValue(int row, String columnId, String value) {
            super.setValue(row, columnId, value);
            setValueBeforeLoad = !loadHasBeenCalled;
            setValueHasBeenCalled = true;
        }


        public void clear() {
            super.clear();
            clearHasBeenCalled = true;
        }


        public void load() throws RequestException {
            if (saveHasBeenCalled) {
                saveBeforeLoad = true;
            }
            loadHasBeenCalled = true;
            loadCalledNb++;
            if (automaticFillAtLoad) {
                fillWithTwoRow(this);
            }
        }


        public void addSaveRequestTo(MultiRequestsHelper mrh) {
            saveHasBeenCalled = true;
            saveCalledNb++;
            try {
                Thread.sleep(100);
            }
            catch (InterruptedException ex) {
                ; // En cas d'echec
            }
            saveTime = System.currentTimeMillis();
        }
    }

    public static class LoadManager implements net.codjo.mad.gui.request.LoadManager {
        boolean doLoadCalled = false;
        boolean addLoadRequestToIsCalled = false;
        int addLoadRequestToCall = 0;


        public void doLoad(DataSourceSupport support)
              throws RequestException {
            support.fireBeforeLoadEvent((MultiRequestsHelper)null);
            doLoadCalled = true;
            support.fireLoadEvent((Result)null);
        }


        public void addLoadRequestTo(MultiRequestsHelper helper) {
            addLoadRequestToIsCalled = true;
            addLoadRequestToCall++;
        }
    }

    /**
     * Mock un datasource.
     */
    public static class DataSource extends AbstractDataSource {
        private final MultiRequestsHelper helper = new MultiRequestsHelper(new RequestSender());
        private int addLoadRequestToCall = 0;
        public int addSaveRequestTo_called = 0;
        public int load_called = 0;
        public int save_called = 0;
        public int clear_called = 0;
        public RequestException load_error;
        public RequestException save_error;
        public int apply_called = 0;
        public Map apply_args = new HashMap();


        public DataSource() {
            setLoadManager(new Mock.LoadManager());
        }


        public int getTotalRowCount() {
            return 0;
        }


        public void startSnapshotMode() {
        }


        public void stopSnapshotMode() {
        }


        public void setLoadResult(Result loadResult) {
        }


        public Result getLoadResult() {
            return null;
        }


        public void setUpdateFactory(RequestFactory updateFactory) {
        }


        public void setInsertFactory(RequestFactory insertFactory) {
        }


        public boolean hasBeenUpdated() {
            return false;
        }


        public void load() throws RequestException {
            load_called++;
            if (load_error != null) {
                throw load_error;
            }
        }


        public void save() throws RequestException {
            save_called++;
            if (save_error != null) {
                throw save_error;
            }
        }


        public void clear() {
            clear_called++;
        }


        public void addLoadRequestTo(MultiRequestsHelper multiRequestHelper) {
            addLoadRequestToCall++;
        }


        public void addSaveRequestTo(MultiRequestsHelper multiRequestHelper) {
            addSaveRequestTo_called++;
        }


        public void apply(String fieldName, String fieldValue) {
            apply_called++;
            apply_args.put(fieldName, fieldValue);
        }


        public void mockBeforeLoadEvent() {
            fireBeforeLoadEvent(helper);
        }


        public void mockLoadEvent() {
            fireLoadEvent(new Result());
        }


        public void mockPropertyChangeEvent() {
            firePropertyChange("property", "a", "b");
        }


        public void mockBeforeSaveEvent() {
            fireBeforeSaveEvent(helper);
        }


        public void declare(final String fieldName) {
        }


        public void mockSaveEvent() {
            fireSaveEvent(new Result());
        }


        public int getAddLoadRequestCall() {
            return addLoadRequestToCall;
        }
    }

    public static class RequestSenderMock extends RequestSender {
        private LogString logString;


        public RequestSenderMock(LogString logString) {
            this.logString = logString;
        }


        @Override
        public String buildRequests(Request[] requests) {
            logString.call("buildRequests");
            return "";
        }


        @Override
        public ResultManager send(Request request) throws RequestException {
            logString.call("send");
            return new ResultManager();
        }


        @Override
        public ResultManager send(Request[] requests) throws RequestException {
            logString.call("send");
            return new ResultManager();
        }
    }

    public static class ErrorHandler implements net.codjo.mad.gui.request.ErrorHandler {
        String errorId;
        Exception ex;


        public void handleError(String id, Exception exception) {
            this.errorId = id;
            this.ex = exception;
        }
    }

    // Mock pour l'historisation
    public static class ArchiveManagerFactory
          implements net.codjo.mad.gui.request.archive.ArchiveManagerFactory {
        private Mock.ArchiveManager lastBuiltManager;


        public net.codjo.mad.gui.request.archive.ArchiveManager newArchiveManager(
              DetailDataSource ds) {
            lastBuiltManager = new Mock.ArchiveManager(ds);
            return getLastBuiltManager();
        }


        public ArchiveManager getLastBuiltManager() {
            return lastBuiltManager;
        }
    }

    public static class ArchiveManager
          implements net.codjo.mad.gui.request.archive.ArchiveManager {
        public DetailDataSource dataSource = null;
        public boolean askArchiveDate_called = false;
        public java.sql.Date askArchiveDate_result = java.sql.Date.valueOf("2004-01-01");
        public boolean updateDSWithArchiveDate_called = false;
        public Date updateDSWithArchiveDate_arg;
        public boolean startArchive_called = false;
        public Date startArchive_arg;
        public boolean doArchive_called;
        public Date doArchive_arg_date;
        public String doArchive_arg_id;


        public ArchiveManager(DetailDataSource ds) {
            this.dataSource = ds;
        }


        public void displayWhatsNewWindow(JInternalFrame frame) {
        }


        public Date askArchiveDate(Component aFrame) {
            askArchiveDate_called = true;
            return askArchiveDate_result;
        }


        public void updateDSWithArchiveDate(Date archiveDate) {
            updateDSWithArchiveDate_called = true;
            updateDSWithArchiveDate_arg = archiveDate;
        }


        public void doArchive(String archiveId, Date archiveDate)
              throws RequestException {
            doArchive_called = true;
            doArchive_arg_date = archiveDate;
            doArchive_arg_id = archiveId;
        }


        public void startArchive(Date archiveDate) {
            startArchive_called = true;
            startArchive_arg = archiveDate;
        }
    }
}
