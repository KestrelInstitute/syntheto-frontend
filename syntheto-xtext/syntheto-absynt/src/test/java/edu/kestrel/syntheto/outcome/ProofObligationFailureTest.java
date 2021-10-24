/*
 * Copyright (C) 2021 Kestrel Institute (http://www.kestrel.edu)
 * License: 3-clause BSD license (https://opensource.org/licenses/BSD-3-Clause)
 * Main Author: Alessandro Coglio (coglio@kestrel.edu)
 * Contributing Author: Eric McCarthy (mccarthy@kestrel.edu)
 */

package edu.kestrel.syntheto.outcome;

import edu.kestrel.syntheto.ast.*;
import edu.kestrel.syntheto.sexpr.Parser;
import edu.kestrel.syntheto.sexpr.SExpression;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ProofObligationFailureTest {

    public static final String expectedString =
            "(SYNTHETO::MAKE-OUTCOME-PROOF-OBLIGATION-FAILURE :MESSAGE \"could not prove this\" :OBLIGATION-EXPR " +
                    "(SYNTHETO::MAKE-EXPRESSION-CALL " +
                    ":FUNCTION (SYNTHETO::MAKE-IDENTIFIER :NAME \"myfun\") " +
                    ":TYPES (LIST) " +
                    ":ARGUMENTS (LIST (SYNTHETO::MAKE-EXPRESSION-LITERAL :GET (SYNTHETO::MAKE-LITERAL-INTEGER :VALUE 1))))" +
                    ")";

    public static final ProofObligationFailure expectedNode = makeNode();

    private static ProofObligationFailure makeNode() {
        // make the expr (as in ExpressionCallTest.java)
        List<Expression> args = new ArrayList<>();
        args.add(ExpressionLiteral.make(LiteralInteger.make(1)));
        ExpressionCall exprCall = ExpressionCall.make(Identifier.make("myfun"),
                args);

        return ProofObligationFailure.make("could not prove this",
                exprCall);
    }

    public static final String checkExpectedType = "(let ((o " + expectedString + ")) (and (syntheto::outcomep o) (syntheto::outcome-case o :proof-obligation-failure)))";

    @Test
    void toSExpression() {

        ProofObligationFailure proofObligationFailure = expectedNode;
        SExpression sExpression = proofObligationFailure.toSExpression();
        assertEquals(sExpression.toString(),
                expectedString);

        ProofObligationFailure rebuilt = (ProofObligationFailure) OutcomeBuilder.fromSExpression(sExpression);
        assertEquals(proofObligationFailure, rebuilt);

        Reader r = new StringReader(sExpression.toString());
        Parser p = new Parser(r);
        SExpression s = p.parseTop();
        assertEquals(sExpression, s);
    }
}