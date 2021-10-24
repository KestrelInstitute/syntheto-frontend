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
 * Syntheto bind expressions.
 */
public class ExpressionBind extends Expression {

    private final List<TypedVariable> variables;

    private final Expression value;

    private final Expression body;

    private ExpressionBind(List<TypedVariable> variables, Expression value, Expression body) {
        this.variables = variables;
        this.value = value;
        this.body = body;
    }

    public static ExpressionBind make(List<TypedVariable> variables,
                                      Expression value,
                                      Expression body)  {
        return new ExpressionBind(variables, value, body);
    }

    /**
     * The symbol for the ACL2 function that creates an instance of this class.
     */
    public static final SExpressionSymbol classMakerFn = SExpression.syntheto("MAKE-EXPRESSION-BIND");

    /**
     * Constructs a bind expression from an S-Expression
     * that looks like
     * (SYNTHETO::MAKE-EXPRESSION-BIND :VARIABLES (LIST var...) :VALUE value :BODY body)
     *
     * @param buildFormArg The AST maker form.
     * @throws IllegalArgumentException If the list is malformed.
     */
    public static ExpressionBind fromSExpression(SExpression buildFormArg) {
        if (! (buildFormArg instanceof SExpressionList))
            throw new IllegalArgumentException("Argument must be an SExpressionList");
        SExpressionList buildForm = (SExpressionList) buildFormArg;
        if (buildForm.length() != 7)
            throw new IllegalArgumentException("List not long enough");
        if (! (buildForm.first().equals(classMakerFn)))
            throw new IllegalArgumentException("Wrong function in list.");

        if (! (buildForm.second().equals(SExpression.keyword("VARIABLES"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        if (! (buildForm.third() instanceof SExpressionList))
            throw new IllegalArgumentException("Wrong type of :VARIABLES argument.");
        // Look up the SExpressionList specifically.
        SExpressionList opSExpr = (SExpressionList) buildForm.third();
        if (opSExpr.isEmpty())
            throw new IllegalArgumentException("List of variables must start with the special symbol LIST.");
        SExpression listSExpr = opSExpr.first();
        if (! (listSExpr.equals(SExpression.LIST())))
            throw new IllegalArgumentException("List of variables must start with the special symbol LIST.");
        List<TypedVariable> vars = new ArrayList<>();
        for (SExpression sexpr: opSExpr.rest().getElements()) {
            Object newVar = ASTBuilder.fromSExpression(sexpr);
            if (! (newVar instanceof TypedVariable))
                throw new IllegalArgumentException("Built class should be TypedVariable.");
            vars.add((TypedVariable) newVar);
        }

        if (! (buildForm.fourth().equals(SExpression.keyword("VALUE"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        if (! (buildForm.fifth() instanceof SExpressionList))
            throw new IllegalArgumentException("Wrong type of :VALUE argument.");
        Object valueRaw = ASTBuilder.fromSExpression(buildForm.fifth());
        if (! (valueRaw instanceof Expression))
            throw new IllegalArgumentException("Wrong AST class returned by maker for :VALUE");
        Expression val = (Expression) valueRaw;

        if (! (buildForm.sixth().equals(SExpression.keyword("BODY"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        if (! (buildForm.seventh() instanceof SExpressionList))
            throw new IllegalArgumentException("Wrong type of :BODY argument.");
        Object bodyRaw = ASTBuilder.fromSExpression(buildForm.seventh());
        if (! (bodyRaw instanceof Expression))
            throw new IllegalArgumentException("Wrong AST class returned by maker for :BODY");
        Expression bod = (Expression) bodyRaw;

        return make(vars, val, bod);
    }

    public List<TypedVariable> getVariables() {
        return variables;
    }

    public Expression getValue() { return value; }

    public Expression getBody() { return body; }

    @Override
    public SExpression toSExpression() {
        List<SExpression> variableSExpressions = new ArrayList<>();
        for (TypedVariable variable : variables) {
            variableSExpressions.add(variable.toSExpression());
        }
        return SExpression.list(classMakerFn,
                SExpression.keyword("VARIABLES"), SExpression.listMaker(variableSExpressions),
                SExpression.keyword("VALUE"), value.toSExpression(),
                SExpression.keyword("BODY"), body.toSExpression());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExpressionBind that = (ExpressionBind) o;
        return variables.equals(that.variables) &&
                value.equals(that.value) &&
                body.equals(that.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(variables, value, body);
    }

    @Override
    public String toString() {
        return this.toString(0);
    }

    @Override
    public String toString(int indentLevel) {
        StringBuilder s = new StringBuilder();
        s.append(line(indentLevel, "ExpressionBind {"));
        for (TypedVariable var : this.variables)
            s.append(var.toString(indentLevel + 1));
        s.append(this.value.toString(indentLevel + 1));
        s.append(this.body.toString(indentLevel + 1));
        s.append(line(indentLevel, "}"));
        return new String(s);
    }
}
