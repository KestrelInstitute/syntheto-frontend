package edu.kestrel.syntheto.bridge;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

/**
 * Processing of commands to the ACL2 bridge.
 * <p>
 * An instance of this class provides functionality to
 * construct commands and send them to the bridge.
 * </p>
 * <p>
 * The constructor of this class takes as input a writer,
 * where the command data is sent.
 * The writer is obtained from the socket associated to the bridge.
 * </p>
 * <p>
 * After constructing an instance of this class,
 * one calls {@link #writeBridgeCommand(String)} repeatedly,
 * once per command, for each command to send.
 * </p>
 */
public class BridgeCommand { 

    /**
     * Destination of the commands.
     * This is the "view" of the bridge that this class has,
     * i.e. as something to write data to.
     */
    public final PrintWriter bridge;

    /**
     * Type of response we want to get from the Bridge.
     * The ACL2 Bridge supports "LISP", "LISP_MV", "JSON", "JSON_MV".
     * Since the try-in-main-thread stuff generally returns NIL as the first value,
     * we need to use a *_MV response type.
     */
    private String bridgeResponseType;

    /**
     * Constructs a bridge command.
     * This is done once, before sending any data.
     * The created class instance is reused to send multiple commands,
     * one after the other.
     */
    public BridgeCommand(PrintWriter bridge, String responseType) {
        this.bridgeResponseType = responseType;
        this.bridge = bridge;
    }

    /**
     * This initializer is deprecated.  Use the one with responseType argument.
     * Default response type is "JSON_MV"
     * @param bridge
     */
    public BridgeCommand(PrintWriter bridge) {
        this.bridgeResponseType = "JSON_MV";
        this.bridge = bridge;
    }

    /**
     * Write a command of the form described in {@link BridgeClient}.
     *
     * @param sexpression The S-expression, as a string,
     *                    which we assume to be all ASCII.
     */
    public void writeBridgeCommand(String sexpression) throws BridgeException {
        String form = "(bridge::try-in-main-thread (nld '"
                + sexpression
                + "))";
        String message = bridgeResponseType + " " + form.length() + "\n" + form + "\n";
        bridge.print(message);
        bridge.flush();
    }

    /**
     * You can use this if you have already wrapped rawForm with
     * (try-in-main-thread (nld (...))
     * or if you need to send a command directly to the lisp underlying ACL2.
     * If you don't have try-in-main-thread in rawForm, it will run
     * directly in the lisp worker thread (not safe for hashcons).
     * @param rawForm
     * @throws BridgeException
     */
   public  void writeRawBridgeCommand(String rawForm) throws BridgeException {
        String message = bridgeResponseType + " " + rawForm.length() + "\n" + rawForm + "\n";
        bridge.print(message);
        bridge.flush();
    }
}