package edu.kestrel.syntheto.ast;

import edu.kestrel.syntheto.sexpr.Parser;
import edu.kestrel.syntheto.sexpr.SExpression;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

public class InitializerTest {

    public static final String expectedString =
            "(SYNTHETO::MAKE-INITIALIZER :FIELD (SYNTHETO::MAKE-IDENTIFIER :NAME \"f1\") "+
                    ":VALUE (SYNTHETO::MAKE-EXPRESSION-LITERAL :GET (SYNTHETO::MAKE-LITERAL-STRING :VALUE \"abc\")))";

    public static final String checkExpectedType = "(syntheto::initializerp " + expectedString + ")";

    @Test
    void toSExpression() {
        // Example is f1 = "abc"
        Identifier fieldname = Identifier.make("f1");
        Expression val = ExpressionLiteral.make(LiteralString.make("abc"));
        Initializer nameAndVal = Initializer.make(fieldname, val);
        SExpression sExpression = nameAndVal.toSExpression();
        assertEquals(expectedString,
                sExpression.toString());

        Initializer rebuilt = (Initializer) ASTBuilder.fromSExpression(sExpression);
        assertEquals(nameAndVal, rebuilt);

        Reader r = new StringReader(sExpression.toString());
        Parser p = new Parser(r);
        SExpression s = p.parseTop();
        assertEquals(sExpression, s);
    }
}