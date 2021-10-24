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

public class TypeMapTest {

    public static final String expectedString =
            "(SYNTHETO::MAKE-TYPE-MAP :DOMAIN (SYNTHETO::MAKE-TYPE-STRING)" +
                    " :RANGE (SYNTHETO::MAKE-TYPE-INTEGER))";

    public static final String checkExpectedType = "(syntheto::typep " + expectedString + ")";

    @Test
    void toSExpression() {
        // Let's make the type of maps from strings to integers
        TypeString stringType = TypeString.make();
        TypeInteger integerType = TypeInteger.make();
        TypeMap mapType = TypeMap.make(stringType, integerType);
        SExpression sExpression = mapType.toSExpression();
        assertEquals(expectedString,
                sExpression.toString());

        TypeMap rebuilt = (TypeMap) ASTBuilder.fromSExpression(sExpression);
        assertEquals(mapType, rebuilt);

        Reader r = new StringReader(sExpression.toString());
        Parser p = new Parser(r);
        SExpression s = p.parseTop();
        assertEquals(sExpression, s);
    }
}