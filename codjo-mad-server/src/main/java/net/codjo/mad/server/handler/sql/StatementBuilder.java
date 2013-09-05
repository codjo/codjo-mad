package net.codjo.mad.server.handler.sql;
import java.sql.PreparedStatement;
import java.util.Map;
import net.codjo.mad.server.handler.HandlerException;
/**
 *
 */
public interface StatementBuilder {
    PreparedStatement buildStatement(Map<String, String> args, SqlHandler sqlHandler) throws HandlerException;
}
