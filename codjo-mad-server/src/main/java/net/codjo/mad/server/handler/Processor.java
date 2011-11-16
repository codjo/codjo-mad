/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.mad.server.handler;
import net.codjo.aspect.AspectConfigException;
import net.codjo.mad.common.Log;
import net.codjo.mad.server.MadRequestContext;
import net.codjo.mad.server.plugin.BackPack;
import net.codjo.mad.server.util.Chronometer;
/**
 * Classe responsable de processing des requetes. Il marche par délegation sur des Handler.
 *
 * @author $Author: gaudefr $
 * @version $Revision: 1.10 $
 */
public class Processor {
    private final HandlerManager handlerManager;


    public Processor(HandlerMap handlerMap, BackPack backPack) throws AspectConfigException {
        handlerManager = new HandlerManager(handlerMap, backPack);
    }


    public String proceed(String xmlRequests, MadRequestContext madRequestContext) {
        Chronometer chrono = new Chronometer();
        chrono.start();
        try {
            return proceedImpl(xmlRequests, madRequestContext);
        }
        finally {
            chrono.stop();
            Log.info("Traitement de la requete en " + chrono.getDelay() + " ms");
        }
    }


    private String proceedImpl(String xmlRequest, MadRequestContext madRequestContext) {
        try {
            return handlerManager.executeRequests(xmlRequest, new HandlerContext(madRequestContext));
        }
        catch (Exception ex) {
            Log.error("Erreur durant le traitement de " + xmlRequest, ex);
            return ErrorUtil.determineErrorResult(ex);
        }
    }
}
