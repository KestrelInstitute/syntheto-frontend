package edu.kestrel.syntheto.ast;

import edu.kestrel.syntheto.sexpr.Parser;
import edu.kestrel.syntheto.sexpr.SExpression;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FunctionSpecifierQuantifiedTest {

    public static final String expectedString =
            "(SYNTHETO::MAKE-FUNCTION-SPECIFIER-QUANTIFIED " +
                    ":QUANTIFIER (SYNTHETO::MAKE-QUANTIFIER-EXISTS) " +
                    ":VARIABLES (LIST (SYNTHETO::MAKE-TYPED-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"solution\") " +
                    ":TYPE (SYNTHETO::MAKE-TYPE-DEFINED :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"assignment\")))) " +
                    ":MATRIX (SYNTHETO::MAKE-EXPRESSION-CALL " +
                    ":FUNCTION (SYNTHETO::MAKE-IDENTIFIER :NAME \"solutionp\") " +
                    ":TYPES (LIST) " +
                    ":ARGUMENTS (LIST (SYNTHETO::MAKE-EXPRESSION-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"solution\")) " +
                    "(SYNTHETO::MAKE-EXPRESSION-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"missions\")) " +
                    "(SYNTHETO::MAKE-EXPRESSION-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"roster\")))))";

    public static final String checkExpectedType = "(syntheto::function-specifierp " + expectedString + ")";

    @Test
    void toSExpression() {
        // Example doesn't try to make sense.  Just something to make into a FunctionSpecifierQuantified

        Quantifier quant = QuantifierExists.make();
        TypedVariable solution = TypedVariable.make(Identifier.make("solution"), TypeDefined.make(Identifier.make("assignment")));
        List<TypedVariable> vars = Arrays.asList(solution);
        // since solution is already restricted to be of type "assignment", we don't need the conjunct (assignment-p solution)
        Expression matrix = ExpressionCall.make(Identifier.make("solutionp"),
                Arrays.asList(Variable.make(Identifier.make("solution")),
                        Variable.make(Identifier.make("missions")),
                        Variable.make(Identifier.make("roster"))));
        FunctionSpecifierQuantified definer = FunctionSpecifierQuantified.make(quant, vars, matrix);

        SExpression sExpr = definer.toSExpression();
        assertEquals(expectedString,
                    sExpr.toString());

        FunctionSpecifierQuantified rebuilt = (FunctionSpecifierQuantified) ASTBuilder.fromSExpression(sExpr);
        assertEquals(definer, rebuilt);

        Reader r = new StringReader(sExpr.toString());
        Parser p = new Parser(r);
        SExpression s = p.parseTop();
        assertEquals(sExpr, s);
    }
}
