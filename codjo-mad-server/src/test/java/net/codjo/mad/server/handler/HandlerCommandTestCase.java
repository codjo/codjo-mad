package net.codjo.mad.server.handler;
import net.codjo.database.common.api.JdbcFixture;
import net.codjo.security.common.api.User;
import net.codjo.tokio.TokioFixture;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import junit.framework.TestCase;
import org.apache.log4j.Logger;
/**
 * @see HandlerCommand
 */
public abstract class HandlerCommandTestCase extends TestCase {
    private static final Logger LOG = Logger.getLogger(HandlerCommandTestCase.class);
    private JdbcFixture jdbc;
    private TokioFixture tokio;
    private String user;
    private User userProfile;


    public void test_getId() throws Exception {
        assertEquals(getHandlerId(), createHandlerCommand().getId());
    }


    protected void initTokio() throws Exception {
        try {
            TokioFixture fixture = new TokioFixture(getClass());
            fixture.doSetUp();
            tokio = fixture;
        }
        catch (FileNotFoundException e) {
            LOG.info("Desactivation de Tokio car aucun fichier n'est present pour le test "
                     + getClass().getSimpleName());
        }
    }


    protected void setUser(String user) {
        this.user = user;
    }


    protected void setUserProfile(User userProfile) {
        this.userProfile = userProfile;
    }


    protected abstract HandlerCommand createHandlerCommand();


    protected abstract String getHandlerId();


    @Override
    protected void setUp() throws Exception {
        initTokio();

        if (tokio != null) {
            jdbc = tokio.getJdbcFixture();
        }
        else {
            jdbc = JdbcFixture.newFixture();
            jdbc.doSetUp();
        }
    }


    @Override
    protected void tearDown() throws Exception {
        if (tokio != null) {
            tokio.doTearDown();
        }
        else {
            jdbc.doTearDown();
        }
    }


    protected HandlerCommand.CommandResult assertExecuteQuery(String storyName,
                                                              Map<String, String> arguments)
          throws SQLException, HandlerException {
        if (tokio != null) {
            tokio.insertInputInDb(storyName);
        }
        HandlerCommand command = createHandlerCommand();
        HandlerContext context =
              new HandlerContext() {
                  @Override
                  public String getUser() {
                      return user;
                  }


                  @Override
                  public User getUserProfil() {
                      return userProfile;
                  }


                  @Override
                  public Connection getConnection() throws SQLException {
                      jdbc.getConnection().setAutoCommit(true);
                      return jdbc.getConnection();
                  }


                  @Override
                  public Connection getTxConnection() {
                      try {
                          jdbc.getConnection().setAutoCommit(false);
                      }
                      catch (SQLException e) {
                          LOG.fatal(
                                "Problème lors de la désactivation de l'autocommit dans le cas d'une connexion transactionnelle :"
                                + e.getMessage());
                          throw new RuntimeException(e);
                      }
                      return jdbc.getConnection();
                  }
              };
        command.setContext(context);
        HandlerCommand.CommandResult result =
              command.executeQuery(HandlerCommand.createCommandQuery(arguments));
        Connection connection = jdbc.getConnection();
        if (!connection.getAutoCommit()) {
            connection.commit();
            connection.setAutoCommit(true);
        }

        if (tokio != null) {
            tokio.assertAllOutputs(storyName);
        }
        return result;
    }


    protected HandlerCommand.CommandResult assertExecuteQuery(Map<String, String> arguments)
          throws SQLException, HandlerException {
        return assertExecuteQuery("", arguments);
    }


    protected void assertExecuteQueryWithFailure(String expectedMessage,
                                                 String storyName,
                                                 Map<String, String> arguments) throws SQLException {
        try {
            assertExecuteQuery(storyName, arguments);
            fail();
        }
        catch (Exception e) {
            assertEquals(expectedMessage, e.getMessage());
            if (tokio != null) {
                tokio.assertAllOutputs(storyName);
            }
        }
    }


    protected void assertExecuteQueryWithFailure(String expectedMessage,
                                                 Map<String, String> arguments) throws SQLException {
        assertExecuteQueryWithFailure(expectedMessage, "", arguments);
    }


    protected JdbcFixture getJdbcFixture() {
        return jdbc;
    }


    protected Connection getConnection() {
        return jdbc.getConnection();
    }
}
