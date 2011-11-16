/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.mad.server.handler.aspect;
import net.codjo.mad.server.handler.XMLUtils;
import net.codjo.reflect.util.ReflectHelper;
import java.beans.IntrospectionException;
import java.util.Iterator;
import java.util.Map;
/**
 * Classe abstraite facilitant l'implantation de {@link Query}
 *
 * @version $Revision: 1.4 $
 */
public abstract class AbstractQuery implements Query {
    public void toBean(Object bean) throws IntrospectionException {
        toBean(bean, null);
    }


    public void toBean(Object bean, PropertyFilter filter) throws IntrospectionException {
        ReflectHelper helper = new ReflectHelper(bean.getClass());

        Map rowToMap = rowToMap();
        for (Iterator iter = helper.shortPropertyNames(); iter.hasNext();) {
            String propertyName = (String)iter.next();

            if (rowToMap.containsKey(propertyName)) {
                Class propertyClass = helper.getPropertyClass(propertyName);
                String propertyValue = (String)rowToMap.get(propertyName);

                if (filter == null || filter.acceptValue(propertyName, propertyValue)) {
                    helper.setPropertyValue(propertyName, bean,
                                            toObject(propertyClass, propertyValue));
                }
            }
        }
    }


    private Object toObject(Class propertyClass, String value) {
        //noinspection unchecked
        return XMLUtils.convertFromStringValue(propertyClass, value);
    }


    public static interface PropertyFilter {
        boolean acceptValue(String propertyName, String value);
    }
}
