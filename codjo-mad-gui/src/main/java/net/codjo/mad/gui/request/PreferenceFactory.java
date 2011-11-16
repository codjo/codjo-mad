package net.codjo.mad.gui.request;
import net.codjo.mad.gui.request.factory.CommandFactory;
import net.codjo.mad.gui.request.factory.DeleteFactory;
import net.codjo.mad.gui.request.factory.InsertFactory;
import net.codjo.mad.gui.request.factory.RequestFactory;
import net.codjo.mad.gui.request.factory.SelectFactory;
import net.codjo.mad.gui.request.factory.UpdateFactory;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.UIManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;
/**
 * Le chargeur de préférence.
 *
 * @author $Author: villard $
 * @version $Revision: 1.35 $
 */
public final class PreferenceFactory {
    private static PreferenceManager preferenceManager;
    private static final Logger LOGGER = Logger.getLogger(PreferenceFactory.class);


    private PreferenceFactory() {
    }


    public static Preference getPreference(String preferenceId) {
        if (preferenceManager == null) {
            throw new IllegalStateException("PreferenceFactory n'est pas initialisé, "
                                            + "appelez la methode initFactory()");
        }
        return preferenceManager.getPreferenceById(preferenceId);
    }


    public static void initFactory(InputStream is)
          throws BuildException {
        if (preferenceManager == null) {
            loadMapping(new InputSource(is));
            loadMadIcons();
        }
    }


    public static void initFactory() {
        if (preferenceManager == null) {
            preferenceManager = new PreferenceManager();
            loadMadIcons();
        }
    }


    public static void clearPreferences() {
        if (preferenceManager != null) {
            preferenceManager = new PreferenceManager();
            loadMadIcons();
        }
    }


    public static void loadMadIcons() {
        UIManager.put("mad.export", loadIcon("/images/idea/export.png"));
        UIManager.put("mad.add", loadIcon("/images/idea/add.png"));
        UIManager.put("mad.edit", loadIcon("/images/idea/edit.png"));
        UIManager.put("mad.delete", loadIcon("/images/idea/delete.png"));
        UIManager.put("mad.previous", loadIcon("/images/idea/previous.png"));
        UIManager.put("mad.next", loadIcon("/images/idea/next.png"));
        UIManager.put("mad.load", loadIcon("/images/idea/filter.png"));
        UIManager.put("mad.clear", loadIcon("/images/mad.eraser.gif"));
        UIManager.put("mad.reload", loadIcon("/images/idea/reload.png"));
        UIManager.put("mad.undo", loadIcon("/images/idea/undo.png"));
        UIManager.put("mad.redo", loadIcon("/images/idea/redo.png"));
        UIManager.put("mad.gotopage", loadIcon("/images/idea/goto-page.png"));
        UIManager.put("mad.pageCopy", loadIcon("/images/idea/pageCopy.png"));
        UIManager.put("mad.save", loadIcon("/images/idea/save.png"));
    }


    public static void loadMapping(InputSource configPreference)
          throws BuildException {
        preferenceManager = new PreferenceManager();
        addMapping(configPreference);
    }


    public static void addMapping(InputSource configPreference)
          throws BuildException {
        if (preferenceManager == null) {
            throw new IllegalStateException("PreferenceFactory n'est pas initialisé, "
                                            + "appelez d'abord la methode initFactory()");
        }
        if (configPreference == null) {
            throw new IllegalArgumentException("configPreference ne peut pas être null.");
        }
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setErrorHandler(new SimpleErrorHandler());
            load(builder.parse(configPreference));
        }
        catch (BuildException pce) {
            throw pce;
        }
        catch (Exception pce) {
            pce.printStackTrace();
            throw new BuildException("Impossible de charger les preferences !", pce);
        }
    }


    public static void addPreference(Preference pref) {
        if (preferenceManager == null) {
            throw new IllegalStateException("PreferenceFactory n'est pas initialisé, "
                                            + "appelez d'abord la methode initFactory()");
        }
        preferenceManager.addPreference(pref);
    }


    public static boolean containsPreferenceId(String preferenceId) {
        return preferenceManager.containsPreferenceId(preferenceId);
    }


    public static PreferenceManager getPreferenceManager() {
        return preferenceManager;
    }


    private static void load(Document doc)
          throws NoSuchMethodException, InvocationTargetException,
                 IllegalAccessException, InstantiationException, ClassNotFoundException {
        NodeList nodes = doc.getElementsByTagName("preference");
        for (int i = 0; i < nodes.getLength(); i++) {
            Preference pref = loadPreference(nodes.item(i));

            preferenceManager.addPreference(pref);
        }
    }


    private static Preference loadPreference(Node prefNode)
          throws NoSuchMethodException, InvocationTargetException,
                 IllegalAccessException, InstantiationException, ClassNotFoundException {
        Preference preference = new Preference();
        preference.setId(getAttribute(prefNode, "id"));
        preference.setDwClassName(getAttribute(prefNode, "detailWindowClassName"));
        NodeList nodes = prefNode.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node currentNode = nodes.item(i);
            loadNode(currentNode, preference);
        }
        return preference;
    }


    private static void loadNode(Node currentNode, Preference preference)
          throws NoSuchMethodException, InvocationTargetException,
                 IllegalAccessException, InstantiationException, ClassNotFoundException {
        if ("selectByPk".equals(currentNode.getNodeName())) {
            preference.setSelectByPk(buildFactory(currentNode, SelectFactory.class));
        }
        else if ("entity".equals(currentNode.getNodeName())) {
            preference.setEntity(currentNode.getFirstChild().getNodeValue());
        }
        else if ("selectAll".equals(currentNode.getNodeName())) {
            preference.setSelectAll(buildFactory(currentNode, SelectFactory.class));
        }
        else if ("insert".equals(currentNode.getNodeName())) {
            preference.setInsert(buildFactory(currentNode, InsertFactory.class));
        }
        else if ("update".equals(currentNode.getNodeName())) {
            preference.setUpdate(buildFactory(currentNode, UpdateFactory.class));
        }
        else if ("delete".equals(currentNode.getNodeName())) {
            preference.setDelete(buildFactory(currentNode, DeleteFactory.class));
        }
        else if ("requetor".equals(currentNode.getNodeName())) {
            preference.setRequetorId(currentNode.getFirstChild().getNodeValue());
        }
        else if ("column".equals(currentNode.getNodeName())) {
            addColumn(currentNode, preference);
        }
        else if ("hidden".equals(currentNode.getNodeName())) {
            addHiddenColumns(preference, currentNode);
        }
        else if (currentNode.getNodeType() != Node.TEXT_NODE
                 && currentNode.getNodeType() != Node.COMMENT_NODE) {
            throw new BuildException("Balise inconnue: " + currentNode.getNodeName()
                                     + "(" + currentNode.getNodeValue() + ")");
        }
    }


    private static void addHiddenColumns(Preference pref, Node hiddenNode) {
        NodeList nodes = hiddenNode.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node currentNode = nodes.item(i);
            if ("column".equals(currentNode.getNodeName())) {
                addHiddenColumn(currentNode, pref);
            }
            else if (currentNode.getNodeType() != Node.TEXT_NODE
                     && currentNode.getNodeType() != Node.COMMENT_NODE) {
                throw new BuildException("Balise inconnue: " + currentNode.getNodeName()
                                         + "(" + currentNode.getNodeValue() + ")");
            }
        }
    }


    private static void addColumn(Node currentNode, Preference pref) {
        Column column = createColumn(currentNode, false);

        pref.getColumns().add(column);
    }


    private static void addHiddenColumn(Node currentNode, Preference pref) {
        Column column = createColumn(currentNode, true);

        pref.getHiddenColumns().add(column);
    }


    private static Column createColumn(Node currentNode, boolean isHidden) {
        Column column = new Column();
        String fieldName = getAttribute(currentNode, "fieldName");
        column.setFieldName(fieldName);
        if (!isHidden || currentNode.getAttributes().getNamedItem("label") != null) {
            column.setLabel(getAttribute(currentNode, "label"));
        }
        else {
            column.setLabel(fieldName);
        }
        if (currentNode.getAttributes().getNamedItem("maxSize") != null) {
            column.setMaxSize(Integer.parseInt(getAttribute(currentNode, "maxSize")));
        }
        if (currentNode.getAttributes().getNamedItem("minSize") != null) {
            column.setMinSize(Integer.parseInt(getAttribute(currentNode, "minSize")));
        }
        if (currentNode.getAttributes().getNamedItem("preferredSize") != null) {
            column.setPreferredSize(Integer.parseInt(getAttribute(currentNode, "preferredSize")));
        }
        if (currentNode.getAttributes().getNamedItem("format") != null) {
            column.setFormat(getAttribute(currentNode, "format"));
        }
        if (currentNode.getAttributes().getNamedItem("sorter") != null) {
            column.setSorter(getAttribute(currentNode, "sorter"));
        }
        if (currentNode.getAttributes().getNamedItem("renderer") != null) {
            column.setRenderer(getAttribute(currentNode, "renderer"));
        }
        if (currentNode.getAttributes().getNamedItem("summable") != null) {
            boolean aBoolean = Boolean.valueOf(getAttribute(currentNode, "summable"));
            column.setSummable(aBoolean);
        }
        if (currentNode.getAttributes().getNamedItem("summableLabel") != null) {
            if (column.isSummable()) {
                throw new BuildException(
                      "Impossible d'utiliser les balises summable et summableLabel en même temps : "
                      + column.getFieldName());
            }
            column.setSummableLabel(getAttribute(currentNode, "summableLabel"));
        }
        return column;
    }


    private static RequestFactory buildFactory(Node node, Class defaultClass)
          throws NoSuchMethodException, InvocationTargetException,
                 IllegalAccessException, InstantiationException, ClassNotFoundException {
        String id = node.getFirstChild().getNodeValue();
        String type = getAttribute(node, "type");

        Class factoryClass = defaultClass;
        if ("command".equals(type)) {
            factoryClass = CommandFactory.class;
        }
        else if (type != null) {
            factoryClass = Class.forName(type);
        }

        Constructor constructor = factoryClass.getConstructor(String.class);
        return (RequestFactory)constructor.newInstance(id);
    }


    private static String getAttribute(Node node, String attributeName) {
        String value = null;

        Node attNode = node.getAttributes().getNamedItem(attributeName);
        if (attNode != null) {
            value = attNode.getNodeValue();
        }

        return value;
    }


    private static Icon loadIcon(String fileName) {
        return new ImageIcon(PreferenceFactory.class.getResource(fileName));
    }


    /**
     * Erreur lors de la construction d'une Preference.
     */
    public static final class BuildException extends RuntimeException {
        private Exception causedBy;


        public BuildException(String msg) {
            super(msg);
        }


        public BuildException(String msg, Exception cause) {
            super(msg);
            causedBy = cause;
        }


        @Override
        public void printStackTrace(PrintWriter writer) {
            super.printStackTrace(writer);
            if (causedBy != null) {
                writer.println(" ---- cause ---- ");
                causedBy.printStackTrace(writer);
            }
        }


        @Override
        public void printStackTrace() {
            super.printStackTrace();
            if (causedBy != null) {
                System.err.println(" ---- cause ---- ");
                causedBy.printStackTrace();
            }
        }


        public void printStackTrace(PrintStream stream) {
            super.printStackTrace(stream);
            if (causedBy != null) {
                stream.println(" ---- cause ---- ");
                causedBy.printStackTrace(stream);
            }
        }
    }

    /**
     * Handler d'erreur.
     */
    private static class SimpleErrorHandler implements ErrorHandler {
        public void error(SAXParseException sAXParseException) {
            LOGGER.error(buildMsg(sAXParseException), sAXParseException);
        }


        public void fatalError(SAXParseException sAXParseException) {
            LOGGER.fatal(buildMsg(sAXParseException), sAXParseException);
        }


        public void warning(SAXParseException sAXParseException) {
            LOGGER.warn(buildMsg(sAXParseException), sAXParseException);
        }


        private String buildMsg(SAXParseException ex) {
            return "[L=" + ex.getLineNumber() + " C=" + ex.getColumnNumber() + "] "
                   + ex.toString();
        }
    }
}
