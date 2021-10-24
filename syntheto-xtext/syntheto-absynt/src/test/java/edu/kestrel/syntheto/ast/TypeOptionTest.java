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

public class TypeOptionTest {

    public static final String expectedString =
            "(SYNTHETO::MAKE-TYPE-OPTION :BASE " +
                    "(SYNTHETO::MAKE-TYPE-DEFINED :NAME " +
                    "(SYNTHETO::MAKE-IDENTIFIER :NAME \"acid\")))";

    public static final String checkExpectedType = "(syntheto::typep " + expectedString + ")";

    @Test
    void toSExpression() {
        // Example is maybe("acid")
        TypeDefined typeref = TypeDefined.make(Identifier.make("acid"));
        TypeOption optionType = TypeOption.make(typeref);
        SExpression sExpression = optionType.toSExpression();
        assertEquals(expectedString,
                sExpression.toString());

        TypeOption rebuilt = (TypeOption) ASTBuilder.fromSExpression(sExpression);
        assertEquals(optionType, rebuilt);

        Reader r = new StringReader(sExpression.toString());
        Parser p = new Parser(r);
        SExpression s = p.parseTop();
        assertEquals(sExpression, s);
    }
}