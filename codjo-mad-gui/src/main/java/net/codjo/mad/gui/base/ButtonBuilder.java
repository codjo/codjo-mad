package net.codjo.mad.gui.base;
import java.awt.Insets;
import javax.swing.Action;
import javax.swing.JButton;
/**
 *
 */
public class ButtonBuilder implements ComponentBuilder {
    private String name;
    private Action action;


    public ButtonBuilder(String name, Action action) {
        this.name = name;
        this.action = action;
    }


    public JButton build() {
        JButton button = new JButton(action);
        button.setName(name);
        button.setBorderPainted(false);
        button.setFocusable(false);
        button.setMargin(new Insets(0, 0, 0, 0));
        return button;
    }
}
