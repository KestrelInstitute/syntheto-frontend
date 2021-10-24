package edu.kestrel.syntheto.ast;

import edu.kestrel.syntheto.sexpr.Parser;
import edu.kestrel.syntheto.sexpr.SExpression;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

public class ExpressionUnlessTest {

    public static final String expectedString =
            "(SYNTHETO::MAKE-EXPRESSION-UNLESS :TEST (SYNTHETO::MAKE-EXPRESSION-LITERAL :GET (SYNTHETO::MAKE-LITERAL-BOOLEAN :VALUE NIL)) " +
                    ":THEN (SYNTHETO::MAKE-EXPRESSION-LITERAL :GET (SYNTHETO::MAKE-LITERAL-STRING :VALUE \"1\")) " +
                    ":ELSE (SYNTHETO::MAKE-EXPRESSION-LITERAL :GET (SYNTHETO::MAKE-LITERAL-STRING :VALUE \"0\")))";

    public static final ExpressionUnless expectedASTNode =
            ExpressionUnless.make(ExpressionLiteral.make(LiteralBoolean.make(false)),
                    ExpressionLiteral.make(LiteralString.make("1")), ExpressionLiteral.make(LiteralString.make("0")));

    // TODO: it would be good to have alternatve predicates automatically generated.  For now just checking expressionp.
    public static final String checkExpectedType = "(syntheto::expressionp " + expectedString + ")";

    @Test
    void toSExpression() {
        ExpressionUnless expressionUnless = expectedASTNode;
        SExpression sExpr = expressionUnless.toSExpression();
        assertEquals(expectedString,
                sExpr.toString());

        ExpressionUnless rebuilt = (ExpressionUnless) ASTBuilder.fromSExpression(sExpr);
        assertEquals(expressionUnless, rebuilt);

        Reader r = new StringReader(sExpr.toString());
        Parser p = new Parser(r);
        SExpression s = p.parseTop();
        assertEquals(sExpr, s);
    }
}