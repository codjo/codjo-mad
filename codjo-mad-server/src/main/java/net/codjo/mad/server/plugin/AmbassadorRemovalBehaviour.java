/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.mad.server.plugin;
import net.codjo.agent.Aid;
import net.codjo.agent.ContainerFailureException;
import net.codjo.agent.UserId;
import net.codjo.agent.behaviour.AmsListenerBehaviour;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
/**
 * Comportement nettoyant les ambassadeur lorsque un president meurt.
 */
class AmbassadorRemovalBehaviour extends AmsListenerBehaviour {
    private static final Logger LOG = Logger.getLogger(AmbassadorRemovalBehaviour.class);
    private final Map<Aid, AmbassadorData> ambassadorDatas = new HashMap<Aid, AmbassadorData>();


    AmbassadorRemovalBehaviour() {
        setAgentDeathHandler(new KillAmbassadorHandler());
    }


    public void declare(Aid presidentAID, Aid ambassadorAID, UserId poolUserId) {
        ambassadorDatas.put(presidentAID, new AmbassadorData(ambassadorAID, poolUserId));
    }


    public boolean isKnownPresidentAID(Aid agentAID) {
        return ambassadorDatas.containsKey(agentAID);
    }


    protected void killAmbassadorOf(Aid presidentAID) {
        final AmbassadorData data = ambassadorDatas.remove(presidentAID);

        if (data == null) {
            LOG.warn(String.format(
                  "Aucune information sur le président %s, suppression de l'ambassadeur annulée",
                  presidentAID));
            return;
        }

        try {
            KamikazeAgent kamikazeAgent = new KamikazeAgent(data);
            getAgent().getAgentContainer()
                  .acceptNewAgent(String.format("kamikaze-of-%s", data.getAmbassadorAID().getLocalName()),
                                  kamikazeAgent)
                  .start();
        }
        catch (ContainerFailureException e) {
            LOG.error("Impossible de créer le Kamikaze pour : "
                      + "\n président  :" + presidentAID.getLocalName()
                      + "\n ambassador :" + data.getAmbassadorAID().getLocalName()
                      + "\n user-id    :" + data.getPoolKey(), e);
        }
    }


    static class AmbassadorData {
        private Aid ambassadorAID;
        private UserId poolUserId;


        AmbassadorData(Aid ambassadorAID, UserId poolUserId) {
            this.ambassadorAID = ambassadorAID;
            this.poolUserId = poolUserId;
        }


        public Aid getAmbassadorAID() {
            return ambassadorAID;
        }


        public UserId getPoolKey() {
            return poolUserId;
        }
    }

    private class KillAmbassadorHandler implements AmsListenerBehaviour.EventHandler {
        public void handle(Aid agentId) {
            if (isKnownPresidentAID(agentId)) {
                killAmbassadorOf(agentId);
            }
        }
    }
}
