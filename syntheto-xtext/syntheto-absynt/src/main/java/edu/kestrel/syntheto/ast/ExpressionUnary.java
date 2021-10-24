/*
 * Copyright (C) 2020 Kestrel Institute (http://www.kestrel.edu)
 * License: 3-clause BSD license (https://opensource.org/licenses/BSD-3-Clause)
 * Main Author: Alessandro Coglio (coglio@kestrel.edu)
 * Contributing Author: Eric McCarthy (mccarthy@kestrel.edu)
 */

package edu.kestrel.syntheto.ast;

import edu.kestrel.syntheto.sexpr.*;

import java.util.HashMap;
import java.util.Objects;

/**
 * Syntheto unary expressions.
 */
public class ExpressionUnary extends Expression {

    public enum Operator {

        // Operator Name is the Enum,
        // Function name for constructing S-Expression is the constructor argument
        NOT("MAKE-UNARY-OP-NOT"),
        MINUS("MAKE-UNARY-OP-MINUS");

        private String SExprFnName;
        private SExpression sExpr;

        // The constructor
        private Operator(String fnName) {
            this.SExprFnName = fnName;
            this.sExpr = SExpression.list(SExpression.syntheto(fnName));
        }

        public String getSExprFnName() { return SExprFnName; }

        public SExpression toSExpression() {
            return this.sExpr;
        }

    } // end of Operator definition

    // After creating the operators, set up a map from SExpression form to operator.
    private static final HashMap<SExpression, ExpressionUnary.Operator> makerToOperator = new HashMap<>();
    static {
        for (ExpressionUnary.Operator operator: ExpressionUnary.Operator.values()) {
            makerToOperator.put(operator.sExpr, operator);
        }
    }

    private final Operator operator;

    private final Expression operand;

    private ExpressionUnary(Operator operator, Expression operand) {
        this.operator = operator;
        this.operand = operand;
    }

    public static ExpressionUnary make(Operator operator, Expression operand) {
        return new ExpressionUnary(operator, operand);
    }

    /**
     * The symbol for the ACL2 function that creates an instance of this class.
     */
    public static final SExpressionSymbol classMakerFn = SExpression.syntheto("MAKE-EXPRESSION-UNARY");

    /**
     * Constructs a unary expression from an S-Expression
     * that looks like
     * (SYNTHETO::MAKE-EXPRESSION-UNARY :OPERATOR op :OPERAND operand)
     *
     * @param buildFormArg The AST maker form.
     * @throws IllegalArgumentException If the list is malformed.
     */
    public static ExpressionUnary fromSExpression(SExpression buildFormArg) {
        if (! (buildFormArg instanceof SExpressionList))
            throw new IllegalArgumentException("Argument must be an SExpressionList");
        SExpressionList buildForm = (SExpressionList) buildFormArg;
        if (buildForm.length() != 5)
            throw new IllegalArgumentException("List not long enough");
        if (! (buildForm.first().equals(classMakerFn)))
            throw new IllegalArgumentException("Wrong function in list.");

        if (! (buildForm.second().equals(SExpression.keyword("OPERATOR"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        if (! (buildForm.third() instanceof SExpressionList))
            throw new IllegalArgumentException("Wrong type of :OPERATOR argument.");
        // Look up the SExpressionList specifically.
        SExpressionList opSExpr = (SExpressionList) buildForm.third();
        ExpressionUnary.Operator thisOp = makerToOperator.get(opSExpr);
        if (thisOp == null)
            throw new IllegalArgumentException("Unknown make function for Operator.");

        if (! (buildForm.fourth().equals(SExpression.keyword("OPERAND"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        if (! (buildForm.fifth() instanceof SExpressionList))
            throw new IllegalArgumentException("Wrong type of :OPERAND argument.");
        Object operandRaw = ASTBuilder.fromSExpression(buildForm.fifth());
        if (! (operandRaw instanceof Expression))
            throw new IllegalArgumentException("Wrong AST class returned by maker for :OPERAND");
        Expression operand = (Expression) operandRaw;

        return make(thisOp, operand);
    }

    public Operator getOperator() {
        return operator;
    }

    public Expression getOperand() {
        return operand;
    }

    @Override
    public SExpression toSExpression() {
        return SExpression.list(classMakerFn,
                SExpression.keyword("OPERATOR"), operator.toSExpression(),
                SExpression.keyword("OPERAND"), operand.toSExpression());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExpressionUnary that = (ExpressionUnary) o;
        return operator == that.operator &&
                operand.equals(that.operand);
    }

    @Override
    public int hashCode() {
        return Objects.hash(operator, operand);
    }

    @Override
    public String toString() {
        return this.toString(0);
    }

    @Override
    public String toString(int indentLevel) {
        StringBuilder s = new StringBuilder();
        s.append(line(indentLevel,
                "ExpressionUnary " + this.operator.toString() + " {"));
        s.append(this.operand.toString(indentLevel + 1));
        s.append(line(indentLevel, "}"));
        return new String(s);
    }
}
