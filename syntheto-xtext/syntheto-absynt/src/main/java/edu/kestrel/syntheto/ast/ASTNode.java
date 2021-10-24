/*
 * Copyright (C) 2020 Kestrel Institute (http://www.kestrel.edu)
 * License: 3-clause BSD license (https://opensource.org/licenses/BSD-3-Clause)
 * Main Author: Alessandro Coglio (coglio@kestrel.edu)
 * Contributing Author: Eric McCarthy (mccarthy@kestrel.edu)
 */

package edu.kestrel.syntheto.ast;

import edu.kestrel.syntheto.sexpr.*;

/**
 * Syntheto abstract syntax nodes.
 */
public abstract class ASTNode {

    /**
     * Translates this expression to an s-expression.
     *
     * @return The s-expression.
     */
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

    /**
     * Return a human-readable string representation of the node.
     *
     * @return The string.
     */
    @Override
    public abstract String toString();

    /**
     * Create an indented line with some content.
     *
     * This is a utility used by the {@link #toString()} methods.
     * We generate string representations of the ASTs
     * as sequences of indented lines.
     * A line consists of some indentation, some actual content, and a newline.
     * This method creates the line from the content,
     * by prepending the indentation and appending the newline.
     * The indentation is specified as a numeric level.
     * We use 4 spaces for each indentation level;
     * this can be easily changed, just in this method.
     *
     * @param level Indentation level. Never negative.
     * @param content Actual string content.
     * @return The line.
     */
    static String line(int level, String content) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < level; ++i) s.append("    ");
        s.append(content);
        s.append('\n');
        return new String(s);
    }
}
