/*
 * Copyright (C) 2020 Kestrel Institute (http://www.kestrel.edu)
 * License: 3-clause BSD license (https://opensource.org/licenses/BSD-3-Clause)
 * Main Author: Alessandro Coglio (coglio@kestrel.edu)
 * Contributing Author: Eric McCarthy (mccarthy@kestrel.edu)
 */

package edu.kestrel.syntheto.ast;

import edu.kestrel.syntheto.sexpr.Parser;
import edu.kestrel.syntheto.sexpr.SExpression;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

public class TypeCharacterTest {

    public static final String expectedString =
            "(SYNTHETO::MAKE-TYPE-CHARACTER)";

    public static final String checkExpectedType = "(syntheto::typep " + expectedString + ")";

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void toSExpression() {
      TypeCharacter characterType = TypeCharacter.make();
      SExpression sExpr = characterType.toSExpression();
      assertEquals(expectedString, sExpr.toString());

      TypeCharacter rebuilt = (TypeCharacter) ASTBuilder.fromSExpression(sExpr);
      assertEquals(characterType, rebuilt);

        Reader r = new StringReader(sExpr.toString());
        Parser p = new Parser(r);
        SExpression s = p.parseTop();
        assertEquals(sExpr, s);
    }
}