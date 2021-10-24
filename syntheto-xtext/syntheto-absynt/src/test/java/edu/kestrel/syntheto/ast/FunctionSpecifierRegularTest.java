package edu.kestrel.syntheto.ast;

import edu.kestrel.syntheto.sexpr.Parser;
import edu.kestrel.syntheto.sexpr.SExpression;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FunctionSpecifierRegularTest {

    public static final String expectedString =
            "(SYNTHETO::MAKE-FUNCTION-SPECIFIER-REGULAR " +
                    ":BODY (SYNTHETO::MAKE-EXPRESSION-MULTI :ARGUMENTS (LIST " +
                    "(SYNTHETO::MAKE-EXPRESSION-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"y\")) " +
                    "(SYNTHETO::MAKE-EXPRESSION-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"x\")))))";

    public static final String checkExpectedType = "(syntheto::function-specifierp " + expectedString + ")";

    @Test
    void toSExpression() {
        // Example doesn't try to make sense.  Just something to make into a FunctionSpecifierRegular
        Variable y = Variable.make(Identifier.make("y"));
        Variable x = Variable.make(Identifier.make("x"));
        List<Expression> vals = Arrays.asList(y, x);
        ExpressionMulti retval = ExpressionMulti.make(vals);
        FunctionSpecifierRegular funDefiner = FunctionSpecifierRegular.make(retval);

        SExpression sExpression = funDefiner.toSExpression();
        assertEquals(expectedString,
                sExpression.toString());

        FunctionSpecifierRegular rebuilt = (FunctionSpecifierRegular) ASTBuilder.fromSExpression(sExpression);
        assertEquals(funDefiner, rebuilt);

        Reader r = new StringReader(sExpression.toString());
        Parser p = new Parser(r);
        SExpression s = p.parseTop();
        assertEquals(sExpression, s);
    }
}
