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

public class ExpressionLiteralTest {

    public static final String expectedString =
            "(SYNTHETO::MAKE-EXPRESSION-LITERAL :GET (SYNTHETO::MAKE-LITERAL-CHARACTER :VALUE (CODE-CHAR 97)))";

    public static final ExpressionLiteral expectedASTNode = makeNode();

    public static ExpressionLiteral makeNode() {
        LiteralCharacter characterLiteral = LiteralCharacter.make('a');
        return ExpressionLiteral.make(characterLiteral);
    }


    // TODO: it would be good to have alternative predicates automatically generated.  For now just checking expressionp.
    public static final String checkExpectedType = "(syntheto::expressionp " + expectedString + ")";

    @Test
    void toSExpression() {
        ExpressionLiteral expressionLiteral = expectedASTNode;
        SExpression sExpression = expressionLiteral.toSExpression();
        assertEquals(expectedString, sExpression.toString());

        ExpressionLiteral rebuilt = (ExpressionLiteral) ASTBuilder.fromSExpression(sExpression);
        assertEquals(expressionLiteral, rebuilt);

        Reader r = new StringReader(sExpression.toString());
        Parser p = new Parser(r);
        SExpression s = p.parseTop();
        assertEquals(sExpression, s);
    }
}
