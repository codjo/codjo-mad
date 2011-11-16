/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.mad.server.handler.aspect;
import net.codjo.mad.server.handler.XMLUtils;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
/**
 * Implantation par défaut d'un {@link QueryManager}.
 */
class QueryManagerImpl implements QueryManager {
    private static final String ID = "id";
    private final Map<String, QueryBuilder> builderMap;
    private Node queries;


    QueryManagerImpl() {
        builderMap = new HashMap<String, QueryBuilder>();
        builderMap.put("insert", new CreateBuilder());
        builderMap.put("select", new ReadBuilder());
        builderMap.put("update", new UpdateBuilder());
        builderMap.put("delete", new DeleteBuilder());
        builderMap.put("command", new CommandBuilder());
        builderMap.put("sql", new SqlBuilder());
    }


    public void setQueries(Document queries) {
        this.queries = queries.getFirstChild();
    }


    public String[] getHandlerIdList() {
        List<String> resultList = new ArrayList<String>();
        try {
            NodeList nodes = queries.getChildNodes();
            for (int i = 0; i < nodes.getLength(); i++) {
                Node node = nodes.item(i);
                if (XMLUtils.containsNode(node, ID)) {
                    resultList.add(XMLUtils.getNodeValue(node, ID));
                }
            }
        }
        catch (SAXException e) {
            StringWriter writer = new StringWriter();
            e.printStackTrace(new PrintWriter(writer));
            throw new AssertionError(writer.toString());
        }

        return resultList.toArray(new String[resultList.size()]);
    }


    public Query[] getQuery(String handlerId) {
        List<Query> resultList = new ArrayList<Query>();
        try {
            NodeList nodes = queries.getChildNodes();
            for (int i = 0; i < nodes.getLength(); i++) {
                Node node = nodes.item(i);
                if (XMLUtils.containsNode(node, ID) && handlerId.equals(XMLUtils.getNodeValue(node, ID))) {
                    QueryBuilder builder = findBuilder(node.getNodeName());
                    resultList.add(builder.newQuery(node));
                }
            }
        }
        catch (SAXException e) {
            StringWriter writer = new StringWriter();
            e.printStackTrace(new PrintWriter(writer));
            throw new AssertionError(writer.toString());
        }
        if (resultList.isEmpty()) {
            throw new IllegalArgumentException("Handler '" + handlerId + "' est introuvable !");
        }
        return resultList.toArray(new Query[resultList.size()]);
    }


    public String getUser() {
        try {
            Node audit = XMLUtils.getNode(queries, "audit");
            return XMLUtils.getNodeValue(audit, "user");
        }
        catch (SAXException e) {
            ; // Normalement impossible (audit/user existe toujours)
            throw new IllegalArgumentException("Pas de balise Audit");
        }
    }


    private QueryBuilder findBuilder(String handlerType) {
        QueryBuilder builder = builderMap.get(handlerType);
        if (builder == null) {
            throw new IllegalArgumentException("Le type de handler '" + handlerType
                                               + "' est inconnu !");
        }
        return builder;
    }


    private static interface QueryBuilder {
        Query newQuery(Node query);
    }

    private static class CreateBuilder implements QueryBuilder {
        public Query newQuery(Node node) {
            return new CreateQueryImpl(node);
        }
    }

    private static class ReadBuilder implements QueryBuilder {
        public Query newQuery(Node node) {
            return new ReadQueryImpl(node);
        }
    }

    private static class UpdateBuilder implements QueryBuilder {
        public Query newQuery(Node node) {
            return new UpdateQueryImpl(node);
        }
    }

    private static class DeleteBuilder implements QueryBuilder {
        public Query newQuery(Node node) {
            return new DeleteQueryImpl(node);
        }
    }

    private static class CommandBuilder implements QueryBuilder {
        public Query newQuery(Node node) {
            return new CommandQuery(node);
        }
    }

    private static class SqlBuilder implements QueryBuilder {
        public Query newQuery(Node node) {
            return new CommandQuery(node);
        }
    }

    static class CreateQueryImpl extends AbstractQuery {
        private Node queryNode;


        CreateQueryImpl(Node query) {
            this.queryNode = query;
        }


        public String getId() {
            try {
                return XMLUtils.getNodeValue(queryNode, ID);
            }
            catch (SAXException e) {
                throw new InternalError("Le noeud ID a disparu");
            }
        }


        public Map rowToMap() {
            try {
                return XMLUtils.getRowFields(queryNode);
            }
            catch (SAXException e) {
                return Collections.emptyMap();
            }
        }


        public Query newQuery(Node node) {
            return new CreateQueryImpl(node);
        }


        protected Node getQueryNode() {
            return queryNode;
        }
    }

    private static class ReadQueryImpl extends CreateQueryImpl {
        ReadQueryImpl(Node query) {
            super(query);
        }


        @Override
        public Map rowToMap() {
            try {
                return XMLUtils.getSelectors(getQueryNode());
            }
            catch (SAXException e) {
                return Collections.emptyMap();
            }
        }
    }

    private static class UpdateQueryImpl extends CreateQueryImpl {
        UpdateQueryImpl(Node query) {
            super(query);
        }


        @Override
        public Map rowToMap() {
            try {
                Map<String, String> rowFields = XMLUtils.getRowFields(getQueryNode());
                rowFields.putAll(XMLUtils.getPrimaryKeys(getQueryNode()));
                return rowFields;
            }
            catch (SAXException e) {
                return Collections.emptyMap();
            }
        }
    }

    private static class DeleteQueryImpl extends CreateQueryImpl {
        DeleteQueryImpl(Node query) {
            super(query);
        }


        @Override
        public Map rowToMap() {
            try {
                return XMLUtils.getPrimaryKeys(getQueryNode());
            }
            catch (SAXException e) {
                return Collections.emptyMap();
            }
        }
    }
}
