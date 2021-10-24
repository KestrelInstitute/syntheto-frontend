/*
 * Copyright (C) 2020 Kestrel Institute (http://www.kestrel.edu)
 * License: 3-clause BSD license (https://opensource.org/licenses/BSD-3-Clause)
 * Main Author: Alessandro Coglio (coglio@kestrel.edu)
 * Contributing Author: Eric McCarthy (mccarthy@kestrel.edu)
 */

package edu.kestrel.syntheto.ast;

import edu.kestrel.syntheto.sexpr.*;

import java.util.Objects;

/**
 * Syntheto regular function definer.
 */
public class FunctionDefinerRegular extends FunctionDefiner {

    private final Expression body;

    private final Expression measure;

    private FunctionDefinerRegular(Expression body,
                                   Expression measure) {
        this.body = body;
        this.measure = measure;
    }

    public static FunctionDefinerRegular make(Expression body,
                                              Expression measure) {
        return new FunctionDefinerRegular(body, measure);
    }

    /**
     * The symbol for the ACL2 function that creates an instance of this class.
     */
    public static final SExpressionSymbol classMakerFn = SExpression.syntheto("MAKE-FUNCTION-DEFINER-REGULAR");

    /**
     * Constructs a regular function definer
     * that looks like
     * (SYNTHETO::MAKE-FUNCTION-DEFINER-REGULAR :BODY expr)
     *
     * @param buildFormArg The AST maker form.
     * @throws IllegalArgumentException If the list is malformed.
     */
    public static FunctionDefinerRegular fromSExpression(SExpression buildFormArg) {
        if (! (buildFormArg instanceof SExpressionList))
            throw new IllegalArgumentException("Argument must be an SExpressionList");
        SExpressionList buildForm = (SExpressionList) buildFormArg;
        if (buildForm.length() != 5)
            throw new IllegalArgumentException("List not long enough");
        if (! (buildForm.first().equals(classMakerFn)))
            throw new IllegalArgumentException("Wrong function in list.");

        if (! (buildForm.second().equals(SExpression.keyword("BODY"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        if (! (buildForm.third() instanceof SExpressionList))
            throw new IllegalArgumentException("Wrong type of :BODY argument.");
        Object bodyRaw = ASTBuilder.fromSExpression(buildForm.third());
        if (! (bodyRaw instanceof Expression))
            throw new IllegalArgumentException("Wrong AST class returned by maker for :BODY");
        Expression body = (Expression) bodyRaw;

        if (! (buildForm.fourth().equals(SExpression.keyword("MEASURE"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        Expression measure = null;
        if (! (buildForm.fifth().equals(SExpression.NIL()))) {
            if (! (buildForm.fifth() instanceof SExpressionList))
                throw new IllegalArgumentException("Wrong type of :MEASURE argument.");
            Object measureRaw = ASTBuilder.fromSExpression(buildForm.fifth());
            if (!(measureRaw instanceof Expression))
                throw new IllegalArgumentException("Wrong AST class returned by maker for :MEASURE");
            measure = (Expression) measureRaw;
        }

        return make(body, measure);
    }

    public Expression getBody() {
        return body;
    }

    public Expression getMeasure() {
        return measure;
    }

    @Override
    public SExpression toSExpression() {
        return SExpression.list(classMakerFn,
                SExpression.keyword("BODY"), body.toSExpression(),
                SExpression.keyword("MEASURE"), 
                (measure == null) ? SExpression.NIL() : measure.toSExpression());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FunctionDefinerRegular that = (FunctionDefinerRegular) o;
        // If this or that measure is null but not both are null, then return false.
        // (The .equals() below does not tolerate a null.)
        if ((measure == null) != (that.measure == null))
            return false;
        return body.equals(that.body) &&
                ( ((measure == null) && (that.measure == null))
                        || measure.equals(that.measure));
    }


    @Override
    public int hashCode() {
        return Objects.hash(body, measure);
    }

    @Override
    public String toString() {
        return this.toString(0);
    }

    @Override
    String toString(int indentLevel) {
        StringBuilder s = new StringBuilder();
        s.append(line(indentLevel, "FunctionDefinerRegular {"));
        s.append(this.body.toString(indentLevel + 1));
        if (this.measure != null)
            s.append(this.measure.toString(indentLevel + 1));
        else
            s.append(line(indentLevel + 1, "/* no measure */"));
        s.append(line(indentLevel, "}"));
        return new String(s);
    }
}
