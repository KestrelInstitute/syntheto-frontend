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
 * Syntheto defined types.
 * These are references to defined types, by name.
 * The actual definitions are represented in other classes.
 */
public class TypeDefined extends Type {

    private final Identifier name;

    private TypeDefined(Identifier name) {
        this.name = name;
    }

    public static TypeDefined make(Identifier name) {
        return new TypeDefined(name);
    }

    /**
     * The symbol for the ACL2 function that creates an instance of this class.
     */
    public static final SExpressionSymbol classMakerFn = SExpression.syntheto("MAKE-TYPE-DEFINED");

    /**
     * Constructs a type declaration referring to a defined named type,
     * from an S-Expression that looks like
     * (SYNTHETO::MAKE-TYPE-DEFINED :NAME NAME)
     *
     * @param buildFormArg The AST maker form.
     * @throws IllegalArgumentException If the list is malformed.
     */
    public static TypeDefined fromSExpression(SExpression buildFormArg) {
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

    public Identifier getName() {
        return this.name;
    }

    @Override
    public SExpression toSExpression() {
        SExpression idSExpr = name.toSExpression();
        return SExpression.list(classMakerFn,
                                SExpression.keyword("NAME"), idSExpr);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypeDefined that = (TypeDefined) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return this.toString(0);
    }

    @Override
    String toString(int indentLevel) {
        return line(indentLevel, "TypeDefined " + this.name);
    }
}
