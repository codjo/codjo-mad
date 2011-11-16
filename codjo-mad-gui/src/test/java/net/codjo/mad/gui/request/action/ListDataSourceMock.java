package net.codjo.mad.gui.request.action;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.gui.request.ListDataSource;
import net.codjo.test.common.LogString;

class ListDataSourceMock extends ListDataSource {
    private LogString logString;
    private boolean hasNextPage = false;
    private boolean hasPreviousPage = false;


    ListDataSourceMock(LogString logString) {
        this.logString = logString;
    }


    public void setHasPreviousPage(boolean hasPreviousPage) {
        this.hasPreviousPage = hasPreviousPage;
    }


    @Override
    public boolean hasPreviousPage() {
        return hasPreviousPage;
    }


    public void setHasNextPage(boolean hasNextPage) {
        this.hasNextPage = hasNextPage;
    }


    @Override
    public boolean hasNextPage() {
        return hasNextPage;
    }


    @Override
    public void loadPreviousPage() throws RequestException {
        logString.call("loadPreviousPage");
    }


    @Override
    public void loadNextPage() throws RequestException {
        logString.call("loadNextPage");
    }
}
