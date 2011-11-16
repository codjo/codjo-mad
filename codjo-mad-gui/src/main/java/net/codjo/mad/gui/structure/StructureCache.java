package net.codjo.mad.gui.structure;
import net.codjo.mad.client.request.CommandRequest;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.client.request.Row;
import net.codjo.mad.common.structure.DefaultStructureReader;
import net.codjo.mad.common.structure.StructureReader;
import net.codjo.mad.gui.request.util.RequestHelper;
import java.io.IOException;
import java.io.StringReader;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
/**
 * Cache du StructureReader cote client.
 *
 * @version $Revision: 1.6 $
 */
public class StructureCache {
    /**
     * Nom de la propriete referencant le cache dans le guiContext
     */
    public static final String STRUCTURE_CACHE = "STRUCTURE_CACHE";
    private StructureReader reader;


    public StructureCache()
          throws RequestException, IOException, ParserConfigurationException,
                 SAXException {
        Row rs = RequestHelper.sendSimpleRequest(new CommandRequest("getStructure"));

        String xmlFile = rs.getField(0).getValue();

        reader = new DefaultStructureReader(new StringReader(xmlFile));
    }


    public StructureReader getStructureReader() {
        return reader;
    }
}
