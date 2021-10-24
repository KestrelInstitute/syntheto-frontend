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
 * Syntheto product type definer.  The body of a product type definition.
 */
public class TypeDefinerProduct extends TypeDefiner {

    private final TypeProduct product;

    private TypeDefinerProduct(TypeProduct product) { this.product = product; }

    public static TypeDefinerProduct make(TypeProduct product) { return new TypeDefinerProduct(product); }

    /**
     * The symbol for the ACL2 function that creates an instance of this class.
     */
    public static final SExpressionSymbol classMakerFn = SExpression.syntheto("MAKE-TYPE-DEFINER-PRODUCT");

    /**
     * Constructs a product type definer from an S-Expression
     * that looks like
     * (SYNTHETO::MAKE-TYPE-DEFINER-PRODUCT :GET product)
     *
     * @param buildFormArg The AST maker form.
     * @throws IllegalArgumentException If the list is malformed.
     */
    public static TypeDefinerProduct fromSExpression(SExpression buildFormArg) {
        if (!(buildFormArg instanceof SExpressionList))
            throw new IllegalArgumentException("Argument must be an SExpressionList");
        SExpressionList buildForm = (SExpressionList) buildFormArg;
        if (buildForm.length() != 3)
            throw new IllegalArgumentException("List wrong length.");
        if (!(buildForm.first().equals(classMakerFn)))
            throw new IllegalArgumentException("Wrong function in list.");

        if (!(buildForm.second().equals(SExpression.keyword("GET"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        if (!(buildForm.third() instanceof SExpressionList))
            throw new IllegalArgumentException("Wrong type of :GET argument.");
        Object productRaw = ASTBuilder.fromSExpression(buildForm.third());
        if (!(productRaw instanceof TypeProduct))
            throw new IllegalArgumentException("Wrong AST class returned by maker for :GET");
        TypeProduct product = (TypeProduct) productRaw;

        return make(product);
    }

    public TypeProduct getProduct() {
        return product;
    }

    @Override
    public SExpression toSExpression() {
        return SExpression.list(classMakerFn,
                SExpression.keyword("GET"), product.toSExpression());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypeDefinerProduct tdprod = (TypeDefinerProduct) o;
        return product.equals(tdprod.product);
    }

    @Override
    public int hashCode() {
        return Objects.hash(product);
    }

    @Override
    public String toString() {
        return this.toString(0);
    }

    @Override
    String toString(int indentLevel) {
        return this.product.toString(indentLevel);
    }

}
