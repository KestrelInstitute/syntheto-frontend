package edu.kestrel.syntheto.ast;

import edu.kestrel.syntheto.sexpr.Parser;
import edu.kestrel.syntheto.sexpr.SExpression;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

public class ExpressionSumTestTest {

    public static final String expectedString =
            "(SYNTHETO::MAKE-EXPRESSION-SUM-TEST " +
                    ":TYPE (SYNTHETO::MAKE-IDENTIFIER :NAME \"Type\") " +
                    ":TARGET (SYNTHETO::MAKE-EXPRESSION-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"x\")) "+
                    ":ALTERNATIVE (SYNTHETO::MAKE-IDENTIFIER :NAME \"defined\"))";

    public static final ExpressionSumTest expectedASTNode = makeNode();

    private static ExpressionSumTest makeNode() {
        // Is x the "defined" alternative?  (I.e. the "defined" subprod of "deftagsum Type"

        Identifier type = Identifier.make("Type");
        Expression val = Variable.make(Identifier.make("x"));
        Identifier altName = Identifier.make("defined");
        return ExpressionSumTest.make(type, val, altName);
    }

    // TODO: it would be good to have alternatve predicates automatically generated.  For now just checking expressionp.
    public static final String checkExpectedType = "(syntheto::expressionp " + expectedString + ")";

    @Test
    void toSExpression() {

        ExpressionSumTest expr = expectedASTNode;
        SExpression sExpression = expr.toSExpression();
        assertEquals(expectedString,
                sExpression.toString());

        ExpressionSumTest rebuilt = (ExpressionSumTest) ASTBuilder.fromSExpression(sExpression);
        assertEquals(expr, rebuilt);

        Reader r = new StringReader(sExpression.toString());
        Parser p = new Parser(r);
        SExpression s = p.parseTop();
        assertEquals(sExpression, s);
    }
}