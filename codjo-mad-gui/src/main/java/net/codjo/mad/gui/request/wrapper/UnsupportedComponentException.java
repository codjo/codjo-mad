package net.codjo.mad.gui.request.wrapper;
/**
 * Indique qu'un composant graphique n'est pas supporté.
 *
 * @author $Author: gaudefr $
 * @version $Revision: 1.3 $
 */
public class UnsupportedComponentException extends Exception {
    public UnsupportedComponentException() {}


    public UnsupportedComponentException(String msg) {
        super(msg);
    }
}
