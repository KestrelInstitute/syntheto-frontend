package edu.kestrel.syntheto.ast;

import edu.kestrel.syntheto.sexpr.Parser;
import edu.kestrel.syntheto.sexpr.SExpression;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TypeDefinerSubsetTest {

    public static final String expectedString1 =
            "(SYNTHETO::MAKE-TYPE-DEFINER-SUBSET :GET (SYNTHETO::MAKE-TYPE-SUBSET " +
                    ":SUPERTYPE (SYNTHETO::MAKE-TYPE-INTEGER) " +
                    ":VARIABLE (SYNTHETO::MAKE-IDENTIFIER :NAME \"x\") " +
                    ":RESTRICTION (SYNTHETO::MAKE-EXPRESSION-BINARY :OPERATOR (SYNTHETO::MAKE-BINARY-OP-GT) " +
                    ":LEFT-OPERAND (SYNTHETO::MAKE-EXPRESSION-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"x\")) " +
                    ":RIGHT-OPERAND (SYNTHETO::MAKE-EXPRESSION-LITERAL :GET (SYNTHETO::MAKE-LITERAL-INTEGER :VALUE 0))) " +
                    ":WITNESS (SYNTHETO::MAKE-EXPRESSION-LITERAL :GET (SYNTHETO::MAKE-LITERAL-INTEGER :VALUE 1))))";

    public static final String checkExpectedType1 = "(syntheto::type-definerp " + expectedString1 + ")";

    public static final String expectedString2 =
            "(SYNTHETO::MAKE-TYPE-DEFINER-SUBSET :GET (SYNTHETO::MAKE-TYPE-SUBSET " +
                    ":SUPERTYPE (SYNTHETO::MAKE-TYPE-INTEGER) " +
                    ":VARIABLE (SYNTHETO::MAKE-IDENTIFIER :NAME \"x\") " +
                    ":RESTRICTION (SYNTHETO::MAKE-EXPRESSION-BINARY :OPERATOR (SYNTHETO::MAKE-BINARY-OP-GT) " +
                    ":LEFT-OPERAND (SYNTHETO::MAKE-EXPRESSION-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"x\")) " +
                    ":RIGHT-OPERAND (SYNTHETO::MAKE-EXPRESSION-LITERAL :GET (SYNTHETO::MAKE-LITERAL-INTEGER :VALUE 0))) " +
                    ":WITNESS NIL))";

    public static final String checkExpectedType2 = "(syntheto::type-definerp " + expectedString2 + ")";

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
        TypeDefinerSubset tdsub = TypeDefinerSubset.make(typeSubset);
        SExpression sExpr = tdsub.toSExpression();
        assertEquals(expectedString1,
                sExpr.toString());

        TypeDefinerSubset rebuilt = (TypeDefinerSubset) ASTBuilder.fromSExpression(sExpr);
        assertEquals(tdsub, rebuilt);

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
        TypeDefinerSubset tdsub2 = TypeDefinerSubset.make(typeSubset2);
        SExpression sExpr2 = tdsub2.toSExpression();
        assertEquals(expectedString2,
                sExpr2.toString());

        TypeDefinerSubset rebuilt2 = (TypeDefinerSubset) ASTBuilder.fromSExpression(sExpr2);
        assertEquals(tdsub2, rebuilt2);

        Reader r2 = new StringReader(sExpr2.toString());
        Parser p2 = new Parser(r2);
        SExpression s2 = p2.parseTop();
        assertEquals(sExpr2, s2);
    }
}