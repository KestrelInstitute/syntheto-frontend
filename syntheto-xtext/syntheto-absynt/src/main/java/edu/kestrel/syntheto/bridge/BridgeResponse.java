package edu.kestrel.syntheto.bridge;

import edu.kestrel.syntheto.sexpr.Parser;
import edu.kestrel.syntheto.sexpr.SExpression;
import org.json.*;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.logging.Logger;

/**
 * Processing of responses from the ACL2 bridge.
 * <p>
 * An instance of this class provides functionality to receive responses from
 * the bridge and extract the relevant information. This relevant information is
 * extracted by parsing the raw response data into higher-level data.
 * </p>
 * <p>
 * The constructor of this class takes as input a reader, from where the
 * response data is parsed. The reader from the socket associated to the bridge.
 * </p>
 * <p>
 * After constructing an instance of this class, one calls
 * {@link #readResponse()} to read and parse the raw response data from the
 * reader. If parsing succeeds, a string is returned that contains the
 * information extracted from the response (see below).
 * </p>
 */
public class BridgeResponse {

	/**
	 * Source of the responses. This is the the "view" of the bridge that this class
	 * has, i.e. as something to read data from.
	 */
	private final Reader bridge;

	public Logger Log;

	/**
	 * The responseType is set up when BridgeClient connects.
	 * It is passed to the bridge by BridgeCommand and it determines the way the return value is formatted.
	 * The default value is "JSON_MV", and we also support "LISP_MV".
	 */
	private final String responseType;

	public String getResponseType() {
		return responseType;
	}

	/**
	 * Constructs a bridge response. This is done once, before receiving any data.
	 * The created class instance is reused to receive multiple responses, one after
	 * the other.
	 */
	public BridgeResponse(Reader bridge, String responseType) {
		this.bridge = bridge;
		this.responseType = responseType;
		Log = null;
	}

	/**
	 * This initializer is deprecated.  Use the one with responseType argument.
	 *
	 * Default response type is "JSON_MV"

	 * Constructs a bridge response. This is done once, before receiving any data.
	 * The created class instance is reused to receive multiple responses, one after
	 * the other.
	 */
	public BridgeResponse(Reader bridge) {
		this.bridge = bridge;
		this.responseType = "JSON_MV";
		Log = null;
	}

	/*
	 * Screen output. Initially empty. Chunks are added to it during parsing.
	 * Note, this is the output stream capture, not the serialized return vlaue.
	 * Emptied just before each response is parsed.
	 */
	private final StringBuilder screenOutput = new StringBuilder();

	/*
	 * Return a string containing the output (stdout, but maybe also stderr?)
	 * from the last command response.
	 */
	public String getScreenOutput() {
		return screenOutput.toString();
		}

	/*
	 * String containing the return value from most recent response.
	 * Emptied just before each response is parsed.
	 */
	private final StringBuilder returnValueString = new StringBuilder();

	/*
	 * Return a string containing the return value output from the most recent response.
	 */
	public String getReturnValueString() {
		return returnValueString.toString();
	}

	/*
	 * SExpression containing the last parsed return value, in the case of LISP_MV response type
	 */
	public SExpression lastParsedSExpression = null;

	/*
	 * Parses the next character from the bridge response, and return it. We assume
	 * that the data is available in a reader, which should be obtainable from the
	 * socket.
	 */
	private char readAny() throws BridgeException {
		try {
			int ch = bridge.read();
			if (ch < 256)
				return (char) ch;
			throw new BridgeException("Found non-8-bit character " + ch + ".");
		} catch (IOException e) {
			throw new BridgeException("Cannot parse anything.", e);
		}
	}

	/*
	 * Parses a specified number of characters, returning them in a string.
	 */
	private String readAny(int num) throws BridgeException {
		char[] chars = new char[num];
		for (int i = 0; i < num; ++i)
			chars[i] = readAny();
		return new String(chars);
	}

	/*
	 * Parses a specific character.
	 */
	private void readChar(char ch) throws BridgeException {
		char next = readAny();
		if (next == ch)
			return;
		throw new BridgeException("Expected " + ch + ", found " + next + ".");
	}

	/*
	 * Parses a newline character.
	 */
	private void readNewline() throws BridgeException {
		readChar('\n');
	}

	/*
	 * Parses a specific string (i.e. sequence of characters).
	 */
	private void readString(String str) throws BridgeException {
		for (int i = 0; i < str.length(); ++i)
			readChar(str.charAt(i));
	}

	/*
	 * Parses a number (of subsequent characters), and the newline that terminates
	 * it.
	 */
	private int readNumber() throws BridgeException {
		int num = 0;
		char ch = readAny();
		if (ch < '0' || ch > '9')
			throw new BridgeException("Expected number, found " + ch + ".");
		do {
			num *= 10;
			num += ch - '0';
			ch = readAny();
		} while ('0' <= ch && ch <= '9');
		if (ch == '\n')
			return num;
		throw new BridgeException("Number followed by " + ch + " instead of newline.");
	}

	/*
	 * Parses the ready message from the bridge.
	 */
	private void readReady() throws BridgeException {
		readString("READY 0");
		readNewline();
		readNewline();
	}

	// Given a parsed JSON version of the ACL2 Return value,
	// return a string describing an error, or "" if we could not find an error.
	// If the JSONArray's structure didn't look right, the error string starts
	// with "JSON"; otherwise it starts with "ACL2" or with ":".
	private String ACL2RetvalError(JSONArray ja) {
		// The JSON return value should be a JSON Array with three elements.
		// ["NIL", innerRetval, "The Live State Itself"].
		if (ja.length() != 3)
			return "JSONTopArrayWrongLength";
		Object topFirstElement = ja.get(0);
		if (!(topFirstElement instanceof String) || !("NIL".equals((String) topFirstElement)))
			return "JSONTopArrayBadFirstElement";
		Object topThirdElement = ja.get(2);
		if (!(topThirdElement instanceof String) || !("The Live State Itself".equals((String) topThirdElement)))
			return "JSONTopArrayBadThirdElement";

		// innerRetval is an array with at least one element, whose first element is
		// either "T" (meaning general error),
		// or "NIL", meaning the innerRetval must be scrutinized further.
		Object topSecondElement = ja.get(1);
		if (!(topSecondElement instanceof JSONArray))
			return "JSONTopArrayBadSecondElement";
		JSONArray innerRetval = (JSONArray) topSecondElement;
		if (0 == innerRetval.length())
			return "JSONTopArrayBadSecondElement";

		Object generalErrorIndicator = innerRetval.get(0);
		if (!(generalErrorIndicator instanceof String))
			return "JSONBadGeneralErrorIndicator";
		// A generalErrorIndicator of "T" can mean a fundamental top-level ACL2 error,
		// like (cons 3)
		// but also can result from a call that has a guard violation, like (car 3).
		String gEI = (String) generalErrorIndicator;
		if (gEI.equals("T"))
			return "ACL2GeneralError";
		if (!gEI.equals("NIL"))
			return "JSONBadGeneralErrorIndicator";
		// After this point, the first element of innerRetVal is "NIL" and we ignore it.

		// If the command was a stateless function call that returns T, the innerRetval
		// looks like
		// [ "NIL", ["NIL"], "T" ]
		// If the stateless function call returns NIL, the innerRetval looks like
		// [ "NIL", ["NIL"] ]
		// No other innerRetvals of length 2 are known, so give an error for other
		// patterns.
		if (innerRetval.length() == 2) {
			if (!(innerRetval.get(1) instanceof JSONArray))
				return "ACL2nonArrayInnerRetval";
			JSONArray inner1j = (JSONArray) innerRetval.get(1);
			if (inner1j.length() != 1)
				return "ACL2unknownInnerRetvalArray";
			Object inner1j_el = inner1j.get(0);
			if (!(inner1j_el.equals("NIL")))
				return "ACL2unexpectedInner1j";
			else
				return ""; // success flag. ACL2 evaluation returned NIL.
		}

		// Now innerRetval should have at least two more elements after the initial "NIL":
		// stobjs_out, followed by stobjs_out.length() number of result values.
		if (innerRetval.length() < 3)
			return "JSONInnerRetvalTooShort";

		// Check that stobjs_out is an array and that innerRetval's length is two more
		// than stobjs_out's length.
		Object s_o = innerRetval.get(1);
		if (!(s_o instanceof JSONArray))
			return "JSONstobjs_outNotArray";
		JSONArray stobjs_out = (JSONArray) s_o;
		if (stobjs_out.length() + 2 != innerRetval.length())
			return "JSONstobjs_outResultsLengthMismatch";

		// If we submitted an event form, the return values would be
		//   [erp, retval, state].
		// Here that means the innerRetval would look like
		//   ["NIL", ["NIL", "NIL", "STATE"], erp, retval, "REPLACED-STATE"].
		// If we submitted something other than an event form, the return values could
		// be something else. In that case if there was any error then the
		// generalErrorIndicator is likely to be "T", and we have already checked that
		// case.
		// So we believe that at this point, a return shape for stobjs_out other than
		// ["NIL","NIL","STATE"]
		// likely means there was no error.
		if (stobjs_out.length() != 3)
			return "";
		Object sto0 = stobjs_out.get(0);
		Object sto1 = stobjs_out.get(1);
		Object sto2 = stobjs_out.get(2);
		if (!(sto0 instanceof String) || !(sto1 instanceof String) || !(sto2 instanceof String))
			return "JSONstobjs_outNotAllStrings";
		// Now if the shape is not ["NIL", "NIL", "STATE"], we will not be able to find
		// any error.
		// Again, as described above, we don't think NLD would return any other
		// stobjs_out if there was an error.
		if (!"NIL".equals((String) sto0) || !"NIL".equals((String) sto1) || !"STATE".equals((String) sto2))
			return "";

		// Now we know the shape matches an event form.
		Object maybe_erp = innerRetval.get(2);
		// if erp is "NIL", it means there was not a detectable error
		// (and, btw, generally innerRetval.get(3) will be the returned value like the
		// name of the definition form, or :REDUNDANT, or ??) (TODO: check further)
		if (maybe_erp instanceof String && "NIL".equals((String) maybe_erp))
			return "";
		// if erp is "T", we still don't know what sort of ACL2 error.
		if (maybe_erp instanceof String && "T".equals((String) maybe_erp))
			return "ACL2GeneralError";

		// If maybe_erp is an array, the first element of which is a string,
		// that string is supposed to be a summary of the sort of error it is.
		// In any other arrangement, we don't know what kind of error this is.
		if (!(maybe_erp instanceof JSONArray))
			return "ACL2ErrorNotYetClassified";
		JSONArray erp = (JSONArray) maybe_erp;
		if (0 == erp.length())
			return "ACL2ErrorNotYetClassified";
		Object errorSummary = erp.get(0);
		if (!(errorSummary instanceof String))
			return "ACL2ErrorNotYetClassified";
		// Something else probably went wrong if the error summary string is the empty
		// string.
		if ("".equals((String) errorSummary))
			return "ACL2ErrorSummaryEmptyString";

		// Otherwise, return the error summary string verbatim.
		return (String) errorSummary;
	}

	/*
	 * A JSON structure for the innerRetVal that means NIL was returned.
	 */
	private static final JSONArray JSON_return_nil =
			new JSONArray("[ \"NIL\", [\"NIL\"] ]");

	/*
	 * A JSON structure for the innerRetVal that means T was returned.
	 * (Warning, might also mean the string "T"... didn't check for homonyms yet.)
	 */
	private static final JSONArray JSON_return_T =
			new JSONArray("[ \"NIL\", [\"NIL\"], \"T\" ]" );

	/**
	 * ACL2_return_value
	 *
	 * Given a JSONArray that was returned by ACL2,
	 * checks that it is properly structured using ACL2RetvalError(ja) above,
	 * then extracts a JSON version of the actual return value without extra state and stobj info,
	 * using code copied from ACL2RetvalError.
	 * TODO: this concept might not be very useful because of the mismatch between lisp and JSON,
	 *       and that is why we didn't bother to implement more.
	 *       or now this method detects only "NIL" and "T", and returns null otherwise.
	 */
	private Object ACL2_return_value(String jaString) {
		// TODO: Initial idea was to copy ACL2RetvalError and modify to return an Object rather than a String,
		//       so that we get information about the returned value as well as any error.

		// First, skip any ja that had a JSON or ACL2 error.
		// This will weed out any that don't have a reasonable innerRetVal.
		if (ACL2RetvalError(jaString).equals("")) {

			Object JSON_or_errorstring = ACL2_JSON_Retval_or_Error(jaString);
			// Since ACL2RetvalError() returned "", we know there is an innerRetVal.
			JSONArray innerRetVal = (JSONArray) ((JSONArray) JSON_or_errorstring).get(1);
			if (innerRetVal.similar(JSON_return_nil)) {
				return "NIL";
			} else if (innerRetVal.similar(JSON_return_T)) {
				return "T";
			} else {
				return null;
			}
		} else
			return null;
	}

	// EM draft: Query a JSON return value response to see if it returned T.
	// Call this after a response.
	public boolean response_JSON_p () {
		String response = getReturnValueString();
		if (response.equals(""))
			return false;
		else {
			Object T_NIL_or_null = ACL2_return_value(response);
			return "T".equals(T_NIL_or_null);
		}
	}


	/*
	 * The ACL2 return value is supposed to be a string with serialized JSON. That
	 * JSON indicates whether ACL2 got an error, and in some cases describes the
	 * type of error. This method parses the JSON. If there is an error parsing it,
	 * "JSONParseError" is returned.
	 *
	 */
	private String ACL2RetvalError(String returnedValuesString) {
		// We parse and process the JSON return value here
		try {
			JSONArray ja = new JSONArray(returnedValuesString);
			// Pretty printed version:
			String ppJSON = ja.toString(2);
			// EM: Abhishek had commented this out, but then we can't see the return value.
			//     I am putting it back in but only for the Log==null case so it doesn't
			//     affect his usage.
			if (Log==null) System.out.println("ACL2 return value in JSON format:\n" + ppJSON);
			return ACL2RetvalError(ja);
		} catch (JSONException je) {
			if (Log == null) {
				System.out.println("ERROR parsing JSON return value.");
				System.out.println("jsonReturnVal = " + returnedValuesString);
				System.out.println(je);
			} else {
				Log.severe("ERROR parsing JSON return value.");
				Log.severe("jsonReturnVal = " + returnedValuesString);
				Log.severe(je.toString());
			}
			return "JSONParseError";
		}
	}

	/*
	 * The ACL2 return value is supposed to be a string with serialized JSON. That
	 * JSON indicates whether ACL2 got an error, and in some cases describes the
	 * type of error. This method parses the JSON. If there is an error parsing it,
	 * "JSONParseError" is returned. Otherwise the JSON object is returned.
	 *
	 */
	private Object ACL2_JSON_Retval_or_Error(String returnedValuesString) {
		// We parse and process the JSON return value here
		try {
			JSONArray ja = new JSONArray(returnedValuesString);
			return ja;
		} catch (JSONException je) {
			if (Log == null) {
				System.out.println("ERROR parsing JSON return value.");
				System.out.println("jsonReturnVal = " + returnedValuesString);
				System.out.println(je);
			} else {
				Log.severe("ERROR parsing JSON return value.");
				Log.severe("jsonReturnVal = " + returnedValuesString);
				Log.severe(je.toString());
			}
			return "JSONParseError";
		}
	}

	private void readReturn() throws BridgeException {
		readString("ETURN "); // note missing initial R is intentional
		int numchars = readNumber();
		String returnVal = readAny(numchars);
		returnValueString.append(returnVal);
		// TODO it would be good to check that we got the response type that we expected
		if (responseType.equals("JSON_MV"))
			readReturnJsonString(returnVal);
		else if (responseType.equals("LISP_MV")) {
			readReturnLispString(returnVal);
			// Apparently the number of chars is off by one for the LISP_MV case.
			// They don't count the newline at the end when counting the number of characters.
			// So we read past the newline:
			readNewline();
		} else if (responseType.equals("LISP")) {
			// NOTE: tried this but it didn't work.  Got NIL return value.
			// Probably there is a place that doesn't handle this response type.
			readReturnLispString(returnVal);
			// TODO: see if "LISP" response type also has the same off-by-one problem as LISP_MV
		}
		else throw new BridgeException("readReturn(): unknown response type");
	}

	/**
	 *
	 * @param lispReturnVal
	 * @throws BridgeException
	 */
	private void readReturnLispString(String lispReturnVal) throws BridgeException {
		// These outputs are for experimenting and debugging.  Add a Log version if you wish.
		if (Log == null) {
			System.out.println("---------------------");
			System.out.println("String received from Bridge:");
			System.out.println("---------------------");
			System.out.println(lispReturnVal);
			System.out.println("---------------------");
			System.out.println("... removing strange symbol with vertical bars ... new string:");
			lispReturnVal = lispReturnVal.replace("ACL2_INVISIBLE::|The Live State Itself|", "NIL");
			System.out.println("---------------------");
			System.out.println(lispReturnVal);
			System.out.println("---------------------");
			Parser p = new Parser(lispReturnVal);
			SExpression se = p.parseTop();
			// record the above information in an instance variable
			lastParsedSExpression = se;
			System.out.println("Parsed S-expression from Lisp:");
			System.out.println("---------------------");
			System.out.println(se.toString());
			System.out.println("---------------------");

		}
	}

	/*
	 * Parses the return message from the bridge, except for the first character,
	 * which has already been parsed by the caller (this is how the caller realizes
	 * that it is the end of the STDOUT messages).
	 */
	private void readReturnJsonString(String jsonReturnVal) throws BridgeException {
		String jErr = ACL2RetvalError(jsonReturnVal);
		if ("".equals(jErr)) {
			if (Log == null) {
				System.out.println("It appears the JSON object " + "containing the return values "
						+ "is properly structured,\n" + "and ACL2 did not get any detectable error.");
				System.out.println("The output value string is between the first two lines of dashes,");
				System.out.println("and the extracted return value between the 2nd and 3rd ine of dashes.");
				System.out.println("---------------------");
				System.out.println(jsonReturnVal);
				System.out.println("---------------------");
				System.out.println("");
				System.out.println("---------------------");
			}
			else
				Log.info("It appears the JSON object " + "containing the return values " + "is correctly structured,\n"
						+ "and ACL2 did not get any detectable error.");
		} else if ("JSONParseError".equals(jErr)) {
			if (Log == null) {
				System.out.println("Unable to parse the string containing "
						+ "the serialized JSON object containing the return values in this string:");
				System.out.println(jsonReturnVal);
			} else {
				Log.severe("Unable to parse the string containing "
						+ "the serialized JSON object containing the return values:");
				Log.severe(jsonReturnVal);
			}
		} else {
			if (Log == null)
				System.out.println("The error returned from ACL2 is: " + jErr);
			else
				Log.severe("The error returned from ACL2 is: " + jErr);
		}
		readNewline();
	}

	/*
	 * Parse the full response message from the bridge. The screen output
	 * can be obtained via getScreenOutput(), and is also returned by this method.
	 * The serialized return value output (string containing either JSON object or LISP s-expression)
	 * can be obtained via getReturnValueString().
	 */
	public String readResponse() throws BridgeException {
		screenOutput.setLength(0);
		returnValueString.setLength(0);
		lastParsedSExpression = null;
		char ch = readAny();
		while (ch == 'S') {
			readString("TDOUT "); // note ending space
			int n = readNumber();
			String chunk = readAny(n);
			screenOutput.append(chunk);
			readNewline();
			ch = readAny();
		}
		if (ch != 'R')
			throw new BridgeException("Expected a literal string 'RETURN', " +
					"but the first character found was '" + ch + "'.");
		readReturn();
		readReady();
		return new String(screenOutput);
	}

	/*
	 * Parses the initial hello message from the bridge.
	 */
	public void readHello() throws BridgeException {
		readString("ACL2_BRIDGE_HELLO "); // note ending space
		int n = readNumber();
		readAny(n); // discard worker name
		readNewline(); // final newline
		readReady();
	}
}