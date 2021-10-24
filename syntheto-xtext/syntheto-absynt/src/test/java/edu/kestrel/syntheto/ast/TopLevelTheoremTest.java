package edu.kestrel.syntheto.ast;

import edu.kestrel.syntheto.sexpr.Parser;
import edu.kestrel.syntheto.sexpr.SExpression;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TopLevelTheoremTest {

    public static final String expectedString =
            "(SYNTHETO::MAKE-TOPLEVEL-THEOREM :GET " +
                    "(SYNTHETO::MAKE-THEOREM " +
                    ":NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"mul0is0\") " +
                    ":VARIABLES (LIST (SYNTHETO::MAKE-TYPED-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"x\") :TYPE (SYNTHETO::MAKE-TYPE-INTEGER))) " +
                    ":FORMULA (SYNTHETO::MAKE-EXPRESSION-BINARY :OPERATOR (SYNTHETO::MAKE-BINARY-OP-EQ) " +
                    ":LEFT-OPERAND (SYNTHETO::MAKE-EXPRESSION-BINARY :OPERATOR (SYNTHETO::MAKE-BINARY-OP-MUL) " +
                    ":LEFT-OPERAND (SYNTHETO::MAKE-EXPRESSION-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"x\")) " +
                    ":RIGHT-OPERAND (SYNTHETO::MAKE-EXPRESSION-LITERAL :GET (SYNTHETO::MAKE-LITERAL-INTEGER :VALUE 0))) " +
                    ":RIGHT-OPERAND (SYNTHETO::MAKE-EXPRESSION-LITERAL :GET (SYNTHETO::MAKE-LITERAL-INTEGER :VALUE 0)))))";

    public static final String checkExpectedType = "(syntheto::toplevelp " + expectedString + ")";

    @Test
    void toSExpression() {
        // example is:  mul0is0 (fa x: int) x*0 == 0
        List<TypedVariable> vars = new ArrayList<>();
        vars.add(TypedVariable.make(Identifier.make("x"), TypeInteger.make()));
        Expression theoremExpr = ExpressionBinary.make(ExpressionBinary.Operator.EQ,
                ExpressionBinary.make(ExpressionBinary.Operator.MUL, Variable.make(Identifier.make("x")), ExpressionLiteral.make(LiteralInteger.make(0))),
                ExpressionLiteral.make(LiteralInteger.make(0)));
        Theorem theorem = Theorem.make(Identifier.make("mul0is0"), vars, theoremExpr);
        TopLevelTheorem tlt = TopLevelTheorem.make(theorem);
        SExpression sExpr = tlt.toSExpression();
        assertEquals(expectedString,
                sExpr.toString());

        TopLevelTheorem rebuilt = (TopLevelTheorem) ASTBuilder.fromSExpression(sExpr);
        assertEquals(tlt, rebuilt);

        Reader r = new StringReader(sExpr.toString());
        Parser p = new Parser(r);
        SExpression s = p.parseTop();
        assertEquals(sExpr, s);
    }

    public static final String expectedString2 =
            "(SYNTHETO::MAKE-TOPLEVEL-THEOREM :GET (SYNTHETO::MAKE-THEOREM :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"factorial_TO_factorial_t\") :VARIABLES (LIST (SYNTHETO::MAKE-TYPED-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"x\") :TYPE (SYNTHETO::MAKE-TYPE-INTEGER))) :FORMULA (SYNTHETO::MAKE-EXPRESSION-BINARY :OPERATOR (SYNTHETO::MAKE-BINARY-OP-EQ) :LEFT-OPERAND (SYNTHETO::MAKE-EXPRESSION-CALL :FUNCTION (SYNTHETO::MAKE-IDENTIFIER :NAME \"factorial\") :TYPES (LIST) :ARGUMENTS (LIST (SYNTHETO::MAKE-EXPRESSION-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"x\")))) :RIGHT-OPERAND (SYNTHETO::MAKE-EXPRESSION-CALL :FUNCTION (SYNTHETO::MAKE-IDENTIFIER :NAME \"factorial_t\") :TYPES (LIST) :ARGUMENTS (LIST (SYNTHETO::MAKE-EXPRESSION-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"x\")) (SYNTHETO::MAKE-EXPRESSION-LITERAL :GET (SYNTHETO::MAKE-LITERAL-INTEGER :VALUE 1)))))))";
    // Extra test from the example that did not work for Daniel, but with the identifier name fixed.
    @Test
    void toSExpression2() {
        Reader r = new StringReader(expectedString2);
        Parser p = new Parser(r);
        SExpression sExpr2 = p.parseTop();
        Object rebuilt2 = ASTBuilder.fromSExpression(sExpr2);
        // just check that we got the right class (and that parsing worked)
        assertTrue(rebuilt2 instanceof TopLevelTheorem);


    }

}
