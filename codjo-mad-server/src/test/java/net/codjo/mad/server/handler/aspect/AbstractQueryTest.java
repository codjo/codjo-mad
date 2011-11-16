/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.mad.server.handler.aspect;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;
/**
 * Classe de test de {@link AbstractQuery}.
 */
public class AbstractQueryTest extends TestCase {
    private static final String BEAN_NAME = "JPop";
    private static final String BEAN_CITY = "Kioto";
    private static final Date BEAN_BIRTHDATE = java.sql.Date.valueOf("1975-02-07");
    private static final int BEAN_AGE = 24;
    private AbstractQuery query;


    @Override
    protected void setUp() throws Exception {
        query = new MockAbstractQuery();
    }


    public void test_toBean_string() throws Exception {
        StringBean bean = new StringBean();
        query.toBean(bean);

        assertEquals(BEAN_NAME, bean.getName());
        assertEquals(BEAN_CITY, bean.getCity());
    }


    public void test_toBean_date() throws Exception {
        MisterBean bean = new MisterBean();
        query.toBean(bean);

        assertEquals(BEAN_BIRTHDATE, bean.getBirthdate());
        assertEquals(BEAN_AGE, bean.getAge());
    }


    public void test_toBean_superBean() throws Exception {
        SuperBean bean = new SuperBean();
        final String superMe = "previousValue";
        bean.setSuperMe(superMe);
        query.toBean(bean);

        assertEquals(superMe, bean.getSuperMe());
    }


    private static class MockAbstractQuery extends AbstractQuery {
        public String getId() {
            return null;
        }


        public Map rowToMap() {
            Map<String, String> result = new HashMap<String, String>();
            result.put("name", BEAN_NAME);
            result.put("city", BEAN_CITY);
            result.put("birthdate", BEAN_BIRTHDATE.toString());
            result.put("age", Integer.toString(BEAN_AGE));
            return result;
        }
    }

    public static class StringBean {
        private String name;
        private String city;


        public String getName() {
            return name;
        }


        public void setName(String name) {
            this.name = name;
        }


        public String getCity() {
            return city;
        }


        public void setCity(String city) {
            this.city = city;
        }
    }

    public static class MisterBean extends StringBean {
        private Date birthdate;
        private int age;


        public int getAge() {
            return age;
        }


        public void setAge(int age) {
            this.age = age;
        }


        public Date getBirthdate() {
            return birthdate;
        }


        public void setBirthdate(Date birthdate) {
            this.birthdate = birthdate;
        }
    }

    public static class SuperBean extends MisterBean {
        private String superMe;


        public String getSuperMe() {
            return superMe;
        }


        public void setSuperMe(String superMe) {
            this.superMe = superMe;
        }
    }
}
