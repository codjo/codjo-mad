package net.codjo.mad.client.request;
/**
 * Description of the Class
 *
 * @author $Author: gaudefr $
 * @version $Revision: 1.3 $
 */
public class Page {
    private String num = "1";
    private String rows = "30";

    public Page() {}

    public void setNum(String num) {
        this.num = num;
    }


    public void setRows(String rows) {
        this.rows = rows;
    }


    public String getNum() {
        return num;
    }


    public String getRows() {
        return rows;
    }

/*

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        Page page = (Page)object;
        boolean comparaison = (num == null ? page.num == null : num.equals(page.num));
        comparaison = comparaison && (rows == null ? page.rows == null : rows.equals(page.rows));
        return comparaison;
    }


    @Override
    public int hashCode() {
        int result;
        result = (num != null ? num.hashCode() : 0);
        result = 31 * result + (rows != null ? rows.hashCode() : 0);
        return result;
    }
*/
}
