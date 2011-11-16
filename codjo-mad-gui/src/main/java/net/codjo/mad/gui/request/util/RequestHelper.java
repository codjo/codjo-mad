package net.codjo.mad.gui.request.util;
import net.codjo.mad.client.request.FieldsList;
import net.codjo.mad.client.request.Request;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.client.request.RequestSender;
import net.codjo.mad.client.request.Result;
import net.codjo.mad.client.request.ResultManager;
import net.codjo.mad.client.request.Row;
import net.codjo.mad.client.request.SelectRequest;
import net.codjo.mad.gui.framework.GuiContext;
import net.codjo.mad.gui.request.DetailDataSource;
import net.codjo.mad.gui.request.Preference;
/**
 * Utiliser de préférence la méthode {@link net.codjo.mad.gui.framework.GuiContext#getSender()}
 */
public final class RequestHelper {
    private RequestHelper() {
    }


    public static DetailDataSource newDataSource(GuiContext guiContext,
                                                 Row row,
                                                 Result result,
                                                 Preference pref) {
        return new DetailDataSource(guiContext,
                                    new RequestSender(),
                                    result.buildPrimaryKeyListForRow(row),
                                    pref.getSelectByPk(),
                                    pref.getUpdate());
    }


    /**
     * @deprecated Utilisez {@link net.codjo.mad.gui.framework.Sender#send(net.codjo.mad.client.request.Request)}
     *             accessible via le GuiContext.
     */
    @Deprecated
    public static Row sendSimpleRequest(Request request) throws RequestException {
        return getFirstRow(sendRequest(request));
    }


    /**
     * @deprecated Utilisez {@link net.codjo.mad.gui.framework.Sender} accessible via le GuiContext.
     */
    @Deprecated
    public static Row selectRow(String handlerId, String selectorFieldName,
                                String selectorFieldValue) throws RequestException {
        SelectRequest request =
              new SelectRequest(handlerId, new FieldsList(selectorFieldName, selectorFieldValue));
        //noinspection deprecation
        return sendSimpleRequest(request);
    }


    private static Result sendRequest(Request request) throws RequestException {
        ResultManager manager = new RequestSender().send(request);

        if (manager.hasError()) {
            throw new RequestException(manager.getErrorResult());
        }

        return manager.getResult(request.getRequestId());
    }


    private static Row getFirstRow(Result rs) throws RequestException {
        if (rs.getRowCount() != 1) {
            throw new RequestException("La requête renvoie " + rs.getRowCount()
                                       + " ligne(s) alors que une seule ligne était attendue");
        }
        return rs.getRow(0);
    }
}
