package net.codjo.mad.client.request;
import net.codjo.mad.client.plugin.MadConnectionOperations;
import junit.framework.TestCase;
/**
 *
 */
public class MadServerFixtureTest extends TestCase {
    private MadServerFixture server = new MadServerFixture();


    @Override
    protected void setUp() throws Exception {
        server.doSetUp();
    }


    @Override
    protected void tearDown() throws Exception {
        server.doTearDown();
    }


    public void test_simple() throws Exception {
        server.mockServerResult(new String[]{"result"}, new String[][]{{"ok"}});

        MadConnectionOperations operations = server.getOperations();
        Result result = operations.sendRequest(new DeleteRequest("deleteAll", new FieldsList("id", "5")));

        assertNotNull(result);
        assertEquals("ok", result.getValue(0, "result"));

        server.assertSentRequests("<requests>"
                                  + "  <delete request_id='1'>"
                                  + "    <id>deleteAll</id>"
                                  + "    <primarykey>"
                                  + "      <field name='id'>5</field>"
                                  + "    </primarykey>"
                                  + "  </delete>"
                                  + "</requests>");
    }
}
