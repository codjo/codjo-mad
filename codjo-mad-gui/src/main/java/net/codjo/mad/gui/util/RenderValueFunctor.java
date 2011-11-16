package net.codjo.mad.gui.util;
import net.codjo.mad.gui.request.RequestTable;

public interface RenderValueFunctor {
    public Object getRenderedValue(RequestTable jTable, int row, int col);
}
