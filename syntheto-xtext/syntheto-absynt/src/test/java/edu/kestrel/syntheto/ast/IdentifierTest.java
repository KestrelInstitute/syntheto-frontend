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

public class IdentifierTest {

    public static final String expectedString =
            "(SYNTHETO::MAKE-IDENTIFIER :NAME \"abc_def\")";

    public static final String checkExpectedType = "(syntheto::identifierp " + expectedString + ")";

    @Test
    void toSExpression() {
        Identifier identifier = Identifier.make("abc_def");
        SExpression sExpr = identifier.toSExpression();
        assertEquals(expectedString, sExpr.toString());

        Identifier rebuilt = (Identifier) ASTBuilder.fromSExpression(sExpr);
        assertEquals(rebuilt, identifier);

        Reader r = new StringReader(sExpr.toString());
        Parser p = new Parser(r);
        SExpression s = p.parseTop();
        assertEquals(sExpr, s);
    }
}