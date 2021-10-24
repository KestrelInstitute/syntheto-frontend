package edu.kestrel.syntheto.ast;

import edu.kestrel.syntheto.sexpr.Parser;
import edu.kestrel.syntheto.sexpr.SExpression;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

public class TypeSubsetTest {

    public static final String expectedString1 =
            "(SYNTHETO::MAKE-TYPE-SUBSET " +
                    ":SUPERTYPE (SYNTHETO::MAKE-TYPE-INTEGER) " +
                    ":VARIABLE (SYNTHETO::MAKE-IDENTIFIER :NAME \"x\") " +
                    ":RESTRICTION (SYNTHETO::MAKE-EXPRESSION-BINARY :OPERATOR (SYNTHETO::MAKE-BINARY-OP-GT) " +
                    ":LEFT-OPERAND (SYNTHETO::MAKE-EXPRESSION-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"x\")) " +
                    ":RIGHT-OPERAND (SYNTHETO::MAKE-EXPRESSION-LITERAL :GET (SYNTHETO::MAKE-LITERAL-INTEGER :VALUE 0))) " +
                    ":WITNESS (SYNTHETO::MAKE-EXPRESSION-LITERAL :GET (SYNTHETO::MAKE-LITERAL-INTEGER :VALUE 1)))";

    public static final String checkExpectedType1 = "(syntheto::type-subsetp " + expectedString1 + ")";

    public static final String expectedString2 =
            "(SYNTHETO::MAKE-TYPE-SUBSET " +
                    ":SUPERTYPE (SYNTHETO::MAKE-TYPE-INTEGER) " +
                    ":VARIABLE (SYNTHETO::MAKE-IDENTIFIER :NAME \"x\") " +
                    ":RESTRICTION (SYNTHETO::MAKE-EXPRESSION-BINARY :OPERATOR (SYNTHETO::MAKE-BINARY-OP-GT) " +
                    ":LEFT-OPERAND (SYNTHETO::MAKE-EXPRESSION-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"x\")) " +
                    ":RIGHT-OPERAND (SYNTHETO::MAKE-EXPRESSION-LITERAL :GET (SYNTHETO::MAKE-LITERAL-INTEGER :VALUE 0))) " +
                    ":WITNESS NIL)";

    public static final String checkExpectedType2 = "(syntheto::type-subsetp " + expectedString2 + ")";

    @Test
    void toSExpression() {
        // Make the type of positive integers.
        TypeSubset typeSubset = TypeSubset.make(
                TypeInteger.make(),  // the base type
                Identifier.make("x"), // the name of the free variable in the restriction
                ExpressionBinary.make(ExpressionBinary.Operator.GT,
                        Variable.make(Identifier.make("x")),
                        ExpressionLiteral.make(LiteralInteger.make(0))),
                ExpressionLiteral.make(LiteralInteger.make(1)));
        SExpression sExpr = typeSubset.toSExpression();
        assertEquals(expectedString1,
                sExpr.toString());

        TypeSubset rebuilt = (TypeSubset) ASTBuilder.fromSExpression(sExpr);
        assertEquals(typeSubset, rebuilt);

        Reader r = new StringReader(sExpr.toString());
        Parser p = new Parser(r);
        SExpression s = p.parseTop();
        assertEquals(sExpr, s);

        // Do the same thing without a witness
        TypeSubset typeSubset2 = TypeSubset.make(
                TypeInteger.make(),  // the base type
                Identifier.make("x"), // the name of the free variable in the restriction
                ExpressionBinary.make(ExpressionBinary.Operator.GT,
                        Variable.make(Identifier.make("x")),
                        ExpressionLiteral.make(LiteralInteger.make(0))),
                null);
        SExpression sExpr2 = typeSubset2.toSExpression();
        assertEquals(expectedString2,
                sExpr2.toString());

        TypeSubset rebuilt2 = (TypeSubset) ASTBuilder.fromSExpression(sExpr2);
        assertEquals(typeSubset2, rebuilt2);

        Reader r2 = new StringReader(sExpr2.toString());
        Parser p2 = new Parser(r2);
        SExpression s2 = p2.parseTop();
        assertEquals(sExpr2, s2);
    }
}