package edu.kestrel.syntheto.ast;

import edu.kestrel.syntheto.sexpr.Parser;
import edu.kestrel.syntheto.sexpr.SExpression;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

public class ExpressionSumFieldTest {

    public static final String expectedString =
            "(SYNTHETO::MAKE-EXPRESSION-SUM-FIELD " +
                    ":TYPE (SYNTHETO::MAKE-IDENTIFIER :NAME \"Type\") " +
                    ":TARGET (SYNTHETO::MAKE-EXPRESSION-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"T\")) " +
                    ":ALTERNATIVE (SYNTHETO::MAKE-IDENTIFIER :NAME \"defined\") " +
                    ":FIELD (SYNTHETO::MAKE-IDENTIFIER :NAME \"name\"))";

    public static ExpressionSumField expectedASTNode = makeNode();

    private static ExpressionSumField makeNode() {
        // T.defined.name
        Identifier sumTypeName = Identifier.make("Type");  // The name of the sum type
        Expression expr = Variable.make(Identifier.make("T"));
        Identifier alternative = Identifier.make("defined");
        Identifier field = Identifier.make("name");
        return ExpressionSumField.make(sumTypeName, expr, alternative, field);
    }

    // TODO: it would be good to have alternatve predicates automatically generated.  For now just checking expressionp.
    public static final String checkExpectedType = "(syntheto::expressionp " + expectedString + ")";

    @Test
    void toSExpression() {

        ExpressionSumField expressionSumField = expectedASTNode;
        SExpression sExpression = expressionSumField.toSExpression();
        assertEquals(expectedString,
                sExpression.toString());

        ExpressionSumField rebuilt = (ExpressionSumField) ASTBuilder.fromSExpression(sExpression);
        assertEquals(expressionSumField, rebuilt);

        Reader r = new StringReader(sExpression.toString());
        Parser p = new Parser(r);
        SExpression s = p.parseTop();
        assertEquals(sExpression, s);
    }
}