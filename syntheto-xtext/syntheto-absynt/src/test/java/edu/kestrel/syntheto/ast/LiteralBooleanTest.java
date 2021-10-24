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

public class LiteralBooleanTest {

    public static final String expectedString1 =
            "(SYNTHETO::MAKE-LITERAL-BOOLEAN :VALUE T)";

    public static final String checkExpectedType1 = "(syntheto::literalp " + expectedString1 + ")";

    public static final String expectedString2 =
            "(SYNTHETO::MAKE-LITERAL-BOOLEAN :VALUE NIL)";

    public static final String checkExpectedType2 = "(syntheto::literalp " + expectedString2 + ")";

    @Test
    void toSExpression() {
        LiteralBoolean booleanLiteral = LiteralBoolean.make(true);
        SExpression sExpression = booleanLiteral.toSExpression();
        assertEquals(expectedString1, sExpression.toString());

        LiteralBoolean rebuilt = (LiteralBoolean) ASTBuilder.fromSExpression(sExpression);
        assertEquals(booleanLiteral, rebuilt);

        Reader r = new StringReader(sExpression.toString());
        Parser p = new Parser(r);
        SExpression s = p.parseTop();
        assertEquals(sExpression, s);

        LiteralBoolean booleanLiteral2 = LiteralBoolean.make(false);
        SExpression sExpression2 = booleanLiteral2.toSExpression();
        assertEquals(expectedString2, sExpression2.toString());

        LiteralBoolean rebuilt2 = (LiteralBoolean) ASTBuilder.fromSExpression(sExpression2);
        assertEquals(booleanLiteral2, rebuilt2);

        Reader r2 = new StringReader(sExpression2.toString());
        Parser p2 = new Parser(r2);
        SExpression s2 = p2.parseTop();
        assertEquals(sExpression2, s2);
    }
}