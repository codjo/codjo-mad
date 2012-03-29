package net.codjo.mad.gui.request.util;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import net.codjo.i18n.common.TranslationManager;
import net.codjo.i18n.gui.InternationalizableContainer;
import net.codjo.i18n.gui.TranslationNotifier;
import net.codjo.mad.gui.framework.GuiContext;

import static net.codjo.mad.gui.i18n.InternationalizationUtil.retrieveTranslationNotifier;
/**
 * Panel contenant les boutons d'un ecran détail.
 *
 * @author $Author: gaudefr $
 * @version $Revision: 1.6 $
 */
public class ButtonPanelGui extends JPanel implements InternationalizableContainer {
    private JButton archiveButton = new JButton();
    private JButton cancelButton = new JButton();
    private JButton okButton = new JButton();
    private JButton redoButton = new JButton();
    private JButton undoButton = new JButton();
    private JButton whatsNewButton = new JButton();
    private TranslationManager translationManager;


    public ButtonPanelGui() {
        jbInit();
    }


    public JButton getRedoButton() {
        return redoButton;
    }


    public void removeRedoButton() {
        remove(redoButton);
    }


    public JButton getUndoButton() {
        return undoButton;
    }


    public void removeUndoButton() {
        remove(undoButton);
    }


    public JButton getOkButton() {
        return okButton;
    }


    public JButton getCancelButton() {
        return cancelButton;
    }


    public JButton getArchiveButton() {
        return archiveButton;
    }


    public JButton getWhatsNewButton() {
        return whatsNewButton;
    }


    public void addWhatsNewButton() {
        this.add(whatsNewButton, 0);
    }


    private void jbInit() {
        okButton.setEnabled(false);
        cancelButton.setEnabled(false);
        okButton.setText("Valider");
        okButton.setName("ButtonPanelGui.okButton");
        FlowLayout panelLayout = new FlowLayout();
        setLayout(panelLayout);
        panelLayout.setAlignment(FlowLayout.RIGHT);
        cancelButton.setText("Annuler");
        cancelButton.setName("ButtonPanelGui.cancelButton");
        archiveButton.setText("Archiver");
        archiveButton.setName("ButtonPanelGui.archiveButton");
        whatsNewButton.setText("What's NEW");
        whatsNewButton.setName("ButtonPanelGui.whatsNewButton");
        undoButton.setName("ButtonPanelGui.undoButton");
        redoButton.setName("ButtonPanelGui.redoButton");
        add(undoButton, null);
        add(redoButton, null);
        add(okButton, null);
        add(cancelButton, null);
    }


    public void addArchiveButton() {
        add(getArchiveButton(), 0);
    }


    public void removeArchiveButton() {
        remove(getArchiveButton());
    }


    public void removeWhatsNewButton() {
        remove(getWhatsNewButton());
    }


    public void setNamePrefix(String prefix) {
        setPrefix(prefix, okButton);
        setPrefix(prefix, cancelButton);
        setPrefix(prefix, archiveButton);
        setPrefix(prefix, undoButton);
        setPrefix(prefix, redoButton);
        setPrefix(prefix, whatsNewButton);
    }


    protected void setPrefix(String prefix, JButton button) {
        button.setName(prefix + "." + button.getName());
    }


    public void setTranslationBackpack(GuiContext guiContext) {
        TranslationNotifier translationNotifier = retrieveTranslationNotifier(guiContext);
        translationNotifier.addInternationalizableContainer(this);
    }


    public void addInternationalizableComponents(TranslationNotifier translationNotifier) {
        translationNotifier.addInternationalizableComponent(okButton, "ButtonPanelGui.ok", null);
        translationNotifier.addInternationalizableComponent(cancelButton, "ButtonPanelGui.cancel", null);
        translationNotifier.addInternationalizableComponent(archiveButton, "ButtonPanelGui.store", null);
        translationNotifier.addInternationalizableComponent(whatsNewButton, "ButtonPanelGui.whatsnew", null);
    }
}
