package queries;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class QueryParseTest {

    @Test
    void testInvalidCondition() {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));

        String condition = "invalid:query:ok";

        ParseQuery query = new ParseQuery(new String[]{condition});
        assertEquals(out.toString(), "Condition: " + condition + " not valid" + System.lineSeparator());
    }

    @Test
    void testValidConditionSingle() {
        String condition = "title:val";
        ParseQuery query = new ParseQuery(new String[]{condition});
        assertEquals(1, query.getQualifierAndValue().size());
    }

    @Test
    void testValidConditionMultiple() {
        String condition = "title:val";
        String condition2 = "desc:val";
        String condition3 = "status:val";

        ParseQuery query = new ParseQuery(new String[]{condition, condition2, condition3});
        assertEquals(3, query.getQualifierAndValue().size());
    }


    @Test
    void testMixOfValidCondition() {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));

        String condition = "title:val";
        String condition2 = "desc:val";
        String condition3 = "invalid:query:ok";

        ParseQuery query = new ParseQuery(new String[]{condition, condition2, condition3});
        assertEquals(2, query.getQualifierAndValue().size());
        assertEquals(out.toString(), "Condition: " + condition3 + " not valid" + System.lineSeparator());
    }

}
