package edu.kestrel.syntheto.ast;

import edu.kestrel.syntheto.sexpr.Parser;
import edu.kestrel.syntheto.sexpr.SExpression;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

public class ExpressionProductFieldTest {

    public static final String expectedString =
            "(SYNTHETO::MAKE-EXPRESSION-PRODUCT-FIELD " +
                    ":TYPE (SYNTHETO::MAKE-IDENTIFIER :NAME \"ordered_pair\") " +
                    ":TARGET (SYNTHETO::MAKE-EXPRESSION-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"pair\")) "+
                    ":FIELD (SYNTHETO::MAKE-IDENTIFIER :NAME \"x\"))";

    public static final ExpressionProductField expectedASTNode = makeNode();

    private static ExpressionProductField makeNode() {
        // Example is pair.x
        Identifier typename = Identifier.make("ordered_pair");
        Expression val = Variable.make(Identifier.make("pair"));
        Identifier fieldname = Identifier.make("x");
        return ExpressionProductField.make(typename, val,fieldname);
    }

    // TODO: it would be good to have alternatve predicates automatically generated.  For now just checking expressionp.
    public static final String checkExpectedType = "(syntheto::expressionp " + expectedString + ")";

    @Test
    void toSExpression() {
        ExpressionProductField expr = expectedASTNode;
        SExpression sExpression = expr.toSExpression();
        assertEquals(expectedString,
                sExpression.toString());

        ExpressionProductField rebuilt = (ExpressionProductField) ASTBuilder.fromSExpression(sExpression);
        assertEquals(expr, rebuilt);

        Reader r = new StringReader(sExpression.toString());
        Parser p = new Parser(r);
        SExpression s = p.parseTop();
        assertEquals(sExpression, s);
    }
}