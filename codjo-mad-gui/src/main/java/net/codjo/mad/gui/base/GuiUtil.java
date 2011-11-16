package net.codjo.mad.gui.base;
import javax.swing.ImageIcon;
/**
 *
 */
public class GuiUtil {
    private GuiUtil() {}


    public static ImageIcon getIcon(String name) {
        return new ImageIcon(GuiUtil.class.getResource("/resources/images/" + name));
    }
}
