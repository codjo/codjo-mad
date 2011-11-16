/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.mad.server.handler;
import net.codjo.aspect.AspectConfigException;
import net.codjo.aspect.AspectContext;
import net.codjo.aspect.util.PointRunner;
import net.codjo.aspect.util.PointRunnerException;
import net.codjo.aspect.util.TransactionalPoint;
import net.codjo.mad.common.Log;
import net.codjo.mad.server.handler.aspect.Keys;
import net.codjo.mad.server.handler.aspect.QueryManager;
import net.codjo.mad.server.handler.aspect.QueryManagerBuilder;
import net.codjo.mad.server.plugin.BackPack;
import java.io.IOException;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

class HandlerManager {
    static final String MAD_TX_MANAGER = "net.codjo.mad.MadTx";
    private HandlerMap handlerMap = null;
    private QueryManagerBuilder queryManagerBuilder = new QueryManagerBuilder();
    private WorkflowTransactionalPoint point;
    private List<HandlerListener> listeners;


    HandlerManager(HandlerMap handlerMaps, BackPack backPack) throws AspectConfigException {
        if (handlerMaps == null) {
            throw new IllegalArgumentException("La liste des handlers est null");
        }
        this.listeners = backPack.getHandlerListeners();
        this.handlerMap = handlerMaps;
        this.point = new WorkflowTransactionalPoint(backPack.getAspectManager(),
                                                    new TxManagerWrapper(),
                                                    backPack.getAspectBranchLauncherFactory());
    }


    public String executeRequests(String requests, HandlerContext handlerContext)
          throws RequestFailureException {
        Connection txConnection = null;

        try {
            Document document = parse(requests);
            handlerContext.setUser(extractUser(document));

            txConnection = handlerContext.getTxConnection();
            QueryManager queryManager = queryManagerBuilder.build(document);

            AspectContext aspectContext = new AspectContext();
            fillAspectContext(aspectContext, handlerContext, queryManager.getHandlerIdList());
            aspectContext.put(QueryManager.class.getName(), queryManager);

            ExecuteHandlerPointRunner runner = new ExecuteHandlerPointRunner(handlerContext, document);

            point.run(aspectContext, runner);

            return runner.getResult();
        }
        catch (PointRunnerException pointRunnerException) {
            if (pointRunnerException.getCause() instanceof RequestFailureException) {
                throw (RequestFailureException)pointRunnerException.getCause();
            }
            else {
                throw new RequestFailureException("runtimeError",
                                                  (Exception)pointRunnerException.getCause());
            }
        }
        catch (Exception exception) {
            throw new RequestFailureException("runtimeError", exception);
        }
        finally {
            if (txConnection != null) {
                try {
                    txConnection.close();
                }
                catch (SQLException sqlException) {
                    throw new RequestFailureException("runtimeError can't close txConnection",
                                                      sqlException);
                }
            }
        }
    }


    static void fillAspectContext(AspectContext context,
                                  HandlerContext handlerContext,
                                  String[] handlerIdList) throws SQLException {
        context.put(MAD_TX_MANAGER, handlerContext.getTransaction());
        context.put(Keys.USER_NAME, handlerContext.getUserProfil().getId().getLogin());
        context.put(Keys.USER, handlerContext.getUserProfil());
        context.put(TransactionalPoint.CONNECTION, handlerContext.getTxConnection());
        context.put(TransactionalPoint.ARGUMENT, handlerIdList);
    }


    private String extractUser(Document document) throws SAXException {
        NodeList nodes = document.getFirstChild().getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            if ("audit".equals(nodes.item(i).getNodeName())) {
                return XMLUtils.getNode(nodes.item(i), "user").getFirstChild()
                      .getNodeValue();
            }
        }
        throw new SAXException("Balise audit introuvable !");
    }


    private Handler getHandler(String id) {
        Handler handler = handlerMap.getHandler(id);
        if (handler == null) {
            Log.error("handler list : " + handlerMap.getHandlerIdSet());
            throw new IllegalArgumentException("Handler introuvable : " + id);
        }
        return handler;
    }


    private String dispatch(Document document, HandlerContext handlercontext)
          throws RequestFailureException, SAXException {
        StringBuilder results = new StringBuilder("<?xml version=\"1.0\"?><results>");

        NodeList nodes = document.getFirstChild().getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            try {
                if ("audit".equals(nodes.item(i).getNodeName())) {
                    continue;
                }
                if ("#text".equals(nodes.item(i).getNodeName())) {
                    // Noeud ajoute lorsque le manager est execute dans le container.
                    continue;
                }
                String id = XMLUtils.getNodeValue(nodes.item(i), "id");
                Handler handler = getHandler(id);
                handler.setContext(handlercontext);
                if (!handlercontext.isAllowedTo(id)) {
                    throw new SecurityException(
                          "Vous n'avez pas les droits pour effectuer ce traitement (" + id + ")");
                }

                fireHandlerStarted(handlercontext, handler);
                String result = handler.proceed(nodes.item(i));
                fireHandlerStopped(handlercontext, handler);

                results.append(result);
            }
            catch (HandlerException handlerException) {
                Exception cause = handlerException;
                if (handlerException.getCausedBy() != null
                    && handlerException.getMessage() == null) {
                    cause = handlerException.getCausedBy();
                }
                throw new RequestFailureException(XMLUtils.getAttribute(nodes.item(i), "request_id"), cause);
            }
            catch (Exception exception) {
                throw new RequestFailureException(XMLUtils.getAttribute(nodes.item(i), "request_id"),
                                                  exception);
            }
        }
        return results.append("</results>").toString();
    }


    private Document parse(String requests)
          throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new InputSource(new StringReader(requests)));
    }


    private void fireHandlerStopped(HandlerContext handlercontext, Handler handler) {
        for (HandlerListener listener : listeners) {
            listener.handlerStopped(handler, handlercontext);
        }
    }


    private void fireHandlerStarted(HandlerContext handlercontext, Handler handler) {
        for (HandlerListener listener : listeners) {
            listener.handlerStarted(handler, handlercontext);
        }
    }


    private class ExecuteHandlerPointRunner implements PointRunner {
        private final HandlerContext handlerContext;
        private final Document document;
        private String result;


        ExecuteHandlerPointRunner(HandlerContext handlerContext, Document document) {
            this.handlerContext = handlerContext;
            this.document = document;
        }


        public String getResult() {
            return result;
        }


        private void setResult(String result) {
            this.result = result;
        }


        public void run() throws PointRunnerException {
            try {
                setResult(dispatch(document, handlerContext));
            }
            catch (Exception exception) {
                throw new PointRunnerException(exception);
            }
        }
    }
}
