/*
 * Copyright (C) 2020 Kestrel Institute (http://www.kestrel.edu)
 * License: 3-clause BSD license (https://opensource.org/licenses/BSD-3-Clause)
 * Main Author: Alessandro Coglio (coglio@kestrel.edu)
 * Contributing Author: Eric McCarthy (mccarthy@kestrel.edu)
 */

package edu.kestrel.syntheto.ast;

import edu.kestrel.syntheto.sexpr.*;
// TODO If this ast building strategy works out, we might need .*:
import edu.kestrel.syntheto.sexpr.*;


import java.math.BigInteger;
import java.util.Objects;

/**
 * Syntheto integer literals.
 */
public class LiteralInteger extends Literal {

    /**
     * The integer value of the integer literal.
     * Never null.
     * Never negative.  Use expression-unary-minus to negate.
     * Since Syntheto integers correspond to ACL2 integers,
     * they are (virtually) unbounded, so we use Java big integers.
     */
    private final BigInteger value;

    /**
     * Constructs an integer literal with the given integer value.
     *
     * @param value The integer value.
     * @throws IllegalArgumentException If the value is null or negative.
     */
    private LiteralInteger(BigInteger value) throws IllegalArgumentException {
        if (value == null) {
            throw new IllegalArgumentException("Null value.");
        } else if (value.compareTo(BigInteger.ZERO) < 0) {
            throw new IllegalArgumentException
                    ("Negative value: " + value + ".");
        } else {
            this.value = value;
        }
    }

    /**
     * Builds an integer literal with the given BigInteger value.
     *
     * @param value The BigInteger value.
     * @return The integer literal.
     * @throws IllegalArgumentException If the value is null or negative.
     */
    public static LiteralInteger make(BigInteger value) {
        return new LiteralInteger(value);
    }

    /**
     * Builds an integer literal given an int.
     *
     * @param value The int value.
     * @return The integer literal.
     * @throws IllegalArgumentException If the value is null or negative.
     */
    public static LiteralInteger make(int value) { return LiteralInteger.make(BigInteger.valueOf(value)); }

    /**
     * The symbol for the ACL2 function that creates an instance of this class.
     */
    public static final SExpressionSymbol classMakerFn = SExpression.syntheto("MAKE-LITERAL-INTEGER");

    /**
     * Constructs an Integer literal from an S-Expression
     * that looks like
     * (SYNTHETO::MAKE-LITERAL-INTEGER :VALUE val)
     *
     * @param buildFormArg The AST maker form.
     * @throws IllegalArgumentException If the list is malformed.
     */
    public static LiteralInteger fromSExpression(SExpression buildFormArg) {
        if (!(buildFormArg instanceof SExpressionList))
            throw new IllegalArgumentException("Argument must be an SExpressionList");
        SExpressionList buildForm = (SExpressionList) buildFormArg;
        if (buildForm.length() != 3)
            throw new IllegalArgumentException("List wrong length.");
        if (!(buildForm.first().equals(classMakerFn)))
            throw new IllegalArgumentException("Wrong function in list.");

        if (!(buildForm.second().equals(SExpression.keyword("VALUE"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        if (!(buildForm.third() instanceof SExpressionInteger))
            throw new IllegalArgumentException("Wrong type of :VALUE argument.");
        SExpressionInteger sInt = (SExpressionInteger) buildForm.third();
        if (sInt.getValue().signum() == -1)
            throw new IllegalArgumentException("Syntheto does not have negative integer literals like " + sInt + "." +
                    " Please wrap it with a unary minus expression.");
        return make(sInt.getValue());
    }

    /**
     * Returns the integer value of this integer literal.
     *
     * @return The integer value. Never null. Never negative.
     */
    public BigInteger getValue() {
        return value;
    }

    /**
     * Translates this integer literal to an s-expression.
     *
     * @return The s-expression.
     */
    @Override
    public SExpression toSExpression() {
        return SExpression.list(classMakerFn,
                    SExpression.keyword("VALUE"),
                    SExpression.integer(value));
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
        LiteralInteger that = (LiteralInteger) o;
        return value.equals(that.value);
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
        String valueString = this.value.toString();
        return line(indentLevel, "Literal " + valueString);
    }
}
