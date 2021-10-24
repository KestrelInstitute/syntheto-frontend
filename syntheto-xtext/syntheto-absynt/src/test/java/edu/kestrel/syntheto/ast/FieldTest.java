package edu.kestrel.syntheto.ast;

import edu.kestrel.syntheto.sexpr.Parser;
import edu.kestrel.syntheto.sexpr.SExpression;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

public class FieldTest {

    public static final String expectedString =
            "(SYNTHETO::MAKE-FIELD :NAME (SYNTHETO::MAKE-IDENTIFIER :NAME \"my_id\") "+
                    ":TYPE (SYNTHETO::MAKE-TYPE-INTEGER))";

    public static final String checkExpectedType = "(syntheto::fieldp " + expectedString + ")";

    @Test
    void toSExpression() {
        // Example is my_id: integer
        Identifier fieldname = Identifier.make("my_id");
        Type fieldtype = TypeInteger.make();
        Field field = Field.make(fieldname, fieldtype);
        SExpression sExpression = field.toSExpression();
        assertEquals(expectedString,
                sExpression.toString());

        Field rebuilt = (Field) ASTBuilder.fromSExpression(sExpression);
        assertEquals(field, rebuilt);

        Reader r = new StringReader(sExpression.toString());
        Parser p = new Parser(r);
        SExpression s = p.parseTop();
        assertEquals(sExpression, s);
    }
}