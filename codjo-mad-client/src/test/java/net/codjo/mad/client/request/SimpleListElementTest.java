package net.codjo.mad.client.request;

import static net.codjo.test.common.matcher.JUnitMatchers.*;
import org.junit.Test;

public class SimpleListElementTest {
    @Test
    public void test_mockTest() throws Exception {
    }

/*
    @Test
    public void test_equals() throws Exception {
        SimpleListElement listElement = new SimpleListElement("val1", "val2", "val3");
        //noinspection ObjectEqualsNull
        assertThat(listElement.equals(null), equalTo(false));
        assertThat(listElement.equals(new SimpleListElement()), equalTo(false));
        assertThat(listElement.equals(new SimpleListElement("val1", "val2")), equalTo(false));
        assertThat(listElement.equals(new SimpleListElement("val1", "val2", "val4")), equalTo(false));
        assertThat(listElement.equals(new SimpleListElement("val1", "val2", "val3")), equalTo(true));
    }


    @Test
    public void test_hashCode() throws Exception {
        SimpleListElement listElement = new SimpleListElement("val1", "val2");
        assertThat(listElement.hashCode(), equalTo(new SimpleListElement("val1", "val2").hashCode()));
        listElement = new SimpleListElement();
        assertThat(listElement.hashCode(), equalTo(0));
    }
*/
}
