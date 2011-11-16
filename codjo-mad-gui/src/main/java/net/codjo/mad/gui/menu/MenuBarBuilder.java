package net.codjo.mad.gui.menu;
import net.codjo.mad.gui.framework.MutableGuiContext;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JMenuBar;

public class MenuBarBuilder {
    private List<MenuBuilder> menus = new ArrayList<MenuBuilder>();

    public MenuBarBuilder() {}

    public void setMenus(List menus) {
        this.menus = menus;
    }


    public List getMenus() {
        return menus;
    }


    public void addMenuBar(MenuBuilder menuBuilder) {
        menus.add(menuBuilder);
    }


    public JMenuBar buildMenuBar(MutableGuiContext ctxt) throws MenuFactory.BuildException {
        JMenuBar bar = new JMenuBar();
        for (Object o : getMenus()) {
            MenuBuilder item = (MenuBuilder)o;
            bar.add(item.build(ctxt));
        }
        return bar;
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("MenuBarBuilder : ");
        String newline = System.getProperty("line.separator");
        for (Object o : getMenus()) {
            MenuBuilder item = (MenuBuilder)o;
            builder.append(newline).append(item.toString());
        }
        return builder.toString();
    }
}
