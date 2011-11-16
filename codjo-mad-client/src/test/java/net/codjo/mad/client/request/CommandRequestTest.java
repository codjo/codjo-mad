package net.codjo.mad.client.request;
import junit.framework.TestCase;
import net.codjo.mad.common.TestHelper;
/**
 */
public class CommandRequestTest extends TestCase {

    public void test_constructor() throws Exception {
        FieldsList arguments = new FieldsList();
        String handlerId = "compute-ptf";
        CommandRequest request = new CommandRequest(handlerId, arguments);

        assertEquals(handlerId, request.getHandlerId());
        assertEquals(arguments, request.getArgs());
    }


    public void test_command() throws Exception {
        CommandRequest request = new CommandRequest();

        request.setId("commandPerf");
        request.addSelector("ac", "458");
        request.addSelector("as", "45");

        String xml =
              "<command request_id=\"" + request.getRequestId() + "\">"
              + "<id>commandPerf</id>" + "<args>"
              + TestHelper.getFieldXmlTag("ac","458")
              + TestHelper.getFieldXmlTag("as","45")
              + "</args>" + "</command>";

        assertEquals(xml, request.toXml());
    }

    public void test_tabulation() throws Exception {
        CommandRequest request = new CommandRequest();

        request.setId("tabulation");
        request.addSelector("ac", "A\tB");

        String xml =
              "<command request_id=\"" + request.getRequestId() + "\">"
              + "<id>tabulation</id><args>"
              + TestHelper.getFieldXmlTag("ac","A\tB")
              + "</args></command>";

        assertEquals(xml, request.toXml());
    }

    public void test_spaceSequence() throws Exception {
        CommandRequest request = new CommandRequest();

        request.setId("spaceSequence");
        request.addSelector("ac", "A       B");

        String xml =
              "<command request_id=\"" + request.getRequestId() + "\">"
              + "<id>spaceSequence</id><args>"
              + TestHelper.getFieldXmlTag("ac","A       B")
              + "</args></command>";

        assertEquals(xml, request.toXml());
    }
}
