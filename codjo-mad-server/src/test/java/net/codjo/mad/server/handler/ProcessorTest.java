/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.mad.server.handler;
import net.codjo.mad.server.MadRequestContext;
import net.codjo.mad.server.MadRequestContextMock;
import net.codjo.mad.server.MadTransactionMock;
import net.codjo.mad.server.plugin.BackPackBuilder;
import net.codjo.security.server.api.UserFactoryMock;
import net.codjo.test.common.LogString;
import java.util.Arrays;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

public class ProcessorTest {
    private LogString log;
    private Processor processor;
    private UserFactoryMock userFactoryMock;


    @Before
    public void setUp() throws Exception {
        log = new LogString();
        userFactoryMock = new UserFactoryMock();
        processor = new Processor(
              createHandlerMap(),
              BackPackBuilder.init()
                    .setHandlerListeners(Arrays.<HandlerListener>asList(new HandlerListenerMock(log)))
                    .get());
    }


    @Test
    public void test_proceed() throws Exception {
        final String xmlRequests = createXmlRequestFor(HandlerMock.ID);

        MadRequestContext madRequestContext =
              new MadRequestContextMock(new MadTransactionMock(new LogString("tx", log)),
                                        SecurityContextMock.userIsInAllRole(),
                                        userFactoryMock.mockUserIsAllowedTo(true));
        final String result = processor.proceed(xmlRequests, madRequestContext);

        log.assertContent("tx.begin(), "
                          + "handlerStarted(" + HandlerMock.ID + "), "
                          + "" + HandlerMock.ID + ".proceed(), "
                          + "handlerStopped(" + HandlerMock.ID + "), "
                          + "tx.flush(), "
                          + "tx.commit()");
        assertEquals("<?xml version=\"1.0\"?><results>ok</results>", result);
    }


    @Test
    public void test_proceed_failure() throws Exception {
        final String xmlRequests = createXmlRequestFor(HandlerMock.ID);

        MadRequestContext madRequestContext =
              new MadRequestContextMock(new MadTransactionMock(new LogString("tx", log)),
                                        SecurityContextMock.userIsInAllRole(),
                                        userFactoryMock.mockUserIsAllowedTo(false));

        final String result = processor.proceed(xmlRequests, madRequestContext);

        log.assertContent("tx.begin(), tx.rollback()");
        assertTrue(result.contains("SecurityException"));
    }


    private HandlerMap createHandlerMap() {
        final DefaultHandlerMap handlerMap = new DefaultHandlerMap();
        final HandlerMock handlerMock = new HandlerMock();
        handlerMock.log = new LogString(HandlerMock.ID, log);
        handlerMap.addHandler(handlerMock);
        return handlerMap;
    }


    private String createXmlRequestFor(String handlertId) {
        return "<requests><audit><user>BLAZART</user></audit>"
               + "<command request_id='1'><id>" + handlertId + "</id></command></requests>";
    }
}
