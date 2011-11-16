/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.mad.server.handler.aspect;
import net.codjo.mad.server.handler.XMLUtils;
import java.util.Collections;
import java.util.Map;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
/**
 *
 */
public class CommandQuery extends QueryManagerImpl.CreateQueryImpl {
    private Map<String, String> ourMap;


    public CommandQuery(Node query) {
        super(query);
    }


    public CommandQuery(Map<String, String> passedMap) {
        super(null);
        ourMap = passedMap;
    }


    @Override
    public Map<String, String> rowToMap() {
        try {
            if (ourMap == null) {
                ourMap = XMLUtils.getArguments(getQueryNode());
            }
        }
        catch (SAXException e) {
            try {
                if (ourMap == null) {
                    ourMap = XMLUtils.getSelectors(getQueryNode());
                }
            }
            catch (SAXException ex) {
                return Collections.emptyMap();
            }
        }

        return ourMap;
    }
}
