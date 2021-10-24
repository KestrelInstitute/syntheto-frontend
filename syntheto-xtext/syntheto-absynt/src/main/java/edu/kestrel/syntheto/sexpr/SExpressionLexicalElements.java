package edu.kestrel.syntheto.sexpr;

/**
 * When creating SExpression
 */
public class SExpressionLexicalElements {
    // TODO: consider moving this stuff to SExpressionSymbol
    // AC prefers that the checks happen when we make the object (e.g., a symbol or string), not when we serialize it.
    // To catch mistakes sooner.
    static public final String symbolStartChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ<>~!@$%^&*+=[]{}?/_";
    static public final String symbolContinueChars = symbolStartChars+"-.0123456789";
}
