/*
 * Copyright (C) 2021 Kestrel Institute (http://www.kestrel.edu)
 * License: 3-clause BSD license (https://opensource.org/licenses/BSD-3-Clause)
 * Main Author: Alessandro Coglio (coglio@kestrel.edu)
 * Contributing Author: Eric McCarthy (mccarthy@kestrel.edu)
 */

package edu.kestrel.syntheto.ast;

import edu.kestrel.syntheto.sexpr.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Syntheto class representing the Boolean kind of Transform Argument Value.
 */
public class TransformArgumentValueBoolean extends TransformArgumentValue {

    private final boolean value;

    private TransformArgumentValueBoolean(boolean value) {
        this.value = value;
    }

    public static TransformArgumentValueBoolean make(boolean value) {
        return new TransformArgumentValueBoolean(value);
    }

    /**
     * The symbol for the ACL2 function that creates an instance of this class.
     */
    public static final SExpressionSymbol classMakerFn = SExpression.syntheto("MAKE-TRANSFORM-ARGUMENT-VALUE-BOOL");

    /**
     * Constructs a Boolean literal from an S-Expression
     * that looks like
     * (SYNTHETO::MAKE-TRANSFORM-ARGUMENT-VALUE-BOOL :VAL val)
     *
     * @param buildFormArg The AST maker form.
     * @throws IllegalArgumentException If the list is malformed.
     */
    public static TransformArgumentValueBoolean fromSExpression(SExpression buildFormArg) {
        if (!(buildFormArg instanceof SExpressionList))
            throw new IllegalArgumentException("Argument must be an SExpressionList");
        SExpressionList buildForm = (SExpressionList) buildFormArg;
        if (buildForm.length() != 3)
            throw new IllegalArgumentException("List not long enough");
        if (!(buildForm.first().equals(classMakerFn)))
            throw new IllegalArgumentException("Wrong function in list.");
        if (!(buildForm.second().equals(SExpression.keyword("VAL"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        if (!(buildForm.third() instanceof SExpressionSymbol))
            throw new IllegalArgumentException("Wrong type of :VAL argument.");
        SExpressionSymbol sSym = (SExpressionSymbol) buildForm.third();
        if (! (sSym.equals(SExpression.T()) || sSym.equals(SExpression.NIL())))
            throw new IllegalArgumentException(":VAL argument must be T or NIL");
        return make(sSym.equals(SExpression.T()));
    }

    public boolean getValue() {
        return this.value;
    }

    @Override
    public SExpression toSExpression() {
        return SExpression.list(classMakerFn,
                SExpression.keyword("VAL"),
                value ? SExpression.T() : SExpression.NIL());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransformArgumentValueBoolean that = (TransformArgumentValueBoolean) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return this.toString(0);
    }

    @Override
    String toString(int indentLevel) {
        String valueString = this.value ? "true" : "false";
        return line(indentLevel, "Value " + valueString);
    }
}
