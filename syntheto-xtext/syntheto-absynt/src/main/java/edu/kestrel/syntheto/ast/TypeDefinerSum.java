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
 * Syntheto sum type definer.  The body of a sum type definition.
 */
public class TypeDefinerSum extends TypeDefiner {

    private final TypeSum sum;

    private TypeDefinerSum(TypeSum sum) { this.sum = sum; }

    public static TypeDefinerSum make(TypeSum sum) { return new TypeDefinerSum(sum); }

    /**
     * The symbol for the ACL2 function that creates an instance of this class.
     */
    public static final SExpressionSymbol classMakerFn = SExpression.syntheto("MAKE-TYPE-DEFINER-SUM");

    /**
     * Constructs a sum type definer from an S-Expression
     * that looks like
     * (SYNTHETO::MAKE-TYPE-DEFINER-SUM :GET sum)
     *
     * @param buildFormArg The AST maker form.
     * @throws IllegalArgumentException If the list is malformed.
     */
    public static TypeDefinerSum fromSExpression(SExpression buildFormArg) {
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
        Object sumRaw = ASTBuilder.fromSExpression(buildForm.third());
        if (!(sumRaw instanceof TypeSum))
            throw new IllegalArgumentException("Wrong AST class returned by maker for :GET");
        TypeSum sum = (TypeSum) sumRaw;

        return make(sum);
    }

    public TypeSum getSum() {
        return sum;
    }

    @Override
    public SExpression toSExpression() {
        return SExpression.list(classMakerFn,
                SExpression.keyword("GET"), sum.toSExpression());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypeDefinerSum tdsum = (TypeDefinerSum) o;
        return sum.equals(tdsum.sum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sum);
    }

    @Override
    public String toString() {
        return this.toString(0);
    }

    @Override
    String toString(int indentLevel) {
        return this.sum.toString(indentLevel);
    }

}
