package edu.kestrel.syntheto.ast;

import edu.kestrel.syntheto.sexpr.Parser;
import edu.kestrel.syntheto.sexpr.SExpression;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ExpressionProductConstructTest {

    public static final String expectedString =
            "(SYNTHETO::MAKE-EXPRESSION-PRODUCT-CONSTRUCT " +
                    ":TYPE (SYNTHETO::MAKE-IDENTIFIER :NAME \"theprod\") " +
                    ":FIELDS (LIST (SYNTHETO::MAKE-INITIALIZER :FIELD (SYNTHETO::MAKE-IDENTIFIER :NAME \"x\") " +
                    ":VALUE (SYNTHETO::MAKE-EXPRESSION-LITERAL :GET (SYNTHETO::MAKE-LITERAL-INTEGER :VALUE 9)))))";

    public static final ExpressionProductConstruct expectedASTNode = makeNode();

    private static ExpressionProductConstruct makeNode() {
        // Make an expression that constructs an instance of the "theprod" type (which only has one field, x:integer)
        // that has 9 as the value of field x.
        List<Initializer> inits = new ArrayList<>();
        inits.add(Initializer.make(Identifier.make("x"), ExpressionLiteral.make(LiteralInteger.make(9))));
        return ExpressionProductConstruct.make(Identifier.make("theprod"), inits);
    }

    // TODO: it would be good to have alternatve predicates automatically generated.  For now just checking expressionp.
    public static final String checkExpectedType = "(syntheto::expressionp " + expectedString + ")";

    @Test
    void toSExpression() {

        ExpressionProductConstruct expr = expectedASTNode;
        SExpression sExpr = expr.toSExpression();
        assertEquals(expectedString,
                sExpr.toString());

        ExpressionProductConstruct rebuilt = (ExpressionProductConstruct) ASTBuilder.fromSExpression(sExpr);
        assertEquals(expr, rebuilt);

        Reader r = new StringReader(sExpr.toString());
        Parser p = new Parser(r);
        SExpression s = p.parseTop();
        assertEquals(sExpr, s);
    }

}
