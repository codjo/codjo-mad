package net.codjo.mad.gui.request.action;
import net.codjo.mad.client.request.Row;
import net.codjo.mad.gui.framework.DefaultGuiContext;
import net.codjo.mad.gui.request.DataSource;
import net.codjo.mad.gui.request.DetailDataSource;
import net.codjo.mad.gui.request.GuiLogic;
import net.codjo.mad.gui.request.ListDataSource;
import net.codjo.mad.gui.request.Preference;
import javax.swing.JInternalFrame;
import junit.framework.TestCase;
/**
 * Classe de test de {@link DetailWindowBuilder}.
 */
public class DetailWindowBuilderTest extends TestCase {
    private DetailWindowBuilder builder;


    @Override
    protected void setUp() throws Exception {
        builder = new DetailWindowBuilder();
    }


    public void test_buildFrame() throws Exception {
        assertDetailBuilt(MockDetailWindow.class);
    }


    public void test_buildFrame_withoutDataSource() throws Exception {
        DetailDataSource ds = new DetailDataSource(new DefaultGuiContext());
        Preference preference = new Preference();
        preference.setDetailWindowClass(MockDetailWindowWithoutDataSource.class);

        Object frame = builder.buildFrame(ds, preference);
        assertNotNull(frame);
        assertSame(MockDetailWindowWithoutDataSource.class, frame.getClass());
    }


    public void test_buildFrame_logic() throws Exception {
        assertDetailBuilt(MockDetailWindowLogic.class);
    }


    public void test_buildFrame_row_logic() throws Exception {
        final Row row = new Row();
        ListDataSource father =
              new ListDataSource() {
                  @Override
                  public Row getSelectedRow() {
                      return row;
                  }
              };
        MockFatherContainer fatherContainer = new MockFatherContainer();
        builder = new DetailWindowBuilder(fatherContainer);
        MockDetailWindow mockDetailWindow =
              assertDetailBuilt(MockDetailWindowLogic.class);
        assertSame(null, mockDetailWindow.row);

        fatherContainer.setFather(father);
        mockDetailWindow = assertDetailBuilt(MockDetailWindowLogic.class);
        assertSame(row, mockDetailWindow.row);
    }


    private MockDetailWindow assertDetailBuilt(Class detailWindowClass) throws Exception {
        DetailDataSource ds = new DetailDataSource(new DefaultGuiContext());

        Preference preference = new Preference();
        preference.setDetailWindowClass(detailWindowClass);

        Object frame = builder.buildFrame(ds, preference);
        assertNotNull(frame);
        assertSame(MockDetailWindow.class, frame.getClass());
        MockDetailWindow mockDetailWindow = (MockDetailWindow)frame;
        assertSame(ds, mockDetailWindow.ds);
        return mockDetailWindow;
    }


    public static class MockDetailWindow extends JInternalFrame {
        DetailDataSource ds;
        Row row;


        public MockDetailWindow(DetailDataSource ds) {
            this.ds = ds;
        }


        public MockDetailWindow(DetailDataSource ds, Row row) {
            this.ds = ds;
            this.row = row;
        }
    }
    public static class MockDetailWindowWithoutDataSource extends JInternalFrame {
        public MockDetailWindowWithoutDataSource() {
        }
    }
    public static class MockDetailWindowLogic implements GuiLogic<JInternalFrame> {
        DetailDataSource ds;
        Row row;


        public MockDetailWindowLogic(DetailDataSource ds) {
            this.ds = ds;
        }


        public MockDetailWindowLogic(DetailDataSource ds, Row row) {
            this.ds = ds;
            this.row = row;
        }


        public JInternalFrame getGui() {
            if (row != null) {
                return new MockDetailWindow(ds, row);
            }
            else {
                return new MockDetailWindow(ds);
            }
        }
    }
    private static class MockFatherContainer implements FatherContainer {
        private DataSource father;


        public void setFather(DataSource father) {
            this.father = father;
        }


        public DataSource getFatherDataSource() {
            return father;
        }
    }
}
