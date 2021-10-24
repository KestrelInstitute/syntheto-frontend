/*
 * Copyright (C) 2020 Kestrel Institute (http://www.kestrel.edu)
 * License: 3-clause BSD license (https://opensource.org/licenses/BSD-3-Clause)
 * Main Author: Alessandro Coglio (coglio@kestrel.edu)
 * Contributing Author: Eric McCarthy (mccarthy@kestrel.edu)
 */

package edu.kestrel.syntheto.sexpr;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * S-expressions that are symbols.
 * Standard symbols have a non-null, nonempty {@code pkg}, and are
 *
 */
public class SExpressionSymbol extends SExpressionAtom {

    private final String pkg;

    protected final String name;

    /**
     * {@code CLspecialNames} are names of symbols which can be retrieved without specifying a package,
     * and that are printed without a package prefix.
     * They are currently all in the COMMON-LISP package.
     * It is possible we may need some special symbols in the ACL2 package
     * to be printed without a package prefix, which would require another collection.
     *
     * Special note on CODE-CHAR: the string-to-S-Expression parser
     * is expected to create an SExpressionCharacter directly when it sees
     * (CODE-CHAR code), rather than an SExpressionList containing this special symbol.
     * However, we provide this symbol in case that needs to change or if the symbol
     * comes up in another context.
     */
    private static final List<String> CLspecialNames =
            Arrays.asList("T", "NIL", "LIST", "CODE-CHAR");

    private static final HashMap<String, SExpressionSymbol> SpecialSymbols = new HashMap<>();
    static {
        for (String s: CLspecialNames) {
            SpecialSymbols.put(s, make("COMMON-LISP", s));
        }
    }

    protected SExpressionSymbol(String pkg, String name) {
        // TODO: check for allowable characters in pkg and name
        this.pkg = pkg;
        this.name = name;
    }

    static SExpressionSymbol make(String pkg, String name) {
        return new SExpressionSymbol(pkg, name);
    }

    static SExpressionSymbol makeKeyword(String name) {
        return new SExpressionSymbol("KEYWORD", name);
    }

    static SExpressionSymbol makeSyntheto(String name) {
        return new SExpressionSymbol("SYNTHETO", name);
    }

    static SExpressionSymbol makeSpecial(String name) {
        SExpressionSymbol sym = SpecialSymbols.get(name);
        if (sym == null) {
            throw new RuntimeException("cannot make special symbol with name" + name);
        } else {
            return sym;
        }
    }

    public boolean is_Keyword() {
        return this.pkg.equals("KEYWORD");
    }
    public boolean is_Syntheto() {
        return this.pkg.equals("SYNTHETO");
    }
    public boolean is_Special() {
        return SpecialSymbols.containsValue(this);
    }

    // Hopefully this next one will not be needed with the interface above.
    // Leaving it as private for now.
    private String getPkg() {
        return this.pkg;
    }
    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        if (SpecialSymbols.containsValue(this)) {
            return this.name;
        } else if (this.pkg.equals("KEYWORD")) {
            return ":" + this.name;
        } else {
            return this.pkg + "::" + this.name;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SExpressionSymbol that = (SExpressionSymbol) o;
        return pkg.equals(that.pkg) &&
                name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pkg, name);
    }
}
