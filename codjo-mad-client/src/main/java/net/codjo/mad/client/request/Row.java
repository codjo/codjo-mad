package net.codjo.mad.client.request;
import java.util.Map;
/**
 * Classe decrivant une ligne.
 *
 * @author $Author: gaudefr $
 * @version $Revision: 1.4 $
 */
public class Row extends FieldsList {
    public Row() {}


    public Row(Row row) {
        super(row);
    }


    public Row(Map fields) {
        super(fields);
    }
}
