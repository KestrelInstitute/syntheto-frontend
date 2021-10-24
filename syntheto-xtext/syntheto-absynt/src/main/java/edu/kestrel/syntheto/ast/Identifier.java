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
 * Syntheto identifiers.
 * These are non-empty sequences of ASCII characters limited to
 * letters, digits, and underscores, starting with a letter or underscore;
 * they are case-sensitive.
 */
public class Identifier extends ASTNode {

    /**
     * The string of characters that forms the identifier.
     * Never null.
     * Satisfies {@link #isValid(String)}.
     */
    private final String name;

    /**
     * Checks if a character is an (ASCII uppercase or lowercase) letter.
     *
     * @param ch The character to check.
     * @return {@code true} if the character is an ASCII letter,
     * {@code false} otherwise.
     */
    private static boolean isLetter(char ch) {
        return ('A' <= ch && ch <= 'Z') || ('a' <= ch && ch <= 'z');
    }

    /**
     * Checks if a character is a digit.
     *
     * @param ch The character to check.
     * @return {@code true} if the character is a digit,
     * {@code false} otherwise.
     */
    private static boolean isDigit(char ch) {
        return '0' <= ch && ch <= '9';
    }

    /**
     * Checks if a character is an underscore.
     *
     * @param ch The character to check.
     * @return {@code true} if the character is an underscore,
     * {@code false} otherwise.
     */
    private static boolean isUnderscore(char ch) {
        return ch == '_';
    }

    /**
     * Checks if a string of characters forms a valid identifier.
     * It must be non-empty, start with a letter or underscore,
     * and contain only letters, digits, and underscores.
     *
     * @param string The string of characters that forms the identifier.
     * @return {@code true} if the string is valid, {@code false} otherwise.
     */
    private static boolean isValid(String string) {
        int len = string.length();
        if (len == 0)
            return false;
        char first = string.charAt(0);
        if (!(isLetter(first) || isUnderscore(first)))
            return false;
        for (int i = 1; i < len; ++i) {
            char ch = string.charAt(i);
            if (!(isLetter(ch) || isDigit(ch) || isUnderscore(ch)))
                return false;
        }
        return true;
    }

    /**
     * Constructs an identifier with the given string of characters.
     *
     * @param name The string of characters.
     * @return The identifier.
     * @throws IllegalArgumentException If the string is null,
     * or does not start with a letter or underscore,
     * or contains anything besides letters, digits, and underscores.
     */
    private Identifier(String name) throws IllegalArgumentException {
        if (name == null) {
            throw new IllegalArgumentException("Null argument.");
        } else if (isValid(name)) {
            this.name = name;
        } else {
            throw new IllegalArgumentException("Invalid identifier: " + name);
        }
    }

    /**
     * Builds an identifier with the given string of characters.
     *
     * @param name The string of characters.
     * @return The identifier.
     * @throws IllegalArgumentException If the string is null,
     * or does not start with a letter or underscore,
     * or contains anything besides letters, digits, and underscores.
     */
    public static Identifier make(String name) throws IllegalArgumentException {
        return new Identifier(name);
    }

    /**
     *
     * The symbol for the ACL2 function that creates an instance of this class.
     */
    public static final SExpressionSymbol classMakerFn = SExpression.syntheto("MAKE-IDENTIFIER");

    /**
     * Constructs an identifier from an S-Expression
     * that looks like
     * (SYNTHETO::MAKE-IDENTIFIER :NAME NAME)
     *
     * @param buildFormArg The AST maker form.
     * @throws IllegalArgumentException If the list is malformed.
     */
    public static Identifier fromSExpression(SExpression buildFormArg) {
        if (! (buildFormArg instanceof SExpressionList))
            throw new IllegalArgumentException("Argument must be an SExpressionList.");
        SExpressionList buildForm = (SExpressionList) buildFormArg;
        if (buildForm.length() != 3)
            throw new IllegalArgumentException("List not the right length.");
        if (! (buildForm.first().equals(classMakerFn)))
            throw new IllegalArgumentException("Wrong function in list.");

        if (! (buildForm.second().equals(SExpression.keyword("NAME"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        if (! (buildForm.third() instanceof SExpressionString))
            throw new IllegalArgumentException("Wrong type of :NAME argument.");
        String name = ((SExpressionString) buildForm.third()).getValue();

        return make(name);
    }

    /**
     * Returns the string of characters that forms this identifier.
     *
     * @return The string of characters that forms this identifier..
     */
    public String getName() {
        return this.name;
    }

    /**
     * Translates this identifier into an s-expression.
     * Syntheto identifiers are turned into the corresponding ACL2 strings,
     * as s-expressions.
     *
     * @return The s-expression for this identifier.
     */
    public SExpression toSExpression() {
        return SExpression.list(classMakerFn, SExpression.keyword("NAME"), SExpression.string(this.name));
    }

    /**
     * Checks if this object is equal to another object.
     *
     * @param o The object to compare this object with.
     * @return {@code true} if they are equal, {@code false} otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Identifier that = (Identifier) o;
        return name.equals(that.name);
    }

    /**
     * Returns a hash code for this object.
     *
     * @return The hash code.
     */
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return this.name;
    }
}
