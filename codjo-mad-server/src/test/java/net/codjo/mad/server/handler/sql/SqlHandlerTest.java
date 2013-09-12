package net.codjo.mad.server.handler.sql;
import java.io.StringReader;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import net.codjo.database.api.DatabaseTesterFactory;
import net.codjo.database.api.query.PreparedQuery;
import net.codjo.database.common.api.JdbcFixture;
import net.codjo.database.common.api.structure.SqlTable;
import net.codjo.mad.server.MadConnectionManagerMock;
import net.codjo.mad.server.MadRequestContextMock;
import net.codjo.mad.server.MadTransactionMock;
import net.codjo.mad.server.handler.Handler;
import net.codjo.mad.server.handler.HandlerContext;
import net.codjo.mad.server.handler.HandlerException;
import net.codjo.mad.server.handler.SecurityContextMock;
import net.codjo.mad.server.handler.XMLUtils;
import net.codjo.mad.server.util.ConnectionNoClose;
import net.codjo.security.server.api.UserFactoryMock;
import net.codjo.test.common.XmlUtil;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class SqlHandlerTest {
    private JdbcFixture jdbc = JdbcFixture.newFixture();
    private HandlerContext handlerContext;

    private static final Logger LOG = Logger.getLogger(SqlHandlerTest.class);


    @Before
    public void setUp() throws Exception {
        jdbc.doSetUp();

        jdbc.create(SqlTable.table("TEST"),
                    "ID varchar(20) not null, VALUE varchar(20), USER_NAME varchar(20)");
        jdbc.executeUpdate("insert into TEST values ('1', 'bla1', 'user1')");
        jdbc.executeUpdate("insert into TEST values ('2', 'bla2', 'user2')");

        MadRequestContextMock madRequestContext =
              new MadRequestContextMock(new MadTransactionMock(),
                                        SecurityContextMock.userIsInAllRole(),
                                        new UserFactoryMock());

        ((MadConnectionManagerMock)madRequestContext.getConnectionManager())
              .mockGetConnection(new ConnectionNoClose(jdbc.getConnection()));

        handlerContext = new HandlerContext(madRequestContext);
        handlerContext.setUser("user1");
    }


    @After
    public void tearDown() throws Exception {
        jdbc.doTearDown();
    }


    @Test
    public void test_proceed() throws Exception {
        String request = select("");

        String expectedResult =
              "<result request_id='1'  totalRowCount='2' >"
              + "  <primarykey><field name='id'/></primarykey>"
              + "  <row><field name='id'><![CDATA[1]]></field><field name='user'><![CDATA[user1]]></field><field name='value'><![CDATA[bla1]]></field></row>"
              + "  <row><field name='id'><![CDATA[2]]></field><field name='user'><![CDATA[user2]]></field><field name='value'><![CDATA[bla2]]></field></row>"
              + "</result>";
        XmlUtil.assertEquals(expectedResult,
                             executeHandler(new SelectAllSqlHandler(), request));
    }

    @Test
    public void test_proceed_totalRowCountGreaterThanPageSize() throws Exception {
        String request = select("<page num='1' rows='1'></page>");
        SelectAllSqlHandler sqlHandler = new SelectAllSqlHandler();

        String expectedResult =
              "<result request_id='1'  totalRowCount='2' >"
              + "  <primarykey><field name='id'/></primarykey>"
              + "  <row><field name='id'><![CDATA[1]]></field><field name='user'><![CDATA[user1]]></field><field name='value'><![CDATA[bla1]]></field></row>"
              + "</result>";
        XmlUtil.assertEquals(expectedResult,
                             executeHandler(sqlHandler, request));
    }


    @Test
    public void test_proceed_query() throws Exception {
        String request = select("");

        String expectedResult =
              "<result request_id='1'  totalRowCount='1' >"
              + "  <primarykey><field name='id'/></primarykey>"
              + "  <row><field name='id'><![CDATA[2]]></field><field name='user'><![CDATA[user2]]></field><field name='value'><![CDATA[bla2]]></field></row>"
              + "</result>";
        XmlUtil.assertEquals(expectedResult,
                             executeHandler(new FactoryQuerySqlHandler(), request));
    }


    @Test
    public void test_proceed_statement() throws Exception {
        String request = select("");

        String expectedResult =
              "<result request_id='1'  totalRowCount='1' >"
              + "  <primarykey><field name='id'/></primarykey>"
              + "  <row><field name='id'><![CDATA[1]]></field><field name='user'><![CDATA[user1]]></field><field name='value'><![CDATA[bla1]]></field></row>"
              + "</result>";
        XmlUtil.assertEquals(expectedResult,
                             executeHandler(new FactoryStatementSqlHandler(), request));
    }


    @Test
    public void test_proceed_replaceUser() throws Exception {
        String request = select("<selector><field name='user'>$user$</field></selector>");
        String expectedResult =
              "<result request_id='1' totalRowCount='1' >"
              + "  <primarykey><field name='id'/></primarykey>"
              + "  <row><field name='id'><![CDATA[1]]></field><field name='user'><![CDATA[user1]]></field><field name='value'><![CDATA[bla1]]></field></row>"
              + "</result>";
        XmlUtil.assertEquals(expectedResult,
                             executeHandler(new UserSqlHandler(), request));
    }


    @Test
    public void test_proceed_update() throws Exception {
        String request = selectWithoutAttributes("<selector><field name='value'>aaa</field></selector>");
        String expectedResult =
              "<result request_id='1' totalRowCount='2' >"
              + "  <primarykey><field name='id'/></primarykey>"
              + "</result>";
        XmlUtil.assertEquals(expectedResult,
                             executeHandler(new UpdateSqlHandler(), request));
    }


    @Test
    public void test_proceed_usePkAndSelectorAndRowAsArguments() throws Exception {
        String request = select("<primarykey><field name='id'>1</field></primarykey>"
                                + "<selector><field name='user'>user1</field></selector>"
                                + "<row><field name='value'>bla1</field></row>");
        String expectedResult =
              "<result request_id='1' totalRowCount='1' >"
              + "  <primarykey><field name='id'/></primarykey>"
              + "  <row><field name='id'><![CDATA[1]]></field><field name='user'><![CDATA[bla1]]></field><field name='value'><![CDATA[user1]]></field></row>"
              + "</result>";
        XmlUtil.assertEquals(expectedResult,
                             executeHandler(new SegoSelectSqlHandler(), request));
    }


    private String select(String body) {
        return select(body, true);
    }


    private String selectWithoutAttributes(String body) {
        return select(body, false);
    }


    private String select(String body, boolean withAttributes) {
        String attributesNode = withAttributes ?
                                "<attributes><name>id</name><name>value</name><name>user</name></attributes>" :
                                "";
        return "<?xml version='1.0'?>"
               + "<requests>"
               + "<audit><user>user1</user></audit>"
               + "<select request_id='1'>"
               + "  <id>unusedInTest</id>"
               + body
               + attributesNode
               + "</select>"
               + "</requests>";
    }


    @Test(expected = HandlerException.class)
    public void test_raiseError() throws Exception {
        String dropQuery
              = "if exists (select 1 from sysobjects where id = object_id('sp_Test_Raiserror') and type = 'P')\n"
                + "   drop proc sp_Test_Raiserror\n";
        jdbc.executeUpdate(dropQuery);

        String createProcQuery
              = "create proc sp_Test_Raiserror as\n"
                + "    create table #testTable(UNIQUE_FIELD integer)\n"
                + "    create unique index X1_testTable on #testTable (UNIQUE_FIELD)\n"
                + "    insert into #testTable(UNIQUE_FIELD) values(3)\n"
                + "    insert into #testTable(UNIQUE_FIELD) values(3)";
        jdbc.executeUpdate(createProcQuery);

        String request = selectWithoutAttributes("");
        executeHandler(new RaiseErrorSqlHandler(), request);
    }


    private String executeHandler(Handler sqlHandler, String requestDoc) throws Exception {
        sqlHandler.setContext(handlerContext);
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.parse(new InputSource(new StringReader(requestDoc)));
        NodeList nodes = doc.getFirstChild().getChildNodes();
        return sqlHandler.proceed(nodes.item(1));
    }


    private class SelectAllSqlHandler extends SqlHandler {
        private String handlerId = "defaultSqlHandlerId";


        SelectAllSqlHandler() {
            super(new String[]{"id"},
                  "select ID,VALUE,USER_NAME from TEST",
                  DatabaseTesterFactory.create().createDatabase());
            addGetter("id", new Getter(1));
            addGetter("value", new Getter(2));
            addGetter("user", new Getter(3));
        }


        @Override
        protected void fillQuery(PreparedQuery query, Map<String, String> pks) {
        }


        @Override
        public String getId() {
            return handlerId;
        }
    }

    private class FactoryQuerySqlHandler extends SqlHandler {
        private String handlerId = "defaultSqlHandlerId";


        FactoryQuerySqlHandler() {
            super(new String[]{"id"},
                  "",
                  DatabaseTesterFactory.create().createDatabase());

            addGetter("id", new Getter(1));
            addGetter("value", new Getter(2));
            addGetter("user", new Getter(3));
        }


        @Override
        protected void fillQuery(PreparedQuery query, Map<String, String> pks) {
        }


        @Override
        public String getId() {
            return handlerId;
        }


        @Override
        protected String buildQuery(Map<String, String> arguments) throws HandlerException {
            return "select ID, VALUE, USER_NAME from TEST where USER_NAME = 'user2'";
        }
    }

    private class FactoryStatementSqlHandler extends SqlHandler {
        private String handlerId = "defaultSqlHandlerId";


        FactoryStatementSqlHandler() {
            super(new String[]{"id"},
                  "",
                  DatabaseTesterFactory.create().createDatabase());

            addGetter("id", new Getter(1));
            addGetter("value", new Getter(2));
            addGetter("user", new Getter(3));
        }


        @Override
        protected void fillQuery(PreparedQuery query, Map<String, String> pks) {
        }


        @Override
        public String getId() {
            return handlerId;
        }


        @Override
        protected PreparedStatement buildStatement(Map<String, String> arguments) throws HandlerException {
            PreparedStatement statement = null;
            String query = "select ID, VALUE, USER_NAME from TEST where USER_NAME = ?";
            try {
                statement = getConnection().prepareStatement(query);
                statement.setString(1, "user1");
            }
            catch (SQLException e) {
                LOG.info("Unable to create statement for query : " + query);
            }

            return statement;
        }
    }

    private class UserSqlHandler extends SqlHandler {
        private String handlerId = "userSqlHandlerId";


        UserSqlHandler() {
            super(new String[]{"id"},
                  "select ID, VALUE, USER_NAME from TEST where USER_NAME=$user$ and USER_NAME=$user$ and ID='1'",
                  DatabaseTesterFactory.create().createDatabase());

            addGetter("id", new Getter(1));
            addGetter("value", new Getter(2));
            addGetter("user", new Getter(3));
        }


        @Override
        protected void fillQuery(PreparedQuery query, Map<String, String> pks) {
        }


        @Override
        public String getId() {
            return handlerId;
        }
    }

    private class UpdateSqlHandler extends SqlHandler {
        private String handlerId = "updateSqlHandlerId";


        UpdateSqlHandler() {
            super(new String[]{"id"},
                  "update TEST set VALUE = ?",
                  DatabaseTesterFactory.create().createDatabase());
        }


        @Override
        protected void fillQuery(PreparedQuery query, Map<String, String> pks) throws SQLException {
            query.setString(1, XMLUtils.convertFromStringValue(String.class, pks.get("value")));
        }


        @Override
        public String getId() {
            return handlerId;
        }
    }

    private class SegoSelectSqlHandler extends SqlHandler {
        private String handlerId = "updateSqlHandlerId";


        SegoSelectSqlHandler() {
            super(new String[]{"id"},
                  "select ID,USER_NAME,VALUE from TEST where ID=? and USER_NAME=? and VALUE=?",
                  DatabaseTesterFactory.create().createDatabase());

            addGetter("id", new Getter(1));
            addGetter("value", new Getter(2));
            addGetter("user", new Getter(3));
        }


        @Override
        protected void fillQuery(PreparedQuery query, Map<String, String> arguments) throws SQLException {
            query.setString(1, XMLUtils.convertFromStringValue(String.class, arguments.get("id")));
            query.setString(2, XMLUtils.convertFromStringValue(String.class, arguments.get("user")));
            query.setString(3, XMLUtils.convertFromStringValue(String.class, arguments.get("value")));
        }


        @Override
        public String getId() {
            return handlerId;
        }
    }

    private class RaiseErrorSqlHandler extends SqlHandler {
        private String handlerId = "raiseErrorSqlHandlerId";


        RaiseErrorSqlHandler() {
            super(new String[]{"id"},
                  "exec sp_Test_Raiserror",
                  DatabaseTesterFactory.create().createDatabase());
        }


        @Override
        protected void fillQuery(PreparedQuery query, Map<String, String> arguments) throws SQLException {
        }


        @Override
        public String getId() {
            return handlerId;
        }
    }
}
