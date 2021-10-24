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
 * Syntheto transform argument.
 */
public class TransformArgument extends ASTNode {

    private final Identifier name;

    private final TransformArgumentValue value;

    private TransformArgument(Identifier name, TransformArgumentValue value) {
        this.name = name;
        this.value = value;
    }

    public static TransformArgument make(Identifier name, TransformArgumentValue value) {
        return new TransformArgument(name, value);
    }

    /**
     * The symbol for the ACL2 function that creates an instance of this class.
     */
    public static final SExpressionSymbol classMakerFn = SExpression.syntheto("MAKE-TRANSFORM-ARGUMENT");

    /**
     * Constructs a transform argument from an S-Expression
     * that looks like
     * (SYNTHETO::MAKE-TRANSFORM-ARGUMENT :NAME name :VALUE value)
     *
     * @param buildFormArg The AST maker form.
     * @throws IllegalArgumentException If the list is malformed.
     */
    public static TransformArgument fromSExpression(SExpression buildFormArg) {
        if (! (buildFormArg instanceof SExpressionList))
            throw new IllegalArgumentException("Argument must be an SExpressionList");
        SExpressionList buildForm = (SExpressionList) buildFormArg;
        if (buildForm.length() != 5)
            throw new IllegalArgumentException("List not long enough");
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

        if (! (buildForm.fourth().equals(SExpression.keyword("VALUE"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        if (! (buildForm.fifth() instanceof SExpressionList))
            throw new IllegalArgumentException("Wrong type of :VALUE argument.");
        Object valueRaw = ASTBuilder.fromSExpression(buildForm.fifth());
        if (! (valueRaw instanceof TransformArgumentValue))
            throw new IllegalArgumentException("Wrong AST class returned by maker for :VALUE");
        TransformArgumentValue value = (TransformArgumentValue) valueRaw;

        return make(name, value);
    }


    public Identifier getName() {
        return this.name;
    }

    public TransformArgumentValue getTransformArgumentValue() {
        return this.value;
    }

    public SExpression toSExpression() {
        return SExpression.list(classMakerFn,
                SExpression.keyword("NAME"), name.toSExpression(),
                SExpression.keyword("VALUE"), value.toSExpression());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransformArgument that = (TransformArgument) o;
        return name.equals(that.name) &&
                value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value);
    }

    @Override
    public String toString() {
        return this.toString(0);
    }

    String toString(int indentLevel) {
        return line(indentLevel, "TransformArgument " + this.name + " {") +
                this.value.toString(indentLevel + 1) +
                line(indentLevel, "}");
    }
}
