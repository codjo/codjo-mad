package net.codjo.mad.gui.request.action;
import net.codjo.mad.client.request.Result;
import net.codjo.mad.client.request.Row;
import net.codjo.mad.gui.request.DetailDataSource;
import net.codjo.mad.gui.request.PreferenceFactory;
import javax.swing.JInternalFrame;
import org.uispec4j.Button;
import org.uispec4j.Desktop;
import org.uispec4j.Table;
import org.uispec4j.Trigger;
import org.uispec4j.UISpecTestCase;
import org.uispec4j.Window;
import org.uispec4j.interception.WindowHandler;
import org.uispec4j.interception.WindowInterceptor;

public abstract class WindowActionTestCase extends UISpecTestCase {
    public static final Class BAD_DW_CLASS = ActionTestHelper.BadDetailWindow.class;
    protected ActionTestHelper helper = new ActionTestHelper();


    public void test_detailWindowIsShown() throws Exception {
        iniGui(ActionTestHelper.BasicDetailWindow.class);
        Window window = new Window(helper.window);
        Button button = window.getButton(getActionName());

        button.click();

        Desktop desktop = new Desktop(helper.desktopPane);

        assertTrue(desktop.containsWindow(ActionTestHelper.DETAIL_WINDOW_TITLE));

        Window detailWindow = desktop.getWindow(ActionTestHelper.DETAIL_WINDOW_TITLE);
        assertTrue(detailWindow.isVisible());
        checkDetailDataSource(helper.dataSource(detailWindow.getAwtComponent()));
    }


    public void test_detailWindowOpen_fail() throws Exception {
        iniGui(ActionTestHelper.BadDetailWindow.class);
        Window window = new Window(helper.window);
        Button button = window.getButton(getActionName());

        WindowInterceptor
              .init(button.triggerClick())
              .process(new WindowHandler() {
                  @Override
                  public Trigger process(Window window) {
                      window.assertTitleEquals("Erreur");
                      return window.getButton("OK").triggerClick();
                  }
              })
              .run();

        Desktop desktop = new Desktop(helper.desktopPane);
        assertFalse(desktop.containsWindow(ActionTestHelper.DETAIL_WINDOW_TITLE));
    }


    private void iniGui(Class<? extends JInternalFrame> detailWindowClass) {
        helper.setDetailWindowClass(detailWindowClass);
        helper.setUp();

        Result result = new Result();
        result.addPrimaryKey("pimsCode");
        Row row = new Row();
        row.addField("pimsCode", "666");
        row.addField("isin", "isinVal");
        row.addField("sicovam", "sicovamVal");
        result.addRow(row);

        helper.setTableResult(result);

        Table table = new Table(helper.requestTable);
        table.selectRow(0);
    }


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        PreferenceFactory.loadMadIcons();
    }


    protected abstract void checkDetailDataSource(final DetailDataSource dataSource);


    protected abstract String getActionName();
}
