/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.mad.server.structure;
import net.codjo.mad.common.Log;
import net.codjo.mad.common.structure.DefaultStructureReader;
import net.codjo.mad.common.structure.DefaultTableStructure;
import net.codjo.mad.common.structure.FieldStructure;
import net.codjo.mad.common.structure.SqlUtil;
import net.codjo.mad.common.structure.StructureReader;
import net.codjo.mad.common.structure.TableStructure;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
/**
 * Classe permettant de charge des objets <code>TableStructure</code> et de fusionner le résultat avec la
 * structure par défaut.
 */
public final class StructureHome {
    private static final String XSL =
          "<?xml version=\"1.0\"?>"
          + "<xsl:stylesheet xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" version=\"1.0\">"
          + "    <xsl:output method=\"xml\" encoding=\"ISO-8859-1\"/>"
          + "    <xsl:template match=\"/\" > <xsl:copy-of select=\".\"/> </xsl:template>"
          + "</xsl:stylesheet>";
    private static final String LABEL = "label";
    private static final String TABLE = "table";
    private static final String TYPE = "type";
    private static final String NAME = "name";
    private static final String SQL = "sql";
    private static final String FIELD = "field";
    private static final String REFERENTIAL = "referential";
    private static final String SQL_TYPE = "sql-type";
    private static final String SQL_PRECISION = "sql-precision";
    private static final String SQL_REQUIRED = "sql-required";
    private static final String SQL_PRIMARY_KEY = "sql-primary-key";
    private static final String FUNCTIONAL_KEY = "functional-key";
    private static final String STRUCTURE = "structure";
    private final SqlUtil sqlUtil = new SqlUtil();
    private TableStructure[] defaultStructure = null;
    private String defaultStructurePath = "/conf/structure.xml";
    private String structure;


    public StructureHome() {
    }


    public void setDefaultStructurePath(String defaultStructurePath) {
        this.defaultStructurePath = defaultStructurePath;
    }


    public String getDefaultStructurePath() {
        return defaultStructurePath;
    }


    public String getStructure()
          throws SAXException, ParserConfigurationException, IOException, TransformerException {
        if (structure == null) {
            ensureDefaultIsLoaded();

            DocumentBuilder builder = initFactory();
            Document root = builder.getDOMImplementation().createDocument(null, STRUCTURE, null);

            for (TableStructure table : defaultStructure) {
                Node tableNode = buildTableNode(root, table);
                root.getDocumentElement().appendChild(tableNode);
            }

            structure = domToString(root);
        }
        return structure;
    }


    private void ensureDefaultIsLoaded() throws SAXException, ParserConfigurationException, IOException {
        if (defaultStructure != null) {
            return;
        }

        InputStream structureStream = StructureHome.class.getResourceAsStream(defaultStructurePath);
        if (structureStream == null) {
            throw new IllegalArgumentException("La resource '" + defaultStructurePath + "' est introuvable");
        }
        StructureReader reader = new DefaultStructureReader(structureStream);

        Collection<DefaultTableStructure> tableStructureList = reader.getAllTableStructure();
        defaultStructure = tableStructureList.toArray(new TableStructure[tableStructureList.size()]);
    }


    private Node buildTableNode(Document root, TableStructure table) {
        Element node = root.createElement(TABLE);

        setAttribute(node, TYPE, table.getType());
        setAttribute(node, LABEL, table.getLabel());
        setAttribute(node, NAME, table.getJavaName());
        setAttribute(node, SQL, table.getSqlName());

        for (Object object : table.getFieldsByJavaKey().values()) {
            FieldStructure field = (FieldStructure)object;
            node.appendChild(buildFieldNode(root, field));
        }

        return node;
    }


    private void setAttribute(Element elt, String attributeName, String value) {
        if (value == null) {
            return;
        }
        elt.setAttribute(attributeName, value);
    }


    private Node buildFieldNode(Document root, FieldStructure field) {
        Element node = root.createElement(FIELD);
        setAttribute(node, LABEL, field.getLabel());
        setAttribute(node, NAME, field.getJavaName());
        setAttribute(node, SQL, field.getSqlName());
        setAttribute(node, REFERENTIAL, field.getReferentialTypeName());
        if (field.getSqlType() != java.sql.Types.JAVA_OBJECT) {
            setAttribute(node, SQL_TYPE, sqlUtil.sqlTypeToString(field.getSqlType()));
        }
        setAttribute(node, SQL_PRECISION, field.getSqlPrecision());
        setAttribute(node, SQL_REQUIRED, Boolean.toString(field.isSqlRequired()));
        setAttribute(node, SQL_PRIMARY_KEY, Boolean.toString(field.isSqlPrimaryKey()));
        setAttribute(node, FUNCTIONAL_KEY, Boolean.toString(field.isFunctionalKey()));
        return node;
    }


    private String domToString(Document rootDocument) throws TransformerException {
        StringWriter writer = new StringWriter();
        TransformerFactory tFactory = TransformerFactory.newInstance();
        Transformer transformer =
              tFactory.newTransformer(new StreamSource(new StringReader(XSL)));
        transformer.transform(new DOMSource(rootDocument), new StreamResult(writer));
        return writer.toString();
    }


    private DocumentBuilder initFactory() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        builder.setErrorHandler(new MyErrorHandler());
        return builder;
    }


    /**
     * ErrorHandler juste au cas ou.
     */
    private static class MyErrorHandler implements ErrorHandler {
        public void error(SAXParseException sAXParseException)
              throws SAXException {
            Log.error("ERROR : ");
            print(sAXParseException);
        }


        public void fatalError(SAXParseException sAXParseException)
              throws SAXException {
            Log.error("FATAL : ");
            print(sAXParseException);
        }


        public void warning(SAXParseException sAXParseException)
              throws SAXException {
            Log.error("WARNING : ");
            print(sAXParseException);
        }


        private void print(SAXParseException error) {
            Log.error("[L=" + error.getLineNumber() + " C=" + error.getColumnNumber()
                      + "] " + error.toString());
        }
    }
}
