/*
 * Copyright (C) 2020 Kestrel Institute (http://www.kestrel.edu)
 * License: 3-clause BSD license (https://opensource.org/licenses/BSD-3-Clause)
 * Main Author: Alessandro Coglio (coglio@kestrel.edu)
 * Contributing Author: Eric McCarthy (mccarthy@kestrel.edu)
 */

package edu.kestrel.syntheto.ast;

import edu.kestrel.syntheto.sexpr.*;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.io.StringReader;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

public class LiteralIntegerTest {

    public static final String expectedString =
            "(SYNTHETO::MAKE-LITERAL-INTEGER :VALUE 712398741293847129384712093847120938471209834712093847)";

    public static final String checkExpectedType = "(syntheto::literalp " + expectedString + ")";

    @Test
    void toSExpression() {
        LiteralInteger integerLiteral =
                LiteralInteger.make(new BigInteger("712398741293847129384712093847120938471209834712093847"));
        SExpression sExpression = integerLiteral.toSExpression();
        assertEquals(sExpression.toString(),
                expectedString);

        LiteralInteger rebuilt = (LiteralInteger) ASTBuilder.fromSExpression(sExpression);
        assertEquals(integerLiteral, rebuilt);

        Reader r = new StringReader(sExpression.toString());
        Parser p = new Parser(r);
        SExpression s = p.parseTop();
        assertEquals(sExpression, s);
    }
}

// TODO: if there is a way to do a must-fail test. we should do something like this:
//
// We might want to require that this sort of thing get the error about negative literal -1 must instead...
//String new_string = "(SYNTHETO::MAKE-EXPRESSION-MULTI " +
//                       ":ARGUMENTS (LIST (SYNTHETO::MAKE-EXPRESSION-LITERAL :GET (SYNTHETO::MAKE-LITERAL-INTEGER :VALUE 1)) (SYNTHETO::MAKE-EXPRESSION-LITERAL :GET (SYNTHETO::MAKE-LITERAL-INTEGER :VALUE -1))))";
//SExpression my_sexpr = SExpression.list(SExpression.syntheto("MAKE-LITERAL-INTEGER"), SExpression.keyword("VALUE"), SExpression.integer(-1));
//ASTNode built = ASTBuilder.fromSExpression(my_sexpr);
