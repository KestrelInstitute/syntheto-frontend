/*
 * Copyright (C) 2020 Kestrel Institute (http://www.kestrel.edu)
 * License: 3-clause BSD license (https://opensource.org/licenses/BSD-3-Clause)
 * Main Author: Alessandro Coglio (coglio@kestrel.edu)
 * Contributing Author: Eric McCarthy (mccarthy@kestrel.edu)
 */

package edu.kestrel.syntheto.ast;

import edu.kestrel.syntheto.sexpr.Parser;
import edu.kestrel.syntheto.sexpr.SExpression;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.io.StringReader;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

public class ExpressionUnaryTest {

    public static final String expectedString =
            "(SYNTHETO::MAKE-EXPRESSION-UNARY :OPERATOR (SYNTHETO::MAKE-UNARY-OP-MINUS) " +
                    ":OPERAND (SYNTHETO::MAKE-EXPRESSION-LITERAL :GET (SYNTHETO::MAKE-LITERAL-INTEGER :VALUE 1)))";

    public static final ExpressionUnary expectedASTNode =
            ExpressionUnary.make(ExpressionUnary.Operator.MINUS,
                    ExpressionLiteral.make(LiteralInteger.make(BigInteger.ONE)));

    // TODO: it would be good to have alternatve predicates automatically generated.  For now just checking expressionp.
    public static final String checkExpectedType = "(syntheto::expressionp " + expectedString + ")";

    @Test
    void toSExpression() {
        ExpressionUnary expressionUnary = expectedASTNode;
        SExpression sExpr = expressionUnary.toSExpression();
        assertEquals(expectedString,
                sExpr.toString());

        ExpressionUnary rebuilt = (ExpressionUnary) ASTBuilder.fromSExpression(sExpr);
        assertEquals(expressionUnary, rebuilt);

        Reader r = new StringReader(sExpr.toString());
        Parser p = new Parser(r);
        SExpression s = p.parseTop();
        assertEquals(sExpr, s);
    }
}