package com.se310.ledger;

/**
 * CommandProcessorException class implementation designed display errors to the user while
 * processing commands
 *
 * @author  Sergey L. Sundukovskiy
 * @version 1.0
 */
public class CommandProcessorException extends Exception {
    // serialVersionUID for Exception serialization compatibility
    private static final long serialVersionUID = 1L;
    private final String command;
    // final (immutable) lineNumber is optional; -1 is not provided. 
    private final int lineNumber;

    /**
     * Construct an exception with the command and reason (message).
     */
    public CommandProcessorException(String command, String reason) {
        super(reason);
        this.command = command;
        this.lineNumber = -1;
    }

    /**
     * Construct an exception with command, reason and a line number where it occurred.
     */
    public CommandProcessorException(String command, String reason, int lineNumber) {
        super(reason);
        this.command = command;
        this.lineNumber = lineNumber;
    }

    /**
     * Construct an exception with a cause.
    */
    public CommandProcessorException(String command, String reason, Throwable cause) {
        super(reason, cause);
        this.command = command;
        this.lineNumber = -1;
    }

    /**
     * Getter for the original command text. Immutable (no setter).
     * @return the command text that caused the error
     */
    public String getCommand() {
        return command;
    }

    /**
     * Getter for the optional line number; -1 if not provided.
     */
    public int getLineNumber() {
        return lineNumber;
    }

    @Override
    public String toString() {
        return "CommandProcessorException{" +
                "command='" + command + '\'' +
                ", message='" + getMessage() + '\'' +
                ", lineNumber=" + lineNumber +
                '}';
    }
}
