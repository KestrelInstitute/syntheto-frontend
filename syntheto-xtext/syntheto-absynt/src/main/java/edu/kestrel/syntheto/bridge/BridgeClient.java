package edu.kestrel.syntheto.bridge;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

/**
 * A simple client for the ACL2 bridge.
 * <p>
 * This is a Java application that establishes a connection
 * and exchanges a few command-response pairs.
 * Its purpose is to demonstrate how to build
 * a connection between an IDE and ACL2 via the bridge.
 * </p>
 * <p>
 * See the documentation for the ACL2 bridge first.
 * </p>
 * <p>
 * After connecting to the bridge,
 * we create an instance of {@link BridgeCommand} to send commands
 * and an instance of {@link BridgeResponse} to receive responses.
 * These two instances are reused for all commands and responses,
 * i.e. we create a single instance of each.
 * </p>
 * <p>
 * When connecting to the bridge,
 * we expect the bridge to send the following response:
 * <pre>
 *     ACL2_BRIDGE_HELLO &lt;n&gt;
 *     &lt;worker-name&gt;
 *     READY 0
 *     &lt;empty line&gt;
 * </pre>
 * We use {@link BridgeResponse#readHello()}
 * to consume and validate this response.
 * </p>
 * <p>
 * After that, we send a few command-response pairs.
 * The first command is sent
 * via {@link BridgeCommand#writeBridgeCommand(String)},
 * after which the corresponding response is received
 * via {@link BridgeResponse#readResponse()}.
 * Similarly, we send the second command and receive the corresponding response,
 * and so on.
 * </p>
 * <p>
 * We send commands that all have the form
 * <pre>
 *     JSON_MV &lt;n&gt;
 *     (bridge::try-in-main-thread (nld '&lt;sexpr&gt;))
 * </pre>
 * where {@code <sexpr>} is an S-expression
 * that could be entered at the top-level ACL2 prompt,
 * such as an event macro (e.g. a {@code defun})
 * or other command (e.g. {@code (pbt 1)}).
 * We only need to supply the S-expression, as a string,
 * to {@link BridgeCommand#writeBridgeCommand(String)}.
 * </p>
 * <p>
 * We expect to receive responses that have the form
 * <pre>
 *     STDOUT &lt;n1&gt;
 *     &lt;chunk1&gt;
 *     STDOUT &lt;n2&gt;
 *     &lt;chunk2&gt;
 *     ...
 *     RETURN &lt;n3&gt;
 *     &lt;return values in JSON format&gt;
 *     READY 0
 *     &lt;empty line&gt;
 * </pre>
 * The chunks form the screen output that ACL2 produces
 * when processing the supplied S-expression.
 * The final {@code READY} carries no information.
 * We extract and join the screen output chunks
 * via {@link BridgeResponse#readResponse()}.
 */
public class BridgeClient {

    /**
     * Command sent to the bridge.
     */
    private static BridgeCommand command;

    /**
     * Response sent from the bridge.
     */
    private static BridgeResponse response;

    // EM draft: Query a JSON return value response to see if it returned T.
    // Call this after a command/response pair.
    public static boolean JSON_response_T_p() {
        if (response == null)
            return false;
        else
            return response.response_JSON_p();
    }

    public static void sendStringRequireT(String stringPred) throws BridgeException {
        sendString(stringPred);
        if (! JSON_response_T_p()) {
            System.out.println("TEST FAILED: " + stringPred);
            throw new BridgeException("bad");
        }
    }

    /**
     * Process a command-response pair.
     * <p>
     *     Given an S-expression in a string, we print it to the screen
     *     and we send it to the bridge.
     *     Then we read a response from the bridge,
     *     and we print it on the screen;
     *     this is ACL2's screen output after it evaluates
     *     the S-expression.
     * </p>
     * @param sexpression The S-expression to send, in a String.
     */
    private static void processCommandResponseInternal(String sexpression, boolean raw)
            throws BridgeException {
        System.out.println(">>>>>>>>>> SEND");
        System.out.println(sexpression);
        if (raw) {
            command.writeRawBridgeCommand(sexpression);
        } else {
            command.writeBridgeCommand(sexpression);
        }
        System.out.println();
        System.out.println("<<<<<<<<<< RECEIVE");
        String screenOutput = response.readResponse();
        System.out.println(screenOutput);
        System.out.println();
    }

    /**
     * Process a command-response pair.
     * <p>
     *     Given an S-expression in a string, we print it to the screen
     *     and we send it to the bridge.
     *     Then we read a response from the bridge,
     *     and we print it on the screen;
     *     this is ACL2's screen output after it evaluates
     *     the S-expression.
     * </p>
     * @param sexpression The S-expression to send.
     */
    public static void processCommandResponse(String sexpression)
    // TODO: this was private.  Make it private again, and change how it is called from BridgeClientTest
            throws BridgeException {
        processCommandResponseInternal(sexpression, false);
    }

    // Variant of above that returns the response
    public static BridgeResponse returnCommandResponse(String sexpression)
        throws BridgeException {
        System.out.println(">>>>>>>>>> SEND");
        System.out.println(sexpression);
        command.writeBridgeCommand(sexpression);
        System.out.println();
        System.out.println("<<<<<<<<<< RECEIVE");
        String screenOutput = response.readResponse();  // currently this has side effect of setting response.lastParsedSExpression
        System.out.println(screenOutput);
        System.out.println();
        return response;
    }

    public static String bridgeHostName = "localhost";

    public static int bridgeHostPort = 55445;

    /**
     * Bridge response types
     */
    static final List<String> BridgeResponseTypes = Arrays.asList(
            "LISP", "LISP_MV", "JSON", "JSON_MV" );

    public static void connectToBridge(String responseType) throws BridgeException {
        if (! BridgeResponseTypes.contains(responseType))
            throw new IllegalArgumentException();
        Socket socket;
        try {
            socket = new Socket(bridgeHostName, bridgeHostPort);
        } catch (IOException e) {
            throw new BridgeException("Cannot connect to bridge.", e);
        }
        PrintWriter writer;
        try {
            writer = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            throw new BridgeException("Cannot obtain writer.", e);
        }
        Reader reader;
        try {
            reader = new BufferedReader
                    (new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            throw new BridgeException("Cannot obtain reader.", e);
        }
        command = new BridgeCommand(writer, responseType);
        response = new BridgeResponse(reader, responseType);
        response.readHello();
    }

    public static void connectToBridge() throws BridgeException {
        connectToBridge("JSON_MV");  // the default
    }

    /**
     * Undoes events in the ACL2 world until the load of syntheto/top.  Leaves it fresh for new Syntheto-related events.
     * @throws BridgeException
     */
    public static void resetWorld() throws BridgeException {
        processCommandResponse("(ubu 0)");
    }



    public static void killBridge() throws BridgeException {
        command.writeRawBridgeCommand("(ccl::process-interrupt (bridge::find-process \"listener\") #'(lambda () (bridge::stop) (ccl::quit 0)))");
    }

    /**
     * Wraps ASTstring with (try-in-main-thread (nld ...))) and sends to the bridge, and outputs to System.out
     */
    public static void sendString(String ASTstring) throws BridgeException {
        processCommandResponse(ASTstring);
    }

    /**
     * Does not wrap ASTstring, so is suitable for already-wrapped content or for a raw command.
     */
    public static void sendRawString(String ASTstring) throws BridgeException {
        processCommandResponseInternal(ASTstring, true);
    }

    /**
     * Connect to the bridge and exchange some command-response pairs.
     * Does not stop the bridge, so you can try out more commands.
     */
    public static void main(String[] args) throws BridgeException {
        connectToBridge("JSON_MV");

        processCommandResponse("'a");
        processCommandResponse("'(a)");
        processCommandResponse("'(a b)");
        processCommandResponse("'(a . b)");
        processCommandResponse("'((a))");
        processCommandResponse("'((a) b)");
        processCommandResponse("'(a (b))");
        processCommandResponse("'((a) (b))");
        processCommandResponse("(defun f3 (x) x)");
        processCommandResponse("(prog2$ (cw \"HI THERE!~%\") (+ 3 4))");
        processCommandResponse("(+ 3 4)");
        processCommandResponse("(list 'a 'b 'c)");
        processCommandResponse("(cons 3)");  // error case
        processCommandResponse("(pbt 100)");  // too many; how is it reported?
        processCommandResponse("(mv 3 4 5 (/ 6 0))");
        processCommandResponse("(mv t t t)");
        processCommandResponse("(mv t t nil)");
        processCommandResponse("(mv t t :state)");
        processCommandResponse("(mv nil nil state)");
        processCommandResponse("(mv t nil state)");  // TODO: currently causes a false positive ACL2GeneralError
        // TODO: fix the problem that the bridge cannot handle an unknown package:
        // processCommandResponse("(cons 3 'nosuchpackage::foo)");
        processCommandResponse("(defthm th (acl2-numberp (- x)))");

        // Following are the tests from kestrel-acl2/community/nld-tests
        processCommandResponse("(defun err1 (x) y)");
        processCommandResponse("(defun err2 (x) (list x y z))");
        processCommandResponse("(defun err3 (x) (declare (xargs :guard (and x y z))) x)");
        processCommandResponse("(defun err4 (x) (if x (err4 (cdr x)) x))");
        processCommandResponse("(defun err5 (x) (declare (xargs :guard t)) (car x))");
        processCommandResponse("(defun foo6 (x) (car x))");
        processCommandResponse("(verify-guards foo6)");
        processCommandResponse("(u)");
        processCommandResponse("(defun err7 () (no-such-function7))");
        processCommandResponse("(defun err8 (x) (cons x))");
        processCommandResponse("(defun err9 (x) (mv-let (x v) (cons 3 4) (list x v)))");
        processCommandResponse("(defun err10 (x) (if (equal x nil) (mv 1 2) (mv 1 2 3)))");
        processCommandResponse("(defun err11 (x y) (declare (type (integer 0 *) x) (type (integer 0 *) y)) (if (or (< x 2) (< y 2)) 0 (if (< x y) (+ 2 (err11 (+ x 1) (- y 2))) (+ 1 (err11 (- x 2) (+ y 1))))))");
        processCommandResponse("(/ 6 0)");
        processCommandResponse("(car 3)");
        processCommandResponse("(cons 3)");
        processCommandResponse("(mv)");
        processCommandResponse("(pbt 10000)");
        processCommandResponse("(defun make-global-package () t)");
        processCommandResponse("(defun multiplythis2 (a b) (* a b))");
        processCommandResponse("(defun dividethis2 (a b) (/ a b))");
        // TODO: this does not return an appropriate error message, and the
        // bridge process outputs "ACL2 Halted", but ACL2 doesn't really halt.
        //processCommandResponse("(defun multiplythis3 (a b) (* a b))\n" +
        //        "(defun dividethis3 (a b) (/ a b))");

    }
}