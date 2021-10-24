package edu.kestrel.syntheto.ast;

import edu.kestrel.syntheto.sexpr.Parser;
import edu.kestrel.syntheto.sexpr.SExpression;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ExpressionSumConstructTest {

    public static final String expectedString =
            "(SYNTHETO::MAKE-EXPRESSION-SUM-CONSTRUCT " +
                    ":TYPE (SYNTHETO::MAKE-IDENTIFIER :NAME \"type\") " +
                    ":ALTERNATIVE (SYNTHETO::MAKE-IDENTIFIER :NAME \"defined\") " +
                    ":FIELDS (LIST (SYNTHETO::MAKE-INITIALIZER :FIELD (SYNTHETO::MAKE-IDENTIFIER :NAME \"name\") " +
                    ":VALUE (SYNTHETO::MAKE-EXPRESSION-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"mytype\")))))";

    public static final ExpressionSumConstruct expectedASTNode = makeNode();

    private static ExpressionSumConstruct makeNode() {
        // Using an example from the Syntheto Abstract Syntax, do (make-type-defined :name (make-identifier :name "mytype"))
        // But instead of doing an ExpressionCall to get the ACL2 identifier, for simplicity we just use a variable expression.
        Identifier sumTypeId = Identifier.make("type");
        Identifier altTypeId = Identifier.make("defined");
        List<Initializer> inits = new ArrayList<>();
        inits.add(Initializer.make(Identifier.make("name"), Variable.make(Identifier.make("mytype"))));
        return ExpressionSumConstruct.make(sumTypeId, altTypeId, inits);
    }

    // TODO: it would be good to have alternatve predicates automatically generated.  For now just checking expressionp.
    public static final String checkExpectedType = "(syntheto::expressionp " + expectedString + ")";

    @Test
    void toSExpression() {

        ExpressionSumConstruct expr = expectedASTNode;
        SExpression sExpr = expr.toSExpression();
        assertEquals(expectedString,
                sExpr.toString());

        ExpressionSumConstruct rebuilt = (ExpressionSumConstruct) ASTBuilder.fromSExpression(sExpr);
        assertEquals(expr, rebuilt);

        Reader r = new StringReader(sExpr.toString());
        Parser p = new Parser(r);
        SExpression s = p.parseTop();
        assertEquals(sExpr, s);
    }

}
