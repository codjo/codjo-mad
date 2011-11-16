package net.codjo.mad.server.handler.sql;

import net.codjo.database.api.DatabaseTesterFactory;
import net.codjo.database.api.query.PreparedQuery;
import net.codjo.mad.server.handler.HandlerContext;
import net.codjo.security.common.api.User;
import net.codjo.security.common.api.UserMock;
import java.sql.SQLException;
import java.util.Map;
import org.w3c.dom.Node;
/**
 * Mock d'un {@link net.codjo.mad.server.handler.sql.SqlHandler}.
 */
public class HandlerSqlMock extends SqlHandler {

    public HandlerSqlMock(String userName) {
        super(new String[]{}, "", DatabaseTesterFactory.create().createDatabase());
        setContext(new HandlerContextMock(userName));
    }


    public HandlerSqlMock(HandlerContextMock handlerContextMock) {
        super(new String[]{}, "", DatabaseTesterFactory.create().createDatabase());
        setContext(handlerContextMock);
    }


    public HandlerContextMock getHandlerContextMock() {
        return (HandlerContextMock)getContext();
    }


    @Override
    public String getId() {
        return "";
    }


    @Override
    public String proceed(Node node) {
        return "";
    }


    @Override
    protected void fillQuery(PreparedQuery query, Map<String, String> arguments) throws SQLException {
    }


    public void mockIsAllowedTo(boolean value) {
        getHandlerContextMock().getUserMock().mockIsAllowedTo(value);
    }


    public class HandlerContextMock extends HandlerContext {
        private UserMock userMock = new UserMock();
        private String userName;


        HandlerContextMock(String userName) {
            this.userName = userName;
        }


        public UserMock getUserMock() {
            return userMock;
        }


        @Override
        public String getUser() {
            return userName;
        }


        @Override
        public User getUserProfil() {
            return userMock;
        }
    }
}
