package net.codjo.mad.client.request.util;
import java.rmi.RemoteException;
/**
 * Interface decrivant les services necessaire pour encapsuler les appels au Serveur de RequestSender.
 *
 * @author $Author: gaudefr $
 * @version $Revision: 1.4 $
 * @see net.codjo.mad.client.request.RequestSender
 */
public interface ServerWrapper {
    /**
     * Retourne une nouvelle instance de ce type d'assistant.
     *
     * @return une copy.
     */
    public ServerWrapper copy();


    /**
     * Ferme la session.
     */
    void close();


    /**
     * Envoie un message et attend la réponse <code>timeout</code> milliseconds.
     *
     * @param text    Le message a envoyer au service
     * @param timeout
     *
     * @return la réponse.
     *
     * @throws RemoteException Erreur lors de l'envoie ou de la reception.
     */
    String sendWaitResponse(String text, long timeout) throws RemoteException;
}
