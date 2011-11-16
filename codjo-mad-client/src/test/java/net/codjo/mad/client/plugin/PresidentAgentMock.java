package net.codjo.mad.client.plugin;
import net.codjo.agent.AclMessage;
import net.codjo.agent.AgentMock;
import net.codjo.agent.MessageTemplate;
import net.codjo.agent.UserId;
import net.codjo.test.common.LogString;
import net.codjo.util.system.EventSynchronizer;
/**
 * Mock d'un {@link net.codjo.mad.client.plugin.PresidentAgent}.
 */
class PresidentAgentMock extends PresidentAgent {
    private AgentMock myAgentMock;


    PresidentAgentMock() {
        super(UserId.createId("login", "password"), new EventSynchronizer<InstitueEvent>());
        setAmbassadorName("Ambassador");
        myAgentMock = new AgentMock();
    }


    public LogString getLog() {
        return myAgentMock.getLog();
    }


    @Override
    public void send(AclMessage aclMessage) {
        myAgentMock.send(aclMessage);
    }


    @Override
    public AclMessage receive(MessageTemplate template) {
        return myAgentMock.receive(template);
    }


    @Override
    public AclMessage receive() {
        return myAgentMock.receive();
    }


    public void mockReceiveContent(AclMessage aclMessage) {
        myAgentMock.mockReceive(aclMessage);
    }


    public void mockResponse(AclMessage aclMessage) {
        myAgentMock.mockResponse(aclMessage);
    }


    public AclMessage getLastSentMessage() {
        return myAgentMock.getLastSentMessage();
    }
}
