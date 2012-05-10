package net.codjo.mad.gui.base;
import java.awt.Insets;
import javax.swing.Action;
import javax.swing.JButton;
import net.codjo.i18n.gui.TranslationNotifier;
import net.codjo.mad.gui.framework.GuiContext;
import net.codjo.mad.gui.i18n.InternationalizationUtil;
/**
 *
 */
public class ButtonBuilder implements ComponentBuilder {
    private String name;
    private Action action;
    private String labelKey;
    private String tooltipKey;
    private TranslationNotifier translationNotifier;


    public ButtonBuilder(String name, Action action) {
        this.name = name;
        this.action = action;
    }


    public ButtonBuilder(GuiContext guiContext, String name, Action action, String labelKey, String tooltipKey) {
        translationNotifier = InternationalizationUtil.retrieveTranslationNotifier(guiContext);
        this.name = name;
        this.action = action;
        this.labelKey = labelKey;
        this.tooltipKey = tooltipKey;
    }


    public JButton build() {
        JButton button = new JButton(action);
        button.setName(name);
        button.setBorderPainted(false);
        button.setFocusable(false);
        button.setMargin(new Insets(0, 0, 0, 0));
        if ((labelKey != null) || (tooltipKey != null)) {
            registerInternationalizableButton(button);
        }
        return button;
    }


    private void registerInternationalizableButton(JButton button) {
        translationNotifier.addInternationalizableComponent(button, labelKey, tooltipKey);
    }
}
