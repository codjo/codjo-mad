package net.codjo.mad.gui.request;
import net.codjo.mad.client.request.Row;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.nullValue;
import static org.hamcrest.core.IsSame.sameInstance;
import org.junit.Assert;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertThat;
import org.junit.Test;

public class SelectionDataSourceTest {
    private static final SelectionDataSource EMPTY_SELECTION = new SelectionDataSource(new Row[]{});
    private static final SelectionDataSource NULL_SELECTION = new SelectionDataSource((Row)null);


    @Test
    public void getRowWithEmptySelection() throws Exception {
        assertThat(EMPTY_SELECTION.getRow(), is(nullValue()));

        assertThat(NULL_SELECTION.getRow(), is(nullValue()));
    }


    @Test
    public void getRowWithSingleSelection() throws Exception {
        Row row = new Row();
        SelectionDataSource selection = new SelectionDataSource(row);
        Row selectedRow = selection.getRow();
        assertThat(selectedRow, sameInstance(row));

        selection = new SelectionDataSource(new Row[]{row});
        selectedRow = selection.getRow();
        assertThat(selectedRow, sameInstance(row));
    }


    @Test
    public void getRowsWithEmptySelection() throws Exception {
        Row[] rows = EMPTY_SELECTION.getRows();
        assertThat(rows.length, equalTo(0));

        rows = NULL_SELECTION.getRows();
        assertThat(rows.length, equalTo(0));
    }


    @Test
    public void getRowsWithNotEmptySelection() throws Exception {
        Row row1 = new Row();
        Row row2 = new Row();
        Row[] selectedRows = new Row[]{row1, row2};

        SelectionDataSource selection = new SelectionDataSource(selectedRows);
        Row[] actualRows = selection.getRows();
        assertArrayEquals(selectedRows, actualRows);
    }


    @Test
    public void getRowWithNotEmptySelection() throws Exception {
        Row row1 = new Row();
        Row row2 = new Row();
        Row[] selectedRows = new Row[]{row1, row2};

        SelectionDataSource selection = new SelectionDataSource(selectedRows);
        Row actualRow = selection.getRow();
        assertThat(actualRow, sameInstance(selectedRows[0]));

        selection = new SelectionDataSource(row2);
        actualRow = selection.getRow();
        assertThat(actualRow, sameInstance(row2));
    }


    @Test
    public void isEmpty() throws Exception {
        assertTrue(EMPTY_SELECTION.isEmpty());

        assertTrue(NULL_SELECTION.isEmpty());
    }


    @Test
    public void equals() throws Exception {
        Row row1 = new Row();
        Row row2 = new Row();
        Row[] selectedRows = new Row[]{row1, row2};

        SelectionDataSource selection = new SelectionDataSource(selectedRows);
        Assert.assertTrue(selection.equals(new SelectionDataSource(selectedRows)));

        Row[] otherSelectedRows = new Row[]{row1, row2};
        Assert.assertTrue(selection.equals(new SelectionDataSource(otherSelectedRows)));
    }
}
