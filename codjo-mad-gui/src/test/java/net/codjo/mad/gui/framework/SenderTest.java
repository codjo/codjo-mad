package net.codjo.mad.gui.framework;
import net.codjo.mad.client.plugin.MadConnectionOperationsMock;
import net.codjo.mad.client.request.CommandRequest;
import net.codjo.mad.client.request.FieldsList;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.client.request.Result;
import net.codjo.mad.client.request.Row;
import junit.framework.TestCase;
/**
 */
public class SenderTest extends TestCase {
    private MadConnectionOperationsMock operations = new MadConnectionOperationsMock();
    private Sender sender = new Sender(operations);
    private Row aRow = new Row();
    private FieldsList primaryKeys = new FieldsList();


    public void test_insertRow() throws Exception {
        operations.mockSendRequest(new Result(primaryKeys, aRow));

        Result actual = sender.insertRow("newPortfolio", new FieldsList("", ""));

        assertSame(aRow, actual.getFirstRow());
    }


    public void test_send() throws Exception {
        operations.mockSendRequest(new Result(primaryKeys, aRow));

        Result actual = sender.send(new CommandRequest("compute-performance"));

        assertSame(aRow, actual.getFirstRow());
    }


    public void test_selectRow() throws Exception {
        operations.mockSendRequest(new Result(primaryKeys, aRow));

        Row actual = sender.selectRow(new CommandRequest("compute-performance"));

        assertSame(aRow, actual);
    }


    public void test_selectOneRowFailure() throws Exception {
        operations.mockSendRequest(new Result(primaryKeys));
        try {
            sender.selectRow(new CommandRequest("compute-performance"));
            fail();
        }
        catch (RequestException ex) {
            assertEquals("La requête renvoie 0 ligne(s) alors qu'une seule ligne était attendue",
                         ex.getMessage());
        }
    }
}
