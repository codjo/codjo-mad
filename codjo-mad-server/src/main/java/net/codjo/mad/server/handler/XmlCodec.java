package net.codjo.mad.server.handler;
import net.codjo.aspect.AspectContext;
import net.codjo.aspect.util.TransactionalPoint;
import net.codjo.mad.server.handler.aspect.AspectBranchId;
import net.codjo.mad.server.handler.aspect.Keys;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.collections.AbstractCollectionConverter;
import com.thoughtworks.xstream.core.JVM;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.mapper.Mapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
/**
 *
 */
public class XmlCodec {

    private static final List<String> FIELDS_TO_EXCLUDE = new ArrayList<String>();


    static {
        FIELDS_TO_EXCLUDE.add(HandlerManager.MAD_TX_MANAGER);
        FIELDS_TO_EXCLUDE.add(Keys.USER);
        FIELDS_TO_EXCLUDE.add(TransactionalPoint.CONNECTION);
    }


    private XmlCodec() {
    }


    public static String toXml(AspectBranchId branchId) {
        XStream xStream = new XStream();
        return xStream.toXML(branchId);
    }


    public static AspectBranchId extractAspectBranchId(String xml) {
        XStream xStream = new XStream(new DomDriver());
        return (AspectBranchId)xStream.fromXML(xml);
    }


    public static String toXml(AspectContext aspectContext) {
        XStream xStream = new XStream();
        xStream.registerConverter(new FilterAspectContextConverter(xStream.getMapper()));
        return xStream.toXML(aspectContext);
    }


    public static AspectContext extractAspectContext(String xml) {
        XStream xStream = new XStream(new DomDriver());
        return (AspectContext)xStream.fromXML(xml);
    }


    private static class FilterAspectContextConverter extends AbstractCollectionConverter {

        private FilterAspectContextConverter(Mapper mapper) {
            super(mapper);
        }


        @Override
        public boolean canConvert(Class type) {
            return type.equals(HashMap.class)
                   || type.equals(Hashtable.class)
                   || (JVM.is14() && "java.util.LinkedHashMap".equals(type.getName()));
        }


        @Override
        public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
            Map map = (Map)source;
            for (Object o : map.entrySet()) {
                Entry entry = (Entry)o;
                String key = entry.getKey().toString();
                if (FIELDS_TO_EXCLUDE.contains(key)
                    || key.endsWith("__inner_aspect_data.AspectSynchronizerCounter")
                    || key.endsWith("__wrapper")) {
                    continue;
                }
                writer.startNode(mapper().serializedClass(Entry.class));

                writeItem(entry.getKey(), context, writer);
                writeItem(entry.getValue(), context, writer);

                writer.endNode();
            }
        }


        @Override
        public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
            Map map = (Map)createCollection(context.getRequiredType());
            populateMap(reader, context, map);
            return map;
        }


        protected void populateMap(HierarchicalStreamReader reader, UnmarshallingContext context, Map map) {
            while (reader.hasMoreChildren()) {
                reader.moveDown();

                reader.moveDown();
                Object key = readItem(reader, context, map);
                reader.moveUp();

                reader.moveDown();
                Object value = readItem(reader, context, map);
                reader.moveUp();

                map.put(key, value);

                reader.moveUp();
            }
        }
    }
}
