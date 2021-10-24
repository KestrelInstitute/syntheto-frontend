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
 * Syntheto fields (of product and sum types).
 */
public class Field extends ASTNode {

    private final Identifier name;

    private final Type type;

    private Field(Identifier name, Type type) {
        this.name = name;
        this.type = type;
    }

    public static Field make(Identifier name, Type type) {
        return new Field(name, type);
    }

    /**
     * The symbol for the ACL2 function that creates an instance of this class.
     */
    public static final SExpressionSymbol classMakerFn = SExpression.syntheto("MAKE-FIELD");

    /**
     * Constructs a field specifier from an S-Expression
     * that looks like
     * (SYNTHETO::MAKE-FIELD :NAME identifier :TYPE type)
     *
     * @param buildFormArg The AST maker form.
     * @throws IllegalArgumentException If the list is malformed.
     */
    public static Field fromSExpression(SExpression buildFormArg) {
        if (!(buildFormArg instanceof SExpressionList))
            throw new IllegalArgumentException("Argument must be an SExpressionList");
        SExpressionList buildForm = (SExpressionList) buildFormArg;
        if (buildForm.length() != 5)
            throw new IllegalArgumentException("List not long enough");
        if (!(buildForm.first().equals(classMakerFn)))
            throw new IllegalArgumentException("Wrong function in list.");

        if (!(buildForm.second().equals(SExpression.keyword("NAME"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        if (!(buildForm.third() instanceof SExpressionList))
            throw new IllegalArgumentException("Wrong type of :NAME argument.");
        Object nameRaw = ASTBuilder.fromSExpression(buildForm.third());
        if (!(nameRaw instanceof Identifier))
            throw new IllegalArgumentException("Wrong AST class returned by maker for :NAME");
        Identifier name = (Identifier) nameRaw;

        if (!(buildForm.fourth().equals(SExpression.keyword("TYPE"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        if (!(buildForm.fifth() instanceof SExpressionList))
            throw new IllegalArgumentException("Wrong type of :TYPE argument.");
        Object typeRaw = ASTBuilder.fromSExpression(buildForm.fifth());
        if (!(typeRaw instanceof Type))
            throw new IllegalArgumentException("Wrong AST class returned by maker for :TYPE");
        Type type = (Type) typeRaw;

        return make(name, type);
    }

    public Identifier getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    @Override
    public SExpression toSExpression() {
        return SExpression.list(classMakerFn,
                SExpression.keyword("NAME"), name.toSExpression(),
                SExpression.keyword("TYPE"), type.toSExpression());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Field field = (Field) o;
        return name.equals(field.name) &&
                type.equals(field.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type);
    }

    @Override
    public String toString() {
        return this.name.toString() + ": " + this.type.toString();
    }

    String toString(int indentLevel) {
        return line(indentLevel, "Field " + this.name + " {") +
                this.type.toString(indentLevel + 1) +
                line(indentLevel, "}");
    }
}