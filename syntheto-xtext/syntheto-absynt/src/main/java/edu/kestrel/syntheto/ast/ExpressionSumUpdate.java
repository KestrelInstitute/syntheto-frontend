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
 * Syntheto sum update expression.
 */
public class ExpressionSumUpdate extends Expression {

    private final Identifier type;

    private final Expression target;

    private final Identifier alternative;

    private final List<Initializer> fields;

    private ExpressionSumUpdate(Identifier type,
                                Expression target,
                                Identifier alternative,
                                List<Initializer> fields) {
        this.type = type;
        this.target = target;
        this.alternative = alternative;
        this.fields = fields;
    }

    /**
     * @param type The name of the sum type that is returned by target.
     * @param target An expression that returns a value of *type*::*alternative*.
     * @param alternative The name of the subproduct type alternative.
     * @param fields The fields of the value that are updated.
     * @return An expression that, when evaluated, returns the new value of *type*::*alternative* that is the same
     *         as target except for the changed fields.
     */
    public static ExpressionSumUpdate make(Identifier type,
                                           Expression target,
                                           Identifier alternative,
                                           List<Initializer> fields) {
        return new ExpressionSumUpdate(type, target, alternative, fields);
    }

    /**
     * The symbol for the ACL2 function that creates an instance of this class.
     */
    public static final SExpressionSymbol classMakerFn = SExpression.syntheto("MAKE-EXPRESSION-SUM-UPDATE");

    /**
     * Constructs a sum "update" expression from an S-Expression
     * that looks like
     * (SYNTHETO::MAKE-EXPRESSION-SUM-UPDATE :TYPE typename :TARGET sumExpr :ALTERNATIVE altId :FIELDS (LIST initializer...))
     *
     * @param buildFormArg The AST maker form.
     * @throws IllegalArgumentException If the list is malformed.
     */
    public static ExpressionSumUpdate fromSExpression(SExpression buildFormArg) {
        if (! (buildFormArg instanceof SExpressionList))
            throw new IllegalArgumentException("Argument must be an SExpressionList");
        SExpressionList buildForm = (SExpressionList) buildFormArg;
        if (buildForm.length() != 9)
            throw new IllegalArgumentException("List wrong length.");
        if (! (buildForm.first().equals(classMakerFn)))
            throw new IllegalArgumentException("Wrong function in list.");

        if (! (buildForm.second().equals(SExpression.keyword("TYPE"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        if (! (buildForm.third() instanceof SExpressionList))
            throw new IllegalArgumentException("Wrong type of :TYPE argument.");
        Object nameRaw = ASTBuilder.fromSExpression(buildForm.third());
        if (! (nameRaw instanceof Identifier))
            throw new IllegalArgumentException("Wrong AST class returned by maker for :TYPE");
        Identifier typeName = (Identifier) nameRaw;

        if (! (buildForm.fourth().equals(SExpression.keyword("TARGET"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        if (! (buildForm.fifth() instanceof SExpressionList))
            throw new IllegalArgumentException("Wrong type of :TARGET argument.");
        Object exprRaw = ASTBuilder.fromSExpression(buildForm.fifth());
        if (! (exprRaw instanceof Expression))
            throw new IllegalArgumentException("Wrong AST class returned by maker for :TARGET");
        Expression sumExpr = (Expression) exprRaw;

        if (! (buildForm.sixth().equals(SExpression.keyword("ALTERNATIVE"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        if (! (buildForm.seventh() instanceof SExpressionList))
            throw new IllegalArgumentException("Wrong type of :ALTERNATIVE argument.");
        Object altRaw = ASTBuilder.fromSExpression(buildForm.seventh());
        if (! (altRaw instanceof Identifier))
            throw new IllegalArgumentException("Wrong AST class returned by maker for :ALTERNATIVE");
        Identifier altId = (Identifier) altRaw;

        if (! (buildForm.eighth().equals(SExpression.keyword("FIELDS"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        if (! (buildForm.ninth() instanceof SExpressionList))
            throw new IllegalArgumentException("Wrong type of :FIELDS argument.");
        // Look up the SExpressionList specifically.
        SExpressionList opSExpr = (SExpressionList) buildForm.ninth();
        if (opSExpr.isEmpty())
            throw new IllegalArgumentException("List of variables must start with the special symbol LIST.");
        SExpression listSExpr = opSExpr.first();
        if (! (listSExpr.equals(SExpression.LIST())))
            throw new IllegalArgumentException("List of variables must start with the special symbol LIST.");
        List<Initializer> inits = new ArrayList<>();
        for (SExpression sexpr: opSExpr.rest().getElements()) {
            Object newInit = ASTBuilder.fromSExpression(sexpr);
            if (! (newInit instanceof Initializer))
                throw new IllegalArgumentException("Built class should be Initializer.");
            inits.add((Initializer) newInit);
        }

        return make(typeName, sumExpr, altId, inits);
    }

    public Identifier getType() {
        return type;
    }

    public Expression getTarget() {
        return target;
    }

    public Identifier getAlternative() {
        return alternative;
    }

    public List<Initializer> getFields() {
        return fields;
    }

    @Override
    public SExpression toSExpression() {
        List<SExpression> fieldSExpressions = new ArrayList<>();
        for (Initializer field : fields) {
            fieldSExpressions.add(field.toSExpression());
        }
        return SExpression.list(classMakerFn,
                SExpression.keyword("TYPE"), type.toSExpression(),
                SExpression.keyword("TARGET"), target.toSExpression(),
                SExpression.keyword("ALTERNATIVE"), alternative.toSExpression(),
                SExpression.keyword("FIELDS"), SExpression.listMaker(fieldSExpressions));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExpressionSumUpdate that = (ExpressionSumUpdate) o;
        return target.equals(that.target) &&
                type.equals(that.type) &&
                alternative.equals(that.alternative) &&
                fields.equals(that.fields);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, target, alternative, fields);
    }

    @Override
    public String toString() {
        return this.toString(0);
    }

    @Override
    public String toString(int indentLevel) {
        StringBuilder s = new StringBuilder();
        s.append(line(indentLevel,
                "ExpressionSumUpdate " + this.type + " " +
                        this.alternative + " {"));
        s.append(this.target.toString(indentLevel + 1));
        for (Initializer init : this.fields)
            s.append(init.toString(indentLevel + 1));
        s.append(line(indentLevel, "}"));
        return new String(s);
    }
}
