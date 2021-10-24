package edu.kestrel.syntheto.ast;

import edu.kestrel.syntheto.sexpr.Parser;
import edu.kestrel.syntheto.sexpr.SExpression;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class ExpressionComponentTest {

    public static final String expectedString =
            "(SYNTHETO::MAKE-EXPRESSION-COMPONENT " +
                    ":MULTI (SYNTHETO::MAKE-EXPRESSION-CALL :FUNCTION (SYNTHETO::MAKE-IDENTIFIER :NAME \"MYFUN\") " +
                    ":TYPES (LIST) " +
                    ":ARGUMENTS (LIST (SYNTHETO::MAKE-EXPRESSION-LITERAL :GET (SYNTHETO::MAKE-LITERAL-INTEGER :VALUE 8)))) " +
                    ":INDEX 2)";

    public static final ExpressionComponent expectedASTNode =
            ExpressionComponent.make(ExpressionCall.make(Identifier.make("MYFUN"),
                    Arrays.asList(ExpressionLiteral.make(LiteralInteger.make(8)))),
                    2);

    // TODO: it would be good to have alternative predicates automatically generated.  For now just checking expressionp.
    public static final String checkExpectedType = "(syntheto::expressionp " + expectedString + ")";

    @Test
    void fromSExpression() {
        // TODO: test failure of -1 and Integer.MAX_VALUE
    }

    @Test
    void toSExpression() {
        ExpressionComponent exprComponent = expectedASTNode;
        SExpression sExpr = exprComponent.toSExpression();
        assertEquals(expectedString,
                sExpr.toString());

        ExpressionComponent rebuilt = (ExpressionComponent) ASTBuilder.fromSExpression(sExpr);
        assertEquals(exprComponent, rebuilt);

        Reader r = new StringReader(sExpr.toString());
        Parser p = new Parser(r);
        SExpression s = p.parseTop();
        assertEquals(sExpr, s);
    }
}
