package net.codjo.mad.server.handler.requetor;
import net.codjo.database.api.DatabaseTesterFactory;
import net.codjo.database.common.api.JdbcFixture;
import net.codjo.mad.server.handler.HandlerContext;
import net.codjo.mad.server.util.ConnectionAdapterMock;
import net.codjo.test.common.XmlUtil;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.apache.xpath.XPathAPI;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class AbstractRequetorHandlerTest {
    private static final String EXPECTED_XML =
          "    <result request_id='1' totalRowCount='1'>                      "
          + "        <primarykey>                                             "
          + "            <field name='period'/>                               "
          + "            <field name='securityId'/>                           "
          + "        </primarykey>                                            "
          + "        <row>                                                    "
          + "            <field name='period'><![CDATA[200405]]></field>      "
          + "            <field name='securityId'><![CDATA[6108]]></field>    "
          + "        </row>                                                   "
          + "    </result>                                                    ";
    private JdbcFixture jdbc;
    private RequetorHandlerImpl handler;


    @Before
    public void setUp() throws Exception {
        jdbc = JdbcFixture.newFixture();
        jdbc.doSetUp();
        handler = new RequetorHandlerImpl();
    }


    @After
    public void tearDown() throws Exception {
        jdbc.doTearDown();
    }


    @Test
    public void test_optimal() throws Exception {
        createApOutstandTable();

        handler.setContext(new HandlerContextMock(jdbc.getConnection()));

        String result = handler.proceed(getNode());

        XmlUtil.assertEquals(EXPECTED_XML, result);
    }


    @Test
    public void test_bugQueryTwice() throws Exception {
        createApOutstandTable();

        handler.setContext(new HandlerContextMock(jdbc.getConnection()));

        handler.proceed(getNode());
        String result = handler.proceed(getNode());

        String expectedXml =
              "    <result request_id='1' totalRowCount='1'>                      "
              + "        <primarykey>                                             "
              + "            <field name='period'/>                               "
              + "            <field name='securityId'/>                           "
              + "        </primarykey>                                            "
              + "        <row>                                                    "
              + "            <field name='period'><![CDATA[200405]]></field>      "
              + "            <field name='securityId'><![CDATA[6108]]></field>    "
              + "        </row>                                                   "
              + "    </result>                                                    ";
        XmlUtil.assertEquals(expectedXml, result);
    }


    public static Document toDocument(final InputStream stream)
          throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new InputSource(stream));
    }


    private Node getNode()
          throws ParserConfigurationException, IOException, SAXException,
                 TransformerException {
        Document document =
              toDocument(getClass().getResourceAsStream("AbstractRequetorHandlerTest_query.xml"));

        return XPathAPI.selectSingleNode(document, "//select");
    }


    private void createApOutstandTable() throws SQLException {
        jdbc.executeUpdate(
              "create table #AP_OUTSTAND (PERIOD varchar(6) null, SECURITY_ID varchar(6) null)");
        jdbc.executeUpdate("insert into #AP_OUTSTAND values ('200405', '6108')");
    }


    private static class RequetorHandlerImpl extends AbstractRequetorHandler {
        private static final String[] PK = {"period", "securityId"};


        RequetorHandlerImpl() {
            super("#AP_OUTSTAND", PK, DatabaseTesterFactory.create().createDatabase());

            wrappers.put("period", new SqlWrapper("PERIOD"));
            wrappers.put("securityId", new SqlWrapper("SECURITY_ID"));
        }


        public String getId() {
            return "me";
        }
    }

    public class HandlerContextMock extends HandlerContext {
        private Connection connection;


        public HandlerContextMock(Connection connection) {
            this.connection = connection;
        }


        @Override
        public Connection getConnection() throws SQLException {
            return new ConnectionNoClose(connection);
        }
    }

    private static class ConnectionNoClose extends ConnectionAdapterMock {
        private Connection connection;


        ConnectionNoClose(Connection connection) {
            this.connection = connection;
        }


        @Override
        protected Connection getSubConnection() throws SQLException {
            return connection;
        }


        public void close() throws SQLException {
        }
    }
}
