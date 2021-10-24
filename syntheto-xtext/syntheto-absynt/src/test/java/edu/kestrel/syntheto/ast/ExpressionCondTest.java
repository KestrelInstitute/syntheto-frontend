package edu.kestrel.syntheto.ast;

import edu.kestrel.syntheto.sexpr.Parser;
import edu.kestrel.syntheto.sexpr.SExpression;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ExpressionCondTest {

    public static final String expectedString =
            "(SYNTHETO::MAKE-EXPRESSION-COND " +
                    ":BRANCHES (LIST " +
                    "(SYNTHETO::MAKE-BRANCH " +
                    ":CONDITION (SYNTHETO::MAKE-EXPRESSION-BINARY :OPERATOR (SYNTHETO::MAKE-BINARY-OP-LT) " +
                    ":LEFT-OPERAND (SYNTHETO::MAKE-EXPRESSION-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"x\")) " +
                    ":RIGHT-OPERAND (SYNTHETO::MAKE-EXPRESSION-LITERAL :GET (SYNTHETO::MAKE-LITERAL-INTEGER :VALUE 0))) " +
                    ":ACTION (SYNTHETO::MAKE-EXPRESSION-BINARY :OPERATOR (SYNTHETO::MAKE-BINARY-OP-ADD) " +
                    ":LEFT-OPERAND (SYNTHETO::MAKE-EXPRESSION-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"x\")) " +
                    ":RIGHT-OPERAND (SYNTHETO::MAKE-EXPRESSION-LITERAL :GET (SYNTHETO::MAKE-LITERAL-INTEGER :VALUE 1)))) " +
                    "(SYNTHETO::MAKE-BRANCH " +
                    ":CONDITION (SYNTHETO::MAKE-EXPRESSION-BINARY :OPERATOR (SYNTHETO::MAKE-BINARY-OP-GT) " +
                    ":LEFT-OPERAND (SYNTHETO::MAKE-EXPRESSION-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"x\")) " +
                    ":RIGHT-OPERAND (SYNTHETO::MAKE-EXPRESSION-LITERAL :GET (SYNTHETO::MAKE-LITERAL-INTEGER :VALUE 0))) " +
                    ":ACTION (SYNTHETO::MAKE-EXPRESSION-BINARY :OPERATOR (SYNTHETO::MAKE-BINARY-OP-SUB) " +
                    ":LEFT-OPERAND (SYNTHETO::MAKE-EXPRESSION-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"x\")) " +
                    ":RIGHT-OPERAND (SYNTHETO::MAKE-EXPRESSION-LITERAL :GET (SYNTHETO::MAKE-LITERAL-INTEGER :VALUE 1))))))";

    public static final ExpressionCond expectedASTNode = makeNode();

    private static ExpressionCond makeNode() {
        List<Branch> branches = new ArrayList<>();
        // Test case is (condition) x < 0 (action) x + 1
        Branch exprBranch1 = Branch.make(
                ExpressionBinary.make(ExpressionBinary.Operator.LT, Variable.make(Identifier.make("x")), ExpressionLiteral.make(LiteralInteger.make(0))),
                ExpressionBinary.make(ExpressionBinary.Operator.ADD, Variable.make(Identifier.make("x")), ExpressionLiteral.make(LiteralInteger.make(1))));
        branches.add(exprBranch1);
        // Test case is (condition) x > 0 (action) x - 1
        Branch exprBranch2 = Branch.make(
                ExpressionBinary.make(ExpressionBinary.Operator.GT, Variable.make(Identifier.make("x")), ExpressionLiteral.make(LiteralInteger.make(0))),
                ExpressionBinary.make(ExpressionBinary.Operator.SUB, Variable.make(Identifier.make("x")), ExpressionLiteral.make(LiteralInteger.make(1))));
        branches.add(exprBranch2);
        return ExpressionCond.make(branches);
    }

    // TODO: it would be good to have alternatve predicates automatically generated.  For now just checking expressionp.
    public static final String checkExpectedType = "(syntheto::expressionp " + expectedString + ")";

    @Test
    void toSExpression() {
        ExpressionCond exprCond = expectedASTNode;
        SExpression sExpr = exprCond.toSExpression();
        assertEquals(expectedString,
                sExpr.toString());

        ExpressionCond rebuilt = (ExpressionCond) ASTBuilder.fromSExpression(sExpr);
        assertEquals(exprCond, rebuilt);

        Reader r = new StringReader(sExpr.toString());
        Parser p = new Parser(r);
        SExpression s = p.parseTop();
        assertEquals(sExpr, s);
    }

}
