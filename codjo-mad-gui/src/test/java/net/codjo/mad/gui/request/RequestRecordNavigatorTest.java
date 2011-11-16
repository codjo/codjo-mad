package net.codjo.mad.gui.request;
import net.codjo.mad.client.request.Result;
import net.codjo.mad.client.request.Row;
import net.codjo.mad.gui.MadGuiContext;
import net.codjo.mad.gui.request.event.DataSourceListener;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class RequestRecordNavigatorTest {
    private RequestRecordNavigator requestRecordNavigator = new RequestRecordNavigator();
    private MadGuiContext guiContext = new MadGuiContext();


    @Test
    public void test_name() throws Exception {
        RequestTable requestTable = createRequestTable();
        requestTable.setName("Bobo");
        requestRecordNavigator.initialize(requestTable, guiContext);
        assertEquals("Bobo.RequestRecordNavigator", requestRecordNavigator.getName());
    }


    @Test

    public void test_empty() throws Exception {
        requestRecordNavigator.initialize(createRequestTable(), guiContext);
        assertFalse(requestRecordNavigator.isEnabled());
    }


    @Test

    public void test_notEmpty() throws Exception {
        RequestTable requestTable = createRequestTable();
        requestTable.setLoadResult(buildResult(10, 100));
        requestRecordNavigator.initialize(requestTable, guiContext);
        assertTrue(requestRecordNavigator.isEnabled());
    }


    @Test
    public void test_pageNumbers() throws Exception {
        RequestTable requestTable = createRequestTable();
        requestTable.setPageSize(10);
        requestTable.setLoadResult(buildResult(10, 100));
        requestRecordNavigator.initialize(requestTable, guiContext);
        assertEquals(requestRecordNavigator.getNumberOfPages(), 10);
    }


    @Test
    public void test_pageNavigation() throws Exception {
        ListDataSource dataSource =
              new ListDataSource() {
                  @Override
                  public void load() {
                      setLoadResult(buildResult(10, 100));
                  }
              };
        dataSource.setGuiContext(new MadGuiContext());
        RequestTable requestTable = new RequestTable(dataSource);

        requestTable.setPageSize(10);
        requestTable.setLoadResult(buildResult(10, 100));
        requestRecordNavigator.initialize(requestTable, guiContext);
        assertEquals(1, requestRecordNavigator.getSelectedPage());

        requestRecordNavigator.setSelectedPage(3);
        assertEquals(3, requestTable.getCurrentPage());

        requestTable.setCurrentPage(10);
        requestTable.load();
        assertEquals(10, requestRecordNavigator.getSelectedPage());
    }


    @Test
    public void test_initialize_differentTables() throws Exception {
        ListDataSourceMock dataSource1 = new ListDataSourceMock();
        requestRecordNavigator.initialize(createTable(dataSource1, 10), guiContext);

        dataSource1.assertListenerCount(1);
        assertEquals(10, requestRecordNavigator.getSelectedPage());

        ListDataSourceMock dataSource2 = new ListDataSourceMock();
        requestRecordNavigator.initialize(createTable(dataSource2, 7), guiContext);

        dataSource1.assertListenerCount(0);
        dataSource2.assertListenerCount(1);
        assertEquals(7, requestRecordNavigator.getSelectedPage());
    }


    private RequestTable createRequestTable() {
        RequestTable requestTable = new RequestTable();
        requestTable.getDataSource().setGuiContext(guiContext);
        return requestTable;
    }


    private RequestTable createTable(ListDataSourceMock dataSourceMock, int currentPage) {
        dataSourceMock.setGuiContext(new MadGuiContext());
        RequestTable requestTable = new RequestTable(dataSourceMock);
        requestTable.setPageSize(10);
        requestTable.setLoadResult(buildResult(10, 100));
        requestTable.setCurrentPage(currentPage);
        dataSourceMock.resetListenerCount();
        return requestTable;
    }


    private Result buildResult(int size, int recordCount) {
        Result result = new Result();
        for (int index = 0; index < size; index++) {
            result.addRow(new Row());
        }
        result.setTotalRowCount(recordCount);
        return result;
    }


    private static class ListDataSourceMock extends ListDataSource {
        private int listenerCount = 0;


        @Override
        public void addDataSourceListener(DataSourceListener listener) {
            listenerCount++;
        }


        @Override
        public void removeDataSourceListener(DataSourceListener listener) {
            listenerCount--;
        }


        public void resetListenerCount() {
            listenerCount = 0;
        }


        public void assertListenerCount(int actualListenerCount) {
            assertEquals(actualListenerCount, listenerCount);
        }
    }
}
