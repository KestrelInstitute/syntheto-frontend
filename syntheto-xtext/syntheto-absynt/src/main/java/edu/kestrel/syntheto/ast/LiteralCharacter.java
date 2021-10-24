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
 * Syntheto character literals.
 */
public class LiteralCharacter extends Literal {

    /**
     * The character value of the character literal.
     * Always below 256.
     * Since Syntheto characters correspond to ACL2 characters,
     * which we can represent as 8-bit Java characters.
     */
    private final char value;

    /**
     * Constructs a character literal with the given character value.
     *
     * @param value The character value.
     * @throws IllegalArgumentException If the value is not below 256.
     */
    private LiteralCharacter(char value) throws IllegalArgumentException {
        if (value < 256) {
            this.value = value;
        } else {
            throw new IllegalArgumentException
                    ("Character too large: " + value + ".");
        }
    }

    /**
     * Builds a character literal with the given character value.
     *
     * @param value The character value.
     * @return The character literal.
     * @throws IllegalArgumentException If the value is not below 256.
     */
    public static LiteralCharacter make(char value) {
        return new LiteralCharacter(value);
    }

    /**
     * The symbol for the ACL2 function that creates an instance of this class.
     */
    public static final SExpressionSymbol classMakerFn = SExpression.syntheto("MAKE-LITERAL-CHARACTER");

    /**
     * Constructs a Character literal from an S-Expression
     * that looks like
     * (SYNTHETO::MAKE-LITERAL-CHARACTER :VALUE (CODE-CHAR code))
     *
     * @param buildFormArg The AST maker form.
     * @throws IllegalArgumentException If the list is malformed.
     */
    public static LiteralCharacter fromSExpression(SExpression buildFormArg) {
        if (!(buildFormArg instanceof SExpressionList))
            throw new IllegalArgumentException("Argument must be an SExpressionList");
        SExpressionList buildForm = (SExpressionList) buildFormArg;
        if (buildForm.length() != 3)
            throw new IllegalArgumentException("List not long enough");
        if (!(buildForm.first().equals(classMakerFn)))
            throw new IllegalArgumentException("Wrong function in list.");
        if (!(buildForm.second().equals(SExpression.keyword("VALUE"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        // The convention is that the string-to-S-Expression parser, when
        // it sees (CODE-CHAR 99), will generate an SExpressionCharacter,
        // not an SExpressionList containing a special symbol and an integer.
        // However, we could relax that here and recognize both forms if necessary.
        if (!(buildForm.third() instanceof SExpressionCharacter))
            throw new IllegalArgumentException("Wrong type of :VALUE argument.");
        SExpressionCharacter sChar = (SExpressionCharacter) buildForm.third();
        return make(sChar.getValue());
    }

    /**
     * Returns the character value of this integer literal.
     * @return The character value. Always below 256.
     */
    public char getValue() {
        return value;
    }

    /**
     * Translates this character literal to an s-expression.
     *
     * @return The s-expression.
     */
    @Override
    public SExpression toSExpression() {
        return SExpression.list(classMakerFn,
                SExpression.keyword("VALUE"),
                SExpression.character(this.value));
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
        LiteralCharacter that = (LiteralCharacter) o;
        return value == that.value;
    }

    /**
     * Returns a hash code for this object.
     *
     * @return The hash code.
     */
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return this.toString(0);
    }

    @Override
    String toString(int indentLevel) {
        String valueString = "'" + this.value + "'";
        return line(indentLevel, "Literal " + valueString);
    }
}
