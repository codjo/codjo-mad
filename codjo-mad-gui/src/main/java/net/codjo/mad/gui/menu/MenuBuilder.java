package net.codjo.mad.gui.menu;
import net.codjo.i18n.gui.TranslationNotifier;
import net.codjo.mad.gui.framework.GuiContext;
import net.codjo.mad.gui.framework.MutableGuiContext;
import net.codjo.mad.gui.i18n.InternationalizationUtil;
import java.awt.Dimension;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
/**
 * Description of the Class
 *
 * @author $Author: gaudefr $
 * @version $Revision: 1.13 $
 */
public class MenuBuilder {
    private List<MenuBuilder> subMenus = new ArrayList<MenuBuilder>();
    private String action;
    private String name;
    private boolean separator;
    private boolean forMenu = true;
    private String plugin;
    private String actionId;
    private boolean hideWhenNotAllowed = false;


    public MenuBuilder() {
    }


    public void addMenuBar(MenuBuilder menuBuilder) {
        subMenus.add(menuBuilder);
    }


    public void setAction(String action) {
        this.action = action;
    }


    public void setName(String name) {
        this.name = name;
    }


    public void setSeparator(boolean separator) {
        this.separator = separator;
    }


    public void setSubMenus(List<MenuBuilder> subMenus) {
        this.subMenus = subMenus;
    }


    public String getAction() {
        return action;
    }


    String getGuiContextProperty() {
        if (plugin == null) {
            return actionId;
        }
        return plugin + "#" + (actionId == null ? "" : actionId);
    }


    public String getName() {
        return name;
    }


    public boolean getSeparator() {
        return separator;
    }


    public boolean isSeparator() {
        return separator;
    }


    public List<MenuBuilder> getSubMenus() {
        return subMenus;
    }


    public JComponent build(MutableGuiContext ctxt) throws MenuFactory.BuildException {
        TranslationNotifier notifier = InternationalizationUtil.retrieveTranslationNotifier(ctxt);

        if (isSeparator()) {
            return buildSeparator();
        }

        JComponent component = null;
        if (getAction() != null) {
            component = buildComponentForAction(ctxt);
        }

        if (getGuiContextProperty() != null) {
            if (isForMenu()) {
                component = (JComponent)ctxt.getProperty(getGuiContextProperty());
            }
            else {
                component = (JComponent)ctxt.getProperty(getGuiContextProperty());
                if (component instanceof JButton) {
                    Action act = ((JButton)component).getAction();
                    if (act != null) {
                        component = new JButton(act);
                    }
                }
                else if (component instanceof JMenuItem) {
                    Action act = ((JMenuItem)component).getAction();
                    if (act != null) {
                        component = new JMenuItem(act);
                    }
                }
            }
        }

        if (component != null) {
            if (AbstractButton.class.isInstance(component)) {
                if (notifier != null) {
                    String key = null;
                    AbstractButton button = (AbstractButton)component;
                    if ((getGuiContextProperty() != null) && (plugin != null)) {
                        key = getGuiContextProperty().replace(' ', '_');
                    }
                    else if (button.getAction() != null) {
                        key = button.getAction().getClass().getName();
                    }

                    if (key != null) {
                        String tooltipKey = key + ".tooltip";
                        if (!isForMenu()) {
                            key = null;
                        }
                        notifier.addInternationalizableComponent((AbstractButton)component, key, tooltipKey);
                    }
                }
            }
            return component;
        }

        return buildSubMenu(ctxt);
    }


    private AbstractButton buildComponentForAction(MutableGuiContext ctxt) throws MenuFactory.BuildException {
        Action act = buildAction(ctxt);

        AbstractButton item;
        if (isForMenu()) {
            item = new JMenuItem(act);
        }
        else {
            item = new JButton(act);
        }

        if (actionId != null) {
            item.setName(actionId);
        }
        return item;
    }


    private Action buildAction(MutableGuiContext ctxt)
          throws MenuFactory.BuildException {
        try {
            if (ctxt.hasProperty(getAction())) {
                return (Action)ctxt.getProperty(getAction());
            }

            Class<?> actionClass = Class.forName(getAction());
            Constructor<?> constructorWithContext = actionClass.getConstructor(GuiContext.class);
            Action act = (Action)constructorWithContext.newInstance(ctxt);

            ctxt.putProperty(getAction(), act);

            return act;
        }
        catch (Exception ex) {
            throw new MenuFactory.BuildException(ex);
        }
    }


    private JSeparator buildSeparator() {
        if (isForMenu()) {
            return new JSeparator();
        }
        else {
            JSeparator newSeparator = new JSeparator(JSeparator.VERTICAL);

            newSeparator.setMaximumSize(new Dimension(10, 20));

            return newSeparator;
        }
    }


    private JMenu buildSubMenu(MutableGuiContext ctxt) throws MenuFactory.BuildException {
        JMenu menu = new JMenu(getName());
        TranslationNotifier translationNotifier = InternationalizationUtil.retrieveTranslationNotifier(ctxt);
        translationNotifier.addInternationalizableComponent(menu, getName(), null);

        boolean enabled = false;
        for (MenuBuilder menuBuilder : getSubMenus()) {
            JComponent component = menuBuilder.build(ctxt);
            if (component instanceof JButton || component instanceof JMenuItem) {
                enabled = enabled || component.isEnabled();
            }
            menu.add(component);
        }
        menu.setEnabled(enabled);
        return menu;
    }


    public boolean isForMenu() {
        return forMenu;
    }


    public void setForMenu(boolean forMenu) {
        this.forMenu = forMenu;
    }


    @Override
    public String toString() {
        StringBuffer strBuf =
              new StringBuffer("    MenuBuilder : " + getName() + "  " + getAction() + "  "
                               + getSeparator() + "  " + getGuiContextProperty());
        String newline = System.getProperty("line.separator") + "    ";
        for (MenuBuilder menuBuilder : this.getSubMenus()) {
            strBuf.append(newline).append(menuBuilder.toString());
        }
        return strBuf.toString();
    }


    public void setPlugin(String plugin) {
        this.plugin = plugin;
    }


    public void setActionId(String actionId) {
        this.actionId = actionId;
    }


    public void setHideWhenNotAllowed(boolean hide) {
        this.hideWhenNotAllowed = hide;
    }


    public boolean isHiddenWhenNotAllowed() {
        return hideWhenNotAllowed;
    }
}
