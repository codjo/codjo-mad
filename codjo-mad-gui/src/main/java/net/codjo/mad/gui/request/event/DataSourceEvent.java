package net.codjo.mad.gui.request.event;
import net.codjo.mad.client.request.Result;
import net.codjo.mad.gui.request.DataSource;
import net.codjo.mad.gui.request.util.MultiRequestsHelper;
import java.util.EventObject;
/**
 * Evenement sur un DataSource.
 *
 * @author $Author: gaudefr $
 * @version $Revision: 1.6 $
 *
 * @see net.codjo.mad.gui.request.event.DataSourceListener
 */
public class DataSourceEvent extends EventObject {
    private Result result;
    private MultiRequestsHelper multiRequestsHelper;

    public DataSourceEvent(DataSource ds, Result result) {
        super(ds);
        this.result = result;
    }


    public DataSourceEvent(DataSource ds, MultiRequestsHelper helper) {
        super(ds);
        this.multiRequestsHelper = helper;
    }

    public DataSource getDataSource() {
        return (DataSource)getSource();
    }


    public Result getResult() {
        return result;
    }


    public MultiRequestsHelper getMultiRequestHelper() {
        return multiRequestsHelper;
    }
}
