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
 * Syntheto binary expressions.
 */
public class ExpressionBinary extends Expression {

    public enum Operator {

        // Operator Name is the Enum,
        // Function name for constructing S-Expression is the constructor argument
        EQ("MAKE-BINARY-OP-EQ"),
        NE("MAKE-BINARY-OP-NE"),
        LT("MAKE-BINARY-OP-LT"),
        LE("MAKE-BINARY-OP-LE"),
        GT("MAKE-BINARY-OP-GT"),
        GE("MAKE-BINARY-OP-GE"),
        AND("MAKE-BINARY-OP-AND"),
        OR("MAKE-BINARY-OP-OR"),
        IMPLIES("MAKE-BINARY-OP-IMPLIES"),
        IMPLIED("MAKE-BINARY-OP-IMPLIED"),
        IFF("MAKE-BINARY-OP-IFF"),
        ADD("MAKE-BINARY-OP-ADD"),
        SUB("MAKE-BINARY-OP-SUB"),
        MUL("MAKE-BINARY-OP-MUL"),
        DIV("MAKE-BINARY-OP-DIV"),
        REM("MAKE-BINARY-OP-REM");

        private String sExprFnName;
        private SExpression sExpr;

        // The constructor
        private Operator(String fnName) {
            this.sExprFnName = fnName;
            this.sExpr = SExpression.list(SExpression.syntheto(fnName));
        }

        public String getSExprFnName() {
            return this.sExprFnName;
        }

        public SExpression toSExpression() { return this.sExpr; }

    } // end of Operator definition

    // After creating the operators, set up a map from SExpression form to operator.
    private static final HashMap<SExpression, Operator> makerToOperator = new HashMap<>();
    static {
        for (Operator operator: Operator.values()) {
            makerToOperator.put(operator.sExpr, operator);
        }
    }

    private final Operator operator;

    private final Expression leftOperand;

    private final Expression rightOperand;

    private ExpressionBinary(Operator operator,
                             Expression leftOperand,
                             Expression rightOperand) {
        this.operator = operator;
        this.leftOperand = leftOperand;
        this.rightOperand = rightOperand;
    }

    public static ExpressionBinary make(Operator operator,
                                        Expression leftOperand,
                                        Expression rightOperand) {
        return new ExpressionBinary(operator, leftOperand, rightOperand);
    }

    /**
     * The symbol for the ACL2 function that creates an instance of this class.
     */
    public static final SExpressionSymbol classMakerFn = SExpression.syntheto("MAKE-EXPRESSION-BINARY");

    /**
     * Constructs a binary expression from an S-Expression
     * that looks like
     * (SYNTHETO::MAKE-EXPRESSION-BINARY :OPERATOR op :LEFT-OPERAND left :RIGHT-OPERAND right)
     *
     * @param buildFormArg The AST maker form.
     * @throws IllegalArgumentException If the list is malformed.
     */
    public static ExpressionBinary fromSExpression(SExpression buildFormArg) {
        if (! (buildFormArg instanceof SExpressionList))
            throw new IllegalArgumentException("Argument must be an SExpressionList");
        SExpressionList buildForm = (SExpressionList) buildFormArg;
        if (buildForm.length() != 7)
            throw new IllegalArgumentException("List not long enough");
        if (! (buildForm.first().equals(classMakerFn)))
            throw new IllegalArgumentException("Wrong function in list.");

        if (! (buildForm.second().equals(SExpression.keyword("OPERATOR"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        if (! (buildForm.third() instanceof SExpressionList))
            throw new IllegalArgumentException("Wrong type of :OPERATOR argument.");
        // Look up the SExpressionList specifically.
        SExpressionList opSExpr = (SExpressionList) buildForm.third();
        Operator thisOp = makerToOperator.get(opSExpr);
        if (thisOp == null)
            throw new IllegalArgumentException("Unknown make function for Operator.");

        if (! (buildForm.fourth().equals(SExpression.keyword("LEFT-OPERAND"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        if (! (buildForm.fifth() instanceof SExpressionList))
            throw new IllegalArgumentException("Wrong type of :LEFT-OPERAND argument.");
        Object leftOpRaw = ASTBuilder.fromSExpression(buildForm.fifth());
        if (! (leftOpRaw instanceof Expression))
            throw new IllegalArgumentException("Wrong AST class returned by maker for :LEFT-OPERAND");
        Expression leftOp = (Expression) leftOpRaw;

        if (! (buildForm.sixth().equals(SExpression.keyword("RIGHT-OPERAND"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        if (! (buildForm.seventh() instanceof SExpressionList))
            throw new IllegalArgumentException("Wrong type of :RIGHT-OPERAND argument.");
        Object rightOpRaw = ASTBuilder.fromSExpression(buildForm.seventh());
        if (! (rightOpRaw instanceof Expression))
            throw new IllegalArgumentException("Wrong AST class returned by maker for :RIGHT-OPERAND");
        Expression rightOp = (Expression) rightOpRaw;

        return make(thisOp, leftOp, rightOp);
    }


    public Operator getOperator() {
        return operator;
    }

    public Expression getLeftOperand() {
        return leftOperand;
    }

    public Expression getRightOperand() {
        return rightOperand;
    }

    @Override
    public SExpression toSExpression() {
        return SExpression.list(classMakerFn,
                SExpression.keyword("OPERATOR"), operator.toSExpression(),
                SExpression.keyword("LEFT-OPERAND"), leftOperand.toSExpression(),
                SExpression.keyword("RIGHT-OPERAND"), rightOperand.toSExpression());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExpressionBinary that = (ExpressionBinary) o;
        return operator == that.operator &&
                leftOperand.equals(that.leftOperand) &&
                rightOperand.equals(that.rightOperand);
    }

    @Override
    public int hashCode() {
        return Objects.hash(operator, leftOperand, rightOperand);
    }

    @Override
    public String toString() {
        return this.toString(0);
    }

    @Override
    public String toString(int indentLevel) {
        StringBuilder s = new StringBuilder();
        s.append(line(indentLevel,
                "ExpressionBinary " + this.operator.toString() + " {"));
        s.append(this.leftOperand.toString(indentLevel + 1));
        s.append(this.rightOperand.toString(indentLevel + 1));
        s.append(line(indentLevel, "}"));
        return new String(s);
    }
}
