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

import static org.junit.jupiter.api.Assertions.*;

public class QuantifierForallTest {

    public static final String expectedString =
            "(SYNTHETO::MAKE-QUANTIFIER-FORALL)";

    public static final String checkExpectedType = "(syntheto::quantifierp " + expectedString + ")";

    @Test
    void toSExpression() {
        QuantifierForall quantifierForall = QuantifierForall.make();
        SExpression sExpr = quantifierForall.toSExpression();
        assertEquals(expectedString, sExpr.toString());

        QuantifierForall rebuilt = (QuantifierForall) ASTBuilder.fromSExpression(sExpr);
        assertEquals(quantifierForall, rebuilt);

        Reader r = new StringReader(sExpr.toString());
        Parser p = new Parser(r);
        SExpression s = p.parseTop();
        assertEquals(sExpr, s);
    }
}