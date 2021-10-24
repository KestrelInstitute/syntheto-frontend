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

public class TypedVariableTest {

    public static final String expectedString =
            "(SYNTHETO::MAKE-TYPED-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"my_id\") "+
                    ":TYPE (SYNTHETO::MAKE-TYPE-INTEGER))";

    public static final String checkExpectedType = "(syntheto::typed-variablep " + expectedString + ")";

    @Test
    void toSExpression() {
        // Example is my_id: integer
        Identifier varname = Identifier.make("my_id");
        Type vartype = TypeInteger.make();
        TypedVariable varAndType = TypedVariable.make(varname, vartype);
        SExpression sExpression = varAndType.toSExpression();
        assertEquals(expectedString,
                sExpression.toString());

        TypedVariable rebuilt = (TypedVariable) ASTBuilder.fromSExpression(sExpression);
        assertEquals(varAndType, rebuilt);

        Reader r = new StringReader(sExpression.toString());
        Parser p = new Parser(r);
        SExpression s = p.parseTop();
        assertEquals(sExpression, s);
    }
}