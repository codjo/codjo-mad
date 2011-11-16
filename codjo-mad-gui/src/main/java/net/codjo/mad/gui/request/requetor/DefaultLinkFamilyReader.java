package net.codjo.mad.gui.request.requetor;
import net.codjo.mad.common.structure.StructureReader;
import java.io.IOException;
import java.io.InputStream;
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
class DefaultLinkFamilyReader implements LinkFamilyReader {
    private Map<String, LinkFamily> allLinks;
    private StructureReader structureReader;


    DefaultLinkFamilyReader(InputStream resourceAsStream, StructureReader structureReader)
          throws IOException, ParserConfigurationException, SAXException {
        this.structureReader = structureReader;

        if (resourceAsStream == null) {
            throw new IllegalArgumentException("fichier de structure null");
        }

        allLinks = loadDefinition(resourceAsStream);
    }


    public LinkFamily getFamily(String id) {
        if (!allLinks.containsKey(id)) {
            throw new IllegalArgumentException("Famille " + id + " inconnue");
        }
        return allLinks.get(id);
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


    private static Document toDocument(final InputStream dataFile)
          throws IOException, ParserConfigurationException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new InputSource(dataFile));
    }


    private Map<String, LinkFamily> loadDefinition(InputStream structureFile)
          throws IOException, ParserConfigurationException, SAXException {
        Map<String, LinkFamily> allLinkFamily = new HashMap<String, LinkFamily>();
        Document doc = toDocument(structureFile);

        for (int i = 0; i < doc.getElementsByTagName("links").getLength(); i++) {
            Node linksNode = doc.getElementsByTagName("links").item(i);

            LinkFamily family =
                  new LinkFamily(getAttribute(linksNode, "id"),
                                 getAttribute(linksNode, "root"));

            loadLinkNode(linksNode, family);
            allLinkFamily.put(family.getId(), family);
        }
        return allLinkFamily;
    }


    private void loadLinkNode(Node linksNode, LinkFamily family) {
        for (int j = 0; j < linksNode.getChildNodes().getLength(); j++) {
            Node linkNode = linksNode.getChildNodes().item(j);

            if (Node.ELEMENT_NODE == linkNode.getNodeType()) {
                Link link =
                      new Link(getAttribute(linkNode, "from"),
                               getAttribute(linkNode, "to"), structureReader);

                family.addLink(link);
                loadKeyNode(linkNode, link);
            }
        }
    }


    private void loadKeyNode(Node linkNode, Link link) {
        for (int j = 0; j < linkNode.getChildNodes().getLength(); j++) {
            Node keyNode = linkNode.getChildNodes().item(j);

            if (Node.ELEMENT_NODE == keyNode.getNodeType()) {
                link.addKey(new Key(getAttribute(keyNode, "from"),
                                    getAttribute(keyNode, "to"),
                                    getAttribute(keyNode, "operator")));
            }
        }
    }
}