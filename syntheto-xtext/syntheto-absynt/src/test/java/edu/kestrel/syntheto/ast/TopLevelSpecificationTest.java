package edu.kestrel.syntheto.ast;

import edu.kestrel.syntheto.sexpr.Parser;
import edu.kestrel.syntheto.sexpr.SExpression;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TopLevelSpecificationTest {

    public static final String expectedString =
            "(SYNTHETO::MAKE-TOPLEVEL-SPECIFICATION :GET " +
                    "(SYNTHETO::MAKE-FUNCTION-SPECIFICATION :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"myspec\") " +
                    ":FUNCTIONS (LIST " +
                    "(SYNTHETO::MAKE-FUNCTION-HEADER :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"F\") "+
                    ":INPUTS (LIST (SYNTHETO::MAKE-TYPED-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"x\") :TYPE (SYNTHETO::MAKE-TYPE-INTEGER)) " +
                    "(SYNTHETO::MAKE-TYPED-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"y\") :TYPE (SYNTHETO::MAKE-TYPE-INTEGER))) " +
                    ":OUTPUTS (LIST (SYNTHETO::MAKE-TYPED-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"output_y\") :TYPE (SYNTHETO::MAKE-TYPE-INTEGER)) " +
                    "(SYNTHETO::MAKE-TYPED-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"output_x\") :TYPE (SYNTHETO::MAKE-TYPE-INTEGER))))" +
                    ") :SPECIFIER " +
                    "(SYNTHETO::MAKE-FUNCTION-SPECIFIER-REGULAR " +
                    ":BODY (SYNTHETO::MAKE-EXPRESSION-MULTI :ARGUMENTS (LIST " +
                    "(SYNTHETO::MAKE-EXPRESSION-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"y\")) " +
                    "(SYNTHETO::MAKE-EXPRESSION-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"x\")))))" +
                    "))";

    public static final String checkExpectedType = "(syntheto::toplevelp " + expectedString + ")";

    @Test
    void toSExpression() {
        // Example doesn't try to make sense.  Just something to make into a FunctionSpecifierRegular
        Identifier id = Identifier.make("myspec");

        // copy FunctionHeaderTest
        Identifier fnname = Identifier.make("F");
        TypedVariable x = TypedVariable.make(Identifier.make("x"), TypeInteger.make());
        TypedVariable y = TypedVariable.make(Identifier.make("y"), TypeInteger.make());
        List<TypedVariable> args = Arrays.asList(x, y);
        TypedVariable output_y = TypedVariable.make(Identifier.make("output_y"), TypeInteger.make());
        TypedVariable output_x = TypedVariable.make(Identifier.make("output_x"), TypeInteger.make());
        List<TypedVariable> output_args = Arrays.asList(output_y, output_x);
        FunctionHeader funhead = FunctionHeader.make(fnname, args, output_args);

        // put it in a list
        List<FunctionHeader> headers = Arrays.asList(funhead);

        // copy FunctionSpecifierRegularTest (rename varnames)
        Variable y2 = Variable.make(Identifier.make("y"));
        Variable x2 = Variable.make(Identifier.make("x"));
        List<Expression> vals = Arrays.asList(y2, x2);
        ExpressionMulti retval = ExpressionMulti.make(vals);
        FunctionSpecifierRegular funDefiner = FunctionSpecifierRegular.make(retval);

        // put them together into a FrankenSpecification
        FunctionSpecification funspec = FunctionSpecification.make(id, headers, funDefiner);
        TopLevelSpecification tlspec = TopLevelSpecification.make(funspec);

        SExpression sExpression = tlspec.toSExpression();
        assertEquals(expectedString,
                sExpression.toString());

        TopLevelSpecification rebuilt = (TopLevelSpecification) ASTBuilder.fromSExpression(sExpression);
        assertEquals(tlspec, rebuilt);

        Reader r = new StringReader(sExpression.toString());
        Parser p = new Parser(r);
        SExpression s = p.parseTop();
        assertEquals(sExpression, s);
    }
}