package edu.kestrel.syntheto.ast;

import edu.kestrel.syntheto.sexpr.Parser;
import edu.kestrel.syntheto.sexpr.SExpression;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TypeDefinitionTest {

    public static final String expectedString =
            "(SYNTHETO::MAKE-TYPE-DEFINITION :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"ordered_pair\") "+
                    ":BODY (SYNTHETO::MAKE-TYPE-DEFINER-PRODUCT :GET " +
                    "(SYNTHETO::MAKE-TYPE-PRODUCT " +
                    ":FIELDS (LIST (SYNTHETO::MAKE-FIELD :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"x\") :TYPE (SYNTHETO::MAKE-TYPE-INTEGER)) " +
                    "(SYNTHETO::MAKE-FIELD :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"y\") :TYPE (SYNTHETO::MAKE-TYPE-INTEGER))) " +
                    ":INVARIANT NIL)))";

    public static final String checkExpectedType = "(syntheto::type-definitionp " + expectedString + ")";

    @Test
    void toSExpression() {
        // Example is struct ordered_pair { x: integer, y: integer }

        // copied code from TypeProductTest.java, then changed names of fields
        // Make two basic fields
        Identifier v1 = Identifier.make("x");
        Identifier v2 = Identifier.make("y");
        Field f1 = Field.make(v1, TypeInteger.make());
        Field f2 = Field.make(v2, TypeInteger.make());
        List<Field> fields = Arrays.asList(f1, f2);
        // Test a product type with a null invariant.
        TypeProduct prod = TypeProduct.make(fields, null);

        Identifier varname = Identifier.make("ordered_pair");
        TypeDefinition typedef = TypeDefinition.make(varname, TypeDefinerProduct.make(prod));
        SExpression sExpression = typedef.toSExpression();
        assertEquals(expectedString,
                sExpression.toString());

        TypeDefinition rebuilt = (TypeDefinition) ASTBuilder.fromSExpression(sExpression);
        assertEquals(typedef, rebuilt);

        Reader r = new StringReader(sExpression.toString());
        Parser p = new Parser(r);
        SExpression s = p.parseTop();
        assertEquals(sExpression, s);
    }
}