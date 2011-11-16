package net.codjo.mad.server.handler.select;
import net.codjo.mad.server.handler.AbstractGetter;

public abstract class Getter<T> extends AbstractGetter {

    protected Getter(int idx) {
        super(idx);
    }


    protected Getter(String name) {
        super(name);
    }


    protected Getter(int idx, int sqlType) {
        super(idx, sqlType);
    }


    protected Getter(String name, int sqlType) {
        super(name, sqlType);
    }


    public abstract String get(T bean) throws Exception;
}
