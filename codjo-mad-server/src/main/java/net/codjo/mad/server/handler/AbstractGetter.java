package net.codjo.mad.server.handler;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;

public abstract class AbstractGetter {

    protected int idx;
    private int sqlType = Types.OTHER;
    protected String name;


    protected AbstractGetter(int idx) {
        this.idx = idx;
    }


    protected AbstractGetter(String name) {
        this.name = name;
    }


    protected AbstractGetter(int idx, int sqlType) {
        this.idx = idx;
        this.sqlType = sqlType;
    }


    protected AbstractGetter(String name, int sqlType) {
        this.name = name;
        this.sqlType = sqlType;
    }


    public String get(ResultSet resultSet) throws SQLException {
        Object value;
        switch (sqlType) {
            case java.sql.Types.DATE:
                value = getDate(resultSet);
                break;
            case java.sql.Types.TIMESTAMP:
                value = getTimestamp(resultSet);
                break;
            case Types.VARCHAR:
                value = getString(resultSet);
                break;
            default:
                value = getObject(resultSet);
        }
        return XMLUtils.convertToStringValue(value);
    }


    private String getString(ResultSet resultSet) throws SQLException {
        if (name == null) {
            return resultSet.getString(idx);
        }
        else {
            return resultSet.getString(name);
        }
    }


    private Object getObject(ResultSet resultSet) throws SQLException {
        if (name == null) {
            return resultSet.getObject(idx);
        }
        else {
            return resultSet.getObject(name);
        }
    }


    private Date getDate(ResultSet resultSet) throws SQLException {
        if (name == null) {
            return resultSet.getDate(idx);
        }
        else {
            return resultSet.getDate(name);
        }
    }


    private Timestamp getTimestamp(ResultSet resultSet) throws SQLException {
        if (name == null) {
            return resultSet.getTimestamp(idx);
        }
        else {
            return resultSet.getTimestamp(name);
        }
    }
}
