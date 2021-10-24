package edu.kestrel.syntheto.ast;

import edu.kestrel.syntheto.sexpr.Parser;
import edu.kestrel.syntheto.sexpr.SExpression;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.io.StringReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ExpressionCallTest {

    public static final String expectedString =
            "(SYNTHETO::MAKE-EXPRESSION-CALL " +
                    ":FUNCTION (SYNTHETO::MAKE-IDENTIFIER :NAME \"myfun\") " +
                    ":TYPES (LIST) " +
                    ":ARGUMENTS (LIST (SYNTHETO::MAKE-EXPRESSION-LITERAL :GET (SYNTHETO::MAKE-LITERAL-INTEGER :VALUE 1))))";

    public static final ExpressionCall expectedASTNode = makeNode();

    private static ExpressionCall makeNode() {
        List<Expression> args = new ArrayList<>();
        args.add(ExpressionLiteral.make(LiteralInteger.make(1)));
        return ExpressionCall.make(Identifier.make("myfun"),
                args);
    }

    // TODO: it would be good to have alternatve predicates automatically generated.  For now just checking expressionp.
    public static final String checkExpectedType = "(syntheto::expressionp " + expectedString + ")";

    @Test
    void toSExpression() {
        ExpressionCall exprCall = expectedASTNode;
        SExpression sExpr = exprCall.toSExpression();
        assertEquals(expectedString,
                sExpr.toString());

        ExpressionCall rebuilt = (ExpressionCall) ASTBuilder.fromSExpression(sExpr);
        assertEquals(exprCall, rebuilt);

        Reader r = new StringReader(sExpr.toString());
        Parser p = new Parser(r);
        SExpression s = p.parseTop();
        assertEquals(sExpr, s);
    }

}
