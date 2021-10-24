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

public class TransformArgumentTest {

    public static final String expectedString =
    "(SYNTHETO::MAKE-TRANSFORM-ARGUMENT :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"new_parameter_name\") " +
        ":VALUE (SYNTHETO::MAKE-TRANSFORM-ARGUMENT-VALUE-IDENTIFIER :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"r\")))";

    public static final String checkExpectedType = "(syntheto::initializerp " + expectedString + ")";

    @Test
    void toSExpression() {
        // Example is f1 = "abc"
        Identifier name = Identifier.make("new_parameter_name");
        TransformArgumentValue val = TransformArgumentValueIdentifier.make(Identifier.make("r"));
        TransformArgument nameAndVal = TransformArgument.make(name, val);
        SExpression sExpression = nameAndVal.toSExpression();
        assertEquals(expectedString,
                sExpression.toString());

        TransformArgument rebuilt = (TransformArgument) ASTBuilder.fromSExpression(sExpression);
        assertEquals(nameAndVal, rebuilt);

        Reader r = new StringReader(sExpression.toString());
        Parser p = new Parser(r);
        SExpression s = p.parseTop();
        assertEquals(sExpression, s);
    }
}