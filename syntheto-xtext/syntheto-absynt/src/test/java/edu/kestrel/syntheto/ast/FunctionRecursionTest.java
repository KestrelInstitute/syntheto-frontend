package edu.kestrel.syntheto.ast;

import edu.kestrel.syntheto.sexpr.Parser;
import edu.kestrel.syntheto.sexpr.SExpression;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FunctionRecursionTest {

    public static final String expectedString =
            "(SYNTHETO::MAKE-FUNCTION-RECURSION :DEFINITIONS (LIST " +
                    "(SYNTHETO::MAKE-FUNCTION-DEFINITION :HEADER " +
                    "(SYNTHETO::MAKE-FUNCTION-HEADER :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"F\") "+
                    ":INPUTS (LIST (SYNTHETO::MAKE-TYPED-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"x\") :TYPE (SYNTHETO::MAKE-TYPE-INTEGER)) " +
                    "(SYNTHETO::MAKE-TYPED-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"y\") :TYPE (SYNTHETO::MAKE-TYPE-INTEGER))) " +
                    ":OUTPUTS (LIST (SYNTHETO::MAKE-TYPED-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"output_y\") :TYPE (SYNTHETO::MAKE-TYPE-INTEGER)) " +
                    "(SYNTHETO::MAKE-TYPED-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"output_x\") :TYPE (SYNTHETO::MAKE-TYPE-INTEGER)))) " +
                    ":PRECONDITION NIL :POSTCONDITION NIL :DEFINER " +
                    "(SYNTHETO::MAKE-FUNCTION-DEFINER-REGULAR " +
                    ":BODY (SYNTHETO::MAKE-EXPRESSION-MULTI :ARGUMENTS (LIST " +
                    "(SYNTHETO::MAKE-EXPRESSION-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"y\")) " +
                    "(SYNTHETO::MAKE-EXPRESSION-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"x\")))) " +
                    ":MEASURE NIL)) " +


                    "(SYNTHETO::MAKE-FUNCTION-DEFINITION :HEADER " +
                    "(SYNTHETO::MAKE-FUNCTION-HEADER :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"solvablep\") " +
                    ":INPUTS (LIST (SYNTHETO::MAKE-TYPED-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"missions\") :TYPE (SYNTHETO::MAKE-TYPE-SEQUENCE :ELEMENT (SYNTHETO::MAKE-TYPE-DEFINED :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"mission\")))) " +
                    "(SYNTHETO::MAKE-TYPED-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"roster\") :TYPE (SYNTHETO::MAKE-TYPE-DEFINED :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"roster\")))) " +
                    ":OUTPUTS (LIST (SYNTHETO::MAKE-TYPED-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"yes_no\") :TYPE (SYNTHETO::MAKE-TYPE-BOOLEAN)))) " +
                    ":PRECONDITION NIL :POSTCONDITION NIL :DEFINER " +

                    "(SYNTHETO::MAKE-FUNCTION-DEFINER-QUANTIFIED " +
                    ":QUANTIFIER (SYNTHETO::MAKE-QUANTIFIER-EXISTS) " +
                    ":VARIABLES (LIST (SYNTHETO::MAKE-TYPED-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"solution\") " +
                    ":TYPE (SYNTHETO::MAKE-TYPE-DEFINED :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"assignment\")))) " +
                    ":MATRIX (SYNTHETO::MAKE-EXPRESSION-CALL " +
                    ":FUNCTION (SYNTHETO::MAKE-IDENTIFIER :NAME \"solutionp\") " +
                    ":TYPES (LIST) " +
                    ":ARGUMENTS (LIST (SYNTHETO::MAKE-EXPRESSION-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"solution\")) " +
                    "(SYNTHETO::MAKE-EXPRESSION-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"missions\")) " +
                    "(SYNTHETO::MAKE-EXPRESSION-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"roster\"))))))))";

    public static final String checkExpectedType = "(syntheto::function-recursionp " + expectedString + ")";

    @Test
    void toSExpression() {
        // TODO: make a real mutually-recursive definitions test.  This one just groups two functiondefs together.

        // First function, from FunctionDefinitionTest
        // Example F(x:int,y:int)->(y:int,x:int)
        Identifier fnname = Identifier.make("F");
        TypedVariable x = TypedVariable.make(Identifier.make("x"), TypeInteger.make());
        TypedVariable y = TypedVariable.make(Identifier.make("y"), TypeInteger.make());
        List<TypedVariable> args = Arrays.asList(x, y);
        TypedVariable output_y = TypedVariable.make(Identifier.make("output_y"), TypeInteger.make());
        TypedVariable output_x = TypedVariable.make(Identifier.make("output_x"), TypeInteger.make());
        List<TypedVariable> output_args = Arrays.asList(output_y, output_x);
        FunctionHeader funhead = FunctionHeader.make(fnname, args, output_args);

        // TODO: also test non-null precondition and postcondition

        Variable vy = Variable.make(Identifier.make("y"));
        Variable vx = Variable.make(Identifier.make("x"));
        List<Expression> vals = Arrays.asList(vy, vx);
        ExpressionMulti retval = ExpressionMulti.make(vals);
        FunctionDefinerRegular funDefiner = FunctionDefinerRegular.make(retval, null);

        FunctionDefinition def = FunctionDefinition.make(funhead, null, null, funDefiner);

        // Second function, built from FunctionDefinerQuantifiedTest.
        // Make something similar to the first define-sk in spec-v0.lisp
        Quantifier quant = QuantifierExists.make();
        TypedVariable solution = TypedVariable.make(Identifier.make("solution"), TypeDefined.make(Identifier.make("assignment")));
        List<TypedVariable> vars = Arrays.asList(solution);
        // since solution is already restricted to be of type "assignment", we don't need the conjunct (assignment-p solution)
        Expression matrix = ExpressionCall.make(Identifier.make("solutionp"),
                Arrays.asList(Variable.make(Identifier.make("solution")),
                        Variable.make(Identifier.make("missions")),
                        Variable.make(Identifier.make("roster"))));
        FunctionDefinerQuantified qdefiner = FunctionDefinerQuantified.make(quant, vars, matrix);
        // Build second function definition
        Identifier fnname2 = Identifier.make("solvablep");
        TypedVariable qarg1 = TypedVariable.make(Identifier.make("missions"), TypeSequence.make(TypeDefined.make(Identifier.make("mission"))));
        TypedVariable qarg2 = TypedVariable.make(Identifier.make("roster"), TypeDefined.make(Identifier.make("roster")));
        List<TypedVariable> input_args2 = Arrays.asList(qarg1, qarg2);
        TypedVariable qret1 = TypedVariable.make(Identifier.make("yes_no"), TypeBoolean.make());
        List<TypedVariable> output_args2 = Arrays.asList(qret1);
        FunctionHeader funhead2 = FunctionHeader.make(fnname2, input_args2, output_args2);
        FunctionDefinition def2 = FunctionDefinition.make(funhead2, null, null, qdefiner);

        // put the two together
        List<FunctionDefinition> defs = Arrays.asList(def, def2);
        FunctionRecursion recur = FunctionRecursion.make(defs);
        SExpression sExpr = recur.toSExpression();
        assertEquals(expectedString,
                sExpr.toString());

        FunctionRecursion rebuilt = (FunctionRecursion) ASTBuilder.fromSExpression(sExpr);
        assertEquals(recur, rebuilt);

        Reader r = new StringReader(sExpr.toString());
        Parser p = new Parser(r);
        SExpression s = p.parseTop();
        assertEquals(sExpr, s);

    }
}