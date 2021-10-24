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
 * Syntheto function headers.
 */
public class FunctionHeader extends ASTNode{

    private final Identifier name;

    private final List<TypedVariable> inputs;

    private final List<TypedVariable> outputs;

    private FunctionHeader(Identifier name,
                           List<TypedVariable> inputs,
                           List<TypedVariable> outputs) {
        this.name = name;
        this.inputs = inputs;
        this.outputs = outputs;
    }

    public static FunctionHeader make(Identifier name,
                                      List<TypedVariable> inputs,
                                      List<TypedVariable> outputs) {
        return new FunctionHeader(name, inputs, outputs);
    }

    /**
     * The symbol for the ACL2 function that creates an instance of this class.
     */
    public static final SExpressionSymbol classMakerFn = SExpression.syntheto("MAKE-FUNCTION-HEADER");

    /**
     * Constructs a function header from an S-Expression
     * that looks like
     * (SYNTHETO::MAKE-FUNCTION-HEADER :NAME identifier :INPUTS (LIST typedVar...) :OUTPUTS (LIST typedVar...))
     *
     *
     * @param buildFormArg The AST maker form.
     * @throws IllegalArgumentException If the list is malformed.
     */
    public static FunctionHeader fromSExpression(SExpression buildFormArg) {
        if (! (buildFormArg instanceof SExpressionList))
            throw new IllegalArgumentException("Argument must be an SExpressionList");
        SExpressionList buildForm = (SExpressionList) buildFormArg;
        if (buildForm.length() != 7)
            throw new IllegalArgumentException("List not long enough");
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

        if (! (buildForm.fourth().equals(SExpression.keyword("INPUTS"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        if (! (buildForm.fifth() instanceof SExpressionList))
            throw new IllegalArgumentException("Wrong type of :INPUTS argument.");
        // Look up the SExpressionList specifically.
        SExpressionList opSExpr = (SExpressionList) buildForm.fifth();
        if (opSExpr.isEmpty())
            throw new IllegalArgumentException("List of variables must start with the special symbol LIST.");
        SExpression listSExpr = opSExpr.first();
        if (! (listSExpr.equals(SExpression.LIST())))
            throw new IllegalArgumentException("List of variables must start with the special symbol LIST.");
        List<TypedVariable> inputs = new ArrayList<>();
        for (SExpression sexpr: opSExpr.rest().getElements()) {
            Object newInput = ASTBuilder.fromSExpression(sexpr);
            if (!(newInput instanceof TypedVariable))
                throw new IllegalArgumentException("Built class should be TypedVariable.");
            inputs.add((TypedVariable) newInput);
        }

        if (! (buildForm.sixth().equals(SExpression.keyword("OUTPUTS"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        if (! (buildForm.seventh() instanceof SExpressionList))
            throw new IllegalArgumentException("Wrong type of :OUTPUTS argument.");
        // Look up the SExpressionList specifically.
        SExpressionList opSExpr2 = (SExpressionList) buildForm.seventh();
        if (opSExpr2.isEmpty())
            throw new IllegalArgumentException("List of variables must start with the special symbol LIST.");
        SExpression listSExpr2 = opSExpr2.first();
        if (! (listSExpr2.equals(SExpression.LIST())))
            throw new IllegalArgumentException("List of variables must start with the special symbol LIST.");
        List<TypedVariable> outputs = new ArrayList<>();
        for (SExpression sexpr2: opSExpr2.rest().getElements()) {
            Object newOutput = ASTBuilder.fromSExpression(sexpr2);
            if (!(newOutput instanceof TypedVariable))
                throw new IllegalArgumentException("Built class should be TypedVariable.");
            outputs.add((TypedVariable) newOutput);
        }

        return make(name, inputs, outputs);
    }

    public Identifier getName() {
        return name;
    }

    public List<TypedVariable> getInputs() {
        return inputs;
    }

    public List<TypedVariable> getOutputs() {
        return outputs;
    }

    public SExpression toSExpression() {
        List<SExpression> inputSExpressions = new ArrayList<>();
        for (TypedVariable input: inputs) { inputSExpressions.add(input.toSExpression()); }
        List<SExpression> outputSExpressions = new ArrayList<>();
        for (TypedVariable output: outputs) { outputSExpressions.add(output.toSExpression()); }
        return SExpression.list(classMakerFn,
                SExpression.keyword("NAME"), name.toSExpression(),
                SExpression.keyword("INPUTS"), SExpression.listMaker(inputSExpressions),
                SExpression.keyword("OUTPUTS"), SExpression.listMaker(outputSExpressions));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FunctionHeader that = (FunctionHeader) o;
        return name.equals(that.name) &&
                inputs.equals(that.inputs) &&
                outputs.equals(that.outputs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, inputs, outputs);
    }

    @Override
    public String toString() {
        return this.toString(0);
    }

    String toString(int indentLevel) {
        StringBuilder s = new StringBuilder();
        s.append(line(indentLevel, "FunctionHeader " + this.name + " {"));
        s.append(line(indentLevel + 1, "Inputs {"));
        for (TypedVariable input : this.inputs)
            s.append(input.toString(indentLevel + 2));
        s.append(line(indentLevel + 1, "}"));
        s.append(line(indentLevel + 1, "Outputs {"));
        for (TypedVariable output : this.outputs)
            s.append(output.toString(indentLevel + 2));
        s.append(line(indentLevel + 1, "}"));
        s.append(line(indentLevel, "}"));
        return new String(s);
    }
}
