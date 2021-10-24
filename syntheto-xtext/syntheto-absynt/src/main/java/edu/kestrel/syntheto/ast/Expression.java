/*
 * Copyright (C) 2020 Kestrel Institute (http://www.kestrel.edu)
 * License: 3-clause BSD license (https://opensource.org/licenses/BSD-3-Clause)
 * Main Author: Alessandro Coglio (coglio@kestrel.edu)
 * Contributing Author: Eric McCarthy (mccarthy@kestrel.edu)
 */

package edu.kestrel.syntheto.ast;

import edu.kestrel.syntheto.sexpr.*;

/**
 * Syntheto expressions.
 */
public abstract class Expression extends ASTNode {

    /**
     * Translates this expression to an s-expression.
     *
     * @return The s-expression.
     */
    @Override
    public abstract SExpression toSExpression();

    /**
     * Checks if this object is equal to another object.
     *
     * @param o The object to compare this object with.
     * @return {@code true} if they are equal, {@code false} otherwise.
     */
    @Override
    public abstract boolean equals(Object o);

    /**
     * Returns a hash code for this object.
     *
     * @return The hash code.
     */
    @Override
    public abstract int hashCode();

    // This must be public because it is referenced in outcome.ProofObligationFailure.toString()
    public abstract String toString(int indentLevel);
}
