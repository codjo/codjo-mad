package net.codjo.mad.gui.request.requetor;
import net.codjo.gui.toolkit.util.ErrorDialog;
import net.codjo.gui.toolkit.util.GuiUtil;
import net.codjo.mad.common.structure.StructureReader;
import net.codjo.mad.common.structure.TableStructure;
import net.codjo.mad.gui.framework.AbstractGuiAction;
import net.codjo.mad.gui.framework.GuiContext;
import net.codjo.mad.gui.request.RequestTable;
import net.codjo.mad.gui.request.action.ModalityService;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.swing.UIManager;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

public class FindAction extends AbstractGuiAction {
    private static final Logger APP = Logger.getLogger(FindAction.class);
    private SqlRequetorRequest lastRequest = null;
    private String lastQueryFromClause;
    private RequestTable table;
    private String mandatoryClause = "";
    private String orderClause = "";
    private boolean displayWaitingPanel = false;


    public FindAction(GuiContext ctxt, RequestTable table) {
        super(ctxt, "Rechercher", "Définir les critères de recherche", "mad.load");
        this.table = table;
        setEnabled(table.getPreference().getRequetor() != null);
    }


    public boolean isDisplayWaitingPanel() {
        return displayWaitingPanel;
    }


    public void setDisplayWaitingPanel(boolean displayWaitingPanel) {
        this.displayWaitingPanel = displayWaitingPanel;
    }


    public void setLastQueryFromClause(String lastQueryFromClause) {
        this.lastQueryFromClause = lastQueryFromClause;
    }


    public void setLastRequest(SqlRequetorRequest lastRequest) {
        this.lastRequest = lastRequest;
    }


    SqlRequetorRequest getLastRequest() {
        return lastRequest;
    }


    public String getLastQueryFromClause() {
        return lastQueryFromClause;
    }


    public RequestTable getTable() {
        return table;
    }


    public void actionPerformed(ActionEvent evt) {
        try {
            displayWaitCursor();
            setEnabled(false);

            RequetorParameters params = buildRequetorParameters();
            SqlRequetor sqlRequetor = new SqlRequetor(this, params, "", displayWaitingPanel);
            sqlRequetor.setMandatoryClause(mandatoryClause);
            sqlRequetor.setOrderClause(orderClause);
            sqlRequetor.addInternalFrameListener(new EndSearchListener());
            applyModality(sqlRequetor);
            sqlRequetor.setFrameIcon(UIManager.getIcon("icon"));
            sqlRequetor.setVisible(true);
            getDesktopPane().add(sqlRequetor);
            GuiUtil.centerWindow(sqlRequetor);
            try {
                sqlRequetor.setSelected(true);
            }
            catch (java.beans.PropertyVetoException g) {
                ; // echec
            }
        }
        catch (Exception e) {
            APP.error("Impossible d'afficher le requeteur !", e);
            ErrorDialog.show(table, "Impossible d'afficher le requeteur !", e);
            setEnabled(true);
        }
        finally {
            displayDefaultCursor();
        }
    }


    private void applyModality(SqlRequetor sqlRequetor) {
        ModalityService modalityService =
              (ModalityService)getGuiContext().getProperty(ModalityService.class);
        modalityService.apply(table, sqlRequetor);
    }


    /**
     * Construit le {@link RequetorParameters} nécessaire au fonctionnement du requêteur. On récupère les
     * paramétres à partir des fichiers structure, datagen et préférence.
     *
     * @return Le RequetorParameters
     *
     * @throws IOException                  Si l'un des fichiers est absent
     * @throws SAXException                 Si erreur dans l'un des fichiers XML
     * @throws ParserConfigurationException Si erreur de parsing
     */
    private RequetorParameters buildRequetorParameters()
          throws IOException, SAXException, ParserConfigurationException {
        StructureReader structureReader =
              RequetorLayerFactory.getFactory().getStructureReader();
        LinkFamilyReader lReader =
              RequetorLayerFactory.getFactory().getLinkFamilyReader();
        LinkFamily family =
              lReader.getFamily(table.getPreference().getRequetor().getId());

        Set<String> columns = new HashSet<String>(Arrays.asList(table.getPreference().getColumnsName()));
        columns.addAll(Arrays.asList(table.getPreference().getHiddenColumnsName()));

        Link rootLink = new Link(null, family.getRoot(), structureReader);

        Set<String> allColumns = new HashSet<String>();
        for (String column : columns) {
            if (rootLink.containsField(column)) {
                allColumns.add(column);
            }
            else {
                for (int i = 0; i < family.size(); i++) {
                    Link link = family.getLink(i);
                    TableStructure toTable = link.getToTable();
                    if (toTable.getFieldByJava(column) != null) {
                        allColumns.add(column);
                    }
                }
            }
        }

        return new RequetorParameters(structureReader, family,
                                      allColumns.toArray(new String[allColumns.size()]));
    }


    public void setSqlRequetorMandatoryClause(String clause) {
        mandatoryClause = clause;
    }


    public void setSqlRequetorOrderClause(String clause) {
        orderClause = clause;
    }


    /**
     * Ce listener est appelé lors de la fermeture de la fenêtre du requêteur. Il est chargé d'exécuter la
     * requête SQL et de recharger la RequestTable.
     */
    private final class EndSearchListener extends InternalFrameAdapter {
        @Override
        public void internalFrameClosed(InternalFrameEvent event) {
            doCleanup(event);
        }


        @Override
        public void internalFrameClosing(InternalFrameEvent event) {
            doCleanup(event);
        }


        private void doCleanup(InternalFrameEvent event) {
            event.getInternalFrame().removeInternalFrameListener(this);
            setEnabled(true);
        }
    }
}
