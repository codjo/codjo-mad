package net.codjo.mad.gui.request;

public class Position {
    public enum Type {
        LEFT,
        RIGHT,
        LAST;
    }
    private final Type type;
    private final String actionName;


    public Position(Type type) {
        this(type, null);
    }


    private Position(Type type, String actionName) {
        this.type = type;
        this.actionName = actionName;
    }


    public static Position left(String actionName) {
        return new Position(Type.LEFT, actionName);
    }


    public static Position right(String actionName) {
        return new Position(Type.RIGHT, actionName);
    }


    public static Position last() {
        return new Position(Type.LAST);
    }


    public Type getType() {
        return type;
    }


    public String getActionName() {
        return actionName;
    }
}
