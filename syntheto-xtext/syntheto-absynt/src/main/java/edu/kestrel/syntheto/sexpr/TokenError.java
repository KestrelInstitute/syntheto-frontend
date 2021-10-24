/*
 * Copyright (C) 2020 Kestrel Institute (http://www.kestrel.edu)
 * License: 3-clause BSD license (https://opensource.org/licenses/BSD-3-Clause)
 * Main Author: Alessandro Coglio (coglio@kestrel.edu)
 * Contributing Author: Eric McCarthy (mccarthy@kestrel.edu)
 */

package edu.kestrel.syntheto.sexpr;

/**
 * A class representing an error while tokenizing.
 * 
 */
public class TokenError extends Token {

    public String message;
    public String charsSoFar;

    public TokenError(String message, String charsSoFar) {
        this.message = message;
        this.charsSoFar = charsSoFar;
    }

    @Override
    public SExpression toSExpression() {
        throw new IllegalArgumentException(message);
    }
}
