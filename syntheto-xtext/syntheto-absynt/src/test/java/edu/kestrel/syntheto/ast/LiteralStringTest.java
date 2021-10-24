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

public class LiteralStringTest {

    public static final String expectedString =
            "(SYNTHETO::MAKE-LITERAL-STRING :VALUE \"abcd\")";

    public static final String checkExpectedType = "(syntheto::literalp " + expectedString + ")";

    @Test
    void toSExpression() {
        LiteralString stringLiteral = LiteralString.make("abcd");
        SExpression sExpression = stringLiteral.toSExpression();
        assertEquals(sExpression.toString(),
                expectedString);

        LiteralString rebuilt = (LiteralString) ASTBuilder.fromSExpression(sExpression);
        assertEquals(stringLiteral, rebuilt);

        Reader r = new StringReader(sExpression.toString());
        Parser p = new Parser(r);
        SExpression s = p.parseTop();
        assertEquals(sExpression, s);
    }
}