package net.codjo.mad.client.request;
import java.util.Arrays;
/**
 * Description of the Class
 *
 * @author $Author: gaudefr $
 * @version $Revision: 1.4 $
 */
public class SelectRequest extends AbstractRequest {
    private SimpleListElement attributes;
    private Page page;
    private FieldsList selector;


    public SelectRequest() {
    }


    public SelectRequest(String handlerId, FieldsList selector) {
        setId(handlerId);
        setSelector(selector);
    }


    public void setAttributes(String[] array) {
        this.attributes = new SimpleListElement();
        attributes.setValue(Arrays.asList(array));
    }


    public void setAttributes(SimpleListElement attributes) {
        this.attributes = attributes;
    }


    public void setPage(Page page) {
        this.page = page;
    }


    public void setPage(int pageNumber, int pageSize) {
        this.page = new Page();
        page.setNum(Integer.toString(pageNumber));
        page.setRows(Integer.toString(pageSize));
    }


    public void setSelector(FieldsList selector) {
        this.selector = selector;
    }


    public SimpleListElement getAttributes() {
        return attributes;
    }


    public Page getPage() {
        return page;
    }


    public FieldsList getSelector() {
        return selector;
    }


    public void addSelector(String pkName, String value) {
        if (getSelector() == null) {
            setSelector(new FieldsList());
        }
        getSelector().addField(pkName, value);
    }


    public String toXml() {
        return toXml("selectMapping.xml");
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

        SelectRequest request = (SelectRequest)object;
        boolean comparaison = super.equals(request);
        comparaison = comparaison && (attributes == null ?
                               request.attributes == null :
                               attributes.equals(request.attributes));
        comparaison = comparaison && (page == null ? request.page == null : page.equals(request.page));
        comparaison = comparaison && (selector == null ?
                                      request.selector == null :
                                      selector.equals(request.selector));
        return comparaison;
    }


    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (attributes != null ? attributes.hashCode() : 0);
        result = 31 * result + (page != null ? page.hashCode() : 0);
        result = 31 * result + (selector != null ? selector.hashCode() : 0);
        return result;
    }
*/
}
