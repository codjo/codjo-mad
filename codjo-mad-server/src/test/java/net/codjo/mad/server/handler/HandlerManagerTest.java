/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.mad.server.handler;
import net.codjo.aspect.AspectConfigException;
import net.codjo.aspect.AspectContext;
import net.codjo.aspect.AspectManager;
import net.codjo.aspect.JoinPoint;
import net.codjo.aspect.util.TransactionalPoint;
import net.codjo.mad.common.TestHelper;
import net.codjo.mad.server.MadRequestContext;
import net.codjo.mad.server.MadRequestContextMock;
import net.codjo.mad.server.MadTransactionMock;
import net.codjo.mad.server.handler.aspect.Keys;
import net.codjo.mad.server.handler.aspect.Query;
import net.codjo.mad.server.handler.aspect.QueryManager;
import net.codjo.mad.server.plugin.BackPackBuilder;
import net.codjo.security.common.api.SecurityContext;
import net.codjo.security.server.api.DefaultUserFactory;
import net.codjo.security.server.api.UserFactoryMock;
import net.codjo.test.common.LogString;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;

public class HandlerManagerTest {
    private static final String USER_NAME = new UserFactoryMock().getUser(null, null).getId().getLogin();
    private static final String INSERT_REQUEST =
          "<insert request_id=\"1\">"
          + "<id>newCodificationPtf</id>"
          + "<row>"
          + TestHelper.getFieldXmlTag("SICOVAM", "6969")
          + TestHelper.getFieldXmlTag("ISIN", "0451")
          + TestHelper.getFieldXmlTag("LABEL", "COUCOU D")
          + "</row>"
          + "</insert>";
    private static final String UPDATE_REQUEST =
          "<update request_id=\"2\">"
          + "<id>updateCodificationPtf</id>"
          + "<primarykey>"
          + TestHelper.getFieldXmlTag("PIMS", "609") + "</primarykey>" + "<row>"
          + TestHelper.getFieldXmlTag("SICOVAM", "6969")
          + TestHelper.getFieldXmlTag("ISIN", "0451")
          + TestHelper.getFieldXmlTag("LABEL", "COUCOU D")
          + "</row>"
          + "</update>";
    private String requests =
          "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
          + "<requests>"
          + "<audit><user>" + USER_NAME + "</user></audit>"
          + INSERT_REQUEST
          + UPDATE_REQUEST
          + "</requests>";


    @Test
    public void test_dispatchHandler() throws Exception {
        HandlerMock insertHandler = new HandlerMock("newCodificationPtf", INSERT_REQUEST, "A");
        HandlerMock updateHandler = new HandlerMock("updateCodificationPtf", UPDATE_REQUEST, "B");

        DefaultHandlerMap defaultHandlerMap = new DefaultHandlerMap();
        defaultHandlerMap.addHandler(insertHandler);
        defaultHandlerMap.addHandler(updateHandler);

        HandlerManager manager = new HandlerManager(defaultHandlerMap, BackPackBuilder.init().get());
        final HandlerContext handlerContext = newHandlerContext();
        String result = manager.executeRequests(requests, handlerContext);

        assertTrue("La requete insert est traité", insertHandler.verify());
        assertEquals(USER_NAME, insertHandler.getContext().getUser());
        assertTrue("La requete update est traité", updateHandler.verify());
        assertEquals(USER_NAME, updateHandler.getContext().getUser());
        assertEquals("Le resultat est correctement collecté",
                     "<?xml version=\"1.0\"?><results>AB</results>",
                     result);

        MadTransactionMock transactionMock = ((MadTransactionMock)handlerContext.getTransaction());
        transactionMock.assertLog("begin(), flush(), commit()");
    }


    @Test
    public void test_dispatchHandler_aspect() throws Exception {
        String handlerIdA = "newCodificationPtf";
        String handlerIdB = "updateCodificationPtf";

        DefaultHandlerMap defaultHandlerMap = new DefaultHandlerMap();
        defaultHandlerMap.addHandler(new HandlerMock(handlerIdA, INSERT_REQUEST, "A"));
        defaultHandlerMap.addHandler(new HandlerMock(handlerIdB, UPDATE_REQUEST, "B"));

        AspectManager aspectManager = new AspectManager();
        aspectManager.addAspect("aspect1", newJoinPoint(handlerIdA), MyAspect.class);

        HandlerManager manager = new HandlerManager(defaultHandlerMap,
                                                    BackPackBuilder.init()
                                                          .setAspectManager(aspectManager)
                                                          .get());

        HandlerContext handlerContext = newHandlerContext();
        manager.executeRequests(requests, handlerContext);

        assertEquals(handlerContext.getTransaction(),
                     MyAspect.aspectContext.get(HandlerManager.MAD_TX_MANAGER));
        assertEquals(handlerContext.getUser(), MyAspect.aspectContext.get(Keys.USER_NAME));
        assertEquals(handlerContext.getUserProfil(), MyAspect.aspectContext.get(Keys.USER));
        assertEquals(handlerContext.getTxConnection(),
                     MyAspect.aspectContext.get(TransactionalPoint.CONNECTION));
        assertEquals("[" + handlerIdA + ", " + handlerIdB + "]",
                     toString(MyAspect.aspectContext, TransactionalPoint.ARGUMENT));

        QueryManager queryManager = ((QueryManager)MyAspect.aspectContext.get(QueryManager.class.getName()));
        assertEquals(handlerContext.getUser(), queryManager.getUser());
        assertEquals("[" + handlerIdA + "]", toString(queryManager.getQuery(handlerIdA)));
        assertEquals("[" + handlerIdB + "]", toString(queryManager.getQuery(handlerIdB)));
        assertEquals("[" + handlerIdA + ", " + handlerIdB + "]", toString(queryManager.getHandlerIdList()));
    }


    private String toString(Query[] query) {
        List<String> ids = new ArrayList<String>();
        for (Query aQuery : query) {
            ids.add(aQuery.getId());
        }
        return ids.toString();
    }


    private static JoinPoint[] newJoinPoint(String handlerId) {
        JoinPoint joinPoint = new JoinPoint();
        joinPoint.setCall(JoinPoint.CALL_BEFORE);
        joinPoint.setArgument(handlerId);
        joinPoint.setPoint("handler.execute");
        return new JoinPoint[]{joinPoint};
    }


    /**
     * Verifie que si un handler echoue (le 2eme), le manager ne renvoie qu'une notification d'erreur avec le
     * bon request_id.
     */
    @Test
    public void test_dispatchHandler_error() throws Exception {
        HandlerMock insertHandler = new HandlerMock("newCodificationPtf", INSERT_REQUEST, "A");
        HandlerMock updateHandler =
              new HandlerMock("updateCodificationPtf", UPDATE_REQUEST, new RuntimeException("err"));

        DefaultHandlerMap defaultHandlerMap = new DefaultHandlerMap();
        defaultHandlerMap.addHandler(insertHandler);
        defaultHandlerMap.addHandler(updateHandler);

        HandlerManager manager = new HandlerManager(defaultHandlerMap, BackPackBuilder.init().get());
        final HandlerContext handlerContext = newHandlerContext();
        try {
            manager.executeRequests(requests, handlerContext);
            fail("L'execution des requetes doit echouer");
        }
        catch (RequestFailureException e) {
            assertTrue("La requete insert est traité", insertHandler.verify());
            assertTrue("La requete update est traité", updateHandler.verify());
            assertEquals("Le resultat est correctement collecté",
                         "<?xml version=\"1.0\"?><results><error request_id = \"2\">"
                         + "<label><![CDATA[err]]></label>"
                         + "<type>class java.lang.RuntimeException</type>"
                         + "</error></results>", e.getErrorXml());

            MadTransactionMock transactionMock = ((MadTransactionMock)handlerContext.getTransaction());
            transactionMock.assertLog("begin(), rollback()");
        }
    }


    @Test
    public void test_dispatchHandler_select() throws Exception {
        String selectRequest =
              "<select request_id=\"1\">"
              + "    <id>selectAllCodificationPtf</id>"
              + "    <attributes>"
              + "        <name>pimsCode</name>"
              + "    </attributes>"
              + "    <page num=\"1\" rows=\"30\"/>"
              + "</select>";

        String requestDoc =
              "<?xml version=\"1.0\"?>"
              + "<requests>"
              + "<audit><user>GONNOT</user></audit>"
              + selectRequest
              + "</requests>";

        HandlerMock selectHandler = new HandlerMock("selectAllCodificationPtf", selectRequest, "A");

        DefaultHandlerMap defaultHandlerMap = new DefaultHandlerMap();
        defaultHandlerMap.addHandler(selectHandler);
        HandlerManager manager = new HandlerManager(defaultHandlerMap, BackPackBuilder.init().get());

        String result = manager.executeRequests(requestDoc, newHandlerContext());

        assertEquals("Le resultat est correctement collecté",
                     "<?xml version=\"1.0\"?><results>A</results>",
                     result);
        assertTrue("La requete insert est traité", selectHandler.verify());
    }


    @Test
    public void test_securityFailure() throws Exception {
        String selectRequest =
              "<select request_id=\"1\">"
              + "    <id>selectAllCodificationPtf</id>"
              + "    <attributes>"
              + "        <name>pimsCode</name>"
              + "    </attributes>"
              + "    <page num=\"1\" rows=\"30\"/>"
              + "</select>";

        String requestDoc =
              "<?xml version=\"1.0\"?>"
              + "<requests>"
              + "<audit><user>GONNOT</user></audit>"
              + selectRequest
              + "</requests>";
        HandlerManager manager = newManagerWithOneHandler("selectAllCodificationPtf", selectRequest, "A");

        HandlerContext ctxt = newHandlerContext(new DefaultUserFactory("/conf/test/role.xml"));

        try {
            manager.executeRequests(requestDoc, ctxt);
            fail("Echec car le handler 'selectAllCodificationPtf' n'est pas autorisé");
        }
        catch (RequestFailureException ex) {
            assertEquals("Vous n'avez pas les droits pour effectuer ce traitement "
                         + "(selectAllCodificationPtf)", ex.getCausedBy().getMessage());
        }
    }


    @Test
    public void test_securityOk() throws Exception {
        String selectRequest =
              "<select request_id=\"1\">"
              + "    <id>selectFundPriceById</id>"
              + "    <attributes>"
              + "        <name>pimsCode</name>"
              + "    </attributes>"
              + "    <page num=\"1\" rows=\"30\"/>"
              + "</select>";

        String requestDoc =
              "<?xml version=\"1.0\"?>"
              + "<requests>"
              + "<audit><user>GONNOT</user></audit>"
              + selectRequest
              + "</requests>";

        HandlerManager manager = newManagerWithOneHandler("selectFundPriceById", selectRequest, "A");

        HandlerContext ctxt = newHandlerContext(new DefaultUserFactory("/conf/" + USER_NAME + "/role.xml"));
        String result = manager.executeRequests(requestDoc, ctxt);
        assertEquals("Le resultat est correctement collecté",
                     "<?xml version=\"1.0\"?><results>A</results>",
                     result);
    }


    @Test
    public void test_handlerListener() throws Exception {
        HandlerMock insertHandler = new HandlerMock("newCodificationPtf", INSERT_REQUEST, "A");
        HandlerMock updateHandler = new HandlerMock("updateCodificationPtf", UPDATE_REQUEST, "B");
        DefaultHandlerMap defaultHandlerMap = new DefaultHandlerMap();
        defaultHandlerMap.addHandler(insertHandler);
        defaultHandlerMap.addHandler(updateHandler);

        LogString log = new LogString();
        HandlerManager manager =
              new HandlerManager(
                    defaultHandlerMap,
                    BackPackBuilder.init()
                          .setHandlerListeners(Arrays.<HandlerListener>asList(new HandlerListenerMock(log)))
                          .get());
        manager.executeRequests(requests, newHandlerContext());

        log.assertContent("handlerStarted(newCodificationPtf),"
                          + " handlerStopped(newCodificationPtf),"
                          + " handlerStarted(updateCodificationPtf),"
                          + " handlerStopped(updateCodificationPtf)");
    }


    private HandlerManager newManagerWithOneHandler(String handlerId, String selectRequest,
                                                    final String handlerResult) throws AspectConfigException {
        HandlerMock selectHandler = new HandlerMock(handlerId, selectRequest, handlerResult);

        DefaultHandlerMap handlers = new DefaultHandlerMap();
        handlers.addHandler(selectHandler);
        return new HandlerManager(handlers, BackPackBuilder.init().get());
    }


    private HandlerContext newHandlerContext(DefaultUserFactory userHome) {
        MadRequestContext madRequestContext =
              new MadRequestContextMock(new MadTransactionMock(new LogString()),
                                        new MockSecurityContext(),
                                        userHome);
        return new HandlerContext(madRequestContext);
    }


    private HandlerContext newHandlerContext() {
        MadRequestContext madRequestContext =
              new MadRequestContextMock(new MadTransactionMock(new LogString()),
                                        SecurityContextMock.userIsInAllRole(),
                                        new UserFactoryMock().mockUserIsAllowedTo(true));
        return new HandlerContext(madRequestContext);
    }


    private static String toString(AspectContext context, String argument) {
        return toString((String[])context.get(argument));
    }


    private static String toString(String[] list) {
        return Arrays.asList(list).toString();
    }


    private static class MockSecurityContext implements SecurityContext {
        public boolean isCallerInRole(String roleId) {
            return "administration_vl".equals(roleId);
        }
    }
}
