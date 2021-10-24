/*
 * Copyright (C) 2021 Kestrel Institute (http://www.kestrel.edu)
 * License: 3-clause BSD license (https://opensource.org/licenses/BSD-3-Clause)
 * Main Author: Alessandro Coglio (coglio@kestrel.edu)
 * Contributing Author: Eric McCarthy (mccarthy@kestrel.edu)
 */

package edu.kestrel.syntheto.outcome;

import edu.kestrel.syntheto.sexpr.*;

/**
 * Syntheto outcomes.
 * These are the possible outcomes of submitting a Syntheto top-level construct
 * to ACL2 through the Bridge.
 */
public abstract class Outcome {

    /**
     * Information about this outcome.
     *
     * Every subclass constructor must initialize this member variable, but it can be "".
     */
    protected String info;

    public String getInfo() {
        return info;
    }

    /**
     * Default constructor just sets info
     */
    Outcome (String info) {
        this.info = info;
    }

    // Since the public static <Subclass> make() methods are static, we can't
    // describe them here in code, just in comments.

    // Every subclass must have:
    //
    // 1. a constructor that calls super(info).  If it has more instance variables other than info,
    //    it can assign them after calling super(info).
    // 2. a public static make(...) method that calls the constructor.
    //
    // 3. public static final SExpressionSymbol classMakerFn
    //    s symbol in the SYNTHETO package that makes the subclass (in ACL2)
    // 4. public static fromSExpression(SExpression buildFormArg)
    // 5. @Override public SExpression toSExpression()
    //
    // 6. @Override public boolean equals(Object a)
    // 7. @Override public int hashCode()
    // 8. @Override public String toString()
    //    with no arguments, that just calls the next method on 0,
    //    and which is used to serialize this object at the top level
    // 9. String toString(int indentLevel)
    //    which is used to serialize this object at level indentLevel


    /**
     * Translates an outcome to an s-expression.
     *
     * Because outcomes are intended to communicate information from ACL2 to an IDE, this method
     * is not of much production use, but it does help to test serialization/deserialization.
     *
     * @return The s-expression
     */
    public abstract SExpression toSExpression();


    //=========================================================================
    // equals, hashCode, toString, and line

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
     * This may become useful at some point.
     * We don't have Syntheto surface syntax for outcomes, but some outcome classes
     * do have Syntheto abstract syntax components.
     *
     * @return The string.
     */
    @Override
    public abstract String toString();

    public abstract String toString(int indentLevel);

    /**
     * Create an indented line with some content.
     *
     * This is a utility used by the {@link #toString()} methods.
     * We generate string representations of the outcome objects,
     * some of which  have components which are ASTNodes.
     *
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
