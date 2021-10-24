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
 * Syntheto typed variables.
 */
public class TypedVariable extends ASTNode {

    private final Identifier name;

    private final Type type;

    private TypedVariable(Identifier name, Type type) {
        this.name = name;
        this.type = type;
    }

    public static TypedVariable make(Identifier name, Type type) {
        return new TypedVariable(name, type);
    }

    /**
     * The symbol for the ACL2 function that creates an instance of this class.
     */
    public static final SExpressionSymbol classMakerFn = SExpression.syntheto("MAKE-TYPED-VARIABLE");

    /**
     * Constructs a typed variable from an S-Expression
     * that looks like
     * (SYNTHETO::MAKE-TYPED-VARIABLE :NAME name :TYPE type)
     *
     * @param buildFormArg The AST maker form.
     * @throws IllegalArgumentException If the list is malformed.
     */
    public static TypedVariable fromSExpression(SExpression buildFormArg) {
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

        if (! (buildForm.fourth().equals(SExpression.keyword("TYPE"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        if (! (buildForm.fifth() instanceof SExpressionList))
            throw new IllegalArgumentException("Wrong type of :TYPE argument.");
        Object typeRaw = ASTBuilder.fromSExpression(buildForm.fifth());
        if (! (typeRaw instanceof Type))
            throw new IllegalArgumentException("Wrong AST class returned by maker for :TYPE");
        Type type = (Type) typeRaw;

        return make(name, type);
    }


    public Identifier getName() {
        return this.name;
    }

    public Type getType() {
        return this.type;
    }

    public SExpression toSExpression() {
        return SExpression.list(classMakerFn,
                SExpression.keyword("NAME"), name.toSExpression(),
                SExpression.keyword("TYPE"), type.toSExpression());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypedVariable that = (TypedVariable) o;
        return name.equals(that.name) &&
                type.equals(that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type);
    }

    @Override
    public String toString() {
        return this.toString(0);
    }

    String toString(int indentLevel) {
        return line(indentLevel, "TypedVariable " + this.name + " {") +
                this.type.toString(indentLevel + 1) +
                line(indentLevel, "}");
    }
}
