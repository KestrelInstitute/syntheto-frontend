package edu.kestrel.syntheto.ast;

import edu.kestrel.syntheto.sexpr.Parser;
import edu.kestrel.syntheto.sexpr.SExpression;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.io.StringReader;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

public class BranchTest {

    public static final String expectedString =
            "(SYNTHETO::MAKE-BRANCH " +
                    ":CONDITION (SYNTHETO::MAKE-EXPRESSION-BINARY :OPERATOR (SYNTHETO::MAKE-BINARY-OP-LT) " +
                    ":LEFT-OPERAND (SYNTHETO::MAKE-EXPRESSION-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"x\")) " +
                    ":RIGHT-OPERAND (SYNTHETO::MAKE-EXPRESSION-LITERAL :GET (SYNTHETO::MAKE-LITERAL-INTEGER :VALUE 0))) " +
                    ":ACTION (SYNTHETO::MAKE-EXPRESSION-BINARY :OPERATOR (SYNTHETO::MAKE-BINARY-OP-ADD) " +
                    ":LEFT-OPERAND (SYNTHETO::MAKE-EXPRESSION-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"x\")) " +
                    ":RIGHT-OPERAND (SYNTHETO::MAKE-EXPRESSION-LITERAL :GET (SYNTHETO::MAKE-LITERAL-INTEGER :VALUE 100))))";

    public static final String checkExpectedType = "(syntheto::branchp " + expectedString + ")";

    @Test
    void toSExpression() {
        // Test case is (condition) x < 0 (action) x + 100
        Branch exprBranch = Branch.make(
                ExpressionBinary.make(ExpressionBinary.Operator.LT, Variable.make(Identifier.make("x")), ExpressionLiteral.make(LiteralInteger.make(0))),
                ExpressionBinary.make(ExpressionBinary.Operator.ADD, Variable.make(Identifier.make("x")), ExpressionLiteral.make(LiteralInteger.make(100))));
        SExpression sExpr = exprBranch.toSExpression();
        assertEquals(expectedString,
                sExpr.toString());

        Branch rebuilt = (Branch) ASTBuilder.fromSExpression(sExpr);
        assertEquals(exprBranch, rebuilt);

        Reader r = new StringReader(sExpr.toString());
        Parser p = new Parser(r);
        SExpression s = p.parseTop();
        assertEquals(sExpr, s);
    }
}