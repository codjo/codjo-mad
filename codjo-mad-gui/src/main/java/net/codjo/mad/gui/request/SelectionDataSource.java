package net.codjo.mad.gui.request;
import net.codjo.mad.client.request.Row;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SelectionDataSource {
    private List<Row> rows;


    SelectionDataSource(Row row) {
        this.rows = new ArrayList<Row>();
        if (row != null) {
            rows.add(row);
        }
    }


    SelectionDataSource(Row[] rows) {
        this.rows = Arrays.asList(rows);
    }


    public Row[] getRows() {
        return rows.toArray(new Row[rows.size()]);
    }


    public Row getRow() {
        if (isEmpty()) {
            return null;
        }
        return rows.get(0);
    }


    public boolean isEmpty() {
        return rows.isEmpty();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SelectionDataSource that = (SelectionDataSource)o;

        return rows.equals(that.rows);
    }


    @Override
    public int hashCode() {
        return rows.hashCode();
    }
}
