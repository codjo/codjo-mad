package net.codjo.mad.server.handler.sql;
import net.codjo.mad.server.handler.AbstractGetter;

public class Getter extends AbstractGetter {

    public Getter(int idx) {
        super(idx);
    }


    public Getter(String name) {
        super(name);
    }


    public Getter(int idx, int sqlType) {
        super(idx, sqlType);
    }


    public Getter(String name, int sqlType) {
        super(name, sqlType);
    }
}
