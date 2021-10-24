/*
 * Copyright (C) 2020 Kestrel Institute (http://www.kestrel.edu)
 * License: 3-clause BSD license (https://opensource.org/licenses/BSD-3-Clause)
 * Main Author: Alessandro Coglio (coglio@kestrel.edu)
 * Contributing Author: Eric McCarthy (mccarthy@kestrel.edu)
 */

package edu.kestrel.syntheto.ast;

import edu.kestrel.syntheto.sexpr.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Syntheto literal expression
 */
public class ExpressionLiteral extends Expression {
    
    private final Literal literal;

    private ExpressionLiteral(Literal literal) {
        this.literal = literal;
    }

    public static ExpressionLiteral make(Literal literal) {
        return new ExpressionLiteral(literal);
    }

    /**
     * The symbol for the ACL2 function that creates an instance of this class.
     */
    public static final SExpressionSymbol classMakerFn = SExpression.syntheto("MAKE-EXPRESSION-LITERAL");

    /**
     * Constructs a literal expression from an S-Expression
     * that looks like
     * (SYNTHETO::MAKE-EXPRESSION-LITERAL :GET literal)
     *
     * @param buildFormArg The AST maker form.
     * @throws IllegalArgumentException If the list is malformed.
     */
    public static ExpressionLiteral fromSExpression(SExpression buildFormArg) {
        if (!(buildFormArg instanceof SExpressionList))
            throw new IllegalArgumentException("Argument must be an SExpressionList");
        SExpressionList buildForm = (SExpressionList) buildFormArg;
        if (buildForm.length() != 3)
            throw new IllegalArgumentException("List wrong length.");
        if (!(buildForm.first().equals(classMakerFn)))
            throw new IllegalArgumentException("Wrong function in list.");

        if (!(buildForm.second().equals(SExpression.keyword("GET"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        if (!(buildForm.third() instanceof SExpressionList))
            throw new IllegalArgumentException("Wrong type of :GET argument.");
        Object literalRaw = ASTBuilder.fromSExpression(buildForm.third());
        if (!(literalRaw instanceof Literal))
            throw new IllegalArgumentException("Wrong AST class returned by maker for :GET");
        Literal literal = (Literal) literalRaw;

        return make(literal);
    }

    public Literal getLiteral() {
        return literal;
    }

    @Override
    public SExpression toSExpression() {
        return SExpression.list(classMakerFn,
                SExpression.keyword("GET"), literal.toSExpression());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExpressionLiteral that = (ExpressionLiteral) o;
        return literal.equals(that.literal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(literal);
    }

    @Override
    public String toString() { return this.toString(0); }

    @Override
    public String toString(int indentLevel) {
        return this.literal.toString(indentLevel);
    }
}