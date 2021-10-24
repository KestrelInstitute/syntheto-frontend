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
 * Syntheto product field expressions.
 */
public class ExpressionProductField extends Expression {

    private final Identifier type;

    private final Expression target;

    private final Identifier field;

    private ExpressionProductField(Identifier type, Expression target, Identifier field) {
        this.type = type;
        this.target = target;
        this.field = field;
    }

    /**
     * @param type The name of the product type that is returned by target.
     * @param target An expression that returns a value of *type*.
     * @param field The field of the value that is selected.
     * @return An expression that, when evaluated, returns the value of the given field of target.
     */
    public static ExpressionProductField make(Identifier type,
                                              Expression target,
                                              Identifier field) {
        return new ExpressionProductField(type, target, field);
    }

    /**
     * The symbol for the ACL2 function that creates an instance of this class.
     */
    public static final SExpressionSymbol classMakerFn = SExpression.syntheto("MAKE-EXPRESSION-PRODUCT-FIELD");

    /**
     * Constructs a product field selection expression from an S-Expression
     * that looks like
     * (SYNTHETO::MAKE-EXPRESSION-PRODUCT-FIELD :TYPE typename :TARGET expression :FIELD identifier)
     *
     * @param buildFormArg The AST maker form.
     * @throws IllegalArgumentException If the list is malformed.
     */
    public static ExpressionProductField fromSExpression(SExpression buildFormArg) {
        if (! (buildFormArg instanceof SExpressionList))
            throw new IllegalArgumentException("Argument must be an SExpressionList");
        SExpressionList buildForm = (SExpressionList) buildFormArg;
        if (buildForm.length() != 7)
            throw new IllegalArgumentException("List wrong length.");
        if (! (buildForm.first().equals(classMakerFn)))
            throw new IllegalArgumentException("Wrong function in list.");

        if (! (buildForm.second().equals(SExpression.keyword("TYPE"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        if (! (buildForm.third() instanceof SExpressionList))
            throw new IllegalArgumentException("Wrong type of :TYPE argument.");
        Object typeRaw = ASTBuilder.fromSExpression(buildForm.third());
        if (! (typeRaw instanceof Identifier))
            throw new IllegalArgumentException("Wrong AST class returned by maker for :TYPE");
        Identifier type = (Identifier) typeRaw;

        if (! (buildForm.fourth().equals(SExpression.keyword("TARGET"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        if (! (buildForm.fifth() instanceof SExpressionList))
            throw new IllegalArgumentException("Wrong type of :TARGET argument.");
        Object valRaw = ASTBuilder.fromSExpression(buildForm.fifth());
        if (! (valRaw instanceof Expression))
            throw new IllegalArgumentException("Wrong AST class returned by maker for :TARGET");
        Expression val = (Expression) valRaw;

        if (! (buildForm.sixth().equals(SExpression.keyword("FIELD"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        if (! (buildForm.seventh() instanceof SExpressionList))
            throw new IllegalArgumentException("Wrong type of :FIELD argument.");
        Object nameRaw = ASTBuilder.fromSExpression(buildForm.seventh());
        if (! (nameRaw instanceof Identifier))
            throw new IllegalArgumentException("Wrong AST class returned by maker for :FIELD");
        Identifier name = (Identifier) nameRaw;

        return make(type, val, name);
    }

    public Expression getTarget() {
        return target;
    }

    public Identifier getField() {
        return field;
    }

    @Override
    public SExpression toSExpression() {
        return SExpression.list(classMakerFn,
                SExpression.keyword("TYPE"), type.toSExpression(),
                SExpression.keyword("TARGET"), target.toSExpression(),
                SExpression.keyword("FIELD"), field.toSExpression());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExpressionProductField that = (ExpressionProductField) o;
        return type.equals(that.type) &&
                target.equals(that.target) &&
                field.equals(that.field);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, target, field);
    }

    @Override
    public String toString() {
        return this.toString(0);
    }

    @Override
    public String toString(int indentLevel) {
        StringBuilder s = new StringBuilder();
        s.append(line(indentLevel,
                "ExpressionProductField " + this.type + " " +
                        this.field + " {"));
        s.append(this.target.toString(indentLevel + 1));
        s.append(line(indentLevel, "}"));
        return new String(s);
    }
}
