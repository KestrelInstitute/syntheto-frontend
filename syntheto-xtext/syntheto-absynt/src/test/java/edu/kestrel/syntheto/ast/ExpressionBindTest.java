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
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ExpressionBindTest {

    public static final String expectedString1 =
            "(SYNTHETO::MAKE-EXPRESSION-BIND " +
                    ":VARIABLES (LIST (SYNTHETO::MAKE-TYPED-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"v1\") " +
                    ":TYPE (SYNTHETO::MAKE-TYPE-INTEGER))) " +
                    ":VALUE (SYNTHETO::MAKE-EXPRESSION-LITERAL :GET (SYNTHETO::MAKE-LITERAL-INTEGER :VALUE 1)) " +
                    ":BODY (SYNTHETO::MAKE-EXPRESSION-BINARY :OPERATOR (SYNTHETO::MAKE-BINARY-OP-ADD) " +
                    ":LEFT-OPERAND (SYNTHETO::MAKE-EXPRESSION-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"v1\")) " +
                    ":RIGHT-OPERAND (SYNTHETO::MAKE-EXPRESSION-LITERAL :GET (SYNTHETO::MAKE-LITERAL-INTEGER :VALUE 10))))";

    public static final ExpressionBind expectedASTNode1 = makeNode1();

    private static ExpressionBind makeNode1() {
        Identifier id1 = Identifier.make("v1");
        Variable v1 = Variable.make(id1);
        // Single variable bind test
        List<TypedVariable> varList1 = Arrays.asList(TypedVariable.make(id1, TypeInteger.make()));
        return ExpressionBind.make(varList1,
                ExpressionLiteral.make(LiteralInteger.make(1)),
                ExpressionBinary.make(ExpressionBinary.Operator.ADD, v1, ExpressionLiteral.make(LiteralInteger.make(10))));
    }

    public static final ExpressionBind expectedASTNode2 = makeNode2();

    private static ExpressionBind makeNode2() {
        Identifier id1 = Identifier.make("v1");
        Identifier id2 = Identifier.make("v2");
        Variable v1 = Variable.make(id1);
        Variable v2 = Variable.make(id2);
        // Multiple variable bind test
        List<TypedVariable> varList = Arrays.asList(TypedVariable.make(id1, TypeInteger.make()),
                TypedVariable.make(id2, TypeInteger.make()));
        return ExpressionBind.make(varList,
                ExpressionMulti.make(Arrays.asList(ExpressionLiteral.make(LiteralInteger.make(1)), ExpressionLiteral.make(LiteralInteger.make(10)))),
                ExpressionBinary.make(ExpressionBinary.Operator.ADD, v1, v2));
    }

    // TODO: it would be good to have alternatve predicates automatically generated.  For now just checking expressionp.
    public static final String checkExpectedType1 = "(syntheto::expressionp " + expectedString1 + ")";

    public static final String expectedString2 =
            "(SYNTHETO::MAKE-EXPRESSION-BIND " +
                    ":VARIABLES (LIST (SYNTHETO::MAKE-TYPED-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"v1\") " +
                    ":TYPE (SYNTHETO::MAKE-TYPE-INTEGER)) " +
                    "(SYNTHETO::MAKE-TYPED-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"v2\") " +
                    ":TYPE (SYNTHETO::MAKE-TYPE-INTEGER))) " +
                    ":VALUE (SYNTHETO::MAKE-EXPRESSION-MULTI :ARGUMENTS (LIST (SYNTHETO::MAKE-EXPRESSION-LITERAL :GET (SYNTHETO::MAKE-LITERAL-INTEGER :VALUE 1)) " +
                    "(SYNTHETO::MAKE-EXPRESSION-LITERAL :GET (SYNTHETO::MAKE-LITERAL-INTEGER :VALUE 10)))) " +
                    ":BODY (SYNTHETO::MAKE-EXPRESSION-BINARY :OPERATOR (SYNTHETO::MAKE-BINARY-OP-ADD) " +
                    ":LEFT-OPERAND (SYNTHETO::MAKE-EXPRESSION-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"v1\")) " +
                    ":RIGHT-OPERAND (SYNTHETO::MAKE-EXPRESSION-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"v2\"))))";

    // TODO: it would be good to have alternatve predicates automatically generated.  For now just checking expressionp.
    public static final String checkExpectedType2 = "(syntheto::expressionp " + expectedString2 + ")";

    @Test
    void toSExpression() {

        // Single variable bind test
        ExpressionBind exprBind1 = expectedASTNode1;
        SExpression sExpr1 = exprBind1.toSExpression();
        assertEquals(expectedString1,
                sExpr1.toString());

        ExpressionBind rebuilt1 = (ExpressionBind) ASTBuilder.fromSExpression(sExpr1);
        assertEquals(exprBind1, rebuilt1);

        Reader r1 = new StringReader(sExpr1.toString());
        Parser p1 = new Parser(r1);
        SExpression s1 = p1.parseTop();
        assertEquals(sExpr1, s1);

        // Multiple variable bind test
        ExpressionBind exprBind = expectedASTNode2;
        SExpression sExpr = exprBind.toSExpression();
        assertEquals(expectedString2,
                    sExpr.toString());

        ExpressionBind rebuilt = (ExpressionBind) ASTBuilder.fromSExpression(sExpr);
        assertEquals(exprBind, rebuilt);

        Reader r = new StringReader(sExpr.toString());
        Parser p = new Parser(r);
        SExpression s = p.parseTop();
        assertEquals(sExpr, s);

    }

}