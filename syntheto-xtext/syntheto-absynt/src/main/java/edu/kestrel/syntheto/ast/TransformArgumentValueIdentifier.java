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
 * Syntheto class representing the Identifier kind of Transform Argument Value.
 */
public class TransformArgumentValueIdentifier extends TransformArgumentValue {

    /**
     * The identifier.
     * Never null.
     */
    private final Identifier name;

    /**
     * Constructs a TransformArgumentValueIdentifier with the given identifier.
     *
     * @param name The identifier.
     * @throws IllegalArgumentException If the identifier is null.
     */
    private TransformArgumentValueIdentifier(Identifier name) {
        if (name == null) {
            throw new IllegalArgumentException("Null argument.");
        } else {
            this.name = name;
        }
    }

    /**
     * Builds a TransformArgumentValueIdentifier with the given identifier.
     *
     * @param name The identifier.
     * @return The TransformArgumentValueIdentifier.
     * @throws IllegalArgumentException If the identifier is null.
     */
    public static TransformArgumentValueIdentifier make(Identifier name) {
        return new TransformArgumentValueIdentifier(name);
    }

    /**
     * The symbol for the ACL2 function that creates an instance of this class.
     */
    public static final SExpressionSymbol classMakerFn = SExpression.syntheto("MAKE-TRANSFORM-ARGUMENT-VALUE-IDENTIFIER");

    /**
     * Constructs a TransformArgumentValueIdentifier expression from an S-Expression
     * that looks like
     * (SYNTHETO::MAKE-TRANSFORM-ARGUMENT-VALUE-IDENTIFIER :NAME NAME)
     *
     * @param buildFormArg The AST maker form.
     * @throws IllegalArgumentException If the list is malformed.
     */
    public static TransformArgumentValueIdentifier fromSExpression(SExpression buildFormArg) {
        if (! (buildFormArg instanceof SExpressionList))
            throw new IllegalArgumentException("Argument must be an SExpressionList.");
        SExpressionList buildForm = (SExpressionList) buildFormArg;
        if (buildForm.length() != 3)
            throw new IllegalArgumentException("List not the right length.");
        if (! (buildForm.first().equals(classMakerFn)))
            throw new IllegalArgumentException("Wrong function in list.");

        if (! (buildForm.second().equals(SExpression.keyword("NAME"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        if (! (buildForm.third() instanceof SExpressionList))
            throw new IllegalArgumentException("Wrong type of :NAME argument.");
        Object nameRaw = ASTBuilder.fromSExpression(buildForm.third());
        if (! (nameRaw instanceof Identifier))
            throw new IllegalArgumentException("Wrong AST class returned by maker for :NAME");
        Identifier name = (Identifier) nameRaw;

        return make(name);
    }

    /**
     * Returns the identifier that forms the TransformArgumentValueIdentifier.
     *
     * @return The identifier.
     */
    public Identifier getName() {
        return this.name;
    }

    /**
     * Translates this TransformArgumentValueIdentifier to an s-expression.
     * The s-expression is {@code (MAKE-EXPRESSION-VARIABLE :NAME <id>)},
     * where {@code <id>} is the s-expression for the identifier.
     *
     * @return The s-expression.
     */
    @Override
    public SExpression toSExpression() {
        return SExpression.list(classMakerFn,
                SExpression.keyword("NAME"),
                this.name.toSExpression());
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
        TransformArgumentValueIdentifier tavi = (TransformArgumentValueIdentifier) o;
        return name.equals(tavi.name);
    }

    /**
     * Returns a hash code for this object.
     *
     * @return The hash code.
     */
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return this.toString(0);
    }

    @Override
    public String toString(int indentLevel) {
        return line(indentLevel, "TransformArgumentValueIdentifier " + this.name);
    }
}

