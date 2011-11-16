package net.codjo.mad.server.handler.sql;
import net.codjo.database.api.Database;
import net.codjo.database.api.query.PreparedQuery;
import net.codjo.database.api.query.PreparedSelectQuery;
import net.codjo.database.api.query.SelectResult;
import net.codjo.database.api.query.SqlAdapter;
import net.codjo.mad.server.handler.AbstractHandler;
import net.codjo.mad.server.handler.AbstractHandlerMapBuilder;
import net.codjo.mad.server.handler.HandlerException;
import net.codjo.mad.server.handler.XMLUtils;
import net.codjo.mad.server.handler.util.QueryUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.apache.log4j.Logger;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class SqlHandler extends AbstractHandler {
    private static final Logger APP = Logger.getLogger(SqlHandler.class);
    private final Map<String, Getter> getters = new HashMap<String, Getter>();
    private final Set<String> primaryKeys = new TreeSet<String>();
    private final String selectSqlQuery;
    private final boolean inTransaction;
    private final String headerPk;
    private final Database database;


    protected SqlHandler(String[] pks, String selectQuery, Database database) {
        this(pks, selectQuery, false, database);
    }


    protected SqlHandler(String[] pks,
                         String selectQuery,
                         boolean inTransaction,
                         Database database) {
        this.selectSqlQuery = selectQuery;
        this.inTransaction = inTransaction;
        this.database = database;

        primaryKeys.addAll(Arrays.asList(pks));

        StringBuilder buffer = new StringBuilder("<primarykey>");
        for (String primaryKey : primaryKeys) {
            buffer.append("<field name=\"").append(primaryKey).append("\"/>");
        }
        buffer.append("</primarykey>");
        headerPk = buffer.toString();
    }


    public String proceed(Node node) throws HandlerException {
        try {
            Map<String, String> fields = getSelectorFields(node);
            fields.putAll(getPrimaryKeyFields(node));
            fields.putAll(getRowFields(node));
            String[] propertyNames = buildPropertyNames(node);

            return proceedSqlQuery(node, fields, propertyNames);
        }
        catch (Exception ex) {
            throw new HandlerException(ex);
        }
    }


    public Connection getConnection() throws SQLException {
        if (inTransaction) {
            return getContext().getTxConnection();
        }
        else {
            return getContext().getConnection();
        }
    }


    public String getId() {
        return AbstractHandlerMapBuilder.getIdFromHandlerName(getClass().getName());
    }


    protected void clearGetters() {
        getters.clear();
    }


    protected void addGetter(String fieldName, Getter getter) {
        getters.put(fieldName, getter);
    }


    protected void fillQuery(PreparedQuery query, Map<String, String> arguments) throws SQLException {
    }


    protected String buildQuery(Map<String, String> arguments) throws HandlerException {
        return QueryUtil.replaceUser(selectSqlQuery, getContext().getUser());
    }


    private String proceedSqlQuery(final Node node,
                                   final Map<String, String> arguments,
                                   final String[] propertyNames)
          throws SQLException, SAXException, HandlerException {
        Connection con = getConnection();
        try {
            final String sqlQuery = buildQuery(arguments);
            APP.debug(sqlQuery);

            if (propertyNames.length > 0) {
                PreparedSelectQuery selectQuery = database.preparedSelectQuery(con, sqlQuery);
                selectQuery.setPage(XMLUtils.determinePage(node));

                fillQuery(selectQuery, arguments);

                SelectResult results = selectQuery.execute();

                StringBuffer response = new StringBuffer("");
                while (results.next()) {
                    addRow(response, results, propertyNames);
                }
                results.close();

                return buildResponseHeader(node, results.getTotalRowCount()) + response.append("</result>");
            }
            else {
                PreparedStatement statement = null;
                try {
                    statement = con.prepareStatement(sqlQuery);
                    fillQuery(SqlAdapter.wrap(statement), arguments);
                    statement.executeUpdate();
                    return buildResponseHeader(node, statement.getUpdateCount()) + "</result>";
                }
                finally {
                    if (statement != null) {
                        statement.close();
                    }
                }
            }
        }
        finally {
            con.close();
        }
    }


    private Map<String, String> getSelectorFields(final Node node) throws SAXException {
        if (!XMLUtils.hasNode(node, "selector")) {
            return new HashMap<String, String>();
        }
        return XMLUtils.getSelectors(node);
    }


    private Map<String, String> getPrimaryKeyFields(final Node node) throws SAXException {
        if (!XMLUtils.hasNode(node, "primarykey")) {
            return new HashMap<String, String>();
        }
        return XMLUtils.getPrimaryKeys(node);
    }


    private Map<String, String> getRowFields(final Node node) throws SAXException {
        if (!XMLUtils.hasNode(node, "row")) {
            return new HashMap<String, String>();
        }
        return XMLUtils.getRowFields(node);
    }


    private String getValue(ResultSet rs, String propertyName) throws SQLException {
        Getter getter = getters.get(propertyName);
        if (getter == null) {
            throw new IllegalArgumentException("La property " + propertyName + " est inconnue");
        }
        return getter.get(rs);
    }


    private String buildResponseHeader(Node node, int totalRowCount) throws SAXException {
        String requestId = XMLUtils.getAttribute(node, "request_id");
        return "<result request_id=\"" + requestId
               + "\"  totalRowCount=\"" + totalRowCount + "\" >" + headerPk;
    }


    private void addRow(StringBuffer buffer, ResultSet rs, String[] propertyNames)
          throws SQLException {
        buffer.append("<row>");
        for (String propertyName : propertyNames) {
            buffer.append("<field name=\"").append(propertyName).append("\">")
                  .append(getValue(rs, propertyName)).append("</field>");
        }
        buffer.append("</row>");
    }


    private String[] buildPropertyNames(Node selectNode) {
        try {
            Node attributes = XMLUtils.getNode(selectNode, "attributes");
            List<Node> fieldsList = XMLUtils.getNodeList(attributes, "name");
            Set<String> propertyNames = new TreeSet<String>();

            for (Node aFieldsList : fieldsList) {
                String propertyName = aFieldsList.getFirstChild().getNodeValue();
                propertyNames.add(propertyName);
            }

            propertyNames.addAll(primaryKeys);

            return propertyNames.toArray(new String[propertyNames.size()]);
        }
        catch (SAXException ex) {
            // Pas de balise attributes ou name. Dans ce cas on renvoie tout.
            return getters.keySet().toArray(new String[getters.keySet().size()]);
        }
    }
}
