package net.codjo.mad.gui.framework;
/**
 * Description of the Class
 *
 * @author $Author: gaudefr $
 * @version $Revision: 1.6 $
 */
public class GuiEvent {
    public static final GuiEvent LOGIN = new GuiEvent("login");
    public static final GuiEvent QUIT = new GuiEvent("quit");
    private String name;


    public GuiEvent(String name) {
        this.name = name;
    }


    public String getName() {
        return name;
    }
}
