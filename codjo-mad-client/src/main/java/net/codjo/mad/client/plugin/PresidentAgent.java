package net.codjo.mad.client.plugin;
import net.codjo.agent.Agent;
import net.codjo.agent.Aid;
import net.codjo.agent.UserId;
import net.codjo.mad.common.message.InstituteAmbassadorProtocol;
import net.codjo.util.system.EventSynchronizer;
/**
 * Agent de l'utilisateur sur le poste client.
 */
class PresidentAgent extends Agent {
    private String ambassadorName;
    private PresidentListener presidentListener;


    PresidentAgent(UserId userId, EventSynchronizer<InstitueEvent> synchronizer) {
        addBehaviour(new InstituteAmbassadorInitiator(userId,
                                                      new Aid(InstituteAmbassadorProtocol.SECRETARY_GENERAL_AGENT_NAME),
                                                      synchronizer));
    }


    @Override
    protected void setup() {
        setEnabledO2ACommunication(true, 10);
    }


    @Override
    protected void tearDown() {
        if (presidentListener != null) {
            presidentListener.stopped();
            presidentListener = null;
        }
    }


    public void setAmbassadorName(String ambassadorName) {
        this.ambassadorName = ambassadorName;
    }


    public String getAmbassadorName() {
        return ambassadorName;
    }


    public void setPresidentListener(PresidentListener presidentListener) {
        this.presidentListener = presidentListener;
    }
}
