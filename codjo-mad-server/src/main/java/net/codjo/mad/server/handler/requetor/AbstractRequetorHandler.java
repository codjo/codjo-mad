/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.mad.server.handler.requetor;
import net.codjo.database.api.Database;
import net.codjo.database.api.query.SelectQuery;
import net.codjo.database.api.query.SelectResult;
import net.codjo.mad.server.handler.AbstractHandler;
import net.codjo.mad.server.handler.HandlerException;
import net.codjo.mad.server.handler.XMLUtils;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.apache.log4j.Logger;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
/**
 * Classe de base pour les handler de type requetor.
 */
public abstract class AbstractRequetorHandler extends AbstractHandler {
    private static final String SQL_QUERY = "sqlQuery"; // Nom du Selector contenant la requête à exécuter
    protected Map<String, SqlWrapper> wrappers
          = new HashMap<String, SqlWrapper>(); // TODO passer en private et utiliser le getter
    private final Logger logger = Logger.getLogger(getClass());
    private final Set<String> primaryKeys = new TreeSet<String>();
    private final String headerPk;
    private final String rootTableName;
    /**
     * Liste des champs de la clé primaire de la table maître qui ne sont pas explicitement demandés dans la
     * requête, mais que l'on doit toutefois ramener avec la requête SELECT.
     */
    private List<String> pkFieldsToAdd = new ArrayList<String>();
    /**
     * Map "nom Java de champ" vers "Type SQL Java (entier sour forme de String)"
     */
    private Map<String, String> linkedFieldsTypeMap = new HashMap<String, String>();
    private Database database;


    protected AbstractRequetorHandler(String sqlTableName, String[] pks, Database database) {
        this.database = database;
        this.rootTableName = sqlTableName;
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
            Map<String, String> args = getSelectors(node);

            buildLinkedFieldsTypeMap(args);

            String[] propertyNames = buildPropertyNames(node);

            return proceedSqlQuery(node, args, propertyNames);
        }
        catch (Exception ex) {
            throw new HandlerException(ex);
        }
    }


    /**
     * Initialise la map <code>linkedFieldsTypeMap</code> d'après les sélecteurs de la requête.
     *
     * @param selectors Les sélecteurs spécifiés dans la requête.
     */
    private void buildLinkedFieldsTypeMap(Map<String, String> selectors) {
        // Tous les sélecteurs représentent des types de champs, sauf SQL_QUERY.
        linkedFieldsTypeMap.clear();
        linkedFieldsTypeMap.putAll(selectors);
        linkedFieldsTypeMap.remove(SQL_QUERY);
    }


    private Map<String, String> getSelectors(final Node node) throws SAXException {
        Map<String, String> pks = null;
        if (XMLUtils.hasNode(node, "selector")) {
            pks = XMLUtils.getSelectors(node);
        }
        return pks;
    }


    /**
     * Récupère un champ dans un {@link java.sql.ResultSet} sous forme de {@link String}.
     *
     * @param rs            Le ResultSet à utiliser
     * @param idx           Index du champ dans le ResultSet
     * @param javaFieldName Nom Java du champ.
     *
     * @return Le contenu du champ.
     *
     * @throws SQLException si une erreur survient lors de la récupération de la valeur.
     */
    private String getValue(ResultSet rs, int idx, String javaFieldName) throws SQLException {
        SqlWrapper wrapper;
        if (linkedFieldsTypeMap.containsKey(javaFieldName)) {
            // Si le type du champ est spécifié dans la requête,
            // on l'utilise pour créer le SqlWrapper correspondant.
            String sqlType = linkedFieldsTypeMap.get(javaFieldName);
            wrapper = new SqlWrapper(toSqlName(javaFieldName), Integer.valueOf(sqlType));
        }
        else {
            // Si le type du champ n'est pas spécifié dans la requête,
            // c'est un attribut de l'entité maître
            // et donc son SqlWrapper a été généré par Datagen.
            wrapper = getSqlWrapper(javaFieldName);
        }
        return wrapper.get(rs, idx);
    }


    /**
     * Récupère le {@link SqlWrapper} généré par Datagen pour un champ donné.
     *
     * @param javaFieldName Nom Java du champ
     *
     * @return Le SqlWrapper associé.
     *
     * @throws IllegalArgumentException si le champ est inconnu.
     */
    private SqlWrapper getSqlWrapper(String javaFieldName) {
        SqlWrapper wrapper = wrappers.get(javaFieldName);
        if (wrapper == null) {
            throw new IllegalArgumentException("Le champ '" + javaFieldName + "' est inconnu.");
        }
        return wrapper;
    }


    private String buildResponseHeader(Node node, int totalRowCount) throws SAXException {
        String requestId = XMLUtils.getAttribute(node, "request_id");
        return "<result request_id=\"" + requestId + "\" totalRowCount=\"" + totalRowCount + "\" >"
               + headerPk;
    }


    private void addRow(StringBuffer buffer, ResultSet rs, String[] propertyNames) throws SQLException {
        buffer.append("<row>");
        for (int i = 0; i < propertyNames.length; i++) {
            String propertyName = propertyNames[i];
            buffer.append("<field name=\"").append(propertyName).append("\">")
                  .append(getValue(rs, i + 1, propertyName)).append("</field>");
        }
        buffer.append("</row>");
    }


    private static String toSqlName(String propertyName) {
        String sqlName = toSqlUpper(propertyName);
        if (sqlName.length() > 28) {
            throw new IllegalArgumentException("La colonne pour le field " + propertyName
                                               + " dépasse 28 caractères !");
        }
        return sqlName;
    }


    private static String toSqlUpper(String propertyName) {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < propertyName.length(); i++) {
            if (Character.isUpperCase(propertyName.charAt(i))) {
                buffer.append('_');
            }
            buffer.append(propertyName.charAt(i));
        }

        return buffer.toString().toUpperCase();
    }


    private String[] buildPropertyNames(Node selectNode) {
        try {
            Node attributes = XMLUtils.getNode(selectNode, "attributes");
            List<Node> fieldsList = XMLUtils.getNodeList(attributes, "name");

            List<String> propertyNames = new ArrayList<String>();

            // Récupérer la liste des champs à ramener à partir de la requête.
            for (Node node : fieldsList) {
                String propertyName = node.getFirstChild().getNodeValue();
                propertyNames.add(propertyName);
            }

            // Il faut aussi ramener les champs de la clé primaire
            buildPkFieldsToAdd(propertyNames);
            propertyNames.addAll(pkFieldsToAdd);

            logger.debug("propertyNames = " + propertyNames);

            return propertyNames.toArray(new String[propertyNames.size()]);
        }
        catch (SAXException ex) {
            // Pas de balise attributes ou name. Dans ce cas on renvoie tout.
            return wrappers.keySet().toArray(new String[wrappers.keySet().size()]);
        }
    }


    private void buildPkFieldsToAdd(List propertyNames) {
        pkFieldsToAdd.clear();
        for (String primaryKey : primaryKeys) {
            if (!propertyNames.contains(primaryKey)) {
                pkFieldsToAdd.add(primaryKey);
            }
        }
    }


    /**
     * Ajoute les champs manquants de la clé primaire à la requête SELECT.
     *
     * @param query La requête d'origine
     *
     * @return La requête complétée
     */
    private String addMissingPkFieldsInSelect(String query) {
        if (pkFieldsToAdd.isEmpty()) {
            return query;
        }

        int indexFrom = query.indexOf(" from ");
        StringBuilder buffer = new StringBuilder(query.substring(0, indexFrom));

        for (String aPkFieldsToAdd : pkFieldsToAdd) {
            buffer.append(",");
            buffer.append(rootTableName).append(".").append(toSqlName(aPkFieldsToAdd));
        }

        buffer.append(query.substring(indexFrom));

        return buffer.toString();
    }


    private String proceedSqlQuery(final Node node,
                                   final Map<String, String> args,
                                   final String[] propertyNames) throws SQLException, SAXException {
        Connection con = getContext().getConnection();
        try {
            StringBuffer response = new StringBuffer("");
            String fullQuery = addMissingPkFieldsInSelect(args.get(SQL_QUERY));
            if (logger.isDebugEnabled()) {
                logger.debug("fullQuery = " + fullQuery);
            }

            SelectQuery query = database.select(con, fullQuery);
            query.setPage(XMLUtils.determinePage(node));
            SelectResult results = query.execute();
            while (results.next()) {
                addRow(response, results, propertyNames);
            }
            results.close();
            return buildResponseHeader(node, results.getTotalRowCount()) + response.append("</result>");
        }
        finally {
            con.close();
        }
    }


    /**
     * Wrapper sur une requete SQL.
     */
    public static class SqlWrapper {
        private String sqlName;
        private int sqlType = java.sql.Types.VARCHAR;


        public SqlWrapper(String sqlName) {
            this.sqlName = sqlName;
        }


        public SqlWrapper(String sqlName, int sqlType) {
            this.sqlName = sqlName;
            this.sqlType = sqlType;
        }


        public String get(ResultSet rs, int idx) throws SQLException {
            if (java.sql.Types.DATE == sqlType) {
                java.sql.Date date = rs.getDate(idx);
                if (date != null) {
                    return date.toString();
                }
                else {
                    return "null";
                }
            }
            else {
                return XMLUtils.convertToStringValue(rs.getObject(idx));
            }
        }


        public String getSqlName() {
            return sqlName;
        }
    }
}
