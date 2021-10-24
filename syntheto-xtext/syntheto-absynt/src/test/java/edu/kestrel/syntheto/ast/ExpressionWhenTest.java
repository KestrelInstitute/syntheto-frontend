package edu.kestrel.syntheto.ast;

import edu.kestrel.syntheto.sexpr.Parser;
import edu.kestrel.syntheto.sexpr.SExpression;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

public class ExpressionWhenTest {

    public static final String expectedString =
            "(SYNTHETO::MAKE-EXPRESSION-WHEN :TEST (SYNTHETO::MAKE-EXPRESSION-LITERAL :GET (SYNTHETO::MAKE-LITERAL-BOOLEAN :VALUE T)) " +
                    ":THEN (SYNTHETO::MAKE-EXPRESSION-LITERAL :GET (SYNTHETO::MAKE-LITERAL-CHARACTER :VALUE (CODE-CHAR 32))) " +
                    ":ELSE (SYNTHETO::MAKE-EXPRESSION-LITERAL :GET (SYNTHETO::MAKE-LITERAL-CHARACTER :VALUE (CODE-CHAR 65))))";

    public static final ExpressionWhen expectedASTNode =
            ExpressionWhen.make(ExpressionLiteral.make(LiteralBoolean.make(true)),
                    ExpressionLiteral.make(LiteralCharacter.make(' ')), ExpressionLiteral.make(LiteralCharacter.make('A')));

    // TODO: it would be good to have alternatve predicates automatically generated.  For now just checking expressionp.
    public static final String checkExpectedType = "(syntheto::expressionp " + expectedString + ")";

    @Test
    void toSExpression() {
        ExpressionWhen expressionWhen = expectedASTNode;
        SExpression sExpr = expressionWhen.toSExpression();
        assertEquals(expectedString,
                sExpr.toString());

        ExpressionWhen rebuilt = (ExpressionWhen) ASTBuilder.fromSExpression(sExpr);
        assertEquals(expressionWhen, rebuilt);

        Reader r = new StringReader(sExpr.toString());
        Parser p = new Parser(r);
        SExpression s = p.parseTop();
        assertEquals(sExpr, s);
    }
}