package net.codjo.mad.gui.request.requetor;
import net.codjo.gui.toolkit.calendar.CalendarHelper;
import net.codjo.gui.toolkit.date.DateField;
import net.codjo.gui.toolkit.fileChooser.FileChooserManager;
import net.codjo.gui.toolkit.util.ErrorDialog;
import net.codjo.gui.toolkit.waiting.WaitingPanel;
import net.codjo.mad.client.request.FieldsList;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.common.structure.FieldLabelComparator;
import net.codjo.mad.common.structure.FieldStructure;
import net.codjo.mad.common.structure.TableStructure;
import net.codjo.mad.gui.request.RequestTable;
import net.codjo.mad.gui.request.factory.RequestFactory;
import net.codjo.mad.gui.request.factory.RequetorFactory;
import net.codjo.mad.gui.structure.StructureRenderer;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.apache.log4j.Logger;
/**
 * Fenêtre de saisie de requêtes SQL. Permet d'executer un select sur une table donnée.
 */
class SqlRequetor extends JInternalFrame {
    private static final Logger APP = Logger.getLogger(SqlRequetor.class);
    static final Link EMPTY_LINK = new Link();
    private static final String TABULATION_SEPARATOR = "\t";
    private JButton buttonAdd = new JButton("Ajouter");
    private JButton buttonDelete = new JButton("Supprimer");
    private JButton buttonDeleteAll = new JButton("Tout supprimer");
    private JButton buttonAnd = new JButton("Et");
    private JButton buttonOr = new JButton("Ou");
    private JLabel labelLinkTables = new JLabel("Tables liées");
    private JList listSqlRequest = new JList();
    private JList listCurrentFields = new JList();
    private JList listOperators = new JList();
    private JList listLinkFields = new JList();
    private JScrollPane linkFieldsScrollPane = new JScrollPane();
    private JScrollPane operatorsScrollPane = new JScrollPane();
    private JScrollPane currentFieldsScrollPane = new JScrollPane();
    private JScrollPane sqlRequestScrollPane = new JScrollPane();
    private DefaultListModel sqlListModel = new DefaultListModel();
    private DefaultListModel currentListFieldsModel = new DefaultListModel();
    private DefaultListModel linkListFieldsModel = new DefaultListModel();
    private String mandatoryClause = "";
    private JTextField textFieldValue = new JTextField();
    private boolean findInSelection = true;
    private String defaultClause = "";
    private Map<Link, List<String>> srcLinkTableFieldsName;
    private Map<Link, List<String>> destLinkTableFieldsName;
    private Map<Link, List<String>> operatorLinkTableFieldsName;
    private Map<String, String> innerJoins = new HashMap<String, String>();
    private JComboBox linkTablesComboBox;
    private SqlRequetorRequest req;
    private FindAction findAction;
    private RequetorParameters reqParams;
    private JButton validateButton = new JButton("Valider");
    private JButton buttonOpen = new JButton("Ouvrir...");
    private JButton buttonSave = new JButton("Sauvegarder...");
    private JButton cancelButton = new JButton("Annuler");
    private JCheckBox findInSelectionCheckBox = new JCheckBox("Rappeler la dernière recherche");
    private JCheckBox leftJoinCheckBox
          = new JCheckBox("Afficher les lignes sans correspondances dans les tables liées");
    private String orderClause = "";
    private String queryFromClause = "";
    private WaitingPanel waitingPanel;
    private boolean displayWaitingPanel = false;
    private JLabel dateFieldLabel;
    private JButton calendarButton;


    SqlRequetor(FindAction fa, RequetorParameters params) {
        this(fa, params, "");
    }


    SqlRequetor(FindAction fa, RequetorParameters params, String defaultClause) {
        super("Recherche sur la table ", false, true, false, true);
        this.defaultClause = defaultClause;
        this.reqParams = params;
        findAction = fa;
        req = new SqlRequetorRequest();
        addOldRequest();

        buildListOpeartors();
        initLinkTablesAndFieldsMap();
        fillLinkTablesComboBox();
        initInnerJoinsMap(" INNER JOIN ");
        buildListFields(reqParams.getRootLink(), currentListFieldsModel, listCurrentFields);
        setVisibleLinkFields(false);
        buildGui();

        initListeners();
    }


    SqlRequetor(FindAction fa, RequetorParameters params, String defaultClause, boolean displayWaitingPanel) {
        this(fa, params, defaultClause);
        this.displayWaitingPanel = displayWaitingPanel;
        if (displayWaitingPanel) {
            waitingPanel = new WaitingPanel();
            setGlassPane(waitingPanel);
        }
    }


    public void setMandatoryClause(String clause) {
        mandatoryClause = clause;
    }


    public void setOrderClause(String orderByClause) {
        orderClause = orderByClause;
    }


    public String getSearchQuery() {
        if (sqlListModel.isEmpty()) {
            return "";
        }
        return buildQuery();
    }


    SqlRequetorRequest getSqlRequetorRequest() {
        return req;
    }


    void buttonValidateActionPerformed(ActionEvent event) {
        if (sqlListModel.isEmpty()) {
            Toolkit.getDefaultToolkit().beep();
            return;
        }
        RequestTable table = findAction.getTable();
        RequestFactory oldRequetor = table.getDataSource().getLoadFactory();
        FieldsList oldSelector = table.getDataSource().getSelector();

        try {
            doLoad();
            dispose();
        }
        catch (RequestException e1) {
            table.getDataSource().setLoadFactory(oldRequetor);
            ErrorDialog.show(null, "Erreur lors de la recherche !!!!", e1);
        }
        finally {
            table.getDataSource().setSelector(oldSelector);
        }
    }


    void buttonCancelActionPerformed(ActionEvent event) {
        if (event.getSource() == cancelButton) {
            sqlListModel.clear();
            dispose();
        }
    }


    void buttonDeleteActionPerformed(ActionEvent event) {
        if (sqlListModel.isEmpty() || listSqlRequest.isSelectionEmpty()) {
            return;
        }
        if (sqlListModel.getSize() > 1 && getIndex() == 0) {
            req.setLogicalOper("", getIndex() + 1);
        }
        textFieldValue.setText("");
        req.removeElements(getIndex());
        sqlListModel.removeElementAt(getIndex());
        updateSqlRequest();
    }


    void buttonAddActionPerformed(ActionEvent event) {
        if (sqlListModel.isEmpty()) {
            return;
        }
        addNewLine();
    }


    void buttonAndActionPerformed(ActionEvent event) {
        if (getIndex() == 0) {
            return;
        }
        req.setLogicalOper("and ", getIndex());
        updateSqlRequest();
    }


    void buttonOrActionPerformed(ActionEvent event) {
        if (getIndex() == 0) {
            return;
        }
        req.setLogicalOper("or ", getIndex());
        updateSqlRequest();
    }


    void textFieldValueKeyReleased() {
        if (!sqlListModel.isEmpty()) {
            req.setValue(textFieldValue.getText(), getIndex());
            updateSqlRequest();
        }
    }


    void buttonDeleteAllActionPerformed(ActionEvent event) {
        sqlListModel.removeAllElements();
        req.removeAllElements();
    }


    void linkTablesComboBoxActionPerformed(ActionEvent event) {
        Link selectLinkTable = (Link)linkTablesComboBox.getSelectedItem();
        linkListFieldsModel.removeAllElements();
        buildListFields(selectLinkTable, linkListFieldsModel, listLinkFields);
        setVisibleLinkFields(selectLinkTable != EMPTY_LINK);
    }


    private void setVisibleLinkFields(boolean visible) {
        linkFieldsScrollPane.setVisible(visible);
        listLinkFields.setVisible(visible);
    }


    void findInSelectionCheckBoxActionPerformed(ActionEvent event) {
        if (findInSelectionCheckBox.isSelected()) {
            addOldRequest();
        }
        else {
            removeOldRequest();
        }
    }


    void leftJoinCheckBoxActionPerformed(ActionEvent event) {
        if (leftJoinCheckBox.isSelected()) {
            req.setLeftJoin(true);
            initInnerJoinsMap(" LEFT JOIN ");
        }
        else {
            req.setLeftJoin(false);
            initInnerJoinsMap(" INNER JOIN ");
        }
    }


    /**
     * Action permettant de sauvegarder la requête courante dans un fichier texte.
     */
    void buttonSaveActionPerformed(ActionEvent event) {
        String fileName =
              FileChooserManager.showChooserForExport("requête.txt", "Sauvegarde de la requête", this);
        if (fileName == null) {
            return;
        }

        try {
            saveRequest(fileName);
        }
        catch (IOException ex) {
            APP.error("Erreur lors de la sauvegarde de la requête", ex);
            ErrorDialog.show(this, "Erreur lors de la sauvegarde de la requête", ex);
        }
    }


    /**
     * Action permettant d'ouvrir une requête sauvegardée dans un fichier texte.
     */
    void buttonOpenActionPerformed(ActionEvent event) {
        String fileName =
              FileChooserManager.showChooserForOpen("requête.txt", "Ouverture de la requête", this);
        if (fileName == null) {
            return;
        }

        try {
            loadRequest(fileName);
        }
        catch (Exception ex) {
            APP.error("Erreur lors de l'ouverture de la requête", ex);
            ErrorDialog.show(this, "Erreur lors de l'ouverture de la requête", ex);
        }
    }


    /**
     * Retourne l'index de l'élément courant de la liste des requêtes.
     *
     * @return L'index.
     */
    private int getIndex() {
        int idx;
        if (sqlListModel.isEmpty()) {
            idx = 0;
        }
        else {
            if (listSqlRequest.isSelectionEmpty()) {
                idx = sqlListModel.size() - 1;
            }
            else {
                idx = listSqlRequest.getSelectedIndex();
            }
        }
        return idx;
    }


    /**
     * Construit la requête à envoyer au serveur.
     *
     * @return La requête.
     */
    private String buildQuery() {
        queryFromClause = getQueryFromClause();
        return reqParams.getSelectClause() + queryFromClause;
    }


    String getQueryFromClause() {
        StringBuilder fromClause = new StringBuilder(" from ");
        fromClause.append(reqParams.getRootLink().getTo()).append(buildInnerJoinKeys());

        if (!"".equals(defaultClause) && findInSelection) {
            fromClause.append(defaultClause).append(" and (");
        }
        else {
            fromClause.append(" where (");
        }

        if (!"".equals(mandatoryClause)) {
            fromClause.append(mandatoryClause);
            if (!sqlListModel.isEmpty()) {
                fromClause.append(" and (");
            }
        }

        for (int i = 0; i < sqlListModel.size(); i++) {
            fromClause.append((sqlListModel.get(i)).toString());
        }

        if (!"".equals(mandatoryClause) && !sqlListModel.isEmpty()) {
            fromClause.append(") ");
        }

        fromClause.append(")");
        if (!"".equals(orderClause)) {
            fromClause.append(" order by ");
            fromClause.append(orderClause);
        }
        return fromClause.toString();
    }


    /**
     * Retire les quotes de la valeur du champ sur lequel on désire faire une recherche.
     *
     * @param value La valeur du champ.
     *
     * @return La valeur du champ sans quote.
     */
    private String removeQuote(String value) {
        StringBuilder tmp = new StringBuilder(value);
        char quote = '\'';
        int index = 0;
        while (index < tmp.length()) {
            if (tmp.charAt(index) == quote) {
                tmp.deleteCharAt(index);
                index++;
            }
            index++;
        }
        return tmp.toString();
    }


    /**
     * Retourne le type SQL du champ sélectioné.
     *
     * @return Le type SQL.
     */
    private int findSqlType() {
        return req.getLink(getIndex()).getToTable().getFieldBySql(req.getField(getIndex())).getSqlType();
    }


    /**
     * Rempli la liste des champs de la table désirée.
     *
     * @param link  La structure de la table.
     * @param model Le model de la liste.
     * @param list  La liste.
     */
    private static void buildListFields(Link link, DefaultListModel model, JList list) {
        Map map = link.getToTable().getFieldsBySqlKey();
        Object[] keys = map.keySet().toArray();

        Arrays.sort(keys, new FieldLabelComparator(map));

        for (int i = 0; i < keys.length; i++) {
            model.add(i, keys[i]);
        }

        list.setCellRenderer(new StructureRenderer(map));
    }


    /**
     * Met à jour la liste des requêtes.
     */
    private void updateSqlRequest() {
        for (int i = 0; i < sqlListModel.size(); i++) {
            sqlListModel.setElementAt(req.getRequest(i), i);
        }
    }


    /**
     * Rempli la liste des opérateurs de comparaison.
     */
    private void buildListOpeartors() {
        List<String> oper = new ArrayList<String>();
        oper.add("Egal");
        oper.add("Supérieur");
        oper.add("Supérieur ou égal");
        oper.add("Inférieur");
        oper.add("Inférieur ou égal");
        oper.add("Différent");
        oper.add("Commence par");
        oper.add("Ne commence pas par");
        oper.add("Finit par");
        oper.add("Ne finit pas par");
        oper.add("Contient");
        oper.add("Ne contient pas");
        oper.add("Est nul");
        oper.add("N'est pas nul");
        listOperators = new JList(oper.toArray());
    }


    /**
     * Initialisation du gui
     */
    private void buildGui() {
        listCurrentFields.setModel(currentListFieldsModel);
        listCurrentFields.setBounds(new Rectangle(29, 27, 183, 142));

        operatorsScrollPane.setBorder(BorderFactory.createEtchedBorder());
        operatorsScrollPane.setBounds(new Rectangle(439, 44, 189, 147));
        listLinkFields.setBounds(new Rectangle(29, 27, 183, 142));
        listLinkFields.setModel(linkListFieldsModel);

        validateButton.setBounds(new Rectangle(477, 40, 79, 25));
        validateButton.setName("validateButton");
        buttonOpen.setBounds(new Rectangle(128, 40, 111, 25));
        buttonSave.setBounds(new Rectangle(3, 40, 118, 25));
        cancelButton.setBounds(new Rectangle(563, 40, 79, 25));
        cancelButton.setName("cancelButton");
        findInSelectionCheckBox.setSelected(true);
        findInSelectionCheckBox.setToolTipText(
              "<html><b><u>But</u> : </b><i>Cela permet de rappeler la dernière recherche<br>"
              + "afin de la compléter et/ou de la modifier.</i></html>");
        findInSelectionCheckBox.setBounds(new Rectangle(3, 18, 400, 18));
        leftJoinCheckBox.setToolTipText(
              "<html><b><u>But</u> : </b><i>Cela permet de conserver les lignes de la table principale<br>"
              + "même s'il n'existe aucune ligne correspondante dans la table liée.</i></html>");
        leftJoinCheckBox.setBounds(new Rectangle(3, 0, 400, 18));

        JPanel panelRequest = new JPanel();
        panelRequest.setLayout(null);
        panelRequest.setBounds(new Rectangle(10, 5, 647, 493));
        panelRequest.add(buildPanelConstructRequest(), null);
        panelRequest.add(buildPanelShowRequest(), null);
        panelRequest.add(buildPanelButton(), null);

        getContentPane().setBackground(UIManager.getColor("Panel.background"));
        setFont(new java.awt.Font("Dialog", 0, 10));
        getContentPane().setLayout(null);
        getContentPane().add(panelRequest, null);
        setSize(new Dimension(675, 534));
    }


    private JPanel buildPanelShowRequest() {
        JPanel panelShowRequest = new JPanel();
        panelShowRequest.setBorder(createTitledBorder("Texte de la requête"));
        panelShowRequest.setBounds(new Rectangle(2, 310, 643, 112));
        panelShowRequest.setLayout(null);

        listSqlRequest.setName("listSqlRequest");
        listSqlRequest.setModel(sqlListModel);
        listSqlRequest.setBounds(new Rectangle(31, 223, 434, 47));
        sqlRequestScrollPane.getViewport().add(listSqlRequest);
        sqlRequestScrollPane.setBorder(BorderFactory.createEtchedBorder());
        sqlRequestScrollPane.setBounds(new Rectangle(6, 21, 631, 85));
        panelShowRequest.add(sqlRequestScrollPane, null);

        return panelShowRequest;
    }


    private void showDateField(boolean visible) {
        if (visible) {
            textFieldValue.setPreferredSize(new Dimension(475, 24));
            dateFieldLabel.setVisible(true);
            calendarButton.setVisible(true);
        }
        else {
            textFieldValue.setPreferredSize(new Dimension(602, 24));
            dateFieldLabel.setVisible(false);
            calendarButton.setVisible(false);
        }
        this.repaint();
    }


    private JPanel buildPanelConstructRequest() {
        JPanel panelConstructRequest = new JPanel();
        panelConstructRequest.setBorder(createTitledBorder("Saisie de la requête"));
        panelConstructRequest.setBounds(new Rectangle(2, 0, 643, 310));
        panelConstructRequest.setLayout(null);

        buttonDeleteAll.setBounds(new Rectangle(229, 208, 185, 22));
        panelConstructRequest.add(buttonDeleteAll, null);

        JLabel labelFields = new JLabel();
        labelFields.setHorizontalAlignment(SwingConstants.CENTER);
        labelFields.setText(reqParams.getRootLink().getToTable().getLabel());
        labelFields.setBounds(new Rectangle(46, 21, 131, 15));
        panelConstructRequest.add(labelFields, null);

        labelLinkTables.setHorizontalAlignment(SwingConstants.CENTER);
        labelLinkTables.setBounds(new Rectangle(256, 21, 131, 15));
        panelConstructRequest.add(labelLinkTables, null);

        JPanel textFieldEditor = new JPanel(new FlowLayout(FlowLayout.LEFT));
        textFieldEditor.setBounds(new Rectangle(16, 269, 612, 30));

        textFieldValue.setName("textFieldValue");
        textFieldValue.setPreferredSize(new Dimension(602, 24));
        textFieldEditor.add(textFieldValue);

        calendarButton = new JButton();
        calendarButton.setName("showCalendarButton");
        calendarButton.setVisible(false);
        calendarButton.setVerticalAlignment(SwingConstants.CENTER);
        calendarButton.setPreferredSize(new Dimension(24, 24));
        calendarButton.setIcon(new ImageIcon(DateField.class.getResource("calendar.icon.gif")));
        textFieldEditor.add(calendarButton);

        dateFieldLabel = new JLabel("( yyyy-MM-dd )");
        dateFieldLabel.setVisible(false);
        dateFieldLabel.setVerticalAlignment(SwingConstants.CENTER);
        textFieldEditor.add(dateFieldLabel);

        panelConstructRequest.add(textFieldEditor, null);

        currentFieldsScrollPane.setBorder(BorderFactory.createEtchedBorder());
        currentFieldsScrollPane.setBounds(new Rectangle(16, 44, 188, 147));
        listCurrentFields.setName("listCurrentFields");
        currentFieldsScrollPane.getViewport().add(listCurrentFields);
        panelConstructRequest.add(currentFieldsScrollPane, null);

        buttonDelete.setBounds(new Rectangle(110, 208, 92, 22));
        panelConstructRequest.add(buttonDelete, null);

        JLabel labelOperators = new JLabel("Liste des opérateurs");
        labelOperators.setBounds(new Rectangle(464, 21, 131, 15));
        labelOperators.setHorizontalAlignment(SwingConstants.CENTER);
        panelConstructRequest.add(labelOperators, null);

        linkTablesComboBox.setName("linkTablesComboBox");
        linkTablesComboBox.setBounds(new Rectangle(228, 44, 187, 21));
        panelConstructRequest.add(linkTablesComboBox, null);

        buttonOr.setBounds(new Rectangle(578, 208, 50, 22));
        panelConstructRequest.add(buttonOr, null);

        JLabel labelValue = new JLabel("Valeur");
        labelValue.setHorizontalAlignment(SwingConstants.CENTER);
        labelValue.setBounds(new Rectangle(285, 252, 72, 17));
        panelConstructRequest.add(labelValue, null);

        listOperators.setName("listOperators");
        operatorsScrollPane.getViewport().add(listOperators);
        panelConstructRequest.add(operatorsScrollPane, null);

        buttonAnd.setBounds(new Rectangle(439, 208, 48, 22));
        panelConstructRequest.add(buttonAnd, null);

        buttonAdd.setBounds(new Rectangle(16, 208, 78, 22));
        buttonAdd.setName("buttonAdd");
        panelConstructRequest.add(buttonAdd, null);

        linkFieldsScrollPane.setBorder(BorderFactory.createEtchedBorder());
        linkFieldsScrollPane.setBounds(new Rectangle(227, 76, 188, 115));
        listLinkFields.setName("listLinkFields");
        linkFieldsScrollPane.getViewport().add(listLinkFields);
        panelConstructRequest.add(linkFieldsScrollPane, null);

        return panelConstructRequest;
    }


    private JPanel buildPanelButton() {
        JPanel panelButton = new JPanel();
        panelButton.setBounds(new Rectangle(2, 425, 643, 66));
        panelButton.setLayout(null);
        panelButton.add(buttonSave, null);
        panelButton.add(buttonOpen, null);
        panelButton.add(cancelButton, null);
        panelButton.add(validateButton, null);
        panelButton.add(leftJoinCheckBox, null);
        panelButton.add(findInSelectionCheckBox, null);

        return panelButton;
    }


    private void initListeners() {
        textFieldValue.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent event) {
                textFieldValueKeyReleased();
            }


            public void removeUpdate(DocumentEvent event) {
                textFieldValueKeyReleased();
            }


            public void changedUpdate(DocumentEvent event) {
                textFieldValueKeyReleased();
            }
        });

        buttonAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent event) {
                buttonAddActionPerformed(event);
            }
        });
        buttonDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent event) {
                buttonDeleteActionPerformed(event);
            }
        });

        linkTablesComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent event) {
                linkTablesComboBoxActionPerformed(event);
            }
        });

        buttonDeleteAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent event) {
                buttonDeleteAllActionPerformed(event);
            }
        });
        buttonAnd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent event) {
                buttonAndActionPerformed(event);
            }
        });

        buttonOr.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent event) {
                buttonOrActionPerformed(event);
            }
        });

        validateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if (displayWaitingPanel) {
                    waitingPanel.exec(new WaitingRunnable(event));
                }
                else {
                    buttonValidateActionPerformed(event);
                }
            }
        });

        buttonOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent event) {
                buttonOpenActionPerformed(event);
            }
        });
        buttonSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent event) {
                buttonSaveActionPerformed(event);
            }
        });

        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent event) {
                buttonCancelActionPerformed(event);
            }
        });

        findInSelectionCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent event) {
                findInSelectionCheckBoxActionPerformed(event);
            }
        });
        leftJoinCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent event) {
                leftJoinCheckBoxActionPerformed(event);
            }
        });

        calendarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                CalendarHelper calendar = new CalendarHelper();
                calendar.askDialog(textFieldValue, calendarButton, "yyyy-MM-dd");
            }
        });

        // List Listeners
        CurrentFieldSelectionListener currentFieldSelection = new CurrentFieldSelectionListener();
        listCurrentFields.getSelectionModel().addListSelectionListener(currentFieldSelection);

        LinkFieldSelectionListener linkFieldSelection = new LinkFieldSelectionListener();
        listLinkFields.getSelectionModel().addListSelectionListener(linkFieldSelection);

        OperatorSelectionListener operatorSelection = new OperatorSelectionListener();
        listOperators.getSelectionModel().addListSelectionListener(operatorSelection);

        SqlRequestSelectionListener sqlRequestSelection = new SqlRequestSelectionListener();
        listSqlRequest.getSelectionModel().addListSelectionListener(sqlRequestSelection);
    }


    private TitledBorder createTitledBorder(String titleName) {
        return new TitledBorder(BorderFactory.createEtchedBorder(Color.white,
                                                                 new Color(134, 134, 134)), titleName);
    }


    /**
     * Rempli les Maps des tables liées avec les champs de jointure (clé : Id de la table liée, valeur : la
     * liste des champs de jointure (source pour la 1ère Map, dest pour la 2ème)).
     */
    private void initLinkTablesAndFieldsMap() {
        srcLinkTableFieldsName = new HashMap<Link, List<String>>();
        destLinkTableFieldsName = new HashMap<Link, List<String>>();
        operatorLinkTableFieldsName = new HashMap<Link, List<String>>();

        Link[] links = reqParams.getLinks();
        for (Link link : links) {
            String[][] jks = reqParams.getJoinKeyToRootTableFor(link);

            List<String> srcFieldName = new ArrayList<String>();
            List<String> destFieldName = new ArrayList<String>();
            List<String> operatorFieldName = new ArrayList<String>();
            for (String[] jk : jks) {
                srcFieldName.add(jk[0]);
                destFieldName.add(jk[1]);
                operatorFieldName.add(jk[2]);
            }
            srcLinkTableFieldsName.put(link, srcFieldName);
            destLinkTableFieldsName.put(link, destFieldName);
            operatorLinkTableFieldsName.put(link, operatorFieldName);
        }
    }


    /**
     * Construit la string avec toutes les jointures utiles à la requête que l'on souhaite exécuter (parcours
     * des tables liées présentes dans la requête).
     *
     * @return La string des jointures
     */
    private String buildInnerJoinKeys() {
        Set<Link> usedTablesSet = new HashSet<Link>();

        // Ajouter les jointures nécessaires pour la clause WHERE
        for (int i = 0; i < sqlListModel.size(); i++) {
            Link link = req.getLink(i);
            usedTablesSet.add(link);
        }

        // Ajouter les jointures nécessaires pour la clause SELECT
        usedTablesSet.addAll(reqParams.getLinksUsedInSelectClause());

        //Ajouter les jointures nécessaires pour la clause WHERE
        usedTablesSet.addAll(getLinksUsedInWhereClause(mandatoryClause));

        // Construire les clauses de jointure
        StringBuilder innerJoinKey = new StringBuilder();
        String rootTableName = reqParams.getRootLink().getTo();
        for (Link link : usedTablesSet) {
            String sqlTableName = link.getTo();
            String innerJoinClause = String.valueOf(innerJoins.get(sqlTableName));
            if (!sqlTableName.equals(rootTableName) && !innerJoinKey.toString().contains(innerJoinClause)) {
                innerJoinKey.append(innerJoinClause);
            }
        }

        return innerJoinKey.toString();
    }


    /**
     * Remplit la Map des jointures a partir de la clause where.
     *
     * @param whereClause clause obligatoire.
     *
     * @return la map
     */
    private Collection<Link> getLinksUsedInWhereClause(String whereClause) {
        Set<Link> linkSet = new HashSet<Link>();
        if (!"".equals(whereClause)) {
            Link[] links = reqParams.getLinks();
            for (Link link : links) {
                TableStructure toTableStructure = link.getToTable();
                String toTableName = toTableStructure.getSqlName();
                if (!"".equals(toTableName) && whereClause.contains(toTableName)) {
                    linkSet.add(link);
                }
            }
        }
        return linkSet;
    }


    /**
     * Remplit la Map des jointures (clé : nom physique de la table liée, valeur : la string de jointure avec
     * la table courante).
     *
     * @param joinType Description of the Parameter
     */
    private void initInnerJoinsMap(String joinType) {
        innerJoins.clear();

        for (Link link : srcLinkTableFieldsName.keySet()) {
            Link rootLink = reqParams.getRootLink();

            List<String> srcLinkFieldName = srcLinkTableFieldsName.get(link);
            List<String> destLinkFieldName = destLinkTableFieldsName.get(link);
            List<String> operatorLinkFieldName = operatorLinkTableFieldsName.get(link);

            StringBuilder innerJoinStr = new StringBuilder(joinType);

            innerJoinStr.append(link.getTo()).append(" ON ");
            innerJoinStr.append(buildFullSqlFieldName(rootLink, srcLinkFieldName, 0))
                  .append(" ").append(operatorLinkFieldName.get(0)).append(" ")
                  .append(buildFullSqlFieldName(link, destLinkFieldName, 0)).append(" ");

            for (int i = 0; i < srcLinkFieldName.size() - 1; i++) {
                innerJoinStr.append("AND ");

                innerJoinStr.append(buildFullSqlFieldName(rootLink, srcLinkFieldName, i + 1))
                      .append(" ").append(operatorLinkFieldName.get(i + 1)).append(" ")
                      .append(buildFullSqlFieldName(link, destLinkFieldName, i + 1))
                      .append(" ");
            }
            innerJoins.put(link.getTo(), innerJoinStr.toString());
        }
    }


    private String buildFullSqlFieldName(Link link, List linkFieldName, int index) {
        return link.completeSqlFieldName((String)linkFieldName.get(index));
    }


    /**
     * Rempli le combo des tables liées.
     */
    private void fillLinkTablesComboBox() {
        linkTablesComboBox = new JComboBox(reqParams.getLinks());

        linkTablesComboBox.addItem(EMPTY_LINK);
        linkTablesComboBox.setSelectedItem(EMPTY_LINK);
        if (reqParams.getLinks().length == 0) {
            labelLinkTables.setVisible(false);
            linkTablesComboBox.setVisible(false);
        }
    }


    /**
     * Ajoute une nouvelle ligne à la requête.
     */
    private void addNewLine() {
        int idx = sqlListModel.getSize();
        req.addElements(idx);
        req.setLogicalOper(" and ", idx);
        sqlListModel.add(idx, "");
        updateSqlRequest();
        listSqlRequest.clearSelection();
        listSqlRequest.setVisibleRowCount(listSqlRequest.getModel().getSize() - 1);
    }


    /**
     * Ajoute l'ancienne requête à la requête actuelle pour faire une recherche dans sélection.
     */
    private void addOldRequest() {
        findInSelection = true;
        if (findAction.getLastRequest() != null) {
            req = new SqlRequetorRequest(findAction.getLastRequest());
            sqlListModel.removeAllElements();
            for (int i = 0; i < req.getRequestListSize(); i++) {
                sqlListModel.add(i, req.getRequest(i));
            }
            leftJoinCheckBox.setSelected(req.getLeftJoin());
        }
    }


    /**
     * Supprime l'ancienne requête de la requête actuelle pour faire une recherche sur la table entière.
     */
    private void removeOldRequest() {
        findInSelection = false;
        sqlListModel.removeAllElements();
        req = new SqlRequetorRequest();
        leftJoinCheckBox.setSelected(false);
    }


    /**
     * Charge la requête présente dans le fichier texte dans le requêteur. Par défaut on supprime les critères
     * précédents (on décoche la case à cocher).
     *
     * @param fileName Le nom du fichier à charger
     *
     * @throws IOException erreur
     */
    private void loadRequest(String fileName) throws IOException {
        findInSelectionCheckBox.setSelected(false);
        removeOldRequest();
        textFieldValue.setText("");
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(new File(fileName)));
            String line = reader.readLine();
            int row = 0;

            while (line != null) {
                req.addElements(row);
                sqlListModel.add(row, "");
                StringTokenizer st = new StringTokenizer(line, TABULATION_SEPARATOR, false);
                int idx = 0;
                while (st.hasMoreTokens()) {
                    if (row == 0 && idx == 0) {
                        req.setLogicalOper("", row);
                        idx++;
                    }
                    String element = st.nextToken();
                    updateRequestElement(row, idx, element);
                    idx++;
                }
                row++;
                line = reader.readLine();
            }
            updateSqlRequest();
        }
        finally {
            if (reader != null) {
                reader.close();
            }
        }
    }


    /**
     * Permet de mettre à jour un élément de la requête.
     *
     * @param row     La ligne en cours de lecture
     * @param idx     L'élément de la ligne
     * @param element L'élément
     *
     * @throws IllegalArgumentException erreur
     */
    private void updateRequestElement(int row, int idx, String element) {
        switch (idx) {
            case 0:
                req.setLogicalOper(element, row);
                break;
            case 1:
                req.setLink(reqParams.getLink(element), row);
                break;
            case 2:
                req.setField(element, row);
                break;
            case 3:
                req.setCompareOper(Integer.parseInt(element), row);
                break;
            case 4:
                req.setPrefixValue(element, row);
                break;
            case 5:
                req.setValue(element, row);
                break;
            case 6:
                req.setSuffixValue(element, row);
                break;
            default:
                throw new IllegalArgumentException("Trop d'éléments !");
        }
    }


    /**
     * Sauvegarde la requête courante dans un fichier texte.
     *
     * @param fileName Le nom du fichier à sauvegarder
     *
     * @throws IOException -
     */
    private void saveRequest(String fileName) throws IOException {
        FileWriter out = null;
        try {
            out = new FileWriter(new File(fileName));
            for (int i = 0; i < sqlListModel.size(); i++) {
                out.write(req.getLogicalOper(i) + TABULATION_SEPARATOR
                          + req.getLink(i).getTo() + TABULATION_SEPARATOR + req.getField(i)
                          + TABULATION_SEPARATOR + req.getCompareOperValue(i)
                          + TABULATION_SEPARATOR + req.getPrefixValue(i) + TABULATION_SEPARATOR
                          + req.getValue(i) + TABULATION_SEPARATOR + req.getSuffixValue(i)
                          + "\r" + "\n");
            }
        }
        finally {
            if (out != null) {
                out.close();
            }
        }
    }


    private void doLoad() throws RequestException {
        String query = getSearchQuery();

        if ("".equals(query)) {
            return;
        }

        if (APP.isDebugEnabled()) {
            APP.debug("*****************************************");
            APP.debug("Search query : " + query);
            APP.debug("*****************************************");
        }
        RequestTable table = findAction.getTable();

        RequetorFactory requetor = new RequetorFactory(table.getPreference().getRequetor().getId());
        requetor.setSqlQuery(query);
        requetor.setDisplayedColNames(reqParams.getColNamesForSelect());
        addLinkedFieldTypes(requetor);

        table.getDataSource().setLoadFactory(requetor);
        table.getDataSource().setSelector(new FieldsList());

        table.setCurrentPage(1);
        table.load();

        table.firePropertyChange("SqlRequetor.load", true, false);

        findAction.setLastQueryFromClause(queryFromClause);
        findAction.setLastRequest(req);
    }


    /**
     * Renseigne la RequetorFactory avec la liste des champs affichés provenant des tables liées, ainsi que
     * leur type SQL Java.
     *
     * @param requetor la factory à renseigner.
     */
    private void addLinkedFieldTypes(RequetorFactory requetor) {
        for (Object object : reqParams.getLinkedFieldsUsedInSelectClause()) {
            FieldStructure field = (FieldStructure)object;
            int sqlType = field.getSqlType();

            // Si le type n'est pas précisé, prendre varchar
            if (sqlType == Types.JAVA_OBJECT) {
                sqlType = Types.VARCHAR;
            }

            requetor.addLinkedFieldType(field.getJavaName(), sqlType);
        }
    }


    /**
     * Classe gérant la sélection sur la liste des champs de la table courante.
     *
     * @author $Author: gaudefr $
     * @version $Revision: 1.28 $
     */
    private class CurrentFieldSelectionListener implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent event) {
            if (event.getValueIsAdjusting()) {
                return;
            }
            ListSelectionModel lsm = (ListSelectionModel)event.getSource();
            if (!lsm.isSelectionEmpty()) {
                if (sqlListModel.isEmpty()) {
                    req.addElements(0);
                    sqlListModel.add(0, "");
                }
                req.setField(((String)listCurrentFields.getSelectedValue()), getIndex());
                req.setLink(reqParams.getRootLink(), getIndex());
                int sqlType = findSqlType();
                showDateField(sqlType == Types.DATE || sqlType == Types.TIME);
                if (req.getCompareOperValue(getIndex()) != -1) {
                    req.updatePrefSuffValue(req.getCompareOperValue(getIndex()), getIndex(), sqlType);
                }
                updateSqlRequest();
            }
            listCurrentFields.clearSelection();
        }
    }

    /**
     * Classe gérant la sélection sur la liste des champs de la table liée.
     *
     * @author $Author: gaudefr $
     * @version $Revision: 1.28 $
     */
    private class LinkFieldSelectionListener implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent event) {
            if (event.getValueIsAdjusting()) {
                return;
            }
            ListSelectionModel lsm = (ListSelectionModel)event.getSource();
            if (!lsm.isSelectionEmpty()) {
                if (sqlListModel.isEmpty()) {
                    sqlListModel.add(0, "");
                    req.addElements(0);
                }

                req.setLink((Link)linkTablesComboBox.getSelectedItem(), getIndex());
                req.setField(listLinkFields.getSelectedValue().toString(), getIndex());

                if (req.getCompareOperValue(getIndex()) != -1) {
                    req.updatePrefSuffValue(req.getCompareOperValue(getIndex()), getIndex(), findSqlType());
                }
                updateSqlRequest();
                listLinkFields.clearSelection();
            }
        }
    }

    /**
     * Classe gérant la sélection sur la liste des opérateurs.
     *
     * @author $Author: gaudefr $
     * @version $Revision: 1.28 $
     */
    private class OperatorSelectionListener implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent event) {
            if (event.getValueIsAdjusting()) {
                return;
            }
            ListSelectionModel lsm = (ListSelectionModel)event.getSource();
            if (!lsm.isSelectionEmpty()) {
                if (!sqlListModel.isEmpty()) {
                    if (!"".equalsIgnoreCase(req.getField(getIndex()))) {
                        int operIdx = listOperators.getSelectedIndex();
                        req.setCompareOper(operIdx, getIndex());
                        req.updatePrefSuffValue(operIdx, getIndex(), findSqlType());
                        if (operIdx == SqlRequetorRequest.NULL || operIdx == SqlRequetorRequest.NOT_NULL) {
                            textFieldValue.setText("");
                            textFieldValue.setEditable(false);
                            req.setValue("", getIndex());
                        }
                        else {
                            textFieldValue.setEditable(true);
                            textFieldValue.selectAll();
                            textFieldValue.requestFocus();
                        }
                        updateSqlRequest();
                    }
                }
            }
            listOperators.clearSelection();
        }
    }

    /**
     * Classe gérant la sélection sur la liste des requêtes.
     *
     * @author $Author: gaudefr $
     * @version $Revision: 1.28 $
     */
    private class SqlRequestSelectionListener implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent event) {
            if (event.getValueIsAdjusting()) {
                return;
            }
            ListSelectionModel lsm = (ListSelectionModel)event.getSource();
            if (!lsm.isSelectionEmpty()) {
                if (!sqlListModel.isEmpty()) {
                    int operIdx = req.getCompareOperValue(getIndex());
                    if (operIdx == SqlRequetorRequest.NULL || operIdx == SqlRequetorRequest.NOT_NULL) {
                        textFieldValue.setEditable(false);
                    }
                    else {
                        textFieldValue.setEditable(true);
                        textFieldValue.setText(removeQuote(req.getValue(listSqlRequest.getSelectedIndex())));
                    }
                    showDateField(false);
                }
                else {
                    textFieldValue.setText("");
                }
            }
        }
    }

    private class WaitingRunnable implements Runnable {
        private ActionEvent event;


        WaitingRunnable(ActionEvent event) {
            this.event = event;
        }


        public void run() {
            buttonValidateActionPerformed(event);
        }
    }
}
