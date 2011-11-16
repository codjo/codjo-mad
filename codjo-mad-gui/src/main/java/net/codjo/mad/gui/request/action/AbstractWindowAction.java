package net.codjo.mad.gui.request.action;
import net.codjo.gui.toolkit.util.ErrorDialog;
import net.codjo.gui.toolkit.util.GuiUtil;
import net.codjo.mad.client.request.RequestException;
import net.codjo.mad.gui.framework.AbstractGuiAction;
import net.codjo.mad.gui.framework.GuiContext;
import net.codjo.mad.gui.request.DetailDataSource;
import net.codjo.mad.gui.request.ListDataSource;
import net.codjo.mad.gui.request.Preference;
import net.codjo.mad.gui.request.RequestTable;
import net.codjo.mad.gui.request.event.DataSourceAdapter;
import net.codjo.mad.gui.request.event.DataSourceEvent;
import java.awt.event.ActionEvent;
import java.lang.reflect.InvocationTargetException;
import javax.swing.JInternalFrame;
import javax.swing.UIManager;
import org.apache.log4j.Logger;

public abstract class AbstractWindowAction extends AbstractGuiAction {
    private static final Logger APP = Logger.getLogger(AbstractWindowAction.class);
    private AutomaticLoadListener automaticLoad = new AutomaticLoadListener();
    private DetailWindowBuilder builder;
    private RequestTable table;
    private boolean modal = true;


    protected AbstractWindowAction(GuiContext ctxt,
                                   RequestTable table,
                                   DetailWindowBuilder builder,
                                   String name,
                                   String description,
                                   String iconId) {
        super(ctxt, name, description, iconId);
        this.table = table;
        this.builder = builder;
    }


    public void actionPerformed(ActionEvent event) {
        if (table.isEditable()) {
            try {
                displayWaitCursor();
                modifiableTableAction();
            }
            catch (Exception ex) {
                APP.error(ex.getMessage(), ex);
                ErrorDialog.show(table, "Erreur interne", ex);
            }
            finally {
                displayDefaultCursor();
            }
        }
        else {
            directDbAction();
        }
    }


    public void setModalForDetailWindow(boolean modal) {
        this.modal = modal;
    }


    protected ListDataSource getDataSource() {
        return getTable().getDataSource();
    }


    protected Preference getPreference() {
        return table.getPreference();
    }


    protected RequestTable getTable() {
        return table;
    }


    protected abstract void modifiableTableAction() throws Exception;


    protected abstract DetailDataSource newDetailDataSource();


    protected JInternalFrame buildFrame() throws Exception {
        DetailDataSource ds = newDetailDataSource();
        String entityName = table.getDataSource().getEntityName();
        if (entityName != null) {
            ds.setEntityName(entityName);
        }
        ds.addDataSourceListener(automaticLoad);
        return builder.buildFrame(ds, table.getPreference());
    }


    protected void directDbAction() {
        try {
            displayWaitCursor();
            JInternalFrame frame = buildFrame();
            displayFrame(frame);
        }
        catch (InvocationTargetException ex) {
            APP.error(ex.getTargetException().getMessage(), ex.getTargetException());
            ErrorDialog.show(table, "erreur interne", (Exception)ex.getTargetException());
        }
        catch (Exception ex) {
            APP.error(ex.getMessage(), ex);
            ErrorDialog.show(table, "Erreur interne", ex);
        }
        finally {
            displayDefaultCursor();
        }
    }


    private void displayFrame(final JInternalFrame frame) {
        frame.setFrameIcon(UIManager.getIcon("icon"));
        frame.setVisible(true);
        frame.pack();
        getGuiContext().getDesktopPane().add(frame);
        if (modal) {
            ModalityService modalityService =
                  (ModalityService)getGuiContext().getProperty(ModalityService.class);
            modalityService.apply(table, frame);
        }
        GuiUtil.centerWindow(frame);
        try {
            frame.setSelected(true);
        }
        catch (java.beans.PropertyVetoException g) {
            APP.error(g.getMessage(), g);
        }
    }


    private class AutomaticLoadListener extends DataSourceAdapter {
        @Override
        public void saveEvent(DataSourceEvent event) {
            try {
                table.load();
            }
            catch (RequestException ex) {
                // tant pis
                ErrorDialog.show(table, "Impossible d'afficher la réponse du serveur", ex);
            }
        }
    }
}
