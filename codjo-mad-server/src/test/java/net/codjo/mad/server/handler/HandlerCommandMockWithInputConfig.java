package net.codjo.mad.server.handler;
import java.sql.SQLException;
/**
 *
 */
public class HandlerCommandMockWithInputConfig extends HandlerCommand {
    public static final String MOCK_ID = "handlerCommandMockWithInputConfig";
    private final Object inputConfig;


    public HandlerCommandMockWithInputConfig(Object inputConfig) {
        this.inputConfig = inputConfig;
    }


    @Override
    public CommandResult executeQuery(CommandQuery query)
            throws HandlerException, SQLException {
        return createResult(inputConfig);
    }
}
