package net.codjo.mad.gui.menu;
import net.codjo.mad.gui.framework.MutableGuiContext;
import java.awt.Component;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JToolBar;
import javax.swing.JSeparator;
/**
 * Builder d'une toolbar.
 *
 * @author $Author: gaudefr $
 * @version $Revision: 1.11 $
 */
public class ToolBarBuilder {
    private List<MenuBuilder> components = new ArrayList<MenuBuilder>();
    private Boolean borderPainted = Boolean.TRUE;
    private Boolean floatable = Boolean.TRUE;
    private Boolean iconOnly = Boolean.FALSE;
    private RolloverEffect rolloverEffect = new RolloverEffect();


    public ToolBarBuilder() {
    }


    public void addMenuBar(MenuBuilder menuBuilder) {
        components.add(menuBuilder);
    }


    public void setComponents(List<MenuBuilder> componentList) {
        this.components = componentList;
    }


    public List<MenuBuilder> getComponents() {
        return components;
    }


    public JToolBar buildToolBar(MutableGuiContext ctxt)
          throws MenuFactory.BuildException {
        JToolBar bar = new JToolBar();
        bar.setFloatable(getFloatable());
        bar.setBorderPainted(getBorderPainted());

        for (MenuBuilder item : getComponents()) {
            item.setForMenu(false);

            Component cmp = item.build(ctxt);

            if (cmp instanceof JMenuItem && ((JMenuItem)cmp).getIcon() != null) {

                Action currentAction = ((JMenuItem)cmp).getAction();
                if(!item.isHiddenWhenNotAllowed() || currentAction.isEnabled() ) {
                    bar.add(currentAction).setName(currentAction.getValue(Action.NAME).toString());;
                }

            }
            else {
                if (cmp instanceof AbstractButton) {
                    Action actionObject = ((AbstractButton)cmp).getAction();
                    if (actionObject != null) {
                        String actionName = (String)actionObject.getValue(Action.NAME);
                        cmp.setName(actionName);
                    }
                }
                if (getIconOnly()
                    && cmp instanceof JButton
                    && ((JButton)cmp).getIcon() != null) {
                    ((JButton)cmp).setText("");
                }
                 if(!item.isHiddenWhenNotAllowed() || cmp.isEnabled() ) {
                     bar.add(cmp);
                 }
            }
        }
        rolloverEffect.doEffect(bar);
        return removeUnnecessarySeparators(bar);
    }


    private JToolBar removeUnnecessarySeparators(JToolBar toolBar) {
        Component component;
        int previousDeleted = -1;
        int nbComponents = toolBar.getComponentCount();
        for(int i = 0; i < nbComponents; ) {
            component = toolBar.getComponent(i);
            if(toolBar.getComponent(i).getClass() == JSeparator.class) {
                if(i > previousDeleted) {
                    toolBar.remove(component);
                    nbComponents--;
                } else {
                    i++;
                }
            } else {
                previousDeleted = ++i;
            }
        }

        if(nbComponents > 0) {
            component = toolBar.getComponent(toolBar.getComponentCount() - 1);
            if(component != null && component.getClass() == JSeparator.class) {
                toolBar.remove(component);
            }
        }
        return toolBar;
    }


    public Boolean getIconOnly() {
        return iconOnly;
    }


    public void setIconOnly(Boolean iconOnly) {
        this.iconOnly = iconOnly;
    }


    public Boolean getBorderPainted() {
        return borderPainted;
    }


    public void setBorderPainted(Boolean borderPainted) {
        this.borderPainted = borderPainted;
    }


    public Boolean getFloatable() {
        return floatable;
    }


    public void setFloatable(Boolean floatable) {
        this.floatable = floatable;
    }


    /**
     * Effet Rollover sur un bouton. Car setRolloverEnabled marche pas.
     */
    private static class RolloverEffect extends java.awt.event.MouseAdapter {
        private Insets inset = new java.awt.Insets(1, 3, 1, 3);


        public void doEffect(JToolBar tb) {
            Component component;
            int idx = 0;
            do {
                component = tb.getComponentAtIndex(idx);
                if (component instanceof JButton) {
                    doEffect((JButton)component);
                }
                idx++;
            }
            while (component != null);
        }


        @Override
        public void mouseEntered(MouseEvent mev) {
            if (!(mev.getSource() instanceof JButton)) {
                return;
            }
            JButton bn = (JButton)mev.getSource();
            if (bn.isEnabled()) {
                bn.setBorderPainted(true);
            }
        }


        @Override
        public void mouseExited(MouseEvent mev) {
            if (!(mev.getSource() instanceof JButton)) {
                return;
            }
            JButton bn = (JButton)mev.getSource();
            bn.setBorderPainted(false);
        }


        private void doEffect(JButton button) {
            button.addMouseListener(this);
            button.setMargin(inset);
            button.setBorderPainted(false);
        }
    }
}
