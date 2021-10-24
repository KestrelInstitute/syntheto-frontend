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
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FunctionHeaderTest {

    public static final String expectedString =
            "(SYNTHETO::MAKE-FUNCTION-HEADER :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"F\") " +
                    ":INPUTS (LIST (SYNTHETO::MAKE-TYPED-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"x\") :TYPE (SYNTHETO::MAKE-TYPE-INTEGER)) " +
                    "(SYNTHETO::MAKE-TYPED-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"y\") :TYPE (SYNTHETO::MAKE-TYPE-INTEGER))) " +
                    ":OUTPUTS (LIST (SYNTHETO::MAKE-TYPED-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"output_y\") :TYPE (SYNTHETO::MAKE-TYPE-INTEGER)) " +
                    "(SYNTHETO::MAKE-TYPED-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"output_x\") :TYPE (SYNTHETO::MAKE-TYPE-INTEGER))))";

    public static final String checkExpectedType = "(syntheto::function-headerp " + expectedString + ")";

    @Test
    void toSExpression() {
        // Example F(x:int,y:int)->(y:int,x:int)
        Identifier fnname = Identifier.make("F");
        TypedVariable x = TypedVariable.make(Identifier.make("x"), TypeInteger.make());
        TypedVariable y = TypedVariable.make(Identifier.make("y"), TypeInteger.make());
        List<TypedVariable> args = Arrays.asList(x, y);
        TypedVariable output_y = TypedVariable.make(Identifier.make("output_y"), TypeInteger.make());
        TypedVariable output_x = TypedVariable.make(Identifier.make("output_x"), TypeInteger.make());
        List<TypedVariable> output_args = Arrays.asList(output_y, output_x);
        FunctionHeader funhead = FunctionHeader.make(fnname, args, output_args);

        SExpression sExpression = funhead.toSExpression();
        assertEquals(expectedString,
                sExpression.toString());

        FunctionHeader rebuilt = (FunctionHeader) ASTBuilder.fromSExpression(sExpression);
        assertEquals(funhead, rebuilt);

        Reader r = new StringReader(sExpression.toString());
        Parser p = new Parser(r);
        SExpression s = p.parseTop();
        assertEquals(sExpression, s);
    }
}