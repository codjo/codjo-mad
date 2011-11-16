package net.codjo.mad.server.handler.util;
/**
 *
 */
public class QueryUtil {
    private QueryUtil() {
    }


    public static String replaceUser(String query, String user) {
        return query.replaceAll("\\$user\\$", "'" + user + "'");
    }
}
