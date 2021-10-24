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
 * S-expressions that are characters.
 */
public class SExpressionCharacter extends SExpressionAtom {

    private final char value;

    /**
     * A transfer language character must have a unicode value 255 or less.
     * @param value
     */
    private SExpressionCharacter(char value) {
        if (value > 255)
            throw new IllegalArgumentException("SExpressionCharacter only characters with codes 0 through 255");
        this.value = value;
    }

    // Creation code.  We recommend using the interface methods from SExpression for creation.

    static SExpressionCharacter make(char value) {
        return new SExpressionCharacter(value);
    }

    // Inspection code.  These are public.

    public char getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        // TODO: handle characters not representable as #\...
        return "(CODE-CHAR " + (int)this.value + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SExpressionCharacter that = (SExpressionCharacter) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
