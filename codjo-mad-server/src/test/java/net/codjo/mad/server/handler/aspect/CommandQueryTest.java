/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.mad.server.handler.aspect;
import net.codjo.mad.server.handler.XMLUtils;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import junit.framework.TestCase;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
/**
 *
 */
public class CommandQueryTest extends TestCase {

    public void test_cachedRowToMap() throws Exception {
        CommandQuery query = new CommandQuery(createCommandQueryNode());

        Map first = query.rowToMap();
        assertSame(first, query.rowToMap());
    }


    public void test_cachedRowToMapSelector() throws Exception {
        CommandQuery query = new CommandQuery(createCommandQueryNodeSelector());

        Map first = query.rowToMap();
        assertSame(first, query.rowToMap());
    }


    public void test_constructor() throws Exception {
        Map<String, String> map = new HashMap<String, String>();
        CommandQuery query = new CommandQuery(map);
        assertSame(map, query.rowToMap());
    }

    public void test_xmlSpaces() throws Exception {
        CommandQuery query = new CommandQuery(createCommandQueryNodeSelectorWithXmlSpaces());

        Map fieldMap = query.rowToMap();
        String fieldValue = (String)fieldMap.get("period2");
        assertEquals(fieldValue, "period\t Value    2");
    }

    private Node createCommandQueryNode()
          throws ParserConfigurationException, IOException, SAXException {
        return XMLUtils.parse(
              "<command request_id='53'>                                       "
              + "    <id>MyCommand</id>                                        "
              + "    <args>                                                    "
              + "        <field name='period'>periodValue</field>            "
              + "    </args>                                                   "
              + "</command>").getFirstChild();
    }


    private Node createCommandQueryNodeSelector()
          throws ParserConfigurationException, IOException, SAXException {
        return XMLUtils.parse(
              "<command request_id='53'>                                       "
              + "    <id>MyCommand</id>                                        "
              + "    <selector>                                                    "
              + "        <field name='period2'>periodValue2</field>            "
              + "    </selector>                                                   "
              + "</command>").getFirstChild();
    }

    private Node createCommandQueryNodeSelectorWithXmlSpaces()
          throws ParserConfigurationException, IOException, SAXException {
        return XMLUtils.parse(
              "<command request_id='53'>                                       "
              + "    <id>MyCommand</id>                                        "
              + "    <selector>                                                    "
              + "        <field name='period2'>period\t Value    2</field>            "
              + "    </selector>                                                   "
              + "</command>").getFirstChild();
    }
}
