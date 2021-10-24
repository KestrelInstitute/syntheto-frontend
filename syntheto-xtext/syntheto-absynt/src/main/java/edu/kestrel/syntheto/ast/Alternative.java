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
 * Syntheto alternatives (of sum types).
 */
public class Alternative extends ASTNode {

    private final Identifier name;

    private final TypeProduct product;

    private Alternative(Identifier name, TypeProduct product) {
        this.name = name;
        this.product = product;
    }

    public static Alternative make(Identifier name, TypeProduct product) {
        return new Alternative(name, product);
    }

    /**
     * The symbol for the ACL2 function that creates an instance of this class.
     */
    public static final SExpressionSymbol classMakerFn = SExpression.syntheto("MAKE-ALTERNATIVE");

    /**
     * Constructs a sum type alternative, which is similar to a product type, from an S-Expression
     * that looks like
     * (SYNTHETO::MAKE-ALTERNATIVE :NAME name :PRODUCT product)
     *
     * @param buildFormArg The AST maker form.
     * @throws IllegalArgumentException If the list is malformed.
     */
    public static Alternative fromSExpression(SExpression buildFormArg) {
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

        if (! (buildForm.fourth().equals(SExpression.keyword("PRODUCT"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        if (! (buildForm.fifth() instanceof SExpressionList))
            throw new IllegalArgumentException("Wrong type of :PRODUCT argument.");
        Object typeRaw = ASTBuilder.fromSExpression(buildForm.fifth());
        if (! (typeRaw instanceof TypeProduct))
            throw new IllegalArgumentException("Wrong AST class returned by maker for :PRODUCT");
        TypeProduct product = (TypeProduct) typeRaw;

        return make(name, product);
    }

    public Identifier getName() {
        return name;
    }

    public TypeProduct getProduct() {
        return product;
    }

    @Override
    public SExpression toSExpression() {
        return SExpression.list(classMakerFn,
                SExpression.keyword("NAME"), name.toSExpression(),
                SExpression.keyword("PRODUCT"), product.toSExpression());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Alternative that = (Alternative) o;
        return name.equals(that.name) &&
                product.equals(that.product);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, product);
    }

    @Override
    public String toString() {
        return this.toString(0);
    }

    String toString(int indentLevel) {
        return line(indentLevel, "Alternative " + this.name + " {") +
                this.product.toString(indentLevel + 1) +
                line(indentLevel, "}");
    }
}
