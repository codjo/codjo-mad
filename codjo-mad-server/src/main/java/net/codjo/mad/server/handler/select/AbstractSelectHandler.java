package net.codjo.mad.server.handler.select;
import net.codjo.database.api.Database;
import net.codjo.database.api.query.Page;
import net.codjo.database.api.query.PreparedQuery;
import net.codjo.database.api.query.PreparedSelectQuery;
import net.codjo.database.api.query.SelectResult;
import net.codjo.mad.server.handler.AbstractHandler;
import net.codjo.mad.server.handler.HandlerException;
import net.codjo.mad.server.handler.XMLUtils;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.exolab.castor.jdo.OQLQuery;
import org.exolab.castor.jdo.QueryResults;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
/**
 *
 */
public abstract class AbstractSelectHandler<T> extends AbstractHandler {
    public final static int OQL_QUERY = 1;
    public final static int SQL_QUERY = 0;
    private final Map<String, Getter<T>> getters = new HashMap<String, Getter<T>>();
    private int queryType;
    private String selectQuery = null;
    private Database database;
    private String[] primaryKeys;
    private String primaryKeyResponseNode;


    protected AbstractSelectHandler(int queryType,
                                    String selectQuery,
                                    String[] primaryKeys,
                                    Database database) {
        this.selectQuery = selectQuery;
        this.queryType = queryType;
        this.primaryKeys = primaryKeys;
        this.database = database;
        this.primaryKeyResponseNode = buildPrimaryKeyResponseNode(primaryKeys);
    }


    public String proceed(Node node) throws HandlerException {
        try {
            Map<String, String> arguments = getSelectors(node);
            String[] propertyNames = buildPropertyNames(node);

            if (queryType == OQL_QUERY) {
                return proceedOqlQuery(node, arguments, propertyNames);
            }
            else if (queryType == SQL_QUERY) {
                return proceedSqlQuery(node, arguments, propertyNames);
            }
            else {
                throw new IllegalArgumentException("Mauvais type de requête " + queryType);
            }
        }
        catch (Exception ex) {
            throw new HandlerException(ex);
        }
    }


    protected void addGetter(String fieldName, Getter<T> getter) {
        getters.put(fieldName, getter);
    }


    protected void fillOqlQuery(OQLQuery query, Map<String, String> arguments) {
        throw new IllegalStateException("mode de requête OQL non supporté");
    }


    protected void fillSqlQuery(PreparedQuery query, Map<String, String> arguments) throws SQLException {
        throw new IllegalStateException("mode de requête SQL non supporté");
    }


    private String proceedSqlQuery(final Node node,
                                   final Map<String, String> arguments,
                                   final String[] propertyNames)
          throws SQLException, SAXException {

        Connection con = getContext().getConnection();
        try {
            StringBuilder response = new StringBuilder();
            PreparedSelectQuery query = database.preparedSelectQuery(con, selectQuery);
            query.setPage(XMLUtils.determinePage(node));
            fillSqlQuery(query, arguments);

            SelectResult results = query.execute();
            while (results.next()) {
                addRow(response, results, propertyNames);
            }
            results.close();

            response.insert(0, buildResponseHeader(node, results.getTotalRowCount()))
                  .append(response.append("</result>"));
            return response.toString();
        }
        finally {
            con.close();
        }
    }


    private String proceedOqlQuery(final Node node,
                                   final Map<String, String> arguments,
                                   final String[] propertyNames) throws Exception {

        org.exolab.castor.jdo.Database db = getContext().getDatabase();
        try {
            Connection con = getContext().getTxConnection();
            try {
                return proceedOqlQuery(con, db, node, arguments, propertyNames);
            }
            finally {
                con.close();
            }
        }
        finally {
            db.close();
        }
    }


    private String proceedOqlQuery(Connection con,
                                   org.exolab.castor.jdo.Database db,
                                   Node node,
                                   Map<String, String> arguments,
                                   String[] propertyNames) throws Exception {
        Page page = XMLUtils.determinePage(node);
        OQLQuery query = db.getOQLQuery(selectQuery);
        fillOqlQuery(query, arguments);
        StringBuilder response = new StringBuilder();
        QueryResults results = query.execute(org.exolab.castor.jdo.Database.ReadOnly);
        int idx = 0;
        while (results.hasMore()) {
            //noinspection unchecked
            T bean = (T)results.next();
            if (page.containsRow(idx)) {
                addRow(response, bean, propertyNames);
            }
            if (idx > page.getEndIndex()) {
                break;
            }
            idx++;
        }

        //noinspection deprecation
        int recordCount = database.confidential().computeRowCount(con, idx);
        return buildResponseHeader(node, recordCount) + response.append("</result>");
    }


    private String[] buildPropertyNames(Node selectNode) throws SAXException {
        try {
            Node attributes = XMLUtils.getNode(selectNode, "attributes");
            List fieldsList = XMLUtils.getNodeList(attributes, "name");
            Set<String> propertyNames = new TreeSet<String>();

            for (Object aFieldsList : fieldsList) {
                String propertyName = ((Node)aFieldsList).getFirstChild().getNodeValue();
                propertyNames.add(propertyName);
            }

            propertyNames.addAll(Arrays.asList(primaryKeys));

            return propertyNames.toArray(new String[propertyNames.size()]);
        }
        catch (SAXException ex) {
            // Pas de balise attributes ou name. Dans ce cas on renvoie tout.
            return getters.keySet().toArray(new String[getters.keySet().size()]);
        }
    }


    private String buildResponseHeader(Node node, int totalRowCount) throws SAXException {
        String requestId = XMLUtils.getAttribute(node, "request_id");
        return "<result request_id=\"" + requestId + "\" totalRowCount=\"" + totalRowCount + "\">"
               + primaryKeyResponseNode;
    }


    private String getValue(ResultSet resultSet, String propertyName) throws SQLException {
        Getter getter = getters.get(propertyName);
        if (getter == null) {
            throw new IllegalArgumentException("La property " + propertyName + " est inconnue");
        }
        return getter.get(resultSet);
    }


    private Map<String, String> getSelectors(Node node) throws SAXException {
        if (!XMLUtils.hasNode(node, "selector")) {
            return null;
        }
        return XMLUtils.getSelectors(node);
    }


    private String getValue(T bean, String propertyName) throws Exception {
        Getter<T> getter = getters.get(propertyName);
        if (getter == null) {
            throw new IllegalArgumentException("La property " + propertyName + " est inconnue");
        }
        return getter.get(bean);
    }


    private void addRow(StringBuilder builder, T bean, String[] propertyNames) throws Exception {
        builder.append("<row>");
        for (String propertyName : propertyNames) {
            builder.append("<field name=\"").append(propertyName).append("\">")
                  .append(getValue(bean, propertyName)).append("</field>");
        }
        builder.append("</row>");
    }


    private void addRow(StringBuilder builder, ResultSet resultSet, String[] propertyNames)
          throws SQLException {
        builder.append("<row>");
        for (String propertyName : propertyNames) {
            builder.append("<field name=\"").append(propertyName).append("\">")
                  .append(getValue(resultSet, propertyName)).append("</field>");
        }
        builder.append("</row>");
    }


    private static String buildPrimaryKeyResponseNode(String[] primaryKeys) {
        StringBuilder builder = new StringBuilder("<primarykey>");
        for (String primaryKey : primaryKeys) {
            builder.append("<field name=\"").append(primaryKey).append("\"/>");
        }
        builder.append("</primarykey>");
        return builder.toString();
    }
}
