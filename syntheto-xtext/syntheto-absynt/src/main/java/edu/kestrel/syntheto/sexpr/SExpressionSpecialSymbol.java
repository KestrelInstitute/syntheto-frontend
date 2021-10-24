/*
 * Copyright (C) 2020 Kestrel Institute (http://www.kestrel.edu)
 * License: 3-clause BSD license (https://opensource.org/licenses/BSD-3-Clause)
 * Main Author: Alessandro Coglio (coglio@kestrel.edu)
 * Contributing Author: Eric McCarthy (mccarthy@kestrel.edu)
 */

package edu.kestrel.syntheto.sexpr;

import java.util.Objects;

/**
 * S-expressions that are "special" symbols,
 * for which we leave off a package prefix.
 * Initially this is just T and NIL.
 */
class SExpressionSpecialSymbol extends SExpressionAtom {

    private final String pkg;

    protected final String name;

    protected SExpressionSpecialSymbol(String pkg, String name) {
        this.pkg = pkg;
        this.name = name;
    }

    static SExpressionSpecialSymbol make(String pkg, String name) {
        return new SExpressionSpecialSymbol(pkg, name);
    }

    @Override
    public String toString() {
        // TODO: handle additional characters not representable in symbols,
        //       as well as for the pkg
        if (this.name.equals(this.name.toUpperCase())) {
            return this.pkg + "::" + this.name;
        } else {
            return this.pkg + "::" + "|" + this.name + "|";
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SExpressionSpecialSymbol that = (SExpressionSpecialSymbol) o;
        return pkg.equals(that.pkg) &&
                name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pkg, name);
    }
}
