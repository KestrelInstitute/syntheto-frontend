/*
 * Copyright (C) 2021 Kestrel Institute (http://www.kestrel.edu)
 * License: 3-clause BSD license (https://opensource.org/licenses/BSD-3-Clause)
 * Main Author: Alessandro Coglio (coglio@kestrel.edu)
 * Contributing Author: Eric McCarthy (mccarthy@kestrel.edu)
 */
package edu.kestrel.syntheto.ast;

import edu.kestrel.syntheto.sexpr.Parser;
import edu.kestrel.syntheto.sexpr.SExpression;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class TopLevelTransformTest {

    public static final String expectedString =
            "(SYNTHETO::MAKE-TOPLEVEL-TRANSFORM :GET " +
                    "(SYNTHETO::MAKE-TRANSFORM " +
                    ":NEW-FUNCTION-NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"factorial_t\") " +
                    ":OLD-FUNCTION-NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"factorial\") " +
                    ":TRANSFORM-NAME \"tail_recursion\" " +
                    ":ARGUMENTS (LIST (SYNTHETO::MAKE-TRANSFORM-ARGUMENT " +
                    ":NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"new_parameter_name\") " +
                    ":VALUE (SYNTHETO::MAKE-TRANSFORM-ARGUMENT-VALUE-IDENTIFIER :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"r\"))))))";

    static TopLevelTransform makeExpectedASTNode() {
        // Example from Syntheto examples/fact.lisp
        Identifier newfnname = Identifier.make("factorial_t");
        Identifier oldfnname = Identifier.make("factorial");
        String tname = "tail_recursion";
        // Make the TransformArgument
        Identifier argname = Identifier.make("new_parameter_name");
        TransformArgumentValue val = TransformArgumentValueIdentifier.make(Identifier.make("r"));
        TransformArgument nameAndVal = TransformArgument.make(argname, val);
        List<TransformArgument> args = Collections.singletonList(nameAndVal);
        // Make the Transform
        Transform trans = Transform.make(newfnname, oldfnname, tname, args);
        // Make the TopLevelTransform
        return TopLevelTransform.make(trans);
    }

    public static final TopLevelTransform expectedASTNode =
            makeExpectedASTNode();

    public static final String checkExpectedType = "(syntheto::toplevelp " + expectedString + ")";

    @Test
    void toSExpression() {
        TopLevelTransform top = expectedASTNode;
        SExpression sExpression = top.toSExpression();
        assertEquals(expectedString,
                sExpression.toString());

        TopLevelTransform rebuilt = (TopLevelTransform) ASTBuilder.fromSExpression(sExpression);
        assertEquals(top, rebuilt);

        Reader r = new StringReader(sExpression.toString());
        Parser p = new Parser(r);
        SExpression s = p.parseTop();
        assertEquals(sExpression, s);
    }
}