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

public class TransformArgumentValueIdentifierTest {

    public static final String expectedString =
            "(SYNTHETO::MAKE-TRANSFORM-ARGUMENT-VALUE-IDENTIFIER :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"t2\"))";

    public static final TransformArgumentValueIdentifier expectedASTNode = makeNode();

    private static TransformArgumentValueIdentifier makeNode() {
        Identifier identifier = Identifier.make("t2");
        return TransformArgumentValueIdentifier.make(identifier);
    }

    public static final String checkExpectedType = "(syntheto::transform-argument-valuep " + expectedString + ")";

    @Test
    void toSExpression() {
        TransformArgumentValueIdentifier tavi = expectedASTNode;
        SExpression sExpr = tavi.toSExpression();
        assertEquals(expectedString, sExpr.toString());

        TransformArgumentValueIdentifier rebuilt = (TransformArgumentValueIdentifier) ASTBuilder.fromSExpression(sExpr);
        assertEquals(tavi, rebuilt);

        Reader r = new StringReader(sExpr.toString());
        Parser p = new Parser(r);
        SExpression s = p.parseTop();
        assertEquals(sExpr, s);
    }
}