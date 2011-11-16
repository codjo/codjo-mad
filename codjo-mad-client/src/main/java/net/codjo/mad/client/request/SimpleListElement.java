package net.codjo.mad.client.request;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/**
 */
public class SimpleListElement {
    private List<String> value;


    public SimpleListElement() {
    }


    public SimpleListElement(String... values) {
        value = new ArrayList<String>();
        value.addAll(Arrays.asList(values));
    }


    public void setValue(List<String> value) {
        this.value = value;
    }


    public List<String> getValue() {
        return value;
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

        SimpleListElement simpleListElement = (SimpleListElement)object;
        return (value == null ? simpleListElement.value == null : value.equals(simpleListElement.value));
    }


    @Override
    public int hashCode() {
        return (value != null ? value.hashCode() : 0);
    }
*/
}
