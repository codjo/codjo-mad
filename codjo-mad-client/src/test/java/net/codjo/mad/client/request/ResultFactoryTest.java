package net.codjo.mad.client.request;
import junit.framework.TestCase;
/**
 */
public class ResultFactoryTest extends TestCase {

    public void test_buildResultManager() throws Exception {
        String results =
              "<?xml version=\"1.0\"?>"
              + "<results>"
              + "     <result request_id=\"uneLigne\">"
              + "        <primarykey>"
              + "           <field name=\"pimsCode\"/>"
              + "        </primarykey>"
              + "        <row>"
              + "           <field name=\"pimsCode\">666</field>"
              + "           <field name=\"sicovamCode\">654</field>"
              + "        </row>"
              + "     </result>"
              + "  "
              + "    <result request_id=\"DeuxLignes\">"
              + "         <primarykey>"
              + "            <field name=\"pimsCode\"/>"
              + "         </primarykey>"
              + "         <row>"
              + "            <field name=\"pimsCode\">555</field>"
              + "            <field name=\"sicovamCode\">666</field>"
              + "         </row>"
              + "         <row>"
              + "            <field name=\"pimsCode\">777</field>"
              + "            <field name=\"sicovamCode\">888</field>"
              + "         </row>"
              + "    </result>"
              + "</results>";

        ResultManager resultManager = ResultFactory.buildResultManager(results);

        assertTrue(!resultManager.hasError());
        assertNull(resultManager.getErrorResult());

        assertEquals("Deux résultats reçus", 2, resultManager.getResultsCount());

        // Verification result
        Result oneRow = resultManager.getResult("uneLigne");
        checkResult(oneRow, 1, "666", "654", null, null);

        Result twoRows = resultManager.getResult("DeuxLignes");
        checkResult(twoRows, 2, "555", "666", "777", "888");
    }


    public void test_buildResultManager_errorResult()
          throws Exception {
        String errorResult =
              "<?xml version=\"1.0\"?>" + "<results>" + "  <error request_id = \"2\">"
              + "    <label>une erreur</label>"
              + "    <type>class java.lang.RuntimeException</type>" + "  </error>"
              + "</results>";

        ResultManager resultManager = ResultFactory.buildResultManager(errorResult);

        assertNull(resultManager.getResults());

        assertTrue(resultManager.hasError());
        ErrorResult result = resultManager.getErrorResult();
        assertNotNull(result);
        assertEquals("une erreur", result.getLabel());
        assertEquals("class java.lang.RuntimeException", result.getType());
        assertEquals("2", result.getRequestId());
    }


    private void checkResult(Result rs, int rowCount, String pims1, String sico1,
                             String pims2, String sico2) {
        assertNotNull(rs);
        // Pk
        assertEquals("Une clef primaire", 1, rs.getPrimaryKeyCount());
        assertEquals("pimsCode", rs.getPrimaryKey(0));
        // Row
        assertEquals(rowCount, rs.getRowCount());
        assertEquals(pims1, rs.getValue(0, "pimsCode"));
        assertEquals(sico1, rs.getValue(0, "sicovamCode"));
        if (rowCount >= 2) {
            assertEquals(pims2, rs.getValue(1, "pimsCode"));
            assertEquals(sico2, rs.getValue(1, "sicovamCode"));
        }
    }
}
