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
 * Syntheto top level function definition.
 */
public class TopLevelFunction extends TopLevel {

    private final FunctionDefinition fndef;

    private TopLevelFunction(FunctionDefinition fndef) {
        this.fndef = fndef;
    }

    public static TopLevelFunction make(FunctionDefinition fndef) {
        return new TopLevelFunction(fndef);
    }

    /**
     * The symbol for the ACL2 function that creates an instance of this class.
     */
    public static final SExpressionSymbol classMakerFn = SExpression.syntheto("MAKE-TOPLEVEL-FUNCTION");

    /**
     * Constructs a top level function definition from an S-Expression
     * that looks like
     * (SYNTHETO::MAKE-TOPLEVEL-FUNCTION :GET fndef)
     *
     * @param buildFormArg The AST maker form.
     * @throws IllegalArgumentException If the list is malformed.
     */
    public static TopLevelFunction fromSExpression(SExpression buildFormArg) {
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
        Object fndefRaw = ASTBuilder.fromSExpression(buildForm.third());
        if (!(fndefRaw instanceof FunctionDefinition))
            throw new IllegalArgumentException("Wrong AST class returned by maker for :GET");
        FunctionDefinition fndef = (FunctionDefinition) fndefRaw;

        return make(fndef);
    }

    public FunctionDefinition getFunctionDefinition() {
        return fndef;
    }

    @Override
    public SExpression toSExpression() {
        return SExpression.list(classMakerFn,
                SExpression.keyword("GET"), fndef.toSExpression());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TopLevelFunction tltype = (TopLevelFunction) o;
        return fndef.equals(tltype.fndef);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fndef);
    }

    @Override
    public String toString() { return this.toString(0); }

    @Override
    // This must be public because it is referenced in outcome.TransformationSuccess.toString()
    public String toString(int indentLevel) {
        return this.fndef.toString(indentLevel);
    }
}