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
 * Syntheto top level (function) specification.
 */
public class TopLevelTheorem extends TopLevel {

    private final Theorem theorem;

    private TopLevelTheorem(Theorem theorem) {
        this.theorem = theorem;
    }

    public static TopLevelTheorem make(Theorem theorem) {
        return new TopLevelTheorem(theorem);
    }

    /**
     * The symbol for the ACL2 function that creates an instance of this class.
     */
    public static final SExpressionSymbol classMakerFn = SExpression.syntheto("MAKE-TOPLEVEL-THEOREM");

    /**
     * Constructs a top level theorem definition from an S-Expression
     * that looks like
     * (SYNTHETO::MAKE-TOPLEVEL-THEOREM :GET theorem)
     *
     * @param buildFormArg The AST maker form.
     * @throws IllegalArgumentException If the list is malformed.
     */
    public static TopLevelTheorem fromSExpression(SExpression buildFormArg) {
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
        Object theoremRaw = ASTBuilder.fromSExpression(buildForm.third());
        if (!(theoremRaw instanceof Theorem))
            throw new IllegalArgumentException("Wrong AST class returned by maker for :GET");
        Theorem theorem = (Theorem) theoremRaw;

        return make(theorem);
    }

    public Theorem getTheorem() {
        return theorem;
    }

    @Override
    public SExpression toSExpression() {
        return SExpression.list(classMakerFn,
                SExpression.keyword("GET"), theorem.toSExpression());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TopLevelTheorem tltype = (TopLevelTheorem) o;
        return theorem.equals(tltype.theorem);
    }

    @Override
    public int hashCode() {
        return Objects.hash(theorem);
    }

    @Override
    public String toString() { return this.toString(0); }

    @Override
    // This must be public because it is referenced in outcome.TransformationSuccess.toString()
    public String toString(int indentLevel) {
        return this.theorem.toString(indentLevel);
    }
}