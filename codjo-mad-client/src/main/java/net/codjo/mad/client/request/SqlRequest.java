package net.codjo.mad.client.request;
import java.util.Arrays;
/**
 */
public class SqlRequest extends AbstractRequest {
    private SimpleListElement attributes;
    private Page page;
    private FieldsList arguments;


    public SqlRequest() {
    }


    public SqlRequest(String handlerId) {
        this(handlerId, null, null);
    }


    public SqlRequest(String handlerId, FieldsList arguments) {
        this(handlerId, arguments, null);
    }


    public SqlRequest(String handlerId, FieldsList arguments, SimpleListElement attributes) {
        setId(handlerId);
        setArguments(arguments);
        setAttributes(attributes);
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


    public void setArguments(FieldsList arguments) {
        this.arguments = arguments;
    }


    public SimpleListElement getAttributes() {
        return attributes;
    }


    public Page getPage() {
        return page;
    }


    public FieldsList getArguments() {
        return arguments;
    }


    public void addArgument(String pkName, String value) {
        if (getArguments() == null) {
            setArguments(new FieldsList());
        }
        getArguments().addField(pkName, value);
    }


    public String toXml() {
        return toXml("sqlMapping.xml");
    }
}
