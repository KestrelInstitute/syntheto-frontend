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
 * Syntheto transform.
 */
public class Transform extends ASTNode{

    private final Identifier newFunctionName;

    private final Identifier oldFunctionName;

    private final String transformName;

    private final List<TransformArgument> arguments;

    private Transform(Identifier newFnName,
                      Identifier oldFnName,
                      String transformName,
                      List<TransformArgument> arguments) {
        this.newFunctionName = newFnName;
        this.oldFunctionName = oldFnName;
        this.transformName = transformName;
        this.arguments = arguments;
    }

    public static Transform make(Identifier newFnName,
                                 Identifier oldFnName,
                                 String transformName,
                                 List<TransformArgument> arguments) {
        return new Transform(newFnName, oldFnName, transformName, arguments);
    }

    /**
     * The symbol for the ACL2 function that creates an instance of this class.
     */
    public static final SExpressionSymbol classMakerFn = SExpression.syntheto("MAKE-TRANSFORM");

    /**
     * Constructs a function header from an S-Expression
     * that looks like
     * (SYNTHETO::MAKE-TRANSFORM :NEW-FUNCTION-NAME identifier1 :OLD-FUNCTION-NAME identifier2 :TRANSFORM-NAME transformName :ARGUMENTS (LIST transformArgument...))
     *
     *
     * @param buildFormArg The AST maker form.
     * @throws IllegalArgumentException If the list is malformed.
     */
    public static Transform fromSExpression(SExpression buildFormArg) {
        if (! (buildFormArg instanceof SExpressionList))
            throw new IllegalArgumentException("Argument must be an SExpressionList");
        SExpressionList buildForm = (SExpressionList) buildFormArg;
        if (buildForm.length() != 9)
            throw new IllegalArgumentException("List not long enough");
        if (! (buildForm.first().equals(classMakerFn)))
            throw new IllegalArgumentException("Wrong function in list.");

        if (! (buildForm.second().equals(SExpression.keyword("NEW-FUNCTION-NAME"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        if (! (buildForm.third() instanceof SExpressionList))
            throw new IllegalArgumentException("Wrong type of :NEW-FUNCTION-NAME argument.");
        Object newNameRaw = ASTBuilder.fromSExpression(buildForm.third());
        if (! (newNameRaw instanceof Identifier))
            throw new IllegalArgumentException("Wrong AST class returned by maker for :NEW-FUNCTION-NAME");
        Identifier newName = (Identifier) newNameRaw;

        if (! (buildForm.fourth().equals(SExpression.keyword("OLD-FUNCTION-NAME"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        if (! (buildForm.fifth() instanceof SExpressionList))
            throw new IllegalArgumentException("Wrong type of :OLD-FUNCTION-NAME argument.");
        Object oldNameRaw = ASTBuilder.fromSExpression(buildForm.fifth());
        if (! (oldNameRaw instanceof Identifier))
            throw new IllegalArgumentException("Wrong AST class returned by maker for :OLD-FUNCTION-NAME");
        Identifier oldName = (Identifier) oldNameRaw;

        if (! (buildForm.sixth().equals(SExpression.keyword("TRANSFORM-NAME"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        if (! (buildForm.seventh() instanceof SExpressionString))
            throw new IllegalArgumentException("Wrong type of :TRANSFORM-NAME argument.");
        String transformName = ((SExpressionString) buildForm.seventh()).getValue();

        if (! (buildForm.eighth().equals(SExpression.keyword("ARGUMENTS"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        if (! (buildForm.ninth() instanceof SExpressionList))
            throw new IllegalArgumentException("Wrong type of :ARGUMENTS argument.");
        // Look up the SExpressionList specifically.
        SExpressionList opSExpr2 = (SExpressionList) buildForm.ninth();
        if (opSExpr2.isEmpty())
            throw new IllegalArgumentException("List of transform arguments must start with the special symbol LIST, even if there are no arguments.");
        SExpression listSExpr2 = opSExpr2.first();
        if (! (listSExpr2.equals(SExpression.LIST())))
            throw new IllegalArgumentException("List of transform arguments must start with the special symbol LIST.");
        List<TransformArgument> arguments = new ArrayList<>();
        for (SExpression sexpr2: opSExpr2.rest().getElements()) {
            Object newOutput = ASTBuilder.fromSExpression(sexpr2);
            if (!(newOutput instanceof TransformArgument))
                throw new IllegalArgumentException("Built class should be TransformArgument.");
            arguments.add((TransformArgument) newOutput);
        }

        return make(newName, oldName, transformName, arguments);
    }

    public Identifier getNewName() {
        return newFunctionName;
    }

    public Identifier getOldName() { return oldFunctionName; }

    public String getTransformName() {
        return transformName;
    }

    public List<TransformArgument> getArguments() {
        return arguments;
    }

    public SExpression toSExpression() {
        List<SExpression> argumentSExpressions = new ArrayList<>();
        for (TransformArgument arg: this.arguments) { argumentSExpressions.add(arg.toSExpression()); }
        return SExpression.list(classMakerFn,
                SExpression.keyword("NEW-FUNCTION-NAME"), this.newFunctionName.toSExpression(),
                SExpression.keyword("OLD-FUNCTION-NAME"), this.oldFunctionName.toSExpression(),
                SExpression.keyword("TRANSFORM-NAME"), SExpression.string(this.transformName),
                SExpression.keyword("ARGUMENTS"), SExpression.listMaker(argumentSExpressions));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transform that = (Transform) o;
        return newFunctionName.equals(that.newFunctionName) &&
                oldFunctionName.equals(that.oldFunctionName) &&
                transformName.equals(that.transformName) &&
                arguments.equals(that.arguments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(newFunctionName, oldFunctionName, transformName, arguments);
    }

    @Override
    public String toString() {
        return this.toString(0);
    }

    String toString(int indentLevel) {
        StringBuilder s = new StringBuilder();
        s.append(line(indentLevel, "Transform " + this.transformName +
                " synthesizes " + this.newFunctionName +
                " from " + this.oldFunctionName + " {"));
        s.append(line(indentLevel + 1, "Arguments {"));
        for (TransformArgument arg : this.arguments)
            s.append(arg.toString(indentLevel + 2));
        s.append(line(indentLevel + 1, "}"));
        s.append(line(indentLevel, "}"));
        return new String(s);
    }
}
