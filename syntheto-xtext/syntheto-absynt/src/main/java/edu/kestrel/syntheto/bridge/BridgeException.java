package edu.kestrel.syntheto.bridge;


/**
 * Exceptions thrown when exchanging data with the bridge.
 */
public class BridgeException extends Exception {

    /**
     * Constructs a bridge exception with a message.
     */
    public BridgeException(String message) {
        super(message);
    }

    /**
     * Constructs a bridge exception with a message and cause.
     */
    public BridgeException(String message, Throwable cause) {
        super(message, cause);
    }
}