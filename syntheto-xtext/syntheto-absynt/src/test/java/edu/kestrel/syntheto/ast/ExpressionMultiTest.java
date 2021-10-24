package edu.kestrel.syntheto.ast;

import edu.kestrel.syntheto.sexpr.Parser;
import edu.kestrel.syntheto.sexpr.SExpression;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ExpressionMultiTest {

    public static final String expectedString =
            "(SYNTHETO::MAKE-EXPRESSION-MULTI " +
                    ":ARGUMENTS (LIST (SYNTHETO::MAKE-EXPRESSION-LITERAL :GET (SYNTHETO::MAKE-LITERAL-INTEGER :VALUE 1)) " +
                    "(SYNTHETO::MAKE-EXPRESSION-UNARY :OPERATOR (SYNTHETO::MAKE-UNARY-OP-MINUS) " +
                    ":OPERAND (SYNTHETO::MAKE-EXPRESSION-LITERAL :GET (SYNTHETO::MAKE-LITERAL-INTEGER :VALUE 1)))))";

    public static final ExpressionMulti expectedASTNode = makeNode();

    private static ExpressionMulti makeNode() {
        List<Expression> args = new ArrayList<>();
        args.add(ExpressionLiteral.make(LiteralInteger.make(1)));
        args.add(ExpressionUnary.make(ExpressionUnary.Operator.MINUS, ExpressionLiteral.make(LiteralInteger.make(1))));
        return ExpressionMulti.make(args);
    }

    // TODO: it would be good to have alternatve predicates automatically generated.  For now just checking expressionp.
    public static final String checkExpectedType = "(syntheto::expressionp " + expectedString + ")";

    @Test
    void toSExpression() {
        ExpressionMulti exprMulti = expectedASTNode;
        SExpression sExpr = exprMulti.toSExpression();
        assertEquals(expectedString,
                sExpr.toString());

        ExpressionMulti rebuilt = (ExpressionMulti) ASTBuilder.fromSExpression(sExpr);
        assertEquals(exprMulti, rebuilt);

        Reader r = new StringReader(sExpr.toString());
        Parser p = new Parser(r);
        SExpression s = p.parseTop();
        assertEquals(sExpr, s);
    }

}
