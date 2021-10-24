/*
 * Copyright (C) 2020 Kestrel Institute (http://www.kestrel.edu)
 * License: 3-clause BSD license (https://opensource.org/licenses/BSD-3-Clause)
 * Main Author: Alessandro Coglio (coglio@kestrel.edu)
 * Contributing Author: Eric McCarthy (mccarthy@kestrel.edu)
 */

package edu.kestrel.syntheto.sexpr;

/**
 * A singleton class representing a lisp NIL.
 */
public class TokenNIL extends Token {

    private static final TokenNIL INSTANCE = new TokenNIL();

    public static TokenNIL get() { return INSTANCE; }

    private TokenNIL() {}  // turn off constructor

    @Override
    public SExpression toSExpression() {
        return SExpression.NIL();
    }

}
