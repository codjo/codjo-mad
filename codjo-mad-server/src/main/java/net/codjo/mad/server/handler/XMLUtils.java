/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.mad.server.handler;
import net.codjo.database.api.query.Page;
import net.codjo.mad.common.Log;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
/**
 */
public final class XMLUtils {
    private static final String FIELD = "field";


    private XMLUtils() {
    }


    public static Map<String, String> getArguments(Node node) throws SAXException {
        return getSubNodesInNode(node, "args", FIELD);
    }


    public static String getAttribute(Node node, String name) throws SAXException {
        try {
            Node attNode = node.getAttributes().getNamedItem(name);
            if (attNode != null) {
                return attNode.getNodeValue();
            }
        }
        catch (NullPointerException ex) {
            Log.error("node" + node, ex);
        }

        throw new SAXException("attribut " + name + " est introuvable dans le noeud "
                               + node.getNodeName());
    }


    public static String getFieldValue(Node node, String name) throws SAXException {
        NodeList nodes = node.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            if ("row".equals(nodes.item(i).getNodeName())) {
                NodeList fields = nodes.item(i).getChildNodes();
                for (int j = 0; j < fields.getLength(); j++) {
                    if (cacaWeblogic(fields.item(j))) {
                        continue;
                    }
                    if (name.equals(getAttribute(fields.item(j), "name"))) {
                        return getAttribute(fields.item(j), "value");
                    }
                }
            }
        }
        throw new SAXException("balise " + name + " est introuvable dans la ligne");
    }


    public static Node getNode(Node node, String name) throws SAXException {
        NodeList nodes = node.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            if (name.equals(nodes.item(i).getNodeName())) {
                return nodes.item(i);
            }
        }

        throw new SAXException("balise " + name + " est introuvable dans le noeud " + node.getNodeName());
    }


    /**
     * retourne une liste de Node fils du node courant et qui ont name comme nom de balise.
     *
     * @param node le neoud
     * @param name le nom du noeud.
     *
     * @return La valeur de nodeList
     */
    public static List<Node> getNodeList(Node node, String name) {
        List<Node> list = new ArrayList<Node>();
        NodeList nodes = node.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            if (name.equals(nodes.item(i).getNodeName())) {
                list.add(nodes.item(i));
            }
        }
        return list;
    }


    public static String getNodeValue(Node node, String name) throws SAXException {
        NodeList nodes = node.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            if (name.equals(nodes.item(i).getNodeName())) {
                return nodes.item(i).getChildNodes().item(0).getNodeValue();
            }
        }

        throw new SAXException("balise " + name + " est introuvable dans le noeud " + node.getNodeName());
    }


    public static Map<String, String> getPrimaryKeys(Node node) throws SAXException {
        return getSubNodesInNode(node, "primarykey", FIELD);
    }


    public static Map<String, String> getRowFields(Node node) throws SAXException {
        return getSubNodesInNode(node, "row", FIELD);
    }


    public static Map<String, String> getSelectors(Node node) throws SAXException {
        return getSubNodesInNode(node, "selector", FIELD);
    }


    public static boolean containsField(Node node, String name) throws SAXException {
        NodeList nodes = node.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            if ("row".equals(nodes.item(i).getNodeName())) {
                NodeList fields = nodes.item(i).getChildNodes();
                for (int j = 0; j < fields.getLength(); j++) {
                    if (cacaWeblogic(fields.item(j))) {
                        continue;
                    }
                    if (name.equals(getAttribute(fields.item(j), "name"))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    public static boolean containsNode(Node node, String name) {
        NodeList nodes = node.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            if (name.equals(nodes.item(i).getNodeName())) {
                return true;
            }
        }
        return false;
    }


    public static <T> T convertFromStringValue(Class<T> propertyClass, String value) {
        //noinspection unchecked
        return (T)convertImpl(propertyClass, value);
    }


    private static Object convertImpl(Class propertyClass, String value) {
        if (("null".equalsIgnoreCase(value) || value == null)
            && Boolean.class != propertyClass) {
            return null;
        }
        if (String.class == propertyClass) {
            return value;
        }
        if (Integer.class == propertyClass || int.class == propertyClass) {
            return new Integer(value);
        }
        if (Double.class == propertyClass || double.class == propertyClass) {
            return new Double(value);
        }
        if (Boolean.class == propertyClass || boolean.class == propertyClass) {
            return "true".equals(value) ? Boolean.TRUE : Boolean.FALSE;
        }
        if (Timestamp.class == propertyClass) {
            return Timestamp.valueOf(value);
        }
        if (java.sql.Date.class == propertyClass || java.util.Date.class == propertyClass) {
            return java.sql.Date.valueOf(value);
        }
        if (BigDecimal.class == propertyClass) {
            return new BigDecimal(value);
        }
        throw new IllegalArgumentException("Type d'attribut non supporté : " + propertyClass);
    }


    public static String convertToStringValue(Object value) {
        if (value == null) {
            return "null";
        }
        else if (value.getClass() == String.class) {
            return "<![CDATA[" + value.toString() + "]]>";
        }
        else {
            return value.toString();
        }
    }


    public static String convertToStringValue(boolean value) {
        if (value) {
            return "true";
        }
        else {
            return "false";
        }
    }


    public static String convertToStringValue(int value) {
        return Integer.toString(value);
    }


    public static String convertToStringValue(double value) {
        return Double.toString(value);
    }


    public static void displayNode(Node node) {
        try {
            Log.info("Node " + node.getNodeName() + " : \n" + toString(node));
        }
        catch (Exception ex) {
            Log.error("Error during displayNode " + node);
        }
    }


    public static boolean hasNode(Node node, String nodeName) {
        try {
            getNode(node, nodeName);
            return true;
        }
        catch (SAXException ex) {
            return false;
        }
    }


    public static Document parse(String requests)
          throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new InputSource(new StringReader(requests)));
    }


    public static String toString(Node node) throws Exception {
        StringWriter str = new StringWriter();

        TransformerFactory tFactory = TransformerFactory.newInstance();
        Transformer transformer = tFactory.newTransformer();

        transformer.transform(new DOMSource(node), new StreamResult(str));

        // NB : on enleve les premiers caractères : <?xml ... />
        String xml = str.toString();
        int index = xml.indexOf('>');
        return xml.substring(index + 1);
    }


    private static Map<String, String> getSubNodesInNode(Node node, String nodeName, String subNodeName)
          throws SAXException {
        Node pkNode = getNode(node, nodeName);
        List<Node> pkList = getNodeList(pkNode, subNodeName);

        Map<String, String> pks = new HashMap<String, String>();
        for (Node fieldNode : pkList) {
            pks.put(getAttribute(fieldNode, "name"), fieldNode.getTextContent());
        }
        return pks;
    }


    private static boolean cacaWeblogic(Node node) {
        // Noeud ajoute lorsque le manager est execute dans le container.
        return "#text".equals(node.getNodeName());
    }


    public static Page determinePage(Node selectNode) throws SAXException {
        Node page;
        try {
            page = getNode(selectNode, "page");
        }
        catch (SAXException ex) {
            // Pas de node page
            return new Page(1, 30);
        }
        int pageNumber = Integer.parseInt(getAttribute(page, "num"));
        int pageSize = Integer.parseInt(getAttribute(page, "rows"));
        return new Page(pageNumber, pageSize);
    }
}
