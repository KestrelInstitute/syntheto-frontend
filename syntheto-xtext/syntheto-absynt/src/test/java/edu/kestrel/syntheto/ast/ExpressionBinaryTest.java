/*
 * Copyright (C) 2020 Kestrel Institute (http://www.kestrel.edu)
 * License: 3-clause BSD license (https://opensource.org/licenses/BSD-3-Clause)
 * Main Author: Alessandro Coglio (coglio@kestrel.edu)
 * Contributing Author: Eric McCarthy (mccarthy@kestrel.edu)
 */

package edu.kestrel.syntheto.ast;

import edu.kestrel.syntheto.sexpr.*;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.io.StringReader;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

public class ExpressionBinaryTest {

    public static final String expectedString =
            "(SYNTHETO::MAKE-EXPRESSION-BINARY :OPERATOR (SYNTHETO::MAKE-BINARY-OP-LT) " +
            ":LEFT-OPERAND (SYNTHETO::MAKE-EXPRESSION-LITERAL :GET (SYNTHETO::MAKE-LITERAL-INTEGER :VALUE 1)) " +
            ":RIGHT-OPERAND (SYNTHETO::MAKE-EXPRESSION-LITERAL :GET (SYNTHETO::MAKE-LITERAL-INTEGER :VALUE 10)))";

    public static final ExpressionBinary expectedASTNode =
            ExpressionBinary.make(ExpressionBinary.Operator.LT,
                    ExpressionLiteral.make(LiteralInteger.make(BigInteger.ONE)),
                    ExpressionLiteral.make(LiteralInteger.make(BigInteger.TEN)));

    // TODO: it would be good to have alternative predicates automatically generated.  For now just checking expressionp.
    public static final String checkExpectedType = "(syntheto::expressionp " + expectedString + ")";

    @Test
    void toSExpression() {
        ExpressionBinary expressionBinary = expectedASTNode;
        SExpression sExpr = expressionBinary.toSExpression();
        assertEquals(expectedString,
                sExpr.toString());

        ExpressionBinary rebuilt = (ExpressionBinary) ASTBuilder.fromSExpression(sExpr);
        assertEquals(expressionBinary, rebuilt);

        Reader r = new StringReader(sExpr.toString());
        Parser p = new Parser(r);
        SExpression s = p.parseTop();
        assertEquals(sExpr, s);
    }
}