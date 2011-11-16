package net.codjo.mad.server.plugin;
import net.codjo.agent.Agent;
import net.codjo.agent.behaviour.HitmanBehaviour;
import net.codjo.agent.behaviour.OneShotBehaviour;
import net.codjo.agent.behaviour.SequentialBehaviour;
import net.codjo.mad.server.plugin.AmbassadorRemovalBehaviour.AmbassadorData;
import org.apache.log4j.Logger;
/**
 *
 */
class KamikazeAgent extends Agent {
    private static final Logger LOG = Logger.getLogger(KamikazeAgent.class);
    private final AmbassadorData ambassadorData;


    KamikazeAgent(AmbassadorData data) {
        this.ambassadorData = data;
    }


    @Override
    protected void setup() {
        addBehaviour(SequentialBehaviour
              .wichStartsWith(new CleanUpBehaviour())
              .andThen(new HitmanBehaviour(this, ambassadorData.getAmbassadorAID()))
              .andThen(new SuicideBehaviour()));
    }


    private void log(String message) {
        LOG.info(message + " - (" + ambassadorData.getPoolKey().encode() + ")");
    }


    private class SuicideBehaviour extends OneShotBehaviour {

        @Override
        protected void action() {
            log("\tSuicide de " + getAgent().getAID().getLocalName());
            getAgent().die();
        }
    }
    private class CleanUpBehaviour extends OneShotBehaviour {
        @Override
        protected void action() {
            log("\tSnipper sur " + ambassadorData.getAmbassadorAID().getLocalName() + " programmé");
        }
    }
}
