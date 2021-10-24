/*
 * Copyright (C) 2021 Kestrel Institute (http://www.kestrel.edu)
 * License: 3-clause BSD license (https://opensource.org/licenses/BSD-3-Clause)
 * Main Author: Alessandro Coglio (coglio@kestrel.edu)
 * Contributing Author: Eric McCarthy (mccarthy@kestrel.edu)
 */

package edu.kestrel.syntheto.ast;

import edu.kestrel.syntheto.sexpr.*;

import java.util.Objects;

/**
 * Syntheto class representing the Term kind of Transform Argument Value.
 */
public class TransformArgumentValueTerm extends TransformArgumentValue {


    /**
     * The expression that forms the transform argument value.
     * Never null.
     */
    private final Expression expr;

    /**
     * Constructs a variable with the given identifier.
     *
     * @param expr The term.
     * @throws IllegalArgumentException If the term is null.
     */
    private TransformArgumentValueTerm(Expression expr) {
        if (expr == null) {
            throw new IllegalArgumentException("Null argument.");
        } else {
            this.expr = expr;
        }
    }

    /**
     * Builds a variable with the given identifier.
     *
     * @param name The identifier.
     * @return The variable.
     * @throws IllegalArgumentException If the identifier is null.
     */
    public static TransformArgumentValueTerm make(Expression name) {
        return new TransformArgumentValueTerm(name);
    }

    /**
     * The symbol for the ACL2 function that creates an instance of this class.
     */
    public static final SExpressionSymbol classMakerFn = SExpression.syntheto("MAKE-TRANSFORM-ARGUMENT-VALUE-TERM");

    /**
     * Constructs a variable expression from an S-Expression
     * that looks like
     * (SYNTHETO::MAKE-TRANSFORM-ARGUMENT-VALUE-TERM :GET term)
     *
     * @param buildFormArg The AST maker form.
     * @throws IllegalArgumentException If the list is malformed.
     */
    public static TransformArgumentValueTerm fromSExpression(SExpression buildFormArg) {
        if (! (buildFormArg instanceof SExpressionList))
            throw new IllegalArgumentException("Argument must be an SExpressionList.");
        SExpressionList buildForm = (SExpressionList) buildFormArg;
        if (buildForm.length() != 3)
            throw new IllegalArgumentException("List not the right length.");
        if (! (buildForm.first().equals(classMakerFn)))
            throw new IllegalArgumentException("Wrong function in list.");

        if (! (buildForm.second().equals(SExpression.keyword("GET"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        if (! (buildForm.third() instanceof SExpressionList))
            throw new IllegalArgumentException("Wrong type of :GET argument.");
        Object nameRaw = ASTBuilder.fromSExpression(buildForm.third());
        if (! (nameRaw instanceof Expression))
            throw new IllegalArgumentException("Wrong AST class returned by maker for :GET");
        Expression name = (Expression) nameRaw;

        return make(name);
    }

    /**
     * Returns the expression that forms the term.
     *
     * @return The expression.
     */
    public Expression getExpr() {
        return this.expr;
    }

    /**
     * Translates this TransformArgumentValueTerm to an s-expression.
     * The s-expression is {@code (MAKE-TRANSFORM-ARGUMENT-VALUE-TERM :GET <term>)},
     * where {@code <term>} is the s-expression for the identifier.
     *
     * @return The s-expression.
     */
    @Override
    public SExpression toSExpression() {
        return SExpression.list(classMakerFn,
                SExpression.keyword("GET"),
                this.expr.toSExpression());
    }

    /**
     * Checks if this object is equal to another object.
     *
     * @param o The object to compare this expression with.
     * @return {@code true} if they are equal, {@code false} otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransformArgumentValueTerm variable = (TransformArgumentValueTerm) o;
        return expr.equals(variable.expr);
    }

    /**
     * Returns a hash code for this object.
     *
     * @return The hash code.
     */
    @Override
    public int hashCode() {
        return Objects.hash(expr);
    }

    @Override
    public String toString() {
        return this.toString(0);
    }

    @Override
    public String toString(int indentLevel) {
        StringBuilder s = new StringBuilder();
        s.append(line(indentLevel, "TransformArgumentValueTerm {"));
        s.append(this.expr.toString(indentLevel + 1));
        s.append(line(indentLevel, "}"));
        return new String(s);
    }
}

