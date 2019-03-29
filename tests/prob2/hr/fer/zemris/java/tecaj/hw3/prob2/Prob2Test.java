package hr.fer.zemris.java.tecaj.hw3.prob2;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import hr.fer.zemris.java.custom.scripting.elems.*;
import hr.fer.zemris.java.custom.scripting.nodes.*;
import hr.fer.zemris.java.custom.scripting.parser.*;
import hr.fer.zemris.java.hw3.SmartScriptTester;


@SuppressWarnings("javadoc")
public class Prob2Test {

//    @Ignore
    @Test(expected=IndexOutOfBoundsException.class)
    public void testEmptyBody() {
        SmartScriptParser parser = new SmartScriptParser("");
        DocumentNode docNode = parser.getDocumentNode();
        // must throw
        docNode.getChild(0);
    }

//    @Ignore
    @Test
    public void testOnlyWhitespaceContent() {
        // When input is only of spaces, tabs, newlines, etc...
        SmartScriptParser parser = new SmartScriptParser("   \r\n\t        ");
        DocumentNode docNode = parser.getDocumentNode();
        // must return 1
        int num = docNode.numberOfChildren();
        assertEquals(1, num);
    }

//    @Ignore
    @Test(expected=IllegalArgumentException.class)
    public void testNullInput() {
        // must throw
        new SmartScriptParser(null);
    }

//    @Ignore
    @Test
    public void testBodyStartingWithTag() {
        // The document body must be allowed to start with a tag
        SmartScriptParser parser = new SmartScriptParser("{$ FOR i 1 10 1 $} {$END$}");
        DocumentNode docNode = parser.getDocumentNode();
        ForLoopNode forLoop = (ForLoopNode) docNode.getChild(0);

        Element[] correctData = {
                new ElementVariable("i"),
                new ElementConstantInteger(1),
                new ElementConstantInteger(10),
                new ElementConstantInteger(1)
        };

        checkForLoopNode(forLoop, correctData);
    }

//    @Ignore
    @Test
    public void testForWithThreeElements() {
        // FOR tag can have three or four elements
        SmartScriptParser parser = new SmartScriptParser("{$FOR i 1 10$} {$END$}");
        DocumentNode docNode = parser.getDocumentNode();
        ForLoopNode forLoop = (ForLoopNode) docNode.getChild(0);

        Element[] correctData = {
                new ElementVariable("i"),
                new ElementConstantInteger(1),
                new ElementConstantInteger(10),
                null
        };

        checkForLoopNode(forLoop, correctData);
    }

//    @Ignore
    @Test(expected=SmartScriptParserException.class)
    public void testEmptyTag() {
        // must throw
        new SmartScriptParser("{$$}");
    }

//    @Ignore
    @Test(expected=SmartScriptParserException.class)
    public void testEmptyForTag() {
        // must throw
        new SmartScriptParser("{$FOR$} {$END$}");
    }

//    @Ignore
    @Test
    public void testEmptyEchoTag() {
        // must NOT throw, the sequence is valid.
        new SmartScriptParser("{$=    $}");
    }

//    @Ignore
    @Test(expected=SmartScriptParserException.class)
    public void testInvalidEscape() {
        // must throw           This is \a letter
        new SmartScriptParser("This is \\a letter");
    }

//    @Ignore
    @Test(expected=SmartScriptParserException.class)
    public void testInvalidStringEscape() {
        // must throw           {$FOR a 10 "\abc" -10 $}
        new SmartScriptParser("{$FOR a 10 \"\\abc\" -10 $} {$END$}");
    }

//    @Ignore
    @Test(expected=SmartScriptParserException.class)
    public void testInvalidEscapeEnding() {
        // must throw           This is regular text.\
        new SmartScriptParser("This is regular text.\\");
    }

//    @Ignore
    @Test(expected=SmartScriptParserException.class)
    public void testInvalidStringEscapeEnding() {
        // must throw           {$FOR a 10 "abc\" -10 $}
        new SmartScriptParser("{$FOR a 10 \"abc\\\" -10 $} {$END$}");
    }

//    @Ignore
    @Test
    public void testValidEscape() {
        // must NOT throw an exception
        //                       This is \{$ a {completely} valid text\\.
        new SmartScriptParser("This is \\{$ a {completely} valid text\\\\.");
    }

//    @Ignore
    @Test
    public void testValidStringEscape() {
        // must NOT throw an exception
        //                             Whitespace characters:  \r\n\t
        new SmartScriptParser("{$= \"Whitespace characters:  \\r\\n\\t\" $}");
    }

//    @Ignore
    @Test(expected=SmartScriptParserException.class)
    public void testInvalidVariableName() {
        // must throw
        new SmartScriptParser("{$FOR _invalidArgument 1 10 1 $} {$END$}");
    }

//    @Ignore
    @Test(expected=SmartScriptParserException.class)
    public void testInvalidFunctionName() {
        // must throw
        new SmartScriptParser("{$= i i * @@invalidFunction $}");
    }

//    @Ignore
    @Test(expected=SmartScriptParserException.class)
    public void testInvalidNumber() {
        // must throw
        new SmartScriptParser("{$FOR i 1 10.0.0 1 $} {$END$}");
    }

//    @Ignore
    @Test(expected=SmartScriptParserException.class)
    public void testInvalidOperator() {
        // must throw
        new SmartScriptParser("{$= i i % @sin $}");
    }

//    @Ignore
    @Test
    public void testNestedString() {
        // must NOT throw an exception
        //                            [0] [1]    [2]    [3] [4] [5]       [6]
        //                           This is "a string" and this is "a \"nested\" string"
        new SmartScriptParser("{$= This is \"a string\" and this is \"a \\\"nested\\\" string\" $}");
    }

//    @Ignore
    @Test
    public void testTagAfterTag() {
        // One tag right after another must be parsed as a tag
        SmartScriptParser parser = new SmartScriptParser("{$= a_variable * $}{$= variable2 @func $}");

        DocumentNode docNode = parser.getDocumentNode();

        EchoNode echoNode1 = (EchoNode) docNode.getChild(0);
        Element[] correctData1 = {
                new ElementVariable("a_variable"),
                new ElementOperator("*")
        };
        checkEchoNode(echoNode1, correctData1);

        EchoNode echoNode2 = (EchoNode) docNode.getChild(1);
        Element[] correctData2 = {
                new ElementVariable("variable2"),
                new ElementFunction("@func")
        };
        checkEchoNode(echoNode2, correctData2);
    }

//    @Ignore
    @Test
    public void testKeepStringWhitespaces() {
        // String whitespaces must be kept as they are (not replaced with a single space)
        //                                                       "A    string    constant"
        SmartScriptParser parser = new SmartScriptParser("{$= \"A    string    constant\" $}");

        DocumentNode docNode = parser.getDocumentNode();
        EchoNode echoNode = (EchoNode) docNode.getChild(0);

        Element[] correctData = {
                new ElementString("\"A    string    constant\"")
        };

        checkEchoNode(echoNode, correctData);
    }

//    @Ignore
    @Test
    public void testLargeNumberConstant() {
        // must be parsed as a double
        new SmartScriptParser("{$FOR i 1 12345678912123123432123 1 $} {$END$}");
    }

//    @Ignore
    @Test
    public void testNegativeNumberConstant() {
        // all must be parsed as a constant number
        new SmartScriptParser("{$FOR i -1 -3.13 -0.01 $} {$END$}");
    }

//    @Ignore
//    @Test
    public void testNoWhitespacesEcho() {
        // the user does not have to input whitespaces for elements
        SmartScriptParser parser = new SmartScriptParser("{$=i*i+212$}");

        DocumentNode docNode = parser.getDocumentNode();
        EchoNode echoNode = (EchoNode) docNode.getChild(0);

        Element[] correctData = {
                new ElementVariable("i"),
                new ElementOperator("*"),
                new ElementVariable("i"),
                new ElementOperator("+"),
                new ElementConstantInteger(212)
        };

        checkEchoNode(echoNode, correctData);
    }

//    @Ignore
    @Test
    public void testCombinedInput() {
        String text = "This is sample text."
                    + "{$ FOR i 1 10 $}\n"
                    + "  This is {$= i $}-th time this message is generated.\n"
                    + "{$END$}\n"
                    + "{$FOR i 0 10 2 $}\n"
                    + "  sin({$=i$}^2) = {$= i i * @sin \"0.000\" @decfmt $}\n"
                    + "{$END$}\n"
                    + "\n"
                    + "{$= \"text/plain\" @setMimeType $}\n"
                    + "Računam sumu brojeva:\n"
                    + "{$=     \"a=\" \"a\" 0 @paramGet \", b=\" \"b\" 0 @paramGet \",  rezultat=\" \"a\" 0\n"
                    + "@paramGet \"b\" 0 @paramGet + $}\n"
                    + "\n\n"
                    + "{$= \"text/plain\" @setMimeType $}\n"
                    + "Ovaj dokument pozvan je sljedeći broj puta:\n"
                    + "{$= \"brojPoziva\" \"1\" @pparamGet @dup 1 + \"brojPoziva\" @pparamSet $}\n"
                    + "\n"
                    + "{$= \"text/plain\" @setMimeType $}Prvih 10 fibonaccijevih brojeva je:\n"
                    + "{$= \"0\" \"a\" @tparamSet\n"
                    + "   \"1\" \"b\" @tparamSet\n"
                    + "   \"0\r\n1\r\n\" $}{$FOR i 3 10 1$}{$=\n"
                    + "\"b\" \"0\" @tparamGet @dup\n"
                    + "\"a\" \"0\" @tparamGet +\n"
                    + "\"b\" @tparamSet \"a\" @tparamSet\n"
                    + "\"b\" \"0\" @tparamGet \"\\r\\n\"\n"
                    + "$}{$END$}";

        SmartScriptParser parser = new SmartScriptParser(text);
        DocumentNode document = parser.getDocumentNode();
        String originalDocumentBody = SmartScriptTester.createOriginalDocumentBody(document);

        SmartScriptParser parser2 = new SmartScriptParser(originalDocumentBody);
        DocumentNode document2 = parser2.getDocumentNode();
        String originalDocumentBody2 = SmartScriptTester.createOriginalDocumentBody(document2);

        assertEquals(originalDocumentBody, originalDocumentBody2);
    }

    /*
     * A helper method for checking if the FOR loop node contains the expected data.
     */
    private void checkForLoopNode(ForLoopNode forLoop, Element[] correctData) {
        String variable = correctData[0].asText();
        assertEquals("Checking variable:", variable, forLoop.getVariable().asText());

        String start = correctData[1].asText();
        assertEquals("Checking start:", start, forLoop.getStartExpression().asText());

        String end = correctData[2].asText();
        assertEquals("Checking end:", end, forLoop.getEndExpression().asText());

        if (correctData[3] != null) {
            String step = correctData[3].asText();
            assertEquals("Checking step:", step, forLoop.getStepExpression().asText());
        } else {
            assertEquals("Checking step:", null, forLoop.getStepExpression());
        }
    }

    /*
     * A helper method for checking if the ECHO node contains the expected data.
     */
    private void checkEchoNode(EchoNode echoNode, Element[] correctData) {
        Element[] echoElements = echoNode.getElements();

        for (int i = 0; i < correctData.length; i++) {
            String msg = "Checking token " + i + ":";

            String expected = correctData[i].asText();
            String actual = echoElements[i].asText();
            assertEquals(msg, expected, actual);
        }
    }

}
