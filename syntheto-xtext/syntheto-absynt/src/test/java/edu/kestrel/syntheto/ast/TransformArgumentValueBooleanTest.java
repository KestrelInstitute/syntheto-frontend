/*
 * Copyright (C) 2021 Kestrel Institute (http://www.kestrel.edu)
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

public class TransformArgumentValueBooleanTest {

    public static final String expectedString1 =
            "(SYNTHETO::MAKE-TRANSFORM-ARGUMENT-VALUE-BOOL :VAL T)";

    public static final String checkExpectedType1 = "(syntheto::transform-argument-valuep " + expectedString1 + ")";

    public static final String expectedString2 =
            "(SYNTHETO::MAKE-TRANSFORM-ARGUMENT-VALUE-BOOL :VAL NIL)";

    public static final String checkExpectedType2 = "(syntheto::transform-argument-valuep " + expectedString2 + ")";

    @Test
    void toSExpression() {
        TransformArgumentValueBoolean tavb = TransformArgumentValueBoolean.make(true);
        SExpression sExpression = tavb.toSExpression();
        assertEquals(expectedString1, sExpression.toString());

        TransformArgumentValueBoolean rebuilt = (TransformArgumentValueBoolean) ASTBuilder.fromSExpression(sExpression);
        assertEquals(tavb, rebuilt);

        Reader r = new StringReader(sExpression.toString());
        Parser p = new Parser(r);
        SExpression s = p.parseTop();
        assertEquals(sExpression, s);

        TransformArgumentValueBoolean tavb2 = TransformArgumentValueBoolean.make(false);
        SExpression sExpression2 = tavb2.toSExpression();
        assertEquals(expectedString2, sExpression2.toString());

        TransformArgumentValueBoolean rebuilt2 = (TransformArgumentValueBoolean) ASTBuilder.fromSExpression(sExpression2);
        assertEquals(tavb2, rebuilt2);

        Reader r2 = new StringReader(sExpression2.toString());
        Parser p2 = new Parser(r2);
        SExpression s2 = p2.parseTop();
        assertEquals(sExpression2, s2);
    }
}