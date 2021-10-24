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
 * Syntheto sum (variant) construction expression with named fields.
 * Constructs the given alternative product (struct).
 */
public class ExpressionSumConstruct extends Expression {

    private final Identifier type;

    private final Identifier alternative;

    private final List<Initializer> fields;

    private ExpressionSumConstruct(Identifier type,
                                   Identifier alternative,
                                   List<Initializer> fields) {
        this.type = type;
        this.alternative = alternative;
        this.fields = fields;
    }

    public static ExpressionSumConstruct make(Identifier type,
                                              Identifier alternative,
                                              List<Initializer> fields) {
        return new ExpressionSumConstruct(type, alternative, fields);
    }

    /**
     * The symbol for the ACL2 function that creates an instance of this class.
     */
    public static final SExpressionSymbol classMakerFn = SExpression.syntheto("MAKE-EXPRESSION-SUM-CONSTRUCT");

    /**
     * Constructs a sum construction expression from an S-Expression
     * that looks like
     * (SYNTHETO::MAKE-EXPRESSION-SUM-CONSTRUCT :TYPE sumId :ALTERNATIVE altId :FIELDS (LIST initializer...))
     *
     * @param buildFormArg The AST maker form.
     * @throws IllegalArgumentException If the list is malformed.
     */
    public static ExpressionSumConstruct fromSExpression(SExpression buildFormArg) {
        if (! (buildFormArg instanceof SExpressionList))
            throw new IllegalArgumentException("Argument must be an SExpressionList");
        SExpressionList buildForm = (SExpressionList) buildFormArg;
        if (buildForm.length() != 7)
            throw new IllegalArgumentException("List not long enough");
        if (! (buildForm.first().equals(classMakerFn)))
            throw new IllegalArgumentException("Wrong function in list.");

        if (! (buildForm.second().equals(SExpression.keyword("TYPE"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        if (! (buildForm.third() instanceof SExpressionList))
            throw new IllegalArgumentException("Wrong type of :TYPE argument.");
        Object idRaw = ASTBuilder.fromSExpression(buildForm.third());
        if (! (idRaw instanceof Identifier))
            throw new IllegalArgumentException("Wrong AST class returned by maker for :TYPE");
        Identifier sumId = (Identifier) idRaw;

        if (! (buildForm.fourth().equals(SExpression.keyword("ALTERNATIVE"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        if (! (buildForm.fifth() instanceof SExpressionList))
            throw new IllegalArgumentException("Wrong type of :ALTERNATIVE argument.");
        Object idRaw2 = ASTBuilder.fromSExpression(buildForm.fifth());
        if (! (idRaw2 instanceof Identifier))
            throw new IllegalArgumentException("Wrong AST class returned by maker for :ALTERNATIVE");
        Identifier altId = (Identifier) idRaw2;

        if (! (buildForm.sixth().equals(SExpression.keyword("FIELDS"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        if (! (buildForm.seventh() instanceof SExpressionList))
            throw new IllegalArgumentException("Wrong type of :FIELDS argument.");
        // Look up the SExpressionList specifically.
        SExpressionList opSExpr = (SExpressionList) buildForm.seventh();
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

        return make(sumId, altId, inits);
    }

    public Identifier getType() {
        return type;
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
                SExpression.keyword("ALTERNATIVE"), alternative.toSExpression(),
                SExpression.keyword("FIELDS"), SExpression.listMaker(fieldSExpressions));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExpressionSumConstruct that = (ExpressionSumConstruct) o;
        return type.equals(that.type) &&
                alternative.equals(that.alternative) &&
                fields.equals(that.fields);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, alternative, fields);
    }

    @Override
    public String toString() {
        return this.toString(0);
    }

    @Override
    public String toString(int indentLevel) {
        StringBuilder s = new StringBuilder();
        s.append(line(indentLevel,
                "ExpressionSumConstruct " + this.type +
                        " " + this.alternative + " {"));
        for (Initializer init : this.fields)
            s.append(init.toString(indentLevel + 1));
        s.append(line(indentLevel, "}"));
        return new String(s);
    }
}
