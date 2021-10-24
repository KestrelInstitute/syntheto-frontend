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
 * Syntheto sequence types.
 */
public class TypeSequence extends TypeCollection {

    private final Type element;

    private TypeSequence(Type element) {
        this.element = element;
    }

    public static TypeSequence make(Type element) {
        return new TypeSequence(element);
    }

    /**
     * The symbol for the ACL2 function that creates an instance of this class.
     */
    public static final SExpressionSymbol classMakerFn = SExpression.syntheto("MAKE-TYPE-SEQUENCE");

    /**
     * Constructs a sequence type declaration from an S-Expression
     * that looks like
     * (SYNTHETO::MAKE-TYPE-SEQUENCE :ELEMENT type)
     *
     * @param buildFormArg The AST maker form.
     * @throws IllegalArgumentException If the list is malformed.
     */
    public static TypeSequence fromSExpression(SExpression buildFormArg) {
        if (! (buildFormArg instanceof SExpressionList))
            throw new IllegalArgumentException("Argument must be an SExpressionList");
        SExpressionList buildForm = (SExpressionList) buildFormArg;
        if (buildForm.length() != 3)
            throw new IllegalArgumentException("List not long enough");
        if (! (buildForm.first().equals(classMakerFn)))
            throw new IllegalArgumentException("Wrong function in list.");

        if (! (buildForm.second().equals(SExpression.keyword("ELEMENT"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        if (! (buildForm.third() instanceof SExpressionList))
            throw new IllegalArgumentException("Wrong type of :ELEMENT argument.");
        Object typeRaw = ASTBuilder.fromSExpression(buildForm.third());
        if (! (typeRaw instanceof Type))
            throw new IllegalArgumentException("Wrong AST class returned by maker for :ELEMENT");
        Type type = (Type) typeRaw;

        return make(type);
    }

    public Type getElement() {
        return this.element;
    }

    @Override
    public SExpression toSExpression() {
        return SExpression.list(classMakerFn,
                SExpression.keyword("ELEMENT"),
                element.toSExpression());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypeSequence that = (TypeSequence) o;
        return element.equals(that.element);
    }

    @Override
    public int hashCode() {
        return Objects.hash(element);
    }

    @Override
    public String toString() {
        return this.toString(0);
    }

    @Override
    String toString(int indentLevel) {
        return line(indentLevel, "TypeSequence {") +
                this.element.toString(indentLevel + 1) +
                line(indentLevel, "}");
    }
}
