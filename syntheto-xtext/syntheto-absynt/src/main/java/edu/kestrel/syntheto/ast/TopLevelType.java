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
 * Syntheto top level type definition.
 */
public class TopLevelType extends TopLevel {

    private final TypeDefinition typedef;

    private TopLevelType(TypeDefinition typedef) {
        this.typedef = typedef;
    }

    public static TopLevelType make(TypeDefinition typedef) {
        return new TopLevelType(typedef);
    }

    /**
     * The symbol for the ACL2 function that creates an instance of this class.
     */
    public static final SExpressionSymbol classMakerFn = SExpression.syntheto("MAKE-TOPLEVEL-TYPE");

    /**
     * Constructs a top level type definition from an S-Expression
     * that looks like
     * (SYNTHETO::MAKE-TOPLEVEL-TYPE :GET typedef)
     *
     * @param buildFormArg The AST maker form.
     * @throws IllegalArgumentException If the list is malformed.
     */
    public static TopLevelType fromSExpression(SExpression buildFormArg) {
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
        Object typedefRaw = ASTBuilder.fromSExpression(buildForm.third());
        if (!(typedefRaw instanceof TypeDefinition))
            throw new IllegalArgumentException("Wrong AST class returned by maker for :GET");
        TypeDefinition typedef = (TypeDefinition) typedefRaw;

        return make(typedef);
    }

    public TypeDefinition getTypeDefinition() {
        return typedef;
    }

    @Override
    public SExpression toSExpression() {
        return SExpression.list(classMakerFn,
                SExpression.keyword("GET"), typedef.toSExpression());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TopLevelType tltype = (TopLevelType) o;
        return typedef.equals(tltype.typedef);
    }

    @Override
    public int hashCode() {
        return Objects.hash(typedef);
    }

    @Override
    public String toString() { return this.toString(0); }

    @Override
    // This must be public because it is referenced in outcome.TransformationSuccess.toString()
    public String toString(int indentLevel) {
        return this.typedef.toString(indentLevel);
    }
}