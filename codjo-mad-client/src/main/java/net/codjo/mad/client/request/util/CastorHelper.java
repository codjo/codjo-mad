package net.codjo.mad.client.request.util;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
/**
 * Classe de type helper pour encapsuler un appel a castor.
 *
 * @author $Author: gaudefr $
 * @version $Revision: 1.6 $
 */
public final class CastorHelper {
    private CastorHelper() {}

    public static String marshaller(Object obj, String mappingFile)
            throws MappingException, IOException, ValidationException, MarshalException {
        StringWriter writer = new StringWriter();
        Marshaller marshaller = new Marshaller(writer);
        marshaller.setMapping(getMapping(mappingFile, obj.getClass()));
        marshaller.marshal(obj);
        return writer.toString();
    }


    /**
     * Construction de l'objet décrit dans <code>xmlString</code> de type
     * <code>objClass</code> avec un fichier de mapping .
     *
     * @param xmlString String xml
     * @param objClass la classe de l'objet a instancier
     * @param mappingFile le fichier de mapping
     *
     * @return un objet instancié.
     *
     * @exception MappingException
     * @exception IOException
     * @exception ValidationException
     * @exception MarshalException
     */
    public static Object unmarshaller(String xmlString, Class objClass, String mappingFile)
            throws MappingException, IOException, ValidationException, MarshalException {
        Unmarshaller unmarshaller = new Unmarshaller(objClass);
        unmarshaller.setMapping(getMapping(mappingFile, objClass));
        return unmarshaller.unmarshal(new InputSource(new StringReader(xmlString)));
    }


    private static Mapping getMapping(String mappingFile, Class objClass)
            throws IOException, MappingException {
        Mapping mapping = new Mapping();
        mapping.setEntityResolver(new MyResolver(objClass));
//        System.out.println("[BOBO]-----> " + mappingFile + " : " + objClass
//                .getResourceAsStream(mappingFile));
        mapping.loadMapping(new InputSource(objClass.getResourceAsStream(mappingFile)));
        return mapping;
    }

    /**
     * Resolver utilisée pour les imports de <code>classesMapping.xml</code>.
     *
     * @author $Author: gaudefr $
     * @version $Revision: 1.6 $
     */
    private static class MyResolver implements EntityResolver {
        private Class clazz;

        MyResolver(Class clazz) {
            this.clazz = clazz;
        }

        public InputSource resolveEntity(String publicId, String systemId) {
//            System.out.println("publicId " + publicId);
//            System.out.println("systemId " + systemId);
            if (systemId.endsWith("classesMapping.xml")) {
//                System.out.println("OK" + clazz.getResourceAsStream("classesMapping.xml"));
                return new InputSource(clazz.getResourceAsStream("classesMapping.xml"));
            }
            else {
                return null;
            }
        }
    }
}
