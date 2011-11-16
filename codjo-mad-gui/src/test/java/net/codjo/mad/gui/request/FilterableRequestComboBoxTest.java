package net.codjo.mad.gui.request;
import net.codjo.mad.client.request.Result;
import net.codjo.mad.client.request.Row;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.uispec4j.Key;
import org.uispec4j.ListBox;
import org.uispec4j.Trigger;
import org.uispec4j.UISpecTestCase;
import org.uispec4j.Window;
import org.uispec4j.interception.WindowInterceptor;
import org.uispec4j.utils.KeyUtils;

public class FilterableRequestComboBoxTest extends UISpecTestCase {

    public void test_filterOnSingleKeyPressed() throws Exception {
        FilterableRequestComboBox comboBox = createComboBox(new String[][]{{"a", "1"},
                                                                           {"b", "2"},
                                                                           {"ab", "3"}});

        Window popup = pressKey(comboBox, Key.A);

        assertTrue(popup.getListBox().contentEquals(new String[]{"a", "ab"}));
    }


    public void test_filterOnMultipleKeyPressed() throws Exception {
        FilterableRequestComboBox comboBox = createComboBox(new String[][]{{"xyz", "1"},
                                                                           {"x12", "2"},
                                                                           {"x32", "3"}});

        pressKey(comboBox, Key.X);
        Window popup = pressKey(comboBox, Key.Y);

        assertTrue(popup.getListBox().contentEquals(new String[]{"xyz"}));
    }


    public void test_filterRemovedAfterSelection() throws Exception {
        FilterableRequestComboBox comboBox = createComboBox(new String[][]{{"a", "1"},
                                                                           {"b", "2"},
                                                                           {"c", "3"}});

        Window popup = pressKey(comboBox, Key.A);

        ListBox listBox = popup.getListBox();
        assertTrue(listBox.contentEquals(new String[]{"a"}));

        KeyUtils.pressKey(comboBox, Key.ENTER);

        assertTrue(listBox.contentEquals(new String[]{"a", "b", "c"}));
    }


    private FilterableRequestComboBox createComboBox(String[][] rows) {
        FilterableRequestComboBox comboBox = new FilterableRequestComboBox();

        String[] columns = new String[]{"LABEL", "CODE"};
        Result loadResult = buildResult(columns, rows);

        comboBox.getDataSource().setLoadResult(loadResult);
        comboBox.setModelFieldName("CODE");
        comboBox.setRendererFieldName("LABEL");

        final JFrame frame = new JFrame();
        frame.add(comboBox);

        WindowInterceptor.run(new Trigger() {
            public void run() throws Exception {
                frame.setVisible(true);
            }
        });

        return comboBox;
    }


    private static Result buildResult(String[] columnNames, String[][] rows) {
        Result loadResult = new Result();

        for (String[] row : rows) {
            Map<String, String> fields = new HashMap<String, String>();

            for (int columnIndex = 0; columnIndex < columnNames.length; columnIndex++) {
                String columnName = columnNames[columnIndex];
                fields.put(columnName, row[columnIndex]);
            }

            loadResult.addRow(new Row(fields));
        }

        return loadResult;
    }


    private Window pressKey(final FilterableRequestComboBox comboBox, final Key key) {
        return WindowInterceptor.run(new Trigger() {
            public void run() throws Exception {
                WindowInterceptor.run(new Trigger() {
                    public void run() throws Exception {
                        KeyUtils.pressKey(comboBox, key);
                    }
                });
            }
        });
    }


    public static void main(String[] args) {
        final JFrame frame = new JFrame("Test FilterableRequestComboBoxTest");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JPanel mainPanel = new JPanel();
        RequestComboBox rc = new FilterableRequestComboBox();
        rc.setSortEnabled(true);
        rc.setModelFieldName("CODE");
        rc.setRendererFieldName("LABEL");
        rc.setContainsNullValue(false);
        Result loadResult = buildResult(new String[]{"LABEL", "CODE"},
                                        new String[][]{{"d1", "Vd1"},
                                                       {"a", "Va"},
                                                       {"d2", "Vd2"},
                                                       {"z", "Vz"}});

        rc.getDataSource().setLoadResult(loadResult);

        rc.setPreferredSize(new Dimension(100, 25));

        mainPanel.add(rc);
        frame.setContentPane(mainPanel);
        frame.pack();
        frame.setVisible(true);
    }
}
