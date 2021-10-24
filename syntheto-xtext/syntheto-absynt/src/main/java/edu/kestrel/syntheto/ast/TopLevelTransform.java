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
 * Syntheto top level transform definition.
 */

public class TopLevelTransform extends TopLevel {

    private final Transform trans;

    private TopLevelTransform(Transform trans) {
        this.trans = trans;
    }

    public static TopLevelTransform make(Transform trans) {
        return new TopLevelTransform(trans);
    }

    /**
     * The symbol for the ACL2 function that creates an instance of this class.
     */
    public static final SExpressionSymbol classMakerFn = SExpression.syntheto("MAKE-TOPLEVEL-TRANSFORM");

    /**
     * Constructs a top level transform definition from an S-Expression
     * that looks like
     * (SYNTHETO::MAKE-TOPLEVEL-TRANSFORM :GET trans)
     *
     * @param buildFormArg The AST maker form.
     * @throws IllegalArgumentException If the list is malformed.
     */
    public static TopLevelTransform fromSExpression(SExpression buildFormArg) {
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
        Object transRaw = ASTBuilder.fromSExpression(buildForm.third());
        if (!(transRaw instanceof Transform))
            throw new IllegalArgumentException("Wrong AST class returned by maker for :GET");
        Transform trans = (Transform) transRaw;

        return make(trans);
    }

    public Transform getTransform() {
        return trans;
    }

    @Override
    public SExpression toSExpression() {
        return SExpression.list(classMakerFn,
                SExpression.keyword("GET"), trans.toSExpression());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TopLevelTransform tltype = (TopLevelTransform) o;
        return trans.equals(tltype.trans);
    }

    @Override
    public int hashCode() {
        return Objects.hash(trans);
    }

    @Override
    public String toString() {
        return this.toString(0);
    }

    @Override
    // This must be public because it is referenced in outcome.TransformationSuccess.toString()
    public String toString(int indentLevel) {
        return this.trans.toString(indentLevel);
    }

}