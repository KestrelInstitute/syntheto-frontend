/*
 * Copyright (C) 2020 Kestrel Institute (http://www.kestrel.edu)
 * License: 3-clause BSD license (https://opensource.org/licenses/BSD-3-Clause)
 * Main Author: Alessandro Coglio (coglio@kestrel.edu)
 * Contributing Author: Eric McCarthy (mccarthy@kestrel.edu)
 */

package edu.kestrel.syntheto.ast;

import edu.kestrel.syntheto.sexpr.*;
import edu.kestrel.syntheto.sexpr.SExpressionList;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Syntheto product type specifier.
 */
public class TypeProduct extends ASTNode {

    private final List<Field> fields;

    private final Expression invariant;

    private TypeProduct(List<Field> fields, Expression invariant) {
        // TODO: Are we going to restrict fields to be nonempty?
        this.fields = fields;
        // the invariant is allowed to be null -- the witness is optional
        this.invariant = invariant;
    }

    /**
     * Builds a product type (aka "struct") specifier.
     *
     * @param fields    The List of fields, each of which is a name and a type.
     * @param invariant An optional expression that restricts the field values that a tuple of this type may have.
     */
    public static TypeProduct make(List<Field> fields, Expression invariant) {
        return new TypeProduct(fields, invariant);
    }

    /**
     * The symbol for the ACL2 function that creates an instance of this class.
     */
    public static final SExpressionSymbol classMakerFn = SExpression.syntheto("MAKE-TYPE-PRODUCT");

    /**
     * Constructs a product type specifier from an S-Expression
     * that looks like
     * (SYNTHETO::MAKE-TYPE-PRODUCT :FIELDS (LIST field...) :INVARIANT <NIL or Expression>)
     *
     * @param buildFormArg The AST maker form.
     * @throws IllegalArgumentException If the list is malformed.
     */
    public static TypeProduct fromSExpression(SExpression buildFormArg) {
        if (! (buildFormArg instanceof SExpressionList))
            throw new IllegalArgumentException("Argument must be an SExpressionList");
        SExpressionList buildForm = (SExpressionList) buildFormArg;
        if (buildForm.length() != 5)
            throw new IllegalArgumentException("List wrong length.");
        if (! (buildForm.first().equals(classMakerFn)))
            throw new IllegalArgumentException("Wrong function in list.");

        if (! (buildForm.second().equals(SExpression.keyword("FIELDS"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        if (! (buildForm.third() instanceof SExpressionList))
            throw new IllegalArgumentException("Wrong type of :FIELDS argument.");
        SExpressionList fieldsSExpr = (SExpressionList) buildForm.third();
        if (fieldsSExpr.isEmpty())
            throw new IllegalArgumentException("List of fields must start with the special symbol LIST.");
        SExpression listSExpr = fieldsSExpr.first();
        if (! (listSExpr.equals(SExpression.LIST())))
            throw new IllegalArgumentException("List of fields must start with the special symbol LIST.");
        List<Field> fields = new ArrayList<>();
        for (SExpression sexpr: fieldsSExpr.rest().getElements()) {
            Object newField = ASTBuilder.fromSExpression(sexpr);
            if (! (newField instanceof Field))
                throw new IllegalArgumentException("Built class should be Field.");
            fields.add((Field) newField);
        }

        if (! (buildForm.fourth().equals(SExpression.keyword("INVARIANT"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        if (! (buildForm.fifth() instanceof SExpressionList)
            && ! (buildForm.fifth().equals(SExpression.NIL())))
            throw new IllegalArgumentException("Wrong type of :INVARIANT argument.");
        Object invariantRaw = null;
        if (buildForm.fifth() instanceof SExpressionList) {
            invariantRaw = ASTBuilder.fromSExpression(buildForm.fifth());
            if (! (invariantRaw instanceof Expression))
                throw new IllegalArgumentException("Wrong AST class returned by maker for :INVARIANT");
        }
        Expression invariant = (Expression) invariantRaw;

        return make(fields, invariant);
    }

    public List<Field> getFields() {
        return fields;
    }

    public Expression getInvariant() {
        return invariant;
    }

    @Override
    public SExpression toSExpression() {
        List<SExpression> fieldSExpressions = new ArrayList<>();
        for (Field field: fields) { fieldSExpressions.add(field.toSExpression()); }
        // NOTE: the Java class combines the ACL2 deftagsum alternative type-definer-product
        // with the defprod type-product
        return SExpression.list(classMakerFn,
                        SExpression.keyword("FIELDS"), SExpression.listMaker(fieldSExpressions),
                        SExpression.keyword("INVARIANT"),
                        (invariant == null) ? SExpression.NIL() : invariant.toSExpression());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypeProduct that = (TypeProduct) o;
        // If either invariant is null but not both, then return false.
        // (The .equals() below does not tolerate a null.)
        if ((invariant == null) != (that.invariant == null))
            return false;
        return fields.equals(that.fields) &&
                ( ((invariant == null) && (that.invariant == null))
                    || invariant.equals(that.invariant));
    }

    @Override
    public int hashCode() {
        return Objects.hash(fields, invariant);
    }

    @Override
    public String toString() {
        return this.toString(0);
    }

    String toString(int indentLevel) {
        StringBuilder s = new StringBuilder();
        s.append(line(indentLevel, "TypeProduct {"));
        for (Field field : this.fields)
            s.append(field.toString(indentLevel + 1));
        if (this.invariant != null)
            s.append(this.invariant.toString(indentLevel + 1));
        else
            s.append(line(indentLevel + 1, "/* no invariant */"));
        s.append(line(indentLevel, "}"));
        return new String(s);
    }
}
