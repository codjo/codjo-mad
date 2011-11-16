/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.mad.server.handler.aspect;

import net.codjo.aspect.Aspect;
import net.codjo.aspect.AspectContext;
import net.codjo.aspect.AspectException;
import net.codjo.aspect.JoinPoint;
import net.codjo.aspect.util.TransactionalPoint;
import net.codjo.tokio.TokioFixture;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import junit.framework.TestCase;
/**
 *
 */
public abstract class AspectTestCase extends TestCase {
    private Aspect aspect;
    protected TokioFixture tokioFixture;
    private String inputTableName;


    protected AspectTestCase() {
        this("#INPUT_QUERY");
    }


    protected AspectTestCase(String inputTableName) {
        this.inputTableName = inputTableName;
    }


    protected abstract Aspect createAspect();


    protected abstract String getSqlQueryInputTableStructure();


    protected abstract String[] getSqlQueryFields();


    protected Aspect getAspect() {
        return aspect;
    }


    @Override
    protected final void setUp() throws Exception {
        aspect = createAspect();
        tokioFixture = new TokioFixture(this.getClass());
        tokioFixture.doSetUp();
        createSqlRequestInputTable();
        doSetUp();
    }


    protected void doSetUp() throws Exception {
    }


    @Override
    protected final void tearDown() throws Exception {
        tokioFixture.doTearDown();
        doTearDown();
    }


    protected void doTearDown() throws Exception {
    }


    protected TokioFixture getTokioFixture() {
        return tokioFixture;
    }


    protected void doSetUpAndCheckOutputs(String handlerId, String scenarioName,
                                          String point) throws Exception {
        tokioFixture.insertInputInDb(scenarioName, false);

        AspectContext context = initContext(point, handlerId);
        aspect.setUp(context, newJoinPoint(point, handlerId));

        tokioFixture.assertAllOutputs(scenarioName);
    }


    protected void doTest(String handlerId, String scenarioName, String point)
          throws Exception {
        doTest(handlerId, scenarioName, point, false);
    }


    protected void doTest(String handlerId, String scenarioName, String point,
                          boolean deleteBeforeInsert) throws Exception {
        tokioFixture.insertInputInDb(scenarioName, deleteBeforeInsert);

        AspectContext context = initContext(point, handlerId);
        aspect.setUp(context, newJoinPoint(point, handlerId));
        //mode tx
        tokioFixture.getConnection().setAutoCommit(false);
        //execution aspect
        aspect.run(context);
        tokioFixture.getConnection().commit();

        // nettoyage
        aspect.cleanUp(context);
        tokioFixture.getConnection().setAutoCommit(true);
    }


    protected void doTestAndCheckOutputs(String handlerId, String scenarioName,
                                         String point) throws Exception {
        doTest(handlerId, scenarioName, point);
        tokioFixture.assertAllOutputs(scenarioName);
    }


    protected void doTestAndCheckOutputs(String handlerId, String scenarioName,
                                         String point, boolean deleteBeforeInsert)
          throws Exception {
        doTest(handlerId, scenarioName, point, deleteBeforeInsert);
        tokioFixture.assertAllOutputs(scenarioName);
    }


    protected void doTestAndCheckError(String handlerId, final String scenarioName,
                                       String point, final String errorMessage) throws Exception {
        doTestAndCheckError(handlerId, scenarioName, point, errorMessage, false);
    }


    protected void doTestAndCheckError(String handlerId, final String scenarioName,
                                       String point, final String errorMessage, boolean deleteBeforeInsert)
          throws Exception {
        try {
            doTest(handlerId, scenarioName, point, deleteBeforeInsert);
            fail("AspectException expected");
        }
        catch (AspectException e) {
            assertEquals(errorMessage, e.getMessage());
        }
    }


    protected AspectContext initContext(String point, String argument)
          throws Exception {
        AspectContext context = newContext(point);
        if ("handler.execute".equals(point)) {
            context.put("DISABLE_CASCADE", "no!");
            context.put(Keys.USER_NAME, "Aspect_generated");

            //utilise la table inputTableName pour simuler l'appel handler
            context.put(QueryManager.class.getName(), createMockManager());
        }
        else {
            context.put("connection", getTokioFixture().getConnection());

            context.put("control.table", inputTableName);
            context.put(TransactionalPoint.CONNECTION, getTokioFixture().getConnection());
            context.put(TransactionalPoint.ARGUMENT, argument);
            context.put("user", "Aspect_generated");
            context.put("requestId", "69");
        }

        //utilise la table #INPUT_QUERY pour simuler l'appel handler
        context.put(QueryManager.class.getName(), createMockManager());
        return context;
    }


    protected JoinPoint newJoinPoint(String point, String argument) {
        JoinPoint joinPoint = new JoinPoint();
        joinPoint.setPoint(point);
        joinPoint.setArgument(argument);
        return joinPoint;
    }


    protected QueryManagerMock createMockManager()
          throws Exception {
        Statement st = tokioFixture.getConnection().createStatement();
        ResultSet rs = st.executeQuery("select * from " + inputTableName);
        String[] sqlQueryFields = getSqlQueryFields();
        String[][] data = convertSqlResultToArray(rs, sqlQueryFields);
        rs.close();
        st.close();
        return new LocalQueryManagerMock(sqlQueryFields, data);
    }


    private String[][] convertSqlResultToArray(ResultSet rs, String[] sqlQueryFields)
          throws SQLException {
        List<String[]> result = new ArrayList<String[]>();
        final int nbColumns = sqlQueryFields.length;

        while (rs.next()) {
            String[] cols = new String[nbColumns];
            for (int i = 0; i < nbColumns; i++) {
                cols[i] = rs.getString(i + 1);
            }
            result.add(cols);
        }
        String[][] list = new String[result.size()][nbColumns];
        return result.toArray(list);
    }


    private AspectContext newContext(String argument) {
        AspectContext context = new AspectContext();
        context.put("connection", tokioFixture.getConnection());
        context.put("argument", argument);
        return context;
    }


    private void createSqlRequestInputTable() throws SQLException {
        Statement st = tokioFixture.getConnection().createStatement();
        st.executeUpdate("create table " + inputTableName + " "
                         + getSqlQueryInputTableStructure());
        st.close();
    }


    private static class LocalQueryManagerMock extends QueryManagerMock {
        private LocalQueryManagerMock(String[] sqlQueryFields, String[][] data) {
            Query[] queries = new Query[data.length];
            for (int i = 0; i < queries.length; i++) {
                Map<String, String> fields = new HashMap<String, String>();
                for (int j = 0; j < sqlQueryFields.length; j++) {
                    fields.put(sqlQueryFields[j], data[i][j]);
                }
                queries[i] = new QueryMock(Integer.toString(i), fields);
            }
            mockGetQuery(queries);
        }
    }
}
