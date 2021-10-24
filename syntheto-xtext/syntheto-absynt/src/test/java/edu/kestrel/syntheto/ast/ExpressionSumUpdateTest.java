package edu.kestrel.syntheto.ast;

import edu.kestrel.syntheto.sexpr.Parser;
import edu.kestrel.syntheto.sexpr.SExpression;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ExpressionSumUpdateTest {

    public static final String expectedString =
            "(SYNTHETO::MAKE-EXPRESSION-SUM-UPDATE " +
                    ":TYPE (SYNTHETO::MAKE-IDENTIFIER :NAME \"Type\") " +
                    ":TARGET (SYNTHETO::MAKE-EXPRESSION-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"T\")) " +
                    ":ALTERNATIVE (SYNTHETO::MAKE-IDENTIFIER :NAME \"defined\") " +
                    ":FIELDS (LIST (SYNTHETO::MAKE-INITIALIZER :FIELD (SYNTHETO::MAKE-IDENTIFIER :NAME \"name\") " +
                    ":VALUE (SYNTHETO::MAKE-EXPRESSION-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"mytype\")))))";

    public static final ExpressionSumUpdate expectedASTNode = makeNode();

    private static ExpressionSumUpdate makeNode() {
        // test is T.defined{name <- "mytype")
        Identifier type = Identifier.make("Type");
        Expression expr = Variable.make(Identifier.make("T"));
        Identifier alternative = Identifier.make("defined");
        Identifier fieldId = Identifier.make("name");
        List<Initializer> inits = new ArrayList<>();
        inits.add(Initializer.make(fieldId, Variable.make(Identifier.make("mytype"))));
        return ExpressionSumUpdate.make(type, expr, alternative, inits);
    }

    // TODO: it would be good to have alternatve predicates automatically generated.  For now just checking expressionp.
    public static final String checkExpectedType = "(syntheto::expressionp " + expectedString + ")";

    @Test
    void toSExpression() {

        ExpressionSumUpdate expressionSumUpdate = expectedASTNode;
        SExpression sExpr = expressionSumUpdate.toSExpression();
        assertEquals(expectedString,
                sExpr.toString());

        ExpressionSumUpdate rebuilt = (ExpressionSumUpdate) ASTBuilder.fromSExpression(sExpr);
        assertEquals(expressionSumUpdate, rebuilt);

        Reader r = new StringReader(sExpr.toString());
        Parser p = new Parser(r);
        SExpression s = p.parseTop();
        assertEquals(sExpr, s);
    }
}