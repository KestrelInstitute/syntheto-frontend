/*
 * Copyright (C) 2021 Kestrel Institute (http://www.kestrel.edu)
 * License: 3-clause BSD license (https://opensource.org/licenses/BSD-3-Clause)
 * Main Author: Alessandro Coglio (coglio@kestrel.edu)
 * Contributing Author: Eric McCarthy (mccarthy@kestrel.edu)
 */
package edu.kestrel.syntheto.bridge;

import edu.kestrel.syntheto.ast.*;
import edu.kestrel.syntheto.outcome.*;
import edu.kestrel.syntheto.sexpr.*;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

// These are disabled by default because they require the bridge to be connected.
// Also, killBridge is not fleshed out.
// The other tests can be run individually after connecting to the Bridge.
@Disabled
class BridgeClientTest {

    @Test
    void sendAST() {
        try {
            BridgeClient.connectToBridge();

            Identifier x = Identifier.make("x");
            Identifier a = Identifier.make("a");
            FunctionDefinition functionAbs =
                    FunctionDefinition.make(
                            FunctionHeader.make(Identifier.make("abs"),
                                Arrays.asList(TypedVariable.make(x, TypeInteger.make())),
                                Arrays.asList(TypedVariable.make(a, TypeInteger.make()))),
                            null,
                            ExpressionBinary.make(ExpressionBinary.Operator.GE,
                                                  Variable.make(a),
                                                  ExpressionLiteral.make(LiteralInteger.make(0))),
                            FunctionDefinerRegular.make(
                                    ExpressionIf.make(
                                            ExpressionBinary.make(
                                                    ExpressionBinary.Operator.GE,
                                                    Variable.make(x),
                                                    ExpressionLiteral.make(LiteralInteger.make(0))),
                                            Variable.make(x),
                                            ExpressionUnary.make(ExpressionUnary.Operator.MINUS,
                                                    Variable.make(x))),
                                    null));
            TopLevelFunction tlfun = TopLevelFunction.make(functionAbs);
            String toSend = Util.wrapTopLevel(tlfun);
            BridgeClient.sendRawString(toSend);

            // It is not much to look at!
            BridgeClient.sendString("(pbt 1)");

            // Try calling it.
            BridgeClient.sendString("(syndef::|abs| -88)");

            BridgeClient.resetWorld();  // leave it ready for another test
            assertTrue(true);
        } catch (BridgeException e) {
            assertTrue(false);
        }
    }



    // support for below.
    // Returns whatever is at the spot where the (MAKE-.. ) form should be.
    // If the rest of the structure is not as expected, returns null.
    SExpression extractMakeForm(SExpression returnedSExpr) {
        if (!(returnedSExpr instanceof SExpressionList)) return null;
        SExpressionList fullForm = (SExpressionList) returnedSExpr;
        if (fullForm.length() != 3) return null;
        if (fullForm.first() != SExpression.NIL()) return null;
        if (fullForm.third() != SExpression.NIL()) return null;
        SExpression innerThing = fullForm.second();
        if (!(innerThing instanceof SExpressionList)) return null;
        SExpressionList innerForm = (SExpressionList) innerThing;
        if (innerForm.length() < 3) return null;
        if (innerForm.first() != SExpression.NIL()) return null;
        // TODO: innerForm.second() should be (NIL)
        SExpression makeForm = innerForm.rest().rest();
        // additional checks could go here
        return makeForm;
    }


    // this should get moved out of test, probably
    // Throws a BridgeException if BridgeClient.returnCommandResponse() failed,
    // or if the returned, parsed ASTNode is not equal to the original ASTNode
    void ExpressionRoundTrip(Expression node) throws BridgeException {
        SExpression lispifiedExpression = node.toSExpression();
        String serializedExpression = lispifiedExpression.toString();
        String mmwrappedExpression = "(syntheto::expression--make-myself " + serializedExpression + ")";
        BridgeResponse response = BridgeClient.returnCommandResponse(mmwrappedExpression);
        SExpression sexpr = response.lastParsedSExpression;
        if (sexpr == null) throw new BridgeException("no parsed SExpression");
        // There is a bunch of stuff wrapped here.
        // Here's an example of what it looks like:
        // (NIL
        //      (NIL (NIL) SYNTHETO::MAKE-EXPRESSION-LITERAL
        //                 :GET (SYNTHETO::MAKE-LITERAL-CHARACTER :VALUE (CODE-CHAR 97)))
        //  NIL)
        SExpression makeForm = extractMakeForm(sexpr);
        if (makeForm == null) throw new BridgeException("could not find (MAKE-..) form in response");
        ASTNode deserialized = ASTBuilder.fromSExpression(makeForm);
        if (!(node.equals(deserialized))) throw new BridgeException("makeForm did not build the same object as passed to the bridge");
        System.out.println("Round trip successful for " + node.getClass().getSimpleName());

    }
    // this should get moved out of test, probably
    // Throws a BridgeException if BridgeClient.returnCommandResponse() failed,
    // or if the returned, parsed ASTNode is not equal to the original ASTNode
    void TopLevelRoundTrip(TopLevel node) throws BridgeException {
        SExpression lispifiedExpression = node.toSExpression();
        String serializedExpression = lispifiedExpression.toString();
        String mmwrappedExpression = "(syntheto::toplevel--make-myself " + serializedExpression + ")";
        BridgeResponse response = BridgeClient.returnCommandResponse(mmwrappedExpression);
        SExpression sexpr = response.lastParsedSExpression;
        if (sexpr == null) throw new BridgeException("no parsed SExpression");
        // There is a bunch of stuff wrapped here.
        // Here's an example of what it looks like:
        // (NIL
        //      (NIL (NIL) SYNTHETO::MAKE-EXPRESSION-LITERAL
        //                 :GET (SYNTHETO::MAKE-LITERAL-CHARACTER :VALUE (CODE-CHAR 97)))
        //  NIL)
        SExpression makeForm = extractMakeForm(sexpr);
        if (makeForm == null) throw new BridgeException("could not find (MAKE-..) form in response");
        ASTNode deserialized = ASTBuilder.fromSExpression(makeForm);
        if (!(node.equals(deserialized))) throw new BridgeException("makeForm did not build the same object as passed to the bridge");
        System.out.println("Round trip successful for " + node.getClass().getSimpleName());

    }

    // this should get moved out of test, probably
    // Throws a BridgeException if BridgeClient.returnCommandResponse() failed,
    // or if the returned, parsed ASTNode is not equal to the original ASTNode
    void OutcomeRoundTrip(Outcome node) throws BridgeException {
        SExpression lispifiedExpression = node.toSExpression();
        String serializedExpression = lispifiedExpression.toString();
        String mmwrappedExpression = "(syntheto::outcome--make-myself " + serializedExpression + ")";
        BridgeResponse response = BridgeClient.returnCommandResponse(mmwrappedExpression);
        SExpression sexpr = response.lastParsedSExpression;
        if (sexpr == null) throw new BridgeException("no parsed SExpression");
        // There is a bunch of stuff wrapped here.
        // Here's an example of what it looks like:
        // (NIL
        //      (NIL (NIL) SYNTHETO::MAKE-OUTCOME-PROOF-OBLIGATION-FAILURE
        //                 :MESSAGE (SYNTHETO::MAKE-EXPRESSION-LITERAL
        //                 :GET (SYNTHETO::MAKE-LITERAL-CHARACTER :VALUE (CODE-CHAR 97))))
        //  NIL)
        SExpression makeForm = extractMakeForm(sexpr);
        if (makeForm == null) throw new BridgeException("could not find (MAKE-..) form in response");
        Outcome deserialized = OutcomeBuilder.fromSExpression(makeForm);
        if (!(node.equals(deserialized))) throw new BridgeException("makeForm did not build the same object as passed to the bridge");
        System.out.println("Round trip successful for " + node.getClass().getSimpleName());

    }

    // extractMakeForm() did not handle the result from SYNTHETO::PROCESS-SYNTHETO-TOPLEVEL properly
    // so here is a version that extracts "2" levels, as it were.
    // The expression returned by something like
    //   (SYNTHETO::PROCESS-SYNTHETO-TOPLEVEL (SYNTHETO::MAKE-TOPLEVEL-TYPE ..)
    // is
    //   (NIL (NIL (NIL NIL STATE)
    //             NIL
    //             (SYNTHETO::MAKE-OUTCOME-TYPE-SUCCESS :MESSAGE "positive")
    //             REPLACED-STATE)
    //        ACL2_INVISIBLE::|The Live State Itself|)
    // But we replace all the state things by NIL.
    SExpression extractMakeForm2(SExpression returnedSExpr) {
        if (!(returnedSExpr instanceof SExpressionList)) return null;
        SExpressionList fullForm = (SExpressionList) returnedSExpr;
        if (fullForm.length() != 3) return null;
        if (fullForm.first() != SExpression.NIL()) return null;
        if (fullForm.third() != SExpression.NIL()) return null;
        SExpression innerThing = fullForm.second();
        if (!(innerThing instanceof SExpressionList)) return null;
        SExpressionList innerForm = (SExpressionList) innerThing;
        if (innerForm.length() != 5) return null;
        if (innerForm.first() != SExpression.NIL()) return null;

        SExpression inThing1 = innerForm.second();
        if (!(inThing1 instanceof SExpressionList)) return null;
        SExpressionList inList1 = (SExpressionList) inThing1;
        if (inList1.length() != 3) return null;
        if (inList1.first() != SExpression.NIL()) return null;
        if (inList1.second() != SExpression.NIL()) return null;
        if (inList1.third() != SExpression.NIL()) return null;  // was STATE

        if (innerForm.third() != SExpression.NIL()) return null;
        if (innerForm.fifth() != SExpression.NIL()) return null; // was REPLACED-STATE

        SExpression makeForm = innerForm.fourth();
        // additional checks could go here
        return makeForm;
    }

    @Test
    void veryAdHocProcessToplevel() {
        String toSend = "(SYNTHETO::PROCESS-SYNTHETO-TOPLEVEL (SYNTHETO::MAKE-TOPLEVEL-TYPE\n" +
                " :GET\n" +
                " (SYNTHETO::MAKE-TYPE-DEFINITION\n" +
                "  :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"positive\")\n" +
                "  :BODY\n" +
                "  (SYNTHETO::MAKE-TYPE-DEFINER-SUBSET\n" +
                "    :GET\n" +
                "    (SYNTHETO::MAKE-TYPE-SUBSET\n" +
                "         :SUPERTYPE (SYNTHETO::MAKE-TYPE-INTEGER)\n" +
                "         :VARIABLE (SYNTHETO::MAKE-IDENTIFIER :NAME \"x\")\n" +
                "         :RESTRICTION\n" +
                "         (SYNTHETO::MAKE-EXPRESSION-BINARY\n" +
                "              :OPERATOR (SYNTHETO::MAKE-BINARY-OP-GT)\n" +
                "              :LEFT-OPERAND\n" +
                "              (SYNTHETO::MAKE-EXPRESSION-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"x\"))\n" +
                "              :RIGHT-OPERAND\n" +
                "              (SYNTHETO::MAKE-EXPRESSION-LITERAL :GET (SYNTHETO::MAKE-LITERAL-INTEGER :VALUE 0)))\n" +
                "         :WITNESS NIL)))))";
        try {
            BridgeClient.connectToBridge("LISP_MV");

            // Now take a part out of OutcomeRoundTrip above
            String mmwrappedExpression = toSend;
            BridgeResponse response = BridgeClient.returnCommandResponse(mmwrappedExpression);
            SExpression sexpr = response.lastParsedSExpression;
            SExpression makeForm = extractMakeForm2(sexpr);
            if (makeForm == null) throw new BridgeException("could not find (MAKE-..) form in response");
            Outcome deserialized = OutcomeBuilder.fromSExpression(makeForm);
            //if (!(node.equals(deserialized))) throw new BridgeException("makeForm did not build the same object as passed to the bridge");
            System.out.println("Got the outcome:\n" + deserialized.toString());

            assertTrue(true);
        } catch (BridgeException e) {
            assertTrue(false);
        }
    }

    // This one works, but I had to add literal newline to the allowed string chars
    @Test
    void veryAdHocProcessToplevel_2() {
        String toSend1 = "(SYNTHETO::PROCESS-SYNTHETO-TOPLEVEL (SYNTHETO::MAKE-TOPLEVEL-TYPE\n" +
                " :GET\n" +
                " (SYNTHETO::MAKE-TYPE-DEFINITION\n" +
                "  :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"positive\")\n" +
                "  :BODY\n" +
                "  (SYNTHETO::MAKE-TYPE-DEFINER-SUBSET\n" +
                "    :GET\n" +
                "    (SYNTHETO::MAKE-TYPE-SUBSET\n" +
                "         :SUPERTYPE (SYNTHETO::MAKE-TYPE-INTEGER)\n" +
                "         :VARIABLE (SYNTHETO::MAKE-IDENTIFIER :NAME \"x\")\n" +
                "         :RESTRICTION\n" +
                "         (SYNTHETO::MAKE-EXPRESSION-BINARY\n" +
                "              :OPERATOR (SYNTHETO::MAKE-BINARY-OP-GT)\n" +
                "              :LEFT-OPERAND\n" +
                "              (SYNTHETO::MAKE-EXPRESSION-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"x\"))\n" +
                "              :RIGHT-OPERAND\n" +
                "              (SYNTHETO::MAKE-EXPRESSION-LITERAL :GET (SYNTHETO::MAKE-LITERAL-INTEGER :VALUE 0)))\n" +
                "         :WITNESS NIL)))))";

        String toSend2 = "(SYNTHETO::PROCESS-SYNTHETO-TOPLEVEL (SYNTHETO::MAKE-TOPLEVEL-TYPE\n" +
                " :GET\n" +
                " (SYNTHETO::MAKE-TYPE-DEFINITION\n" +
                "  :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"mytype\")\n" +
                "  :BODY\n" +
                "  (SYNTHETO::MAKE-TYPE-DEFINER-PRODUCT\n" +
                "   :GET\n" +
                "   (SYNTHETO::MAKE-TYPE-PRODUCT\n" +
                "    :FIELDS\n" +
                "    (LIST\n" +
                "     (SYNTHETO::MAKE-FIELD\n" +
                "         :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"y\")\n" +
                "         :TYPE (SYNTHETO::MAKE-TYPE-DEFINED :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"positive\"))))\n" +
                "    :INVARIANT\n" +
                "    (SYNTHETO::MAKE-EXPRESSION-BINARY\n" +
                "         :OPERATOR (SYNTHETO::MAKE-BINARY-OP-GT)\n" +
                "         :LEFT-OPERAND\n" +
                "         (SYNTHETO::MAKE-EXPRESSION-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"y\"))\n" +
                "         :RIGHT-OPERAND\n" +
                "         (SYNTHETO::MAKE-EXPRESSION-LITERAL :GET (SYNTHETO::MAKE-LITERAL-INTEGER :VALUE 0))))))))";

        try {
            BridgeClient.connectToBridge("LISP_MV");
            BridgeClient.resetWorld();  // when debugging

            // Now take a part out of OutcomeRoundTrip above
            String mmwrappedExpression = toSend1;
            BridgeResponse response = BridgeClient.returnCommandResponse(mmwrappedExpression);
            SExpression sexpr = response.lastParsedSExpression;
            SExpression makeForm = extractMakeForm2(sexpr);
            if (makeForm == null) throw new BridgeException("could not find (MAKE-..) form in response");
            Outcome deserialized = OutcomeBuilder.fromSExpression(makeForm);
            //if (!(node.equals(deserialized))) throw new BridgeException("makeForm did not build the same object as passed to the bridge");
            System.out.println("Got the outcome:\n" + deserialized.toString());

              mmwrappedExpression = toSend2;
              response = BridgeClient.returnCommandResponse(mmwrappedExpression);
              sexpr = response.lastParsedSExpression;
              makeForm = extractMakeForm2(sexpr);
            if (makeForm == null) throw new BridgeException("could not find (MAKE-..) form in response");
              deserialized = OutcomeBuilder.fromSExpression(makeForm);
            //if (!(node.equals(deserialized))) throw new BridgeException("makeForm did not build the same object as passed to the bridge");
            System.out.println("Got the outcome:\n" + deserialized.toString());

            assertTrue(true);
        } catch (BridgeException e) {
            assertTrue(false);
        }
    }

    @Test
    void veryAdHocProcessToplevel_3() {
        String toSend1 = "(SYNTHETO::PROCESS-SYNTHETO-TOPLEVEL (SYNTHETO::MAKE-TOPLEVEL-TYPE\n" +
                " :GET\n" +
                " (SYNTHETO::MAKE-TYPE-DEFINITION\n" +
                "  :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"positive\")\n" +
                "  :BODY\n" +
                "  (SYNTHETO::MAKE-TYPE-DEFINER-SUBSET\n" +
                "    :GET\n" +
                "    (SYNTHETO::MAKE-TYPE-SUBSET\n" +
                "         :SUPERTYPE (SYNTHETO::MAKE-TYPE-INTEGER)\n" +
                "         :VARIABLE (SYNTHETO::MAKE-IDENTIFIER :NAME \"x\")\n" +
                "         :RESTRICTION\n" +
                "         (SYNTHETO::MAKE-EXPRESSION-BINARY\n" +
                "              :OPERATOR (SYNTHETO::MAKE-BINARY-OP-GT)\n" +
                "              :LEFT-OPERAND\n" +
                "              (SYNTHETO::MAKE-EXPRESSION-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"x\"))\n" +
                "              :RIGHT-OPERAND\n" +
                "              (SYNTHETO::MAKE-EXPRESSION-LITERAL :GET (SYNTHETO::MAKE-LITERAL-INTEGER :VALUE 0)))\n" +
                "         :WITNESS NIL)))))";

        String toSend2 = "(SYNTHETO::PROCESS-SYNTHETO-TOPLEVEL (SYNTHETO::MAKE-TOPLEVEL-TYPE\n" +
                " :GET\n" +
                " (SYNTHETO::MAKE-TYPE-DEFINITION\n" +
                "  :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"mytype\")\n" +
                "  :BODY\n" +
                "  (SYNTHETO::MAKE-TYPE-DEFINER-PRODUCT\n" +
                "   :GET\n" +
                "   (SYNTHETO::MAKE-TYPE-PRODUCT\n" +
                "    :FIELDS\n" +
                "    (LIST\n" +
                "     (SYNTHETO::MAKE-FIELD\n" +
                "         :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"y\")\n" +
                "         :TYPE (SYNTHETO::MAKE-TYPE-DEFINED :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"positive\"))))\n" +
                "    :INVARIANT\n" +
                "    (SYNTHETO::MAKE-EXPRESSION-BINARY\n" +
                "         :OPERATOR (SYNTHETO::MAKE-BINARY-OP-GT)\n" +
                "         :LEFT-OPERAND\n" +
                "         (SYNTHETO::MAKE-EXPRESSION-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"y\"))\n" +
                "         :RIGHT-OPERAND\n" +
                "         (SYNTHETO::MAKE-EXPRESSION-LITERAL :GET (SYNTHETO::MAKE-LITERAL-INTEGER :VALUE 0))))))))";

        String toSend3 = "(SYNTHETO::PROCESS-SYNTHETO-TOPLEVEL (SYNTHETO::MAKE-TOPLEVEL-FUNCTION\n" +
                " :GET\n" +
                " (SYNTHETO::MAKE-FUNCTION-DEFINITION\n" +
                "  :HEADER\n" +
                "  (SYNTHETO::MAKE-FUNCTION-HEADER\n" +
                "      :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"gcd\")\n" +
                "      :INPUTS (LIST (SYNTHETO::MAKE-TYPED-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"x\")\n" +
                "                                         :TYPE (SYNTHETO::MAKE-TYPE-INTEGER))\n" +
                "                    (SYNTHETO::MAKE-TYPED-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"y\")\n" +
                "                                         :TYPE (SYNTHETO::MAKE-TYPE-INTEGER)))\n" +
                "      :OUTPUTS (LIST (SYNTHETO::MAKE-TYPED-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"out\")\n" +
                "                                          :TYPE (SYNTHETO::MAKE-TYPE-INTEGER))))\n" +
                "  :PRECONDITION\n" +
                "  (SYNTHETO::MAKE-EXPRESSION-BINARY\n" +
                "       :OPERATOR (SYNTHETO::MAKE-BINARY-OP-AND)\n" +
                "       :LEFT-OPERAND\n" +
                "       (SYNTHETO::MAKE-EXPRESSION-BINARY\n" +
                "            :OPERATOR (SYNTHETO::MAKE-BINARY-OP-GE)\n" +
                "            :LEFT-OPERAND\n" +
                "            (SYNTHETO::MAKE-EXPRESSION-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"x\"))\n" +
                "            :RIGHT-OPERAND\n" +
                "            (SYNTHETO::MAKE-EXPRESSION-LITERAL :GET (SYNTHETO::MAKE-LITERAL-INTEGER :VALUE 0)))\n" +
                "       :RIGHT-OPERAND\n" +
                "       (SYNTHETO::MAKE-EXPRESSION-BINARY\n" +
                "            :OPERATOR (SYNTHETO::MAKE-BINARY-OP-GE)\n" +
                "            :LEFT-OPERAND\n" +
                "            (SYNTHETO::MAKE-EXPRESSION-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"y\"))\n" +
                "            :RIGHT-OPERAND\n" +
                "            (SYNTHETO::MAKE-EXPRESSION-LITERAL :GET (SYNTHETO::MAKE-LITERAL-INTEGER :VALUE 0))))\n" +
                "  :POSTCONDITION NIL\n" +
                "  :DEFINER\n" +
                "  (SYNTHETO::MAKE-FUNCTION-DEFINER-REGULAR\n" +
                "   :BODY\n" +
                "   (SYNTHETO::MAKE-EXPRESSION-IF\n" +
                "    :TEST\n" +
                "    (SYNTHETO::MAKE-EXPRESSION-BINARY\n" +
                "         :OPERATOR (SYNTHETO::MAKE-BINARY-OP-EQ)\n" +
                "         :LEFT-OPERAND\n" +
                "         (SYNTHETO::MAKE-EXPRESSION-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"x\"))\n" +
                "         :RIGHT-OPERAND\n" +
                "         (SYNTHETO::MAKE-EXPRESSION-LITERAL :GET (SYNTHETO::MAKE-LITERAL-INTEGER :VALUE 0)))\n" +
                "    :THEN (SYNTHETO::MAKE-EXPRESSION-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"y\"))\n" +
                "    :ELSE\n" +
                "    (SYNTHETO::MAKE-EXPRESSION-IF\n" +
                "     :TEST\n" +
                "     (SYNTHETO::MAKE-EXPRESSION-BINARY\n" +
                "          :OPERATOR (SYNTHETO::MAKE-BINARY-OP-EQ)\n" +
                "          :LEFT-OPERAND\n" +
                "          (SYNTHETO::MAKE-EXPRESSION-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"y\"))\n" +
                "          :RIGHT-OPERAND\n" +
                "          (SYNTHETO::MAKE-EXPRESSION-LITERAL :GET (SYNTHETO::MAKE-LITERAL-INTEGER :VALUE 0)))\n" +
                "     :THEN (SYNTHETO::MAKE-EXPRESSION-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"x\"))\n" +
                "     :ELSE\n" +
                "     (SYNTHETO::MAKE-EXPRESSION-IF\n" +
                "      :TEST\n" +
                "      (SYNTHETO::MAKE-EXPRESSION-BINARY\n" +
                "           :OPERATOR (SYNTHETO::MAKE-BINARY-OP-LT)\n" +
                "           :LEFT-OPERAND\n" +
                "           (SYNTHETO::MAKE-EXPRESSION-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"x\"))\n" +
                "           :RIGHT-OPERAND\n" +
                "           (SYNTHETO::MAKE-EXPRESSION-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"y\")))\n" +
                "      :THEN\n" +
                "      (SYNTHETO::MAKE-EXPRESSION-CALL\n" +
                "       :FUNCTION (SYNTHETO::MAKE-IDENTIFIER :NAME \"gcd\")\n" +
                "       :TYPES (LIST)\n" +
                "       :ARGUMENTS\n" +
                "       (LIST\n" +
                "         (SYNTHETO::MAKE-EXPRESSION-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"x\"))\n" +
                "         (SYNTHETO::MAKE-EXPRESSION-BINARY\n" +
                "              :OPERATOR (SYNTHETO::MAKE-BINARY-OP-REM)\n" +
                "              :LEFT-OPERAND\n" +
                "              (SYNTHETO::MAKE-EXPRESSION-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"y\"))\n" +
                "              :RIGHT-OPERAND\n" +
                "              (SYNTHETO::MAKE-EXPRESSION-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"x\")))))\n" +
                "      :ELSE\n" +
                "      (SYNTHETO::MAKE-EXPRESSION-CALL\n" +
                "       :FUNCTION (SYNTHETO::MAKE-IDENTIFIER :NAME \"gcd\")\n" +
                "       :TYPES (LIST)\n" +
                "       :ARGUMENTS\n" +
                "       (LIST\n" +
                "           (SYNTHETO::MAKE-EXPRESSION-BINARY\n" +
                "                :OPERATOR (SYNTHETO::MAKE-BINARY-OP-REM)\n" +
                "                :LEFT-OPERAND\n" +
                "                (SYNTHETO::MAKE-EXPRESSION-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"x\"))\n" +
                "                :RIGHT-OPERAND\n" +
                "                (SYNTHETO::MAKE-EXPRESSION-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"y\")))\n" +
                "           (SYNTHETO::MAKE-EXPRESSION-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"y\")))))))\n" +
                "   :MEASURE\n" +
                "   (SYNTHETO::MAKE-EXPRESSION-BINARY\n" +
                "        :OPERATOR (SYNTHETO::MAKE-BINARY-OP-ADD)\n" +
                "        :LEFT-OPERAND\n" +
                "        (SYNTHETO::MAKE-EXPRESSION-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"x\"))\n" +
                "        :RIGHT-OPERAND\n" +
                "        (SYNTHETO::MAKE-EXPRESSION-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"y\")))))))";

        try {
            BridgeClient.connectToBridge("LISP_MV");
            BridgeClient.resetWorld();  // when debugging

            // Now take a part out of OutcomeRoundTrip above
            String mmwrappedExpression = toSend1;
            BridgeResponse response = BridgeClient.returnCommandResponse(mmwrappedExpression);
            SExpression sexpr = response.lastParsedSExpression;
            SExpression makeForm = extractMakeForm2(sexpr);
            if (makeForm == null) throw new BridgeException("could not find (MAKE-..) form in response");
            Outcome deserialized = OutcomeBuilder.fromSExpression(makeForm);
            //if (!(node.equals(deserialized))) throw new BridgeException("makeForm did not build the same object as passed to the bridge");
            System.out.println("Got the outcome:\n" + deserialized.toString());

            mmwrappedExpression = toSend2;
            response = BridgeClient.returnCommandResponse(mmwrappedExpression);
            sexpr = response.lastParsedSExpression;
            makeForm = extractMakeForm2(sexpr);
            if (makeForm == null) throw new BridgeException("could not find (MAKE-..) form in response");
            deserialized = OutcomeBuilder.fromSExpression(makeForm);
            //if (!(node.equals(deserialized))) throw new BridgeException("makeForm did not build the same object as passed to the bridge");
            System.out.println("Got the outcome:\n" + deserialized.toString());

            mmwrappedExpression = toSend3;
            response = BridgeClient.returnCommandResponse(mmwrappedExpression);
            sexpr = response.lastParsedSExpression;
            makeForm = extractMakeForm2(sexpr);
            if (makeForm == null) throw new BridgeException("could not find (MAKE-..) form in response");
            deserialized = OutcomeBuilder.fromSExpression(makeForm);
            //if (!(node.equals(deserialized))) throw new BridgeException("makeForm did not build the same object as passed to the bridge");
            System.out.println("Got the outcome:\n" + deserialized.toString());

            assertTrue(true);
        } catch (BridgeException e) {
            assertTrue(false);
        }
    }

    @Test
    void adHocProofObligFailure() {
        String toSend = "(SYNTHETO::PROCESS-SYNTHETO-TOPLEVEL (SYNTHETO::MAKE-TOPLEVEL-FUNCTION\n" +
                " :GET\n" +
                " (SYNTHETO::MAKE-FUNCTION-DEFINITION\n" +
                "  :HEADER\n" +
                "  (SYNTHETO::MAKE-FUNCTION-HEADER\n" +
                "       :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"abs_bad\")\n" +
                "       :INPUTS (LIST (SYNTHETO::MAKE-TYPED-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"x\")\n" +
                "                                          :TYPE (SYNTHETO::MAKE-TYPE-INTEGER)))\n" +
                "       :OUTPUTS (LIST (SYNTHETO::MAKE-TYPED-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"a\")\n" +
                "                                           :TYPE (SYNTHETO::MAKE-TYPE-INTEGER))))\n" +
                "  :PRECONDITION NIL\n" +
                "  :POSTCONDITION\n" +
                "  (SYNTHETO::MAKE-EXPRESSION-BINARY\n" +
                "   :OPERATOR (SYNTHETO::MAKE-BINARY-OP-GT)\n" +
                "   :LEFT-OPERAND (SYNTHETO::MAKE-EXPRESSION-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"a\"))\n" +
                "   :RIGHT-OPERAND\n" +
                "   (SYNTHETO::MAKE-EXPRESSION-LITERAL :GET (SYNTHETO::MAKE-LITERAL-INTEGER :VALUE 0)))\n" +
                "  :DEFINER\n" +
                "  (SYNTHETO::MAKE-FUNCTION-DEFINER-REGULAR\n" +
                "   :BODY\n" +
                "   (SYNTHETO::MAKE-EXPRESSION-IF\n" +
                "    :TEST\n" +
                "    (SYNTHETO::MAKE-EXPRESSION-BINARY\n" +
                "         :OPERATOR (SYNTHETO::MAKE-BINARY-OP-GE)\n" +
                "         :LEFT-OPERAND\n" +
                "         (SYNTHETO::MAKE-EXPRESSION-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"x\"))\n" +
                "         :RIGHT-OPERAND\n" +
                "         (SYNTHETO::MAKE-EXPRESSION-LITERAL :GET (SYNTHETO::MAKE-LITERAL-INTEGER :VALUE 0)))\n" +
                "    :THEN (SYNTHETO::MAKE-EXPRESSION-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"x\"))\n" +
                "    :ELSE\n" +
                "    (SYNTHETO::MAKE-EXPRESSION-UNARY\n" +
                "      :OPERATOR (SYNTHETO::MAKE-UNARY-OP-MINUS)\n" +
                "      :OPERAND (SYNTHETO::MAKE-EXPRESSION-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"x\"))))\n" +
                "   :MEASURE NIL))))";
        try {
            BridgeClient.connectToBridge("LISP_MV");
            // BridgeClient.resetWorld();  // when debugging

            // Now take a part out of OutcomeRoundTrip above
            String mmwrappedExpression = toSend;
            BridgeResponse response = BridgeClient.returnCommandResponse(mmwrappedExpression);
            SExpression sexpr = response.lastParsedSExpression;
            SExpression makeForm = extractMakeForm2(sexpr);
            if (makeForm == null) throw new BridgeException("could not find (MAKE-..) form in response");
            Outcome deserialized = OutcomeBuilder.fromSExpression(makeForm);
            //if (!(node.equals(deserialized))) throw new BridgeException("makeForm did not build the same object as passed to the bridge");
            System.out.println("Got the outcome:\n" + deserialized.toString());

            assertTrue(true);
        } catch (BridgeException e) {
            assertTrue(false);
        }

    }


    @Test
    void AdHocProcessToplevelTransform() {
        String toSend1 = "(SYNTHETO::PROCESS-SYNTHETO-TOPLEVEL\n" +
                "  (SYNTHETO::MAKE-TOPLEVEL-FUNCTION\n" +
                "   :GET\n" +
                "   (SYNTHETO::MAKE-FUNCTION-DEFINITION\n" +
                "    :HEADER\n" +
                "    (SYNTHETO::MAKE-FUNCTION-HEADER\n" +
                "        :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"factorial\")\n" +
                "        :INPUTS (LIST (SYNTHETO::MAKE-TYPED-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"n\")\n" +
                "                                           :TYPE (SYNTHETO::MAKE-TYPE-INTEGER)))\n" +
                "        :OUTPUTS (LIST (SYNTHETO::MAKE-TYPED-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"out\")\n" +
                "                                            :TYPE (SYNTHETO::MAKE-TYPE-INTEGER))))\n" +
                "    :PRECONDITION\n" +
                "    (SYNTHETO::MAKE-EXPRESSION-BINARY\n" +
                "     :OPERATOR (SYNTHETO::MAKE-BINARY-OP-GE)\n" +
                "     :LEFT-OPERAND (SYNTHETO::MAKE-EXPRESSION-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"n\"))\n" +
                "     :RIGHT-OPERAND\n" +
                "     (SYNTHETO::MAKE-EXPRESSION-LITERAL :GET (SYNTHETO::MAKE-LITERAL-INTEGER :VALUE 0)))\n" +
                "    :POSTCONDITION\n" +
                "    (SYNTHETO::MAKE-EXPRESSION-BINARY\n" +
                "         :OPERATOR (SYNTHETO::MAKE-BINARY-OP-GT)\n" +
                "         :LEFT-OPERAND\n" +
                "         (SYNTHETO::MAKE-EXPRESSION-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"out\"))\n" +
                "         :RIGHT-OPERAND\n" +
                "         (SYNTHETO::MAKE-EXPRESSION-LITERAL :GET (SYNTHETO::MAKE-LITERAL-INTEGER :VALUE 0)))\n" +
                "    :DEFINER\n" +
                "    (SYNTHETO::MAKE-FUNCTION-DEFINER-REGULAR\n" +
                "     :BODY\n" +
                "     (SYNTHETO::MAKE-EXPRESSION-IF\n" +
                "      :TEST\n" +
                "      (SYNTHETO::MAKE-EXPRESSION-BINARY\n" +
                "           :OPERATOR (SYNTHETO::MAKE-BINARY-OP-EQ)\n" +
                "           :LEFT-OPERAND\n" +
                "           (SYNTHETO::MAKE-EXPRESSION-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"n\"))\n" +
                "           :RIGHT-OPERAND\n" +
                "           (SYNTHETO::MAKE-EXPRESSION-LITERAL :GET (SYNTHETO::MAKE-LITERAL-INTEGER :VALUE 0)))\n" +
                "      :THEN (SYNTHETO::MAKE-EXPRESSION-LITERAL :GET (SYNTHETO::MAKE-LITERAL-INTEGER :VALUE 1))\n" +
                "      :ELSE\n" +
                "      (SYNTHETO::MAKE-EXPRESSION-BINARY\n" +
                "       :OPERATOR (SYNTHETO::MAKE-BINARY-OP-MUL)\n" +
                "       :LEFT-OPERAND\n" +
                "       (SYNTHETO::MAKE-EXPRESSION-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"n\"))\n" +
                "       :RIGHT-OPERAND\n" +
                "       (SYNTHETO::MAKE-EXPRESSION-CALL\n" +
                "        :FUNCTION (SYNTHETO::MAKE-IDENTIFIER :NAME \"factorial\")\n" +
                "        :TYPES (LIST)\n" +
                "        :ARGUMENTS\n" +
                "        (LIST\n" +
                "         (SYNTHETO::MAKE-EXPRESSION-BINARY\n" +
                "            :OPERATOR (SYNTHETO::MAKE-BINARY-OP-SUB)\n" +
                "            :LEFT-OPERAND\n" +
                "            (SYNTHETO::MAKE-EXPRESSION-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"n\"))\n" +
                "            :RIGHT-OPERAND\n" +
                "            (SYNTHETO::MAKE-EXPRESSION-LITERAL :GET (SYNTHETO::MAKE-LITERAL-INTEGER :VALUE 1)))))))\n" +
                "     :MEASURE NIL))))";

        String toSend2 = "(SYNTHETO::PROCESS-SYNTHETO-TOPLEVEL\n" +
                " (SYNTHETO::MAKE-TOPLEVEL-TRANSFORM\n" +
                " :GET\n" +
                " (SYNTHETO::MAKE-TRANSFORM\n" +
                "     :NEW-FUNCTION-NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"factorial_t\")\n" +
                "     :OLD-FUNCTION-NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"factorial\")\n" +
                "     :TRANSFORM-NAME \"tail_recursion\"\n" +
                "     :ARGUMENTS (LIST (SYNTHETO::MAKE-TRANSFORM-ARGUMENT\n" +
                "                           :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"new_parameter_name\")\n" +
                "                           :VALUE (SYNTHETO::MAKE-TRANSFORM-ARGUMENT-VALUE-IDENTIFIER\n" +
                "                                   :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"r\")))))))";

        try {
            BridgeClient.connectToBridge("LISP_MV");
            BridgeClient.resetWorld();  // when debugging

            // Now take a part out of OutcomeRoundTrip above
            String mmwrappedExpression = toSend1;
            BridgeResponse response = BridgeClient.returnCommandResponse(mmwrappedExpression);
            SExpression sexpr = response.lastParsedSExpression;
            SExpression makeForm = extractMakeForm2(sexpr);
            if (makeForm == null) throw new BridgeException("could not find (MAKE-..) form in response");
            Outcome deserialized = OutcomeBuilder.fromSExpression(makeForm);
            //if (!(node.equals(deserialized))) throw new BridgeException("makeForm did not build the same object as passed to the bridge");
            System.out.println("Got the outcome:\n" + deserialized.toString());

            mmwrappedExpression = toSend2;
            response = BridgeClient.returnCommandResponse(mmwrappedExpression);
            sexpr = response.lastParsedSExpression;
            makeForm = extractMakeForm2(sexpr);
            if (makeForm == null) throw new BridgeException("could not find (MAKE-..) form in response");
            deserialized = OutcomeBuilder.fromSExpression(makeForm);
            //if (!(node.equals(deserialized))) throw new BridgeException("makeForm did not build the same object as passed to the bridge");
            System.out.println("Got the outcome:\n" + deserialized.toString());

            assertTrue(true);
        } catch (BridgeException e) {
            assertTrue(false);
        }
    }

    @Test
    void AdHocProcessToplevelPath() {
        String toSend1 = "(SYNTHETO::PROCESS-SYNTHETO-TOPLEVEL\n" +
                " (SYNTHETO::MAKE-TOPLEVEL-TYPE\n" +
                "  :GET\n" +
                "  (SYNTHETO::MAKE-TYPE-DEFINITION\n" +
                "   :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"point\")\n" +
                "   :BODY\n" +
                "   (SYNTHETO::MAKE-TYPE-DEFINER-PRODUCT\n" +
                "    :GET\n" +
                "    (SYNTHETO::MAKE-TYPE-PRODUCT\n" +
                "     :FIELDS\n" +
                "     (LIST (SYNTHETO::MAKE-FIELD :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"x\")\n" +
                "                                 :TYPE (SYNTHETO::MAKE-TYPE-INTEGER))\n" +
                "           (SYNTHETO::MAKE-FIELD :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"y\")\n" +
                "                                 :TYPE (SYNTHETO::MAKE-TYPE-INTEGER)))\n" +
                "     :INVARIANT NIL)))))";
        String toSend2 = "(SYNTHETO::PROCESS-SYNTHETO-TOPLEVEL\n" +
                " (SYNTHETO::MAKE-TOPLEVEL-TYPE\n" +
                "  :GET\n" +
                "  (SYNTHETO::MAKE-TYPE-DEFINITION\n" +
                "   :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"edge\")\n" +
                "   :BODY\n" +
                "   (SYNTHETO::MAKE-TYPE-DEFINER-PRODUCT\n" +
                "    :GET\n" +
                "    (SYNTHETO::MAKE-TYPE-PRODUCT\n" +
                "     :FIELDS\n" +
                "     (LIST (SYNTHETO::MAKE-FIELD :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"p1\")\n" +
                "                                 :TYPE (SYNTHETO::MAKE-TYPE-DEFINED :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"point\")))\n" +
                "           (SYNTHETO::MAKE-FIELD :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"p2\")\n" +
                "                                 :TYPE (SYNTHETO::MAKE-TYPE-DEFINED :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"point\"))))\n" +
                "     :INVARIANT NIL)))))";
        String toSend3 = "(SYNTHETO::PROCESS-SYNTHETO-TOPLEVEL\n" +
                " (SYNTHETO::MAKE-TOPLEVEL-FUNCTION\n" +
                "  :GET\n" +
                "  (SYNTHETO::MAKE-FUNCTION-DEFINITION\n" +
                "   :HEADER\n" +
                "   (SYNTHETO::MAKE-FUNCTION-HEADER\n" +
                "    :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"path\")\n" +
                "    :INPUTS (LIST (SYNTHETO::MAKE-TYPED-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"vertices\") :TYPE (SYNTHETO::MAKE-TYPE-SEQUENCE :ELEMENT (SYNTHETO::MAKE-TYPE-DEFINED :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"point\")))))\n" +
                "    :OUTPUTS (LIST (SYNTHETO::MAKE-TYPED-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"edges\") :TYPE (SYNTHETO::MAKE-TYPE-SEQUENCE :ELEMENT (SYNTHETO::MAKE-TYPE-DEFINED :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"edge\"))))))\n" +
                "   :PRECONDITION NIL\n" +
                "   :POSTCONDITION NIL\n" +
                "   :DEFINER\n" +
                "   (SYNTHETO::MAKE-FUNCTION-DEFINER-REGULAR\n" +
                "    :BODY\n" +
                "    (SYNTHETO::MAKE-EXPRESSION-IF\n" +
                "     :TEST\n" +
                "     (SYNTHETO::MAKE-EXPRESSION-BINARY\n" +
                "      :OPERATOR (SYNTHETO::MAKE-BINARY-OP-OR)\n" +
                "      :LEFT-OPERAND (SYNTHETO::MAKE-EXPRESSION-CALL :FUNCTION (SYNTHETO::MAKE-IDENTIFIER :NAME \"is_empty\")\n" +
                "                                                    ;; These types must be inferred by the front end:                                                                                                                                                                                                                                                                                                                         \n" +
                "                                                    :TYPES (LIST (SYNTHETO::MAKE-TYPE-SEQUENCE :ELEMENT (SYNTHETO::MAKE-TYPE-DEFINED :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"point\"))))\n" +
                "                                                    :ARGUMENTS (LIST (SYNTHETO::MAKE-EXPRESSION-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"vertices\"))))\n" +
                "      :RIGHT-OPERAND (SYNTHETO::MAKE-EXPRESSION-CALL :FUNCTION (SYNTHETO::MAKE-IDENTIFIER :NAME \"is_empty\")\n" +
                "                                                     ;; inferred:                                                                                                                                                                                                                                                                                                                                                             \n" +
                "                                                     :TYPES (LIST (SYNTHETO::MAKE-TYPE-SEQUENCE :ELEMENT (SYNTHETO::MAKE-TYPE-DEFINED :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"point\"))))\n" +
                "                                                     :ARGUMENTS (LIST (SYNTHETO::MAKE-EXPRESSION-CALL :FUNCTION (SYNTHETO::MAKE-IDENTIFIER :NAME \"rest\")\n" +
                "                                                                                                      ;; inferred:                                                                                                                                                                                                                                                                                                            \n" +
                "                                                                                                      :TYPES (LIST (SYNTHETO::MAKE-TYPE-SEQUENCE :ELEMENT (SYNTHETO::MAKE-TYPE-DEFINED :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"point\"))))\n" +
                "                                                                                                      :ARGUMENTS (LIST (SYNTHETO::MAKE-EXPRESSION-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"vertices\"))))))\n" +
                "      )\n" +
                "     :THEN\n" +
                "     (SYNTHETO::MAKE-EXPRESSION-CALL :FUNCTION (SYNTHETO::MAKE-IDENTIFIER :NAME \"empty\")\n" +
                "                                     :TYPES (LIST (SYNTHETO::MAKE-TYPE-SEQUENCE :ELEMENT (SYNTHETO::MAKE-TYPE-DEFINED :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"edge\"))))\n" +
                "                                     :ARGUMENTS (LIST))\n" +
                "     :ELSE\n" +
                "     (SYNTHETO::MAKE-EXPRESSION-CALL :FUNCTION (SYNTHETO::MAKE-IDENTIFIER :NAME \"add\")\n" +
                "                                     :TYPES (LIST (SYNTHETO::MAKE-TYPE-SEQUENCE :ELEMENT (SYNTHETO::MAKE-TYPE-DEFINED :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"edge\"))))\n" +
                "                                     :ARGUMENTS (LIST (SYNTHETO::MAKE-EXPRESSION-PRODUCT-CONSTRUCT\n" +
                "                                                       :TYPE (SYNTHETO::MAKE-IDENTIFIER :NAME \"edge\")\n" +
                "                                                       :FIELDS (LIST (SYNTHETO::MAKE-INITIALIZER :FIELD (SYNTHETO::MAKE-IDENTIFIER :NAME \"p1\")\n" +
                "                                                                                                 :VALUE (SYNTHETO::MAKE-EXPRESSION-CALL :FUNCTION (SYNTHETO::MAKE-IDENTIFIER :NAME \"first\")\n" +
                "                                                                                                                                        :TYPES (LIST (SYNTHETO::MAKE-TYPE-SEQUENCE :ELEMENT (SYNTHETO::MAKE-TYPE-DEFINED :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"point\"))))\n" +
                "                                                                                                                                        :ARGUMENTS (LIST (SYNTHETO::MAKE-EXPRESSION-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"vertices\")))))\n" +
                "                                                                     (SYNTHETO::MAKE-INITIALIZER :FIELD (SYNTHETO::MAKE-IDENTIFIER :NAME \"p2\")\n" +
                "                                                                                                 :VALUE (SYNTHETO::MAKE-EXPRESSION-CALL :FUNCTION (SYNTHETO::MAKE-IDENTIFIER :NAME \"first\")\n" +
                "                                                                                                                                        :TYPES (LIST (SYNTHETO::MAKE-TYPE-SEQUENCE :ELEMENT (SYNTHETO::MAKE-TYPE-DEFINED :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"point\"))))\n" +
                "                                                                                                                                        :ARGUMENTS (LIST (SYNTHETO::MAKE-EXPRESSION-CALL :FUNCTION (SYNTHETO::MAKE-IDENTIFIER :NAME \"rest\")\n" +
                "                                                                                                                                                                                         :TYPES (LIST (SYNTHETO::MAKE-TYPE-SEQUENCE :ELEMENT (SYNTHETO::MAKE-TYPE-DEFINED :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"point\"))))\n" +
                "                                                                                                                                                                                         :ARGUMENTS (LIST (SYNTHETO::MAKE-EXPRESSION-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"vertices\")))))))))\n" +
                "                                                      (SYNTHETO::MAKE-EXPRESSION-CALL :FUNCTION (SYNTHETO::MAKE-IDENTIFIER :NAME \"path\")\n" +
                "                                                                                      :TYPES NIL\n" +
                "                                                                                      :ARGUMENTS (LIST (SYNTHETO::MAKE-EXPRESSION-CALL :FUNCTION (SYNTHETO::MAKE-IDENTIFIER :NAME \"rest\")\n" +
                "                                                                                                                                       :TYPES (LIST (SYNTHETO::MAKE-TYPE-SEQUENCE :ELEMENT (SYNTHETO::MAKE-TYPE-DEFINED :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"point\"))))\n" +
                "                                                                                                                                       :ARGUMENTS (LIST (SYNTHETO::MAKE-EXPRESSION-VARIABLE :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"vertices\")))))))))\n" +
                "    :MEASURE NIL))))";
        //String toSend4 = "";
        //String toSend5 = "";
        //String toSend6 = "";


        try {
            BridgeClient.connectToBridge("LISP_MV");
            BridgeClient.resetWorld();  // when debugging

            // Now take a part out of OutcomeRoundTrip above
            String mmwrappedExpression = toSend1;
            BridgeResponse response = BridgeClient.returnCommandResponse(mmwrappedExpression);
            SExpression sexpr = response.lastParsedSExpression;
            SExpression makeForm = extractMakeForm2(sexpr);
            if (makeForm == null) throw new BridgeException("could not find (MAKE-..) form in response");
            Outcome deserialized = OutcomeBuilder.fromSExpression(makeForm);
            //if (!(node.equals(deserialized))) throw new BridgeException("makeForm did not build the same object as passed to the bridge");
            System.out.println("Got the outcome:\n" + deserialized.toString());

            mmwrappedExpression = toSend2;
            response = BridgeClient.returnCommandResponse(mmwrappedExpression);
            sexpr = response.lastParsedSExpression;
            makeForm = extractMakeForm2(sexpr);
            if (makeForm == null) throw new BridgeException("could not find (MAKE-..) form in response");
            deserialized = OutcomeBuilder.fromSExpression(makeForm);
            //if (!(node.equals(deserialized))) throw new BridgeException("makeForm did not build the same object as passed to the bridge");
            System.out.println("Got the outcome:\n" + deserialized.toString());

            mmwrappedExpression = toSend3;
            response = BridgeClient.returnCommandResponse(mmwrappedExpression);
            sexpr = response.lastParsedSExpression;
            makeForm = extractMakeForm2(sexpr);
            if (makeForm == null) throw new BridgeException("could not find (MAKE-..) form in response");
            deserialized = OutcomeBuilder.fromSExpression(makeForm);
            //if (!(node.equals(deserialized))) throw new BridgeException("makeForm did not build the same object as passed to the bridge");
            System.out.println("Got the outcome:\n" + deserialized.toString());

            assertTrue(true);
        } catch (BridgeException e) {
            assertTrue(false);
        }
    }


    // Make sure you have started the bridge appropriately before running this test!
    @Test
    void coreASTRoundTrips() {
        // DOING: developing this into round trip tests
        // Right now we don't have AST node instances in the tests.  For now, make the AST nodes here.
        // Later, factor that part out of the toSExpression() method of the test and reuse it here.
        try {
            BridgeClient.connectToBridge("LISP_MV");

            // We will not need this if the test doesn't actually submit any events that define things.
            // BridgeClient.resetWorld();  // leave it ready for another test

            OutcomeRoundTrip(ProofObligationFailureTest.expectedNode);

            ExpressionRoundTrip(ExpressionBinaryTest.expectedASTNode);
            ExpressionRoundTrip(ExpressionBindTest.expectedASTNode1);
            ExpressionRoundTrip(ExpressionBindTest.expectedASTNode2);
            ExpressionRoundTrip(ExpressionCallTest.expectedASTNode);
            ExpressionRoundTrip(ExpressionComponentTest.expectedASTNode);
            ExpressionRoundTrip(ExpressionCondTest.expectedASTNode);
            ExpressionRoundTrip(ExpressionIfTest.expectedASTNode);
            ExpressionRoundTrip(ExpressionLiteralTest.expectedASTNode);
            ExpressionRoundTrip(ExpressionMultiTest.expectedASTNode);
            ExpressionRoundTrip(ExpressionProductConstructTest.expectedASTNode);
            ExpressionRoundTrip(ExpressionProductFieldTest.expectedASTNode);
            ExpressionRoundTrip(ExpressionProductUpdateTest.expectedASTNode);
            ExpressionRoundTrip(ExpressionSumConstructTest.expectedASTNode);
            ExpressionRoundTrip(ExpressionSumFieldTest.expectedASTNode);
            ExpressionRoundTrip(ExpressionSumTestTest.expectedASTNode);
            ExpressionRoundTrip(ExpressionSumUpdateTest.expectedASTNode);
            ExpressionRoundTrip(ExpressionUnaryTest.expectedASTNode);
            ExpressionRoundTrip(ExpressionUnlessTest.expectedASTNode);
            ExpressionRoundTrip(ExpressionWhenTest.expectedASTNode);
            ExpressionRoundTrip(VariableTest.expectedASTNode);

            TopLevelRoundTrip(TopLevelTransformTest.expectedASTNode);



            assertTrue(true);
        } catch (BridgeException e) {
            assertTrue(false);
        }

    }


    // Make sure you have started the bridge appropriately before running this test!
    @Test
    void checkWellFormedACL2() {
        try {
            BridgeClient.connectToBridge();

            // Uncomment this to reset world so tests don't depend on previous state.
            // BridgeClient.resetWorld();

            BridgeClient.sendStringRequireT(AlternativeTest.checkExpectedType);
            BridgeClient.sendStringRequireT(BranchTest.checkExpectedType);

            // This call exemplifies what happens when ACL2 returns NIL.
            // BridgeClient.sendStringRequireT("(null " + BranchTest.checkExpectedType + ")");

            BridgeClient.sendStringRequireT(ExpressionBinaryTest.checkExpectedType);
            BridgeClient.sendStringRequireT(ExpressionBindTest.checkExpectedType1);
            BridgeClient.sendStringRequireT(ExpressionBindTest.checkExpectedType2);
            BridgeClient.sendStringRequireT(ExpressionCallTest.checkExpectedType);
            BridgeClient.sendStringRequireT(ExpressionComponentTest.checkExpectedType);
            BridgeClient.sendStringRequireT(ExpressionCondTest.checkExpectedType);
            BridgeClient.sendStringRequireT(ExpressionIfTest.checkExpectedType);
            BridgeClient.sendStringRequireT(ExpressionLiteralTest.checkExpectedType);
            BridgeClient.sendStringRequireT(ExpressionMultiTest.checkExpectedType);
            BridgeClient.sendStringRequireT(ExpressionProductConstructTest.checkExpectedType);
            BridgeClient.sendStringRequireT(ExpressionProductFieldTest.checkExpectedType);
            BridgeClient.sendStringRequireT(ExpressionProductUpdateTest.checkExpectedType);
            BridgeClient.sendStringRequireT(ExpressionSumConstructTest.checkExpectedType);
            BridgeClient.sendStringRequireT(ExpressionSumFieldTest.checkExpectedType);
            BridgeClient.sendStringRequireT(ExpressionSumTestTest.checkExpectedType);
            BridgeClient.sendStringRequireT(ExpressionSumUpdateTest.checkExpectedType);
            BridgeClient.sendStringRequireT(ExpressionUnaryTest.checkExpectedType);
            BridgeClient.sendStringRequireT(ExpressionUnlessTest.checkExpectedType);
            BridgeClient.sendStringRequireT(ExpressionWhenTest.checkExpectedType);
            BridgeClient.sendStringRequireT(FieldTest.checkExpectedType);

            BridgeClient.sendStringRequireT(FunctionDefinerQuantifiedTest.checkExpectedType);
            BridgeClient.sendStringRequireT(FunctionDefinerRegularTest.checkExpectedType);
            BridgeClient.sendStringRequireT(FunctionDefinitionTest.checkExpectedType);
            BridgeClient.sendStringRequireT(FunctionHeaderTest.checkExpectedType);
            BridgeClient.sendStringRequireT(FunctionRecursionTest.checkExpectedType);
            BridgeClient.sendStringRequireT(FunctionSpecificationTest.checkExpectedType);
            BridgeClient.sendStringRequireT(FunctionSpecifierInputOutputTest.checkExpectedType);
            BridgeClient.sendStringRequireT(FunctionSpecifierQuantifiedTest.checkExpectedType);
            BridgeClient.sendStringRequireT(FunctionSpecifierRegularTest.checkExpectedType);

            BridgeClient.sendStringRequireT(IdentifierTest.checkExpectedType);
            BridgeClient.sendStringRequireT(InitializerTest.checkExpectedType);

            BridgeClient.sendStringRequireT(LiteralBooleanTest.checkExpectedType1);
            BridgeClient.sendStringRequireT(LiteralBooleanTest.checkExpectedType2);
            BridgeClient.sendStringRequireT(LiteralCharacterTest.checkExpectedType);
            BridgeClient.sendStringRequireT(LiteralIntegerTest.checkExpectedType);
            BridgeClient.sendStringRequireT(LiteralStringTest.checkExpectedType);

            BridgeClient.sendStringRequireT(ProgramTest.checkExpectedType);
            BridgeClient.sendStringRequireT(QuantifierExistsTest.checkExpectedType);
            BridgeClient.sendStringRequireT(QuantifierForallTest.checkExpectedType);
            BridgeClient.sendStringRequireT(TheoremTest.checkExpectedType);

            BridgeClient.sendStringRequireT(TopLevelFunctionTest.checkExpectedType);
            BridgeClient.sendStringRequireT(TopLevelFunctionsTest.checkExpectedType);
            BridgeClient.sendStringRequireT(TopLevelSpecificationTest.checkExpectedType);
            BridgeClient.sendStringRequireT(TopLevelTheoremTest.checkExpectedType);
            BridgeClient.sendStringRequireT(TopLevelTypeTest.checkExpectedType);
            BridgeClient.sendStringRequireT(TopLevelTypesTest.checkExpectedType);

            BridgeClient.sendStringRequireT(TypeBooleanTest.checkExpectedType);
            BridgeClient.sendStringRequireT(TypeCharacterTest.checkExpectedType);
            BridgeClient.sendStringRequireT(TypeDefinedTest.checkExpectedType);

            BridgeClient.sendStringRequireT(TypeDefinerProductTest.checkExpectedType);
            BridgeClient.sendStringRequireT(TypeDefinerSubsetTest.checkExpectedType1);
            BridgeClient.sendStringRequireT(TypeDefinerSubsetTest.checkExpectedType2);
            BridgeClient.sendStringRequireT(TypeDefinerSumTest.checkExpectedType);

            BridgeClient.sendStringRequireT(TypeDefinitionTest.checkExpectedType);
            BridgeClient.sendStringRequireT(TypedVariableTest.checkExpectedType);
            BridgeClient.sendStringRequireT(TypeIntegerTest.checkExpectedType);
            BridgeClient.sendStringRequireT(TypeMapTest.checkExpectedType);
            BridgeClient.sendStringRequireT(TypeOptionTest.checkExpectedType);

            BridgeClient.sendStringRequireT(TypeProductTest.checkExpectedType1);
            BridgeClient.sendStringRequireT(TypeProductTest.checkExpectedType2);

            BridgeClient.sendStringRequireT(TypeRecursionTest.checkExpectedType);
            BridgeClient.sendStringRequireT(TypeSequenceTest.checkExpectedType);
            BridgeClient.sendStringRequireT(TypeSetTest.checkExpectedType);
            BridgeClient.sendStringRequireT(TypeStringTest.checkExpectedType);

            BridgeClient.sendStringRequireT(TypeSubsetTest.checkExpectedType1);
            BridgeClient.sendStringRequireT(TypeSubsetTest.checkExpectedType2);

            BridgeClient.sendStringRequireT(TypeSumTest.checkExpectedType);
            BridgeClient.sendStringRequireT(VariableTest.checkExpectedType);

            BridgeClient.sendStringRequireT(FunctionSuccessTest.checkExpectedType);
            BridgeClient.sendStringRequireT(ProofObligationFailureTest.checkExpectedType);
            BridgeClient.sendStringRequireT(SpecificationSuccessTest.checkExpectedType);
            BridgeClient.sendStringRequireT(TheoremFailureTest.checkExpectedType);
            BridgeClient.sendStringRequireT(TheoremSuccessTest.checkExpectedType);
            BridgeClient.sendStringRequireT(TransformationFailureTest.checkExpectedType);
            BridgeClient.sendStringRequireT(TransformationSuccessTest.checkExpectedType);
            BridgeClient.sendStringRequireT(TypeSuccessTest.checkExpectedType);
            BridgeClient.sendStringRequireT(UnexpectedFailureTest.checkExpectedType);

            // We will not need this if the test doesn't actually submit any events.
            // BridgeClient.resetWorld();  // leave it ready for another test

            assertTrue(true);
        } catch (BridgeException e) {
            assertTrue(false);
        }

    }

    /**
     * Simple test to try to get s-expression parsing to work.
     * @throws BridgeException
     */
    @Test
    void wasMain_lisp() throws BridgeException {
        BridgeClient.connectToBridge("LISP_MV");
        BridgeClient.processCommandResponse("(list T NIL T)");
    }

    /**
     * Connect to the bridge and exchange some command-response pairs.
     * Does not stop the bridge, so you can try out more commands.
     * NOTE by EM: converted this from BridgeClient.main() so that I could start to modify to handle LISP-MV
     */
    @Test
    void wasMain() throws BridgeException {
        BridgeClient.connectToBridge("JSON_MV");
        BridgeClient.processCommandResponse("(ubu 0)");  // EM added this
        BridgeClient.processCommandResponse("'a");
        BridgeClient.processCommandResponse("'(a)");
        BridgeClient.processCommandResponse("'(a b)");
        BridgeClient.processCommandResponse("'(a . b)");
        BridgeClient.processCommandResponse("'((a))");
        BridgeClient.processCommandResponse("'((a) b)");
        BridgeClient.processCommandResponse("'(a (b))");
        BridgeClient.processCommandResponse("'((a) (b))");
        BridgeClient.processCommandResponse("(defun f3 (x) x)");
        BridgeClient.processCommandResponse("(prog2$ (cw \"HI THERE!~%\") (+ 3 4))");
        BridgeClient.processCommandResponse("(+ 3 4)");
        BridgeClient.processCommandResponse("(list 'a 'b 'c)");
        BridgeClient.processCommandResponse("(cons 3)");  // error case
        BridgeClient.processCommandResponse("(pbt 100)");  // too many; how is it reported?
        BridgeClient.processCommandResponse("(mv 3 4 5 (/ 6 0))");
        BridgeClient.processCommandResponse("(mv t t t)");
        BridgeClient.processCommandResponse("(mv t t nil)");
        BridgeClient.processCommandResponse("(mv t t :state)");
        BridgeClient.processCommandResponse("(mv nil nil state)");
        BridgeClient.processCommandResponse("(mv t nil state)");  // TODO: currently causes a false positive ACL2GeneralError
        // TODO: fix the problem that the bridge cannot handle an unknown package:
        // BridgeClient.processCommandResponse("(cons 3 'nosuchpackage::foo)");
        BridgeClient.processCommandResponse("(defthm th (acl2-numberp (- x)))");

        // Following are the tests from kestrel-acl2/community/nld-tests
        BridgeClient.processCommandResponse("(defun err1 (x) y)");
        BridgeClient.processCommandResponse("(defun err2 (x) (list x y z))");
        BridgeClient.processCommandResponse("(defun err3 (x) (declare (xargs :guard (and x y z))) x)");
        BridgeClient.processCommandResponse("(defun err4 (x) (if x (err4 (cdr x)) x))");
        BridgeClient.processCommandResponse("(defun err5 (x) (declare (xargs :guard t)) (car x))");
        BridgeClient.processCommandResponse("(defun foo6 (x) (car x))");
        BridgeClient.processCommandResponse("(verify-guards foo6)");
        BridgeClient.processCommandResponse("(u)");
        BridgeClient.processCommandResponse("(defun err7 () (no-such-function7))");
        BridgeClient.processCommandResponse("(defun err8 (x) (cons x))");
        BridgeClient.processCommandResponse("(defun err9 (x) (mv-let (x v) (cons 3 4) (list x v)))");
        BridgeClient.processCommandResponse("(defun err10 (x) (if (equal x nil) (mv 1 2) (mv 1 2 3)))");
        BridgeClient.processCommandResponse("(defun err11 (x y) (declare (type (integer 0 *) x) (type (integer 0 *) y)) (if (or (< x 2) (< y 2)) 0 (if (< x y) (+ 2 (err11 (+ x 1) (- y 2))) (+ 1 (err11 (- x 2) (+ y 1))))))");
        BridgeClient.processCommandResponse("(/ 6 0)");
        BridgeClient.processCommandResponse("(car 3)");
        BridgeClient.processCommandResponse("(cons 3)");
        BridgeClient.processCommandResponse("(mv)");
        BridgeClient.processCommandResponse("(pbt 10000)");
        BridgeClient.processCommandResponse("(defun make-global-package () t)");
        BridgeClient.processCommandResponse("(defun multiplythis2 (a b) (* a b))");
        BridgeClient.processCommandResponse("(defun dividethis2 (a b) (/ a b))");
        // TODO: this does not return an appropriate error message, and the
        // bridge process outputs "ACL2 Halted", but ACL2 doesn't really halt.
        //BridgeClient.processCommandResponse("(defun multiplythis3 (a b) (* a b))\n" +
        //        "(defun dividethis3 (a b) (/ a b))");

    }

    // TODO doesn't work yet
    @Test
    void killBridge() {
        try {
            BridgeClient.killBridge();
        } catch (BridgeException e) {
        }
        assertTrue(true);
    }

}