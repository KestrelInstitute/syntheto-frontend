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
 * Syntheto regular function specifiers.
 */
public class FunctionSpecifierRegular extends FunctionSpecifier {

    private final Expression body;

    private FunctionSpecifierRegular(Expression body) {
        this.body = body;
    }

    public static FunctionSpecifierRegular make(Expression body) {
        return new FunctionSpecifierRegular(body);
    }

    /**
     * The symbol for the ACL2 function that creates an instance of this class.
     */
    public static final SExpressionSymbol classMakerFn = SExpression.syntheto("MAKE-FUNCTION-SPECIFIER-REGULAR");

    /**
     * Constructs a regular function specifier
     * that looks like
     * (SYNTHETO::MAKE-FUNCTION-SPECIFIER-REGULAR :BODY expr)
     *
     * @param buildFormArg The AST maker form.
     * @throws IllegalArgumentException If the list is malformed.
     * @return
     */
    public static FunctionSpecifierRegular fromSExpression(SExpression buildFormArg) {
        if (! (buildFormArg instanceof SExpressionList))
            throw new IllegalArgumentException("Argument must be an SExpressionList");
        SExpressionList buildForm = (SExpressionList) buildFormArg;
        if (buildForm.length() != 3)
            throw new IllegalArgumentException("List not long enough");
        if (! (buildForm.first().equals(classMakerFn)))
            throw new IllegalArgumentException("Wrong function in list.");

        if (! (buildForm.second().equals(SExpression.keyword("BODY"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        if (! (buildForm.third() instanceof SExpressionList))
            throw new IllegalArgumentException("Wrong type of :BODY argument.");
        Object bodyRaw = ASTBuilder.fromSExpression(buildForm.third());
        if (! (bodyRaw instanceof Expression))
            throw new IllegalArgumentException("Wrong AST class returned by maker for :BODY");
        Expression body = (Expression) bodyRaw;

        return make(body);
    }

    public Expression getBody() {
        return body;
    }

    @Override
    public SExpression toSExpression() {
        return SExpression.list(classMakerFn,
                SExpression.keyword("BODY"), body.toSExpression());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FunctionSpecifierRegular that = (FunctionSpecifierRegular) o;
        return body.equals(that.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(body);
    }

    @Override
    public String toString() {
        return this.toString(0);
    }

    @Override
    String toString(int indentLevel) {
        StringBuilder s = new StringBuilder();
        s.append(line(indentLevel, "FunctionSpecifierRegular {"));
        s.append(this.body.toString(indentLevel + 1));
        s.append(line(indentLevel, "}"));
        return new String(s);
    }
}
