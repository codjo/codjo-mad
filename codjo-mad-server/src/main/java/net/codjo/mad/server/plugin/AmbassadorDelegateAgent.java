package net.codjo.mad.server.plugin;
import net.codjo.agent.AclMessage;
import net.codjo.agent.Agent;
import net.codjo.agent.behaviour.OneShotBehaviour;
import net.codjo.agent.util.IdUtil;
import net.codjo.mad.common.ZipUtil;
import net.codjo.mad.server.handler.Processor;
import net.codjo.mad.server.handler.RequestFailureException;
import net.codjo.mad.server.plugin.HandlerExecutor.HandlerExecutorCommand;
import net.codjo.security.common.api.User;
import net.codjo.sql.server.ConnectionPool;
import org.apache.log4j.Logger;
import org.exolab.castor.jdo.JDO;
/**
 *
 */
class AmbassadorDelegateAgent extends Agent {
    private static final Logger LOG = Logger.getLogger(AmbassadorDelegateAgent.class);
    private final AclMessage message;
    private final HandlerExecutor handlerExecutor;
    private final Processor processor;
    private final ConnectionPool pool;
    private final JDO jdo;
    private final User user;


    AmbassadorDelegateAgent(AclMessage message,
                            HandlerExecutor handlerExecutor,
                            Processor processor,
                            ConnectionPool pool,
                            JDO jdo,
                            User user) {
        this.message = message;
        this.handlerExecutor = handlerExecutor;
        this.processor = processor;
        this.pool = pool;
        this.jdo = jdo;
        this.user = user;
    }


    @Override
    protected void setup() {
        addBehaviour(new OneShotBehaviour() {
            @Override
            protected void action() {
                executeAndReply(getAgent(), message);
                die();
            }
        });
    }


    public String createNickName(String workName) {
        return new StringBuilder(workName).append(IdUtil.createUniqueId(this)).toString();
    }


    public void executeAndReply(Agent agent, AclMessage aclMessage) {
        AclMessage reply = aclMessage.createReply();
        String xmlRequests = aclMessage.getContent();
        AgentMadRequestContext agentMadRequestContext = new AgentMadRequestContext(pool, jdo, user);
        try {
            handlerExecutor.execute(xmlRequests, new AmbassadorHandlerExecutorCommand(xmlRequests,
                                                                                      agentMadRequestContext,
                                                                                      agent,
                                                                                      reply));
        }
        catch (Throwable error) {
            LOG.error("Une erreur grave est survenue durant le traitement de \n\t" + xmlRequests, error);
        }
        finally {
            agentMadRequestContext.close();
        }
    }


    private class AmbassadorHandlerExecutorCommand implements HandlerExecutorCommand {
        private final String xmlRequests;
        private final AgentMadRequestContext agentMadRequestContext;
        private final AclMessage reply;
        private Agent agent;


        private AmbassadorHandlerExecutorCommand(String xmlRequests,
                                                 AgentMadRequestContext agentMadRequestContext,
                                                 Agent agent,
                                                 AclMessage reply) {
            this.xmlRequests = xmlRequests;
            this.agentMadRequestContext = agentMadRequestContext;
            this.agent = agent;
            this.reply = reply;
        }


        public void setResultSenderAgent(Agent agent) {
            this.agent = agent;
        }


        public void execute() throws Exception {
            try {
                reply.setEncoding(AclMessage.ZIP_ENCODING);
                String result = processor.proceed(xmlRequests, agentMadRequestContext);
                reply.setByteSequenceContent(ZipUtil.zip(result));
            }
            catch (Exception e) {
                String errorNode = RequestFailureException.buildErrorNode(e, "Interne");
                reply.setContent(errorNode);
                throw e;
            }
            finally {
                agent.send(reply);
            }
        }
    }
}
