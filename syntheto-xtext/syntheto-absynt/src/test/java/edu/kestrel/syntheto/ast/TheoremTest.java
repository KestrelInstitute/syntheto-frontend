package edu.kestrel.syntheto.ast;

import edu.kestrel.syntheto.sexpr.Parser;
import edu.kestrel.syntheto.sexpr.SExpression;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TheoremTest {

    public static final String expectedString =
            "(SYNTHETO::MAKE-THEOREM " +
                    ":NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"mul0is0\") " +
                    ":VARIABLES (LIST (SYNTHETO::MAKE-TYPED-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"x\") :TYPE (SYNTHETO::MAKE-TYPE-INTEGER))) " +
                    ":FORMULA (SYNTHETO::MAKE-EXPRESSION-BINARY :OPERATOR (SYNTHETO::MAKE-BINARY-OP-EQ) " +
                    ":LEFT-OPERAND (SYNTHETO::MAKE-EXPRESSION-BINARY :OPERATOR (SYNTHETO::MAKE-BINARY-OP-MUL) " +
                    ":LEFT-OPERAND (SYNTHETO::MAKE-EXPRESSION-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"x\")) " +
                    ":RIGHT-OPERAND (SYNTHETO::MAKE-EXPRESSION-LITERAL :GET (SYNTHETO::MAKE-LITERAL-INTEGER :VALUE 0))) " +
                    ":RIGHT-OPERAND (SYNTHETO::MAKE-EXPRESSION-LITERAL :GET (SYNTHETO::MAKE-LITERAL-INTEGER :VALUE 0))))";

    public static final String checkExpectedType = "(syntheto::theoremp " + expectedString + ")";

    @Test
    void toSExpression() {
        // example is:  mul0is0 (fa x: int) x*0 == 0
        List<TypedVariable> vars = new ArrayList<>();
        vars.add(TypedVariable.make(Identifier.make("x"), TypeInteger.make()));
        Expression theoremExpr = ExpressionBinary.make(ExpressionBinary.Operator.EQ,
                ExpressionBinary.make(ExpressionBinary.Operator.MUL, Variable.make(Identifier.make("x")), ExpressionLiteral.make(LiteralInteger.make(0))),
                ExpressionLiteral.make(LiteralInteger.make(0)));
        Theorem theorem = Theorem.make(Identifier.make("mul0is0"), vars, theoremExpr);
        SExpression sExpr = theorem.toSExpression();
        assertEquals(expectedString,
                sExpr.toString());

        Theorem rebuilt = (Theorem) ASTBuilder.fromSExpression(sExpr);
        assertEquals(theorem, rebuilt);

        Reader r = new StringReader(sExpr.toString());
        Parser p = new Parser(r);
        SExpression s = p.parseTop();
        assertEquals(sExpr, s);
    }

}
