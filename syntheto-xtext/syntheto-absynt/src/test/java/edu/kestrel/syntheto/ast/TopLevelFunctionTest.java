package edu.kestrel.syntheto.ast;

import edu.kestrel.syntheto.sexpr.Parser;
import edu.kestrel.syntheto.sexpr.SExpression;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TopLevelFunctionTest {

    public static final String expectedString =
            "(SYNTHETO::MAKE-TOPLEVEL-FUNCTION :GET " +
                    "(SYNTHETO::MAKE-FUNCTION-DEFINITION :HEADER " +
                    "(SYNTHETO::MAKE-FUNCTION-HEADER :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"F\") "+
                    ":INPUTS (LIST (SYNTHETO::MAKE-TYPED-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"x\") :TYPE (SYNTHETO::MAKE-TYPE-INTEGER)) " +
                    "(SYNTHETO::MAKE-TYPED-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"y\") :TYPE (SYNTHETO::MAKE-TYPE-INTEGER))) " +
                    ":OUTPUTS (LIST (SYNTHETO::MAKE-TYPED-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"output_y\") :TYPE (SYNTHETO::MAKE-TYPE-INTEGER)) " +
                    "(SYNTHETO::MAKE-TYPED-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"output_x\") :TYPE (SYNTHETO::MAKE-TYPE-INTEGER)))) " +
                    ":PRECONDITION NIL :POSTCONDITION NIL :DEFINER " +
                    "(SYNTHETO::MAKE-FUNCTION-DEFINER-REGULAR " +
                    ":BODY (SYNTHETO::MAKE-EXPRESSION-MULTI :ARGUMENTS (LIST " +
                    "(SYNTHETO::MAKE-EXPRESSION-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"y\")) " +
                    "(SYNTHETO::MAKE-EXPRESSION-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"x\")))) " +
                    ":MEASURE NIL)))";

    public static final String checkExpectedType = "(syntheto::toplevelp " + expectedString + ")";

    @Test
    void toSExpression() {
        // Example F(x:int,y:int)->(y:int,x:int)
        Identifier fnname = Identifier.make("F");
        TypedVariable x = TypedVariable.make(Identifier.make("x"), TypeInteger.make());
        TypedVariable y = TypedVariable.make(Identifier.make("y"), TypeInteger.make());
        List<TypedVariable> args = Arrays.asList(x, y);
        TypedVariable output_y = TypedVariable.make(Identifier.make("output_y"), TypeInteger.make());
        TypedVariable output_x = TypedVariable.make(Identifier.make("output_x"), TypeInteger.make());
        List<TypedVariable> output_args = Arrays.asList(output_y, output_x);
        FunctionHeader funhead = FunctionHeader.make(fnname, args, output_args);

        // TODO: also test non-null precondition and postcondition

        Variable vy = Variable.make(Identifier.make("y"));
        Variable vx = Variable.make(Identifier.make("x"));
        List<Expression> vals = Arrays.asList(vy, vx);
        ExpressionMulti retval = ExpressionMulti.make(vals);
        FunctionDefinerRegular funDefiner = FunctionDefinerRegular.make(retval, null);

        FunctionDefinition def = FunctionDefinition.make(funhead, null, null, funDefiner);
        TopLevelFunction tlf = TopLevelFunction.make(def);

        SExpression sExpression = tlf.toSExpression();
        assertEquals(expectedString,
                sExpression.toString());

        TopLevelFunction rebuilt = (TopLevelFunction) ASTBuilder.fromSExpression(sExpression);
        assertEquals(tlf, rebuilt);

        Reader r = new StringReader(sExpression.toString());
        Parser p = new Parser(r);
        SExpression s = p.parseTop();
        assertEquals(sExpression, s);
    }
}