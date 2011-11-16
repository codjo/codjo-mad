/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.mad.server.plugin;
import net.codjo.agent.AclMessage;
import net.codjo.agent.Agent;
import net.codjo.agent.MessageTemplate;
import net.codjo.agent.UserId;
import net.codjo.agent.protocol.BasicQueryParticipantHandler;
import net.codjo.agent.protocol.RequestProtocol;
import net.codjo.mad.server.handler.Processor;
import net.codjo.mad.server.plugin.HandlerExecutor.HandlerExecutionMode;
import net.codjo.security.common.api.User;
import net.codjo.sql.server.ConnectionPool;
import java.util.List;
import org.exolab.castor.jdo.JDO;
/**
 * Agent gérant le pool de connection d'un client PresidentAgent.
 */
class AmbassadorAgent extends Agent {
    public static final String NAME = "Ambassador";
    private final UserId userId;
    private List groupsList;


    protected AmbassadorAgent(UserId userId) {
        this.userId = userId;

        MessageTemplate queryTemplate =
              MessageTemplate.and(MessageTemplate.matchPerformative(AclMessage.Performative.QUERY),
                                  MessageTemplate.matchProtocol(RequestProtocol.QUERY));

        addBehaviour(new net.codjo.agent.protocol.RequestParticipant(this,
                                                                   new BasicQueryParticipantHandler(this),
                                                                   queryTemplate));
    }


    protected AmbassadorAgent(UserId userId,
                              HandlerExecutor executor,
                              HandlerExecutionMode mode,
                              Processor processor,
                              ConnectionPool pool,
                              JDO jdo,
                              User user) {
        this(userId);
        addBehaviour(new RequestParticipant(executor, mode, processor, pool, jdo, user));
    }


    public UserId getUserId() {
        return userId;
    }


    public List getGroupsList() {
        return groupsList;
    }


    public void setGroupsList(List groupsList) {
        this.groupsList = groupsList;
    }
}
