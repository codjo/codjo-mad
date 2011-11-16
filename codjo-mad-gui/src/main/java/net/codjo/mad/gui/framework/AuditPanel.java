package net.codjo.mad.gui.framework;
import net.codjo.gui.toolkit.date.TimestampDateField;
import net.codjo.gui.toolkit.text.DocumentWithMaxSize;
import net.codjo.gui.toolkit.util.GuiUtil;
import net.codjo.mad.gui.request.DetailDataSource;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
/**
 *
 */
public class AuditPanel extends JPanel {
    public static final int HORIZONTAL_LAYOUT = 0;
    public static final int VERTICAL_LAYOUT = 1;
    private JTextArea comment = new JTextArea(new DocumentWithMaxSize(255));
    private JTextField creationBy = new JTextField();
    private TimestampDateField creationDatetime = new TimestampDateField();
    private JTextField updateBy = new JTextField();
    private TimestampDateField updateDatetime = new TimestampDateField();
    private GridBagLayout gridBagLayout = new GridBagLayout();
    private JLabel creationDateLabel = new JLabel("Création le");
    private JLabel creatorLabel = new JLabel("par");
    private JLabel modificationDateLabel = new JLabel("Modifié le");
    private JLabel modifierLabel = new JLabel("par");
    private JLabel commentLabel = new JLabel();
    private JScrollPane commentScrollPane = new JScrollPane();
    protected JDesktopPane ourDesktopPane;


    public AuditPanel() {
        init(VERTICAL_LAYOUT);
    }


    public void setDesktopPane(JDesktopPane ourDesktopPane) {
        ((DocumentWithMaxSize)comment.getDocument()).setDesktopPane(ourDesktopPane);
    }


    public AuditPanel(int layoutOrientation) {
        init(layoutOrientation);
    }


    public JTextField getCreationBy() {
        return creationBy;
    }


    public TimestampDateField getCreationDatetime() {
        return creationDatetime;
    }


    public JTextField getUpdateBy() {
        return updateBy;
    }


    public TimestampDateField getUpdateDatetime() {
        return updateDatetime;
    }


    public void declareFields(DetailDataSource dataSource) {
        dataSource.declare("comment", comment);
        dataSource.declare("creationBy", creationBy);
        dataSource.declare("creationDatetime", creationDatetime);
        dataSource.declare("updateBy", updateBy);
        dataSource.declare("updateDatetime", updateDatetime);
    }


    private void init(int layoutOrientation) {
        creationDateLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        creatorLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        modificationDateLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        modifierLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        creationDatetime.setName("creationDatetime");
        GuiUtil.disableTextEdition(creationDatetime);
        creationDatetime.setEnabled(false);

        creationBy.setName("creationBy");
        GuiUtil.disableTextEdition(creationBy);
        creationBy.setEnabled(false);

        updateDatetime.setName("updateDatetime");
        GuiUtil.disableTextEdition(updateDatetime);
        updateDatetime.setEnabled(false);

        updateBy.setName("updateBy");
        GuiUtil.disableTextEdition(updateBy);
        updateBy.setEnabled(false);

        commentLabel.setText("Commentaires");
        commentLabel.setHorizontalAlignment(SwingConstants.CENTER);

        comment.setName("comment");
        comment.setLineWrap(true);
        comment.setWrapStyleWord(true);
        comment.setLineWrap(true);
        comment.setColumns(10);
        comment.setRows(2);

        setLayout(gridBagLayout);
        setBorder(new TitledBorder(BorderFactory.createEtchedBorder(Color.white,
                                                                    new Color(134, 134, 134)), "Audit"));
        if (layoutOrientation == HORIZONTAL_LAYOUT) {
            buildHorizontalLayout();
        }
        else {
            buildVerticalLayout();
        }
        commentScrollPane.getViewport().add(comment, null);
    }


    private void buildHorizontalLayout() {
        setMinimumSize(new Dimension(150, 100));

        GridBagConstraints gbc = new GridBagConstraints();

        this.creationDatetime.setPreferredSize(new Dimension(120, 21));
        this.creationDatetime.setMinimumSize(new Dimension(120, 21));
        this.creationDatetime.setMaximumSize(new Dimension(120, 21));

        creationBy.setPreferredSize(new Dimension(120, 21));
        creationBy.setMinimumSize(new Dimension(120, 21));
        creationBy.setMaximumSize(new Dimension(120, 21));

        updateDatetime.setPreferredSize(new Dimension(120, 21));
        updateDatetime.setMinimumSize(new Dimension(120, 21));
        updateDatetime.setMaximumSize(new Dimension(120, 21));

        updateBy.setPreferredSize(new Dimension(120, 21));
        updateBy.setMinimumSize(new Dimension(120, 21));
        updateBy.setMaximumSize(new Dimension(120, 21));


        addLabel(gbc, 0, 0, creationDateLabel);
        addField(gbc, 1, 0, creationDatetime);

        addLabel(gbc, 2, 0, creatorLabel);
        addField(gbc, 3, 0, creationBy);

        addLabel(gbc, 4, 0, modificationDateLabel);
        addField(gbc, 5, 0, updateDatetime);

        addLabel(gbc, 6, 0, modifierLabel);
        addField(gbc, 7, 0, updateBy);

        addLabel(gbc, 0, 1, commentLabel, new Insets(10, 5, 2, 5), GridBagConstraints.NONE);

        gbc.gridwidth = 7;
        gbc.weighty = 1.0;
        addField(gbc, 1, 1, commentScrollPane, new Insets(10, 5, 2, 5), GridBagConstraints.BOTH);

        commentScrollPane.setMinimumSize(new Dimension(100, 30));
        commentScrollPane.setPreferredSize(new Dimension(500, 30));
    }


    private void addField(GridBagConstraints gbc, int xPos, int yPos, JComponent componentField) {
        addField(gbc, xPos, yPos, componentField, new Insets(2, 5, 2, 5), GridBagConstraints.NONE);
    }


    private void addField(GridBagConstraints gbc,
                          int xPos,
                          int yPos,
                          JComponent componentField,
                          Insets insets,
                          int fill) {
        gbc.gridx = xPos;
        gbc.gridy = yPos;
        gbc.weightx = 1.0;
        gbc.insets = insets;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = fill;
        gridBagLayout.setConstraints(componentField, gbc);
        add(componentField);
    }


    private void addLabel(GridBagConstraints gbc, int xPos, int yPos, JLabel label) {
        addLabel(gbc, xPos, yPos, label, new Insets(2, 5, 2, 5), GridBagConstraints.HORIZONTAL);
    }


    private void addLabel(GridBagConstraints gbc,
                          int xPos,
                          int yPos,
                          JLabel label,
                          Insets insets,
                          int fill
    ) {
        gbc.gridx = xPos;
        gbc.gridy = yPos;
        gbc.weightx = 0.0;
        gbc.insets = insets;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = fill;
        gridBagLayout.setConstraints(label, gbc);
        add(label);
    }


    private void buildVerticalLayout() {
        add(creationDateLabel,
            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                                   GridBagConstraints.NONE, new Insets(-5, 0, 0, 0), 6, 0));
        add(creationDatetime,
            new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
                                   GridBagConstraints.BOTH, new Insets(2, 5, 2, 5), 20, 0));
        add(creatorLabel,
            new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                                   GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 48, 0));
        add(creationBy,
            new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
                                   GridBagConstraints.BOTH, new Insets(2, 5, 2, 5), 0, 0));
        add(modificationDateLabel,
            new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                                   GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 14, 0));
        add(updateDatetime,
            new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
                                   GridBagConstraints.HORIZONTAL, new Insets(2, 5, 2, 5), 0, 0));
        add(modifierLabel,
            new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                                   GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 48, 0));
        add(updateBy,
            new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
                                   GridBagConstraints.HORIZONTAL, new Insets(2, 5, 2, 5), 0, 0));
        add(commentLabel,
            new GridBagConstraints(0, 4, 2, 1, 0.0, 0.0, GridBagConstraints.WEST,
                                   GridBagConstraints.NONE, new Insets(15, 5, 0, 0), 0, 0));
        add(commentScrollPane,
            new GridBagConstraints(0, 5, 2, 1, 0.0, 1.0, GridBagConstraints.CENTER,
                                   GridBagConstraints.BOTH, new Insets(0, 0, 1, 1), 0, 87));
    }


    public JTextArea getComment() {
        return comment;
    }

}

