/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.mad.server.handler;
import net.codjo.test.common.XmlUtil;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import junit.framework.TestCase;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
/**
 *
 */
public class HandlerCommandTest extends TestCase {
    private static final String RESULT_MOCKED = "returned result";


    public void test_simpleResult() throws Exception {
        HandlerCommand command = new SimpleHandlerCommandMock();

        Node node = createCommandQueryNode("200405");

        //noinspection deprecation
        String actual = command.proceed(node);

        assertSingleResult(RESULT_MOCKED, actual);
    }


    public void test_returnQueryArgument() throws Exception {
        HandlerCommand command = new BasicHandlerCommandMock();

        Node node = createCommandQueryNode("200405");

        //noinspection deprecation
        String actual = command.proceed(node);

        assertSingleResult("200405", actual);
    }


    public void test_useToBean() throws Exception {
        HandlerCommand command =
              new HandlerCommand() {
                  @Override
                  public CommandResult executeQuery(CommandQuery query)
                        throws HandlerException {
                      PojoWithPeriod pojoWithPeriod = new PojoWithPeriod();
                      query.toBean(pojoWithPeriod);
                      return createResult(pojoWithPeriod.getPeriod());
                  }
              };
        Node node = createCommandQueryNode("200405");

        //noinspection deprecation
        String actual = command.proceed(node);

        assertSingleResult("200405", actual);
    }


    public void test_useToBean_filterProperties() throws Exception {
        HandlerCommand command =
              new HandlerCommand() {
                  @Override
                  public CommandResult executeQuery(CommandQuery query) throws HandlerException {
                      PojoWithPeriod pojoWithPeriod = new PojoWithPeriod();
                      pojoWithPeriod.setPeriod("before-to-bean");
                      query.toBean(pojoWithPeriod, new PropertyFilter() {
                          public boolean acceptValue(String propertyName, String value) {
                              return !"period".equals(propertyName);
                          }
                      });
                      return createResult(pojoWithPeriod.getPeriod());
                  }
              };
        Node node = createCommandQueryNode("200405");

        //noinspection deprecation
        String actual = command.proceed(node);

        assertSingleResult("before-to-bean", actual);
    }


    public void test_useToBean_error() throws Exception {
        HandlerCommand command =
              new HandlerCommand() {
                  @Override
                  public CommandResult executeQuery(CommandQuery query)
                        throws HandlerException {
                      query.toBean(null);
                      return createResult("never reached");
                  }
              };
        Node node = createCommandQueryNode("200405");

        try {
            //noinspection deprecation
            command.proceed(node);
            fail();
        }
        catch (HandlerException ex) {
            assertTrue(ex.getCausedBy() instanceof NullPointerException);
        }
    }


    public void test_checkRequiredArguments() throws Exception {
        HandlerCommand command =
              new HandlerCommand() {
                  @Override
                  public CommandResult executeQuery(CommandQuery query) throws HandlerException {
                      query.checkRequiredArguments("period");
                      return createEmptyResult();
                  }
              };

        //noinspection deprecation
        command.proceed(createCommandQueryNode("period", "200405"));

        try {
            //noinspection deprecation
            command.proceed(createCommandQueryNode("notAperiod", "200405"));
            fail();
        }
        catch (HandlerException ex) {
            assertEquals("L'argument obligatoire 'period' est absent de la requête", ex.getMessage());
        }
    }


    public void test_isExecuteQueryTestable() throws Exception {
        HandlerCommand command =
              new HandlerCommand() {
                  @Override
                  public CommandResult executeQuery(CommandQuery query) {
                      return createResult(query.getArgumentString("period"));
                  }
              };

        Map<String, String> arguments = new HashMap<String, String>();
        arguments.put("period", "200405");

        HandlerCommand.CommandQuery query = HandlerCommand.createCommandQuery(arguments);

        HandlerCommand.CommandResult result = command.executeQuery(query);

        assertEquals(HandlerCommand.createResult("200405"), result);
    }


    public void test_getId() throws Exception {
        assertEquals("handlerCommandMock", new HandlerCommandMock().getId());
        assertEquals(AbstractHandlerMapBuilder.getIdFromHandlerName(HandlerCommandMock.class.getName()),
                     new HandlerCommandMock().getId());
    }


    public void test_commandResult_toString() throws Exception {
        assertEquals("200701", HandlerCommand.createResult("200701").toString());
    }


    public void test_commandResult_equals() throws Exception {
        HandlerCommand.CommandResult actual = HandlerCommand.createResult("200701");
        assertTrue(actual.equals(HandlerCommand.createResult("200701")));

        HandlerCommand.CommandResult withName = HandlerCommand.createResult("otherName", "200701");
        assertTrue(withName.equals(HandlerCommand.createResult("otherName", "200701")));

        assertFalse(actual.equals(withName));
    }


    public void test_commandResultToXml() throws Exception {

        HandlerCommand.CommandResult result = HandlerCommand.createResult("fred", "Ceci est un test");
        String expected = "<row><field name=\"fred\"><![CDATA[Ceci est un test]]></field></row>";

        assertEquals(expected, result.toXML());
    }

    ///// Méthodes privées /////


    private void assertSingleResult(String expectedValue, String actual)
          throws TransformerException, IOException {
        String expectedXml =
              "<result request_id='53'>                                      "
              + "<row>                                                       "
              + "    <field name='result'><![CDATA[" + expectedValue + "]]></field>"
              + "</row>                                                      "
              + "</result>                                                   ";
        XmlUtil.assertEquals(expectedXml, actual);
    }


    private Node createCommandQueryNode(String periodValue)
          throws ParserConfigurationException, IOException, SAXException {
        return createCommandQueryNode("period", periodValue);
    }


    private Node createCommandQueryNode(String fieldName, String fieldValue)
          throws ParserConfigurationException, IOException, SAXException {
        return XMLUtils.parse("<command request_id='53'>                                       "
                              + "    <id>MyCommand</id>                                        "
                              + "    <args>                                                    "
                              + "        <field name='" + fieldName + "'>" + fieldValue + "</field>"
                              + "    </args>                                                   "
                              + "</command>").getFirstChild();
    }

    ///// Inner classes /////

    private static class SimpleHandlerCommandMock extends HandlerCommand {
        @Override
        public CommandResult executeQuery(CommandQuery query)
              throws HandlerException, SQLException {
            return createResult(RESULT_MOCKED);
        }
    }

    private static class BasicHandlerCommandMock extends HandlerCommand {
        @Override
        public CommandResult executeQuery(CommandQuery query)
              throws HandlerException, SQLException {
            String arg = query.getArgumentString("period");
            return createResult(arg);
        }
    }

    public static class PojoWithPeriod {
        private String period;


        public String getPeriod() {
            return period;
        }


        public void setPeriod(String period) {
            this.period = period;
        }
    }
}
