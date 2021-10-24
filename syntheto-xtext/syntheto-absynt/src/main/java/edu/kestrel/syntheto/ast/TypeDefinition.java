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
 * Syntheto type definitions.
 */
public class TypeDefinition extends ASTNode {

    private final Identifier name;

    private final TypeDefiner body;

    private TypeDefinition(Identifier name, TypeDefiner body)
            throws IllegalArgumentException {
        if (name == null)
            throw new IllegalArgumentException("Null name.");
        if (body == null)
            throw new IllegalArgumentException("Null body.");
        this.name = name;
        this.body = body;
    }

    public static TypeDefinition make(Identifier name, TypeDefiner body) { return new TypeDefinition(name, body); }

    /**
     * The symbol for the ACL2 function that creates an instance of this class.
     */
    public static final SExpressionSymbol classMakerFn = SExpression.syntheto("MAKE-TYPE-DEFINITION");

    /**
     * Constructs a type definition from an S-Expression
     * that looks like
     * (SYNTHETO::MAKE-TYPE-DEFINITION :NAME identifier :BODY typeDefiner)
     *
     * @param buildFormArg The AST maker form.
     * @throws IllegalArgumentException If the list is malformed.
     */
    public static TypeDefinition fromSExpression(SExpression buildFormArg) {
        if (! (buildFormArg instanceof SExpressionList))
            throw new IllegalArgumentException("Argument must be an SExpressionList");
        SExpressionList buildForm = (SExpressionList) buildFormArg;
        if (buildForm.length() != 5)
            throw new IllegalArgumentException("List wrong length.");
        if (! (buildForm.first().equals(classMakerFn)))
            throw new IllegalArgumentException("Wrong function in list.");

        if (! (buildForm.second().equals(SExpression.keyword("NAME"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        if (! (buildForm.third() instanceof SExpressionList))
            throw new IllegalArgumentException("Wrong type of :NAME argument.");
        Object nameRaw = ASTBuilder.fromSExpression(buildForm.third());
        if (! (nameRaw instanceof Identifier))
            throw new IllegalArgumentException("Wrong AST class returned by maker for :NAME");
        Identifier name = (Identifier) nameRaw;

        if (! (buildForm.fourth().equals(SExpression.keyword("BODY"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        if (! (buildForm.fifth() instanceof SExpressionList))
            throw new IllegalArgumentException("Wrong type of :BODY argument.");
        Object definerRaw = ASTBuilder.fromSExpression(buildForm.fifth());
        if (! (definerRaw instanceof TypeDefiner))
            throw new IllegalArgumentException("Wrong AST class returned by maker for :BODY");
        TypeDefiner typedefiner = (TypeDefiner) definerRaw;

        return make(name, typedefiner);
    }

    public Identifier getName() {
        return name;
    }

    public TypeDefiner getBody() {
        return body;
    }
    /**
     * Translates this type definition to an S-expression.
     *
     * @return The s-expression.
     */
    @Override
    public SExpression toSExpression() {
        return SExpression.list(classMakerFn,
                                 SExpression.keyword("NAME"), name.toSExpression(),
                                 SExpression.keyword("BODY"), body.toSExpression());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypeDefinition that = (TypeDefinition) o;
        return name.equals(that.name) &&
                body.equals(that.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, body);
    }

    @Override
    public String toString() {
        return this.toString(0);
    }

    String toString(int indentLevel) {
        StringBuilder s = new StringBuilder();
        s.append(line(indentLevel, "TypeDefinition " + this.name + " {"));
        s.append(this.body.toString(indentLevel + 1));
        s.append(line(indentLevel, "}"));
        return new String(s);
    }
}
