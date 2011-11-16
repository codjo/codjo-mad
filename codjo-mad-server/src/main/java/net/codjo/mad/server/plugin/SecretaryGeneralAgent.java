/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.mad.server.plugin;
import net.codjo.agent.Agent;
/**
 * Agent Serveur.
 */
class SecretaryGeneralAgent extends Agent {
    private final BackPack backPack;


    SecretaryGeneralAgent() {
        backPack = BackPackBuilder.init().get();
    }


    SecretaryGeneralAgent(BackPack backPack) {
        this.backPack = backPack;
    }


    @Override
    protected void setup() {
        AmbassadorRemovalBehaviour behaviour = new AmbassadorRemovalBehaviour();

        addBehaviour(behaviour);
        addBehaviour(new InstituteAmbassadorParticipant(backPack, behaviour));
    }
}
