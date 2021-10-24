package edu.kestrel.syntheto.ast;

import edu.kestrel.syntheto.sexpr.Parser;
import edu.kestrel.syntheto.sexpr.SExpression;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.io.StringReader;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

public class ExpressionIfTest {

    public static final String expectedString =
            "(SYNTHETO::MAKE-EXPRESSION-IF :TEST (SYNTHETO::MAKE-EXPRESSION-LITERAL :GET (SYNTHETO::MAKE-LITERAL-BOOLEAN :VALUE NIL)) " +
                    ":THEN (SYNTHETO::MAKE-EXPRESSION-LITERAL :GET (SYNTHETO::MAKE-LITERAL-STRING :VALUE \"1\")) " +
                    ":ELSE (SYNTHETO::MAKE-EXPRESSION-LITERAL :GET (SYNTHETO::MAKE-LITERAL-STRING :VALUE \"0\")))";

    public static final ExpressionIf expectedASTNode =
            ExpressionIf.make(ExpressionLiteral.make(LiteralBoolean.make(false)),
            ExpressionLiteral.make(LiteralString.make("1")), ExpressionLiteral.make(LiteralString.make("0")));

    // TODO: it would be good to have alternatve predicates automatically generated.  For now just checking expressionp.
    public static final String checkExpectedType = "(syntheto::expressionp " + expectedString + ")";

    @Test
    void toSExpression() {
        ExpressionIf expressionIf = expectedASTNode;
        SExpression sExpr = expressionIf.toSExpression();
        assertEquals(expectedString,
                sExpr.toString());

        ExpressionIf rebuilt = (ExpressionIf) ASTBuilder.fromSExpression(sExpr);
        assertEquals(expressionIf, rebuilt);

        Reader r = new StringReader(sExpr.toString());
        Parser p = new Parser(r);
        SExpression s = p.parseTop();
        assertEquals(sExpr, s);
    }
}