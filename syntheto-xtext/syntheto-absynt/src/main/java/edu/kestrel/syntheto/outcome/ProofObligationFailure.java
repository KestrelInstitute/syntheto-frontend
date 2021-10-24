package edu.kestrel.syntheto.outcome;

import edu.kestrel.syntheto.ast.ASTBuilder;
import edu.kestrel.syntheto.sexpr.SExpression;
import edu.kestrel.syntheto.sexpr.SExpressionList;
import edu.kestrel.syntheto.sexpr.SExpressionString;
import edu.kestrel.syntheto.sexpr.SExpressionSymbol;

import edu.kestrel.syntheto.ast.Expression;

import java.util.Objects;

/**
 * Outcome of a Syntheto top-level construct
 * that produces a failing proof obligation.
 * This may be caused in a number of different top-level constructs.
 * It may also be caused by the failure to prove
 * an applicability condition of a transformation
 * (top-level constructs for transformations are planned additions to Syntheto):
 * we regard these applicability conditions as proof obligations as well.
 */
public class ProofObligationFailure extends Outcome {

    // This is the main information, but we may discover the need for more.
    private final Expression formula;

    public Expression getFormula() {
        return formula;
    }

    /**
     * Constructor.
     */
    private ProofObligationFailure(String info, Expression formula) {
        super(info);
        this.formula = formula;
    }

    /**
     * Static make method.
     *
     * @param info a string describing the proof obligation failure outcome
     * @param formula an expression that could not be proved
     * @return the new ProofObligationFailure object
     */
    public static ProofObligationFailure make(String info, Expression formula) {
        return new ProofObligationFailure(info, formula);
    }


    /* *************************************************************************
     * to and from SExpression
     * ************************************************************************/

    /**
     * The symbol for the ACL2 function that creates an instance of this class.
     */
    public static final SExpressionSymbol classMakerFn = SExpression.syntheto("MAKE-OUTCOME-PROOF-OBLIGATION-FAILURE");

    @Override
    public SExpression toSExpression() {
        return SExpression.list(classMakerFn,
                SExpression.keyword("MESSAGE"), SExpression.string(info),
                SExpression.keyword("OBLIGATION-EXPR"), formula.toSExpression());
    }

    /**
     * Constructs a ProofObligationFailure from an S-Expression
     * that looks like
     * (SYNTHETO::MAKE-OUTCOME-PROOF-OBLIGATION-FAILURE :MESSAGE MESSAGE :OBLIGATION-EXPR EXPR)
     *
     * @param buildFormArg The AST maker form.
     * @throws IllegalArgumentException If the list is malformed.
     */
    public static ProofObligationFailure fromSExpression(SExpression buildFormArg) {
        if (!(buildFormArg instanceof SExpressionList))
            throw new IllegalArgumentException("Argument must be an SExpressionList.");
        SExpressionList buildForm = (SExpressionList) buildFormArg;
        if (buildForm.length() != 5)
            throw new IllegalArgumentException("List not the right length.");
        if (!(buildForm.first().equals(classMakerFn)))
            throw new IllegalArgumentException("Wrong function in list.");

        if (!(buildForm.second().equals(SExpression.keyword("MESSAGE"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        if (!(buildForm.third() instanceof SExpressionString))
            throw new IllegalArgumentException("Wrong type of :MESSAGE argument.");
        String info = ((SExpressionString) buildForm.third()).getValue();

        if (! (buildForm.fourth().equals(SExpression.keyword("OBLIGATION-EXPR"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        if (! (buildForm.fifth() instanceof SExpressionList))
            throw new IllegalArgumentException("Wrong type of :OBLIGATION-EXPR argument.");
        Object exprRaw = ASTBuilder.fromSExpression(buildForm.fifth());
        if (! (exprRaw instanceof Expression))
            throw new IllegalArgumentException("Wrong AST class returned by maker for :OBLIGATION-EXPR");
        Expression expr = (Expression) exprRaw;

        return make(info, expr);
    }


    /* *************************************************************************
     * equals, hashCode, and toString
     * ************************************************************************/

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProofObligationFailure that = (ProofObligationFailure) o;
        return info.equals(that.info) && formula.equals(that.formula);
    }

    @Override
    public int hashCode() {
        return Objects.hash(info, formula);
    }



    @Override
    public String toString() {
        return this.toString(0);
    }

    @Override
    public String toString(int indentLevel) {
        StringBuilder s = new StringBuilder();
        s.append(line(indentLevel, "ProofObligationFailure {"));
        // TODO: change to print readably, e.g. StringEscapeUtils.escapeJava(info)
        //       in the package org.apache.commons.text
        s.append(line(indentLevel + 1, "info: \"" + info + "\";"));
        s.append(this.formula.toString(indentLevel + 1));
        s.append(line(indentLevel, "}"));
        return new String(s);
    }

}
