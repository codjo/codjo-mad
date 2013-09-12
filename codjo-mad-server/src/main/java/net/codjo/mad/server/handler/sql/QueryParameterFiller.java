package net.codjo.mad.server.handler.sql;
import java.sql.SQLException;
import java.util.Map;
import net.codjo.database.api.query.PreparedQuery;
/**
 *
 */
public interface QueryParameterFiller {
    void fillQuery(PreparedQuery query, Map<String, String> arguments) throws SQLException;
}
