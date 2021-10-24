package edu.kestrel.syntheto.ast;

import edu.kestrel.syntheto.sexpr.Parser;
import edu.kestrel.syntheto.sexpr.SExpression;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TypeProductTest {

    public static final String expectedString1 =
            "(SYNTHETO::MAKE-TYPE-PRODUCT " +
                    ":FIELDS (LIST (SYNTHETO::MAKE-FIELD :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"v1\") :TYPE (SYNTHETO::MAKE-TYPE-INTEGER)) " +
                    "(SYNTHETO::MAKE-FIELD :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"v2\") :TYPE (SYNTHETO::MAKE-TYPE-INTEGER))) " +
                    ":INVARIANT NIL)";

    public static final String checkExpectedType1 = "(syntheto::type-productp " + expectedString1 + ")";

    public static final String expectedString2 =
            "(SYNTHETO::MAKE-TYPE-PRODUCT " +
                    ":FIELDS (LIST (SYNTHETO::MAKE-FIELD :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"v1\") :TYPE (SYNTHETO::MAKE-TYPE-INTEGER)) " +
                    "(SYNTHETO::MAKE-FIELD :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"v2\") :TYPE (SYNTHETO::MAKE-TYPE-INTEGER))) " +
                    ":INVARIANT (SYNTHETO::MAKE-EXPRESSION-BINARY " +
                    ":OPERATOR (SYNTHETO::MAKE-BINARY-OP-EQ) " +
                    ":LEFT-OPERAND (SYNTHETO::MAKE-EXPRESSION-BINARY " +
                    ":OPERATOR (SYNTHETO::MAKE-BINARY-OP-MUL) " +
                    ":LEFT-OPERAND (SYNTHETO::MAKE-EXPRESSION-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"v1\")) " +
                    ":RIGHT-OPERAND (SYNTHETO::MAKE-EXPRESSION-LITERAL :GET (SYNTHETO::MAKE-LITERAL-INTEGER :VALUE 2))) " +
                    ":RIGHT-OPERAND (SYNTHETO::MAKE-EXPRESSION-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"v2\"))))";

    public static final String checkExpectedType2 = "(syntheto::type-productp " + expectedString2 + ")";

    @Test
    void toSExpression() {
        // Make two basic fields
        Identifier v1 = Identifier.make("v1");
        Identifier v2 = Identifier.make("v2");
        Field f1 = Field.make(v1, TypeInteger.make());
        Field f2 = Field.make(v2, TypeInteger.make());
        List<Field> fields = Arrays.asList(f1, f2);

        // Test a product type with a null invariant.
        TypeProduct prod1 = TypeProduct.make(fields, null);
        SExpression sexpr1 = prod1.toSExpression();
        assertEquals(expectedString1,
                sexpr1.toString());

        TypeProduct rebuilt1 = (TypeProduct) ASTBuilder.fromSExpression(sexpr1);
        assertEquals(prod1, rebuilt1);

        Reader r1 = new StringReader(sexpr1.toString());
        Parser p1 = new Parser(r1);
        SExpression s1 = p1.parseTop();
        assertEquals(sexpr1, s1);

        // Test a product type with a non-null invariant. v1*2 = v2
        Expression expr = ExpressionBinary.make(ExpressionBinary.Operator.EQ,
                            ExpressionBinary.make(ExpressionBinary.Operator.MUL,
                                Variable.make(v1), ExpressionLiteral.make(LiteralInteger.make(2))),
                            Variable.make(v2));
        TypeProduct prod2 = TypeProduct.make(fields, expr);
        SExpression sexpr2 = prod2.toSExpression();
        assertEquals(expectedString2,
                sexpr2.toString());

        TypeProduct rebuilt = (TypeProduct) ASTBuilder.fromSExpression(sexpr2);
        assertEquals(prod2, rebuilt);

        Reader r2 = new StringReader(sexpr2.toString());
        Parser p2 = new Parser(r2);
        SExpression s2 = p2.parseTop();
        assertEquals(sexpr2, s2);
    }
}