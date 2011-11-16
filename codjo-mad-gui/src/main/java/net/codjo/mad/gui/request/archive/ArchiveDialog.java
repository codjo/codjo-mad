package net.codjo.mad.gui.request.archive;
import net.codjo.gui.toolkit.date.DateField;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.util.Calendar;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
/**
 * Dialogue pour la date d'historisation.
 *
 * @author $Author: gaudefr $
 * @version $Revision: 1.10 $
 */
public final class ArchiveDialog {
    private JButton cancelButton = new JButton();
    private JPanel centerPanel = new JPanel();
    private DateField dateField = new DateField();
    private JButton okButton = new JButton();
    private JLabel statusLabel = new JLabel(" ");
    private JDialog dialog;
    private Date first;
    private Date second;

    private ArchiveDialog(Component aFrame, Date first, Date beginDate, Date second) {
        init();

        Calendar cal = Calendar.getInstance();
        cal.setTime(first);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        first = cal.getTime();

        cal.setTime(second);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);

        init(first, beginDate, second);

        Object[] array = {centerPanel, statusLabel};

        Object[] options = {okButton, cancelButton};

        JOptionPane optionPane =
            new JOptionPane(array, JOptionPane.QUESTION_MESSAGE,
                JOptionPane.DEFAULT_OPTION, null, options, options[0]);

        dialog = optionPane.createDialog(aFrame, "Sélectionner la date d'historisation");
        correctLocationWithStartMenu(dialog);
        dialog.setVisible(true);
    }

    public static Date askBeginDate(Component aFrame, Date first, Date beginDate,
        Date second) {
        ArchiveDialog dlg = new ArchiveDialog(aFrame, first, beginDate, second);
        return dlg.getBeginDate();
    }


    private void setBeginDate(Date date) {
        dateField.setDate(date);
    }


    private Date getBeginDate() {
        return dateField.getDate();
    }


    private boolean isValidDate(Date nvVal) {
        if (nvVal.compareTo(first) <= 0) {
            return false;
        }
        else if (nvVal.compareTo(second) >= 0) {
            return false;
        }
        return true;
    }


    private void correctLocationWithStartMenu(JDialog jdialog) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension dialogSize = jdialog.getSize();
        int locationY = jdialog.getLocation().y;

        if (locationY + dialogSize.height > (screenSize.height - 20)) {
            locationY -= locationY + dialogSize.height - (screenSize.height - 20);
        }

        jdialog.setLocation(jdialog.getLocation().x, locationY);
    }


    private void dateFieldPropertyChange(PropertyChangeEvent event) {
        if (event.getNewValue() == null) {
            okButton.setEnabled(false);
        }
        else if (isValidDate((Date)event.getNewValue())) {
            statusLabel.setText(" ");
            okButton.setEnabled(true);
        }
        else {
            statusLabel.setText("La date n'est pas valide");
            dateField.setDate(null);
        }
    }


    private void init(Date firstDate, Date beginDate, Date secondDate) {
        this.first = firstDate;
        this.second = secondDate;
        if (firstDate.equals(beginDate)) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(beginDate);
            cal.add(Calendar.DAY_OF_MONTH, 1);
            beginDate = cal.getTime();
        }
        setBeginDate(beginDate);
    }


    private void init() {
        statusLabel.setForeground(Color.red);
        TitledBorder titledBorder1 =
            new TitledBorder(BorderFactory.createEtchedBorder(Color.white,
                    new Color(134, 134, 134)), "Date de début d'historisation");
        dateField.setDisplayingDayOfWeek(true);
        dateField.setDefaultCalendarHelper(true);
        dateField.setEditable(false);
        centerPanel.setBorder(titledBorder1);
        centerPanel.add(dateField, null);
        dateField.addPropertyChangeListener(DateField.DATE_PROPERTY_NAME,
            new java.beans.PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent event) {
                    dateFieldPropertyChange(event);
                }
            });
        dateField.setName("archiveDialog.dateField");
        cancelButton.setText("Annuler");
        cancelButton.setName("ArchiveDialog.cancelButton");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    setBeginDate(null);
                    dialog.dispose();
                }
            });
        okButton.setEnabled(false);
        okButton.setText("Archiver");
        okButton.setName("ArchiveDialog.okButton");
        okButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    dialog.dispose();
                }
            });
    }
}
