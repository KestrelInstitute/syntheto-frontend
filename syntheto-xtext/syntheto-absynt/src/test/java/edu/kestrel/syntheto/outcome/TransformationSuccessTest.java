/*
 * Copyright (C) 2021 Kestrel Institute (http://www.kestrel.edu)
 * License: 3-clause BSD license (https://opensource.org/licenses/BSD-3-Clause)
 * Main Author: Alessandro Coglio (coglio@kestrel.edu)
 * Contributing Author: Eric McCarthy (mccarthy@kestrel.edu)
 */

package edu.kestrel.syntheto.outcome;

import edu.kestrel.syntheto.ast.*;
import edu.kestrel.syntheto.sexpr.Parser;
import edu.kestrel.syntheto.sexpr.SExpression;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TransformationSuccessTest {

    // Take the TopLevelTypeTest and TopLevelFunctionTest examples as the TopLevel instances returned.

    public static final String expectedString =
            "(SYNTHETO::MAKE-OUTCOME-TRANSFORMATION-SUCCESS :MESSAGE \"result of transformation\" :TOPLEVELS (LIST " +
                    "(SYNTHETO::MAKE-TOPLEVEL-TYPE :GET " +
                    "(SYNTHETO::MAKE-TYPE-DEFINITION :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"ordered_pair\") "+
                    ":BODY (SYNTHETO::MAKE-TYPE-DEFINER-PRODUCT :GET " +
                    "(SYNTHETO::MAKE-TYPE-PRODUCT " +
                    ":FIELDS (LIST (SYNTHETO::MAKE-FIELD :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"x\") :TYPE (SYNTHETO::MAKE-TYPE-INTEGER)) " +
                    "(SYNTHETO::MAKE-FIELD :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"y\") :TYPE (SYNTHETO::MAKE-TYPE-INTEGER))) " +
                    ":INVARIANT NIL))))" + " " +

                    "(SYNTHETO::MAKE-TOPLEVEL-FUNCTION :GET " +
                    "(SYNTHETO::MAKE-FUNCTION-DEFINITION :HEADER " +
                    "(SYNTHETO::MAKE-FUNCTION-HEADER :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"F\") "+
                    ":INPUTS (LIST (SYNTHETO::MAKE-TYPED-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"x\") :TYPE (SYNTHETO::MAKE-TYPE-INTEGER)) " +
                    "(SYNTHETO::MAKE-TYPED-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"y\") :TYPE (SYNTHETO::MAKE-TYPE-INTEGER))) " +
                    ":OUTPUTS (LIST (SYNTHETO::MAKE-TYPED-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"output_y\") :TYPE (SYNTHETO::MAKE-TYPE-INTEGER)) " +
                    "(SYNTHETO::MAKE-TYPED-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"output_x\") :TYPE (SYNTHETO::MAKE-TYPE-INTEGER)))) " +
                    ":PRECONDITION NIL :POSTCONDITION NIL :DEFINER " +
                    "(SYNTHETO::MAKE-FUNCTION-DEFINER-REGULAR " +
                    ":BODY (SYNTHETO::MAKE-EXPRESSION-MULTI :ARGUMENTS (LIST " +
                    "(SYNTHETO::MAKE-EXPRESSION-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"y\")) " +
                    "(SYNTHETO::MAKE-EXPRESSION-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"x\")))) " +
                    ":MEASURE NIL)))" + "))";

    public static final String checkExpectedType = "(let ((o " + expectedString + ")) (and (syntheto::outcomep o) (syntheto::outcome-case o :transformation-success)))";

    @Test
    void toSExpression() {

        // make the TopLevelType (as in TopLevelTypeTest.java)
        Identifier v1 = Identifier.make("x");
        Identifier v2 = Identifier.make("y");
        Field f1 = Field.make(v1, TypeInteger.make());
        Field f2 = Field.make(v2, TypeInteger.make());
        List<Field> fields = Arrays.asList(f1, f2);
        // Test a product type with a null invariant.
        TypeProduct prod = TypeProduct.make(fields, null);
        Identifier varname = Identifier.make("ordered_pair");
        TypeDefinition typedef = TypeDefinition.make(varname, TypeDefinerProduct.make(prod));
        TopLevelType tltype = TopLevelType.make(typedef);

        // make the TopLevelFunction (as in TopLevelFunctionTest.java)
        // Example F(x:int,y:int)->(y:int,x:int)
        Identifier fnname = Identifier.make("F");
        TypedVariable x = TypedVariable.make(Identifier.make("x"), TypeInteger.make());
        TypedVariable y = TypedVariable.make(Identifier.make("y"), TypeInteger.make());
        List<TypedVariable> args = Arrays.asList(x, y);
        TypedVariable output_y = TypedVariable.make(Identifier.make("output_y"), TypeInteger.make());
        TypedVariable output_x = TypedVariable.make(Identifier.make("output_x"), TypeInteger.make());
        List<TypedVariable> output_args = Arrays.asList(output_y, output_x);
        FunctionHeader funhead = FunctionHeader.make(fnname, args, output_args);
        Variable vy = Variable.make(Identifier.make("y"));
        Variable vx = Variable.make(Identifier.make("x"));
        List<Expression> vals = Arrays.asList(vy, vx);
        ExpressionMulti retval = ExpressionMulti.make(vals);
        FunctionDefinerRegular funDefiner = FunctionDefinerRegular.make(retval, null);
        FunctionDefinition def = FunctionDefinition.make(funhead, null, null, funDefiner);
        TopLevelFunction tlf = TopLevelFunction.make(def);

        List<TopLevel> topLevels = new ArrayList<>();
        topLevels.add(tltype);
        topLevels.add(tlf);

        TransformationSuccess transformationSuccess = TransformationSuccess.make("result of transformation",
                topLevels);
        SExpression sExpression = transformationSuccess.toSExpression();
        assertEquals(sExpression.toString(),
                expectedString);

        TransformationSuccess rebuilt = (TransformationSuccess) OutcomeBuilder.fromSExpression(sExpression);
        assertEquals(transformationSuccess, rebuilt);

        Reader r = new StringReader(sExpression.toString());
        Parser p = new Parser(r);
        SExpression s = p.parseTop();
        assertEquals(sExpression, s);
    }
}