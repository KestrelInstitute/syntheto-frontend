/*
 * Copyright (C) 2020 Kestrel Institute (http://www.kestrel.edu)
 * License: 3-clause BSD license (https://opensource.org/licenses/BSD-3-Clause)
 * Main Author: Alessandro Coglio (coglio@kestrel.edu)
 * Contributing Author: Eric McCarthy (mccarthy@kestrel.edu)
 */

package edu.kestrel.syntheto.sexpr;

import java.util.Objects;

/**
 * A lexer token representing a lisp symbol.
 */
public class TokenSymbol extends Token {

    public String packageName;  // keyword package here is ""
    // we do not model external (:) vs internal (::)
    // Note: SExpressionSymbol does not allow a package of the empty string.
    // It is best to make it with SExpression.keyword(name).
    public String name;

    public TokenSymbol(String packageName, String name) {
        this.packageName = packageName;
        this.name = name;
    }

    private static final TokenSymbol tokenCodeChar = new TokenSymbol(null, "CODE-CHAR");

    public static TokenSymbol CodeChar() {
        return tokenCodeChar;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TokenSymbol that = (TokenSymbol) o;

        // If either packageName is null but not both, then return false.
        // (The packageName.equals() below does not tolerate a null.)
        if ((packageName == null) != (that.packageName == null))
            return false;
        return name.equals(that.name) &&
                ( ((packageName == null) && (that.packageName == null))
                        || packageName.equals(that.packageName));
    }

    @Override
    public int hashCode() {
        return Objects.hash(packageName, name);
    }

    /**
     * Given a String of valid symbol characters, makes sure that
     * it is either a single consing dot, T, NIL, LIST, or CODE-CHAR
     * or of the forms :KEY or PKG::SYM.
     * If not, an error token is returned.
     * @param packageAndName
     */
    public static Token ParseSymbol(String packageAndName) {
        String[] parts = packageAndName.split(":");
        if (parts.length == 1 && parts[0].equals(".")) {
            return TokenDot.get();
        } else if (parts.length == 1 && parts[0].equals("T")) {
            return TokenT.get();
        } else if (parts.length == 1 && parts[0].equals("NIL")) {
            return TokenNIL.get();
        }
        // STATE and REPLACED-STATE get stuck in the returned list, outside the interesting part.
        // Just convert them to NIL.  This supports the LISP_MV response type.
        else if (parts.length == 1 && parts[0].equals("STATE")) {
            return TokenNIL.get();
        } else if (parts.length == 1 && parts[0].equals("REPLACED-STATE")) {
            return TokenNIL.get();

        } else if (parts.length == 1 && parts[0].equals("LIST")) {
            return new TokenSymbol(null, parts[0]);
        } else if (parts.length == 1 && parts[0].equals("CODE-CHAR")) {
            return new TokenSymbol(null, parts[0]);
        }
        // The form :KEY will be split as ["", "KEY"]
        else if (parts.length == 2 && parts[0].isEmpty()
            && ! parts[1].isEmpty()) {
            return new TokenSymbol("",parts[1]);
        }
        // The form PKG::SYM will be split as ["PKG", "", "SYM"]
        else if (parts.length == 3
                && ! parts[0].isEmpty()
                && parts[1].isEmpty()
                && ! parts[2].isEmpty()) {
            return new TokenSymbol(parts[0],parts[2]);
        } else {
            return new TokenError("Incorrect symbol syntax", packageAndName);
        }

    }



    @Override
    public SExpression toSExpression() {
        if (this.packageName == null) {
            if (this.name.equals("LIST")) {
                return SExpression.LIST();
            } else if (this.name.equals("CODE-CHAR")) {
                return SExpression.symbol(null, "CODE-CHAR");
            } else {
                throw new IllegalArgumentException("unknown symbol with no package prefix");
            }
        } else if ("".equals(packageName)) {
            return SExpression.keyword(name);
        } else {
            return SExpression.symbol(packageName, name);
        }
    }


}
