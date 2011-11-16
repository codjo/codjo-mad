package net.codjo.mad.gui.request.util;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
/**
 * Panel contenant les boutons d'un ecran détail.
 *
 * @author $Author: gaudefr $
 * @version $Revision: 1.6 $
 */
public class ButtonPanelGui extends JPanel {
    private JButton archiveButton = new JButton();
    private JButton cancelButton = new JButton();
    private JButton okButton = new JButton();
    private JButton redoButton = new JButton();
    private JButton undoButton = new JButton();
    private JButton whatsNewButton = new JButton();


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
}
