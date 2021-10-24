package edu.kestrel.syntheto.ast;

import edu.kestrel.syntheto.sexpr.Parser;
import edu.kestrel.syntheto.sexpr.SExpression;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TypeSumTest {

    public static final String expectedString =
            "(SYNTHETO::MAKE-TYPE-SUM :ALTERNATIVES (LIST " +

                    "(SYNTHETO::MAKE-ALTERNATIVE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"ordered_pair\") :PRODUCT " +
                    "(SYNTHETO::MAKE-TYPE-PRODUCT " +
                    ":FIELDS (LIST (SYNTHETO::MAKE-FIELD :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"x\") :TYPE (SYNTHETO::MAKE-TYPE-INTEGER)) " +
                    "(SYNTHETO::MAKE-FIELD :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"y\") :TYPE (SYNTHETO::MAKE-TYPE-INTEGER))) " +
                    ":INVARIANT NIL)) " +

                    "(SYNTHETO::MAKE-ALTERNATIVE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"polar_coords\") :PRODUCT "+
                    "(SYNTHETO::MAKE-TYPE-PRODUCT " +
                    ":FIELDS (LIST (SYNTHETO::MAKE-FIELD :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"r\") :TYPE (SYNTHETO::MAKE-TYPE-INTEGER)) " +
                    "(SYNTHETO::MAKE-FIELD :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"theta\") :TYPE (SYNTHETO::MAKE-TYPE-INTEGER))) " +
                    ":INVARIANT (SYNTHETO::MAKE-EXPRESSION-BINARY " +
                    ":OPERATOR (SYNTHETO::MAKE-BINARY-OP-GE) " +
                    ":LEFT-OPERAND (SYNTHETO::MAKE-EXPRESSION-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"r\")) " +
                    ":RIGHT-OPERAND (SYNTHETO::MAKE-EXPRESSION-LITERAL :GET (SYNTHETO::MAKE-LITERAL-INTEGER :VALUE 0)))))))";

    public static final String checkExpectedType = "(syntheto::type-sump " + expectedString + ")";

    @Test
    void toSExpression() {
        List<Alternative> alts = new ArrayList<>();

        // Copied from AlternativeTest

        // Example is struct ordered_pair { x: integer, y: integer }
        // copied code from TypeProductTest.java, then changed names of fields
        // Make two basic fields
        Identifier v1 = Identifier.make("x");
        Identifier v2 = Identifier.make("y");
        Field f1 = Field.make(v1, TypeInteger.make());
        Field f2 = Field.make(v2, TypeInteger.make());
        List<Field> fields = Arrays.asList(f1, f2);
        // Make a product type with a null invariant.
        TypeProduct prod1 = TypeProduct.make(fields, null);
        Identifier varname1 = Identifier.make("ordered_pair");
        Alternative alt1 = Alternative.make(varname1, prod1);
        alts.add(alt1);

        // Another alternative is struct ordered_pair { r: integer, theta: integer }
        Identifier v3 = Identifier.make("r");
        Identifier v4 = Identifier.make("theta");
        Field f3 = Field.make(v3, TypeInteger.make());
        Field f4 = Field.make(v4, TypeInteger.make());
        List<Field> fields2 = Arrays.asList(f3, f4);
        // Let's make the invariant require that r>=0
        Expression expr = ExpressionBinary.make(ExpressionBinary.Operator.GE,
                        Variable.make(v3), ExpressionLiteral.make(LiteralInteger.make(0)));
        TypeProduct prod2 = TypeProduct.make(fields2, expr);
        Identifier varname2 = Identifier.make("polar_coords");
        Alternative alt2 = Alternative.make(varname2, prod2);
        alts.add(alt2);

        TypeSum typeSum = TypeSum.make(alts);
        SExpression sexpr = typeSum.toSExpression();
        assertEquals(expectedString, sexpr.toString());

        TypeSum rebuilt = (TypeSum) ASTBuilder.fromSExpression(sexpr);
        assertEquals(typeSum, rebuilt);

        Reader r = new StringReader(sexpr.toString());
        Parser p = new Parser(r);
        SExpression s = p.parseTop();
        assertEquals(sexpr, s);
    }
}