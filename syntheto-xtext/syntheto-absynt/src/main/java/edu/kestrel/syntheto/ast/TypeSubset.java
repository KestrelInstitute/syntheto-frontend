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
 * Syntheto subset type specifier.
 */
public class TypeSubset extends ASTNode {

    /**
     * The supertype of this subtype.
     * Never null.
     */
    private final Type supertype;

    /**
     * The free variable in {@link #restriction}.
     * Never null.
     */
    private final Identifier variable;

    /**
     * The expression defining the subset restriction.
     * Never null.
     * It should be a boolean-valued expression
     * with {@link #variable} as its only free variables,
     * which implicitly has the supertype in {@link #supertype}.
     */
    private final Expression restriction;

    /**
     * The expression defining the witness of the subtype's non-emptiness.
     * May be null; it is optional.
     * If present, it should be an expression without free variables
     * whose value satisfies the {@link #restriction}
     * when the {@link #variable} is replaced with the value.
     */
    private final Expression witness;

    /**
     * Constructs a subset type specifier.
     *
     * @param supertype   The supertype.
     * @param variable    The free variable in the restriction expression.
     * @param restriction The restriction that defines the subset.
     * @param witness     The witness to the restriction's satisfiability.
     * @throws IllegalArgumentException
     * When the supertype, variable, or restriction is null.
     */
    private TypeSubset(Type supertype,
                       Identifier variable,
                       Expression restriction,
                       Expression witness)
            throws IllegalArgumentException {
        if (supertype == null)
            throw new IllegalArgumentException("Null supertype.");
        if (variable == null)
            throw new IllegalArgumentException("Null variable.");
        if (restriction == null)
            throw new IllegalArgumentException("Null restriction.");
        // the witness is allowed to be null -- the witness is optional
        this.supertype = supertype;
        this.variable = variable;
        this.restriction = restriction;
        this.witness = witness;
    }

    /**
     * Builds a subset type specifier.
     *
     * @param supertype   The supertype.
     * @param variable    The free variable in the restriction expression.
     * @param restriction The restriction that defines the subset.
     * @param witness     The witness to the restriction's satisfiability.
     * @return The type specifier.
     * @throws IllegalArgumentException
     * When the supertype, variable, or restriction is null.
     */
    public static TypeSubset make(Type supertype,
                                  Identifier variable,
                                  Expression restriction,
                                  Expression witness) {
        return new TypeSubset(supertype, variable, restriction, witness);
    }

    /**
     * The symbol for the ACL2 function that creates an instance of this class.
     */
    public static final SExpressionSymbol classMakerFn = SExpression.syntheto("MAKE-TYPE-SUBSET");

    /**
     * Constructs a subset type specifier from an S-Expression
     * that looks like
     * (SYNTHETO::MAKE-TYPE-SUBSET :SUPERTYPE superType :VARIABLE freeVar :RESTRICTION rExpression :WITNESS <NIL or wExpression>)
     *
     * @param buildFormArg The AST maker form.
     * @throws IllegalArgumentException If the list is malformed.
     */
    public static TypeSubset fromSExpression(SExpression buildFormArg) {
        if (!(buildFormArg instanceof SExpressionList))
            throw new IllegalArgumentException("Argument must be an SExpressionList");
        SExpressionList buildForm = (SExpressionList) buildFormArg;
        if (buildForm.length() != 9)
            throw new IllegalArgumentException("List wrong length.");
        if (!(buildForm.first().equals(classMakerFn)))
            throw new IllegalArgumentException("Wrong function in list.");

        if (!(buildForm.second().equals(SExpression.keyword("SUPERTYPE"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        if (!(buildForm.third() instanceof SExpressionList))
            throw new IllegalArgumentException("Wrong type of :SUPERTYPE argument.");
        Object typeRaw = ASTBuilder.fromSExpression(buildForm.third());
        if (!(typeRaw instanceof Type))
            throw new IllegalArgumentException("Wrong AST class returned by maker for :SUPERTYPE");
        Type type = (Type) typeRaw;

        if (!(buildForm.fourth().equals(SExpression.keyword("VARIABLE"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        if (!(buildForm.fifth() instanceof SExpressionList))
            throw new IllegalArgumentException("Wrong type of :VARIABLE argument.");
        Object varRaw = ASTBuilder.fromSExpression(buildForm.fifth());
        if (!(varRaw instanceof Identifier))
            throw new IllegalArgumentException("Wrong AST class returned by maker for :VARIABLE");
        Identifier var = (Identifier) varRaw;

        if (!(buildForm.sixth().equals(SExpression.keyword("RESTRICTION"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        if (!(buildForm.seventh() instanceof SExpressionList))
            throw new IllegalArgumentException("Wrong type of :RESTRICTION argument.");
        Object restrictionRaw = ASTBuilder.fromSExpression(buildForm.seventh());
        if (!(restrictionRaw instanceof Expression))
            throw new IllegalArgumentException("Wrong AST class returned by maker for :RESTRICTION");
        Expression restriction = (Expression) restrictionRaw;

        if (!(buildForm.eighth().equals(SExpression.keyword("WITNESS"))))
            throw new IllegalArgumentException("Wrong keyword name.");
        Expression witness = null;
        if (!(buildForm.ninth().equals(SExpression.NIL()))) {
            if (!(buildForm.ninth() instanceof SExpressionList))
                throw new IllegalArgumentException("Wrong type of :WITNESS argument.");
            Object witnessRaw = ASTBuilder.fromSExpression(buildForm.ninth());
            if (!(witnessRaw instanceof Expression))
                throw new IllegalArgumentException("Wrong AST class returned by maker for :WITNESS");
            witness = (Expression) witnessRaw;
        }
        return make(type, var, restriction, witness);
    }

    /**
     * Returns the supertype of this subset type definer.
     *
     * @return The supertype.
     */
    public Type getSupertype() {
        return supertype;
    }

    /**
     * Returns the free variable in the restriction of this subset type definer.
     *
     * @return The variable.
     */
    public Identifier getVariable() {
        return variable;
    }

    /**
     * Returns the restriction of this subset type definer.
     *
     * @return The restriction.
     */
    public Expression getRestriction() {
        return restriction;
    }

    /**
     * Returns the witness of this subset type definer.
     *
     * @return The witness.
     */
    public Expression getWitness() {
        return witness;
    }

    @Override
    public SExpression toSExpression() {
        // NOTE: the Java class combines the ACL2 deftagsum alternative type-definer-subset
        // with the defprod type-subset
        return SExpression.list(classMakerFn,
                    SExpression.keyword("SUPERTYPE"), supertype.toSExpression(),
                    SExpression.keyword("VARIABLE"), variable.toSExpression(),
                    SExpression.keyword("RESTRICTION"), restriction.toSExpression(),
                    SExpression.keyword("WITNESS"),
                    (witness == null) ? SExpression.NIL() : witness.toSExpression());
    }

    /**
     * Checks if this object is equal to another object.
     *
     * @param o The object to compare this object with.
     * @return {@code true} if they are equal, {@code false} otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypeSubset that = (TypeSubset) o;
        // If either witness is null but not both, then return false.
        // (The .equals() below does not tolerate a null.)
        if ((witness == null) != (that.witness == null))
            return false;
        return supertype.equals(that.supertype) &&
                variable.equals(that.variable) &&
                restriction.equals(that.restriction) &&
                ( ((witness == null) && (that.witness == null))
                        || witness.equals(that.witness));
    }

    /**
     * Returns a hash code for this object.
     *
     * @return The hash code.
     */
    @Override
    public int hashCode() {
        return Objects.hash(supertype, variable, restriction, witness);
    }

    @Override
    public String toString() {
        return this.toString(0);
    }

    String toString(int indentLevel) {
        StringBuilder s = new StringBuilder();
        s.append(line(indentLevel, "TypeSubset {"));
        s.append(this.supertype.toString(indentLevel + 1));
        s.append(line(indentLevel + 1, this.variable.toString()));
        s.append(this.restriction.toString(indentLevel + 1));
        if (this.witness != null)
            s.append(this.witness.toString(indentLevel + 1));
        else
            s.append(line(indentLevel + 1, "/* no witness */"));
        s.append(line(indentLevel, "}"));
        return new String(s);
    }
}
