package net.codjo.mad.gui.framework;
import net.codjo.mad.client.plugin.MadConnectionOperations;
import net.codjo.mad.client.request.CommandRequest;
import net.codjo.mad.client.request.DeleteRequest;
import net.codjo.mad.client.request.FieldsList;
import net.codjo.mad.client.request.InsertRequest;
import net.codjo.mad.client.request.Request;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.client.request.Result;
import net.codjo.mad.client.request.Row;
import net.codjo.mad.client.request.SelectRequest;
import net.codjo.mad.client.request.SqlRequest;
import net.codjo.mad.client.request.UpdateRequest;
/**
 *
 */
public class Sender {
    private MadConnectionOperations operations;


    public Sender() {
    }


    public Sender(MadConnectionOperations operations) {
        this.operations = operations;
    }


    public Result insertRow(String handlerId, FieldsList row) throws RequestException {
        return operations.sendRequest(new InsertRequest(handlerId, row));
    }


    public Row selectRow(String handlerId, String selectorName, String selectorValue)
          throws RequestException {
        SelectRequest request = new SelectRequest(handlerId, new FieldsList(selectorName, selectorValue));
        return selectRow(request);
    }


    public Row selectRow(String handlerId, FieldsList selector) throws RequestException {
        SelectRequest request = new SelectRequest(handlerId, selector);
        return selectRow(request);
    }


    public Row selectRow(Request request) throws RequestException {
        return assertOneRow(operations.sendRequest(request));
    }


    public Result updateRow(String handlerId, FieldsList primaryKey, FieldsList newValues)
          throws RequestException {
        UpdateRequest request = new UpdateRequest(handlerId, primaryKey, newValues);
        return operations.sendRequest(request);
    }


    public Result deleteRow(String handlerId, FieldsList primaryKey) throws RequestException {
        DeleteRequest request = new DeleteRequest(handlerId, primaryKey);
        return operations.sendRequest(request);
    }


    public Result executeSqlHandler(String handlerId, FieldsList arguments) throws RequestException {
        SqlRequest request = new SqlRequest(handlerId, arguments);
        return operations.sendRequest(request);
    }


    public Result executeCommand(String handlerId, FieldsList arguments) throws RequestException {
        CommandRequest request = new CommandRequest(handlerId, arguments);
        return operations.sendRequest(request);
    }


    public Result send(Request request) throws RequestException {
        return operations.sendRequest(request);
    }


    public MadConnectionOperations getConnectionOperations() {
        return operations;
    }


    private static Row assertOneRow(Result rs) throws RequestException {
        if (rs.getRowCount() != 1) {
            throw new RequestException("La requête renvoie " + rs.getRowCount()
                                       + " ligne(s) alors qu'une seule ligne était attendue");
        }
        return rs.getRow(0);
    }
}
