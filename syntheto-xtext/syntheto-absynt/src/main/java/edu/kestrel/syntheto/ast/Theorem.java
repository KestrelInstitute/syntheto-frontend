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
 * Syntheto theorems.
 */
public class Theorem extends ASTNode {

    private final Identifier name;

    private final List<TypedVariable> variables;

    private final Expression formula;

    private Theorem(Identifier name,
                    List<TypedVariable> variables,
                    Expression formula) {
        this.name = name;
        this.variables = variables;
        this.formula = formula;
    }

    public static Theorem make(Identifier name,
                               List<TypedVariable> variables,
                               Expression formula) {
        return new Theorem(name, variables, formula);
    }

    /**
     * The symbol for the ACL2 function that creates an instance of this class.
     */
    public static final SExpressionSymbol classMakerFn = SExpression.syntheto("MAKE-THEOREM");

    /**
     * Constructs a theorem definition from an S-Expression
     * that looks like
     * (SYNTHETO::MAKE-THEOREM :NAME identifier :VARIABLES (LIST typedvar...) :FORMULA expression)
     *
     * @param buildFormArg The AST maker form.
     * @throws IllegalArgumentException If the list is malformed.
     */
    public static Theorem fromSExpression(SExpression buildFormArg) {
        if (! (buildFormArg instanceof SExpressionList))
            throw new IllegalArgumentException("Argument must be an SExpressionList");
        SExpressionList buildForm = (SExpressionList) buildFormArg;
        if (buildForm.length() != 7)
            throw new IllegalArgumentException("List wrong length.");
        if (! (buildForm.first().equals(classMakerFn)))
            throw new IllegalArgumentException("Wrong function in list.");

        if (! (buildForm.second().equals(SExpression.keyword("NAME"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        if (! (buildForm.third() instanceof SExpressionList))
            throw new IllegalArgumentException("Wrong type of :NAME argument.");
        Object idRaw = ASTBuilder.fromSExpression(buildForm.third());
        if (! (idRaw instanceof Identifier))
            throw new IllegalArgumentException("Wrong AST class returned by maker for :NAME");
        Identifier id = (Identifier) idRaw;

        if (! (buildForm.fourth().equals(SExpression.keyword("VARIABLES"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        if (! (buildForm.fifth() instanceof SExpressionList))
            throw new IllegalArgumentException("Wrong type of :VARIABLES argument.");
        // Look up the SExpressionList specifically.
        SExpressionList opSExpr = (SExpressionList) buildForm.fifth();
        if (opSExpr.isEmpty())
            throw new IllegalArgumentException("List of variables must start with the special symbol LIST.");
        SExpression listSExpr = opSExpr.first();
        if (! (listSExpr.equals(SExpression.LIST())))
            throw new IllegalArgumentException("List of variables must start with the special symbol LIST.");
        List<TypedVariable> vars = new ArrayList<>();
        for (SExpression sexpr: opSExpr.rest().getElements()) {
            Object newTypedVar = ASTBuilder.fromSExpression(sexpr);
            if (! (newTypedVar instanceof TypedVariable))
                throw new IllegalArgumentException("Built class should be TypedVariable.");
            vars.add((TypedVariable) newTypedVar);
        }

        if (! (buildForm.sixth().equals(SExpression.keyword("FORMULA"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        if (! (buildForm.seventh() instanceof SExpressionList))
            throw new IllegalArgumentException("Wrong type of :FORMULA argument.");
        Object formulaRaw = ASTBuilder.fromSExpression(buildForm.seventh());
        if (! (formulaRaw instanceof Expression))
            throw new IllegalArgumentException("Wrong AST class returned by maker for :FORMULA");
        Expression expr = (Expression) formulaRaw;

        return make(id, vars, expr);
    }

    public Identifier getName() {
        return name;
    }

    public List<TypedVariable> getVariables() {
        return variables;
    }

    public Expression getFormula() {
        return formula;
    }

    @Override
    public SExpression toSExpression() {
        List<SExpression> variableSExpressions = new ArrayList<>();
        for (TypedVariable variable: variables) { variableSExpressions.add(variable.toSExpression()); }
        return SExpression.list(classMakerFn,
                    SExpression.keyword("NAME"), name.toSExpression(),
                    SExpression.keyword("VARIABLES"), SExpression.listMaker(variableSExpressions),
                    SExpression.keyword("FORMULA"), formula.toSExpression());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Theorem theorem = (Theorem) o;
        return name.equals(theorem.name) &&
                variables.equals(theorem.variables) &&
                formula.equals(theorem.formula);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, variables, formula);
    }

    @Override
    public String toString() {
        return this.toString(0);
    }

    String toString(int indentLevel) {
        StringBuilder s = new StringBuilder();
        s.append(line(indentLevel, "Theorem " + this.name + " {"));
        for (TypedVariable var : this.variables)
            s.append(var.toString(indentLevel + 1));
        s.append(this.formula.toString(indentLevel + 1));
        s.append(line(indentLevel, "}"));
        return new String(s);
    }
}
