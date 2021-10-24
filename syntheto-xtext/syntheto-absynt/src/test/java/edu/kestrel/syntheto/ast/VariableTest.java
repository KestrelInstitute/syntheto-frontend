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

public class VariableTest {

    public static final String expectedString =
            "(SYNTHETO::MAKE-EXPRESSION-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"acid\"))";

    public static final Variable expectedASTNode = makeNode();

    private static Variable makeNode() {
        Identifier identifier = Identifier.make("acid");
        return Variable.make(identifier);
    }

    public static final String checkExpectedType = "(syntheto::expressionp " + expectedString + ")";

    @Test
    void toSExpression() {
        Variable variable = expectedASTNode;
        SExpression sExpr = variable.toSExpression();
        assertEquals(expectedString, sExpr.toString());

        Variable rebuilt = (Variable) ASTBuilder.fromSExpression(sExpr);
        assertEquals(variable, rebuilt);

        Reader r = new StringReader(sExpr.toString());
        Parser p = new Parser(r);
        SExpression s = p.parseTop();
        assertEquals(sExpr, s);
    }
}