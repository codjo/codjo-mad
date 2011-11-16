/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.mad.server.handler;
import net.codjo.mad.server.MadConnectionManager;
import net.codjo.mad.server.MadRequestContext;
import net.codjo.mad.server.MadTransaction;
import net.codjo.security.common.api.User;
import java.sql.Connection;
import java.sql.SQLException;
import org.exolab.castor.jdo.Database;
import org.exolab.castor.jdo.PersistenceException;
/**
 * Contexte d'exécution d'une requête.
 *
 * @author $Author: gaudefr $
 * @version $Revision: 1.5 $
 */
public class HandlerContext implements MadRequestContext {
    private String user;
    private MadRequestContext madRequestContext;

    public HandlerContext() {}


    public HandlerContext(MadRequestContext madRequestContext) {
        this.madRequestContext = madRequestContext;
    }

    public void setUser(String user) {
        this.user = user;
    }


    public String getUser() {
        return user;
    }


    public boolean isAllowedTo(String function) {
        return madRequestContext.getUserProfil().isAllowedTo(function);
    }


    public Connection getConnection() throws SQLException {
        return madRequestContext.getConnectionManager().getConnection();
    }


    public Connection getTxConnection() throws SQLException {
        return madRequestContext.getConnectionManager().getTxConnection();
    }


    public Database getDatabase() throws PersistenceException {
        return madRequestContext.getConnectionManager().getDatabase();
    }


    public MadTransaction getTransaction() {
        return madRequestContext.getTransaction();
    }


    public MadConnectionManager getConnectionManager() {
        return madRequestContext.getConnectionManager();
    }


    public User getUserProfil() {
        return madRequestContext.getUserProfil();
    }


    public void close() {
        madRequestContext.close();
    }
}
