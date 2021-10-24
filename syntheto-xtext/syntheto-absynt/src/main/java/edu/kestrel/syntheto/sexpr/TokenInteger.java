/*
 * Copyright (C) 2020 Kestrel Institute (http://www.kestrel.edu)
 * License: 3-clause BSD license (https://opensource.org/licenses/BSD-3-Clause)
 * Main Author: Alessandro Coglio (coglio@kestrel.edu)
 * Contributing Author: Eric McCarthy (mccarthy@kestrel.edu)
 */

package edu.kestrel.syntheto.sexpr;

import java.math.BigInteger;
import java.util.Objects;

/**
 * A lexer token representing a lisp integer.
 */
public class TokenInteger extends Token {

    public BigInteger value;

    public TokenInteger(String lexed) {
        this.value = new BigInteger(lexed);
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TokenInteger that = (TokenInteger) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }



    @Override
    public SExpression toSExpression() {
        return SExpression.integer(value);
    }


}
