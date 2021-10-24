/*
 * Copyright (C) 2020 Kestrel Institute (http://www.kestrel.edu)
 * License: 3-clause BSD license (https://opensource.org/licenses/BSD-3-Clause)
 * Main Author: Alessandro Coglio (coglio@kestrel.edu)
 * Contributing Author: Eric McCarthy (mccarthy@kestrel.edu)
 */

package edu.kestrel.syntheto.ast;

import edu.kestrel.syntheto.sexpr.*;

/**
 * Syntheto top-level constructs.
 */
public abstract class TopLevel extends ASTNode {
    /**
     * Translates this top-level construct to an s-expression.
     *
     * @return The s-expression.
     */
    @Override
    public abstract SExpression toSExpression();

    @Override
    public abstract boolean equals(Object obj);

    @Override
    public abstract int hashCode();

    // This must be public because it is referenced in outcome.TransformationSuccess.toString()
    public abstract String toString(int indentLevel);
}
