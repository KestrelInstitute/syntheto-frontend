package edu.kestrel.syntheto.ast;

import edu.kestrel.syntheto.sexpr.Parser;
import edu.kestrel.syntheto.sexpr.SExpression;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class TransformArgumentValueTermTest {

    public static final String expectedString =
            "(SYNTHETO::MAKE-TRANSFORM-ARGUMENT-VALUE-TERM :GET " +
                    "(SYNTHETO::MAKE-EXPRESSION-LITERAL :GET (SYNTHETO::MAKE-LITERAL-INTEGER :VALUE 8)))";

    public static final TransformArgumentValueTerm expectedASTNode =
            TransformArgumentValueTerm.make(ExpressionLiteral.make(LiteralInteger.make(8)));

    // TODO: it would be good to have alternative predicates automatically generated.  For now just checking expressionp.
    public static final String checkExpectedType = "(syntheto::transform-argument-valuep " + expectedString + ")";


    @Test
    void toSExpression() {
        TransformArgumentValueTerm exprComponent = expectedASTNode;
        SExpression sExpr = exprComponent.toSExpression();
        assertEquals(expectedString,
                sExpr.toString());

        TransformArgumentValueTerm rebuilt = (TransformArgumentValueTerm) ASTBuilder.fromSExpression(sExpr);
        assertEquals(exprComponent, rebuilt);

        Reader r = new StringReader(sExpr.toString());
        Parser p = new Parser(r);
        SExpression s = p.parseTop();
        assertEquals(sExpr, s);
    }
}
