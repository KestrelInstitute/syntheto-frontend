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
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TransformArgumentValueIdentifiersTest {

    public static final String expectedString =
            "(SYNTHETO::MAKE-TRANSFORM-ARGUMENT-VALUE-IDENTIFIERS :IDENTIFIER-LIST (LIST " +
                    "(SYNTHETO::MAKE-IDENTIFIER :NAME \"r\") " +
                    "(SYNTHETO::MAKE-IDENTIFIER :NAME \"s\")))";

    public static final TransformArgumentValueIdentifiers expectedASTNode = makeNode();

    private static TransformArgumentValueIdentifiers makeNode() {
        List<Identifier> ids = new ArrayList<>();
        ids.add(Identifier.make("r"));
        ids.add(Identifier.make("s"));
        return TransformArgumentValueIdentifiers.make(ids);
    }

    // TODO: it would be good to have alternative predicates automatically generated.  For now just checking expressionp.
    public static final String checkExpectedType = "(syntheto::transform-argument-valuep " + expectedString + ")";

    @Test
    void toSExpression() {
        TransformArgumentValueIdentifiers tavis = expectedASTNode;
        SExpression sExpr = tavis.toSExpression();
        assertEquals(expectedString,
                sExpr.toString());

        TransformArgumentValueIdentifiers rebuilt = (TransformArgumentValueIdentifiers) ASTBuilder.fromSExpression(sExpr);
        assertEquals(tavis, rebuilt);

        Reader r = new StringReader(sExpr.toString());
        Parser p = new Parser(r);
        SExpression s = p.parseTop();
        assertEquals(sExpr, s);
    }

}
