/*
 * Copyright (C) 2020 Kestrel Institute (http://www.kestrel.edu)
 * License: 3-clause BSD license (https://opensource.org/licenses/BSD-3-Clause)
 * Main Author: Alessandro Coglio (coglio@kestrel.edu)
 * Contributing Author: Eric McCarthy (mccarthy@kestrel.edu)
 */

package edu.kestrel.syntheto.ast;

import edu.kestrel.syntheto.sexpr.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Syntheto top level function recursion (i.e. mutually recursive function definitions).
 */
public class TopLevelFunctions extends TopLevel {

    private final FunctionRecursion fnrec;

    private TopLevelFunctions(FunctionRecursion fnrec) {
        this.fnrec = fnrec;
    }

    public static TopLevelFunctions make(FunctionRecursion functionRecursion) {
        return new TopLevelFunctions(functionRecursion);
    }

    /**
     * The symbol for the ACL2 function that creates an instance of this class.
     */
    public static final SExpressionSymbol classMakerFn = SExpression.syntheto("MAKE-TOPLEVEL-FUNCTIONS");

    /**
     * Constructs a top level function recursion group of function definitions from an S-Expression
     * that looks like
     * (SYNTHETO::MAKE-TOPLEVEL-FUNCTIONS :GET function-recursion)
     *
     * @param buildFormArg The AST maker form.
     * @throws IllegalArgumentException If the list is malformed.
     */
    public static TopLevelFunctions fromSExpression(SExpression buildFormArg) {
        if (! (buildFormArg instanceof SExpressionList))
            throw new IllegalArgumentException("Argument must be an SExpressionList");
        SExpressionList buildForm = (SExpressionList) buildFormArg;
        if (buildForm.length() != 3)
            throw new IllegalArgumentException("List wrong length.");
        if (! (buildForm.first().equals(classMakerFn)))
            throw new IllegalArgumentException("Wrong function in list.");

        if (! (buildForm.second().equals(SExpression.keyword("GET"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        if (! (buildForm.third() instanceof SExpressionList))
            throw new IllegalArgumentException("Wrong type of :GET argument.");
        Object fnrecRaw = ASTBuilder.fromSExpression(buildForm.third());
        if (! (fnrecRaw instanceof FunctionRecursion))
            throw new IllegalArgumentException("Wrong AST class returned by maker for :GET");
        FunctionRecursion fnrec = (FunctionRecursion) fnrecRaw;

        return make(fnrec);
    }

    public FunctionRecursion getFunctionRecursion() {
        return fnrec;
    }

    @Override
    public SExpression toSExpression() {
        return SExpression.list(classMakerFn,
                SExpression.keyword("GET"), fnrec.toSExpression());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TopLevelFunctions that = (TopLevelFunctions) o;
        return fnrec.equals(that.fnrec);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fnrec);
    }

    @Override
    public String toString() {
        return this.toString(0);
    }

    @Override
    // This must be public because it is referenced in outcome.TransformationSuccess.toString()
    public String toString(int indentLevel) {
        return this.fnrec.toString(indentLevel);
    }

}
