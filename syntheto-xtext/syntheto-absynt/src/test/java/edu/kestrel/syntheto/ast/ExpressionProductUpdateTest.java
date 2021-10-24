package edu.kestrel.syntheto.ast;

import edu.kestrel.syntheto.sexpr.Parser;
import edu.kestrel.syntheto.sexpr.SExpression;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ExpressionProductUpdateTest {

    public static final String expectedString =
            "(SYNTHETO::MAKE-EXPRESSION-PRODUCT-UPDATE " +
                    ":TYPE (SYNTHETO::MAKE-IDENTIFIER :NAME \"ordered_pair\") " +
                    ":TARGET (SYNTHETO::MAKE-EXPRESSION-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"pt\")) " +
                    ":FIELDS (LIST (SYNTHETO::MAKE-INITIALIZER :FIELD (SYNTHETO::MAKE-IDENTIFIER :NAME \"x\") " +
                    ":VALUE (SYNTHETO::MAKE-EXPRESSION-LITERAL :GET (SYNTHETO::MAKE-LITERAL-INTEGER :VALUE 3)))))";

    public static final ExpressionProductUpdate expectedASTNode = makeNode();

    private static ExpressionProductUpdate makeNode() {
        // test is pt{y <- 3} (not sure about the surface syntax, but the variable pt is of type ordered_pair
        // with fields x and y, and the update returns an ordered_pair with the same x and y = 3.

        Identifier typename = Identifier.make("ordered_pair");
        Expression prodexpr = Variable.make(Identifier.make("pt"));
        List<Initializer> inits = new ArrayList<>();
        inits.add(Initializer.make(Identifier.make("x"), ExpressionLiteral.make(LiteralInteger.make(3))));
        return ExpressionProductUpdate.make(typename, prodexpr, inits);
    }

    // TODO: it would be good to have alternatve predicates automatically generated.  For now just checking expressionp.
    public static final String checkExpectedType = "(syntheto::expressionp " + expectedString + ")";

    @Test
    void toSExpression() {
        ExpressionProductUpdate update = expectedASTNode;
        SExpression sExpr = update.toSExpression();
        assertEquals(expectedString,
                sExpr.toString());

        ExpressionProductUpdate rebuilt = (ExpressionProductUpdate) ASTBuilder.fromSExpression(sExpr);
        assertEquals(update, rebuilt);

        Reader r = new StringReader(sExpr.toString());
        Parser p = new Parser(r);
        SExpression s = p.parseTop();
        assertEquals(sExpr, s);
    }

}
