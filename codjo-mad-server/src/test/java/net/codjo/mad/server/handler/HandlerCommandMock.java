/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.mad.server.handler;
import java.sql.SQLException;
/**
 *
 */
public class HandlerCommandMock extends HandlerCommand {
    public static final String MOCK_ID = "handlerCommandMock";


    @Override
    public CommandResult executeQuery(CommandQuery query)
          throws HandlerException, SQLException {
        return createResult("nothing");
    }
}
