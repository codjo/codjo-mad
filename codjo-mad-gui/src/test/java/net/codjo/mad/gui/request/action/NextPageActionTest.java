package net.codjo.mad.gui.request.action;
import net.codjo.mad.gui.framework.DefaultGuiContext;
import net.codjo.mad.gui.request.Column;
import net.codjo.mad.gui.request.Preference;
import net.codjo.mad.gui.request.RequestTable;
import net.codjo.security.common.api.UserMock;
import net.codjo.test.common.LogString;
import java.util.Arrays;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

public class NextPageActionTest {
    private NextPageAction action;
    private LogString log = new LogString();
    private ListDataSourceMock dataSource = new ListDataSourceMock(log);
    private RequestTable table = new RequestTable(dataSource);


    @Before
    public void setUp() throws Exception {
        DefaultGuiContext guiContext = new DefaultGuiContext();
        guiContext.setUser(new UserMock().mockIsAllowedTo(true));
        action = new NextPageAction(guiContext, table);

        dataSource.setHasNextPage(true);

        Preference preference = new Preference();
        preference.setColumns(Arrays.asList(new Column("col1", "COL1")));
        table.setPreference(preference);
        table.load();
    }


    @Test
    public void test_actionPerformed() throws Exception {
        assertTrue(action.isEnabled());

        action.actionPerformed(null);

        log.assertContent("loadNextPage()");
    }


    @Test
    public void test_enabled() throws Exception {
        assertTrue(action.isEnabled());

        dataSource.setHasNextPage(false);
        table.load();

        assertFalse(action.isEnabled());
    }
}
