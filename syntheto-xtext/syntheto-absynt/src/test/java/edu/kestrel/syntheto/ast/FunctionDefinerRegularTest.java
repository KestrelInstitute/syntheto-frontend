package edu.kestrel.syntheto.ast;

import edu.kestrel.syntheto.sexpr.Parser;
import edu.kestrel.syntheto.sexpr.SExpression;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FunctionDefinerRegularTest {

    public static final String expectedString =
            "(SYNTHETO::MAKE-FUNCTION-DEFINER-REGULAR " +
                    ":BODY (SYNTHETO::MAKE-EXPRESSION-MULTI :ARGUMENTS (LIST " +
                    "(SYNTHETO::MAKE-EXPRESSION-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"y\")) " +
                    "(SYNTHETO::MAKE-EXPRESSION-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"x\")))) " +
                    ":MEASURE NIL)";

    public static final String checkExpectedType = "(syntheto::function-definerp " + expectedString + ")";

    @Test
    void toSExpression() {
        // Example F(x:int,y:int)->(y:int,x:int)
        // But for the function definer it is just an ExpressionMulti that returns y and x.
        Variable y = Variable.make(Identifier.make("y"));
        Variable x = Variable.make(Identifier.make("x"));
        List<Expression> vals = Arrays.asList(y, x);
        ExpressionMulti retval = ExpressionMulti.make(vals);
        FunctionDefinerRegular funDefiner = FunctionDefinerRegular.make(retval, null);

        SExpression sExpression = funDefiner.toSExpression();
        assertEquals(expectedString,
                sExpression.toString());

        FunctionDefinerRegular rebuilt = (FunctionDefinerRegular) ASTBuilder.fromSExpression(sExpression);
        assertEquals(funDefiner, rebuilt);

        Reader r = new StringReader(sExpression.toString());
        Parser p = new Parser(r);
        SExpression s = p.parseTop();
        assertEquals(sExpression, s);
    }
}