/*
 * Copyright (C) 2021 Kestrel Institute (http://www.kestrel.edu)
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
 * Syntheto class representing the List-of-Identifiers kind of Transform Argument Value.
 */
public class TransformArgumentValueIdentifiers extends TransformArgumentValue {

    private final List<Identifier> identifiers;

    private TransformArgumentValueIdentifiers(List<Identifier> identifiers) {
        this.identifiers = identifiers;
    }

    public static TransformArgumentValueIdentifiers make(List<Identifier> identifiers) {
        return new TransformArgumentValueIdentifiers(identifiers);
    }

    /**
     * The symbol for the ACL2 function that creates an instance of this class.
     */
    public static final SExpressionSymbol classMakerFn = SExpression.syntheto("MAKE-TRANSFORM-ARGUMENT-VALUE-IDENTIFIERS");

    /**
     * Constructs a multi value expression from an S-Expression
     * that looks like
     * (SYNTHETO::MAKE-TRANSFORM-ARGUMENT-VALUE-IDENTIFIERS :IDENTIFIER-LIST (LIST identifier...))
     *
     * @param buildFormArg The AST maker form.
     * @throws IllegalArgumentException If the list is malformed.
     */
    public static TransformArgumentValueIdentifiers fromSExpression(SExpression buildFormArg) {
        if (! (buildFormArg instanceof SExpressionList))
            throw new IllegalArgumentException("Argument must be an SExpressionList");
        SExpressionList buildForm = (SExpressionList) buildFormArg;
        if (buildForm.length() != 3)
            throw new IllegalArgumentException("List not long enough");
        if (! (buildForm.first().equals(classMakerFn)))
            throw new IllegalArgumentException("Wrong function in list.");

        if (! (buildForm.second().equals(SExpression.keyword("IDENTIFIER-LIST"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        if (! (buildForm.third() instanceof SExpressionList))
            throw new IllegalArgumentException("Wrong type of :IDENTIFIER-LIST argument.");
        // Look up the SExpressionList specifically.
        SExpressionList opSExpr = (SExpressionList) buildForm.third();
        if (opSExpr.isEmpty())
            throw new IllegalArgumentException("List of identifiers must start with the special symbol LIST, even if there are no identifiers.");
        SExpression listSExpr = opSExpr.first();
        if (! (listSExpr.equals(SExpression.LIST())))
            throw new IllegalArgumentException("List of identifiers must start with the special symbol LIST.");
        List<Identifier> ids = new ArrayList<>();
        for (SExpression sexpr: opSExpr.rest().getElements()) {
            Object newExpr = ASTBuilder.fromSExpression(sexpr);
            if (! (newExpr instanceof Identifier))
                throw new IllegalArgumentException("Built class should be Identifier.");
            ids.add((Identifier) newExpr);
        }

        return make(ids);
    }

    public List<Identifier> getIdentifiers() {
        return identifiers;
    }

    @Override
    public SExpression toSExpression() {
        List<SExpression> argumentSExpressions = new ArrayList<>();
        for (Identifier id : identifiers) {
            argumentSExpressions.add(id.toSExpression());
        }
        return SExpression.list(classMakerFn,
                SExpression.keyword("IDENTIFIER-LIST"), SExpression.listMaker(argumentSExpressions));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransformArgumentValueIdentifiers that = (TransformArgumentValueIdentifiers) o;
        return identifiers.equals(that.identifiers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifiers);
    }

    @Override
    public String toString() {
        return this.toString(0);
    }

    @Override
    public String toString(int indentLevel) {
        StringBuilder s = new StringBuilder();
        s.append(line(indentLevel, "TransformArgumentValueIdentifiers {"));
        for (Identifier arg : this.identifiers)
            s.append(line(indentLevel+1, arg.toString()));
        s.append(line(indentLevel, "}"));
        return new String(s);
    }
}
