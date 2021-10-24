/*
 * Copyright (C) 2020 Kestrel Institute (http://www.kestrel.edu)
 * License: 3-clause BSD license (https://opensource.org/licenses/BSD-3-Clause)
 * Main Author: Alessandro Coglio (coglio@kestrel.edu)
 * Contributing Author: Eric McCarthy (mccarthy@kestrel.edu)
 */

package edu.kestrel.syntheto.sexpr;

import java.math.BigInteger;
import java.util.List;

/**
 * S-expressions for ACL2.
 */
public abstract class SExpression {

    /**
     * Builds an s-expression consisting of an ACL2 integer.
     *
     * @param value A BigInteger.
     * @return The s-expression.
     */
    public static SExpressionInteger integer(BigInteger value) {
        return SExpressionInteger.make(value);
    }

    /**
     * Builds an s-expression consisting of an ACL2 integer.
     *
     * @param value A Java int.
     * @return The s-expression.
     */
    public static SExpressionInteger integer(int value) {
        return SExpressionInteger.make(BigInteger.valueOf(value));
    }

    /**
     * Builds an s-expression consisting of an ACL2 integer.
     *
     * @param value A Java string containing decimal digits that is parsed.
     * @return The s-expression.
     */
    public static SExpressionInteger integer(String value) {
        return SExpressionInteger.make(new BigInteger(value));
    }

    /**
     * Builds an s-expression consisting of an ACL2 character.
     *
     * @param value The character.
     * @return The s-expression.
     */
    public static SExpressionCharacter character(char value) {
        return SExpressionCharacter.make(value);
    }

    /**
     * Builds an s-expression consisting of an ACL2 character.
     *
     * @param value The character as an int.  If not in the range [0 .. 256), throws an exception.
     * @return The s-expression.
     */
    public static SExpressionCharacter character(int value) {
        if (value >= 0 && value < 256) {
            return SExpressionCharacter.make((char) value);
        } else {
            throw new IllegalArgumentException("value must be from 0 to 255, inclusive");
        }
    }

    /**
     * Builds an s-expression consisting of an ACL2 string.
     *
     * @param value The string.
     * @return The s-expression.
     */
    public static SExpressionString string(String value) {
        return SExpressionString.make(value);
    }

    /**
     * Builds an s-expression consisting of an ACL2 symbol.
     *
     * @param pkg The package name.
     * @param name The symbol name.
     * @return The s-expression.
     */
    public static SExpressionSymbol symbol(String pkg, String name) {
        return SExpressionSymbol.make(pkg, name);
    }

    /**
     * Builds an s-expression consisting of the special ACL2 symbol T.
     *
     * @return The s-expression.
     */
    public static SExpressionSymbol T() {
        return SExpressionSymbol.makeSpecial("T");
    }

    /**
     * Builds an s-expression consisting of the special ACL2 symbol NIL.
     *
     * @return The s-expression.
     */
    public static SExpressionSymbol NIL() {
        return SExpressionSymbol.makeSpecial("NIL");
    }

    /**
     * Builds an s-expression consisting of the special ACL2 symbol LIST.
     *
     * @return The s-expression.
     */
    public static SExpressionSymbol LIST() {
        return SExpressionSymbol.makeSpecial("LIST");
    }

    /**
     * Builds an s-expression consisting of the special ACL2 symbol CODE-CHAR.
     *
     * @return The s-expression.
     */
    public static SExpressionSymbol codeChar() {
        return SExpressionSymbol.makeSpecial("CODE-CHAR");
    }

    /**
     * Builds an s-expression consisting of an ACL2 keyword.
     *
     * @param name The keyword name.
     * @return The s-expression.
     */
    public static SExpressionSymbol keyword(String name) {
        return SExpressionSymbol.makeKeyword(name);
    }

    /**
     * Builds an s-expression consisting of an ACL2 Syntheto symbol.
     *
     * @param name The symbol name.
     * @return The s-expression.
     */
    public static SExpressionSymbol syntheto(String name) {
        return SExpressionSymbol.makeSyntheto(name);
    }

    /**
     * Builds an s-expression consisting of an ACL2 proper list.
     *
     * @param sexprs The elements of the list.
     * @return The s-expression.
     */
    public static SExpressionList list(SExpression... sexprs) {
        return SExpressionList.makeProper(sexprs);
    }

    /**
     * Builds an s-expression consisting of an ACL2 proper list.
     *
     * @param sexprs The elements of the list, in a Java List.
     * @return The s-expression.
     */
    public static SExpressionList list(List<SExpression> sexprs) {
        return SExpressionList.makeProper(sexprs);
    }

    /**
     * Builds an s-expression that when evaluated, will construct an ACL2 list.
     * The elements should also be evaluable.
     *
     * @param sexprs The elements of the list.
     * @return The s-expression.
     */
    public static SExpressionList listMaker(SExpression... sexprs) {
        return SExpressionList.cons(SExpression.LIST(),
                SExpressionList.makeProper(sexprs));
    }
    public static SExpressionList listMaker(List<SExpression> sexprs) {
        return SExpressionList.cons(SExpression.LIST(),
                SExpressionList.makeProper(sexprs));
    }


    /**
     * Builds an s-expression consisting of a dotted list.
     *
     * @param finalCdr The final {@code cdr}.  (Restricted to be an atom.)
     * @param sexprs The elements of the list.
     * @return The s-expression.
     */
    public static SExpressionList listStar(SExpressionAtom finalCdr,
                                           SExpression... sexprs) {
        return SExpressionList.makeDotted(finalCdr, sexprs);
    }

    /**
     * Serialize this s-expression into a string.
     * Subclasses must implement this to be instantiatable.
     *
     * @return The serialized s-expression.
     */
    @Override
    public abstract String toString();

    /**
     * Checks if this object is equal to another object.
     *
     * @param obj The object to compare this s-expression with.
     * @return {@code true} if they are equal, {@code false} otherwise.
     */
    @Override
    public abstract boolean equals(Object obj);

    /**
     * Returns a hash code for this object.
     *
     * @return The hash code.
     */
    @Override
    public abstract int hashCode();
}
