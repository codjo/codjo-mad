/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.mad.server.handler;
import static net.codjo.mad.server.handler.XMLUtils.convertFromStringValue;
import net.codjo.mad.server.handler.aspect.AbstractQuery;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Map;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
/**
 * Schema de nommage : xxxCommand, e.g. 'GenerateAllocationCommand'.
 */
public abstract class HandlerCommand extends AbstractHandler {
    private StringBuffer buildResponseHeader(Node node)
          throws SAXException {
        String requestId = XMLUtils.getAttribute(node, "request_id");
        return new StringBuffer("<result request_id='").append(requestId).append("'>");
    }


    /**
     * @param node
     *
     * @return
     *
     * @throws HandlerException
     * @deprecated Ne doit pas être surchargé
     */
    @Deprecated
    public String proceed(Node node) throws HandlerException {
        try {
            CommandResult commandResult = executeQuery(new CommandQuery(node));

            StringBuffer result = buildResponseHeader(node);
            result.append(commandResult.toXML());
            result.append("</result>");
            return result.toString();
        }
        catch (HandlerException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw new HandlerException(ex);
        }
    }


    public final String getId() {
        return AbstractHandlerMapBuilder.getIdFromHandlerName(getClass().getName());
    }


    public abstract CommandResult executeQuery(CommandQuery query)
          throws HandlerException, SQLException;


    public static CommandResult createResult(Object oneResult) {
        return new CommandResult(oneResult);
    }


    public static CommandResult createResult(String fieldName, Object oneResult) {
        return new CommandResult(fieldName, oneResult);
    }


    public static CommandResult createEmptyResult() {
        return new CommandResult("");
    }


    public static CommandQuery createCommandQuery(Map<String, String> arguments) {
        return new CommandQuery(arguments);
    }


    public static class CommandQuery {
        private net.codjo.mad.server.handler.aspect.CommandQuery implementation;


        CommandQuery(Node node) {
            implementation = new net.codjo.mad.server.handler.aspect.CommandQuery(node);
        }


        CommandQuery(Map<String, String> passedMap) {
            implementation =
                  new net.codjo.mad.server.handler.aspect.CommandQuery(passedMap);
        }


        public String getArgumentString(String fieldName) {
            return implementation.rowToMap().get(fieldName);
        }


        public BigDecimal getArgumentBigDecimal(String fieldName) {
            return convertFromStringValue(BigDecimal.class, getArgumentString(fieldName));
        }


        public Integer getArgumentInteger(String fieldName) {
            return convertFromStringValue(Integer.class, getArgumentString(fieldName));
        }


        public Double getArgumentDouble(String fieldName) {
            return convertFromStringValue(Double.class, getArgumentString(fieldName));
        }


        public Date getArgumentDate(String fieldName) {
            return convertFromStringValue(Date.class, getArgumentString(fieldName));
        }


        public Timestamp getArgumentTimestamp(String fieldName) {
            return convertFromStringValue(Timestamp.class, getArgumentString(fieldName));
        }


        public Boolean getArgumentBoolean(String fieldName) {
            return convertFromStringValue(Boolean.class, getArgumentString(fieldName));
        }


        public Map<String, String> getArguments() {
            return implementation.rowToMap();
        }


        public void toBean(Object javaBean) throws HandlerException {
            try {
                implementation.toBean(javaBean);
            }
            catch (Exception e) {
                throw new HandlerException(e);
            }
        }


        public void toBean(Object javaBean, final PropertyFilter propertyFilter) throws HandlerException {
            try {
                //noinspection InnerClassTooDeeplyNested
                implementation.toBean(javaBean, new AbstractQuery.PropertyFilter() {
                    public boolean acceptValue(String propertyName, String value) {
                        return propertyFilter.acceptValue(propertyName, value);
                    }
                });
            }
            catch (Exception e) {
                throw new HandlerException(e);
            }
        }


        public void checkRequiredArguments(String... fieldNames) throws HandlerException {
            Map<String, String> map = implementation.rowToMap();
            for (String fieldName : fieldNames) {
                if (!map.containsKey(fieldName)) {
                    throw new HandlerException("L'argument obligatoire '" + fieldName
                                               + "' est absent de la requête");
                }
            }
        }
    }

    public static interface PropertyFilter {
        boolean acceptValue(String propertyName, String value);
    }

    public static class CommandResult {
        private final Object result;
        private final String fieldName;


        CommandResult(Object result) {
            this("result", result);
        }


        public CommandResult(String fieldName, Object result) {
            this.result = result;
            this.fieldName = fieldName;
        }


        public String toXML() {

            StringBuffer buffer = new StringBuffer();
            if (result instanceof ResultTable) {
                return buffer.append(((ResultTable)result).toXML()).toString();
            }
            return buffer.append("<row><field name=\"").append(fieldName).append("\"><![CDATA[")
                  .append(result).append("]]></field></row>")
                  .toString();
        }


        @Override
        public boolean equals(Object passedObject) {
            if (this == passedObject) {
                return true;
            }
            if (passedObject == null || getClass() != passedObject.getClass()) {
                return false;
            }

            final CommandResult that = (CommandResult)passedObject;

            return (result != null ? result.equals(that.result) : that.result == null)
                   && (fieldName.equals(that.fieldName));
        }


        @Override
        public int hashCode() {
            return (result != null ? result.hashCode() : 0);
        }


        @Override
        public String toString() {
            if (result != null) {
                return result.toString();
            }
            else {
                return super.toString();
            }
        }
    }
}
