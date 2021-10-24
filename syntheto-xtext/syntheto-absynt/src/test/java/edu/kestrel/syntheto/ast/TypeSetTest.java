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

public class TypeSetTest {

    public static final String expectedString =
            "(SYNTHETO::MAKE-TYPE-SET :ELEMENT (SYNTHETO::MAKE-TYPE-CHARACTER))";

    public static final String checkExpectedType = "(syntheto::typep " + expectedString + ")";

    @Test
    void toSExpression() {
        // Example is set(char)
        TypeSet setType = TypeSet.make(TypeCharacter.make());
        SExpression sExpression = setType.toSExpression();
        assertEquals(expectedString, sExpression.toString());

        TypeSet rebuilt = (TypeSet) ASTBuilder.fromSExpression(sExpression);
        assertEquals(setType, rebuilt);

        Reader r = new StringReader(sExpression.toString());
        Parser p = new Parser(r);
        SExpression s = p.parseTop();
        assertEquals(sExpression, s);
    }
}