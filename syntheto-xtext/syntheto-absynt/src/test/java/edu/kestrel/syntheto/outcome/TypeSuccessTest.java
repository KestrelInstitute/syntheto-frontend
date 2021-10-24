/*
 * Copyright (C) 2021 Kestrel Institute (http://www.kestrel.edu)
 * License: 3-clause BSD license (https://opensource.org/licenses/BSD-3-Clause)
 * Main Author: Alessandro Coglio (coglio@kestrel.edu)
 * Contributing Author: Eric McCarthy (mccarthy@kestrel.edu)
 */

package edu.kestrel.syntheto.outcome;

import edu.kestrel.syntheto.sexpr.Parser;
import edu.kestrel.syntheto.sexpr.SExpression;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

public class TypeSuccessTest {

    public static final String expectedString =
            "(SYNTHETO::MAKE-OUTCOME-TYPE-SUCCESS :MESSAGE \"abcd\")";

    public static final String checkExpectedType = "(let ((o " + expectedString + ")) (and (syntheto::outcomep o) (syntheto::outcome-case o :type-success)))";

    @Test
    void toSExpression() {
        TypeSuccess typeSuccess = TypeSuccess.make("abcd");
        SExpression sExpression = typeSuccess.toSExpression();
        assertEquals(sExpression.toString(),
                expectedString);

        TypeSuccess rebuilt = (TypeSuccess) OutcomeBuilder.fromSExpression(sExpression);
        assertEquals(typeSuccess, rebuilt);

        Reader r = new StringReader(sExpression.toString());
        Parser p = new Parser(r);
        SExpression s = p.parseTop();
        assertEquals(sExpression, s);
    }
}