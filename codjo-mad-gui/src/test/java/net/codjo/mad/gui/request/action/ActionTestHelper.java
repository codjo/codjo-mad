package net.codjo.mad.gui.request.action;
import net.codjo.mad.client.request.Result;
import net.codjo.mad.client.request.util.RequestTestHelper;
import net.codjo.mad.gui.MadGuiContext;
import net.codjo.mad.gui.request.DetailDataSource;
import net.codjo.mad.gui.request.Preference;
import net.codjo.mad.gui.request.RequestTable;
import net.codjo.mad.gui.request.RequestToolBar;
import net.codjo.security.common.api.UserMock;
import java.awt.BorderLayout;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
/**
 * Description of the Class
 *
 * @author $Author: palmont $
 * @version $Revision: 1.4 $
 */
public class ActionTestHelper {
    RequestTable requestTable;
    JFrame window;
    RequestTestHelper requestHelper;
    JDesktopPane desktopPane = new JDesktopPane();
    public static final String DETAIL_WINDOW_TITLE = "Fenetre valide";
    private Preference preference = new Preference();


    public ActionTestHelper() {
    }


    public void setTableResult(Result result) {
        requestTable.setResult(result);
    }


    public void setUp() {
        window = new JFrame();
        window.getContentPane().add(desktopPane, BorderLayout.CENTER);

        MadGuiContext guiContext = new MadGuiContext();
        guiContext.setUser(new UserMock().mockIsAllowedTo(true));
        guiContext.putProperty(ModalityService.class, new ModalityService());
        guiContext.setDesktopPane(desktopPane);

        preference.setInsertId("newCodificationPtf");
        preference.setSelectByPkId("selectId");
        preference.setUpdateId("updateId");
        preference.setEntity("myEntity");

        requestTable = new RequestTable();
        requestTable.setPreference(preference);

        RequestToolBar requestToolBar = new RequestToolBar();
        requestToolBar.init(guiContext, requestTable);

        window.getContentPane().add(requestToolBar, BorderLayout.NORTH);

        requestHelper = new RequestTestHelper();
    }


    public void setDetailWindowClass(Class<? extends JInternalFrame> detailWindowClass) {
        preference.setDetailWindowClass(detailWindowClass);
    }


    public DetailDataSource dataSource(Object frame) {
        return ((BasicDetailWindow)frame).dataSource;
    }


    public static class BadDetailWindow extends JInternalFrame {
        @SuppressWarnings({"UnusedDeclaration"})
        public BadDetailWindow(DetailDataSource dataSource) {
            super(DETAIL_WINDOW_TITLE);
            throw new IllegalArgumentException("Erreur normale :)");
        }
    }

    public static class BasicDetailWindow extends JInternalFrame {
        private DetailDataSource dataSource;


        public BasicDetailWindow(DetailDataSource dataSource) {
            super(DETAIL_WINDOW_TITLE);
            this.dataSource = dataSource;
            getContentPane().add(new JLabel("DetailWindow"));
        }
    }
}
