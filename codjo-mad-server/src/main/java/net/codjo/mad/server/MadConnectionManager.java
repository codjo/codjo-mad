/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.mad.server;
import java.sql.Connection;
import java.sql.SQLException;
import org.exolab.castor.jdo.Database;
import org.exolab.castor.jdo.PersistenceException;
/**
 * Interface décrivant un objet gérant les connexions.
 *
 * <p> Exemple d'utilisation :
 * <pre>
 *  MadConnectionManager manager = ...
 *  Connection con = manager.getConnection();
 *  try {
 *  ...
 *  }
 *  finally {
 *  con.close();
 *  }
 *  </pre>
 * </p>
 */
public interface MadConnectionManager {
    Connection getConnection() throws SQLException;


    Connection getTxConnection() throws SQLException;


    Database getDatabase() throws PersistenceException;


    int countUsedConnections();


    int countUnusedConnections();
}
