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
 * Syntheto string literals.
 */
public class LiteralString extends Literal {

    private final String value;

    private LiteralString(String value) {
        this.value = value;
    }

    public static LiteralString make(String value) {
        return new LiteralString(value);
    }

    /**
     * The symbol for the ACL2 function that creates an instance of this class.
     */
    public static final SExpressionSymbol classMakerFn = SExpression.syntheto("MAKE-LITERAL-STRING");

    /**
     * Constructs a String literal from an S-Expression
     * that looks like
     * (SYNTHETO::MAKE-LITERAL-STRING :VALUE "s-expr-string-contents")
     *
     * @param buildFormArg The AST maker form.
     * @throws IllegalArgumentException If the list is malformed.
     */
    public static LiteralString fromSExpression(SExpression buildFormArg) {
        if (!(buildFormArg instanceof SExpressionList))
            throw new IllegalArgumentException("Argument must be an SExpressionList");
        SExpressionList buildForm = (SExpressionList) buildFormArg;
        if (buildForm.length() != 3)
            throw new IllegalArgumentException("List not long enough");
        if (!(buildForm.first().equals(classMakerFn)))
            throw new IllegalArgumentException("Wrong function in list.");
        if (!(buildForm.second().equals(SExpression.keyword("VALUE"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        if (!(buildForm.third() instanceof SExpressionString))
            throw new IllegalArgumentException("Wrong type of :VALUE argument.");
        SExpressionString sStr = (SExpressionString) buildForm.third();
        return make(sStr.getValue());
    }

    public String getValue() {
        return this.value;
    }

    @Override
    public SExpression toSExpression() {
        return SExpression.list(classMakerFn,
                SExpression.keyword("VALUE"),
                SExpression.string(value));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LiteralString that = (LiteralString) o;
        return value.equals(that.value);
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
        String valueString = "\"" + this.value + "\"";
        return line(indentLevel, "Literal " + valueString);
    }
}
