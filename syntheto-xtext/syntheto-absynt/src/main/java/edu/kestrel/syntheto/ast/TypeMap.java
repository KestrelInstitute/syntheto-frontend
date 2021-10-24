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
 * Syntheto map types.
 */
public class TypeMap extends TypeCollection {

    private final Type domain;

    private final Type range;

    private TypeMap(Type domain, Type range) {
        this.domain = domain;
        this.range = range;
    }

    public static TypeMap make(Type domain, Type range) {
        return new TypeMap(domain, range);
    }

    /**
     * The symbol for the ACL2 function that creates an instance of this class.
     */
    public static final SExpressionSymbol classMakerFn = SExpression.syntheto("MAKE-TYPE-MAP");

    /**
     * Constructs a map type declaration from an S-Expression
     * that looks like
     * (SYNTHETO::MAKE-TYPE-MAP :DOMAIN domainType :RANGE rangeType)
     *
     * @param buildFormArg The AST maker form.
     * @throws IllegalArgumentException If the list is malformed.
     */
    public static TypeMap fromSExpression(SExpression buildFormArg) {
        if (! (buildFormArg instanceof SExpressionList))
            throw new IllegalArgumentException("Argument must be an SExpressionList");
        SExpressionList buildForm = (SExpressionList) buildFormArg;
        if (buildForm.length() != 5)
            throw new IllegalArgumentException("List not long enough");
        if (! (buildForm.first().equals(classMakerFn)))
            throw new IllegalArgumentException("Wrong function in list.");

        if (! (buildForm.second().equals(SExpression.keyword("DOMAIN"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        if (! (buildForm.third() instanceof SExpressionList))
            throw new IllegalArgumentException("Wrong type of :DOMAIN argument.");
        Object domainTypeRaw = ASTBuilder.fromSExpression(buildForm.third());
        if (! (domainTypeRaw instanceof Type))
            throw new IllegalArgumentException("Wrong AST class returned by maker for :DOMAIN");
        Type domainType = (Type) domainTypeRaw;

        if (! (buildForm.fourth().equals(SExpression.keyword("RANGE"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        if (! (buildForm.fifth() instanceof SExpressionList))
            throw new IllegalArgumentException("Wrong type of :RANGE argument.");
        Object rangeTypeRaw = ASTBuilder.fromSExpression(buildForm.fifth());
        if (! (rangeTypeRaw instanceof Type))
            throw new IllegalArgumentException("Wrong AST class returned by maker for :RANGE");
        Type rangeType = (Type) rangeTypeRaw;

        return make(domainType, rangeType);
    }

    public Type getDomain() {
        return this.domain;
    }

    public Type getRange() {
        return this.range;
    }

    @Override
    public SExpression toSExpression() {
        SExpression domainSExpr = domain.toSExpression();
        SExpression rangeSExpr = range.toSExpression();
        return SExpression.list(classMakerFn,
                                SExpression.keyword("DOMAIN"), domainSExpr,
                                SExpression.keyword("RANGE"), rangeSExpr);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypeMap mapType = (TypeMap) o;
        return domain.equals(mapType.domain) &&
                range.equals(mapType.range);
    }

    @Override
    public int hashCode() {
        return Objects.hash(domain, range);
    }

    @Override
    public String toString() {
        return this.toString(0);
    }

    @Override
    String toString(int indentLevel) {
        return line(indentLevel, "TypeSet {") +
                this.domain.toString(indentLevel + 1) +
                this.range.toString(indentLevel + 1) +
                line(indentLevel, "}");
    }
}
