package net.codjo.mad.common.structure;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Classe qui lit le fichier structure.xml et génére une liste de tables de quarantaines.
 *
 * @author $Author: gaudefr $
 * @version $Revision: 1.6 $
 */
public class DefaultStructureReader implements StructureReader {

    private SqlUtil sqlUtil = new SqlUtil();
    private Map<String, DefaultTableStructure> allTablesBySqlName;
    private Map<String, DefaultTableStructure> allTablesByJavaName;


    public DefaultStructureReader(InputStream structureStream)
          throws IOException, ParserConfigurationException, SAXException {
        this(new InputStreamReader(structureStream));
    }


    public DefaultStructureReader(Reader reader)
          throws IOException, ParserConfigurationException, SAXException {
        if (reader == null) {
            throw new IllegalArgumentException("fichier de structure null");
        }
        loadStructure(reader);
    }


    public DefaultStructureReader(String structureFile)
          throws IOException, ParserConfigurationException, SAXException {
        this(StructureReader.class.getResourceAsStream(structureFile));
    }


    public Collection<DefaultTableStructure> getAllTableStructure() {
        return Collections.unmodifiableCollection(allTablesByJavaName.values());
    }


    public Map<String, TableStructure> getQuarantineTables() {
        Map<String, TableStructure> quarantineTables = new HashMap<String, TableStructure>();

        for (DefaultTableStructure structure : allTablesBySqlName.values()) {
            if ("quarantine".equals(structure.getType())) {
                quarantineTables.put(structure.getSqlName(), structure);
            }
        }
        return quarantineTables;
    }


    public TableStructure getTableByJavaName(String javaName) {
        TableStructure structure = allTablesByJavaName.get(javaName);
        if (structure == null) {
            throw new IllegalArgumentException("La table " + javaName
                                               + " ne possede pas de structure"
                                               + " - Le fichier de paramétrage est incomplet");
        }
        return structure;
    }


    public TableStructure getTableBySqlName(String sqlTableName) {
        TableStructure structure = allTablesBySqlName.get(sqlTableName);
        if (structure == null) {
            throw new IllegalArgumentException("La table " + sqlTableName
                                               + " ne possede pas de structure"
                                               + " - Le fichier de paramétrage est incomplet");
        }

        return structure;
    }


    public boolean containsTableByJavaName(String javaName) {
        return allTablesByJavaName.containsKey(javaName);
    }


    public boolean containsTableBySqlName(String sqlTableName) {
        return allTablesBySqlName.containsKey(sqlTableName);
    }


    private static String getAttribute(Node node, String attributeName) {
        if (node.getAttributes() == null
            || node.getAttributes().getNamedItem(attributeName) == null) {
            return null;
        }
        else {
            return node.getAttributes().getNamedItem(attributeName).getNodeValue();
        }
    }


    private static Document toDocument(final Reader reader)
          throws IOException, ParserConfigurationException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new InputSource(reader));
    }


    private int getSqlTypeAttribute(Node node) {
        String sqlType = getAttribute(node, "sql-type");
        if (sqlType != null) {
            return sqlUtil.stringToSqlType(sqlType);
        }
        else {
            return java.sql.Types.JAVA_OBJECT;
        }
    }


    private void loadStructure(Reader reader)
          throws IOException, ParserConfigurationException, SAXException {
        Map<String, DefaultTableStructure> tablesStructureBySqlName
              = new HashMap<String, DefaultTableStructure>();
        Map<String, DefaultTableStructure> tablesStructureByJavaName
              = new HashMap<String, DefaultTableStructure>();
        Document doc = toDocument(reader);

        for (int i = 0; i < doc.getElementsByTagName("table").getLength(); i++) {
            Node tableNode = doc.getElementsByTagName("table").item(i);

            DefaultTableStructure table =
                  new DefaultTableStructure(getAttribute(tableNode, "label"),
                                            getAttribute(tableNode, "name"), getAttribute(tableNode, "sql"),
                                            getAttribute(tableNode, "type"));

            for (int j = 0; j < tableNode.getChildNodes().getLength(); j++) {
                Node fieldNode = tableNode.getChildNodes().item(j);

                if (Node.ELEMENT_NODE == fieldNode.getNodeType()) {
                       table.addField(new DefaultFieldStructure(
                          getAttribute(fieldNode, "label"),
                          getAttribute(fieldNode, "name"),
                          getAttribute(fieldNode, "sql"),
                          getAttribute(fieldNode, "referential"),
                          getSqlTypeAttribute(fieldNode),
                          getAttribute(fieldNode, "sql-precision"),
                          Boolean.valueOf(getAttribute(fieldNode, "sql-required")),
                          Boolean.valueOf(getAttribute(fieldNode, "sql-primary-key")),
                          Boolean.valueOf(getAttribute(fieldNode, "functional-key"))));
                }
            }
            tablesStructureBySqlName.put(table.getSqlName(), table);
            tablesStructureByJavaName.put(table.getJavaName(), table);
        }
        allTablesBySqlName = tablesStructureBySqlName;
        allTablesByJavaName = tablesStructureByJavaName;
    }
}