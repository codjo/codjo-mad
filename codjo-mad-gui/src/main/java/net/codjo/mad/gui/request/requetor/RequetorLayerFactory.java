package net.codjo.mad.gui.request.requetor;
import net.codjo.mad.common.structure.StructureReader;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
/**
 */
public final class RequetorLayerFactory {
    private static RequetorLayerFactory singleton;
    private String linksFilePath;
    private StructureReader structureReader;
    private LinkFamilyReader linkFamilyReader;


    private RequetorLayerFactory(String linksFilePath, StructureReader structures) {
        this.linksFilePath = linksFilePath;
        this.structureReader = structures;
    }


    public RequetorLayerFactory(LinkFamilyReader linkFamilyReader, StructureReader structures) {
        this.linkFamilyReader = linkFamilyReader;
        this.structureReader = structures;
    }


    StructureReader getStructureReader() {
        return structureReader;
    }


    LinkFamilyReader getLinkFamilyReader() throws SAXException, ParserConfigurationException, IOException {
        if (linkFamilyReader == null) {
            this.linkFamilyReader = new DefaultLinkFamilyReader(
                  RequetorLayerFactory.class.getResourceAsStream(linksFilePath), getStructureReader());
        }
        return linkFamilyReader;
    }


    static RequetorLayerFactory getFactory() {
        if (singleton == null) {
            throw new IllegalStateException("La factory requetor n'est pas initalisée ! "
                                            + "Veuillez appeler la méthode RequetorLayerFactory.initFactory(..)");
        }
        return singleton;
    }


    public static void initFactory(String linksFilePath, StructureReader structures) {
        singleton = new RequetorLayerFactory(linksFilePath, structures);
    }


    public static void initFactory(LinkFamilyReader linkFamilyReader, StructureReader structures) {
        singleton = new RequetorLayerFactory(linkFamilyReader, structures);
    }
}
