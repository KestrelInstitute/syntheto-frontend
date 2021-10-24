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
 * Syntheto integer type.
 */
public class TypeInteger extends TypePrimitive {

    private static final TypeInteger singleton = new TypeInteger();

    public static TypeInteger make() {
        return singleton;
    }

    /**
     * The symbol for the ACL2 function that creates an instance of this class.
     */
    public static final SExpressionSymbol classMakerFn = SExpression.syntheto("MAKE-TYPE-INTEGER");

    /**
     * Constructs an integer type indicator from an S-Expression
     * that looks like
     * (SYNTHETO::MAKE-TYPE-INTEGER)
     * Actually, just looks up the singleton.
     *
     * @param buildFormArg The AST maker form.
     * @throws IllegalArgumentException If the list is malformed.
     */
    public static TypeInteger fromSExpression(SExpression buildFormArg) {
        if (! (buildFormArg instanceof SExpressionList))
            throw new IllegalArgumentException("Argument must be an SExpressionList.");
        SExpressionList buildForm = (SExpressionList) buildFormArg;
        if (buildForm.length() != 1)
            throw new IllegalArgumentException("List not the right length.");
        if (! (buildForm.first().equals(classMakerFn)))
            throw new IllegalArgumentException("Wrong function in list.");

        return make();
    }

    /**
     * Translates this (unique) integer type to an s-expression.
     *
     * @return The s-expression.
     */
    @Override
    public SExpression toSExpression() {
        return SExpression.list(classMakerFn);
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }

    @Override
    public String toString() {
        return this.toString(0);
    }

    @Override
    String toString(int indentLevel) {
        return line(indentLevel, "TypeInteger");
    }
}
