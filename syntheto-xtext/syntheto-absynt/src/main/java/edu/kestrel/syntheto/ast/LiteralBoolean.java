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
 * Syntheto boolean literals.
 */
public class LiteralBoolean extends Literal {

    private final boolean value;

    private LiteralBoolean(boolean value) {
        this.value = value;
    }

    public static LiteralBoolean make(boolean value) {
        return new LiteralBoolean(value);
    }

    /**
     * The symbol for the ACL2 function that creates an instance of this class.
     */
    public static final SExpressionSymbol classMakerFn = SExpression.syntheto("MAKE-LITERAL-BOOLEAN");

    /**
     * Constructs a Boolean literal from an S-Expression
     * that looks like
     * (SYNTHETO::MAKE-LITERAL-BOOLEAN :VALUE val)
     *
     * @param buildFormArg The AST maker form.
     * @throws IllegalArgumentException If the list is malformed.
     */
    public static LiteralBoolean fromSExpression(SExpression buildFormArg) {
        if (!(buildFormArg instanceof SExpressionList))
            throw new IllegalArgumentException("Argument must be an SExpressionList");
        SExpressionList buildForm = (SExpressionList) buildFormArg;
        if (buildForm.length() != 3)
            throw new IllegalArgumentException("List not long enough");
        if (!(buildForm.first().equals(classMakerFn)))
            throw new IllegalArgumentException("Wrong function in list.");
        if (!(buildForm.second().equals(SExpression.keyword("VALUE"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        if (!(buildForm.third() instanceof SExpressionSymbol))
            throw new IllegalArgumentException("Wrong type of :VALUE argument.");
        SExpressionSymbol sSym = (SExpressionSymbol) buildForm.third();
        if (! (sSym.equals(SExpression.T()) || sSym.equals(SExpression.NIL())))
            throw new IllegalArgumentException(":VALUE argument must be T or NIL");
        return make(sSym.equals(SExpression.T()));
    }

    public boolean getValue() {
        return this.value;
    }

    @Override
    public SExpression toSExpression() {
        return SExpression.list(classMakerFn,
                SExpression.keyword("VALUE"),
                value ? SExpression.T() : SExpression.NIL());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LiteralBoolean that = (LiteralBoolean) o;
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
        return line(indentLevel, "Literal " + valueString);
    }
}
